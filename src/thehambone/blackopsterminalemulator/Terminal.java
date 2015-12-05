/*
 * The MIT License
 *
 * Copyright 2015 thehambone <thehambone93@gmail.com>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package thehambone.blackopsterminalemulator;

import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.TimeUnit;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import thehambone.blackopsterminalemulator.util.FixedLengthQueue;
/**
 * Created on Nov 18, 2015.
 *
 * @author thehambone <thehambone93@gmail.com>
 */
public final class Terminal
{
    public static final int COLUMNS = 80;
    public static final int LINES = 27;
    
    private static final int INPUT_BUFFER_LENGTH = 81;
    
    // These characters won't be used anywhere else, so why not?
    private static final char INPUT_HISTORY_CYCLE_UP = '\uFFFE';
    private static final char INPUT_HISTORY_CYCLE_DOWN = '\uFFFF';
    
    private static final int MAX_LOGIN_SHELLS = 15;
    
    private static final Terminal TERMINAL_INSTANCE = new Terminal();
    
    private final Object inputLock;
    
    private final JFrame frame;
    
    private final Screen screen;
    
    private final InputMap inputMap;
    private final ActionMap actionMap;
    
    private final Stack<LoginShell> activeShells;
    private final FixedLengthQueue<String> inputHistory;
    private final List<System> systems;
    
    private volatile char charTyped;
    
    private String motd;
    
    // Don't allow this class to be instantiated externally
    private Terminal()
    {
        inputLock = new Object();
        
        frame = new JFrame();
        
        Screen.ScreenColor bg = Screen.ScreenColor.BLACK;
        Screen.ScreenColor fg = Screen.ScreenColor.WHITE;
        Font font = new Font("Courier New", Font.PLAIN, 13);
        int cursorBlinkRate = 300;
        screen = new Screen(COLUMNS, LINES, bg, fg, font, cursorBlinkRate);
        
        inputMap = new InputMap();
        actionMap = new ActionMap();
        screen.getComponent().setInputMap(JComponent.WHEN_FOCUSED, inputMap);
        screen.getComponent().setActionMap(actionMap);
        
        activeShells = new Stack<>();
        
        inputHistory = new FixedLengthQueue<>(8);
        
        systems = new ArrayList<>();
        
        charTyped = 0;
        
        motd = "";
        
        registerInputKeys();
    }
    
