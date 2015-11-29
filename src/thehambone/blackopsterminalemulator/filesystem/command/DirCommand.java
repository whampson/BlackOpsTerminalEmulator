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

package thehambone.blackopsterminalemulator.filesystem.command;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;
import thehambone.blackopsterminalemulator.LoginShell;
import thehambone.blackopsterminalemulator.ScreenBuffer;
import thehambone.blackopsterminalemulator.Terminal;
import thehambone.blackopsterminalemulator.filesystem.Directory;
import thehambone.blackopsterminalemulator.filesystem.Executable;
import thehambone.blackopsterminalemulator.filesystem.File;
import thehambone.blackopsterminalemulator.filesystem.FileSystemObject;

/**
 * Created on Nov 28, 2015.
 *
 * @author thehambone <thehambone93@gmail.com>
 */
public class DirCommand extends Executable
{
    public DirCommand()
    {
        super("dir");
    }
    
    private double roundHalfDown(double d)
    {
        return new BigDecimal(d).setScale(0, RoundingMode.HALF_DOWN)
                .doubleValue();
    }
    
    @Override
    public void exec(String[] args)
    {
        LoginShell shell = Terminal.getActiveLoginShell();
        FileSystemObject cd = shell.getCurrentDirectory();
        
        List<FileSystemObject> objs = cd.getChildren();
        
        FileSystemObject o;
        String objName;
        int itemsPrinted = 0;
        for (int i = 0; i < objs.size(); i++) {
            o = objs.get(i);
            objName = o.getName();
            
            if (o instanceof Directory) {
                objName += Directory.FILE_SEPARATOR_CHAR;
            }
            
            if (o instanceof File && ((File)o).isHidden()) {
                continue;
            }
            
            Terminal.print(' ');
            Terminal.print(objName);
            itemsPrinted++;
            
            if (itemsPrinted % 4 == 0) {
                Terminal.println();
            } else {
                int tabCount = 5 - (int)roundHalfDown((double)objName.length() / ScreenBuffer.TAB_LENGTH);
                for (int j = 0; j < tabCount; j++) {
                    Terminal.print('\t');
                }
            }
        }
        
        Terminal.println();
    }
}