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
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

/**
 * Created on Nov 18, 2015.
 *
 * @author thehambone <thehambone93@gmail.com>
 */
public final class Terminal
{
    public static final int COLUMNS = 80;
    public static final int LINES = 27;
    
    private static final Font FONT = new Font("Courier New", 0, 13);
    
    private static final int CURSOR_BLINK_RATE = 300;
    private static final int INPUT_BUFFER_LENGTH = 81;
    
    private static final Screen.ScreenColor BACKGROUND
            = Screen.ScreenColor.BLACK;
    private static final Screen.ScreenColor FOREGROUND
            = Screen.ScreenColor.WHITE;
    
    private static final JFrame FRAME = new JFrame();
    private static final Screen SCREEN 
            = new Screen(COLUMNS, LINES, BACKGROUND,
            FOREGROUND, FONT, CURSOR_BLINK_RATE);
    
    private static final InputMap INPUT_MAP = new InputMap();
    private static final ActionMap ACTION_MAP = new ActionMap();
    
    private static final Object INPUT_LOCK = new Object();
    
    private static volatile char charTyped = 0;
    
    private static final Stack<LoginShell> SHELL_STACK = new Stack<>(15);
    
    private static final Queue<String> PREVIOUS_INPUT_QUEUE = new Queue<>(8);
    
    private static final char VK_UP_CHAR = '\uFFFE';
    private static final char VK_DOWN_CHAR = '\uFFFF';
    
    private static String motd = "";
    
    static
    {
        registerKeys();
        
        SCREEN.getComponent().setInputMap(JComponent.WHEN_FOCUSED, INPUT_MAP);
        SCREEN.getComponent().setActionMap(ACTION_MAP);
    }
    
    // Don't allow this class to be instantiated
    private Terminal() { }
    
    public static LoginShell getActiveLoginShell()
    {
        return SHELL_STACK.peek();
    }
    
    public static Stack<LoginShell> getLoginShellStack()
    {
        return SHELL_STACK;
    }
    
    /**
     * Sets the window title.
     * 
     * @param title the text to be used as the new title
     */
    public static void setTitle(String title)
    {
        FRAME.setTitle(title);
    }
    
    public static void setMOTD(String motd)
    {
        Terminal.motd = motd;
    }
    
    public static void printMOTD()
    {
        println(motd);
    }
    
