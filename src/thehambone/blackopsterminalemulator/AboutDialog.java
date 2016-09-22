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

package thehambone.blackopsterminalemulator;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import thehambone.blackopsterminalemulator.io.Logger;
import thehambone.blackopsterminalemulator.io.ResourceLoader;

/**
 * Created on Jan 6, 2016.
 *
 * @author Wes Hampson <thehambone93@gmail.com>
 */
public class AboutDialog extends JDialog
{
    private static final String LOGO_PATH = "res/logo.png";
    private static final String BACGROUND_PATH = "res/gradient.png";
    private static final String PROGRAM_DESCRIPTION
            = "This program emulates the fake computer terminal found in the "
            + "videogame \"Call of Duty: Black Ops\". The aim is to match the "
            + "behavior of the fake terminal exactly; it should feel as if one "
            + "were using the original terminal found in the game. This "
            + "involves including all text files, images, sound files, and mail "
            + "found on the original terminal, as well as incorporating its "
            + "various bugs and shortcomings."
            + "\n----------\n"
            + "THE SOFTWARE IS PROVIDED \"AS IS\", WITHOUT WARRANTY OF ANY "
            + "KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE "
            + "WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE "
            + "AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT "
            + "HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, "
            + "WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING "
            + "FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR "
            + "OTHER DEALINGS IN THE SOFTWARE.";
    
    private static final int WINDOW_WIDTH = 320;
    private static final int WINDOW_HEIGHT = 260;
    
    public AboutDialog(Frame parent, boolean modal)
    {
        super(parent, "About", modal);
        
        initComponents();
        initESCKey();
    }
    
