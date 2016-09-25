/*
 * The MIT License
 *
 * Copyright 2015-2016 Wes Hampson <thehambone93@gmail.com>.
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

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JOptionPane;
import thehambone.blackopsterminalemulator.Mail;
import thehambone.blackopsterminalemulator.Server;
import thehambone.blackopsterminalemulator.Terminal;
import thehambone.blackopsterminalemulator.UserAccount;
import thehambone.blackopsterminalemulator.filesystem.Directory;
import thehambone.blackopsterminalemulator.filesystem.ExecutableFile;
import thehambone.blackopsterminalemulator.filesystem.File;
import thehambone.blackopsterminalemulator.filesystem.FileSystem;
import thehambone.blackopsterminalemulator.filesystem.FileSystemObject;
import thehambone.blackopsterminalemulator.filesystem.HomeDirectory;
import thehambone.blackopsterminalemulator.filesystem.ImageFile;
import thehambone.blackopsterminalemulator.filesystem.SoundFile;
import thehambone.blackopsterminalemulator.filesystem.TextFile;

/**
 * This class handles the loading of supplementary files.
 * <p>
 * Created on Dec 18, 2015.
 *
 * @author Wes Hampson
 */
public class ResourceLoader
{
    // Configuration file paths
    private static final String DEFAULT_DATA_DIR = "data";
    private static final String CFG_FILES = "files.dat";
    private static final String CFG_FILESYSTEM = "filesystem.dat";
    private static final String CFG_MAIL = "mail.dat";
    private static final String CFG_MOTD = "_motd";
    private static final String CFG_SERVERS = "systems.dat";
    private static final String CFG_USERS = "users.dat";
    
    // Resource directory paths
    private static final String TEXT_FILE_PATH = "txt/";
    private static final String IMAGE_FILE_PATH = "img/";
    private static final String SOUND_FILE_PATH = "aud/";
    
    private static String dataDir = DEFAULT_DATA_DIR;
    
    /**
     * Sets the directory from which to load configuration files.
     * <p>
     * The default directory is "data/".
     * 
     * @param dir the new data directory
     */
    public static void setDataDirectory(String dir)
    {
        dataDir = dir;
    }
    
    /**
     * Loads a text resource.
     * 
     * @param resourceName the name of the resource to load
     * @return the text data
     */
    public static String loadTextFile(String resourceName)
    {
        String textData = "";
        
        try {
            String txtPath = dataDir + "/" + TEXT_FILE_PATH;
            String resourcePath = txtPath + resourceName;
            BufferedReader reader
                    = new BufferedReader(new FileReader(resourcePath));
            
            String line;
            while ((line = reader.readLine()) != null) {
                textData += line + "\n";
            }
        } catch (IOException ex) {
            Logger.stackTrace(ex);
        }
        
        return textData;
    }
    
    /**
     * Loads an image resource.
     * 
     * @param resourceName the name of the resource to load
     * @return the image data
     */
    public static BufferedImage loadImageFile(String resourceName)
    {
        BufferedImage image = null;
        
        try {
            String imgPath = dataDir + "/" + IMAGE_FILE_PATH;
            String resourcePath = imgPath + resourceName;
            image = ImageIO.read(new FileInputStream(resourcePath));
        } catch (IOException ex) {
            Logger.stackTrace(ex);
        }
        
        return image;
    }
    
    public static Image loadEmbeddedImage(String resourcePath)
    {
        Image image = null;
        
        try {
            InputStream stream = new FileInputStream(resourcePath);
            image = ImageIO.read(stream);
        } catch (IOException ex) {
            Logger.stackTrace(ex);
        }
        
        return image;
    }
    
    /**
     * Loads an audio resource.
     * 
     * @param resourceName the name of the resource to load
     * @return the audio stream
     */
    public static AudioInputStream loadSoundFile(String resourceName)
    {
        AudioInputStream stream = null;
        
        try {
            String audPath = dataDir + "/" + SOUND_FILE_PATH;
            String resourcePath = audPath + resourceName;
            stream = AudioSystem
                    .getAudioInputStream(new java.io.File(resourcePath));
        } catch (UnsupportedAudioFileException | IOException ex) {
            Logger.stackTrace(ex);
        }
        
        return stream;
    }
    
