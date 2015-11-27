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
import java.util.concurrent.TimeUnit;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

/**
 * Created on Nov 18, 2015.
 *
 * @author thehambone <thehambone93@gmail.com>
 */
public final class Terminal
{
    private static final Font FONT = new Font("Courier New", 0, 13);
    
    private static final int COLUMNS = 80;
    private static final int LINES = 27;
    private static final int CURSOR_BLINK_RATE = 300;
    private static final int INPUT_BUFFER_LENGTH = 81;
    
    private static final Screen.ScreenColor BACKGROUND = Screen.ScreenColor.BLACK;
    private static final Screen.ScreenColor FOREGROUND = Screen.ScreenColor.WHITE;
    
    private static final JFrame FRAME = new JFrame();
    private static final Screen SCREEN = new Screen(COLUMNS, LINES, BACKGROUND, FOREGROUND, FONT, CURSOR_BLINK_RATE);
    
    private static final InputMap INPUT_MAP = new InputMap();
    private static final ActionMap ACTION_MAP = new ActionMap();
    
    private static final Object INPUT_LOCK = new Object();
    
    private static volatile char charTyped = 0;
    
    static
    {
        registerKeys();
        
        SCREEN.getComponent().setInputMap(JComponent.WHEN_FOCUSED, INPUT_MAP);
        SCREEN.getComponent().setActionMap(ACTION_MAP);
    }
    
    // Don't allow this class to be instantiated
    private Terminal() { }
    
    public static void setTitle(String title)
    {
        FRAME.setTitle(title);
    }
    
    public static void print(char c)
    {
        SCREEN.print(c);
    }
    
    public static void print(String s)
    {
        for (int i = 0; i < s.length(); i++) {            
            SCREEN.print(s.charAt(i));
            
            // Sleep 1ms to simulate output on a terminal with a low baud rate
            try {
                TimeUnit.MILLISECONDS.sleep(1);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
    
    public static void println()
    {
        println("");
    }
    
    public static void println(String s)
    {
        print(s + "\n");
    }
    
    public static void println(BufferedImage img)
    {
        SCREEN.printImage(img);
        println();
    }
    
    public static char getChar()
    {
        return getChar(true);
    }
    
    public static char getChar(boolean printChar)
    {
        
        charTyped = 0;
        
        synchronized (INPUT_LOCK) {
            try {
                INPUT_LOCK.wait();
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        }
        
        if (printChar) {
            print(charTyped);
        }
        
        return charTyped;
    }
    
    public static String readLine()
    {
        return readLine((char)0);
    }
    
    public static String readLine(char charToPrint)
    {
        char c;
        char[] buf;
        int pointer;
        boolean printDifferentChar;
        boolean isReadingInput;
        
        buf = new char[INPUT_BUFFER_LENGTH];
        pointer = 0;
        printDifferentChar = charToPrint != 0;
        isReadingInput = true;
        
        do {
            c = getChar(false);
            if (!printDifferentChar) {
                charToPrint = c;
            }
            switch (c) {
                case '\n':
                    isReadingInput = false;
                    println();
                    break;
                case '\b':
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
        
        return new String(buf);
    }
    
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
    }
    
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
        registerKey(' ', KeyEvent.VK_SPACE, 0);
        registerKey(' ', KeyEvent.VK_SPACE, KeyEvent.SHIFT_DOWN_MASK);
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
    }
    
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