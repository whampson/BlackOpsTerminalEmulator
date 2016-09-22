/*
 * The MIT License
 *
 * Copyright 2015-2016 thehambone <thehambone93@gmail.com>.
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

package thehambone.blackopsterminalemulator.filesystem;

import java.awt.image.BufferedImage;
import thehambone.blackopsterminalemulator.Terminal;
import thehambone.blackopsterminalemulator.io.ResourceLoader;

/**
 * An {@code ImageFile} is a file containing image data.
 * <p>
 * Created on Nov 30, 2015.
 *
 * @author Wes Hampson <thehambone93@gmail.com>
 */
public final class ImageFile extends PrintableFile
{
    /**
     * Creates a new {@code ImageFile}.
     * 
     * @param id the filesystem object id
     * @param name the name of this file
     * @param resourcePath the path to the resource containing the file data
     */
    public ImageFile(int id, String name, String resourcePath)
    {
        super(id, name, resourcePath);
    }
    
    @Override
    public void print()
    {
        // Load file data
        BufferedImage image = ResourceLoader.loadImageFile(getResourceName());
        
        // Ignore and continue if no image data was loaded
        if (image == null) {
            return;
        }
        
        // Output the image to the terminal
        Terminal.println(image);
    }
}
