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
 * Created on Dec 6, 2015.
 *
 * @author thehambone <thehambone93@gmail.com>
 */
public class DecodeCommand extends ExecutableFile
{
    private static final Map<String, String> CIPHER = new HashMap<>();
    static
    {
        CIPHER.put("A", "IM");
        CIPHER.put("B", "DKM");
        CIPHER.put("C", "DJM");
        CIPHER.put("D", "DG");
        CIPHER.put("E", "G");
        CIPHER.put("F", "EJM");
        CIPHER.put("G", "FG");
        CIPHER.put("H", "EKM");
        CIPHER.put("I", "KM");
        CIPHER.put("J", "CLM");
        CIPHER.put("K", "DH");
        CIPHER.put("L", "CAG");
        CIPHER.put("M", "LM");
        CIPHER.put("N", "JM");
        CIPHER.put("O", "FH");
        CIPHER.put("P", "CJM");
        CIPHER.put("Q", "FIM");
        CIPHER.put("R", "CG");
        CIPHER.put("S", "EG");
        CIPHER.put("T", "H");
        CIPHER.put("U", "EH");
        CIPHER.put("V", "EIM");
        CIPHER.put("W", "CH");
        CIPHER.put("X", "DIM");
        CIPHER.put("Y", "DLM");
        CIPHER.put("Z", "FKM");
        CIPHER.put("0", "FFH");
        CIPHER.put("1", "CFH");
        CIPHER.put("2", "EFH");
        CIPHER.put("3", "ECH");
        CIPHER.put("4", "EEH");
        CIPHER.put("5", "EEG");
        CIPHER.put("6", "DEG");
        CIPHER.put("7", "FEG");
        CIPHER.put("8", "FDG");
        CIPHER.put("9", "FFG");
    }
    
    public DecodeCommand(int id)
    {
        super(id, "decode");
    }
    
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
            if (c - 65 >= 13) {
                c = (char)(c - 13);
            }
            encodedLetter += c;
            if (c == 'G' || c == 'H' || c == 'M') {
                decodedLetter = getKeyFromValue(CIPHER, encodedLetter);
                if (decodedLetter != null) {
                    result += decodedLetter;
                } else {
                    result += " ";
                }
                encodedLetter = "";
            }
        }
        
        Terminal.println(result);
    }
}
