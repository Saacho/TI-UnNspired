import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.function.DoubleUnaryOperator;
import javax.swing.*;
import java.awt.geom.*;


public class TIUnNspired extends Canvas {
    private static int width = 800;
    private static int height = 600;
    private int scale = 50; // dynamic scale (pixels per unit)
    private static ArrayList < Function > functions;
    private int currentFunctionIndex = 0;
    private static boolean addedAFunction = false;


    private static Color[] graphColors = new Color[] {
        Color.BLUE,
            Color.RED,
            Color.BLACK,
            new Color(0x800080),
            Color.GREEN,
            Color.ORANGE,
            Color.MAGENTA,
            Color.GRAY,
            new Color(0xffcd17),
            Color.PINK
    };


    private ArrayList < Point > zeros = new ArrayList < > (); // to store zeros of the function
    private ArrayList < Point > intersections = new ArrayList < > (); // to store intersections between functions


    private double xOffset = 0, yOffset = 0;


    public TIUnNspired(ArrayList < Function > functions) {
        currentFunctionIndex = 0;


        // Convert expressions into Function objects and add them to the functions list
        for (Function func: functions) {
            functions.add(func);
        }


        calculatePOI();


        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyChar()) {
                    case '+', '=' - > zoom(true); // Zoom In
                    case '-', '_' - > zoom(false); // Zoom Out
                }


                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT - > {
                        xOffset -= 0.5;repaint();
                    }
                    case KeyEvent.VK_RIGHT - > {
                        xOffset += 0.5;repaint();
                    }
                    case KeyEvent.VK_UP - > {
                        yOffset -= 0.5;repaint();
                    }
                    case KeyEvent.VK_DOWN - > {
                        yOffset += 0.5;repaint();
                    }


                    case KeyEvent.VK_0 - > {
                        currentFunctionIndex = 0;calculatePOI();repaint();
                    }
                    case KeyEvent.VK_1 - > {
                        currentFunctionIndex = 1;calculatePOI();repaint();
                    }
                    case KeyEvent.VK_2 - > {
                        currentFunctionIndex = 2;calculatePOI();repaint();
                    }
                    case KeyEvent.VK_3 - > {
                        currentFunctionIndex = 3;calculatePOI();repaint();
                    }
                    case KeyEvent.VK_4 - > {
                        currentFunctionIndex = 4;calculatePOI();repaint();
                    }
                    case KeyEvent.VK_5 - > {
                        currentFunctionIndex = 5;calculatePOI();repaint();
                    }
                    case KeyEvent.VK_6 - > {
                        currentFunctionIndex = 6;calculatePOI();repaint();
                    }
                    case KeyEvent.VK_7 - > {
                        currentFunctionIndex = 7;calculatePOI();repaint();
                    }
                    case KeyEvent.VK_8 - > {
                        currentFunctionIndex = 8;calculatePOI();repaint();
                    }
                    case KeyEvent.VK_9 - > {
                        currentFunctionIndex = 9;calculatePOI();repaint();
                    }


                    case KeyEvent.VK_DELETE, KeyEvent.VK_BACK_SPACE - > {
                        if (functions.size() > 1) {
                            functions.remove(currentFunctionIndex);
                            if (currentFunctionIndex > 0) currentFunctionIndex--;
                            calculatePOI();
                            repaint();
                        }
                    }