    /**
     * Loads the filesystem configuration.
     * 
     * @param exes a Map containing classes representing executable files and
     *             their corresponding names
     */
    public static void loadFileSystemConfiguration(
            Map<String, Class<? extends ExecutableFile>> exes)
    {
        /* Load files into temporary filesystem. The temporary filesytem is used
           to access files while the actual filesystem is being built.
        */
        FileSystem tempFileSystem = loadFiles(exes);
        
        try {
            String fsCfgPath = dataDir + "/" + CFG_FILESYSTEM;
            DATFileReader reader = new DATFileReader(fsCfgPath);
            reader.setCommentChar('#');
            reader.ignoreWhitespaces(true);
            
            // Read file line by line
            while (reader.loadNextLine()) {
                // Read data from current line
                int id = Integer.parseInt(reader.nextField());
                String systemName = reader.nextField();
                String dirName = reader.nextField();
                int parentID = Integer.parseInt(reader.nextField());
                String files = "";
                if (reader.hasNextField()) {
                    files = reader.nextField();
                }
                
                Server system = Terminal.getServer(systemName);
                
                Directory dir;
                if (parentID == -1 || (id > 600 && id < 700)) {
                    // Load directory, set it as root on the current system
                    dir = new Directory(id, dirName);
                    system.setFileSystem(new FileSystem(dir));
                } else if (id > 800 && id < 900) {
                    // Load directory as as user's home directory
                    dir = new HomeDirectory(id, dirName);
                } else {
                    // Load generic directory
                    dir = new Directory(id, dirName);
                }
                
                /* Place the directory in the correct location in the filesystem
                   tree
                */
                FileSystemObject parent = system.getFileSystem()
                        .getFileSystemObject(parentID);
                if (parent != null) {
                    parent.addChild(dir);
                }
                
                // Populate directory with files (if it has any)
                String[] fileIDList = files.split(" ");
                for (String fileIDStr : fileIDList) {
                    if (fileIDStr.isEmpty()) {
                        continue;
                    }
                    int fileID = Integer.parseInt(fileIDStr);
                    
                    // Get file from temporary filesystem based on file ID
                    File file = (File)tempFileSystem
                            .getFileSystemObject(fileID);
                    if (file == null) {
                        Logger.error("Unresolved file ID: %d "
                                + "(dir: %s, system: %s)\n",
                                fileID, dirName, systemName);
                        continue;
                    }
                    
                    // Place the file in the directory
                    dir.addChild(file);
                }
                
                Logger.info("Loaded directory: %s (system: %s, id: ID)\n",
                        dirName.isEmpty() ? "<no name, root?>" : dirName,
                        systemName, id);
            }
        } catch (IOException ex) {
            Logger.stackTrace(ex);
            String msg = "Failed to load filesystem configuration";
            throw new RuntimeException(msg, ex);
        }
    }
    
    /*
     * Loads the list of files.
     */
    private static
    FileSystem loadFiles(Map<String, Class<? extends ExecutableFile>> exes)
    {
        FileSystem tempFileSystem = new FileSystem(new Directory(0, ""));
        try {
            String fCfgPath = dataDir + "/" + CFG_FILES;
            DATFileReader reader = new DATFileReader(fCfgPath);
            reader.setCommentChar('#');
            reader.ignoreWhitespaces(true);
            
            // Read file line by line
            while (reader.loadNextLine()) {
                // Read data from current line
                int id = Integer.parseInt(reader.nextField());
                String fileName = reader.nextField();
                boolean isHidden = Boolean.parseBoolean(reader.nextField());
                String resourceName = reader.nextField();
                String aliasIDStr = "";
                if (reader.hasNextField()) {
                    aliasIDStr = reader.nextField();
                }
                
                File f;
                if (!aliasIDStr.isEmpty()) {
                    // Resolve alias target if the file is an alias
                    f = new File(id, fileName);
                    int aliasID = Integer.parseInt(aliasIDStr);
                    File aliasTarget = (File)tempFileSystem
                            .getFileSystemObject(aliasID);
                    f.markAsAlias(aliasTarget);
                } else if (id > 100 && id < 200) {
                    // Match file to an ExecurableFile subclass
                    Class<? extends ExecutableFile> clazz = exes.get(fileName);
                    if (clazz == null) {
                        Logger.error("Unresolved executable: %s (id: %d)\n",
                                fileName, id);
                        continue;
                    }
                    
                    // Create instance of subclass
                    Constructor<? extends ExecutableFile> constructor
                            = clazz.getConstructor(int.class);
                    f = constructor.newInstance(id);
                } else if (id > 300 && id < 400) {
                    // Load file as a text file
                    f = new TextFile(id, fileName, resourceName);
                } else if (id > 400 && id < 500) {
                    // Load file as an image file
                    f = new ImageFile(id, fileName, resourceName);
                } else if (id > 500 && id < 600) {
                    // Load file as a sound file
                    f = new SoundFile(id, fileName, resourceName);
                } else {
                    // Load generic file
                    f = new File(id, fileName);
                }
                
                f.setHidden(isHidden);
                
                // Add file to temporay filesystem
                tempFileSystem.getRoot().addChild(f);
                
                Logger.info("Loaded file: %s (id: %d%s)\n", fileName, id,
                        resourceName.isEmpty()
                                ? "" : ", resource: " + resourceName);
            }
        } catch (IOException ex) {
            Logger.stackTrace(ex);
            String msg = "Failed to load filesystem configuration";
            throw new RuntimeException(msg, ex);
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new RuntimeException(ex);
        } catch (NoSuchMethodException | SecurityException ex) {
            throw new RuntimeException(ex);
        } catch (IllegalArgumentException | InvocationTargetException ex) {
            throw new RuntimeException(ex);
        }
        
        return tempFileSystem;
    }
    
