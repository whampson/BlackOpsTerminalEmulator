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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import thehambone.blackopsterminalemulator.filesystem.Directory;
import thehambone.blackopsterminalemulator.filesystem.File;
import thehambone.blackopsterminalemulator.filesystem.FileSystem;

/**
 * Created on Nov 28, 2015.
 *
 * @author thehambone <thehambone93@gmail.com>
 */
public class Server
{
    private static final List<Server> SERVERS = new ArrayList<>();
    
    public static void addServer(Server s)
    {
        SERVERS.add(s);
    }
    
    public static Server getServer(String name)
    {
        Server server = null;
        
        for (Server s : SERVERS) {
            if (s.getName().equalsIgnoreCase(name)) {
                server = s;
                break;
            }
        }
        
        return server;
    }
    
    private final String name;
    private final String loginMessage;
    
    private final List<User> users;
    private final FileSystem fileSystem;
    private final Directory commandDirectory;
    
    public Server(String name, String loginMessage,
            List<User> users, FileSystem fileSystem, Directory commandDirectory)
    {
        this.name = name;
        this.loginMessage = loginMessage;
        
        this.users = new ArrayList<>(users);
        this.fileSystem = fileSystem;
        this.commandDirectory = commandDirectory;
    }
    
    public String getName()
    {
        return name;
    }
    
    public String getLoginMessage()
    {
        return loginMessage;
    }
    
    public FileSystem getFileSystem()
    {
        return fileSystem;
    }
    
    public Directory getCommandDirectory()
    {
        return commandDirectory;
    }
    
    public User getUser(String username)
    {
        User user = null;
        
        for (User u : users) {
            if (u.getUsername().equalsIgnoreCase(username)) {
                user = u;
                break;
            }
        }
        
        return user;
    }
    
    public List<User> getUsers()
    {
        return Collections.unmodifiableList(users);
    }
    
    public LoginShell login()
    {
        if (!loginMessage.isEmpty()) {
            Terminal.println(getLoginMessage());
        }
        
        Terminal.print("USER:");
        String username = Terminal.readLine();
        Terminal.print("PASSWORD:");
        String password = Terminal.readLine('*');
        
        LoginShell shell = null;
        User user = getUser(username);
        if (user == null || !user.getPassword().equalsIgnoreCase(password)
                || user.getHomeDirectory().isUnlisted()) {
            Terminal.println("Invalid Password");
        } else {
            shell = new LoginShell(this, user);
        }
        
        return shell;
    }
}