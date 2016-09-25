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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A {@code Directory} is a record in the filesystem that contains references
 * to other filesystem objects (like files or other directories). A directory
 * has one parent and can contain any number of child objects.
 * <p>
 * Created on Nov 28, 2015.
 *
 * @author Wes Hampson
 */
public class Directory implements FileSystemObject
{
    private final int id;
    private final String name;
    private final List<FileSystemObject> children;
    
    private FileSystemObject parent;
    
    /**
     * Creates a new {@code Directory}.
     * 
     * @param id the filesystem object id
     * @param name the directory name
     */
    public Directory(int id, String name)
    {
        this.id = id;
        this.name = name;
        children = new ArrayList<>();
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
        return !children.isEmpty();
    }
    
    @Override
    public void addChild(FileSystemObject child)
    {
        child.setParent(this);
        children.add(child);
        Collections.sort(children);
    }
    
    @Override
    public FileSystemObject getChild(int id)
    {
        if (!hasChildren()) {
            return null;
        }
        
        for (FileSystemObject child : children) {
            if (child == null) {
                continue;
            }
            
            if (child.getID() == id) {
                return child;
            } else {
                child = child.getChild(id);
                
                if (child != null && child.getID() == id) {
                    return child;
                }
            }
        }
        
        return null;
    }
    
    @Override
    public FileSystemObject getChild(String name)
    {
        if (!hasChildren()) {
            return null;
        }
        
        // Search for the child with the given name
        for (FileSystemObject child : children) {
            // Skip null children (I know, I'm a bad person)
            if (child == null) {
                continue;
            }
            
            // Have we found what we're looking for?
            if (child.getName().equalsIgnoreCase(name)) {
                // Yes we have!
                return child;
            } else {
                // We have not found what we're looking for
                // We must go deeper...
                child = child.getChild(name);
                
                // OK, /now/ have we found what we're looking for?
                if (child != null && child.getName().equalsIgnoreCase(name)) {
                    // Yes we have!
                    return child;
                }
            }
        }
        
        // Child not found
        return null;
    }
    
    @Override
    public List<FileSystemObject> getChildren()
    {
        return Collections.unmodifiableList(children);
    }
    
    @Override
    public String getName()
    {
        return name;
    }
    
    @Override
    public String getPath()
    {
        FileSystemObject obj = this;
        String path = "";
        
        // Traverse tree up to root node
        // Prepend the name of the parent node to the path
        while (obj != null) {
            path = obj.getName() + FILE_SEPARATOR_CHAR + path;
            obj = obj.getParent();
        }
        
        return path;
    }
    
    @Override
    public int compareTo(FileSystemObject o)
    {
        return name.compareTo(o.getName());
    }
}
