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

import java.util.ArrayList;
import java.util.List;

/**
 * A {@code File} is a an entry the filesystem that can contain data. A file is
 * also the endpoint of a branch in the filesystem tree, also known as a "leaf."
 * This means that a {@code File} can have no child filesystem objects.
 * <p>
 * Created on Nov 28, 2015.
 *
 * @author thehambone <thehambone93@gmail.com>
 */
public class File implements FileSystemObject
{
    private final int id;
    private final String name;
    
    private FileSystemObject parent;
    private boolean isHidden;
    private File aliasTarget;
    
    /**
     * Creates a new {@code File}.
     * 
     * @param id the filesystem object id
     * @param name the file name
     */
    public File(int id, String name)
    {
        this(id, name, false);
    }
    
    /**
     * Creates a new {@code File}.
     * 
     * @param id the filesystem object id
     * @param name the file name
     * @param isHidden a boolean value indicating whether the file should be
     *                 marked as hidden
     */
    public File(int id, String name, boolean isHidden)
    {
        this.id = id;
        this.name = name;
        this.isHidden = isHidden;
        aliasTarget = null;
    }
    
    /**
     * Checks whether this file is marked as hidden.
     * 
     * @return {@code true} if this file is hidden, {@code false} otherwise
     */
    public boolean isHidden()
    {
        return isHidden;
    }
    
    /**
     * Marks or unmarks this file as hidden.
     * 
     * @param isHidden a boolean value indicating whether the file should be
     *                 marked as hidden
     */
    public void setHidden(boolean isHidden)
    {
        this.isHidden = isHidden;
    }
    
    /**
     * Checks whether this file is a link to another file.
     * 
     * @return {@code true} if this file points to another file in the
     *         filesystem, {@code false} otherwise
     */
    public boolean isAlias()
    {
        return aliasTarget != null;
    }
    
    /**
     * Marks this file as a link to another file.
     * 
     * @param target the file to which this file should link to
     */
    public void markAsAlias(File target)
    {
        aliasTarget = target;
    }
    
    /**
     * Returns the file that this file points to if this file is an alias. If
     * this file is not an alias, {@code null} is returned.
     * 
     * @return the target file id this file is an alias
     */
    public File getAliasTarget()
    {
        return aliasTarget;
    }
    
    @Override
    public int getID()
    {
        return id;
    }
    
    @Override
    public boolean hasParent()
    {
        return parent != null;
    }
    
    @Override
    public FileSystemObject getParent()
    {
        return parent;
    }
    
    @Override
    public void setParent(FileSystemObject parent)
    {
        this.parent = parent;
    }
    
    @Override
    public boolean hasChildren()
    {
        return false;
    }
    
    @Override
    public void addChild(FileSystemObject child)
    {
        throw new UnsupportedOperationException(
                "this operation is not supported for files");
    }
    
    @Override
    public FileSystemObject getChild(int id)
    {
        // Files do not have children
        return null;
    }
    
    @Override
    public FileSystemObject getChild(String name)
    {
        // Files do not have children
        return null;
    }
    
    @Override
    public List<FileSystemObject> getChildren()
    {
        return new ArrayList<>();
    }
    
    @Override
    public String getName()
    {
        return name;
    }
    
    @Override
    public String getPath()
    {
        FileSystemObject par = this;
        String path = "";
        
        // Traverse tree up to root node
        // Prepend the name of the parent node to the path
        while (par != null) {
            path = par.getName() + FILE_SEPARATOR_CHAR + path;
            par = par.getParent();
        }
        
        return path;
    }
    
    @Override
    public int compareTo(FileSystemObject o)
    {
        return name.compareTo(o.getName());
    }
}
