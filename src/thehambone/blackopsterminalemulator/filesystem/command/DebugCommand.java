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

package thehambone.blackopsterminalemulator.filesystem.command;

import java.awt.Font;
import java.io.IOException;
import thehambone.blackopsterminalemulator.Main;
import thehambone.blackopsterminalemulator.Screen;
import thehambone.blackopsterminalemulator.Terminal;
import thehambone.blackopsterminalemulator.filesystem.ExecutableFile;
import thehambone.blackopsterminalemulator.io.Logger;
import thehambone.blackopsterminalemulator.io.ResourceLoader;

/**
 * The "debug" command.
 * <p>
 * This is a special command used for carrying out operations useful for
 * debugging purposes. This command does not exist in the original Terminal.
 * <p>
 * Created on Dec 25, 2015.
 *
 * @author Wes Hampson
 */
public class DebugCommand extends ExecutableFile
{
    /**
     * Creates a new instance of the {@code DebugCommand} class.
     * 
     * @param id the filesystem object id
     */
    public DebugCommand(int id)
    {
        super(id, "debug");
    }
    
    /*
     * Show all possible debug commands.
     */
    private void printUsage()
    {
        Terminal.println("Usage: debug <options>\n");
        Terminal.println("Options:");
        Terminal.println("\tbg <colorID>\t\t\t\t"
                + "sets the background color");
        Terminal.println("\tfg <colorID>\t\t\t\t"
                + "sets the foreground color");
        Terminal.println();
        Terminal.println("\tcrashdump\t\t\t\t\t"
                + "creates a crash dump file");
        Terminal.println("\trtexception [message]\t\t"
                + "creates a fake RuntimeException");
        Terminal.println();
        Terminal.println("\treloadfs\t\t\t\t\t"
                + "reloads file system config");
        Terminal.println();
        Terminal.println("\tfont <name>\t\t\t\t\t"
                + "sets the terminal font");
    }
    
    /*
     * Throws a dummy RuntimeException.
     */
    private void causeRuntimeException(String message)
    {
        throw new RuntimeException(message);
    }
    
    /*
     * Writes a crash report to a file without actually crashing the program.
     */
    private void createCrashDump()
    {
        String fileName = "";
        try {
            fileName = Logger.generateCrashDump();
        } catch (IOException ex) {
            Logger.error("Failed to write crash dump");
            Logger.stackTrace(ex);
            Terminal.println("Failed to write crash dump ("
                    + ex.getClass().getSimpleName() + ": " + ex.getMessage());
        }
        
        if (!fileName.isEmpty()) {
            Terminal.println("Crash dump saved to " + fileName + ".");
        }
    }
    
    /*
     * Lists all available screen colors with their respective IDs.
     */
    private void listColors()
    {
        for (Screen.ScreenColor color : Screen.ScreenColor.values()) {
            Terminal.println(color.ordinal() + ": " + color.name());
        }
    }
    
    /*
     * Reloads the terminal file system configuration.
     */
    private void reloadFileSystem()
    {
        ResourceLoader.loadFileSystemConfiguration(Main.registerExecutables());
        Terminal.println("File system reloaded");
    }
    
    /*
     * Sets the background color of the terminal screen.
     */
    private void setBG(int colorID)
    {
        if (colorID > -1 && colorID < Screen.ScreenColor.values().length) {
            Terminal.getScreen().setBackground(
                    Screen.ScreenColor.values()[colorID]);
        }
    }
    
    /*
     * Sets the foreground color of the terminal screen.
     */
    private void setFG(int colorID)
    {
        if (colorID > -1 && colorID < Screen.ScreenColor.values().length) {
            Terminal.getScreen().setForeground(
                    Screen.ScreenColor.values()[colorID]);
        }
    }
    
    @Override
    public void exec(String[] args)
    {
        if (args.length == 0) {
            printUsage();
            return;
        }
        
        switch (args[0]) {
            case "bg":
                if (args.length - 1 < 1) {
                    Terminal.println("Usage: bg <colorID>");
                    Terminal.println("Color IDs:");
                    listColors();
                    return;
                }
                int bgColorID = Integer.parseInt(args[1]);
                setBG(bgColorID);
                break;
                
            case "crashdump":
                createCrashDump();
                break;
                
            case "fg":
                if (args.length - 1 < 1) {
                    Terminal.println("Usage: fg <colorID>");
                    Terminal.println("Color IDs:");
                    listColors();
                    return;
                }
                int fgColorID = Integer.parseInt(args[1]);
                setFG(fgColorID);
                break;
                
            case "font":
                String fontName = "";
                for (int i = 0; i < args.length - 1; i++) {
                    fontName += args[i + 1] + " ";
                }
                fontName = fontName.trim();
                Terminal.println("Setting font to " + fontName + "...");
                Font f = new Font(fontName, Font.PLAIN, 13);
                Terminal.getScreen().setFont(f);
                break;
                
            case "reloadfs":
                reloadFileSystem();
                break;
                
            case "rtexception":
                String message = "";
                for (int i = 0; i < args.length - 1; i++) {
                    message += args[i + 1] + " ";
                }
                causeRuntimeException(message.trim());
                break;
                
            default:
                Terminal.println("Error:  Invalid Argument");
        }
    }
}
