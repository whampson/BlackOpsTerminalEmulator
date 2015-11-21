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

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import javax.imageio.ImageIO;

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
            = "1.0-alpha (dev build)";
    
    /**
     * Program entry point.
     * 
     * @param args command-line arguments
     */
    public static void main(String[] args)
    {
        Terminal terminal = Terminal.newTerminal();
        terminal.show(PROGRAM_TITLE + " - " + PROGRAM_VERSION);
        
//        try {
//            Terminal.print("***** Line scrolling demo *****");
//            terminal.getScreen().setCursorPosition(0, 26);
//            Terminal.print("$cat wolfinchains.pic");
//            BufferedImage img = ImageIO.read(new File("res/reznov2.png"));
//            Thread.sleep(2500);
//            for (int i = 0; i < 20; i++) {
//                Terminal.println();
//            }
//            terminal.getScreen().setCursorPosition(0, 7);
//            Terminal.println(img);
//            Thread.sleep(1000);
//            for (int i = 0; i < 27; i++) {
//                Terminal.println(Integer.toString(i));
//                Thread.sleep(250);
//            }
//            Terminal.println("This is some long text to test how the screen "
//                    + "handles text that extends beyond the number of "
//                    + "available columns.");
//            Terminal.print("$^1");
//        } catch (IOException | InterruptedException ex) {
//            throw new RuntimeException(ex);
//        }
    }
}