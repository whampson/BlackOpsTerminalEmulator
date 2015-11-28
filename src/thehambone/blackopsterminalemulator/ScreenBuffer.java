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

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * A {@code ScreenBuffer} contains all of the items currently visible on the
 * screen. It is also responsible for removing items once they go off-screen as
 * well as keeping track of the cursor position.
 * <p>
 * Created on Nov 18, 2015.
 *
 * @author thehambone <thehambone93@gmail.com>
 */
public final class ScreenBuffer
{
    private static final float IMAGE_SCALE_FACTOR = 0.66f;
    
    private static final int TAB_LENGTH = 4;
    
    private final int columns;
    private final int lines;
    private final int charWidth;
    private final int charHeight;
    private final List<ScreenItem> buf;
    
    private int cursorX;
    private int cursorY;
    
    /**
     * Creates a new {@code ScreenBuffer}.
     * 
     * @param columns the number of columns that are visible on the screen
     * @param lines the number of lines that are visible on the screen
     * @param charWidth the height of a character on the screen
     * @param charHeight the width of a character on the screen
     */
    public ScreenBuffer(int columns, int lines, int charWidth, int charHeight)
    {
        this.columns = columns;
        this.lines = lines;
        this.charWidth = charWidth;
        this.charHeight = charHeight;
        
        buf = new ArrayList<>();
        
        cursorX = 0;
        cursorY = 0;
    }
    
    /**
     * Returns the item at the specified index.
     * 
     * @param index the index of the desired item
     * @return the item at the specified index
     * @throws IndexOutOfBoundsException if the specified index is out of bounds
     */
    public ScreenItem itemAt(int index)
    {
        if (index < 0 || index > buf.size() - 1) {
            throw new IndexOutOfBoundsException(
                    "Index: " + index + ", Size" + buf.size());
        }
        
        return buf.get(index);
    }
    
    /**
     * Calculates the number of lines occupied by an image.
     * 
     * @param image the image to analyze
     * @return the number of lines occupied by the image
     */
    public int countImageLines(BufferedImage image)
    {
        return (int)Math.ceil((double)image.getHeight() / charHeight) - 1;
    }
    
    /**
     * Calculates the number of columns occupied by an image.
     * 
     * @param image the image to analyze
     * @return the number of columns occupied by the image
     */
    public int countImageColumns(BufferedImage image)
    {
        return (int)Math.ceil((double)image.getWidth() / charWidth);
    }
    
    /**
     * Returns the number of items currently in the buffer.
     * 
     * @return the buffer item count
     */
    public int getLength()
    {
        return buf.size();
    }
    
    /**
     * Gets the current x-coordinate of the cursor in terms of columns.
     * 
     * @return the x-coordinate of the cursor
     */
    public int getCursorX()
    {
        return cursorX;
    }
    
    /**
     * Gets the current y-coordinate of the cursor in terms of lines.
     * 
     * @return the y-coordinate of the cursor
     */
    public int getCursorY()
    {
        return cursorY;
    }
    
    /**
     * Adds a character to the end of the buffer and moves the cursor
     * accordingly.
     * 
     * @param c the character to add to the buffer
     */
    public void putChar(char c)
    {    
        /* Due to the lazyness of Treyarch's programmers, I had to incorporate
           some convoluted logic here in order to get some text output to behave
           in the same manner as is does on the actual terminal.
           
           On the actual terminal, if the user types text that wraps on screen
           and then attempts to erase that text with the <backspace> key, the
           cursor will move back with the text until it hits the left edge of
           the screen. It will remain on the same line at the left edge of the
           screen until text is entered back into that line, or until the
           <enter> key is pressed. For the sake of accuracy, I've decided to
           replicate this bug. */
        
        // Handle backspace
        if (c == '\b') {
            
            // Do nothing if the buffer is already empty
            if (buf.isEmpty()) {
                return;
            }
            
            // Get the last item in the buffer
            ScreenItem item = buf.get(buf.size() - 1);
            
            // Do nothing if the item is an image
            if (item.hasImage()) {
                return;
            }
            
            // Delete the last item in the buffer
            buf.remove(item);
            
            /* Treyarch screwed up right here -- the cursor should move up a
               line if the cursor hits the left edge of the screen when there is
               still more text to be removed. */
            // Move cursor back one space until it hits the left edge of screen
            if (cursorX > 0) {
                cursorX--;
            }
            
            return;
        }
        
        // Handle tab
        if (c == '\t') {
            // Calculate number of spaces to add to buffer
            int spacesToAdd = TAB_LENGTH - (cursorX % TAB_LENGTH);
            
            // Add spaces to buffer and increment cursor
            for (int i = 0; i < spacesToAdd; i++) {
                buf.add(new ScreenItem(' '));
                cursorX++;
            }
            
            return;
        }
        
        // Handle line wrap
        // Ignore typed newlines; they have to be handled later
        if (cursorX > columns - 1 && c != '\n') {
            
            // Add a newline to the buffer
            buf.add(new ScreenItem('\n'));
            
            // Move cursor all the way to the left and down a line
            cursorX = 0;
            cursorY++;
        }
        
        // Add character to buffer
        // Don't add newlines; they have to be handled later
        if (c != '\n') {
            buf.add(new ScreenItem(c));
        }
        
        // Calculate the position of the new character on the screen
        int newCharPosX = 0;
        int newCharPosY = 0;
        
        for (ScreenItem i : buf) {
            if (i.hasImage()) {
                newCharPosX += countImageColumns(i.getImage());
                newCharPosY += countImageLines(i.getImage());
                continue;
            }
            
            if (i.getCharacter() == '\n') {
                newCharPosX = 0;
                newCharPosY++;
                continue;
            }
            
            if (newCharPosX > columns - 1) {
                newCharPosX = 0;
                newCharPosY++;
            }
            
            newCharPosX++;
        }
        
        // Handle newline
        if (c == '\n') {
            
            // Add a newline to the buffer
            buf.add(new ScreenItem('\n'));
            
            /* If the cursor is below the new character, add an extra newline to
               realign the cursor */
            if (cursorY > newCharPosY) {
                buf.add(new ScreenItem('\n'));
            }
            
            // Move cursor all the way to the left and down a line
            cursorX = 0;
            cursorY++;
            
            return;
        }
                
        // Move cursor forward one space if it is not below the new character
        if (cursorY <= newCharPosY) {
            cursorX++;
        }
        
        // Trim line from the top if character will be drawn off screen (scroll)
        while (cursorY > lines - 1) {
            trimLine();
            cursorY--;
        }
    }
    
