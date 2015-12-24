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

package thehambone.blackopsterminalemulator.io;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import thehambone.blackopsterminalemulator.Terminal;

/**
 * This class is responsible for outputting information about the program as it
 * is running.
 * <p>
 * Created on Dec 14, 2015.
 *
 * @author thehambone <thehambone93@gmail.com>
 */
public class Logger
{
    private static final StringBuilder LOG_BUFFER = new StringBuilder();
    
    private static final String TIMESTAMP_FORMAT = "HH:mm:ss";
    private static final String CRASH_DUMP_TIMESTAMP_FORMAT = "yyyyMMddHHmmss";
    
    private static final String INFO_PREFIX_FORMAT = "<%s> [INFO]: %s";
    private static final String ERROR_PREFIX_FORMAT = "<%s> [ERROR]: %s";
    
    /**
     * Creates a text file containing information about the current state of the
     * program, including everything that has been logged thus far. This file is
     * useful for debugging a fatal crash.
     * 
     * @return the name of the newly-created file.
     * @throws IOException if the file failed to be created or written to
     */
    public static String generateCrashDump() throws IOException
    {
        String fileName = "crash-dump_" + timestamp(CRASH_DUMP_TIMESTAMP_FORMAT)
                + ".log";
        
        PrintWriter pw = new PrintWriter(new FileOutputStream(fileName), true);
        
        // Dump the current state of the Terminal to the file
        Terminal.crashDump(pw);
        
        // Write the log buffer to the file with the correct newline characters
        pw.println(LOG_BUFFER.toString()
                .replaceAll("\\\n", System.getProperty("line.separator")));
        pw.flush();
        
        return fileName;
    }
    
    /**
     * Prints an informational message.
     * 
     * @param s the message to print
     */
    public static void info(String s)
    {
        info("%s\n", s);
    }
    
    /**
     * Prints a formatted informational message.
     * 
     * @param format the message format
     * @param args the format arguments
     * @see String#format(java.lang.String, java.lang.Object...)
     */
    public static void info(String format, Object... args)
    {
        formatAndOutput(INFO_PREFIX_FORMAT, format, args);
    }
    
    /**
     * Prints an error message.
     * 
     * @param s the message to be printed
     */
    public static void error(String s)
    {
        error("%s\n", s);
    }
    
    /**
     * Prints a formatted error message.
     * 
     * @param format the message format
     * @param args the format arguments
     * @see String#format(java.lang.String, java.lang.Object...)
     */
    public static void error(String format, Object... args)
    {
        formatAndOutput(ERROR_PREFIX_FORMAT, format, args);
    }
    
    /**
     * Appends the stack trace of an error to the log.
     * 
     * @param cause the cause of the error
     */
    public static void stackTrace(Throwable cause)
    {
        StringWriter writer = new StringWriter();
        PrintWriter pw = new PrintWriter(writer);
        cause.printStackTrace(pw);
        
        formatAndOutput(ERROR_PREFIX_FORMAT, "%s\n",
                writer.getBuffer().toString());
    }
    
    /*
     * Prepends the message with a message type prefix, then formats the string
     * by printf() rules.
     */
    private static void formatAndOutput(String messagePrefixFormat,
            String format, Object... args)
    {
        String message = String.format(format, args);
        String toOutput = String.format(messagePrefixFormat,
                timestamp(), message);
        
        LOG_BUFFER.append(toOutput);
        System.out.print(toOutput);
    }
    
    /*
     * Returns a string containing the current time.
     */
    private static String timestamp()
    {
        return timestamp(TIMESTAMP_FORMAT);
    }
    
    /*
     * Returns a string containing the current date and time as specified in the
     * timestamp format parameter.
     */
    private static String timestamp(String format)
    {
        return new SimpleDateFormat(format).format(new Date());
    }
}
