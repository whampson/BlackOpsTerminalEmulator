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

import thehambone.blackopsterminalemulator.filesystem.ExecutableFile;
import thehambone.blackopsterminalemulator.filesystem.File;
import thehambone.blackopsterminalemulator.filesystem.FileSystemObject;

/**
 * A {@code LoginShell} is a shell associated with a specific user account. It
 * processes input through a text-based interface.
 * <p>
 * Created on Nov 28, 2015.
 *
 * @author thehambone <thehambone93@gmail.com>
 */
public final class LoginShell extends Shell
{
    private static final String DEFAULT_PROMPT = "$";
    
    private final Server system;
    private final UserAccount user;
    
    private FileSystemObject currentDirectory;
    
    /**
     * Creates a new {@code LoginShell}.
     * 
     * @param system the system on which this shell should be invoked
     * @param user the user account associated with this shell
     */
    public LoginShell(Server system, UserAccount user)
    {
        super(DEFAULT_PROMPT);
        
        this.system = system;
        this.user = user;
        
        currentDirectory = user.getHomeDirectory();
    }
    
    /**
     * Returns the system that this shell is running on.
     * 
     * @return the system that this shell is running on 
     */
    public Server getSystem()
    {
        return system;
    }
    
    /**
     * Gets the user account associated with this shell.
     * 
     * @return the user account associated with this shell
     */
    public UserAccount getUser()
    {
        return user;
    }
    
    /**
     * Returns the current working directory of the shell.
     * 
     * @return the current working directory
     */
    public FileSystemObject getCurrentDirectory()
    {
        return currentDirectory;
    }
    
    /**
     * Sets the current working directory.
     * 
     * @param dir the new working directory
     */
    public void setCurrentDirectory(FileSystemObject dir)
    {
        currentDirectory = dir;
    }
    
    // Internal command "3arc"
    private void cmd3arc(String[] args)
    {
        if (args.length == 0) {
            return;
        }
        
        if (args[0].equals("intel") || args[0].equals("unlock")) {
            Terminal.println("Cheat Enabled!");
        }
    }
    
    // Internal command "exit"
    private void cmdExit(String[] args)
    {
        Terminal.popLoginShell();
        terminate();
    }
    
    @Override
    protected void onLaunch()
    {
        // Nothing special to be done here
    }
    
    @Override
    protected void run()
    {
        String input;
        String prompt;
        String commandName;
        String[] args;
        ExecutableFile exe;
        FileSystemObject fso;
        
        // Loop until shell is terminated
        while (isRunning()) {
            // Reset previous command
            exe = null;
            
            // Print prompt
            prompt = getPrompt();
            Terminal.print(prompt);
            
            /* Reset the prompt to default if it has changed since the last
               command was executed */
            if (!prompt.equals(DEFAULT_PROMPT)) {
                setPrompt(DEFAULT_PROMPT);
            }
            
            // Read a line of text from the user
            input = Terminal.readLine();
            
            // Re-prompt user if input is blank
            if (input.isEmpty()) {
                continue;
            }
            
            // Separate command name and arguments
            /* The input is tokenized -- first token is the command name,
               remaining tokens are the arguments */
            if (input.contains(" ")) {
                int spaceIndex = input.indexOf(' ');
                commandName = input.substring(0, spaceIndex);
                args = input.substring(spaceIndex + 1).split("\\s");
            } else {
                commandName = input;
                args = new String[0];
            }
            
            // Check if an "internal" command was called
            switch (commandName) {
                case "3arc":
                    cmd3arc(args);
                    continue;
                case "exit":
                    cmdExit(args);
                    continue;
            }
            
            // Search the filesystem for an executable matching the command name
            fso = system.getFileSystem().getFileSystemObject(commandName);
            
            // Ckeck if the retrieved object is an exeutable
            if (fso instanceof ExecutableFile) {
                exe = (ExecutableFile)fso;
            } else if (fso instanceof File) {
                // Check if object is a symlink to an executable
                File f = (File)fso;
                if (f.isAlias() && f.getAliasTarget() instanceof ExecutableFile) {
                    exe = (ExecutableFile)f.getAliasTarget();
                }
            }
            
            // Print error message if an executable with matching name not found
            if (exe == null) {
                Terminal.println("Error:  Unknown Command - try \"help\"");
                continue;
            }
            
            // Run the command
            exe.exec(args);
        }
    }
}
