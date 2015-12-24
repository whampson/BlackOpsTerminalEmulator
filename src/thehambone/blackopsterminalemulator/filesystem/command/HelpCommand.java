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

import thehambone.blackopsterminalemulator.Terminal;
import thehambone.blackopsterminalemulator.filesystem.ExecutableFile;

/**
 * The "help" command.
 * <p>
 * This command outputs information about some of the system commands.
 * <p>
 * Created on Nov 28, 2015.
 *
 * @author thehambone <thehambone93@gmail.com>
 */
public class HelpCommand extends ExecutableFile
{
    /**
     * Creates a new instance of the {@code HelpCommand} class.
     * 
     * @param id the filesystem object id
     */
    public HelpCommand(int id)
    {
        super(id, "help");
    }
    
    @Override
    public void exec(String[] args)
    {
        Terminal.println("System Help:\n");
        Terminal.println("help\tdisplays "
                + "this help information");
        Terminal.println("cat\t\t\t\t   "
                + "prints the contents of a file (.txt,.pic,.snd)");
        Terminal.println("cd [ |.|..|path]   "
                + "changes the current directory.");
        Terminal.println("clear\t\t\t   "
                + "clear the display");
        Terminal.println("dir\t\t\t\t   "
                + "displays the contents of the current directory");
        Terminal.println("decode\t\t\t   "
                + "decodes a encrypted string using an agency standard cypher");
        Terminal.println("encode\t\t\t   "
                + "encodes a string using an agency standard cypher");
        Terminal.println("exit\t\t\t   "
                + "exits the current login session");
        Terminal.println("help\t\t\t   "
                + "display's this help screen");
        Terminal.println("login\t\t\t   "
                + "starts a new login session on the current system");
        Terminal.println("mail\t\t\t   "
                + "opens the current users mailbox");
        Terminal.println("rlogin [system]\t   "
                + "attempts a login session on a remote system");
        Terminal.println("who\t\t\t\t   "
                + "lists the users that have accounts on the current system");
    }
}
