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
 * This class represents the terminal screen.
 * <p>
 * Created on Nov 18, 2015.
 *
 * @author thehambone <thehambone93@gmail.com>
 */
public final class Screen
{
    private static final int TEXT_HORIZONTAL_OFFSET = 2;
    private static final int TEXT_VERTICAL_OFFSET = -2;
    
    private static final int IMAGE_HORIZONTAL_OFFSET = 3;
    private static final int IMAGE_VERTICAL_OFFSET = 4;
    
    private static final int PADDING_X = 2;
    private static final int PADDING_Y = 2;
    
    private final Object syncLock = new Object();
    
    private final ScreenBuffer screenBuffer;
    
    private final int columns;
    private final int lines;
    
    private Color background;
    private Color foreground;
    
    private final Font font;
    private int charWidth;
    private int charHeight;
    
    private final int cursorBlinkRate;
    private boolean isCursorVisible;
    
    private JComponent component;
    
    /**
     * Creates a new {@code Screen}.
     * 
     * @param columns the number of columns to be displayed by the screen;
     *                measured in characters
     * @param lines the number of lines to be displayed by the screen; measured
     *              in characters
     * @param background the background color
     * @param foreground the foreground (text) color
     * @param font the text font
     * @param cursorBlinkRate the rate at which the cursor will blink; measured
     *                        in milliseconds; 0 for no blinking
     */
    public Screen(int columns, int lines,
            ScreenColor background, ScreenColor foreground,
            Font font, int cursorBlinkRate)
    {
        this.columns = columns;
        this.lines = lines;
        
        this.background = background.getColor();
        this.foreground = foreground.getColor();
        
        this.font = font;
        charWidth = 0;
        charHeight = 0;
        
        this.cursorBlinkRate = cursorBlinkRate;
        isCursorVisible = true;
        
        initComponent();
        
        screenBuffer = new ScreenBuffer(columns, lines, charWidth, charHeight);
        
        blinkCursor();
    }
    
    /**
     * Gets the {@code ScreenBuffer} that contains all of the items displayed on
     * the screen.
     * 
     * @return the {@code ScreenBuffer} object associated with this screen
     */
    public ScreenBuffer getScreenBuffer()
    {
        return screenBuffer;
    }
    
    /**
     * Returns the {@code JComponent} on which the screen data is drawn.
     * 
     * @return the {@code JComponent} containing the screen data
     */
    public JComponent getComponent()
    {
        return component;
    }
    
    /**
     * Gets the background color.
     * 
     * @return the background color
     */
    public Color getBackground()
    {
        return background;
    }
    
    /**
     * Sets the background color. The color must be one of the predefined
     * {@code ScreenColor} constants.
     * 
     * @param background the color to be used as the background
     */
    public void setBackground(ScreenColor background)
    {
        this.background = background.getColor();
    }
    
    public Color getForeground()
    {
        return foreground;
    }
    
    /**
     * Sets the foreground color. The color must be one of the predefined
     * {@code ScreenColor} constants.
     * 
     * @param foreground the color to be used as the foreground
     */
    public void setForeground(ScreenColor foreground)
    {
        this.foreground = foreground.getColor();
    }
    
    /**
     * Draws a character on the screen after the previous screen item.
     * 
     * @param c the character to be drawn
     */
    public void print(char c)
    {
        screenBuffer.putChar(c);
        component.repaint();
    }
    
    /**
     * Draws an image on the screen after the previous screen item.
     * 
     * @param image the image to be drawn
     */
    public void printImage(BufferedImage image)
    {
        screenBuffer.putImage(image);
        component.repaint();
    }
    
