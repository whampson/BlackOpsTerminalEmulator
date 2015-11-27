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
 * Created on Nov 18, 2015.
 *
 * @author thehambone <thehambone93@gmail.com>
 */
public final class ScreenBuffer
{
    private static final float IMAGE_SCALE_FACTOR = 0.66f;
    
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
        ScreenItem item;
        
        // Handle newlines and line wrapping
        if (c == '\n') {
            cursorX = -1;
            cursorY++;
        } else if (cursorX > columns - 1) {
            cursorX = 0;
            cursorY++;
        }
        
        // Handle backspace
        if (c == '\b') {
            if (buf.isEmpty()) {
                return;
            }
            item = buf.get(buf.size() - 1);
            if (item.hasImage()) {
                return;
            }
            buf.remove(item);
            if (cursorX > 0) {
                cursorX--;
            }
            return;
        }
        
        // Add character to buffer
        item = new ScreenItem(c);
        buf.add(item);
        
        // Trim line from the top if character will be drawn off screen (scroll)
        while (cursorY > lines - 1) {
            trimLine();
            cursorY--;
        }
        
        // Move cursor forward 1 character
        cursorX++;
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