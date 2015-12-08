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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created on Dec 6, 2015.
 *
 * @author thehambone <thehambone93@gmail.com>
 */
public class Mailbox
{
    private final List<Mail> mailList;
    
    public Mailbox()
    {
        mailList = new ArrayList<>();
    }
    
    public void addMail(Mail m)
    {
        mailList.add(m);
    }
    
    public Mail getMail(int id)
    {
        if (mailList.isEmpty() || id < 0 || id > mailList.size() - 1) {
            return null;
        }
        
        return mailList.get(id);
    }
    
    public List<Mail> getAllMail()
    {
        return Collections.unmodifiableList(mailList);
    }
}
