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

package thehambone.blackopsterminalemulator.filesystem;

/**
 * A {@code PrintableFile} is a type of file whose data is meant to be output to
 * the user.
 * <p>
 * Created on Nov 30, 2015.
 *
 * @author thehambone <thehambone93@gmail.com>
 */
public abstract class PrintableFile extends File
{
    private final String resourceName;
    
    /**
     * Creates a new {@code PrintableFile}.
     * 
     * @param id the filesystem object id
     * @param name the name of this file
     * @param resourceName the name of the resource containing the file data
     */
    public PrintableFile(int id, String name, String resourceName)
    {
        super(id, name);
        
        this.resourceName = resourceName;
    }
    
    /**
     * Gets the name of the resource containing the file data.
     * 
     * @return the resource path
     */
    protected String getResourceName()
    {
        return resourceName;
    }
    
    /**
     * Outputs the contents of this file.
     */
    public abstract void print();
}
