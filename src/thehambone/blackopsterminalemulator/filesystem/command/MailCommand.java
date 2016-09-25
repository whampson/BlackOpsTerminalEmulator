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

import java.util.List;
import thehambone.blackopsterminalemulator.Mail;
import thehambone.blackopsterminalemulator.Mailbox;
import thehambone.blackopsterminalemulator.ScreenBuffer;
import thehambone.blackopsterminalemulator.Shell;
import thehambone.blackopsterminalemulator.Terminal;
import thehambone.blackopsterminalemulator.filesystem.ExecutableFile;

/**
 * The "mail" command.
 * <p>
 * This command opens the current user's mailbox and allows them to read mail.
 * <p>
 * Created on Dec 6, 2015.
 *
 * @author Wes Hampson
 */
public class MailCommand extends ExecutableFile
{
    private Mailbox mailbox;
    
    /**
     * Creates a new instance of the {@code MailCommand} class.
     * 
     * @param id the filesystem object id
     */
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
        
        /*
         * Creates a new {@code MailShell} object.
         */
        private MailShell()
        {
            super("&");
        }
        
        /*
         * Prints n tabs to the console.
         */
        private void printTabs(int n)
        {
            for (int i = 0; i < n; i++) {
                Terminal.print('\t');
            }
        }
        
        /*
         * Shows the contents of the inbox.
         */
        private void showInbox()
        {
            Terminal.println("id\t\t\t from\t\t\t\t\t  "
                    + "date\t\t\t\t\t   subject");
            
            List<Mail> mail = mailbox.getAllMail();
            Mail m;
            int tabCount;
            
            for (int i = 0; i < mail.size(); i++) {
                m = mail.get(i);
                
                // Print number
                if (i < 10) {
                    Terminal.print(' ');
                }
                Terminal.print(i + " ");
                
                // Print sender
                Terminal.print(m.getSender());
                tabCount = 7 - (int)Math.round((double)
                        m.getSender().length() / ScreenBuffer.TAB_LENGTH);
                printTabs(tabCount);
                
                // Print date
                Terminal.print(m.getDate());
                tabCount = 6 - (int)Math.floor((double)
                        m.getDate().length() / ScreenBuffer.TAB_LENGTH);
                printTabs(tabCount);
                
                // Print subject
                Terminal.print(m.getSubject());
                Terminal.println();
            }
        }
        
        /*
         * Prints the mail help informaton.
         */
        private void showHelp()
        {
            Terminal.println("Mail Help:");
            Terminal.println("\t ?\t\t\tThis help information");
            Terminal.println("\t i\t\t\tDisplay inbox");
            Terminal.println("\t [n]\t\tRead Message [n]");
            Terminal.println("\t q\t\t\tQuit Mail");
        }
        
        /*
         * Opens a mail.
         */
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
                // Print prompt
                Terminal.print(getPrompt());
                
                // Read input from the user
                input = Terminal.readLine();
                
                // Check if the user typed one of the mail commands
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
                
                // Extracts a number from the user-typed string
                for (int i = 0; i < Math.min(4, input.length()); i++) {
                    c = input.charAt(i);
                    if (c > 0x29 && c < 0x3A) {
                        // Append the character if it is a numeric char
                        mailIDStr += c;
                    } else {
                        // Break at the first occurance of a non-numeric char
                        break;
                    }
                }
                
                if (mailIDStr.isEmpty()) {
                    Terminal.println(DEFAULT_MESSAGE);
                    continue;
                }
                
                // Open the specified mail
                int mailID = Integer.parseInt(mailIDStr);
                openMail(mailID);
            }
        }
    }
}
