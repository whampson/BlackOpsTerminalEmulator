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

package thehambone.blackopsterminalemulator.filesystem;

/**
 * A {@code HomeDirectory} is a special directory that is used to store a
 * specific user's files. A home directory is only meant to be accessible by the
 * user who "owns" it.
 * <p>
 * Created on Nov 28, 2015.
 *
 * @author Wes Hampson <thehambone93@gmail.com>
 */
public class HomeDirectory extends Directory
{
    private boolean isUnlisted;
    
    /**
     * Creates a new {@code HomeDirectory}.
     * 
     * @param id the filesystem object id
     * @param name the name of the directory
     */
    public HomeDirectory(int id, String name)
    {
        this(id, name, false);
    }
    
    /**
     * Creates a new {@code HomeDirectory}.
     * 
     * @param id the filesystem object id
     * @param name the name of the directory
     * @param isUnlisted a boolean indicating whether this home directory should
     *                   be unlisted
     */
    public HomeDirectory(int id, String name, boolean isUnlisted)
    {
        super(id, name);
        
        this.isUnlisted = isUnlisted;
    }
    
    /**
     * Checks whether this home directory is unlisted.
     * <p>
     * Users with unlisted home directories are not shown in the results when
     * the system's list of users is queried, nor can they be logged in via the
     * standard login process. However, the directory itself is not hidden and
     * will be shown when the proper directory list is queried.
     * 
     * @return {@code true} if this home directory is marked as unlisted,
     * {@code false} otherwise
     */
    public boolean isUnlisted()
    {
        return isUnlisted;
    }
    
    /**
     * Marks or unmarks this directory as unlisted.
     * <p>
     * Users with unlisted home directories are not shown in the results when
     * the system's list of users is queried, nor can they be logged in via the
     * standard login process. However, the directory itself is not hidden and
     * will be shown when the proper directory list is queried.
     * 
     * @param isUnlisted a boolean indicating whether the home directory as
     *                   unlisted
     */
    public void setUnlisted(boolean isUnlisted)
    {
        this.isUnlisted = isUnlisted;
    }
}
