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

import java.util.List;

/**
 * A {@code FileSystemObject} is and individual element within a filesystem.
 * Every entry in the filesystem, whether it be a file or directory, is a
 * {@code FileSystemObject}. This interface extends the Comparable interface so
 * FileSystemObjects can be compared to one another.
 * <p>
 * Created on Nov 28, 2015.
 *
 * @author thehambone <thehambone93@gmail.com>
 */
public interface FileSystemObject extends Comparable<FileSystemObject>
{
    /**
     * The character used to separate filesystem object names.
     */
    public static final char FILE_SEPARATOR_CHAR = '/';
    
    /**
     * Gets unique identifier of this filesystem object.
     * 
     * @return the filesystem object id
     */
    public int getID();
    
    /**
     * Gets the name of this filesystem object.
     * 
     * @return the filesystem object name
     */
    public String getName();
    
    /**
     * Checks whether this filesystem object is the child of another filesystem
     * object.
     * 
     * @return {@code true} if this filesystem object has a parent,
     *         {@code false} otherwise
     */
    public boolean hasParent();
    
    /**
     * Gets the parent object of this filesystem object.
     * 
     * @return this filesystem object's parent
     */
    public FileSystemObject getParent();
    
    /**
     * Sets the parent object of this filesystem object.
     * 
     * @param parent the new parent
     */
    public void setParent(FileSystemObject parent);
    
    /**
     * Checks whether this filesystem object has child objects.
     * 
     * @return {@code true} if this filesystem object has children,
     *         {@code false} otherwise
     */
    public boolean hasChildren();
    
    /**
     * Marks a filesystem object as a child of this filesystem object.
     * 
     * @param child the object to be marked as a child
     */
    public void addChild(FileSystemObject child);
    
    /**
     * Gets a child of this filesystem object by name.
     * 
     * @param name the name of the object to retrieve
     * @return the retrieved object if found, {@code null} if not found
     */
    public FileSystemObject getChild(String name);
    
    /**
     * Returns all children of this filesystem object as a list of filesystem
     * objects.
     * 
     * @return a list containing all children
     */
    public List<FileSystemObject> getChildren();
    
    /**
     * Returns a string representation of this filesystem object's location in
     * the filesystem tree. Object names are separated by the character defined
     * by {@link #FILE_SEPARATOR_CHAR}.
     * 
     * @return a string describing the location of this object in the filesystem
     *         tree
     */
    public String getPath();
}