                    default - > yOffset += 0; // No action for other keys
                }
            }
        });


        setFocusable(true);
    }


    private void zoom(boolean zoomIn) {
        int mouseX = MouseInfo.getPointerInfo().getLocation().x - getLocationOnScreen().x;
        int mouseY = MouseInfo.getPointerInfo().getLocation().y - getLocationOnScreen().y;


        // Convert mouse position to graph coordinates
        double graphX = (mouseX - getWidth() / 2.0) / scale + xOffset;
        double graphY = (getHeight() / 2.0 - mouseY) / scale + yOffset;


        // Adjust scale
        int oldScale = scale;
        if (zoomIn) {
            scale = Math.min(scale + 20, 1000); // Upper limit
        } else {
            scale = Math.max(scale - 20, 1); // Lower limit
        }


        // Adjust offsets to keep the zoom centered
        xOffset += graphX - ((mouseX - getWidth() / 2.0) / scale + xOffset);
        yOffset += graphY - ((getHeight() / 2.0 - mouseY) / scale + yOffset);


        if (oldScale != scale) {
            calculatePOI();
            repaint();
        }
    }


    @Override
    public void paint(Graphics g) {
        width = getWidth();
        height = getHeight();
        g.clearRect(0, 0, width, height);
        g.setColor(Color.BLACK);


        int scaledXOffset = (int) Math.round(xOffset * scale);
        int scaledYOffset = (int) Math.round(yOffset * scale);


        g.drawLine(width / 2 - scaledXOffset, 0, width / 2 - scaledXOffset, height); // y-axis
        g.drawLine(0, height / 2 - scaledYOffset, width, height / 2 - scaledYOffset); // x-axis
        int tickSpacing = 50;
        g.setColor(Color.GRAY);
        for (int x = -2 * width; x <= width * 4; x += tickSpacing) {
            int graphX = (x - width / 2);
            double graphCoord = graphX / (double) scale;
            g.drawLine(x - scaledXOffset, height / 2 - 5 - scaledYOffset, x - scaledXOffset, height / 2 + 5 - scaledYOffset);
            if (graphCoord != 0) {
                g.drawString(String.format("%.1f", graphCoord), x - 15 - scaledXOffset, height / 2 + 20 - scaledYOffset);
            }
        }
        for (int y = -2 * height; y <= height * 4; y += tickSpacing) {
            int graphY = (height / 2 - y);
            double graphCoord = graphY / (double) scale;
            g.drawLine(width / 2 - 5 - scaledXOffset, y - scaledYOffset, width / 2 + 5 - scaledXOffset, y - scaledYOffset);
            if (graphCoord != 0) {
                g.drawString(String.format("%.1f", graphCoord), width / 2 + 10 - scaledXOffset, y + 5 - scaledYOffset);
            }
        }


        // graph the function
        for (int k = 0; k < functions.size(); k++) {
            Function func = functions.get(k);
            g.setColor(graphColors[k]);
            if (k == currentFunctionIndex) {
                ((Graphics2D) g).setStroke(new BasicStroke(2));
            } else {
                ((Graphics2D) g).setStroke(new BasicStroke(1));
            }


            for (double i = -width / 2; i < width / 2; i++) {
                double x1 = (i / (double) scale) + xOffset;
                double x2 = ((i + 1) / (double) scale) + xOffset;


                // Evaluate the function at both points
                double y1 = func.evaluate(x1) + yOffset;
                double y2 = func.evaluate(x2) + yOffset;


                // Check if both y1 and y2 are valid numbers (and the slope is valid)
                if ((y1 < 100 && y1 > -100) && (y2 < 100 && y2 > -100) && (Math.abs((y2 - y1) / (x2 - x1)) < 500)) {
                    double screenX1 = width / 2 + i;
                    double screenX2 = width / 2 + i + 1;
                    double screenY1 = height / 2 - (int)(y1 * scale);
                    double screenY2 = height / 2 - (int)(y2 * scale);


                    Shape l = new Line2D.Double(screenX1, screenY1, screenX2, screenY2);
                    ((Graphics2D) g).draw(l);
                }
            }
        }


        // draw zeros
        g.setColor(graphColors[currentFunctionIndex]);
        for (Point zero: zeros) {
            int screenX = width / 2 + (int)(zero.x * scale);
            int screenY = height / 2 - (int)(zero.y * scale);
            int dotSize = 6;
            g.fillOval(screenX - dotSize / 2 - scaledXOffset, screenY - dotSize / 2 - scaledYOffset, dotSize, dotSize);
            String coordText = ("(" + new DecimalFormat("#.##").format(zero.x) + "," + new DecimalFormat("#.##").format(zero.y) + ")");
            g.drawString(coordText, screenX + dotSize - scaledXOffset, screenY - dotSize - scaledYOffset);
        }


        // draw intersection
        for (Point intersection: intersections) {
            int screenX = width / 2 + (int)(intersection.x * scale);
            int screenY = height / 2 - (int)(intersection.y * scale);
            int dotSize = 6;
            g.fillRect(screenX - dotSize / 2 - scaledXOffset, screenY - dotSize / 2 - scaledYOffset, dotSize, dotSize);
            String coordText = ("(" + new DecimalFormat("#.##").format(intersection.x) + "," + new DecimalFormat("#.##").format(intersection.y) + ")");
            g.drawString(coordText, screenX + dotSize - scaledXOffset, screenY - dotSize - scaledYOffset);
        }


        // function label
        try {
            g.drawString("f" + currentFunctionIndex + "(x): " + functions.get(currentFunctionIndex).getExpression(), 10, 10);
        } catch (IndexOutOfBoundsException ex) {}


    }


    public class Point {
        public double x;
        public double y;
        public Point(double x, double y) {
            this.x = x;
            this.y = y;
        }
        @Override
        public String toString() {
            return ("(" + new DecimalFormat("#.##").format(x) + "," + new DecimalFormat("#.##").format(y) + ")");
        }
    }


    private void calculatePOI() {
        calculateZeros();
        calculateIntersections();
    }


    private void calculateZeros() {
        zeros.clear();
        int width = getWidth();
        DecimalFormat formatter = new DecimalFormat("#.###");
        RootFinder rootFinder = new RootFinder(1e-15, 1e-17, 1e-17);
        double minX = -width / 2.0 / scale;
        double maxX = width / 2.0 / scale;


        DoubleUnaryOperator func = x - > functions.get(currentFunctionIndex).evaluate(x);


        ArrayList < Double > roots = (ArrayList < Double > ) rootFinder.findAllRoots(func, minX, maxX, 1000);


        for (double root: roots) {
            double test = functions.get(currentFunctionIndex).evaluate(root);
            if (test < 10000 && test > -10000) {
                root = Double.parseDouble(formatter.format(root));
                zeros.add(new Point(root, 0));
            }
        }
    }


    private void calculateIntersections() {
        intersections.clear();
        int width = getWidth();
        ArrayList < Double > storedXValues = new ArrayList < > ();


        RootFinder rootFinder = new RootFinder(1e-15, 1e-15, 1e-17);
        double minX = -width / 2.0 / scale;
        double maxX = width / 2.0 / scale;


        for (int i = 0; i < functions.size(); i++) {
            if (i == currentFunctionIndex) continue;
            final int functionIndex = i;
            DoubleUnaryOperator diffFunc = x - > functions.get(currentFunctionIndex).evaluate(x) - functions.get(functionIndex).evaluate(x);


            ArrayList < Double > intersectionsFound = (ArrayList < Double > ) rootFinder.findAllRoots(diffFunc, minX, maxX, 1000);


            for (double intersectionX: intersectionsFound) {
                double intersectionY = functions.get(currentFunctionIndex).evaluate(intersectionX);


                intersectionX = Math.round(intersectionX * 100000.0) / 100000.0;
                intersectionY = Math.round(intersectionY * 100000.0) / 100000.0;


                double test = functions.get(currentFunctionIndex).evaluate(intersectionX);
                double test2 = functions.get(functionIndex).evaluate(intersectionX);


                if (test < 10000 && test > -10000 && test2 < 10000 && test2 > -10000) {
                    intersections.add(new Point(intersectionX, intersectionY));
                    storedXValues.add(intersectionX);
                }


            }
        }


    }


    public static class Function {
        private final DoubleUnaryOperator

        function;
        private final String expression;
        private final Color color;


        // Constructor for initializing with an expression and color
        public Function(String expression, Color color) {
            this.expression = expression;
            this.function = eval.parseExpression(expression); // Use eval to parse the expression
            this.color = color;
        }


        // Constructor for initializing without color (default to black)
        public Function(String expression) {
            this(expression, Color.BLACK);
        }


        // Evaluates the function for a given x value
        public double evaluate(double x) {
            return function.applyAsDouble(x);
        }


        // Returns the mathematical expression as a string
        public String getExpression() {
            return expression;
        }


        // Returns the color associated with the function
        public Color getColor() {
            return color;
        }
    }


    public static void main(String[] args) {
        functions = new ArrayList < > ();
        JFrame frame = new JFrame("TI Un-Nspired");
        frame.setSize(width, height);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JTextField inputField = new JTextField("Terms must be separated by '+', and exponentiation must be written in the form a^(b^(c^d))...");
        TIUnNspired graphingCanvas = new TIUnNspired(functions);


        inputField.addActionListener(e - > {
            String input = inputField.getText();
            try {
                String expression = input;
                Function

                function = new Function(expression);


                functions.add(function);


                if (addedAFunction) graphingCanvas.currentFunctionIndex++;
                addedAFunction = true;


                graphingCanvas.calculatePOI();
                graphingCanvas.repaint();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Invalid input. Please use a valid mathematical expression.");
            }
        });


        frame.setLayout(new BorderLayout());
        frame.add(inputField, BorderLayout.NORTH);
        frame.add(graphingCanvas, BorderLayout.CENTER);
        frame.setVisible(true);
        frame.setResizable(true);
    }


}
