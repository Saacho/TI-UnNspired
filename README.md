
## TIUninspired

a fun little graphing calculator

Overview
--------
TIUninspired is a simple Java-based graphing calculator that allows you to:
- Plot multiple mathematical functions.
- Zoom in/out and pan across the graph.
- See function zeros and intersections.
- Input custom functions dynamically.

Features
--------
- Graph multiple functions simultaneously.
- Keyboard controls for navigation and zoom:
  - `+` / `=`: Zoom in
  - `-` / `_`: Zoom out
  - Arrow keys: Pan graph
  - Number keys `0-9`: Switch between functions
  - `Delete` / `Backspace`: Remove current function
- Automatic detection and display of function zeros.
- Automatic detection and display of intersections with other functions.
- Customizable colors for each function.

Dependencies
------------
- Java SE 8 or higher.
- **Custom classes required:**
  - `RootFinder.java` – numeric root-finding utility.
  - `eval.java` – parses string expressions into evaluatable functions.
- Standard Java libraries:
  - `javax.swing.*`
  - `java.awt.*`
  - `java.awt.geom.*`

Setup
-----
1. Clone or download this repository.
2. Make sure the following files are present:
   - `TIUninspired.java`
   - `RootFinder.java`
   - `eval.java` (or include the relevant library)
3. Compile the Java files:
   ```
   javac *.java
   ```
4. Run the program:
   ```
   java TIUninspired
   ```

Usage
-----
- Enter a function in the input field at the top (e.g., `x^2 + 3*x - 5`).
- Press `Enter` to add the function to the graph.
- Use keyboard shortcuts to navigate, zoom, and switch functions.
- Click inside the window to focus before using keyboard controls.

Notes
-----
- Function terms must be separated by `+`.
- Exponentiation must use the form `a^(b^(c^d))` for nested powers.
- Only standard mathematical expressions are supported (addition, subtraction, multiplication, division, exponentiation).

Contributing
------------
Feel free to fork the project and submit pull requests. Ensure that any new functionality maintains compatibility with existing graphing features.
