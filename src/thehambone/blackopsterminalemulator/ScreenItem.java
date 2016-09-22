/*
 * The MIT License
 *
 * Copyright 2015-2016 Wes Hampson <thehambone93@gmail.com>.
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

import java.awt.image.BufferedImage;

/**
 * A {@code ScreenItem} is an item that can be displayed on the screen. A
 * {@code ScreenItem} can either be an image or a character, but not both at the
 * same time.
 * <p>
 * Created on Nov 21, 2015.
 *
 * @author Wes Hampson <thehambone93@gmail.com>
 */
public final class ScreenItem
{
    private char ch;
    private BufferedImage image;
    
    /**
     * Creates a new character {@code ScreenItem}.
     * 
     * @param ch the character to be added
     */
    public ScreenItem(char ch)
    {
        this.ch = ch;
        image = null;
    }
    
    /**
     * Creates a new image {@code ScreenItem}.
     * 
     * @param image the image to be added
     */
    public ScreenItem(BufferedImage image)
    {
        this.image = image;
        ch = 0;
    }
    
    /**
     * Returns the character represented by this item.
     * 
     * @return the character represented by this item; 0 (NUL) is returned if
     *         this item represents an image and not a character
     */
    public char getCharacter()
    {
        return ch;
    }
    
    /**
     * Indicates whether this item represents an image.
     * 
     * @return {@code true} if this item represents an image, {@code false}
     *         otherwise
     */
    public boolean hasImage()
    {
        return image != null;
    }
    
    /**
     * Gets the image represented by this item. {@link #hasImage()} should be
     * used before calling this method to check whether this item contains an
     * image.
     * 
     * @return the image represented by this item; {@code null} if the item
     *         represents a character
     */
    public BufferedImage getImage()
    {
        return image;
    }
    
    /**
     * Sets the character represented by this item. If the item previously
     * represented an image, the image will be replaced by the character.
     * 
     * @param ch the character to add
     */
    public void setCharacter(char ch)
    {
        this.ch = ch;
        image = null;
    }
    
    /**
     * Sets the image represented by this item. If the item previously
     * represented a character, the character will be replaced by the image.
     * 
     * @param image the image to add
     */
    public void setImage(BufferedImage image)
    {
        this.image = image;
        ch = 0;
    }
    
    @Override
    public String toString()
    {
        String data = "";
        
        if (hasImage()) {
            data = String.format("image: { width = %d, height = %d }",
                    image.getWidth(), image.getHeight());
        } else {
            if (ch == '\n') {
                data += "(\\n)";
            }
            data += Character.toString(ch);
        }
        
        return data;
    }
}
