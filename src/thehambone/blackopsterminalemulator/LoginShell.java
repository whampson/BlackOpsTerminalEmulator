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

import thehambone.blackopsterminalemulator.filesystem.command.CdCommand;
import thehambone.blackopsterminalemulator.filesystem.command.HelpCommand;
import thehambone.blackopsterminalemulator.filesystem.Directory;
import thehambone.blackopsterminalemulator.filesystem.File;
import thehambone.blackopsterminalemulator.filesystem.FileSystemObject;
import thehambone.blackopsterminalemulator.filesystem.Executable;

/**
 * Created on Nov 28, 2015.
 *
 * @author thehambone <thehambone93@gmail.com>
 */
public class LoginShell extends Shell
{
    private final Server server;
    private final User user;
    
    private FileSystemObject currentDirectory;
    
    public LoginShell(Server server, User user)
    {
        super("$", "Error:  Unknown Command - try \"help\"");
        
        this.server = server;
        this.user = user;
        
        currentDirectory = user.getHomeDirectory();
        
//        commands.put("cd", new CdCommand());
//        commands.put("help", new HelpCommand());
    }
    
    public Server getServer()
    {
        return server;
    }
    
    public User getUser()
    {
        return user;
    }
    
    public FileSystemObject getCurrentDirectory()
    {
        return currentDirectory;
    }
    
    public void setCurrentDirectory(FileSystemObject dir)
    {
        currentDirectory = dir;
    }
    
    @Override
    protected void onLaunch()
    {
        String input;
        String commandName;
        String[] args;
        Executable executable;
        FileSystemObject fso;
        
        Directory commandDir = server.getCommandDirectory();
        
        while (true) {
            executable = null;
            Terminal.print(prompt);
            input = Terminal.readLine();
            if (input.isEmpty()) {
                continue;
            }
            if (input.contains(" ")) {
                int spaceIndex = input.indexOf(' ');
                commandName = input.substring(0, spaceIndex);
                args = input.substring(spaceIndex + 1).split("\\s");
            } else {
                commandName = input;
                args = new String[0];
            }
            
            fso = commandDir.getChild(commandName);
            if (fso instanceof File) {
                File f = (File)fso;
                if (f.isAlias() && f.getAliasTarget() instanceof Executable) {
                    executable = (Executable)f.getAliasTarget();
                }
            }
            if (fso instanceof Executable) {
                executable = (Executable)fso;
            }
            
            if (executable == null) {
                Terminal.println(errorMessage);
            } else {
                executable.exec(args);
            }
            if (commandName.equals("exit")) {
                break;
            }
        }
    }
}