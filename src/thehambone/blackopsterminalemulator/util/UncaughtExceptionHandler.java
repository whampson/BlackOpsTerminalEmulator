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

package thehambone.blackopsterminalemulator.util;

import java.io.IOException;
import javax.swing.JOptionPane;
import thehambone.blackopsterminalemulator.Terminal;
import thehambone.blackopsterminalemulator.io.Logger;

/**
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
        Logger.error("Fatal exception in thread \"%s\"\n", t.getName());
        Logger.stackTrace(e);
        
        String crashReportFileName = null;
        try {
            crashReportFileName = Logger.generateCrashDump();
        } catch (IOException ex) {
            Logger.stackTrace(ex);
        }
        
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
        
        JOptionPane.showMessageDialog(null,
                message,
                "Fatal Error",
                JOptionPane.ERROR_MESSAGE);
        
        System.exit(1);
    }
}
