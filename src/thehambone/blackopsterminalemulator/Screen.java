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
import java.awt.image.BufferedImage;
import javax.swing.JComponent;
import javax.swing.Timer;

/**
 * Created on Nov 18, 2015.
 *
 * @author thehambone <thehambone93@gmail.com>
 */
public final class Screen
{
    private static final int TEXT_HORIZONTAL_OFFSET = 0;
    private static final int TEXT_VERTICAL_OFFSET = -2;
    
    private static final int IMAGE_HORIZONTAL_OFFSET = 0;
    private static final int IMAGE_VERTICAL_OFFSET = 2;
    
    private final ScreenBuffer screenBuffer;
    
    private final int columns;
    private final int rows;
    
    private Color background;
    private Color foreground;
    
    private final Font font;
    private int charWidth;
    private int charHeight;
    
    private final int cursorBlinkRate;
    private boolean isCursorVisible;
    
    private JComponent component;
    
    public Screen(int columns, int rows,
            ScreenColor background, ScreenColor foreground,
            Font font, int cursorBlinkRate)
    {
        this.columns = columns;
        this.rows = rows;
        
        this.background = background.getColor();
        this.foreground = foreground.getColor();
        
        this.font = font;
        charWidth = 0;
        charHeight = 0;
        
        this.cursorBlinkRate = cursorBlinkRate;
        isCursorVisible = true;
        
        initComponent();
        
        screenBuffer = new ScreenBuffer(columns, rows, charWidth, charHeight);
        
        blinkCursor();
    }
    
    public JComponent getComponent()
    {
        return component;
    }
    
    public Color getBackground()
    {
        return background;
    }
    
    public void setBackground(ScreenColor background)
    {
        this.background = background.getColor();
    }
    
    public Color getForeground()
    {
        return foreground;
    }
    
    public void setForeground(ScreenColor foreground)
    {
        this.foreground = foreground.getColor();
    }
    
    public void setCursorPosition(int x, int y)
    {
        screenBuffer.setCursorPosition(x, y);
        component.repaint();
    }
    
    public void print(char c)
    {
        screenBuffer.putChar(c);
        component.repaint();
    }
    
    public void printImage(BufferedImage image)
    {
        screenBuffer.putImage(image);
    }
    
    private void blinkCursor()
    {
        ActionListener cursorBlink = new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                isCursorVisible = !isCursorVisible;
                component.repaint();
            }
        };
        
        Timer blinkTimer = new Timer(cursorBlinkRate, cursorBlink);
        blinkTimer.start();
    }
    
    /*
     * Creates the screen component.
     */
    private void initComponent()
    {
        component = new JComponent()
        {
            @Override
            public void paintComponent(Graphics g)
            {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D)g;
                
                ScreenLine line;
                int x;
                int y;
                
                // Draw background
                g2d.setColor(background);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Draw foreground
                g2d.setColor(foreground);
                g2d.setFont(font);
                
                // Draw row by row
                for (y = 0; y < rows; y++) {
                    line = screenBuffer.getLine(y);
                    
                    // Draw image section
                    if (line.hasImageData()) {
                        g2d.drawImage(line.getImageData(), null,
                                0 * charWidth + IMAGE_HORIZONTAL_OFFSET,
                                y * charHeight + IMAGE_VERTICAL_OFFSET);
                        continue;
                    }
                    
                    // Draw text
                    for (x = 0; x < line.getLength(); x++) {
                        if (line.charAt(x) == 0) {
                            continue;
                        }
                        g2d.drawString(Character.toString(line.charAt(x)),
                                x * charWidth + TEXT_HORIZONTAL_OFFSET,
                                (y + 1) * charHeight + TEXT_VERTICAL_OFFSET);
                    }
                }
                
                // Draw cursor
                if (isCursorVisible) {
                    x = screenBuffer.getCursorX();
                    y = screenBuffer.getCursorY();
                    g2d.drawLine(x * charWidth, (y + 1) * charHeight,
                            (x + 1) * charWidth - 1, (y + 1) * charHeight);
                }
            }
        };
        component.setPreferredSize(calculateComponentSize());
    }
    
    /*
     * Determines the size of the screen component based on the screen font and
     * the number of rows and columns within the screen area.
     */
    private Dimension calculateComponentSize()
    {
        Dimension dim = new Dimension();
        FontMetrics fm = component.getFontMetrics(font);
        String testString = "";
        
        // Set character dimensions
        charWidth = fm.getWidths()[1];
        charHeight = fm.getAscent();
        
        /* Fill test string with as many spaces as the screen will show in one
           line. We want 1 extra space to accomodate for the cursor. */
        for (int i = 0; i < columns + 1; i++) {
            testString += " ";
        }
        
        // TODO: develop a way to accurately measure height from text
        // As of now, height is only accurate for Monospaced 13pt font
        dim.width = (fm.stringWidth(testString) - charWidth)
                + (charWidth * 2) - 2;
        dim.height = charHeight * (rows + 3)
                - charHeight - TEXT_VERTICAL_OFFSET;
        
        return dim;
    }
    
    /**
     * Defines all of the available screen colors.
     */
    public static enum ScreenColor
    {
        BLACK(0, 0, 0),
        RED(255, 0, 0),
        GREEN(0, 255, 0),
        YELLOW(255, 255, 0),
        BLUE(0, 0, 255),
        CYAN(0, 255, 255),
        MAGENTA(255, 0, 255),
        WHITE(255, 255, 255),
        DARK_CYAN_GRAY(95, 127, 127),
        DARK_YELLOW(127, 127, 0);
        
        private final int r;
        private final int g;
        private final int b;
        
        private ScreenColor(int r, int g, int b)
        {
            this.r = r;
            this.g = g;
            this.b = b;
        }
        
        public Color getColor()
        {
            return new Color(r, g, b);
        }
    }
}