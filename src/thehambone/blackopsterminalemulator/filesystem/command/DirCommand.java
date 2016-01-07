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

package thehambone.blackopsterminalemulator.filesystem.command;

import java.util.List;
import thehambone.blackopsterminalemulator.LoginShell;
import thehambone.blackopsterminalemulator.ScreenBuffer;
import thehambone.blackopsterminalemulator.Terminal;
import thehambone.blackopsterminalemulator.filesystem.Directory;
import thehambone.blackopsterminalemulator.filesystem.ExecutableFile;
import thehambone.blackopsterminalemulator.filesystem.File;
import thehambone.blackopsterminalemulator.filesystem.FileSystemObject;

/**
 * The "dir" command.
 * <p>
 * Prints the contents of the current directory.
 * <p>
 * Created on Nov 28, 2015.
 *
 * @author thehambone <thehambone93@gmail.com>
 */
public class DirCommand extends ExecutableFile
{
    /**
     * Creates a new instance of the {@code DirCommand} class.
     * 
     * @param id the filesystem object id
     */
    public DirCommand(int id)
    {
        super(id, "dir");
    }
    
    /*
     * Rounds a decimal number to the nearest whole number; 0.5 is rounded down.
     */
    private double roundHalfDown(double d)
    {
        if (d % 0.5 == 0) {
            return (double)(int)d;  // Round down by casting to int (truncate)
        } else {
            return Math.round(d);   // Round as usual
        }
    }
    
    @Override
    public void exec(String[] args)
    {
        LoginShell shell = Terminal.getActiveLoginShell();
        FileSystemObject cd = shell.getCurrentDirectory();
        
        List<FileSystemObject> objs = cd.getChildren();
        
        FileSystemObject o;
        String objName;
        int itemsPrinted = 0;
        
        // Iterate through all children of the current directory
        for (int i = 0; i < objs.size(); i++) {
            o = objs.get(i);
            objName = o.getName();
            
            /* Append a slash (/) to the end of the object name to indicate it
               is a directory
            */
            if (o instanceof Directory) {
                objName += Directory.FILE_SEPARATOR_CHAR;
            }
            
            // Skip hidden objects
            if (o instanceof File && ((File)o).isHidden()) {
                continue;
            }
            
            // Print object name
            Terminal.print(' ');
            Terminal.print(objName);
            itemsPrinted++;
            
            // Print a newline after every 4 items printed
            if (itemsPrinted % 4 == 0) {
                Terminal.println();
                continue;
            }
            
            // Align items on the line by padding space with tab characters
            int tabCount = 5 - (int)roundHalfDown(
                            (double)objName.length() / ScreenBuffer.TAB_LENGTH);
            for (int j = 0; j < tabCount; j++) {
                Terminal.print('\t');
            }
        }
        
        Terminal.println();
    }
}
