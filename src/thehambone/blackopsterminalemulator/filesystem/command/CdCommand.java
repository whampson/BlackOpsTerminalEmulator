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
import thehambone.blackopsterminalemulator.io.Logger;

/**
 * Created on Nov 28, 2015.
 *
 * @author thehambone <thehambone93@gmail.com>
 */
public class CdCommand extends ExecutableFile
{
    public CdCommand(int id)
    {
        super(id, "cd");
    }
    
    private void printCurrentDirectory()
    {
        LoginShell shell = Terminal.getActiveLoginShell();
        Terminal.println(shell.getCurrentDirectory().getPath());
    }
    
    private String[] tokenizePath(String path)
    {
        int tokenCount = 1;
        for (int i = 0; i < path.length(); i++) {
            if (path.charAt(i) == FILE_SEPARATOR_CHAR) {
                tokenCount++;
            }
        }
        
        String[] tokens = new String[tokenCount];
        
        int tokenIndex = 0;
        String tokenBuffer = "";
        char c;
        for (int i = 0; i < path.length(); i++) {
            c = path.charAt(i);
            if (c == FILE_SEPARATOR_CHAR) {
                tokens[tokenIndex++] = tokenBuffer;
                tokenBuffer = "";
            } else {
                tokenBuffer += c;
            }
        }
        
        tokens[tokenIndex] = tokenBuffer;
        
        return tokens;
    }
    
    @Override
    public void exec(String[] args)
    {
        LoginShell shell = Terminal.getActiveLoginShell();
        FileSystem fileSystem = shell.getSystem().getFileSystem();
        HomeDirectory currentUserHomeDir = shell.getUser().getHomeDirectory();
        
        if (args.length == 0) {
            printCurrentDirectory();
            return;
        }
        
        String path = args[0];
        String[] pathNodes = tokenizePath(path);
        
        FileSystemObject cd = shell.getCurrentDirectory();
        
        if (pathNodes.length > 2
                && (pathNodes[0].isEmpty() && pathNodes[1].isEmpty())) {
            printCurrentDirectory();
            return;
        }
        
        FileSystemObject fso;
        String node;
        boolean wasLastNodePopOperator = false;
       
        for (int i = 0; i < pathNodes.length; i++) {
            node = pathNodes[i];
            
            if (node.isEmpty()) {
                if (i == 0) {
                    cd = fileSystem.getRoot();
                    continue;
                } else if (i == pathNodes.length - 1) {
                    continue;
                }
                Terminal.println("Error:  Invalid Path");
                return;
            }
            if (node.equals(".")) {
                continue;
            } else if (node.equals("..")) {
                if (wasLastNodePopOperator) {
                    continue;
                }
                if (cd.hasParent()) {
                    cd = cd.getParent();
                }
                wasLastNodePopOperator = true;
                continue;
            }
            fso = fileSystem.getFileSystemObject(node);
            if (fso == null) {
                Terminal.println("Error:  Invalid Path");
                return;
            } else if (fso instanceof HomeDirectory
                    && fso != currentUserHomeDir) {
                Terminal.println("Error:  Insufficient Permissions");
                return;
            }
            cd = fso;
            wasLastNodePopOperator = false;
        }
        
        shell.setCurrentDirectory(cd);
//        Logger.debug("working directory set to %s\n", cd.getPath());
    }
}
