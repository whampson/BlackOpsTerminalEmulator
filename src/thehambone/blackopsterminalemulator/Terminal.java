/*
 * The MIT License
 *
 * Copyright 2015-2016 thehambone <thehambone93@gmail.com>.
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
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.TimeUnit;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import thehambone.blackopsterminalemulator.filesystem.FileSystem;
import thehambone.blackopsterminalemulator.filesystem.FileSystemObject;
import thehambone.blackopsterminalemulator.filesystem.PrintableFile;
import thehambone.blackopsterminalemulator.io.ResourceLoader;
import thehambone.blackopsterminalemulator.util.Debuggable;
import thehambone.blackopsterminalemulator.util.FixedLengthQueue;
/**
 * Created on Nov 18, 2015.
 *
 * @author thehambone <thehambone93@gmail.com>
 */
public final class Terminal implements Debuggable
{
    public static final int COLUMNS = 80;
    public static final int LINES = 27;
    
    private static final int INPUT_BUFFER_LENGTH = 81;
    private static char[] INPUT_BUFFER = new char[INPUT_BUFFER_LENGTH];
    
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
    private final List<Server> servers;
    
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
        
        servers = new ArrayList<>();
        
        charTyped = 0;
        
        motd = "";
        
        initFrameIcon();
        initMenuBar();
        registerInputKeys();
        initWindowClosePrompt();
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
                frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
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
     * Adds a server to the list of available servers.
     * 
     * @param s the server to add
     */
    public static void addServer(Server s)
    {
        TERMINAL_INSTANCE.servers.add(s);
    }
    
    /**
     * Gets a server from the list of available servers by name.
     * 
     * @param name the name of the server to be retrieved
     * @return the desired server if found, {@code null} if the server is not
     *         found
     */
    public static Server getServer(String name)
    {
        Server server = null;
        
        for (Server s : TERMINAL_INSTANCE.servers) {
            if (s.getName().equalsIgnoreCase(name)) {
                server = s;
                break;
            }
        }
        
        return server;
    }
    
    /**
     * Gets the screen associated with the current Terminal instance.
     * 
     * @return the terminal screen
     */
    public static Screen getScreen()
    {
        return TERMINAL_INSTANCE.screen;
    }
    
    /**
     * Checks whether the maximum number of active login shells has been
     * reached.
     * 
     * @return {@code true} if the limit has been reached, {@code false}
     *         otherwise
     */
    public static boolean maxLoginShellsReached()
    {
        return TERMINAL_INSTANCE.activeShells.size() == MAX_LOGIN_SHELLS;
    }
    
    /**
     * Gets the LoginShell instance currently in use. The active login shell is
     * located at the top of the login shell stack.
     * 
     * @return the LoginShell instance currently in use
     */
    public static LoginShell getActiveLoginShell()
    {
        return TERMINAL_INSTANCE.activeShells.peek();
    }
    
    /**
     * Pushes a login shell to the top of the login shell stack and marks it as
     * the active shell.
     * 
     * @param shell the shell to be added to the login shell stack
     */
    public static void pushLoginShell(LoginShell shell)
    {
        TERMINAL_INSTANCE.activeShells.push(shell);
    }
    
    /**
     * Removes the top login shell from the login shell stack. The shell
     * immediately below the popped shell will become the new active shell.
     * 
     * @return the popped login shell
     */
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
     * <p>
     * If more than 22 lines are printed on the screen by the same string, the
     * output will be stopped until a key is pressed and "--MORE--" will be
     * printed on screen. This creates a screen paging effect and its purpose is
     * to allow the user to read the output one screenful at a time.
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
        int bufPointer;
        boolean printDifferentChar;
        boolean isReadingInput;
        int historyIndex;
        String input;
        FixedLengthQueue<String> inputHistory;
        
        INPUT_BUFFER = new char[INPUT_BUFFER_LENGTH];
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
                        INPUT_BUFFER[--bufPointer] = 0;
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
                    for (int i = 0; i < INPUT_BUFFER_LENGTH; i++) {
                        if (INPUT_BUFFER[i] == 0) {
                            break;
                        }
                        print('\b');
                    }
                    
                    // Reset the input buffer
                    bufPointer = 0;
                    INPUT_BUFFER = new char[INPUT_BUFFER_LENGTH];
                    
                    // Add the retrieved string to input buffer and print
                    for (int i = 0; i < input.length(); i++) {
                        INPUT_BUFFER[i] = input.charAt(i);
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
                    for (int i = 0; i < INPUT_BUFFER_LENGTH; i++) {
                        if (INPUT_BUFFER[i] == 0) {
                            break;
                        }
                        print('\b');
                    }
                    
                    // Reset the input buffer
                    bufPointer = 0;
                    INPUT_BUFFER = new char[INPUT_BUFFER_LENGTH];
                    
                    // Add the retrieved string to input buffer and print
                    for (int i = 0; i < input.length(); i++) {
                        INPUT_BUFFER[i] = input.charAt(i);
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
                    if (bufPointer < INPUT_BUFFER_LENGTH) {
                        INPUT_BUFFER[bufPointer++] = c;
                        print(charToPrint);
                    }
            }
        } while (isReadingInput);
        
        
        // Create string from input buffer
        // Trim to remove extra null characters
        input = new String(INPUT_BUFFER).trim();
        
