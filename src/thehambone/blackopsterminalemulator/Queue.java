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

package thehambone.blackopsterminalemulator;

/**
 * Created on Nov 29, 2015.
 *
 * @author thehambone <thehambone93@gmail.com>
 */
public class Queue<T>
{
    private final T[] queue;
    
    private int front;
    private int rear;
    private int itemCount;
    
    @SuppressWarnings("unchecked")
    public Queue(int capacity)
    {
        if (capacity < 1) {
            throw new IllegalArgumentException(
                    "capacity must be a postive integer");
        }
        queue = (T[])new Object[capacity];
        front = 0;
        rear = -1;
        itemCount = 0;
    }
    
    public boolean isEmpty()
    {
        return itemCount == 0;
    }
    
    public boolean isFull()
    {
        return itemCount == queue.length;
    }
    
    public int getItemCount()
    {
        return itemCount;
    }
    
    public T peek()
    {
        if (isEmpty()) {
            throw new QueueException("queue is empty");
        }
        
        return queue[front];
    }
    
    public void insert(T item)
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
    
    public T remove()
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
}