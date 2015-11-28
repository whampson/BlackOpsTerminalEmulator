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

import java.util.HashMap;
import java.util.Map;

/**
 * Created on Nov 28, 2015.
 *
 * @author thehambone <thehambone93@gmail.com>
 */
public abstract class Shell
{
    protected final Map<String, Command> commands;
    
    protected String prompt;
    protected String errorMessage;
    
    protected Shell(String prompt, String errorMessage)
    {
        commands = new HashMap<>();
        
        this.prompt = prompt;
        this.errorMessage = errorMessage;
    }
    
    public void setPrompt(String prompt)
    {
        this.prompt = prompt;
    }
    
    public void launch()
    {
        onLaunch();

        String input;
        String commandName;
        String[] args;
        Command command;
        
        while (true) {
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
            
            command = commands.get(commandName);
            if (command == null) {
                Terminal.println(errorMessage);
            } else {
                command.exec(null);
            }
        }
    }
    
    protected abstract void onLaunch();
}