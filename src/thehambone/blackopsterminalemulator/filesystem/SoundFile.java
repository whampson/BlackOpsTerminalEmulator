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

package thehambone.blackopsterminalemulator.filesystem;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * Created on Nov 30, 2015.
 *
 * @author thehambone <thehambone93@gmail.com>
 */
public class SoundFile extends PrintableFile
{
    private static Clip activeSoundClip;
    
    public SoundFile(int id, String name, String resourcePath)
    {
        super(id, name, resourcePath);
    }
    
    @Override
    public void print()
    {
        try {
            AudioInputStream stream;
            AudioFormat format;
            DataLine.Info info;
            
            stream = AudioSystem.getAudioInputStream(new java.io.File(getResourcePath()));
            format = stream.getFormat();
            info = new DataLine.Info(Clip.class, format);
            
            if (activeSoundClip != null && activeSoundClip.isActive()) {
                activeSoundClip.stop();
            }
            
            activeSoundClip = (Clip)AudioSystem.getLine(info);
            activeSoundClip.open(stream);
            activeSoundClip.start();
        } catch (IOException | UnsupportedAudioFileException | LineUnavailableException ex) {
            // TODO: log
            ex.printStackTrace();
        }
    }
}