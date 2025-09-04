import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.DoubleUnaryOperator;


public class RootFinder {
    private final double relativeAccuracy;
    private final double absoluteAccuracy;
    private final double functionValueAccuracy;


    public RootFinder(double relativeAccuracy, double absoluteAccuracy, double functionValueAccuracy) {
        this.relativeAccuracy = relativeAccuracy;
        this.absoluteAccuracy = absoluteAccuracy;
        this.functionValueAccuracy = functionValueAccuracy;
    }


    /**
     * Finds a single root in the given interval using a default initial guess.
     *
     * @param func the function
     * @param min  lower bound
     * @param max  upper bound
     * @return a root in the interval
     */
    public double findRoot(DoubleUnaryOperator func, double min, double max) {
        final double initial = (min == max) ? min : 0.5 * min + 0.5 * max;
        return findRoot(func, min, initial, max);
    }


    /**
     * Finds a single root in the interval [min, max] with the specified initial guess.
     * It requires that the interval actually brackets a sign change.
     *
     * @param func    the function
     * @param min     lower bound
     * @param initial initial guess (must be in [min, max])
     * @param max     upper bound
     * @return a root in the interval
     * @throws IllegalArgumentException if no sign change is found.
     */
    public double findRoot(DoubleUnaryOperator func, double min, double initial, double max) {
        if (min > max) {
            throw new IllegalArgumentException("Lower bound is greater than upper bound.");
        }
        if (initial < min || initial > max) {
            throw new IllegalArgumentException("Initial guess is out of range.");
        }
        // Evaluate function values at key points.
        final double yInitial = func.applyAsDouble(initial);
        final double yMin = func.applyAsDouble(min);
        final double yMax = func.applyAsDouble(max);
        // Standard check: require a true sign change.
        if (Double.compare(yInitial * yMin, 0.0) < 0) {
            return brent(func, min, initial, yMin, yInitial);
        }
        if (Double.compare(yInitial * yMax, 0.0) < 0) {
            return brent(func, initial, max, yInitial, yMax);
        }
        // Fallback: if the function does not change sign, require a sign change in the derivative.
        double dMin = approximateDerivative(func, min);
        double dMax = approximateDerivative(func, max);
        if (Double.compare(dMin * dMax, 0.0) < 0) {
            // Use our method to find a zero of the derivative.
            // We use the same numeric method on the approximate derivative function.
            double candidate = findRoot(x - > approximateDerivative(func, x), min, max);
            // If the function is sufficiently close to zero at the candidate, accept it.
            if (Math.abs(func.applyAsDouble(candidate)) <= functionValueAccuracy) {
                return candidate;
            }
        }
        throw new IllegalArgumentException("Interval does not bracket a root.");
    }


    /**
     * Approximates the derivative of the given function at x using central differences.
     *
     * @param func the function
     * @param x    the point at which to approximate the derivative
     * @return an approximation of f'(x)
     */
    private double approximateDerivative(DoubleUnaryOperator func, double x) {
        double delta = 1e-6;
        return (func.applyAsDouble(x + delta) - func.applyAsDouble(x - delta)) / (2 * delta);
    }


    /**
     * Brent's method implementation.
     */
    private double brent(DoubleUnaryOperator func, double lo, double hi, double fLo, double fHi) {
        double a = lo, fa = fLo;
        double b = hi, fb = fHi;
        double c = a, fc = fa;
        double d = b - a, e = d;


        final double tolAbsolute = absoluteAccuracy;
        final double tolRelative = relativeAccuracy;


        while (true) {
            // Swap so that |f(b)| <= |f(c)|
            if (Math.abs(fc) < Math.abs(fb)) {
                double temp = b;
                b = c;
                c = temp;
                double tempF = fb;
                fb = fc;
                fc = tempF;
            }


            final double tol = 2 * tolRelative * Math.abs(b) + tolAbsolute;
            final double m = 0.5 * (c - b);


            if (Math.abs(m) <= tol || equalsZero(fb)) {
                return b;
            }


            if (Math.abs(e) < tol || Math.abs(fa) <= Math.abs(fb)) {
                d = m;
                e = d;
            } else {
                final double s = fb / fa;
                double p, q;
                if (a == c) {
                    // Secant method.
                    p = 2 * m * s;
                    q = 1 - s;
                } else {
                    // Inverse quadratic interpolation.
                    q = fa / fc;
                    final double r = fb / fc;
                    p = s * (2 * m * q * (q - r) - (b - a) * (r - 1));
                    q = (q - 1) * (r - 1) * (s - 1);
                }
                if (p > 0) {
                    q = -q;
                } else {
                    p = -p;
                }


                if (p >= 1.5 * m * q - Math.abs(tol * q) || p >= Math.abs(0.5 * e * q)) {
                    d = m;
                    e = d;
                } else {
                    e = d;
                    d = p / q;
                }
            }
            a = b;
            fa = fb;
            if (Math.abs(d) > tol) {
                b += d;
            } else if (m > 0) {
                b += tol;
            } else {
                b -= tol;
            }
            fb = func.applyAsDouble(b);
            if ((fb > 0 && fc > 0) || (fb <= 0 && fc <= 0)) {
                c = a;
                fc = fa;
                d = b - a;
                e = d;
            }
        }
    }


    private static boolean equalsZero(double value) {
        return Math.abs(value) <= Double.MIN_VALUE;
    }


    /**
     * Finds all roots of the given function in the interval [min, max].
     * The interval is divided into subintervals for testing.
     * A post-processing step then filters out multiple roots that are too close together.
     *
     * @param func         the function to solve.
     * @param min          the lower bound of the interval.
     * @param max          the upper bound of the interval.
     * @param subintervals the number of subintervals to search.
     * @return a list of distinct roots.
     */
    public List < Double > findAllRoots(DoubleUnaryOperator func, double min, double max, int subintervals) {
        List < Double > roots = new ArrayList < > ();
        double step = (max - min) / subintervals; // The width of each subinterval.
        double currentMin = min;


        while (currentMin < max) {
            double currentMax = currentMin + step;
            if (currentMax > max) {
                currentMax = max; // Ensure the last interval is valid.
            }


            try {
                double root = findRoot(func, currentMin, currentMax);
                roots.add(root);
            } catch (IllegalArgumentException e) {
                // No root found in this subinterval; just move to the next one.
            }


            currentMin = currentMax; // Move to the next subinterval.
        }


        // Filter out roots that are too close together.
        return filterCloseRoots(roots, step * 0.5);
    }


    /**
     * Filters out roots that are closer than a minimum separation.
     *
     * @param roots         the unsorted list of roots.
     * @param minSeparation the minimum allowed separation between distinct roots.
     * @return a filtered list of roots.
     */
    private List < Double > filterCloseRoots(List < Double > roots, double minSeparation) {
        if (roots.isEmpty()) {
            return roots;
        }
        Collections.sort(roots);
        List < Double > filtered = new ArrayList < > ();
        double last = roots.get(0);
        filtered.add(last);
        for (double root: roots) {
            if (Math.abs(root - last) > minSeparation) {
                filtered.add(root);
                last = root;
            }
        }
        return filtered;
    }
}