    /**
     * Loads the mail configuration.
     */
    public static void loadMailConfiguration()
    {
        try {
            String mCfgPath = dataDir + "/" + CFG_MAIL;
            DATFileReader reader = new DATFileReader(mCfgPath);
            reader.setCommentChar('#');
            reader.ignoreWhitespaces(true);
            
            // Read file line by line
            while (reader.loadNextLine()) {
                // Read data from current line
                String systemName = reader.nextField();
                String userName = reader.nextField();
                String sender = reader.nextField();
                String date = reader.nextField();
                String subject = reader.nextField();
                String resourceName = reader.nextField();
                
                Server system = Terminal.getServer(systemName);
                UserAccount user = system.getUser(userName);
                Mail m = new Mail(sender, date, subject, resourceName);
                user.getMailbox().addMail(m);
                
                Logger.info("Loaded mail: subject: %s (user: %s, system: %s%s)\n",
                        subject.isEmpty() ? "<no subject>" : subject,
                        userName, systemName,
                        resourceName.isEmpty()
                                ? "" : ", resource: " + resourceName);
            }
        } catch (IOException ex) {
            Logger.stackTrace(ex);
            String msg = "Failed to load mail configuration";
            throw new RuntimeException(msg, ex);
        }
    }
    
    /**
     * Loads server configuration.
     * 
     * @return the last loaded server
     */
    public static Server loadServerConfiguration()
    {
        Server s = null;
        
        try {
            String sCfgPath = dataDir + "/" + CFG_SERVERS;
            DATFileReader reader = new DATFileReader(sCfgPath);
            reader.setCommentChar('#');
            reader.ignoreWhitespaces(true);
            
            // Read file line by line
            while (reader.loadNextLine()) {
                // Read data from current line
                String serverName = reader.nextField();
                String loginMessage = reader.nextField();
                s = new Server(serverName, loginMessage);
                Terminal.addServer(s);
                
                Logger.info("Loaded server: %s\n", serverName);
            }
            
        } catch (IOException ex) {
            Logger.stackTrace(ex);
            String msg = "Failed to load server configuration";
            throw new RuntimeException(msg, ex);
        }
        
        return s;
    }
    
    /**
     * Loads user configuration
     * 
     * @return the last loaded user account
     */
    public static UserAccount loadUserConfiguration()
    {
        UserAccount u = null;
        
        try {
            String uCfgPath = dataDir + "/" + CFG_USERS;
            DATFileReader reader = new DATFileReader(uCfgPath);
            reader.setCommentChar('#');
            reader.ignoreWhitespaces(true);
            
            // Read file line by line
            while (reader.loadNextLine()) {
                // Read data from current line
                String systemName = reader.nextField();
                String username = reader.nextField();
                String password = reader.nextField();
                int homeDirID = Integer.parseInt(reader.nextField());
                boolean isUnlisted = Boolean.parseBoolean(reader.nextField());
                
                Server system = Terminal.getServer(systemName);
                HomeDirectory homeDir = (HomeDirectory)system.getFileSystem()
                        .getFileSystemObject(homeDirID);
                homeDir.setUnlisted(isUnlisted);
                u = new UserAccount(username, password, homeDir);
                system.addUser(u);
                
                Logger.info("Loaded user: %s (system: %s)\n",
                        username, systemName);
            }
        } catch (IOException ex) {
            Logger.stackTrace(ex);
            String msg = "Failed to load user configuration";
            throw new RuntimeException(msg, ex);
        }
        
        return u;
    }
    
    /**
     * Loads the message of the day (MOTD). This is displayed when the terminal
     * is first opened.
     */
    public static void loadMOTD()
    {
        String motd = "";
        String line;
        
        try {
            String motdCfgPath = dataDir + "/" + CFG_MOTD;
            BufferedReader fileReader
                    = new BufferedReader(new FileReader(motdCfgPath));
            
            while ((line = fileReader.readLine()) != null) {
                motd += line + "\n";
            }
            
            Terminal.setMOTD(motd);
            Logger.info("Loaded MOTD");
        } catch (IOException ex) {
            Logger.stackTrace(ex);
            String msg = "Failed to load motd";
            throw new RuntimeException(msg, ex);
        }
    }
}
