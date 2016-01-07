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

/**
 * An {@code ExecutableFile} is a special type of file that contains code to
 * perform various tasks. {@code ExecutableFiles} can be run, or "executed," by
 * the user via use of the {@link thehambone.blackopsterminalemulator.Shell}.
 * <p>
 * Created on Nov 28, 2015.
 *
 * @author thehambone <thehambone93@gmail.com>
 */
public abstract class ExecutableFile extends File
{
    /**
     * Creates a new {@code ExecutableFile}.
     * 
     * @param id the filesystem object id
     * @param name the name of this file
     */
    public ExecutableFile(int id, String name)
    {
        super(id, name);
    }
    
    /**
     * Executes the code contained within this file.
     * 
     * @param args execution arguments
     */
    public abstract void exec(String[] args);
}
