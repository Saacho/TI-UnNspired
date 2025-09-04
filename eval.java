import java.util.*;
import java.util.function.DoubleUnaryOperator;


public class eval {


    private static final Set < String > FUNCTIONS = Set.of("sin", "cos", "tan", "ln", "sqrt", "cbrt", "abs", "arctan", "arccos", "arcsin");
    private static final Set < Character > OPERATORS = Set.of('+', '-', '*', '/', '^');


    // Tokenizes the input expression
    static ArrayList < String > tokenize(String expression) {
        ArrayList < String > tokens = new ArrayList < > ();
        StringBuilder token = new StringBuilder();


        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);


            if (Character.isWhitespace(c)) {
                continue; // Skip whitespace
            }


            // If it's a minus sign and it's followed by a digit or period (like 0-x)
            if (c == '-' && (i == 0 || OPERATORS.contains(expression.charAt(i - 1)) || expression.charAt(i - 1) == '(' || FUNCTIONS.contains(expression.charAt(i - 1)))) {
                token.append(c); // Treat as negative sign (unary minus)
            } else if (Character.isDigit(c) || c == '.') {
                // Build numeric tokens
                token.append(c);
            } else if (Character.isLetter(c)) {
                // Build variable or function tokens
                if (token.length() > 0 && !Character.isLetter(token.charAt(0))) {
                    tokens.add(token.toString());
                    token.setLength(0);
                }
                token.append(c);
            } else if (OPERATORS.contains(c) || c == '(' || c == ')') {
                // Add existing token before operator or parentheses
                if (token.length() > 0) {
                    tokens.add(token.toString());
                    token.setLength(0);
                }
                tokens.add(String.valueOf(c)); // Add operator or parenthesis as a token
            } else {
                throw new IllegalArgumentException("Unexpected character: " + c);
            }


            // If the token is complete, add it to the tokens list
            if (token.length() > 0 && (i == expression.length() - 1 || OPERATORS.contains(c) || c == '(' || c == ')')) {
                tokens.add(token.toString());
                token.setLength(0);
            }
        }


        // Add the final token if it exists
        if (token.length() > 0) {
            tokens.add(token.toString());
        }


        return tokens;
    }


    // Converts infix notation to postfix (RPN)
    static ArrayList < String > toPostfix(List < String > tokens) {
        ArrayList < String > output = new ArrayList < > ();
        Stack < String > operators = new Stack < > ();


        for (String token: tokens) {
            if (isNumber(token) || token.equals("x") || token.equals("e") || token.equals("p")) {
                output.add(token); // Add numbers and variables directly to output
            } else if (FUNCTIONS.contains(token)) {
                operators.push(token); // Push functions onto the stack
            } else if (OPERATORS.contains(token.charAt(0))) {
                // Handle operators
                while (!operators.isEmpty() &&
                    precedence(operators.peek()) >= precedence(token)) {
                    output.add(operators.pop());
                }
                operators.push(token);
            } else if (token.equals("(")) {
                operators.push(token); // Push '(' onto the stack
            } else if (token.equals(")")) {
                // Pop until '(' is found
                while (!operators.isEmpty() && !operators.peek().equals("(")) {
                    output.add(operators.pop());
                }
                if (operators.isEmpty()) {
                    throw new IllegalArgumentException("Mismatched parentheses");
                }
                operators.pop(); // Remove '('
                if (!operators.isEmpty() && FUNCTIONS.contains(operators.peek())) {
                    output.add(operators.pop()); // Add function to output
                }
            } else {
                throw new IllegalArgumentException("Unknown token: " + token);
            }
        }


        // Add remaining operators to output
        while (!operators.isEmpty()) {
            String op = operators.pop();
            if (op.equals("(") || op.equals(")")) {
                throw new IllegalArgumentException("Mismatched parentheses");
            }
            output.add(op);
        }


        return output;
    }


    // Evaluates an expression in postfix notation
    private static double evaluatePostfix(List < String > postfix, double x) {
        Stack < Double > stack = new Stack < > ();


        for (String token: postfix) {
            if (isNumber(token) && !token.equals("e") && !token.equals("p")) {
                stack.push(Double.parseDouble(token));
            } else if (isNumber(token) && token.equals("e") && !token.equals("p")) {
                stack.push(Math.E);
            } else if (isNumber(token) && !token.equals("e") && token.equals("p")) {
                stack.push(Math.PI);
            } else if (token.equals("x")) {
                stack.push(x);
            } else if (FUNCTIONS.contains(token)) {
                if (stack.isEmpty()) {
                    throw new IllegalArgumentException("Missing operand for function " + token);
                }
                double arg = stack.pop();
                stack.push(applyFunction(token, arg));
            } else if (OPERATORS.contains(token.charAt(0))) {
                if (token.equals("-")) {
                    // Unary minus
                    double a = stack.pop();
                    stack.push(-a); // Apply unary minus
                } else if (stack.size() < 2) {
                    throw new IllegalArgumentException("Missing operands for operator " + token);
                } else {
                    double b = stack.pop();
                    double a = stack.pop();
                    stack.push(applyOperator(token, a, b));
                }
            }
        }


        if (stack.size() != 1) {
            throw new IllegalArgumentException("Invalid expression");
        }


        return stack.pop();
    }


    private static double applyFunction(String
        function, double value) {
        return switch (function) {
            case "sin" - > Math.sin(value);
            case "cos" - > Math.cos(value);
            case "tan" - > Math.tan(value);
            case "ln" - > Math.log(value);
            case "sqrt" - > Math.sqrt(value);
            case "cbrt" - > Math.cbrt(value);
            case "abs" - > Math.abs(value);
            case "arctan" - > Math.atan(value);
            case "arcsin" - > Math.asin(value);
            case "arccos" - > Math.acos(value);
            default - >
            throw new IllegalArgumentException("Unknown function: " + function);
        };
    }


    private static double applyOperator(String operator, double a, double b) {
        return switch (operator) {
            case "+" - > a + b;
            case "-" - > a - b;
            case "*" - > a * b;
            case "/" - > a / b;
            case "^" - > Math.pow(a, b);
            default - >
            throw new IllegalArgumentException("Unknown operator: " + operator);
        };
    }


    private static boolean isNumber(String token) {
        if (token.equals("e") || token.equals("p")) {
            return true;
        }
        try {
            Double.parseDouble(token);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }


    private static int precedence(String operator) {
        return switch (operator) {
            case "+", "-" - > 1;
            case "*", "/" - > 2;
            case "^" - > 3;
            default - > 0;
        };
    }


    public static DoubleUnaryOperator parseExpression(String expression) {
        List < String > tokens = tokenize(expression);
        List < String > postfix = toPostfix(tokens);
        return x - > evaluatePostfix(postfix, x);
    }


    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);


        System.out.println("Enter a mathematical expression (use 'x' as the variable):");
        String expression = scanner.nextLine();


        System.out.println("Enter a value for x:");
        double x = scanner.nextDouble();


        DoubleUnaryOperator func = parseExpression(expression);
        double result = func.applyAsDouble(x);


        System.out.println("Result: " + result);


        scanner.close();
    }
}
