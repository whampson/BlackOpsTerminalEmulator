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

import thehambone.blackopsterminalemulator.Terminal;
import thehambone.blackopsterminalemulator.filesystem.ExecutableFile;

/**
 * The "more" command.
 * <p>
 * This command is useless. It changes the shell prompt to "--MORE--" until
 * another command is executed. On a real terminal, output from one program
 * would be piped into this program to allow for the program's output to be
 * paged. Since the Black Ops Terminal doesn't support piping, this command
 * exists for purely aesthetic purposes.
 * <p>
 * Created on Nov 29, 2015.
 *
 * @author thehambone <thehambone93@gmail.com>
 */
public class MoreCommand extends ExecutableFile
{
    /**
     * Creates a new instance of the {@code MoreCommand} class.
     * 
     * @param id the filesystem object id
     */
    public MoreCommand(int id)
    {
        super(id, "more");
    }
    
    @Override
    public void exec(String[] args)
    {
        Terminal.getActiveLoginShell().setPrompt("--MORE--");
    }
}
