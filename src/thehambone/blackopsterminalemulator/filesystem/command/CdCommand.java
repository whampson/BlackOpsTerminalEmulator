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

package thehambone.blackopsterminalemulator.filesystem.command;

import thehambone.blackopsterminalemulator.LoginShell;
import thehambone.blackopsterminalemulator.Terminal;
import thehambone.blackopsterminalemulator.filesystem.ExecutableFile;
import thehambone.blackopsterminalemulator.filesystem.FileSystem;
import thehambone.blackopsterminalemulator.filesystem.FileSystemObject;
import thehambone.blackopsterminalemulator.filesystem.HomeDirectory;

/**
 * Created on Nov 28, 2015.
 *
 * @author thehambone <thehambone93@gmail.com>
 */
public class CdCommand extends ExecutableFile
{
    public CdCommand()
    {
        super(101, "cd");
    }
    
    @Override
    public void exec(String[] args)
    {
        LoginShell shell = Terminal.getActiveLoginShell();
        FileSystem fileSystem = shell.getServer().getFileSystem();
        FileSystemObject cd = shell.getCurrentDirectory();
        
        if (args.length == 0) {
            Terminal.println(cd.getPath());
            return;
        }
        
        if (args[0].equals(".")) {
            return;
        }
        
        if (args[0].equals("..")) {
            if (cd.getParent() == null) {
                return;     // root node; do nothing
            }
            shell.setCurrentDirectory(cd.getParent());
            return;
        }
        
        FileSystemObject fso = fileSystem.getFileSystemObject(args[0]);
        if (fso == null || (fso instanceof ExecutableFile && ((ExecutableFile)fso).isHidden())) {
            Terminal.println("Error:  Invalid Path");
            return;
        }

        if (fso instanceof HomeDirectory) {
            HomeDirectory homeDir = (HomeDirectory)fso;
            if (!homeDir.isUnlisted() && !homeDir.getName().equals(shell.getUser().getUsername())) {
                Terminal.println("Error:  Insufficient Permissions");
                return;
            }
        }
        shell.setCurrentDirectory(fso);
    }
}