/*
 * The MIT License
 *
 * Copyright 2015-2016 thehambone <thehambone93@gmail.com>.
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

import thehambone.blackopsterminalemulator.io.ResourceLoader;

/**
 * This class represents an email that can be found on many of the user's
 * accounts accessible via the "mail" command.
 * <p>
 * Created on Dec 6, 2015.
 *
 * @author Wes Hampson <thehambone93@gmail.com>
 */
public final class Mail
{
    private final String sender;
    private final String date;
    private final String subject;
    private final String resourceName;
    
    /**
     * Creates a new {@code Mail} object.
     * 
     * @param sender the sender of the mail
     * @param date the date the mail was received
     * @param subject the subject of the mail
     * @param resourceName the name of the file containing the mail body
     */
    public Mail(String sender, String date, String subject, String resourceName)
    {
        this.sender = sender;
        this.date = date;
        this.subject = subject;
        this.resourceName = resourceName;
    }
    
    /**
     * Gets the sender of this mail item.
     * 
     * @return the mail sender
     */
    public String getSender()
    {
        return sender;
    }
    
    /**
     * Gets the date that this mail was received.
     * 
     * @return the mail received date as a String
     */
    public String getDate()
    {
        return date;
    }
    
    /**
     * Gets the subject of this mail.
     * 
     * @return the mail subject
     */
    public String getSubject()
    {
        return subject;
    }
    
    /**
     * Gets the name of the resource containing the mail body.
     * 
     * @return the name of the mail body resource
     */
    public String getResourceName()
    {
        return resourceName;
    }
    
    /**
     * Loads the mail body from the resource file and prints it to the screen.
     */
    public void open()
    {
        // Load file data
        String fileData = ResourceLoader.loadTextFile(getResourceName());
        
        // Output the contents of the file
        /* Print entire file as a single string to allow for the "--MORE--"
           pager prompt to show */
        Terminal.print(fileData);
    }
    
    @Override
    public String toString()
    {
        return String.format(
                "Mail: { sender = %s, date = %s, subject = %s, resource = %s }",
                sender, date, subject, resourceName);
    }
}