        // Add input string to the input history queue
        if (inputHistory.isFull()) {
            inputHistory.remove();
        }
        inputHistory.insert(input);
        
        return input;
    }
    
    /**
     * Writes debug info to a data stream.
     * 
     * @param pw data stream
     */
    public static void printCurrentState(PrintWriter pw)
    {
        TERMINAL_INSTANCE.printDebugInfo(pw);
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
     * Loads and sets the program icon.
     */
    private void initFrameIcon()
    {
        frame.setIconImage(
                ResourceLoader.loadEmbeddedImage("res/logosmall.png"));
    }
    
    /*
     * Initializes the menu bar.
     */
    private void initMenuBar()
    {
        JMenuBar menuBar = new JMenuBar();
        
        JMenu fileMenu = new JMenu("File");
        JMenu helpMenu = new JMenu("Help");
        
        JMenuItem fileExitMenuItem = new JMenuItem("Exit");
        JMenuItem helpAboutMenuItem = new JMenuItem("About");
        
        fileMenu.setMnemonic('F');
        fileExitMenuItem.setMnemonic('x');
        helpMenu.setMnemonic('H');
        helpAboutMenuItem.setMnemonic('A');
        
        fileMenu.add(fileExitMenuItem);
        helpMenu.add(helpAboutMenuItem);
        
        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
        
        frame.setJMenuBar(menuBar);
        
        fileExitMenuItem.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                // Attempt to close window
                frame.dispatchEvent(
                        new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
            }
        });
        
        helpAboutMenuItem.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                // Show About dialog
                AboutDialog ad = new AboutDialog(frame, true);
                ad.setLocationRelativeTo(frame);
                ad.setVisible(true);
            }
        });
    }
    
    /*
     * Sets up the close program confirmation dialog.
     */
    private void initWindowClosePrompt()
    {
        frame.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent evt)
            {
                int option = JOptionPane.showConfirmDialog(frame,
                        "Are you sure you want to exit?\n\n"
                                + "Your current session will be lost.",
                        "Confirm Exit",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
                
                // "Yes" selected
                if (option == JOptionPane.YES_OPTION) {
                    frame.dispose();
                    System.exit(0);
                }
            }
        });
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
        
        registerESCKey();
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
     * Maps the escape key and defines its action.
     */
    private void registerESCKey()
    {
        AbstractAction keyAction = new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                // Attempt to close window
                frame.dispatchEvent(
                        new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
            }
        };
        
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "ESC");
        actionMap.put("ESC", keyAction);
    }
    
    /*
     * Checks whether caps lock is enabled on the keyboard.
     */
    private boolean isCapsLockEnabled()
    {
        Toolkit tk = Toolkit.getDefaultToolkit();
        return tk.getLockingKeyState(KeyEvent.VK_CAPS_LOCK);
    }
    
    @Override
    public void printDebugInfo(PrintWriter pw)
    {
        screen.getScreenBuffer().printDebugInfo(pw);
        
        pw.println("Input Buffer");
        pw.println("------------");
        for (int i = 0; i < INPUT_BUFFER_LENGTH; i++) {
            if (INPUT_BUFFER[i] == 0) {
                break;
            }
            pw.print(INPUT_BUFFER[i]);
        }
        pw.println();
        pw.println();

        pw.println("Input History");
        pw.println("-------------");
        for (String s : inputHistory) {
            pw.println(s);
        }
        pw.println();
        
        pw.println("MOTD");
        pw.println("----");
        if (!motd.isEmpty()) {
            pw.println(motd);
        }
        pw.println();
        
        pw.println("Server Info");
        pw.println("-----------");
        for (Server s : servers) {
            pw.println("Server: " + s.getName());
            for (UserAccount u : s.getUsers()) {
                pw.println("    User: " + u.getUsername());
                for (Mail m : u.getMailbox().getAllMail()) {
                    pw.println("        " + m);
                }
            }
        }
        pw.println();
        
        pw.println("Server Filesystems");
        pw.println("------------------");
        for (Server s : servers) {
            pw.println("Server: " + s.getName());
            
            FileSystem fs = s.getFileSystem();
            if (fs == null) {
                continue;
            }
            
            Iterator<FileSystemObject> it = fs.iterator();
            
            FileSystemObject obj;
            boolean printResource;
            while (it.hasNext()) {
                obj = it.next();
                if (obj == null) {
                    continue;
                }
                
                printResource = obj instanceof PrintableFile;
                
                pw.printf("    %s { id = %d%s }",
                        obj.getPath(),
                        obj.getID(),
                        printResource
                                ? ", resource = "
                                        + ((PrintableFile)obj).getResourceName()
                                : "");
                pw.println();
            }
        }
        pw.println();
    }
}
