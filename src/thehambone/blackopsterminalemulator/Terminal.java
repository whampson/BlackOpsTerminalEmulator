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

import java.awt.Font;
import java.awt.image.BufferedImage;
import java.util.concurrent.TimeUnit;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * Created on Nov 18, 2015.
 *
 * @author thehambone <thehambone93@gmail.com>
 */
public final class Terminal
{
    private static final Screen.ScreenColor BACKGROUND_DEFAULT
            = Screen.ScreenColor.BLACK;
    private static final Screen.ScreenColor FOREGROUND_DEFAULT
            = Screen.ScreenColor.WHITE;
    
    private static final Font FONT_DEFAULT = new Font("Monospaced", 0, 13);
    
    private static final int COLUMNS_DEFAULT = 80;
    private static final int ROWS_DEFAULT = 27;
    private static final int CURSOR_BLINK_RATE = 300;
    
    private static Terminal instance;
    
    public static Terminal newTerminal()
    {
        if (instance == null) {
            instance = new Terminal();
        }
        
        return instance;
    }
    public static void print(String s)
    {
        instance.printString(s);
    }
    
    public static void println()
    {
        print("\n");
    }
    
    public static void println(String s)
    {
        print(s + "\n");
    }
    
    public static void println(BufferedImage img)
    {
        instance.printImage(img);
        println();
    }
    
    private final Screen screen;
    
    private Terminal()
    {
        screen = new Screen(COLUMNS_DEFAULT, ROWS_DEFAULT,
                BACKGROUND_DEFAULT, FOREGROUND_DEFAULT,
                FONT_DEFAULT, CURSOR_BLINK_RATE);
    }
    
    public Screen getScreen()
    {
        return screen;
    }
    
    public void show(final String title)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                createAndShow(title);
            }
        });
    }
    
    private void createAndShow(String title)
    {
        JComponent screenComponent = screen.getComponent();
        JFrame frame = new JFrame(title);
        
        frame.add(screenComponent);
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setSize(screenComponent.getPreferredSize());
        frame.setLocationRelativeTo(null);      // Center frame on screen
        frame.setVisible(true);
        
    }
    
    private void printString(String s)
    {
        for (int i = 0; i < s.length(); i++) {            
            screen.print(s.charAt(i));
            
            // Sleep 1ms to simulate output on a terminal with a low baud rate
            try {
                TimeUnit.MILLISECONDS.sleep(1);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
    
    private void printImage(BufferedImage image) {
        screen.printImage(image);
    }
}