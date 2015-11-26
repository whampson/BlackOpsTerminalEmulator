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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.Timer;

/**
 * Created on Nov 17, 2015.
 *
 * @author thehambone <thehambone93@gmail.com>
 * 
 * @deprecated this class will soon be removed
 */
public class Shell extends JComponent
{
    private static final Font FONT = new Font("Monospaced", Font.PLAIN, 13);
    
    private static final int COLUMNS = 80;
    private static final int ROWS = 27;
    private static final int CURSOR_BLINK_RATE = 275;
    private static final int MAX_INPUT_BUFFER_LENGTH = 81;
    private static final int VERTICAL_TEXT_OFFSET = -2;
    private static final String DEFAULT_PROMPT = "$";
    
    private final Dimension windowSize;
    
    private volatile boolean isCursorVisible;
    
    private int charWidth;
    private int charHeight;
    private int cursorX;
    private int cursorY;
    
    private String screenBuffer;
    private String inputBuffer;
    private String prompt;
    
    public Shell()
    {
        super();
        
        isCursorVisible = false;
        charWidth = 0;
        charHeight = 0;
        cursorX = 0;
        cursorY = 1;
        screenBuffer = "";
        inputBuffer = "";
        prompt = DEFAULT_PROMPT;
        
        windowSize = calculateWindowSize();
        
        initializeKeyboardInput();
        blinkCursor();
    }
    
    public Dimension getWindowSize()
    {
        return windowSize;
    }
    
    private Dimension calculateWindowSize()
    {
        Dimension dim = new Dimension();
        FontMetrics fm = getFontMetrics(FONT);
        String testString = "";
        
        // Set character dimensions
        charWidth = fm.getWidths()[1];
        charHeight = fm.getAscent();
        
        // Fill test string with spaces as wide as the screen width
        // We want 1 extra space to accomodate for the cursor
        for (int i = 0; i < COLUMNS + 1; i++) {
            testString += " ";
        }
        
        // TODO: develop a way to accurately measure height from text
        // As of now, height is only accurate for Monospaced 13pt font
        dim.width = (fm.stringWidth(testString) -  charWidth)
                + (charWidth * 2) - 2;
        dim.height = charHeight * (ROWS + 3)
                - charHeight - VERTICAL_TEXT_OFFSET;
        
        return dim;
    }
    
    public void print(char c)
    {
//        cursorX++;
//        if (cursorX > COLUMNS) {
//            screenBuffer += "\n";
//            cursorX = 1;
//            cursorY++;
//        } else if (c == '\n') {
//            cursorX = 0;
//            cursorY++;
//        } else if (c == '\b') {
//            cursorX -= 2;
//            if (screenBuffer.charAt(screenBuffer.length() - 1) == '\n') {
//                screenBuffer = screenBuffer.substring(0, screenBuffer.length() - 2);
//            } else {
//                screenBuffer = screenBuffer.substring(0, screenBuffer.length() - 1);
//            }
//            repaint();
//            return;
//        }
//        
//        screenBuffer += Character.toString(c);
//        repaint();
        
        if (c == '\n') {
            cursorX = 0;
            cursorY++;
            print0(c);
            return;
        }
        if (c == '\b') {
            if (cursorX > 0) {
                cursorX--;
            }
            if (screenBuffer.charAt(screenBuffer.length() - 1) == '\n') {
                screenBuffer = screenBuffer.substring(0, screenBuffer.length() - 2);
            } else {
                screenBuffer = screenBuffer.substring(0, screenBuffer.length() - 1);
            }
            repaint();
            return;
        }
        
        cursorX++;
        if (cursorX > COLUMNS) {
            screenBuffer += "\n";
            cursorX = 1;
            cursorY++;
        }
        print0(c);
    }
    
    private void print0(char c)
    {
        screenBuffer += Character.toString(c);
        repaint();
    }
    
    public void print(String s)
    {
        for (int i = 0; i < s.length(); i++) {
            print(s.charAt(i));
        }
    }
    
    public void println()
    {
        print("\n");
    }
    
    public void println(String s)
    {
        print(s + "\n");
    }
    
    private void initializeKeyboardInput()
    {
        for (int i = 0; i < 26; i++) {
            registerKey(KeyStroke.getKeyStroke(KeyEvent.VK_A + i, 0),
                    (char) (KeyEvent.VK_A + i + 0x20));
        }
        
        for (int i = 0; i < 26; i++) {
            registerKey(KeyStroke.getKeyStroke(KeyEvent.VK_A + i, KeyEvent.SHIFT_DOWN_MASK),
                    (char) (KeyEvent.VK_A + i));
        }
        
        registerKey(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), ' ');
        
        getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "enter");
        getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0), "backspace");
        
        AbstractAction enter = new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                println();
                System.out.println("parsing '" + inputBuffer + "'...");
                inputBuffer = "";
                print(prompt);
            }
        };
        AbstractAction backspace = new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if (inputBuffer.isEmpty()) {
                    return;
                }
                inputBuffer = inputBuffer.substring(0, inputBuffer.length() - 1);
                print('\b');
            }
        };
        getActionMap().put("enter", enter);
        getActionMap().put("backspace", backspace);
    }
    
    private void registerKey(KeyStroke keyStroke, final char ch)
    {
        final String s = Character.toString(ch); 
        AbstractAction action = new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if (inputBuffer.length() < MAX_INPUT_BUFFER_LENGTH) {
                    inputBuffer += s;
                    print(ch);
                }
            }
        };
        getInputMap().put(keyStroke, s);
        getActionMap().put(s, action);
    }
    
    private void blinkCursor()
    {
        ActionListener cursorBlink = new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                isCursorVisible = !isCursorVisible;
                repaint();
            }
        };
        
        Timer blinkTimer = new Timer(CURSOR_BLINK_RATE, cursorBlink);
        blinkTimer.start();
    }
    
    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        
        Graphics2D g2d = (Graphics2D) g;
        
        // Draw background
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        
        g2d.setFont(FONT);
        g2d.setColor(Color.WHITE);
        int x = 0;
        int y = 1;
        String s;
        char c;
        
        // Draw text line by line
        for (int i = 0; i < screenBuffer.length(); i++) {
            c = screenBuffer.charAt(i);
            if (c == '\n') {
                x = 0;
                y++;
            } else {
                s = Character.toString(c);
                g2d.drawString(s,
                        x * charWidth,
                        y * charHeight + VERTICAL_TEXT_OFFSET);
                x++;
            }
        }
        
        // Draw cursor
        if (isCursorVisible) {
            g2d.drawLine(cursorX * charWidth, cursorY * charHeight,
                    (cursorX + 1) * charWidth, cursorY * charHeight);
        }
    }
}