/*
 * The MIT License
 *
 * Copyright 2015-2016 thehambone <thehambone93@gmail.com>.
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
 * This class provides a means to read the terminal configuration files (.dat)
 * files. .dat files are basically CSV files, but support comments and ignore
 * whitespaces between cells. Comments must be on their own lines and cannot
 * occur after data on the same line.
 * <p>
 * Created on Dec 5, 2015.
 *
 * @author Wes Hampson <thehambone93@gmail.com>
 */
public class DATFileReader
{
    private final BufferedReader reader;
    
    private char fieldSeparator;
    private char commentChar;
    private boolean ignoreWhitespaces;
    private String[] currentLine;
    private int fieldIndex;
    
    /**
     * Creates a new {@code DATFileReader} object.
     * 
     * @param fileName the file to read
     * @throws FileNotFoundException if the specified file doesn't exist
     */
    public DATFileReader(String fileName) throws FileNotFoundException
    {
        reader = new BufferedReader(new FileReader(fileName));
        
        fieldSeparator = ',';
        commentChar = 0;
        ignoreWhitespaces = false;
        
        currentLine = new String[0];
        fieldIndex = -1;
    }
    
    /**
     * Sets the character used to separate fields. A comma (,) is the default
     * character.
     * 
     * @param c the character to use as the field separator
     */
    public void setFieldSeparatorChar(char c)
    {
        fieldSeparator = c;
    }
    
    /**
     * Sets the character used to denote comments in the file. The hash symbol
     * (#) is the default comment character.
     * 
     * @param c the character to use to denote comments
     */
    public void setCommentChar(char c)
    {
        commentChar = c;
    }
    
    /**
     * Marks whether whitespaces between fields should be ignored.
     * 
     * @param ignoreWhitespaces a boolean value indicating whether to ignore
     *                          whitespaces between fields
     */
    public void ignoreWhitespaces(boolean ignoreWhitespaces)
    {
        this.ignoreWhitespaces = ignoreWhitespaces;
    }
    
    /**
     * Reads the next line in the file.
     * 
     * @return a boolean value indicating whether there exists another line to
     *         be read, {@code true} means a there is another line to be read,
     *         {@code false} indicates that the end of the file has been reached
     * @throws IOException if an I/O error occurs
     */
    public boolean loadNextLine() throws IOException
    {
        // Read the next line in the file, skip comments
        String line = "";
        do {
            line = reader.readLine();
            if (line == null) {
                // End of file reached
                return false;
            }
        } while (line.isEmpty()
                || line.startsWith(Character.toString(commentChar)));
        
        // Replace all instances of the string "\n" with the newline character
        line = line.replaceAll("\\\\n", "\n");
        
        // Tokenize the line by the field separator char
        String[] temp = line.split(Character.toString(fieldSeparator));
        currentLine = new String[temp.length];
        
        // Trim whitespaces if parser is set to do so
        if (ignoreWhitespaces) {
            for (int i = 0; i < temp.length; i++) {
                currentLine[i] = temp[i].trim();
            }
        }
        
        // Reset field index
        fieldIndex = -1;
        
        return true;
    }
    
    /**
     * Checks whether another field is available to be read from the current
     * line.
     * 
     * @return {@code true} if there is another field to read, {@code false} if
     *         the end of the line has been reached
     */
    public boolean hasNextField()
    {
        return currentLine.length > (fieldIndex  + 1);
    }
    
    /**
     * Gets the next field in the line.
     * 
     * @return the next field if one exists, {@code null} if the end of the line
     *         has been reached
     */
    public String nextField()
    {
        if (hasNextField()) {
            return currentLine[++fieldIndex].replaceAll("\"", "");
        } else {
            return null;
        }
    }
}
