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
import javax.swing.JComponent;

/**
 * Created on Nov 17, 2015.
 *
 * @author thehambone <thehambone93@gmail.com>
 */
public class Shell extends JComponent
{
    private static final Font FONT = new Font("Monospaced", Font.PLAIN, 13);
    
    private static final int COLUMNS = 80;
    private static final int ROWS = 27;
    
    private static final int TEXT_VERTICAL_OFFSET = -2;
    
    private static final String DEFAULT_PROMPT = "$";
    
    private int charWidth;
    private int charHeight;
    private int cursorX;
    
    private String screenBuffer;
    private String inputBuffer;
    private String prompt;
    
    public Shell()
    {
        super();
        
        charWidth = 0;
        charHeight = 0;
        cursorX = 0;
        screenBuffer = "";
        inputBuffer = "";
        prompt = DEFAULT_PROMPT;
    }
    
    public Dimension calculateWindowSize()
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
                - charHeight - TEXT_VERTICAL_OFFSET;
        
        return dim;
    }
    
    public void print(char c)
    {
        cursorX++;
        if (cursorX > COLUMNS) {
            screenBuffer += "\n" + Character.toString(c);
            cursorX = 1;
            repaint();
            return;
        }
        
        if (c == '\n') {
            cursorX = 0;
        }
        
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
        int y = 0;
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
                        (y + 1) * charHeight + TEXT_VERTICAL_OFFSET);
                x++;
            }
        }
    }
}