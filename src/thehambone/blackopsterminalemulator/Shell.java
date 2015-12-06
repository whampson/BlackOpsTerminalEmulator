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

/**
 * A {@code Shell} provides an interface that allows the user to interact with
 * the terminal.
 * <p>
 * Created on Nov 28, 2015.
 *
 * @author thehambone <thehambone93@gmail.com>
 */
public abstract class Shell
{
    private volatile boolean isRunning;
    
    private String prompt;
    
    /**
     * Creates a new {@code Shell}.
     * 
     * @param prompt the default command prompt
     */
    public Shell(String prompt)
    {
        this.prompt = prompt;
        isRunning = false;
    }
    
    /**
     * Returns the command prompt string.
     * 
     * @return the shell prompt
     */
    public String getPrompt()
    {
        return prompt;
    }
    
    /**
     * Sets the command prompt string.
     * 
     * @param prompt the new command prompt
     */
    public void setPrompt(String prompt)
    {
        this.prompt = prompt;
    }
    
    /**
     * Launches the shell session.
     */
    public void exec()
    {
        isRunning = true;
        onLaunch();
        run();
    }
    
    /**
     * Ends the shell session.
     */
    public void terminate()
    {
        isRunning = false;
    }
    
    /**
     * Checks whether a shell session is currently in progress.
     * 
     * @return {@code true} if a session is in progress, {@code false} otherwise
     */
    protected boolean isRunning()
    {
        return isRunning;
    }
    
    /**
     * This code runs when a shell session is launched.
     */
    protected abstract void onLaunch();
    
    /**
     * This code runs while a session is in progress.
     */
    protected abstract void run();
}
