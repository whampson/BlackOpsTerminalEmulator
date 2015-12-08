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
import java.math.RoundingMode;
import java.util.List;
import thehambone.blackopsterminalemulator.Mail;
import thehambone.blackopsterminalemulator.Mailbox;
import thehambone.blackopsterminalemulator.ScreenBuffer;
import thehambone.blackopsterminalemulator.Shell;
import thehambone.blackopsterminalemulator.Terminal;
import thehambone.blackopsterminalemulator.filesystem.ExecutableFile;

/**
 * Created on Dec 6, 2015.
 *
 * @author thehambone <thehambone93@gmail.com>
 */
public class MailCommand extends ExecutableFile
{
    private Mailbox mailbox;
    
    public MailCommand(int id)
    {
        super(id, "mail");
        mailbox = new Mailbox();
    }
    
    @Override
    public void exec(String[] args)
    {
        mailbox = Terminal.getActiveLoginShell().getUser().getMailbox();
        MailShell shell = new MailShell();
        shell.exec();
    }
    
    private class MailShell extends Shell
    {
        private final static String DEFAULT_MESSAGE
                = "Mail Version 0.72.  Type ? for help.";
        
        public MailShell()
        {
            super("&");
        }
        
        private void printTabs(int n)
        {
            for (int i = 0; i < n; i++) {
                Terminal.print('\t');
            }
        }
        
        private void showInbox()
        {
            Terminal.println("id\t\t\t from\t\t\t\t\t  date\t\t\t\t\t   subject");
            List<Mail> mail = mailbox.getAllMail();
            Mail m;
            for (int i = 0; i < mail.size(); i++) {
                m = mail.get(i);
                if (i < 10) {
                    Terminal.print(' ');
                }
                Terminal.print(i + " ");
                
                int tabCount = 7 - (int)Math.round((double)m.getSender().length() / ScreenBuffer.TAB_LENGTH);
                Terminal.print(m.getSender());
                printTabs(tabCount);
                
                tabCount = 6 - (int)Math.floor((double)m.getDate().length() / ScreenBuffer.TAB_LENGTH);
                Terminal.print(m.getDate());
                printTabs(tabCount);
                
                Terminal.print(m.getSubject());
                Terminal.println();
            }
        }
        
        private void showHelp()
        {
            Terminal.println("Mail Help:");
            Terminal.println("\t ?\t\t\tThis help information");
            Terminal.println("\t i\t\t\tDisplay inbox");
            Terminal.println("\t [n]\t\tRead Message [n]");
            Terminal.println("\t q\t\t\tQuit Mail");
        }
        
        private void openMail(int id)
        {
            Mail m = mailbox.getMail(id);
            Terminal.println();
            if (m != null) {
                m.open();
                Terminal.println();
            }
        }
        
        @Override
        protected void onLaunch()
        {
            showInbox();
            Terminal.println(DEFAULT_MESSAGE);
        }
        
        @Override
        protected void run()
        {
            String input;
            
            while (isRunning()) {
                Terminal.print(getPrompt());
                input = Terminal.readLine();
                switch (input) {
                    case "i":
                        showInbox();
                        continue;
                    case "q":
                        terminate();
                        continue;
                    case "?":
                        showHelp();
                        continue;
                }
                
                String mailIDStr = "";
                char c;
                for (int i = 0; i < Math.min(4, input.length()); i++) {
                    c = input.charAt(i);
                    if (c > 0x29 && c < 0x3A) {
                        mailIDStr += c;
                    } else {
                        break;
                    }
                }
                if (mailIDStr.isEmpty()) {
                    Terminal.println(DEFAULT_MESSAGE);
                    continue;
                }
                
                int mailID = Integer.parseInt(mailIDStr);
                openMail(mailID);
            }
        }
    }
}
