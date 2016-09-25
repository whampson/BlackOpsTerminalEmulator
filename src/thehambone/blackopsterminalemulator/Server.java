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

package thehambone.blackopsterminalemulator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import thehambone.blackopsterminalemulator.filesystem.FileSystem;

/**
 * A {@code Server} is a system that hosts files and and contains a set of users
 * what have access to those files.
 * <p>
 * Created on Nov 28, 2015.
 *
 * @author Wes Hampson
 */
public class Server
{
    private final String name;
    private final String loginMessage;
    private final List<UserAccount> users;
    
    private FileSystem fileSystem;
    
    /**
     * Creates a new {@code Server}.
     * 
     * @param name the name of the server
     * @param loginMessage the message to be displayed when a user attempts to
     *                     log in 
     */
    public Server(String name, String loginMessage)
    {
        this.name = name;
        this.loginMessage = loginMessage;
        
        this.users = new ArrayList<>();
    }
    
    /**
     * Gets the name of this server.
     * 
     * @return the server name
     */
    public String getName()
    {
        return name;
    }
    
    /**
     * Gets the {@code FileSystem} object containing the files and directories
     * on this system.
     * 
     * @return the {@code FileSystem} object associated with this system
     */
    public FileSystem getFileSystem()
    {
        return fileSystem;
    }
    
    /**
     * 
     * @param fileSystem a {@code FileSystem} object containing containing the
     *                   directories and files for the system
     */
    public void setFileSystem(FileSystem fileSystem)
    {
        this.fileSystem = fileSystem;
    }
    
    /**
     * Adds a user account to this server.
     * 
     * @param u the user account to add
     */
    public void addUser(UserAccount u)
    {
        users.add(u);
    }
    
    /**
     * Gets a user account on this server by its username.
     * 
     * @param username the username of the user account to search for
     * @return the user account with the matching username, {@code null} if the
     *         account is not found
     */
    public UserAccount getUser(String username)
    {
        UserAccount user = null;
        
        for (UserAccount u : users) {
            if (u.getUsername().equalsIgnoreCase(username)) {
                user = u;
                break;
            }
        }
        
        return user;
    }
    
    /**
     * Returns a list containing all users that have accounts on this server.
     * 
     * @return a list of users on this server
     */
    public List<UserAccount> getUsers()
    {
        return Collections.unmodifiableList(users);
    }
    
    /**
     * Invokes the user login process for this server.
     * <p>
     * The login process is as follows:
     *     1) The user is prompted to enter their username.
     *     2) The user is prompted to enter their password.
     *     3) The username and password are validated. If validation is
     *        successful, a new {@code LoginShell} is returned starting in the
     *        user's home directory.
     * 
     * @return a login shell for the user if the login attempt was successful, 
     *         {@code null} if the login attempt was not successful
     */
    public LoginShell login()
    {
        // Show login message
        if (!loginMessage.isEmpty()) {
            Terminal.println(loginMessage);
        }
        
        // Prompt for username
        Terminal.print("USER:");
        String username = Terminal.readLine();
        
        // Prompt for password
        Terminal.print("PASSWORD:");
        String password = Terminal.readLine('*');
        
        // Validate username and password
        // If the user's homedir is unlisted, don't validate
        LoginShell shell = null;
        UserAccount user = getUser(username);
        if (user == null || !user.getPassword().equalsIgnoreCase(password)
                || user.getHomeDirectory().isUnlisted()) {
            Terminal.println("Invalid Password");
        } else {
            shell = new LoginShell(this, user);
        }
        
        return shell;
    }
}
