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
        
        int millis = 1000;
        
        while (true) {
            Terminal.println("The ^1quirky ^0quick ^7light ^5brown-colored ^4fox "
                    + "^3hops ^8gracefully ^9over ^7the lazy, tired, ^2old ^6canine.");
            sleep(millis);
            Terminal.print("^1ABC");
            sleep(millis);
            try {
                BufferedImage img = ImageIO.read(new File("res/reznov1.png"));
                Terminal.println(img);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            sleep(millis);
            Terminal.println("01");
            sleep(millis);
            Terminal.println("02");
            sleep(millis);
            Terminal.println("03");
            sleep(millis);
            Terminal.println("04");
            sleep(millis);
            Terminal.println("05");
            sleep(millis);
            Terminal.println("06");
            sleep(millis);
            Terminal.println("07");
            sleep(millis);
            try {
                BufferedImage img = ImageIO.read(new File("res/sp_bop1.png"));
                Terminal.println(img);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            sleep(millis);
            Terminal.print("$");
            sleep(millis);
        }
    }
    
    private static void sleep(int ms)
    {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }
}