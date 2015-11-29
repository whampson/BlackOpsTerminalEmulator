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

import thehambone.blackopsterminalemulator.filesystem.File;
import java.util.ArrayList;
import java.util.List;
import thehambone.blackopsterminalemulator.filesystem.Directory;
import thehambone.blackopsterminalemulator.filesystem.FileSystem;
import thehambone.blackopsterminalemulator.filesystem.HomeDirectory;
import thehambone.blackopsterminalemulator.filesystem.command.CdCommand;
import thehambone.blackopsterminalemulator.filesystem.command.DirCommand;
import thehambone.blackopsterminalemulator.filesystem.Executable;
import thehambone.blackopsterminalemulator.filesystem.command.ClearCommand;
import thehambone.blackopsterminalemulator.filesystem.command.DOACommand;
import thehambone.blackopsterminalemulator.filesystem.command.ExitCommand;
import thehambone.blackopsterminalemulator.filesystem.command.FoobarCommand;
import thehambone.blackopsterminalemulator.filesystem.command.HelloCommand;
import thehambone.blackopsterminalemulator.filesystem.command.HelpCommand;
import thehambone.blackopsterminalemulator.filesystem.command.LoginCommand;
import thehambone.blackopsterminalemulator.filesystem.command.MoreCommand;
import thehambone.blackopsterminalemulator.filesystem.command.RloginCommand;
import thehambone.blackopsterminalemulator.filesystem.command.ThreeArcCommand;
import thehambone.blackopsterminalemulator.filesystem.command.WhoCommand;
import thehambone.blackopsterminalemulator.filesystem.command.ZorkCommand;

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
    
    /**
     * Program entry point.
     * 
     * @param args command-line arguments
     */
    public static void main(String[] args)
    {
        // TODO: caps lock, arrow keys, cd command arg parsing, documentation,
        // "more" should not do anything if newline typed once
        
        String title = PROGRAM_TITLE + " - " + PROGRAM_VERSION;
        Terminal.setTitle(title);
        
        String ciaLoginMessage = "Central Intelligence Agency Data system\n\n"
                + "Unauthorized use of this system is against the law.\n\n"
                + "Security Privileges Required";
        String dreamlandLoginMessage = "Dreamland Server 12\n\n\n"
                + "***MJ12 Clearance Required***";
        
        Terminal.setMOTD(ciaLoginMessage + "\n"
                + "USER:amason\n"
                + "PASSWORD:********\n");
        
        Directory root = new Directory("");
        Directory bin = new Directory("bin");
        Directory home = new Directory("home");
        HomeDirectory amason = new HomeDirectory("amason");
        HomeDirectory asmith = new HomeDirectory("asmith");
        
        root.addChild(bin);
        root.addChild(home);
        home.addChild(amason);
        home.addChild(asmith);
        
        Executable cd = new CdCommand();
        Executable help = new HelpCommand();
        Executable dir = new DirCommand();
        Executable login = new LoginCommand();
        Executable exit = new ExitCommand();
        Executable clear = new ClearCommand();
        Executable foobar = new FoobarCommand();
        Executable hello = new HelloCommand();
        Executable doa = new DOACommand();
        Executable threearc = new ThreeArcCommand();
        Executable zork = new ZorkCommand();
        Executable more = new MoreCommand();
        Executable who = new WhoCommand();
        Executable rlogin = new RloginCommand();
        File ls = new File("ls");
        ls.markAsAlias(dir);
        File cls = new File("cls");
        cls.markAsAlias(clear);
        bin.addChild(cd);
        bin.addChild(help);
        bin.addChild(dir);
        bin.addChild(ls);
        bin.addChild(login);
        bin.addChild(exit);
        bin.addChild(clear);
        bin.addChild(cls);
        bin.addChild(foobar);
        bin.addChild(hello);
        bin.addChild(doa);
        bin.addChild(threearc);
        bin.addChild(zork);
        bin.addChild(more);
        bin.addChild(who);
        bin.addChild(rlogin);
        
        File motd = new File("_motd.txt", true);
        root.addChild(motd);
        
        Directory root2 = new Directory("");
        Directory home2 = new Directory("home");
        HomeDirectory vbush = new HomeDirectory("vbush");
        
        root2.addChild(bin);
        root2.addChild(home2);
        home2.addChild(vbush);
        
        FileSystem ciaFileSystem = new FileSystem(root);
        FileSystem dreamlandFileSystem = new FileSystem(root2);
        
        List<File> files = new ArrayList<>();
        List<User> ciaUsers = new ArrayList<>();
        List<User> dreamlandUsers = new ArrayList<>();
        User userAmason = new User("amason", "password", amason, files);
        ciaUsers.add(new User("asmith", "roxy", asmith, files));
        ciaUsers.add(userAmason);
        dreamlandUsers.add(new User("vbush", "majestic1", vbush, files));
        Server cia = new Server("cia", ciaLoginMessage, ciaUsers, files, ciaFileSystem, bin);
        Server dreamland = new Server("dreamland", dreamlandLoginMessage, dreamlandUsers, files, dreamlandFileSystem, bin);
        Server.addServer(cia);
        Server.addServer(dreamland);
        Terminal.show();
        
        LoginShell loginShell = new LoginShell(cia, userAmason);
        Terminal.getLoginShellStack().push(loginShell);
        loginShell.exec();
    }
}