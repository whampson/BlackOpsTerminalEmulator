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
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import thehambone.blackopsterminalemulator.filesystem.ExecutableFile;
import thehambone.blackopsterminalemulator.filesystem.command.AliciaCommand;
import thehambone.blackopsterminalemulator.filesystem.command.CatCommand;
import thehambone.blackopsterminalemulator.filesystem.command.CdCommand;
import thehambone.blackopsterminalemulator.filesystem.command.ClearCommand;
import thehambone.blackopsterminalemulator.filesystem.command.DOACommand;
import thehambone.blackopsterminalemulator.filesystem.command.DecodeCommand;
import thehambone.blackopsterminalemulator.filesystem.command.DirCommand;
import thehambone.blackopsterminalemulator.filesystem.command.EncodeCommand;
import thehambone.blackopsterminalemulator.filesystem.command.FoobarCommand;
import thehambone.blackopsterminalemulator.filesystem.command.HelloCommand;
import thehambone.blackopsterminalemulator.filesystem.command.HelpCommand;
import thehambone.blackopsterminalemulator.filesystem.command.LoginCommand;
import thehambone.blackopsterminalemulator.filesystem.command.MailCommand;
import thehambone.blackopsterminalemulator.filesystem.command.MoreCommand;
import thehambone.blackopsterminalemulator.filesystem.command.RloginCommand;
import thehambone.blackopsterminalemulator.filesystem.command.WhoCommand;
import thehambone.blackopsterminalemulator.filesystem.command.ZorkCommand;
import thehambone.blackopsterminalemulator.io.Logger;
import thehambone.blackopsterminalemulator.io.ResourceLoader;
import thehambone.blackopsterminalemulator.util.UncaughtExceptionHandler;

/**
 * This class handles program initialization.
 * <p>
 * Created on Nov 17, 2015.
 *
 * @author thehambone <thehambone93@gmail.com>
 */
public class Main
{
    public static final String PROGRAM_TITLE
            = "Call of Duty: Black Ops Terminal Emulator";
    public static final String PROGRAM_VERSION
            = "1.0-alpha";
    
//    public static final boolean DEBUG = true;
    
    /**
     * Program entry point.
     * 
     * @param args command-line arguments
     */
    public static void main(String[] args)
    {
        // TODO: class for file io, cat command should
        // resolve path like CD command does,
        // unchaught exception handler, documentation, logging, add resources,
        // arrow keys, menu bar
        
        Logger.info("%s version %s\n", PROGRAM_TITLE, PROGRAM_VERSION);
        
        initUncaughtExceptionHandler();
        initLookAndFeel();
        
        String title = PROGRAM_TITLE + " - " + PROGRAM_VERSION;
        Terminal.setTitle(title);
        
        Map<String, Class<? extends ExecutableFile>> executables;
        executables = registerExecutables();
        
        Server lastServer;
        UserAccount lastUser;
        
        /* Must load data in the following order:
          servers, filesystem, users, mail
        */
        ResourceLoader.loadMOTD();
        lastServer = ResourceLoader.loadServers();
        ResourceLoader.loadFileSystem(executables);
        lastUser = ResourceLoader.loadUsers();
        ResourceLoader.loadMail();
        
        launchTerminal(new LoginShell(lastServer, lastUser));
    }
    
    private static void launchTerminal(LoginShell defaultLoginShell)
    {
        Logger.info("Launching terminal...");
        
        Terminal.show();
        Terminal.printMOTD();
        
        while (true) {
            Terminal.pushLoginShell(defaultLoginShell);
            defaultLoginShell.exec();
            Terminal.printMOTD();
            Terminal.print(defaultLoginShell.getPrompt());
        }
    }
    
    private static
    Map<String, Class<? extends ExecutableFile>> registerExecutables()
    {
        Map<String, Class<? extends ExecutableFile>> exes = new HashMap<>();
        exes.put("alicia", AliciaCommand.class);
        exes.put("cat", CatCommand.class);
        exes.put("cd", CdCommand.class);
        exes.put("clear", ClearCommand.class);
        exes.put("doa", DOACommand.class);
        exes.put("decode", DecodeCommand.class);
        exes.put("dir", DirCommand.class);
        exes.put("encode", EncodeCommand.class);
        exes.put("foobar", FoobarCommand.class);
        exes.put("hello", HelloCommand.class);
        exes.put("help", HelpCommand.class);
        exes.put("mail", MailCommand.class);
        exes.put("login", LoginCommand.class);
        exes.put("more", MoreCommand.class);
        exes.put("rlogin", RloginCommand.class);
        exes.put("who", WhoCommand.class);
        exes.put("zork", ZorkCommand.class);
        
        return exes;
    }
    
    private static void initUncaughtExceptionHandler()
    {
        Thread.setDefaultUncaughtExceptionHandler(
                new UncaughtExceptionHandler());
    }
    private static void initLookAndFeel()
    {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException
                | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            Logger.error("Failed to initalize system look and feel: %s: %s\n",
                    ex.getClass().getName(), ex.getMessage());
        }
    }
}
