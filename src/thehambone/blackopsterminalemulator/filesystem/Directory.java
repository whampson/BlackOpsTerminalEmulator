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
import java.util.Collections;
import java.util.List;

/**
 * Created on Nov 28, 2015.
 *
 * @author thehambone <thehambone93@gmail.com>
 */
public class Directory implements FileSystemObject
{
    private final int id;
    private final String name;
    private final List<FileSystemObject> children;
    
    private FileSystemObject parent;
    
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
    public FileSystemObject getChild(String name)
    {
        if (!hasChildren()) {
            return null;
        }
        
        for (FileSystemObject child : children) {
            if (child == null) {
                continue;
            }
            if (child.getName().equalsIgnoreCase(name)) {
                return child;
            }
            if (!child.hasChildren()) {
                continue;
            }
            child = child.getChild(name);
            if (child != null && child.getName().equalsIgnoreCase(name)) {
                return child;
            }
        }
        
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
