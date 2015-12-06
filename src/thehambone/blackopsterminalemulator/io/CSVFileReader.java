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

package thehambone.blackopsterminalemulator.io;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created on Dec 5, 2015.
 *
 * @author thehambone <thehambone93@gmail.com>
 */
public class CSVFileReader
{
    private final BufferedReader reader;
    
    private char fieldSeparator;
    private char commentChar;
    private boolean ignoreWhitespaces;
    private String[] currentLine;
    private int fieldIndex;
    
    public CSVFileReader(String fileName) throws FileNotFoundException
    {
        reader = new BufferedReader(new FileReader(fileName));
        
        fieldSeparator = ',';
        commentChar = 0;
        ignoreWhitespaces = false;
        
        currentLine = new String[0];
        fieldIndex = -1;
    }
    
    public void setFieldSeparatorChar(char c)
    {
        fieldSeparator = c;
    }
    
    public void setCommentChar(char c)
    {
        commentChar = c;
    }
    
    public void ignoreWhitespaces(boolean ignoreWhitespaces)
    {
        this.ignoreWhitespaces = ignoreWhitespaces;
    }
    
    public boolean loadNextLine() throws IOException
    {
        String line = "";
        do {
            line = reader.readLine();
            if (line == null) {
                return false;
            }
        } while (line.isEmpty()
                || line.startsWith(Character.toString(commentChar)));
        
        line = line.replaceAll("\\\\n", "\n");
        
        String[] temp = line.split(Character.toString(fieldSeparator));
//        String[] temp = line.split("(?!\\B\"[^\"]*),(?![^\"]*\"\\B)");
        
        if (ignoreWhitespaces) {
            for (int i = 0; i < temp.length; i++) {
                temp[i] = temp[i].trim();
            }
        }
        
        currentLine = new String[temp.length];
        System.arraycopy(temp, 0, currentLine, 0, temp.length);
        
        fieldIndex = -1;
        
        return true;
    }
    
    public String[] getLineAsArray()
    {
        return new String[0];
    }
    
    public boolean hasNextField()
    {
        return currentLine.length > (fieldIndex  + 1);
    }
    
    public String nextField()
    {
        return currentLine[++fieldIndex].replaceAll("\"", "");
    }
}
