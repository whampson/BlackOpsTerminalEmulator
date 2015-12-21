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

import thehambone.blackopsterminalemulator.LoginShell;
import thehambone.blackopsterminalemulator.Terminal;
import static thehambone.blackopsterminalemulator.filesystem.FileSystemObject.FILE_SEPARATOR_CHAR;

/**
 * A {@code FileSystem} is a collection of records which contain information
 * regarding files present on the system. A filesystem can also contain
 * directories, which are used to group files and give the filesystem a
 * hierarchal, tree-like structure.
 * <p>
 * Created on Nov 28, 2015.
 *
 * @author thehambone <thehambone93@gmail.com>
 */
public class FileSystem
{
    private final FileSystemObject root;
    
    /**
     * Creates a new {@code FileSystem}.
     * 
     * @param root the object to use as the filesystem root
     */
    public FileSystem(FileSystemObject root)
    {
        this.root = root;
    }
    
    /**
     * Returns the topmost node in the filesystem tree.
     * 
     * @return the root object
     */
    public FileSystemObject getRoot()
    {
        return root;
    }
    
    public FileSystemObject getFileSystemObject(int id)
    {
        if (id == root.getID()) {
            return root;
        }
        
        return root.getChild(id);
    }
    
    /**
     * Gets an object from within the filesystem by name.
     * 
     * @param name the name of the filesystem object to retrieve
     * @return the filesystem object if found, {@code null} if the object is not
     *         found
     */
    public FileSystemObject getFileSystemObject(String name)
    {
        String rootName;
        
        // Check if the root node was requested
        /* If the root node is nameless, treat the file separator character (/)
           as the root node name */
        rootName = Character.toString(FileSystemObject.FILE_SEPARATOR_CHAR);
        if ((root.getName() == null || root.getName().isEmpty())
                && name.equals(rootName)) {
            return root;
        }
        
        // Traverse the tree until the child with the matching name is found
        return root.getChild(name);
    }
}
