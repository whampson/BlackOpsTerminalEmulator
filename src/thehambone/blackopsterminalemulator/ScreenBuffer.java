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

/**
 * Created on Nov 18, 2015.
 *
 * @author thehambone <thehambone93@gmail.com>
 */
public final class ScreenBuffer
{
    private static final float IMAGE_SCALE_FACTOR = 0.65f;
    
    private final int columns;
    private final int rows;
    private final int charWidth;
    private final int charHeight;
    private final ScreenLine[] lines;
    
    private int cursorX;
    private int cursorY;
    
    public ScreenBuffer(int columns, int rows, int charWidth, int charHeight)
    {
        this.columns = columns;
        this.rows = rows;
        this.charWidth = charWidth;
        this.charHeight = charHeight;
        
        lines = new ScreenLine[rows];
        for (int i = 0; i < rows; i++) {
            lines[i] = new ScreenLine(columns);
        }
        
        cursorX = 0;
        cursorY = 0;
    }
    
    public ScreenLine getLine(int line)
    {
        return lines[line];
    }
    
    public int getCursorX()
    {
        return cursorX;
    }
    
    public int getCursorY()
    {
        return cursorY;
    }
    
    public void setCursorPosition(int x, int y)
    {
        cursorX = x;
        cursorY = y;
    }
    
    public void putChar(char c)
    {
        if (c == '\n') {
            cursorX = 0;
            cursorY++;
            appendLine(new ScreenLine(columns));
            return;
        } else if (cursorX >= columns) {
            cursorX = 0;
            cursorY++;
            appendLine(new ScreenLine(columns));
        }
        lines[cursorY].putChar(cursorX++, c);
    }
    
    public void putImage(BufferedImage image)
    {
        int x;
        int y;
        int width;
        int height;
        int nLines;
        BufferedImage scaledImage;
        BufferedImage section;
        
        // Scale image
        scaledImage = scaleImage(image);
        
        // Divide image into horizontal strips, each as tall as a character
        x = 0;
        width = scaledImage.getWidth();
        nLines = (int)Math.ceil((double)scaledImage.getHeight() / charHeight);
        for (int i = 0; i < nLines; i++) {
            y = i * charHeight;
            height = Math.min(charHeight, scaledImage.getHeight() - y);
            section = scaledImage.getSubimage(x, y, width, height);
            lines[cursorY].setImageData(section);
            if (cursorY >= rows -1) {
                break;
            } else {
                cursorY++;
            }
//            if (cursorY < rows - 1) {
//                cursorY++;
//            }
        }
    }
    
    private BufferedImage scaleImage(BufferedImage image)
    {
        AffineTransform transform;
        AffineTransformOp transformOp;
        BufferedImage scaledImage;
        Graphics2D scaledImageGraphics;
        Rectangle2D scaledBounds;
        int scaledWidth;
        int scaledHeight;
        
        // Create transformation 
        transform = AffineTransform.getScaleInstance(IMAGE_SCALE_FACTOR,
                IMAGE_SCALE_FACTOR);
        transformOp = new AffineTransformOp(transform,
                AffineTransformOp.TYPE_BILINEAR);
        
        /* Get bounds of transformed image and make a new empty image with the
          scaled bounds */
        scaledBounds = transformOp.getBounds2D(image);
        scaledWidth = (int)Math.ceil(scaledBounds.getWidth());
        scaledHeight = (int)Math.ceil(scaledBounds.getHeight());
        scaledImage = new BufferedImage(scaledWidth, scaledHeight,
                image.getType());
        
        // Draw scaled image on blank image
        scaledImageGraphics = scaledImage.createGraphics();
        scaledImageGraphics.drawImage(image, transformOp, 0, 0);
        
        return scaledImage;
    }
    
    private void appendLine(ScreenLine line)
    {
        // Shift lines upwards if the cursor is at the bottom of the screen
        if (cursorY > rows - 1) {
            for (int i = 1; i < lines.length; i++) {
                lines[i - 1] = lines[i];
            }
            cursorY = rows - 1;
        }
        
        lines[cursorY] = line;
    }
}