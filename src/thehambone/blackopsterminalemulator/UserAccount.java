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

import thehambone.blackopsterminalemulator.filesystem.HomeDirectory;

/**
 * A {@code UserAccount} is a record that stores a user's files and login
 * credentials.
 * <p>
 * Created on Nov 28, 2015.
 *
 * @author Wes Hampson <thehambone93@gmail.com>
 */
public final class UserAccount
{
    private final String username;
    private final String password;
    private final HomeDirectory homeDirectory;
    private final Mailbox mailbox;
    
    /**
     * Creates a new {@code UserAccount}.
     * 
     * @param username the login name
     * @param password the login password
     * @param homeDirectory the directory where files are located
     */
    public UserAccount(String username, String password,
            HomeDirectory homeDirectory)
    {
        this.username = username;
        this.password = password;
        this.homeDirectory = homeDirectory;
        
        mailbox = new Mailbox();
    }
    
    /**
     * Gets this user's login name.
     * 
     * @return the user's login name
     */
    public String getUsername()
    {
        return username;
    }
    
    /**
     * Gets this user's login password.
     * 
     * @return the user's password
     */
    public String getPassword()
    {
        return password;
    }
    
    /**
     * Returns the directory where all of the user's files are located.
     * 
     * @return the user's home directory
     */
    public HomeDirectory getHomeDirectory()
    {
        return homeDirectory;
    }
    
    /**
     * Returns the mailbox associated with this user account.
     * 
     * @return this user's mailbox
     */
    public Mailbox getMailbox()
    {
        return mailbox;
    }
}
