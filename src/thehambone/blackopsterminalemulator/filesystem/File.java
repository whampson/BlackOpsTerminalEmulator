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
 * Created on Nov 28, 2015.
 *
 * @author thehambone <thehambone93@gmail.com>
 */
public class File implements FileSystemObject
{
    private final String name;
    
    private FileSystemObject parent;
    private boolean isHidden;
    private File aliasTarget;
    
    public File(String name)
    {
        this(name, false);
    }
    
    public File(String name, boolean isHidden)
    {
        this.name = name;
        this.isHidden = isHidden;
        aliasTarget = null;
    }
    
    public boolean isHidden()
    {
        return isHidden;
    }
    
    public void setHidden(boolean isHidden)
    {
        this.isHidden = isHidden;
    }
    
    public boolean isAlias()
    {
        return aliasTarget != null;
    }
    
    public void markAsAlias(File target)
    {
        aliasTarget = target;
    }
    
    public File getAliasTarget()
    {
        return aliasTarget;
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
        // nop
    }
    
    @Override
    public FileSystemObject getChild(String name)
    {
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