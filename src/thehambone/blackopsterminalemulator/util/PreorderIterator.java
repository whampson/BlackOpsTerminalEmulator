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

package thehambone.blackopsterminalemulator.util;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Stack;
import thehambone.blackopsterminalemulator.filesystem.FileSystemObject;

/**
 * A {@code PreorderIterator} is an Iterator that traverses a tree of
 * FileSystemObjects. In a preorder traversal, the root node is visited before
 * the node's children.
 * <p>
 * Created on Dec 25, 2015.
 *
 * @author Wes Hampson <thehambone93@gmail.com>
 */
public class PreorderIterator implements Iterator<FileSystemObject>
{
    private final Stack<FileSystemObject> nodeStack;
    
    /**
     * Creates a new {@code PreorderIterator} object.
     * 
     * @param root the tree root
     */
    public PreorderIterator(FileSystemObject root)
    {
        nodeStack = new Stack<>();
        if (root != null) {
            nodeStack.push(root);
        }
    }
    
    @Override
    public boolean hasNext()
    {
        return !nodeStack.isEmpty();
    }
    
    @Override
    public FileSystemObject next()
    {
        FileSystemObject nextNode;
        
        if (hasNext()) {
            nextNode = nodeStack.pop();
            if (nextNode.hasChildren()) {
                for (FileSystemObject child : nextNode.getChildren()) {
                    nodeStack.push(child);
                }
            }
        } else {
            throw new NoSuchElementException();
        }
        
        return nextNode;
    }
}