    /*
     * Creates and positions the dialog's components.
     */
    private void initComponents()
    {
        JLabel programTitleLabel;
        JLabel programSloganLabel;
        JLabel programVersionTitleLabel;
        JLabel programVersionLabel;
        JLabel programAuthorTitleLabel;
        JLabel programAuthorLabel;
        JLabel programAuthorContactLabel;
        JLabel programCopyrightLabel;
        JLabel logolabel;
        JLabel backgroundLabel;
        JScrollPane descriptionScrollPane;
        JTextPane descriptionTextPane;
        JButton closeButton;
        
        Font defaultLabelFont = UIManager.getFont("Label.font");
        
        // General window properties
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        setMinimumSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        setResizable(false);
        getContentPane().setLayout(null);
        
        // Title label
        programTitleLabel = new JLabel(Main.PROGRAM_TITLE);
        programTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        programTitleLabel.setFont(new Font("Courier New", Font.BOLD, 24));
        getContentPane().add(programTitleLabel);
        programTitleLabel.setBounds(78, 6, 230, 17);
        
        // Slogan label
        programSloganLabel = new JLabel(Main.PROGRAM_SLOGAN_HTML);
        programSloganLabel.setHorizontalAlignment(SwingConstants.CENTER);
        getContentPane().add(programSloganLabel);
        programSloganLabel.setBounds(78, 26, 230, 14);
        
        // Version label 1
        programVersionTitleLabel = new JLabel("Version:");
        programVersionTitleLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        getContentPane().add(programVersionTitleLabel);
        programVersionTitleLabel.setBounds(78, 46, 114, 14);
        
        // Version label 2
        programVersionLabel = new JLabel(Main.PROGRAM_VERSION);
        getContentPane().add(programVersionLabel);
        programVersionLabel.setBounds(194, 46, 114, 14);
        
        // Author label 1
        programAuthorTitleLabel = new JLabel("Created by:");
        programAuthorTitleLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        getContentPane().add(programAuthorTitleLabel);
        programAuthorTitleLabel.setBounds(78, 60, 114, 14);
        
        // Author label 2
        programAuthorLabel = new JLabel(Main.PROGRAM_AUTHOR);
        getContentPane().add(programAuthorLabel);
        programAuthorLabel.setBounds(194, 60, 114, 14);
        
        // Description text pane
        descriptionTextPane = new JTextPane();
        descriptionTextPane.setEditable(false);
        descriptionTextPane.setText(PROGRAM_DESCRIPTION);
        descriptionTextPane.setCaretPosition(0);
        descriptionTextPane.setBackground(new Color(235, 235, 235));
        
        // Description scroll pane
        descriptionScrollPane = new JScrollPane();
        descriptionScrollPane.setViewportView(descriptionTextPane);
        getContentPane().add(descriptionScrollPane);
        descriptionScrollPane.setBounds(6, 80, 302, 108);
        
        // Copyright label
        programCopyrightLabel = new JLabel(Main.PROGRAM_COPYRIGHT);
        getContentPane().add(programCopyrightLabel);
        programCopyrightLabel.setBounds(6, 194, 200, 14);
        
        // Email label
        programAuthorContactLabel = new JLabel(
                String.format("<html><a href='mailto:%s'>%s</html>",
                        Main.PROGRAM_AUTHOR_EMAIL, Main.PROGRAM_AUTHOR_EMAIL));
        programAuthorContactLabel.setToolTipText("Email "
                + Main.PROGRAM_AUTHOR);
        programAuthorContactLabel.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        getContentPane().add(programAuthorContactLabel);
        FontMetrics fm =
                programAuthorContactLabel.getFontMetrics(defaultLabelFont);
        int width = fm.stringWidth(Main.PROGRAM_AUTHOR_EMAIL);
        programAuthorContactLabel.setBounds(6, 210, width, 14);
        
        // Close button
        closeButton = new JButton("Close");
        closeButton.setToolTipText("Close this dialog (ESC)");
        getContentPane().add(closeButton);
        closeButton.setBounds(248, 200, 60, 24);
        
        // Logo
        logolabel = new JLabel(new ImageIcon(
                ResourceLoader.loadEmbeddedImage(LOGO_PATH)));
        getContentPane().add(logolabel);
        logolabel.setBounds(6, 6, 64, 64);
        
        // Background
        backgroundLabel = new JLabel(new ImageIcon(
                ResourceLoader.loadEmbeddedImage(BACGROUND_PATH)));
        getContentPane().add(backgroundLabel);
        backgroundLabel.setBounds(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
        
        // Email label mouse click action
        programAuthorContactLabel.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                try {
                    Desktop.getDesktop().browse(
                            new URI("mailto:" + Main.PROGRAM_AUTHOR_EMAIL));
                } catch (IOException | URISyntaxException ex) {
                    Logger.stackTrace(ex);
                }
            }
        });
        
        // Close button action
        closeButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                dispose();
            }
        });
        
        // Show component boundaries (debug)
//        programTitleLabel.setBorder(new LineBorder(Color.BLACK));
//        programSloganLabel.setBorder(new LineBorder(Color.BLACK));
//        programVersionTitleLabel.setBorder(new LineBorder(Color.BLACK));
//        programVersionLabel.setBorder(new LineBorder(Color.BLACK));
//        programAuthorTitleLabel.setBorder(new LineBorder(Color.BLACK));
//        programAuthorLabel.setBorder(new LineBorder(Color.BLACK));
//        descriptionScrollPane.setBorder(new LineBorder(Color.BLACK));
//        programCopyrightLabel.setBorder(new LineBorder(Color.BLACK));
//        programAuthorContactLabel.setBorder(new LineBorder(Color.BLACK));
//        closeButton.setBorder(new LineBorder(Color.BLACK));
//        iconLabel.setBorder(new LineBorder(Color.BLACK));
//        backgroundLabel.setBorder(new LineBorder(Color.BLACK));
    }
    
    /*
     * Defines the action for the escape key.
     */
    private void initESCKey()
    {
        InputMap im
                = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = getRootPane().getActionMap();
        
        AbstractAction escAction = new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                // Close window
                dispose();
            }
        };
        
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "ESC");
        am.put("ESC", escAction);
    }
}
