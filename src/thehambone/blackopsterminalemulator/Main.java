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

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import thehambone.blackopsterminalemulator.filesystem.Directory;
import thehambone.blackopsterminalemulator.filesystem.ExecutableFile;
import thehambone.blackopsterminalemulator.filesystem.File;
import thehambone.blackopsterminalemulator.filesystem.FileSystem;
import thehambone.blackopsterminalemulator.filesystem.FileSystemObject;
import thehambone.blackopsterminalemulator.filesystem.HomeDirectory;
import thehambone.blackopsterminalemulator.filesystem.ImageFile;
import thehambone.blackopsterminalemulator.filesystem.SoundFile;
import thehambone.blackopsterminalemulator.filesystem.TextFile;
import thehambone.blackopsterminalemulator.filesystem.command.CatCommand;
import thehambone.blackopsterminalemulator.filesystem.command.CdCommand;
import thehambone.blackopsterminalemulator.filesystem.command.ClearCommand;
import thehambone.blackopsterminalemulator.filesystem.command.DOACommand;
import thehambone.blackopsterminalemulator.filesystem.command.DecodeCommand;
import thehambone.blackopsterminalemulator.filesystem.command.DirCommand;
import thehambone.blackopsterminalemulator.filesystem.command.EncodeCommand;
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
import thehambone.blackopsterminalemulator.io.CSVFileReader;

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
        // TODO: arrow keys, cd command arg parsing, documentation,
        // logging, encode decode alicia mail cmds
        
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
        
        System s = null;
        try {
            CSVFileReader reader = new CSVFileReader("res/systems.csv");
            reader.setCommentChar('#');
            reader.ignoreWhitespaces(true);
            while (reader.loadNextLine()) {
                String systemName = reader.nextField();
                String loginMessage = reader.nextField();
                java.lang.System.out.println("loading system " + systemName);
                s = new System(systemName, loginMessage);
                Terminal.addSystem(s);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        Map<String, Class<? extends ExecutableFile>> executables = new HashMap<>();
        executables.put("3arc", ThreeArcCommand.class);
        executables.put("cat", CatCommand.class);
        executables.put("cd", CdCommand.class);
        executables.put("clear", ClearCommand.class);
        executables.put("doa", DOACommand.class);
        executables.put("decode", DecodeCommand.class);
        executables.put("dir", DirCommand.class);
        executables.put("encode", EncodeCommand.class);
        executables.put("exit", ExitCommand.class);
        executables.put("foobar", FoobarCommand.class);
        executables.put("hello", HelloCommand.class);
        executables.put("help", HelpCommand.class);
        executables.put("login", LoginCommand.class);
        executables.put("more", MoreCommand.class);
        executables.put("rlogin", RloginCommand.class);
        executables.put("who", WhoCommand.class);
        executables.put("zork", ZorkCommand.class);
        
        FileSystem tempFileSystem = new FileSystem(new Directory(0, ""));
        try {
            CSVFileReader reader = new CSVFileReader("res/files.csv");
            reader.setCommentChar('#');
            reader.ignoreWhitespaces(true);
            while (reader.loadNextLine()) {
                int id = Integer.parseInt(reader.nextField());
                String fileName = reader.nextField();
                boolean isHidden = Boolean.parseBoolean(reader.nextField());
                String resourcePath = reader.nextField();
                if (!resourcePath.isEmpty()) {
                    resourcePath = "res/files/" + resourcePath;
                }
                String aliasIDStr = "";
                if (reader.hasNextField()) {
                    aliasIDStr = reader.nextField();
                }
                java.lang.System.out.println("loading file " + fileName + " (" + id + ")");
                File f;
                if (!aliasIDStr.isEmpty()) {
                    f = new File(id, fileName);
                    int aliasID = Integer.parseInt(aliasIDStr);
                    File aliasTarget = (File)tempFileSystem.getFileSystemObject(aliasID);
                    f.markAsAlias(aliasTarget);
                } else if (id > 100 && id < 200) {
                    Class<? extends ExecutableFile> clazz = executables.get(fileName);
                    if (clazz == null) {
                        continue;
                    }
                    Constructor<? extends ExecutableFile> constructor = clazz.getConstructor(int.class);
                    f = constructor.newInstance(id);
                } else if (id > 300 && id < 400) {
                    f = new TextFile(id, fileName, resourcePath);
                } else if (id > 400 && id < 500) {
                    f = new ImageFile(id, fileName, resourcePath);
                } else if (id > 500 && id < 600) {
                    f = new SoundFile(id, fileName, resourcePath);
                } else {
                    f = new File(id, fileName);
                }
                f.setHidden(isHidden);
                tempFileSystem.getRoot().addChild(f);
            }
        } catch (IOException | InstantiationException | IllegalAccessException ex) {
            ex.printStackTrace();
        } catch (NoSuchMethodException | SecurityException ex) {
            ex.printStackTrace();
        } catch (IllegalArgumentException | InvocationTargetException ex) {
            ex.printStackTrace();
        }
        
        try {
            CSVFileReader reader = new CSVFileReader("res/filesystem.csv");
            reader.setCommentChar('#');
            reader.ignoreWhitespaces(true);
            while (reader.loadNextLine()) {
                int id = Integer.parseInt(reader.nextField());
                String systemName = reader.nextField();
                String dirName = reader.nextField();
                int parentID = Integer.parseInt(reader.nextField());
                String files = "";
                if (reader.hasNextField()) {
                    files = reader.nextField();
                }
                java.lang.System.out.println("loading directory " + dirName + " (" + id + ")");
                System system = Terminal.getSystem(systemName);
                Directory dir;
                if (parentID == -1 || (id > 600 && id < 700)) {
                    dir = new Directory(id, dirName);
                    system.setFileSystem(new FileSystem(dir));
                } else if (id > 800 && id < 900) {
                    dir = new HomeDirectory(id, dirName);
                } else {
                    dir = new Directory(id, dirName);
                }
                FileSystemObject parent = system.getFileSystem().getFileSystemObject(parentID);
                if (parent != null) {
                    parent.addChild(dir);
                }
                
                String[] fileIDList = files.split(" ");
                for (String fileIDStr : fileIDList) {
                    if (fileIDStr.isEmpty()) {
                        continue;
                    }
                    int fileID = Integer.parseInt(fileIDStr);
                    File file = (File)tempFileSystem.getFileSystemObject(fileID);
                    if (file == null) {
                        java.lang.System.out.println("null id: " + fileID);
                        continue;
                    }
                    dir.addChild(file);
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        UserAccount u = null;
        try {
            CSVFileReader reader = new CSVFileReader("res/users.csv");
            reader.setCommentChar('#');
            reader.ignoreWhitespaces(true);
            while (reader.loadNextLine()) {
                String systemName = reader.nextField();
                String username = reader.nextField();
                String password = reader.nextField();
                java.lang.System.out.println("loading user " + username + " on system " + systemName);
                int homeDirID = Integer.parseInt(reader.nextField());
                boolean isUnlisted = Boolean.parseBoolean(reader.nextField());
                System system = Terminal.getSystem(systemName);
                HomeDirectory homeDir = (HomeDirectory)system.getFileSystem().getFileSystemObject(homeDirID);
                homeDir.setUnlisted(isUnlisted);
                u = new UserAccount(username, password, homeDir);
                system.addUser(u);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        LoginShell loginShell = new LoginShell(s, u);
        
        Terminal.show();
        Terminal.printMOTD();
        
        while (true) {
            Terminal.pushLoginShell(loginShell);
            loginShell.exec();
            Terminal.printMOTD();
            Terminal.print(loginShell.getPrompt());
        }
    }
}
