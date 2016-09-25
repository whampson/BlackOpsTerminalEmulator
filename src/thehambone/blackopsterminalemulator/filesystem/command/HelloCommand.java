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

package thehambone.blackopsterminalemulator.filesystem.command;

import thehambone.blackopsterminalemulator.Terminal;
import thehambone.blackopsterminalemulator.filesystem.ExecutableFile;

/**
 * The "hello" command.
 * <p>
 * This command basically does nothing apart from launching Zork if the correct
 * argument is provided.
 * <p>
 * Created on Nov 28, 2015.
 *
 * @author Wes Hampson
 */
public class HelloCommand extends ExecutableFile
{
    /**
     * Creates a new instance of the {@code HelloCommand} class.
     * 
     * @param id the filesystem object id
     */
    public HelloCommand(int id)
    {
        super(id, "hello");
    }
    
    @Override
    public void exec(String[] args)
    {
        // Show a help messsage if no arguments are provided
        if (args.length == 0) {
            Terminal.println("Error:  Invalid Input - common usages include:");
            Terminal.println("hello brother, hello nurse, and hello sailor");
            return;
        }
        
        // Launch Zork
        if (args[0].equals("sailor")) {
            ExecutableFile zork = (ExecutableFile)Terminal.getActiveLoginShell()
                    .getSystem().getFileSystem().getFileSystemObject("zork");
            zork.exec(new String[0]);
        }
    }
}
