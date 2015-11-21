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

import java.awt.image.BufferedImage;

/**
 * Created on Nov 18, 2015.
 *
 * @author thehambone <thehambone93@gmail.com>
 */
public final class ScreenLine
{
    private final char[] textData;
    private BufferedImage imageData;
    
    public ScreenLine(int length)
    {
        textData = new char[length];
    }
    
    public int getLength()
    {
        return textData.length;
    }
    
    public boolean hasImageData()
    {
        return imageData != null;
    }
    
    public BufferedImage getImageData()
    {
        return imageData;
    }
    
    public void setImageData(BufferedImage imageData)
    {
        this.imageData = imageData;
    }
    
//    public String getTextData()
//    {
//        return new String(textData);
//    }
//    
//    public void setTextData(String s)
//    {
//        for (int i = 0; i < textData.length; i++) {
//            if (i > s.length() - 1) {
//                textData[i] = 0;
//            } else {
//                textData[i] = s.charAt(i);
//            }
//        }
//    }
    
    public char charAt(int index)
    {
        return textData[index];     // Potentially unsafe
    }
    
    public void putChar(int index, char c)
    {
        textData[index] = c;        // Potentially unsafe
    }
    
    public char removeChar(int index)
    {
        char c = textData[index];   // Potentially unsafe
        for (int i = 1; i < textData.length - index; i++) {
            textData[index + i - 1] = textData[index + i];
        }
//        textData[textData.length - index] = 0;
        return c;
    }
}