    /**
     * Appends a character to the screen.
     * 
     * @param c the character to be printed
     */
    public static void print(char c)
    {
        SCREEN.print(c);
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
        
        for (int i = 0; i < s.length(); i++) {
            c = s.charAt(i);
            SCREEN.print(c);
            lineIndex++;
            
            if (lineIndex > (COLUMNS - 1)
                    || c == '\n') {
                linesPrinted++;
                lineIndex = 0;
            }
            
            if (linesPrinted == 22) {
                String morePrompt = "--MORE--";
                for (int j = 0; j < morePrompt.length(); j++) {
                    SCREEN.print(morePrompt.charAt(j));
                }
                getChar(false);
                for (int j = 0; j < morePrompt.length(); j++) {
                    SCREEN.print('\b');
                }
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
        SCREEN.printImage(img);
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
        charTyped = 0;
        
        // Wait until a character is typed
        synchronized (INPUT_LOCK) {
            try {
                INPUT_LOCK.wait();
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        }
        
        // Print the character
        if (printChar && charTyped != 0) {
            print(charTyped);
        }
        
        return charTyped;
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
     * @param charToPrint the character to print in place of the typed
     *                    characters; use 0 ({@code NUL}) to indicate that
     *                    characters should be printed exactly as they're typed
     * @return the string typed
     */
    public static String readLine(char charToPrint)
    {
        char c;
        char[] buf;
        int pointer;
        boolean printDifferentChar;
        boolean isReadingInput;
        String input;
        int previousInputIndex = 0;
        Iterator<String> it;
        
        pointer = 0;
        printDifferentChar = charToPrint != 0;
        isReadingInput = true;
        buf = new char[INPUT_BUFFER_LENGTH];
        
        // Loop until <enter> is pressed
        do {
            // Get character
            c = getChar(false);
            
            // Handle arrow keys
            // TODO: ugly code, clean upls
            if (c == VK_UP_CHAR) {
                if (previousInputIndex + 1 > PREVIOUS_INPUT_QUEUE.getItemCount()) {
                    continue;
                }
                previousInputIndex++;
                System.out.println(previousInputIndex);
                it = PREVIOUS_INPUT_QUEUE.iterator();
                String previousInput = null;
                int i = 0;
                while (it.hasNext() && i < PREVIOUS_INPUT_QUEUE.getItemCount() + 1 - previousInputIndex) {
                    previousInput = it.next();
                    i++;
                }
                for (i = 0; i < buf.length; i++) {
                    if (buf[i] == 0) {
                        break;
                    }
                    print('\b');
                }
                buf = new char[INPUT_BUFFER_LENGTH];
                pointer = 0;
                for (i = 0; i < previousInput.length(); i++) {
                    buf[i] = previousInput.charAt(i);
                    pointer++;
                }
                print(previousInput);
                continue;
            } else if (c == VK_DOWN_CHAR) {
                if (previousInputIndex - 1 < 0) {
                    continue;
                }
                previousInputIndex--;
                boolean wrapAround = false;
                if (previousInputIndex == 0) {
                    previousInputIndex = PREVIOUS_INPUT_QUEUE.getItemCount();
                    wrapAround = true;
                }
                System.out.println(previousInputIndex);
                it = PREVIOUS_INPUT_QUEUE.iterator();
                String previousInput = null;
                int i = 0;
                while (it.hasNext() && i < PREVIOUS_INPUT_QUEUE.getItemCount() + 1 - previousInputIndex) {
                    previousInput = it.next();
                    i++;
                }
                pointer = 0;
                for (i = 0; i < buf.length; i++) {
                    if (buf[i] == 0) {
                        break;
                    }
                    print('\b');
                }
                buf = new char[INPUT_BUFFER_LENGTH];
                for (i = 0; i < previousInput.length(); i++) {
                    buf[i] = previousInput.charAt(i);
                    pointer++;
                }
                print(previousInput);
                if (wrapAround) {
                    previousInputIndex = 0;
                }
                continue;
            }
            
            // Print character
            if (!printDifferentChar) {
                charToPrint = c;
            }
            
            // Handle control characters
            switch (c) {
                case '\n':  // Newline (<enter>)
                    isReadingInput = false;
                    println();
                    break;
                case '\b':  // Backspace
                    if (pointer > 0) {
                        buf[--pointer] = 0;
                        print('\b');
                    }
                    break;
                default:
                    if (pointer < buf.length) {
                        buf[pointer++] = c;
                        print(charToPrint);
                    }
            }
        } while (isReadingInput);
        
        input = new String(buf).trim();  // Trim to remove extra null characters
        
        if (PREVIOUS_INPUT_QUEUE.isFull()) {
            PREVIOUS_INPUT_QUEUE.remove();
        }
        PREVIOUS_INPUT_QUEUE.insert(input);
        
        return input;
    }
    
    /**
     * Displays the terminal window.
     */
    public static void show()
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                JComponent screenComponent = SCREEN.getComponent();
                FRAME.add(screenComponent);
                FRAME.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                FRAME.setResizable(false);
                FRAME.setSize(screenComponent.getPreferredSize());
                FRAME.setLocationRelativeTo(null);  // Center frame on screen
                FRAME.setVisible(true);
            }
        });
        
        for (int i = 0; i < LINES; i++) {
            Terminal.println();
        }
        Terminal.println(motd);
    }
    
