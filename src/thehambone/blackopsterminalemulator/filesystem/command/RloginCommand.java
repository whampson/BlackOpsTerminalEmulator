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
import thehambone.blackopsterminalemulator.Server;
import thehambone.blackopsterminalemulator.Terminal;
import thehambone.blackopsterminalemulator.filesystem.ExecutableFile;

/**
 * Created on Nov 29, 2015.
 *
 * @author thehambone <thehambone93@gmail.com>
 */
public class RloginCommand extends ExecutableFile
{
    public RloginCommand(int id)
    {
        super(id, "rlogin");
    }
    
    @Override
    public void exec(String[] args)
    {
        if (args.length == 0) {
            Terminal.println("Error:  Invalid Input - expected machine name");
            return;
        }
        
        if (Terminal.maxLoginShellsReached()) {
            Terminal.println("Error:  Too many logins - "
                    + "Use exit to close open shells");
            return;
        }
        
        Server system = Terminal.getServer(args[0]);
        
        if (system == null) {
            Terminal.println("Error:  unknown system");
            return;
        }
        LoginShell newShell = system.login();
        
        if (newShell != null) {
            Terminal.pushLoginShell(newShell);
            newShell.exec();
        }
    }
}
