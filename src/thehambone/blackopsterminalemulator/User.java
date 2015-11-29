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

import thehambone.blackopsterminalemulator.filesystem.File;
import java.util.ArrayList;
import java.util.List;
import thehambone.blackopsterminalemulator.filesystem.Directory;
import thehambone.blackopsterminalemulator.filesystem.HomeDirectory;

/**
 * Created on Nov 28, 2015.
 *
 * @author thehambone <thehambone93@gmail.com>
 */
public class User
{
    public final String username;
    public final String password;
    public final HomeDirectory homeDirectory;
    public final List<File> files;
//    public final Mailbox mailbox;
    
    public User(String username, String password,
            HomeDirectory homeDirectory, List<File> files)
    {
        this.username = username;
        this.password = password;
        this.homeDirectory = homeDirectory;
        this.files = new ArrayList<>(files);
    }
    
    public String getUsername()
    {
        return username;
    }
    
    public String getPassword()
    {
        return password;
    }
    
    public HomeDirectory getHomeDirectory()
    {
        return homeDirectory;
    }
}