    /*
     * Registers all of the keystrokes that can be used with the terminal.
     */
    private static void registerKeys()
    {
        // Lowercase letters
        registerKey('a', KeyEvent.VK_A, 0);
        registerKey('b', KeyEvent.VK_B, 0);
        registerKey('c', KeyEvent.VK_C, 0);
        registerKey('d', KeyEvent.VK_D, 0);
        registerKey('e', KeyEvent.VK_E, 0);
        registerKey('f', KeyEvent.VK_F, 0);
        registerKey('g', KeyEvent.VK_G, 0);
        registerKey('h', KeyEvent.VK_H, 0);
        registerKey('i', KeyEvent.VK_I, 0);
        registerKey('j', KeyEvent.VK_J, 0);
        registerKey('k', KeyEvent.VK_K, 0);
        registerKey('l', KeyEvent.VK_L, 0);
        registerKey('m', KeyEvent.VK_M, 0);
        registerKey('n', KeyEvent.VK_N, 0);
        registerKey('o', KeyEvent.VK_O, 0);
        registerKey('p', KeyEvent.VK_P, 0);
        registerKey('q', KeyEvent.VK_Q, 0);
        registerKey('r', KeyEvent.VK_R, 0);
        registerKey('s', KeyEvent.VK_S, 0);
        registerKey('t', KeyEvent.VK_T, 0);
        registerKey('u', KeyEvent.VK_U, 0);
        registerKey('v', KeyEvent.VK_V, 0);
        registerKey('w', KeyEvent.VK_W, 0);
        registerKey('x', KeyEvent.VK_X, 0);
        registerKey('y', KeyEvent.VK_Y, 0);
        registerKey('z', KeyEvent.VK_Z, 0);
        
        // Uppercase letters
        registerKey('A', KeyEvent.VK_A, KeyEvent.SHIFT_DOWN_MASK);
        registerKey('B', KeyEvent.VK_B, KeyEvent.SHIFT_DOWN_MASK);
        registerKey('C', KeyEvent.VK_C, KeyEvent.SHIFT_DOWN_MASK);
        registerKey('D', KeyEvent.VK_D, KeyEvent.SHIFT_DOWN_MASK);
        registerKey('E', KeyEvent.VK_E, KeyEvent.SHIFT_DOWN_MASK);
        registerKey('F', KeyEvent.VK_F, KeyEvent.SHIFT_DOWN_MASK);
        registerKey('G', KeyEvent.VK_G, KeyEvent.SHIFT_DOWN_MASK);
        registerKey('H', KeyEvent.VK_H, KeyEvent.SHIFT_DOWN_MASK);
        registerKey('I', KeyEvent.VK_I, KeyEvent.SHIFT_DOWN_MASK);
        registerKey('J', KeyEvent.VK_J, KeyEvent.SHIFT_DOWN_MASK);
        registerKey('K', KeyEvent.VK_K, KeyEvent.SHIFT_DOWN_MASK);
        registerKey('L', KeyEvent.VK_L, KeyEvent.SHIFT_DOWN_MASK);
        registerKey('M', KeyEvent.VK_M, KeyEvent.SHIFT_DOWN_MASK);
        registerKey('N', KeyEvent.VK_N, KeyEvent.SHIFT_DOWN_MASK);
        registerKey('O', KeyEvent.VK_O, KeyEvent.SHIFT_DOWN_MASK);
        registerKey('P', KeyEvent.VK_P, KeyEvent.SHIFT_DOWN_MASK);
        registerKey('Q', KeyEvent.VK_Q, KeyEvent.SHIFT_DOWN_MASK);
        registerKey('R', KeyEvent.VK_R, KeyEvent.SHIFT_DOWN_MASK);
        registerKey('S', KeyEvent.VK_S, KeyEvent.SHIFT_DOWN_MASK);
        registerKey('T', KeyEvent.VK_T, KeyEvent.SHIFT_DOWN_MASK);
        registerKey('U', KeyEvent.VK_U, KeyEvent.SHIFT_DOWN_MASK);
        registerKey('V', KeyEvent.VK_V, KeyEvent.SHIFT_DOWN_MASK);
        registerKey('W', KeyEvent.VK_W, KeyEvent.SHIFT_DOWN_MASK);
        registerKey('X', KeyEvent.VK_X, KeyEvent.SHIFT_DOWN_MASK);
        registerKey('Y', KeyEvent.VK_Y, KeyEvent.SHIFT_DOWN_MASK);
        registerKey('Z', KeyEvent.VK_Z, KeyEvent.SHIFT_DOWN_MASK);
        
        // Numbers
        registerKey('0', KeyEvent.VK_0, 0);
        registerKey('1', KeyEvent.VK_1, 0);
        registerKey('2', KeyEvent.VK_2, 0);
        registerKey('3', KeyEvent.VK_3, 0);
        registerKey('4', KeyEvent.VK_4, 0);
        registerKey('5', KeyEvent.VK_5, 0);
        registerKey('6', KeyEvent.VK_6, 0);
        registerKey('7', KeyEvent.VK_7, 0);
        registerKey('8', KeyEvent.VK_8, 0);
        registerKey('9', KeyEvent.VK_9, 0);
        registerKey('0', KeyEvent.VK_NUMPAD0, 0);
        registerKey('1', KeyEvent.VK_NUMPAD1, 0);
        registerKey('2', KeyEvent.VK_NUMPAD2, 0);
        registerKey('3', KeyEvent.VK_NUMPAD3, 0);
        registerKey('4', KeyEvent.VK_NUMPAD4, 0);
        registerKey('5', KeyEvent.VK_NUMPAD5, 0);
        registerKey('6', KeyEvent.VK_NUMPAD6, 0);
        registerKey('7', KeyEvent.VK_NUMPAD7, 0);
        registerKey('8', KeyEvent.VK_NUMPAD8, 0);
        registerKey('9', KeyEvent.VK_NUMPAD9, 0);
        
        // Symbols
        registerKey('-', KeyEvent.VK_MINUS, 0);
        registerKey('=', KeyEvent.VK_EQUALS, 0);
        registerKey('[', KeyEvent.VK_OPEN_BRACKET, 0);
        registerKey(']', KeyEvent.VK_CLOSE_BRACKET, 0);
        registerKey('\\', KeyEvent.VK_BACK_SLASH, 0);
        registerKey(';', KeyEvent.VK_SEMICOLON, 0);
        registerKey('\'', KeyEvent.VK_QUOTE, 0);
        registerKey(',', KeyEvent.VK_COMMA, 0);
        registerKey('.', KeyEvent.VK_PERIOD, 0);
        registerKey('/', KeyEvent.VK_SLASH, 0);
        registerKey('/', KeyEvent.VK_DIVIDE, 0);
        registerKey('*', KeyEvent.VK_MULTIPLY, 0);
        registerKey('-', KeyEvent.VK_SUBTRACT, 0);
        registerKey('+', KeyEvent.VK_ADD, 0);
        registerKey('.', KeyEvent.VK_DECIMAL, 0);
        registerKey('!', KeyEvent.VK_1, KeyEvent.SHIFT_DOWN_MASK);
        registerKey('@', KeyEvent.VK_2, KeyEvent.SHIFT_DOWN_MASK);
        registerKey('#', KeyEvent.VK_3, KeyEvent.SHIFT_DOWN_MASK);
        registerKey('$', KeyEvent.VK_4, KeyEvent.SHIFT_DOWN_MASK);
        registerKey('%', KeyEvent.VK_5, KeyEvent.SHIFT_DOWN_MASK);
        registerKey('^', KeyEvent.VK_6, KeyEvent.SHIFT_DOWN_MASK);
        registerKey('&', KeyEvent.VK_7, KeyEvent.SHIFT_DOWN_MASK);
        registerKey('*', KeyEvent.VK_8, KeyEvent.SHIFT_DOWN_MASK);
        registerKey('(', KeyEvent.VK_9, KeyEvent.SHIFT_DOWN_MASK);
        registerKey(')', KeyEvent.VK_0, KeyEvent.SHIFT_DOWN_MASK);
        registerKey('_', KeyEvent.VK_MINUS, KeyEvent.SHIFT_DOWN_MASK);
        registerKey('+', KeyEvent.VK_EQUALS, KeyEvent.SHIFT_DOWN_MASK);
        registerKey('{', KeyEvent.VK_OPEN_BRACKET, KeyEvent.SHIFT_DOWN_MASK);
        registerKey('}', KeyEvent.VK_CLOSE_BRACKET, KeyEvent.SHIFT_DOWN_MASK);
        registerKey('|', KeyEvent.VK_BACK_SLASH, KeyEvent.SHIFT_DOWN_MASK);
        registerKey(':', KeyEvent.VK_SEMICOLON, KeyEvent.SHIFT_DOWN_MASK);
        registerKey('"', KeyEvent.VK_QUOTE, KeyEvent.SHIFT_DOWN_MASK);
        registerKey('<', KeyEvent.VK_COMMA, KeyEvent.SHIFT_DOWN_MASK);
        registerKey('>', KeyEvent.VK_PERIOD, KeyEvent.SHIFT_DOWN_MASK);
        registerKey('?', KeyEvent.VK_SLASH, KeyEvent.SHIFT_DOWN_MASK);
        registerKey('/', KeyEvent.VK_DIVIDE, KeyEvent.SHIFT_DOWN_MASK);
        registerKey('*', KeyEvent.VK_MULTIPLY, KeyEvent.SHIFT_DOWN_MASK);
        registerKey('-', KeyEvent.VK_SUBTRACT, KeyEvent.SHIFT_DOWN_MASK);
        registerKey('+', KeyEvent.VK_ADD, KeyEvent.SHIFT_DOWN_MASK);
        
        // Control characters
        registerKey('\b', KeyEvent.VK_BACK_SPACE, 0);
        registerKey('\b', KeyEvent.VK_BACK_SPACE, KeyEvent.SHIFT_DOWN_MASK);
        registerKey('\n', KeyEvent.VK_ENTER, 0);
        registerKey('\n', KeyEvent.VK_ENTER, KeyEvent.SHIFT_DOWN_MASK);
        registerKey(' ', KeyEvent.VK_SPACE, 0);
        registerKey(' ', KeyEvent.VK_SPACE, KeyEvent.SHIFT_DOWN_MASK);
        registerKey(VK_UP_CHAR, KeyEvent.VK_UP, 0);
        registerKey(VK_DOWN_CHAR, KeyEvent.VK_DOWN, 0);
    }
    
    /*
     * Registers a keystroke.
     */
    private static void registerKey(final char ch, int keyChar, int modifiers)
    {
        AbstractAction keyAction = new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                synchronized (INPUT_LOCK) {
                    charTyped = ch;
                    INPUT_LOCK.notify();
                }
            }
        };
        
        INPUT_MAP.put(KeyStroke.getKeyStroke(keyChar, modifiers), ch);
        ACTION_MAP.put(ch, keyAction);
    }
}
