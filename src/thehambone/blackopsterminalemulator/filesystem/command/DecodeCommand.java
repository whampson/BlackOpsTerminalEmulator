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

import java.util.HashMap;
import java.util.Map;
import thehambone.blackopsterminalemulator.Terminal;
import thehambone.blackopsterminalemulator.filesystem.ExecutableFile;

/**
 * The "decode" command.
 * <p>
 * This command decodes an encrypted string.
 * <p>
 * Created on Dec 6, 2015.
 *
 * @author thehambone <thehambone93@gmail.com>
 */
public class DecodeCommand extends ExecutableFile
{
    // TODO: find a way to decode mathematically
    private static final Map<String, String> CIPHER_TABLE = new HashMap<>();
    static
    {
        CIPHER_TABLE.put("A", "IM");
        CIPHER_TABLE.put("B", "DKM");
        CIPHER_TABLE.put("C", "DJM");
        CIPHER_TABLE.put("D", "DG");
        CIPHER_TABLE.put("E", "G");
        CIPHER_TABLE.put("F", "EJM");
        CIPHER_TABLE.put("G", "FG");
        CIPHER_TABLE.put("H", "EKM");
        CIPHER_TABLE.put("I", "KM");
        CIPHER_TABLE.put("J", "CLM");
        CIPHER_TABLE.put("K", "DH");
        CIPHER_TABLE.put("L", "CAG");
        CIPHER_TABLE.put("M", "LM");
        CIPHER_TABLE.put("N", "JM");
        CIPHER_TABLE.put("O", "FH");
        CIPHER_TABLE.put("P", "CJM");
        CIPHER_TABLE.put("Q", "FIM");
        CIPHER_TABLE.put("R", "CG");
        CIPHER_TABLE.put("S", "EG");
        CIPHER_TABLE.put("T", "H");
        CIPHER_TABLE.put("U", "EH");
        CIPHER_TABLE.put("V", "EIM");
        CIPHER_TABLE.put("W", "CH");
        CIPHER_TABLE.put("X", "DIM");
        CIPHER_TABLE.put("Y", "DLM");
        CIPHER_TABLE.put("Z", "FKM");
        CIPHER_TABLE.put("0", "FFH");
        CIPHER_TABLE.put("1", "CFH");
        CIPHER_TABLE.put("2", "EFH");
        CIPHER_TABLE.put("3", "ECH");
        CIPHER_TABLE.put("4", "EEH");
        CIPHER_TABLE.put("5", "EEG");
        CIPHER_TABLE.put("6", "DEG");
        CIPHER_TABLE.put("7", "FEG");
        CIPHER_TABLE.put("8", "FDG");
        CIPHER_TABLE.put("9", "FFG");
    }
    
    /**
     * Creates a new instance of the {@code DecodeCommand} class.
     * 
     * @param id the filesystem id
     */
    public DecodeCommand(int id)
    {
        super(id, "decode");
    }
    
    /*
     * The opposite of Map.get(); gets a key given the key's value.
     */
    private String getKeyFromValue(Map<String, String> map, String value)
    {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (entry.getValue().equals(value)) {
                return entry.getKey();
            }
        }
        
        return null;
    }
    
    @Override
    public void exec(String[] args)
    {
        // Combine all arguments into a single string separated by spaces
        String decodeStr = "";
        for (String arg : args) {
            decodeStr += arg + " ";
        }
        decodeStr = decodeStr.toUpperCase().trim();
        
        String result = "";
        String encodedLetter = "";
        String decodedLetter;
        char c;
        for (int i = 0; i < decodeStr.length(); i++) {
            c = decodeStr.charAt(i);
            
            // Convert the letter to its base form (L->A, Z->M, etc)
            if (c - 65 >= 13) {
                c = (char)(c - 13);
            }
            
            encodedLetter += c;
            
            // Each encoded letter ends with either a G, H, or M.
            if (c == 'G' || c == 'H' || c == 'M') {
                decodedLetter = getKeyFromValue(CIPHER_TABLE, encodedLetter);
                if (decodedLetter != null) {
                    // Append the decoded letter to the result
                    result += decodedLetter;
                } else {
                    // If the encoced letter is unknown, append a space
                    result += " ";
                }
                encodedLetter = "";
            }
        }
        
        // Print the result
        Terminal.println(result);
    }
}