    /**
     * Displays the terminal window.
     * <p>
     * The terminal window is displayed in a non-resizable JFrame that appears
     * at the center of the screen when created.
     */
    public static void show()
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                JFrame frame = TERMINAL_INSTANCE.frame;
                Screen screen = TERMINAL_INSTANCE.screen;
                JComponent screenComponent = screen.getComponent();
                frame.add(screenComponent);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setResizable(false);
                frame.setSize(screenComponent.getPreferredSize());
                frame.setLocationRelativeTo(null);  // Center frame on screen
                frame.setVisible(true);
            }
        });
        
        // Move cusor to bottom of screen
        for (int i = 0; i < LINES; i++) {
            Terminal.println();
        }
    }
    
    /**
     * Adds a system to the list of available systems.
     * 
     * @param s the system to add
     */
    public static void addSystem(System s)
    {
        TERMINAL_INSTANCE.systems.add(s);
    }
    
    /**
     * Gets a system from the list of available systems by name.
     * 
     * @param name the name of the system to be retrieved
     * @return the desired system if found, {@code null} if the system is not
     *         found
     */
    public static System getSystem(String name)
    {
        System system = null;
        
        for (System s : TERMINAL_INSTANCE.systems) {
            if (s.getName().equalsIgnoreCase(name)) {
                system = s;
                break;
            }
        }
        
        return system;
    }
    
    public static boolean maxLoginShellsReached()
    {
        return TERMINAL_INSTANCE.activeShells.size() == MAX_LOGIN_SHELLS;
    }
    
    public static LoginShell getActiveLoginShell()
    {
        return TERMINAL_INSTANCE.activeShells.peek();
    }
    
    public static void pushLoginShell(LoginShell shell)
    {
        TERMINAL_INSTANCE.activeShells.push(shell);
    }
    
    public static LoginShell popLoginShell()
    {
        return TERMINAL_INSTANCE.activeShells.pop();
    }
    
    /**
     * Sets the window title.
     * 
     * @param title the text to be used as the new title
     */
    public static void setTitle(String title)
    {
        TERMINAL_INSTANCE.frame.setTitle(title);
    }
    
    /**
     * Sets the message of the day.
     * 
     * @param motd the new message of the day
     */
    public static void setMOTD(String motd)
    {
        TERMINAL_INSTANCE.motd = motd;
    }
    
    /**
     * Prints the message of the day.
     */
    public static void printMOTD()
    {
        println(TERMINAL_INSTANCE.motd);
    }
    
    /**
     * Appends a character to the screen.
     * 
     * @param c the character to be printed
     */
    public static void print(char c)
    {
        TERMINAL_INSTANCE.screen.print(c);
    }
    
    /**
     * Appends a string of characters to the screen.
     * 
     * @param s the string to be printed
     */
    public static void print(String s)
    {
        char c;
        int lineIndex = 0;
        int linesPrinted = 0;
        
        // Print string character by character
        for (int i = 0; i < s.length(); i++) {
            // Get next char and print
            c = s.charAt(i);
            print(c);
            
            // Increment number of lines printed
            lineIndex++;
            if (lineIndex > (COLUMNS - 1)
                    || c == '\n') {
                linesPrinted++;
                lineIndex = 0;
            }
            
            // Show "--MORE--" pager prompt
            if (linesPrinted == 22) {
                // Print "--MORE--" prompt
                String morePrompt = "--MORE--";
                for (int j = 0; j < morePrompt.length(); j++) {
                    print(morePrompt.charAt(j));
                }
                
                // Wait for keypress
                getChar(false);
                
                // Backspace "--MORE--" prompt
                for (int j = 0; j < morePrompt.length(); j++) {
                    print('\b');
                }
                
                // Reset lines printed counter
                linesPrinted = 0;
            }
            
            // Sleep 1ms to simulate output on a terminal with a low baud rate
            try {
                TimeUnit.MILLISECONDS.sleep(1);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
    
    /**
     * Appends a newline to the screen.
     */
    public static void println()
    {
        println("");
    }
    
    /**
     * Appends a string of characters to the screen followed by a newline.
     * 
     * @param s the string to be printed
     */
    public static void println(String s)
    {
        print(s + "\n");
    }
    
    /**
     * Appends an image to the screen followed by a newline.
     * 
     * @param img the image to be printed
     */
    public static void println(BufferedImage img)
    {
        TERMINAL_INSTANCE.screen.printImage(img);
        println();
    }
    
    /**
     * Gets a character typed from the keyboard and prints that character to the
     * screen.
     * <p>
     * This method blocks until a character is typed.
     * 
     * @return the typed character
     */
    public static char getChar()
    {
        return getChar(true);
    }
    
    /**
     * Gets a character typed from the keyboard.
     * <p>
     * This method blocks until a character is typed.
     * 
     * @param printChar a boolean indicating whether the typed character should
     *                  be displayed on screen
     * @return the typed character
     */
    public static char getChar(boolean printChar)
    {
        // Reset typed char
        TERMINAL_INSTANCE.charTyped = 0;
        
        // Wait until a character is typed
        synchronized (TERMINAL_INSTANCE.inputLock) {
            try {
                TERMINAL_INSTANCE.inputLock.wait();
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        }
        
        // Print the character
        if (printChar && TERMINAL_INSTANCE.charTyped != 0) {
            print(TERMINAL_INSTANCE.charTyped);
        }
        
        return TERMINAL_INSTANCE.charTyped;
    }
    
    /**
     * Reads a string of characters typed from the keyboard and prints
     * characters to the screen as they're typed.
     * <p>
     * This method blocks until the {@code <ENTER>} key is pressed.
     * 
     * @return the string typed
     */
    public static String readLine()
    {
        return readLine((char)0);
    }
    
    /**
     * Reads a string of characters typed from the keyboard.
     * <p>
     * This method blocks until the {@code <ENTER>} key is pressed.
     * 
     * @param charToPrint the character to print to the screen in place of the
     *                    typed characters; use 0 ({@code NUL}) to indicate that
     *                    characters should be printed exactly as they're typed
     * @return the string typed
     */
    public static String readLine(char charToPrint)
    {
        char c;
        char[] buf;
        int bufPointer;
        boolean printDifferentChar;
        boolean isReadingInput;
        int historyIndex;
        String input;
        FixedLengthQueue<String> inputHistory;
        
        buf = new char[INPUT_BUFFER_LENGTH];
        bufPointer = 0;
        printDifferentChar = charToPrint != 0;
        isReadingInput = true;
        historyIndex = 0;
        inputHistory = TERMINAL_INSTANCE.inputHistory;
        
        // Loop until <enter> is pressed
        do {
            // Get character
            c = getChar(false);
            
            // Determine the character to print
            if (!printDifferentChar) {
                charToPrint = c;
            }
            
            // Handle control characters
            switch (c) {
                case '\n':
                    isReadingInput = false;     // End the loop
                    println();
                    break;
                case '\b':  // Backspace
                    if (bufPointer > 0) {
                        buf[--bufPointer] = 0;
                        print('\b');
                    }
                    break;
                case INPUT_HISTORY_CYCLE_UP:
                    // Ignore if historyIndex is out of range
                    if (historyIndex + 1 > inputHistory.getItemCount()) {
                        continue;
                    }
                    
                    historyIndex++;
                    
                    // Get item from input history
                    input = cycleInputHistory(historyIndex);
                    if (input == null) {
                        continue;
                    }
                    
                    // Erase the current input
                    for (int i = 0; i < buf.length; i++) {
                        if (buf[i] == 0) {
                            break;
                        }
                        print('\b');
                    }
                    
                    // Reset the input buffer
                    bufPointer = 0;
                    buf = new char[INPUT_BUFFER_LENGTH];
                    
                    // Add the retrieved string to input buffer and print
                    for (int i = 0; i < input.length(); i++) {
                        buf[i] = input.charAt(i);
                        bufPointer++;
                    }
                    print(input);
                    break;
                case INPUT_HISTORY_CYCLE_DOWN:
                    // Ignore if historyIndex is out of range
                    if (historyIndex - 1 < 0) {
                        continue;
                    }
                    
                    historyIndex--;
                    
                    /* Determine wheter the queue should wrap to the beginning
                       after the item at the current index has been retrieved.
                       This is used to replicate is what is presumed to be a bug
                       in the actual terminal. When cycling down through the
                       input history and the most recently typed item is reached
                       (i.e. the end of the queue is reached), cycling down once
                       more will cause the first item in the queue to be
                       displayed.
                    */
                    boolean wrapAround = false;
                    if (historyIndex == 0) {
                        historyIndex = inputHistory.getItemCount();
                        wrapAround = true;
                    }
                    
                    // Get item from input history
                    input = cycleInputHistory(historyIndex);
                    if (input == null) {
                        continue;
                    }
                    
                    // Erase the current input
                    for (int i = 0; i < buf.length; i++) {
                        if (buf[i] == 0) {
                            break;
                        }
                        print('\b');
                    }
                    
                    // Reset the input buffer
                    bufPointer = 0;
                    buf = new char[INPUT_BUFFER_LENGTH];
                    
                    // Add the retrieved string to input buffer and print
                    for (int i = 0; i < input.length(); i++) {
                        buf[i] = input.charAt(i);
                        bufPointer++;
                    }
                    print(input);
                    
                    // "Wrap" to the beginning of the queue
                    if (wrapAround) {
                        historyIndex = 0;
                    }
                    break;
                default:
                    // Add character to buffer and print character
                    if (bufPointer < buf.length) {
                        buf[bufPointer++] = c;
                        print(charToPrint);
                    }
            }
        } while (isReadingInput);
        
        
        // Create string from input buffer
        // Trim to remove extra null characters
        input = new String(buf).trim();
        
        // Add input string to the input history queue
        if (inputHistory.isFull()) {
            inputHistory.remove();
        }
        inputHistory.insert(input);
        
        return input;
    }
    
    /*
     * Returns the string at the specified index in the input history queue.
     * If the index is out of range, null is returned.
     */
    private static String cycleInputHistory(int index)
    {
        FixedLengthQueue<String> inputHistory = TERMINAL_INSTANCE.inputHistory;
        Iterator<String> it = inputHistory.iterator();
        String previousInput = null;
        int i = 0;
        
        while (it.hasNext() && i < inputHistory.getItemCount() + 1 - index) {
            previousInput = it.next();
            i++;
        }
        
        return previousInput;
    }
    
    /*
     * Registers all of the keystrokes that can be used for input.
     */
    private void registerInputKeys()
    {
        // Lowercase letters
        registerInputKey('a', KeyEvent.VK_A, 0);
        registerInputKey('b', KeyEvent.VK_B, 0);
        registerInputKey('c', KeyEvent.VK_C, 0);
        registerInputKey('d', KeyEvent.VK_D, 0);
        registerInputKey('e', KeyEvent.VK_E, 0);
        registerInputKey('f', KeyEvent.VK_F, 0);
        registerInputKey('g', KeyEvent.VK_G, 0);
        registerInputKey('h', KeyEvent.VK_H, 0);
        registerInputKey('i', KeyEvent.VK_I, 0);
        registerInputKey('j', KeyEvent.VK_J, 0);
        registerInputKey('k', KeyEvent.VK_K, 0);
        registerInputKey('l', KeyEvent.VK_L, 0);
        registerInputKey('m', KeyEvent.VK_M, 0);
        registerInputKey('n', KeyEvent.VK_N, 0);
        registerInputKey('o', KeyEvent.VK_O, 0);
        registerInputKey('p', KeyEvent.VK_P, 0);
        registerInputKey('q', KeyEvent.VK_Q, 0);
        registerInputKey('r', KeyEvent.VK_R, 0);
        registerInputKey('s', KeyEvent.VK_S, 0);
        registerInputKey('t', KeyEvent.VK_T, 0);
        registerInputKey('u', KeyEvent.VK_U, 0);
        registerInputKey('v', KeyEvent.VK_V, 0);
        registerInputKey('w', KeyEvent.VK_W, 0);
        registerInputKey('x', KeyEvent.VK_X, 0);
        registerInputKey('y', KeyEvent.VK_Y, 0);
        registerInputKey('z', KeyEvent.VK_Z, 0);
        
        // Uppercase letters
        registerInputKey('A', KeyEvent.VK_A, KeyEvent.SHIFT_DOWN_MASK);
        registerInputKey('B', KeyEvent.VK_B, KeyEvent.SHIFT_DOWN_MASK);
        registerInputKey('C', KeyEvent.VK_C, KeyEvent.SHIFT_DOWN_MASK);
        registerInputKey('D', KeyEvent.VK_D, KeyEvent.SHIFT_DOWN_MASK);
        registerInputKey('E', KeyEvent.VK_E, KeyEvent.SHIFT_DOWN_MASK);
        registerInputKey('F', KeyEvent.VK_F, KeyEvent.SHIFT_DOWN_MASK);
        registerInputKey('G', KeyEvent.VK_G, KeyEvent.SHIFT_DOWN_MASK);
        registerInputKey('H', KeyEvent.VK_H, KeyEvent.SHIFT_DOWN_MASK);
        registerInputKey('I', KeyEvent.VK_I, KeyEvent.SHIFT_DOWN_MASK);
        registerInputKey('J', KeyEvent.VK_J, KeyEvent.SHIFT_DOWN_MASK);
        registerInputKey('K', KeyEvent.VK_K, KeyEvent.SHIFT_DOWN_MASK);
        registerInputKey('L', KeyEvent.VK_L, KeyEvent.SHIFT_DOWN_MASK);
        registerInputKey('M', KeyEvent.VK_M, KeyEvent.SHIFT_DOWN_MASK);
        registerInputKey('N', KeyEvent.VK_N, KeyEvent.SHIFT_DOWN_MASK);
        registerInputKey('O', KeyEvent.VK_O, KeyEvent.SHIFT_DOWN_MASK);
        registerInputKey('P', KeyEvent.VK_P, KeyEvent.SHIFT_DOWN_MASK);
        registerInputKey('Q', KeyEvent.VK_Q, KeyEvent.SHIFT_DOWN_MASK);
        registerInputKey('R', KeyEvent.VK_R, KeyEvent.SHIFT_DOWN_MASK);
        registerInputKey('S', KeyEvent.VK_S, KeyEvent.SHIFT_DOWN_MASK);
        registerInputKey('T', KeyEvent.VK_T, KeyEvent.SHIFT_DOWN_MASK);
        registerInputKey('U', KeyEvent.VK_U, KeyEvent.SHIFT_DOWN_MASK);
        registerInputKey('V', KeyEvent.VK_V, KeyEvent.SHIFT_DOWN_MASK);
        registerInputKey('W', KeyEvent.VK_W, KeyEvent.SHIFT_DOWN_MASK);
        registerInputKey('X', KeyEvent.VK_X, KeyEvent.SHIFT_DOWN_MASK);
        registerInputKey('Y', KeyEvent.VK_Y, KeyEvent.SHIFT_DOWN_MASK);
        registerInputKey('Z', KeyEvent.VK_Z, KeyEvent.SHIFT_DOWN_MASK);
        
        // Numbers
        registerInputKey('0', KeyEvent.VK_0, 0);
        registerInputKey('1', KeyEvent.VK_1, 0);
        registerInputKey('2', KeyEvent.VK_2, 0);
        registerInputKey('3', KeyEvent.VK_3, 0);
        registerInputKey('4', KeyEvent.VK_4, 0);
        registerInputKey('5', KeyEvent.VK_5, 0);
        registerInputKey('6', KeyEvent.VK_6, 0);
        registerInputKey('7', KeyEvent.VK_7, 0);
        registerInputKey('8', KeyEvent.VK_8, 0);
        registerInputKey('9', KeyEvent.VK_9, 0);
        registerInputKey('0', KeyEvent.VK_NUMPAD0, 0);
        registerInputKey('1', KeyEvent.VK_NUMPAD1, 0);
        registerInputKey('2', KeyEvent.VK_NUMPAD2, 0);
        registerInputKey('3', KeyEvent.VK_NUMPAD3, 0);
        registerInputKey('4', KeyEvent.VK_NUMPAD4, 0);
        registerInputKey('5', KeyEvent.VK_NUMPAD5, 0);
        registerInputKey('6', KeyEvent.VK_NUMPAD6, 0);
        registerInputKey('7', KeyEvent.VK_NUMPAD7, 0);
        registerInputKey('8', KeyEvent.VK_NUMPAD8, 0);
        registerInputKey('9', KeyEvent.VK_NUMPAD9, 0);
        
        // Symbols
        registerInputKey('-', KeyEvent.VK_MINUS, 0);
        registerInputKey('=', KeyEvent.VK_EQUALS, 0);
        registerInputKey('[', KeyEvent.VK_OPEN_BRACKET, 0);
        registerInputKey(']', KeyEvent.VK_CLOSE_BRACKET, 0);
        registerInputKey('\\', KeyEvent.VK_BACK_SLASH, 0);
        registerInputKey(';', KeyEvent.VK_SEMICOLON, 0);
        registerInputKey('\'', KeyEvent.VK_QUOTE, 0);
        registerInputKey(',', KeyEvent.VK_COMMA, 0);
        registerInputKey('.', KeyEvent.VK_PERIOD, 0);
        registerInputKey('/', KeyEvent.VK_SLASH, 0);
        registerInputKey('/', KeyEvent.VK_DIVIDE, 0);
        registerInputKey('*', KeyEvent.VK_MULTIPLY, 0);
        registerInputKey('-', KeyEvent.VK_SUBTRACT, 0);
        registerInputKey('+', KeyEvent.VK_ADD, 0);
        registerInputKey('.', KeyEvent.VK_DECIMAL, 0);
        registerInputKey(' ', KeyEvent.VK_SPACE, 0);
        registerInputKey('!', KeyEvent.VK_1, KeyEvent.SHIFT_DOWN_MASK);
        registerInputKey('@', KeyEvent.VK_2, KeyEvent.SHIFT_DOWN_MASK);
        registerInputKey('#', KeyEvent.VK_3, KeyEvent.SHIFT_DOWN_MASK);
        registerInputKey('$', KeyEvent.VK_4, KeyEvent.SHIFT_DOWN_MASK);
        registerInputKey('%', KeyEvent.VK_5, KeyEvent.SHIFT_DOWN_MASK);
        registerInputKey('^', KeyEvent.VK_6, KeyEvent.SHIFT_DOWN_MASK);
        registerInputKey('&', KeyEvent.VK_7, KeyEvent.SHIFT_DOWN_MASK);
        registerInputKey('*', KeyEvent.VK_8, KeyEvent.SHIFT_DOWN_MASK);
        registerInputKey('(', KeyEvent.VK_9, KeyEvent.SHIFT_DOWN_MASK);
        registerInputKey(')', KeyEvent.VK_0, KeyEvent.SHIFT_DOWN_MASK);
        registerInputKey('_', KeyEvent.VK_MINUS, KeyEvent.SHIFT_DOWN_MASK);
        registerInputKey('+', KeyEvent.VK_EQUALS, KeyEvent.SHIFT_DOWN_MASK);
        registerInputKey('{', KeyEvent.VK_OPEN_BRACKET, KeyEvent.SHIFT_DOWN_MASK);
        registerInputKey('}', KeyEvent.VK_CLOSE_BRACKET,KeyEvent.SHIFT_DOWN_MASK);
        registerInputKey('|', KeyEvent.VK_BACK_SLASH, KeyEvent.SHIFT_DOWN_MASK);
        registerInputKey(':', KeyEvent.VK_SEMICOLON, KeyEvent.SHIFT_DOWN_MASK);
        registerInputKey('"', KeyEvent.VK_QUOTE, KeyEvent.SHIFT_DOWN_MASK);
        registerInputKey('<', KeyEvent.VK_COMMA, KeyEvent.SHIFT_DOWN_MASK);
        registerInputKey('>', KeyEvent.VK_PERIOD, KeyEvent.SHIFT_DOWN_MASK);
        registerInputKey('?', KeyEvent.VK_SLASH, KeyEvent.SHIFT_DOWN_MASK);
        registerInputKey('/', KeyEvent.VK_DIVIDE, KeyEvent.SHIFT_DOWN_MASK);
        registerInputKey('*', KeyEvent.VK_MULTIPLY, KeyEvent.SHIFT_DOWN_MASK);
        registerInputKey('-', KeyEvent.VK_SUBTRACT, KeyEvent.SHIFT_DOWN_MASK);
        registerInputKey('+', KeyEvent.VK_ADD, KeyEvent.SHIFT_DOWN_MASK);
        registerInputKey(' ', KeyEvent.VK_SPACE, KeyEvent.SHIFT_DOWN_MASK);
        
        // Control characters
        registerInputKey('\b', KeyEvent.VK_BACK_SPACE, 0);
        registerInputKey('\b', KeyEvent.VK_BACK_SPACE, KeyEvent.SHIFT_DOWN_MASK);
        registerInputKey('\n', KeyEvent.VK_ENTER, 0);
        registerInputKey('\n', KeyEvent.VK_ENTER, KeyEvent.SHIFT_DOWN_MASK);
        registerInputKey(INPUT_HISTORY_CYCLE_UP, KeyEvent.VK_UP, 0);
        registerInputKey(INPUT_HISTORY_CYCLE_DOWN, KeyEvent.VK_DOWN, 0);
    }
    
    /*
     * Registers a keystroke for input.
     */
    private void registerInputKey(final char ch, int keyChar, int modifiers)
    {
        AbstractAction keyAction = new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                synchronized (inputLock) {
                    char c = ch;
                    
                    // Handle caps lock
                    if (isCapsLockEnabled()) {
                        if (c > 0x40 && c < 0x5B) {         // ASCII a-z
                            // Make uppercase
                            c += 0x20;
                        } else if (c > 0x60 && c < 0x7B) {  // ASCII A-Z
                            // Make lowercase
                            c -= 0x20;
                        }
                    }
                    
                    charTyped = c;
                    inputLock.notify();
                }
            }
        };
        
        inputMap.put(KeyStroke.getKeyStroke(keyChar, modifiers), ch);
        actionMap.put(ch, keyAction);
    }
    
    /*
     * Checks whether caps lock is enabled on the keyboard.
     */
    private boolean isCapsLockEnabled()
    {
        Toolkit tk = Toolkit.getDefaultToolkit();
        return tk.getLockingKeyState(KeyEvent.VK_CAPS_LOCK);
    }
}
