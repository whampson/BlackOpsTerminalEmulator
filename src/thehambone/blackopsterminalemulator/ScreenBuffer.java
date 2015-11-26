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
    
    public ScreenItem itemAt(int index)
    {
        return buf.get(index);
    }
    
    public int countImageLines(BufferedImage image)
    {
        return (int)Math.ceil((double)image.getHeight() / charHeight) - 1;
    }
    
    public int countImageColumns(BufferedImage image)
    {
        return (int)Math.ceil((double)image.getWidth() / charWidth);
    }
    
    public int getLineCount()
    {
        if (buf.isEmpty()) {
            return 0;
        }
        
        int lineCount = 1;
        int x = 0;
        for (ScreenItem i : buf) {
            x++;
            if (i.hasImage()) {
                lineCount += countImageLines(i.getImage());
                x = 0;
            } else if (i.getCharacter() == '\n' || x > columns) {
                lineCount++;
                x = 0;
            }
        }
        return lineCount;
    }
    
    public int getLength()
    {
        return buf.size();
    }
    
    public int getCursorX()
    {
        return cursorX;
    }
    
    public int getCursorY()
    {
        return cursorY;
    }
    
    public void putChar(char c)
    {
        if (c == '\n') {
            cursorX = -1;
            if (cursorY < lines - 1) {
                cursorY++;
            }
        } else if (cursorX >= columns) {
            cursorX = 0;
            if (cursorY < lines - 1) {
                cursorY++;
            }
        }
        buf.add(new ScreenItem(c));
        
        if (getLineCount() > lines) {
            trimLine();
        }
        
        cursorX++;
    }
    
    public void putImage(BufferedImage image)
    {
        BufferedImage scaledImage = scaleImage(image);
        cursorX += countImageColumns(scaledImage);
        cursorY += countImageLines(scaledImage);
        buf.add(new ScreenItem(scaledImage));
        for (int i = 0; i < countImageColumns(scaledImage); i++) {
            buf.add(new ScreenItem(' '));
        }
        while (cursorY > lines - 1) {
            trimLine();
            cursorY--;
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
    
    private void trimLine()
    {
        int x = 0;
        ScreenItem item;
        ListIterator<ScreenItem> it;
        
        for (it = buf.listIterator(); it.hasNext(); x++) {
            item = it.next();
            if (item.hasImage()) {
                BufferedImage img = item.getImage();
                int y = charHeight;
                int width = img.getWidth();
                int height = img.getHeight() - charHeight;
                if (height <= 0) {
                    it.remove();
                    continue;
                }
                BufferedImage chopped = img.getSubimage(0, y, width, height);
                it.set(new ScreenItem(chopped));
                break;
            }
            if (x > columns - 1) {
                break;
            }
            it.remove();
            if (item.getCharacter() == '\n') {
                break;
            }
        }
    }
}