    /**
     * Adds an image to the end of the buffer and moves the cursor accordingly.
     * 
     * @param image the image to be added to the buffer
     */
    public void putImage(BufferedImage image)
    {
        // Scale image
        BufferedImage scaledImage = scaleImage(image);
        
        // Calculate dimensions of scaled image and move cursor
        int width = countImageColumns(scaledImage);
        int height = countImageLines(scaledImage);
        cursorX += width;
        cursorY += height;
        
        // Add image to buffer
        buf.add(new ScreenItem(scaledImage));
        
        // Add horizontal padding so image doesn't overlap next print
        for (int i = 0; i < width; i++) {
            buf.add(new ScreenItem(' '));
        }
        
        // Trim lines from the top if image will be drawn off screen (scroll)
        while (cursorY > lines - 1) {
            trimLine();
            cursorY--;
        }
    }
    
    /*
     * Scales an image according to the value of IMAGE_SCALE_FACTOR.
     */
    private BufferedImage scaleImage(BufferedImage image)
    {
        AffineTransform transform;
        AffineTransformOp transformOp;
        BufferedImage scaledImage;
        Graphics2D scaledImageGraphics;
        Rectangle2D scaledBounds;
        int scaledWidth;
        int scaledHeight;
        
        // Create scale transformation 
        transform = AffineTransform.getScaleInstance(IMAGE_SCALE_FACTOR,
                IMAGE_SCALE_FACTOR);
        transformOp = new AffineTransformOp(transform,
                AffineTransformOp.TYPE_BILINEAR);
        
        // Get bounds of transformed image
        scaledBounds = transformOp.getBounds2D(image);
        scaledWidth = (int)Math.ceil(scaledBounds.getWidth());
        scaledHeight = (int)Math.ceil(scaledBounds.getHeight());
        
        // Make a new blank image with the scaled bounds
        scaledImage = new BufferedImage(scaledWidth, scaledHeight,
                image.getType());
        
        // Draw scaled image data on blank image
        scaledImageGraphics = scaledImage.createGraphics();
        scaledImageGraphics.drawImage(image, transformOp, 0, 0);
        
        return scaledImage;
    }
    
    /*
     * Removes a line from the start of the buffer.
     * Use this to create a scrolling effect.
     */
    private void trimLine()
    {
        int x;
        int y;
        int width;
        int height;
        ScreenItem item;
        BufferedImage image;
        BufferedImage trimmedImage;
        ListIterator<ScreenItem> it;
        
        x = 0;
        
        /* Iterate through all screen items and remove items until an entire
           line has been removed */
        for (it = buf.listIterator(); it.hasNext(); x++) {
            
            // Get next screen item
            item = it.next();
            
            // Trim image
            if (item.hasImage()) {
                
                // Get image and calculate dimensions of trimmed image
                image = item.getImage();
                y = charHeight;
                width = image.getWidth();
                height = image.getHeight() - charHeight;
                
                // Remove image completely if there is no more left to trim
                if (height <= 0) {
                    it.remove();
                    continue;
                }
                
                // Trim image from top; replace current image with trimmed image
                trimmedImage = image.getSubimage(0, y, width, height);
                it.set(new ScreenItem(trimmedImage));
                break;
            }
            
            // Remove character
            it.remove();
            
            /*  End if a newline character is reached or if we've hit the edge
                of the screen */
            if (item.getCharacter() == '\n' || x > columns) {
                break;
            }
        }
    }
}