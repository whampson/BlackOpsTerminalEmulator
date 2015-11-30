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
import thehambone.blackopsterminalemulator.filesystem.ExecutableFile;
import thehambone.blackopsterminalemulator.filesystem.ImageFile;
import thehambone.blackopsterminalemulator.filesystem.PrintableFile;
import thehambone.blackopsterminalemulator.filesystem.SoundFile;
import thehambone.blackopsterminalemulator.filesystem.TextFile;
import thehambone.blackopsterminalemulator.filesystem.command.CatCommand;
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
        // logging
        
        String title = PROGRAM_TITLE + " - " + PROGRAM_VERSION;
        Terminal.setTitle(title);
        
        // TEMPORARY CODE FOR TESTING
        // ALL CONTENT WILL BE DEFINED EXTERNALLY
        String ciaLoginMessage = "Central Intelligence Agency Data system\n\n"
                + "Unauthorized use of this system is against the law.\n\n"
                + "Security Privileges Required";
        String dreamlandLoginMessage = "Dreamland Server 12\n\n\n"
                + "***MJ12 Clearance Required***";
        
        Terminal.setMOTD(ciaLoginMessage + "\n"
                + "USER:amason\n"
                + "PASSWORD:********\n");
        
        Directory root = new Directory(1, "");
        Directory bin = new Directory(2, "bin");
        Directory home = new Directory(3, "home");
        HomeDirectory amason = new HomeDirectory(4, "amason");
        HomeDirectory asmith = new HomeDirectory(5, "asmith");
        HomeDirectory hkissinger = new HomeDirectory(6, "hkissinger", true);
        HomeDirectory jhudson = new HomeDirectory(7, "jhudson");
        HomeDirectory vbush = new HomeDirectory(33, "vbush");
        
        root.addChild(bin);
        root.addChild(home);
        home.addChild(amason);
        home.addChild(asmith);
        home.addChild(hkissinger);
        home.addChild(jhudson);
        home.addChild(vbush);
        
        ExecutableFile cd = new CdCommand();
        ExecutableFile help = new HelpCommand();
        ExecutableFile dir = new DirCommand();
        ExecutableFile login = new LoginCommand();
        ExecutableFile exit = new ExitCommand();
        ExecutableFile clear = new ClearCommand();
        ExecutableFile foobar = new FoobarCommand();
        ExecutableFile hello = new HelloCommand();
        ExecutableFile doa = new DOACommand();
        ExecutableFile threearc = new ThreeArcCommand();
        ExecutableFile zork = new ZorkCommand();
        ExecutableFile more = new MoreCommand();
        ExecutableFile who = new WhoCommand();
        ExecutableFile rlogin = new RloginCommand();
        ExecutableFile cat = new CatCommand();
        File ls = new File(8, "ls");
        ls.markAsAlias(dir);
        File cls = new File(9, "cls");
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
        bin.addChild(cat);
        
        File motd = new File(19, "_motd.txt", true);
        PrintableFile battleberlin = new TextFile(11, "BattleBerlin.txt", "res/files/bb77b895.txt");
        PrintableFile gknovamemo = new TextFile(11, "GK-NovaMemo.txt", "res/files/c5be8bb9.txt");
        PrintableFile thewolf = new ImageFile(20, "thewolf.pic", "res/files/reznov1.png");
        PrintableFile barhavana = new ImageFile(20, "barhavana.pic", "res/files/sp_bop1.png");
        PrintableFile anvil = new SoundFile(21, "anvil.snd", "res/files/mus_anvil_trippy.wav");
        PrintableFile reznov = new SoundFile(23, "reznov.snd", "res/files/mus_rapture_intro_radio.wav");
        PrintableFile doa1 = new ImageFile(34, "doa1.pic", "res/files/doa1.png");
        PrintableFile doa2 = new ImageFile(34, "doa2.pic", "res/files/doa2.png");
        PrintableFile doa3 = new ImageFile(34, "doa3.pic", "res/files/doa3.png");
        File masonbio = new File(12, "MasonBio.txt");
        root.addChild(motd);
        asmith.addChild(battleberlin);
        asmith.addChild(masonbio);
        amason.addChild(battleberlin);
        amason.addChild(thewolf);
        amason.addChild(anvil);
        amason.addChild(reznov);
        amason.addChild(barhavana);
        jhudson.addChild(gknovamemo);
        amason.addChild(gknovamemo);
        vbush.addChild(doa1);
        vbush.addChild(doa2);
        vbush.addChild(doa3);

        Directory root2 = new Directory(13, "");
        Directory home2 = new Directory(14, "home");
        HomeDirectory vbush2 = new HomeDirectory(15, "vbush");
        
        root2.addChild(bin);
        root2.addChild(home2);
        home2.addChild(vbush2);
        
        FileSystem ciaFileSystem = new FileSystem(root);
        FileSystem dreamlandFileSystem = new FileSystem(root2);
        
        List<User> ciaUsers = new ArrayList<>();
        List<User> dreamlandUsers = new ArrayList<>();
        User userAmason = new User("amason", "password", amason);
        ciaUsers.add(new User("vbush", "manhattan", vbush));
        ciaUsers.add(new User("jhudson", "bryant1950", jhudson));
        ciaUsers.add(new User("hkissinger", "", hkissinger));
        ciaUsers.add(new User("asmith", "roxy", asmith));
        ciaUsers.add(userAmason);
        dreamlandUsers.add(new User("vbush", "majestic1", vbush2));
        Server cia = new Server("CIA", ciaLoginMessage, ciaUsers, ciaFileSystem, bin);
        Server dreamland = new Server("Dreamland", dreamlandLoginMessage, dreamlandUsers, dreamlandFileSystem, bin);
        Server dod = new Server("DoD", "", new ArrayList<User>(), null, bin);
        Server derriese = new Server("DerRiese", "", new ArrayList<User>(), null, bin);
        Server.addServer(cia);
        Server.addServer(dreamland);
        Server.addServer(dod);
        Server.addServer(derriese);
        Terminal.show();
        
        LoginShell loginShell = new LoginShell(cia, userAmason);
        Terminal.getLoginShellStack().push(loginShell);
        loginShell.exec();
    }
}