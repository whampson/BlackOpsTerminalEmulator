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

import thehambone.blackopsterminalemulator.Shell;
import thehambone.blackopsterminalemulator.Terminal;
import thehambone.blackopsterminalemulator.filesystem.ExecutableFile;

/**
 * The "alicia" command.
 * <p>
 * Alicia is a virtual therapist designed to help those in need of someone to
 * talk to about their problems.
 * <p>
 * Created on Dec 6, 2015.
 *
 * @author Wes Hampson
 */
public class AliciaCommand extends ExecutableFile
{
    // Alicia is a well-versed computerwoman with a whopping 10 phrases!
    private static final String[] RESPONSES = new String[]
    {
        "Can you go into greater detail?",
        "Can you tell me more?",
        "Do you feel that is the source of your problems?",
        "Does that question interest you?",
        "I think you're on to something there!",
        "I'd be interested in hearing more about that.",
        "I'd like to hear more about that.",
        "Really?",
        "Tell me more about yourself.",
        "This is very interesting."
    };
    
    /**
     * Creates a new instance of the {@code AliciaCommand} class.
     * 
     * @param id the filesystem object id
     */
    public AliciaCommand(int id)
    {
        super(id, "alicia");
    }
    
    /*
     * Gets a random response from the list of responses.
     */
    private String getRandomResponse()
    {
        int min = 0;
        int max = RESPONSES.length - 1;
        
        int rand = (int)(Math.random() * (max - min)) + min;
        
        return RESPONSES[rand];
    }
    
    @Override
    public void exec(String[] args)
    {
        AliciaShell shell = new AliciaShell();
        shell.exec();
    }
    
    /*
     * This extension of the {@code Shell} class provies an interface for
     * interacting with Alicia.
     */
    private class AliciaShell extends Shell
    {
        /*
         * Creates a new {@code AlicaShell} object.
         */
        private AliciaShell()
        {
            super(">");
        }
        
        @Override
        protected void onLaunch()
        {
            Terminal.println("Welcome to Alicia, your virtual therapist.  "
                    + "(type quit to exit)");
        }
        
        @Override
        protected void run()
        {
            String input;
            
            // Simple input loop
            while (isRunning()) {
                Terminal.print(getPrompt());
                input = Terminal.readLine();
                if (input.equalsIgnoreCase("quit")) {
                    Terminal.println("It's been nice talking with you!  "
                            + "Goodbye!");
                    terminate();
                } else {
                    /* Alicia gives a random response regardless of what the
                       user types. What a great listener!
                    */
                    Terminal.println(getRandomResponse());
                }
            }
        }
    }
}
