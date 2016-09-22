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

/**
 * A {@code FixedLengthQueue} represents a first-in-first-out (FIFO) collection
 * of objects whose maximum capacity does not change. The class implements the
 * Iterable interface so items in the queue can be iterated.
 * <p>
 * Created on Nov 29, 2015.
 *
 * @author Wes Hampson <thehambone93@gmail.com>
 * @param <E> the type of each element in the queue
 */
public class FixedLengthQueue<E> implements Iterable<E>
{
    private final E[] queue;
    
    private int front;
    private int rear;
    private int itemCount;
    
    /**
     * Creates an empty {@code Queue} with the specified capacity.
     * 
     * @param capacity the maximum number of elements the queue can hold
     */
    @SuppressWarnings("unchecked")      // Should be safe to cast Object to E
    public FixedLengthQueue(int capacity)
    {
        if (capacity < 1) {
            throw new IllegalArgumentException(
                    "capacity must be a postive integer");
        }
        
        queue = (E[])new Object[capacity];
        front = 0;
        rear = -1;
        itemCount = 0;
    }
    
    /**
     * Checks whether the queue is empty.
     * 
     * @return {@code true} if the queue is empty, {@code false} otherwise
     */
    public boolean isEmpty()
    {
        return itemCount == 0;
    }
    
    /**
     * Checks whether the maximum capacity of the queue has been reached.
     * 
     * @return {@code true} if the queue is full, {@code false} otherwise
     */
    public boolean isFull()
    {
        return itemCount == queue.length;
    }
    
    /**
     * Returns the current number of items in the queue.
     * 
     * @return the number of items in the queue
     */
    public int getItemCount()
    {
        return itemCount;
    }
    
    /**
     * Gets the item at the front of the queue.
     * 
     * @return the item at the front of the queue
     * @throws QueueException if the queue is empty
     */
    public E peek()
    {
        if (isEmpty()) {
            throw new QueueException("queue is empty");
        }
        
        return queue[front];
    }
    
    /**
     * Adds an item to the rear of the queue.
     * 
     * @param item the item to be added
     * @throws QueueException if the queue is full
     */
    public void insert(E item)
    {
        if (isFull()) {
            throw new QueueException("queue is full");
        }
        
        if (rear == queue.length - 1) {
            rear = -1;
        }
        
        queue[++rear] = item;
        itemCount++;
    }
    
    /**
     * Removes the item at the front of the queue.
     * 
     * @return the item removed
     * @throws QueueException if the queue is empty
     */
    public E remove()
    {
        if (isEmpty()) {
            throw new QueueException("queue is empty");
        }
        
        if (front == queue.length) {
            front = 0;
        }
        itemCount--;
        return queue[front++];
    }
    
    @Override
    public Iterator<E> iterator()
    {
        return new Iterator<E>()
        {
            int itemsIterated = 0;
            int index = front;
            
            @Override
            public boolean hasNext()
            {
                return itemsIterated != itemCount;
            }

            @Override
            public E next()
            {
                if (index == queue.length) {
                    index = 0;
                }
                itemsIterated++;
                return queue[index++];
            }
        };
    }
}