    /*
     * Starts a timer thread that blinks the cursor at the specified rate.
     */
    private void blinkCursor()
    {
        // Don't blink if blink rate is 0
        if (cursorBlinkRate == 0) {
            return;
        }
        
        // Create an ActionListener that runs each time the blink timer is fired
        ActionListener cursorBlinkAction = new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                // Toggle isVisible boolean and repaint screen
                synchronized (syncLock) {
                    isCursorVisible = !isCursorVisible;
                    component.repaint();
                }
            }
        };
        
        /* Create a timer thread that fires the blink action at the rate of the
           cursor blink */
        Timer blinkTimer = new Timer(cursorBlinkRate, cursorBlinkAction);
        
        // Start timer
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
                
                // Draw background
                g2d.setColor(background);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Draw foreground
                g2d.setColor(foreground);
                g2d.setFont(font);
                
                ScreenItem item;
                BufferedImage img;
                char c;
                char c1;                
                int x;
                int y;
                int colorID;
                int colorCharOffset;
                
                x = 0;
                y = 0;
                colorCharOffset = 0;
                
                synchronized (syncLock) {
                    for (int i = 0; i < screenBuffer.getLength(); i++) {
                        item = screenBuffer.itemAt(i);
                        
                        // Draw image
                        if (item.hasImage()) {
                            img = item.getImage();
                            g2d.drawImage(img, null,
                                    0 * charWidth + IMAGE_HORIZONTAL_OFFSET,
                                    y * charHeight + IMAGE_VERTICAL_OFFSET);
                            y += screenBuffer.countImageLines(img);
                            continue;
                        }
                        
                        // Handle newlines
                        c = item.getCharacter();
                        if (c == '\n') {
                            x = 0;
                            y++;
                            g2d.setColor(foreground);
                            colorCharOffset = 0;
                            continue;
                        }
                        if (x > columns - 1) {
                            x = 0;
                            y++;
                            g2d.setColor(foreground);
                        }
                        
                        // Handle color
                        if (c == '^' && i != screenBuffer.getLength() - 1) {
                            c1 = screenBuffer.itemAt(i + 1).getCharacter();
                            colorID = c1 - 0x30; // Integer value of ASCII char

                            if (colorID >= 0 && colorID <= 9) {
                                for (ScreenColor sc : ScreenColor.values()) {
                                    if (colorID == sc.getID()) {
                                        g2d.setColor(sc.getColor());
                                        break;
                                    }
                                }
                                colorCharOffset += 2;
                                i++;
                                continue;
                            }
                        }
                        
                        // Draw character
                        g2d.drawString(Character.toString(c),
                                x * charWidth + TEXT_HORIZONTAL_OFFSET,
                                (y + 1) * charHeight + TEXT_VERTICAL_OFFSET);
                        x++;
                    }
                }
                
                // Draw cursor
                if (isCursorVisible) {
                    g2d.setColor(foreground);
                    x = screenBuffer.getCursorX() - colorCharOffset;
                    y = screenBuffer.getCursorY();
                    g2d.drawLine(x * charWidth + TEXT_HORIZONTAL_OFFSET + 1,
                            (y + 1) * charHeight - TEXT_VERTICAL_OFFSET,
                            (x + 1) * charWidth + TEXT_HORIZONTAL_OFFSET,
                            (y + 1) * charHeight - TEXT_VERTICAL_OFFSET);
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
        charHeight = fm.getHeight();
        
        /* Fill test string with as many spaces as the screen will show in one
           line. We want 1 extra space to accomodate for the cursor. */
        for (int i = 0; i < columns + 1; i++) {
            testString += " ";
        }
        
        // TODO: develop a way to accurately measure height from text
        // As of now, height is only accurate for Courier New 13pt font
        dim.width = (fm.stringWidth(testString) - charWidth)
                + (charWidth * 2) - 2 + (TEXT_HORIZONTAL_OFFSET * 2)
                + PADDING_X;
        dim.height = charHeight * (lines + 3)
                - charHeight - TEXT_VERTICAL_OFFSET + PADDING_Y;
        
        return dim;
    }
    
    /**
     * Defines all of the available screen colors.
     */
    public static enum ScreenColor
    {
        BLACK(0, 0, 0, 0),
        RED(1, 255, 0, 0),
        GREEN(2, 0, 255, 0),
        YELLOW(3, 255, 255, 0),
        BLUE(4, 0, 0, 255),
        CYAN(5, 0, 255, 255),
        MAGENTA(6, 255, 0, 255),
        WHITE(7, 255, 255, 255),
        DARK_CYAN_GRAY(8, 95, 127, 127),
        DARK_YELLOW(9, 127, 127, 0);
        
        private final int id;
        private final int r;
        private final int g;
        private final int b;
        
        /**
         * Creates a new {@code ScreenColor} constant.
         * 
         * @param id the color ID
         * @param r red value
         * @param g green value
         * @param b blue value
         */
        private ScreenColor(int id, int r, int g, int b)
        {
            this.id = id;
            this.r = r;
            this.g = g;
            this.b = b;
        }
        
        /**
         * Gets the color ID.
         * 
         * @return the color ID
         */
        public int getID()
        {
            return id;
        }
        
        /**
         * Gets this color as a {@link java.awt.Color} object containing the RGB
         * values.
         * 
         * @return the color as a {@link java.awt.Color} object
         */
        public Color getColor()
        {
            return new Color(r, g, b);
        }
    }
}