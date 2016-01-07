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

package thehambone.blackopsterminalemulator.util;

import javax.swing.JOptionPane;
import thehambone.blackopsterminalemulator.io.Logger;

/**
 * An {@code UncaughtExceptionHandler} is effectively responsible for catching
 * unexpected exceptions. This particular uncaught exception handler will do the
 * following when an unchecked exception is thrown:
 *     1) Log the error to the console.
 *     2) Attempt to write details about the current state of the program to a
 *        file (crash dump).
 *     3) Display an error message to alert the user that a fatal exception has
 *        occurred.
 *     4) Exit the JVM with a nonzero exit code.
 * <p>
 * Created on Dec 20, 2015.
 *
 * @author thehambone <thehambone93@gmail.com>
 */
public final class UncaughtExceptionHandler
        implements Thread.UncaughtExceptionHandler
{
    @Override
    public void uncaughtException(Thread t, Throwable e)
    {
        // Log the exception
        Logger.error("Fatal exception in thread \"%s\"\n", t.getName());
        Logger.stackTrace(e);
        
        // Attempt to write a crash dump to a file
        String crashReportFileName = null;
        try {
            crashReportFileName = Logger.generateCrashDump();
        } catch (Throwable tr) {
            // Catch anything that can be thrown
            // We don't want another RuntimeException!
            Logger.error("Failed to generate crash dump");
            Logger.stackTrace(tr);
        }
        
        // Error message
        String message = String.format("<html><p style='width: 300px'>"
                + "A fatal exception has occured in thread \"%s\"."
                + "<br><br>%s: %s<br><br>"
                + "%s<br>"
                + "Please contact the emulator developer for assistance."
                + "</p></html>",
                t.getName(), e.getClass().getSimpleName(), e.getMessage(),
                crashReportFileName != null
                        ? "A crash report has been generated "
                                + "(" + crashReportFileName + ")."
                        : "A crash report failed to generate.");
        
        // Show error message
        JOptionPane.showMessageDialog(null,
                message,
                "Fatal Error",
                JOptionPane.ERROR_MESSAGE);
        
        /* Exit the JVM with a code of 1 to indicate that the program exited
           with an error
         */
        System.exit(1);
    }
}
