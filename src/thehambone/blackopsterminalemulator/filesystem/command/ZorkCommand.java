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

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import thehambone.blackopsterminalemulator.filesystem.ExecutableFile;
import thehambone.blackopsterminalemulator.io.Logger;

/**
 * The "zork" command.
 * <p>
 * This command launches Zork I: The Great Underground Empire.
 * <p>
 * Created on Nov 28, 2015.
 *
 * @author Wes Hampson
 */
public class ZorkCommand extends ExecutableFile
{
    private static final String ZORK_WEB_URL
            = "http://iplayif.com/?story=http%3A%2F%2Fwww.ifarchive.org"
            + "%2Fif-archive%2Fgames%2Fzcode%2Fzdungeon.z5";
    /**
     * Creates a new instance of the {@code ZorkCommand} class.
     * 
     * @param id the filesystem object id
     */
    public ZorkCommand(int id)
    {
        super(id, "zork");
    }
    
    @Override
    public void exec(String[] args)
    {
        // TODO: become a code guru and reverse-engineer Zork.
        // (or just use a pre-made library)
        // For now, launch a web version of Zork I.
        try {
            Desktop.getDesktop().browse(new URI(ZORK_WEB_URL));
        } catch (IOException | URISyntaxException ex) {
            Logger.stackTrace(ex);
        }
    }
}
