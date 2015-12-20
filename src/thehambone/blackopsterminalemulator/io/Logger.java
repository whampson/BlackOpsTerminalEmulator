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
import thehambone.blackopsterminalemulator.Main;
import thehambone.blackopsterminalemulator.filesystem.File;

/**
 * Created on Dec 14, 2015.
 *
 * @author thehambone <thehambone93@gmail.com>
 */
public class Logger
{
    private static final StringBuilder LOG = new StringBuilder();
    
    private static final String TIMESTAMP_FORMAT = "HH:mm:ss";
    private static final String CRASH_DUMP_TIMESTAMP_FORMAT = "yyyyMMddHHmmss";
    
    private static final String DEBUG_PREFIX_FORMAT = "<%s> [DEBUG]: %s";
    private static final String INFO_PREFIX_FORMAT = "<%s> [INFO]: %s";
    private static final String ERROR_PREFIX_FORMAT = "<%s> [ERROR]: %s";
    
    public static String generateCrashDump() throws IOException
    {
        String timestamp = new SimpleDateFormat(CRASH_DUMP_TIMESTAMP_FORMAT)
                .format(new Date());
        
        String fileName = "crash-dump_" + timestamp + ".log";
        
        PrintWriter pw = new PrintWriter(new FileOutputStream(fileName), true);
        
//        Terminal.crashDump(pw);
        
        pw.println(LOG.toString().replaceAll("\\\n",
                System.getProperty("line.separator")));
        pw.flush();
        
        return fileName;
    }
    
//    public static void debug(String s)
//    {
//        debug("%s\n", s);
//    }
//    
//    public static void debug(String format, Object... args)
//    {
//        if (!Main.DEBUG) {
//            return;
//        }
//        
//        formatAndOutput(DEBUG_PREFIX_FORMAT, format, args);
//    }
    
    public static void info(String s)
    {
        info("%s\n", s);
    }
    
    public static void info(String format, Object... args)
    {
        formatAndOutput(INFO_PREFIX_FORMAT, format, args);
    }
    
    public static void error(String s)
    {
        error("%s\n", s);
    }
    
    public static void error(String format, Object... args)
    {
        formatAndOutput(ERROR_PREFIX_FORMAT, format, args);
    }
    
//    public static void error(Throwable cause)
//    {
//        if (Main.DEBUG) {
//            stackTrace(cause);
//        } else {
//            error("%s: %s\n", cause.getClass().getName(), cause.getMessage());
//        }
//    }
    
    public static void stackTrace(Throwable cause)
    {
        StringWriter writer = new StringWriter();
        PrintWriter pw = new PrintWriter(writer);
        cause.printStackTrace(pw);
        
        formatAndOutput(ERROR_PREFIX_FORMAT, "%s\n",
                writer.getBuffer().toString());
        
    }
    
    private static void formatAndOutput(String messagePrefixFormat,
            String format, Object... args)
    {
        String message = String.format(format, args);
        String toOutput = String.format(messagePrefixFormat,
                timestamp(), message);
        
        LOG.append(toOutput);
        System.out.print(toOutput);
    }
    
    private static String timestamp()
    {
        return new SimpleDateFormat(TIMESTAMP_FORMAT).format(new Date());
    }
}
