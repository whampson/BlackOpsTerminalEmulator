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
import thehambone.blackopsterminalemulator.filesystem.command.HelpCommand;

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
        String title = PROGRAM_TITLE + " - " + PROGRAM_VERSION;
        Terminal.setTitle(title);
        Terminal.setMOTD("Central Intelligence Agency Data system\n\n"
                + "Unauthorized use of this system is against the law.\n\n"
                + "Security Privileges Required\n"
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
        File ls = new File("ls");
        ls.markAsAlias(dir);
        bin.addChild(cd);
        bin.addChild(help);
        bin.addChild(dir);
        bin.addChild(ls);
        
        File motd = new File("_motd.txt", true);
        root.addChild(motd);
        
        FileSystem ciaFileSystem = new FileSystem(root);
        
        List<User> users = new ArrayList<>();
        List<File> files = new ArrayList<>();
        users.add(new User("amason", "password", amason, files));
        users.add(new User("asmith", "roxy", asmith, files));
        Server cia = new Server("cia", "", users, files, ciaFileSystem, bin);
        Server.addServer(cia);
        Terminal.show();
        Terminal.login("cia", "amason", "password");
    }
}