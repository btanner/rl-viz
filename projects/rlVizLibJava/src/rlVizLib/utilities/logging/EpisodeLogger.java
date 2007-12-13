/*
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package rlVizLib.utilities.logging;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This is a utility class that can be used by environments to keep episode logs
 * in temporary files on their computer.  These logs can be easily sent to the 
 * experiment program (if requested) via {@link rlVizLib.messaging.environment.EpisodeSummaryRequest}.
 * <p>
 * Note that these log files do not persist past the end of session, or even past
 * a call to {@link EpisodeLogger.clear}.  If you need them for longer than that
 * remember to save a copy.
 * @author Brian Tanner
 */
public class EpisodeLogger {
    //1 million chars is about 1 MB right?
    static int defaultMaxStringSizeBeforeFileStarts = 1000000;
    int maxStringSizeBeforeFileStarts;
    StringBuilder theLogStringBuilder = null;
    File theLogFile = null;
    boolean failedWithError = false;

    /**
     * This doesn't really do anything fancy for now.
     */
    public EpisodeLogger() {
        theLogStringBuilder = new StringBuilder();
        this.maxStringSizeBeforeFileStarts=defaultMaxStringSizeBeforeFileStarts;
    }
    public EpisodeLogger(int bufferSize) {
        theLogStringBuilder = new StringBuilder();
        this.maxStringSizeBeforeFileStarts=bufferSize;
    }

    public void appendLogString(String newLogString) {
        if(failedWithError)return;
        
        if (newLogString.length() + theLogStringBuilder.length() > maxStringSizeBeforeFileStarts) {
            flush();
            appendToFile(newLogString);
        } else {
            theLogStringBuilder.append(newLogString);
        }
    }
    
    public String getLogSubString(long startPoint, int amount){
            flush();
        try {
            RandomAccessFile raf = new RandomAccessFile(theLogFile, "r");
            raf.seek(startPoint);
            byte[] result=new byte[amount];
            int amountRead=raf.read(result);
            String stringResult=new String(result);
            if(amountRead<=0)return "";
            String returnString=stringResult.substring(0, amountRead);
            return returnString;
        } catch (IOException ex) {
            Logger.getLogger(EpisodeLogger.class.getName()).log(Level.SEVERE, null, ex);
        }
            return "";
    }
    
    public void clear(){
        if(theLogFile!=null)theLogFile.delete();
        theLogFile=null;
        failedWithError=false;
        theLogStringBuilder=new StringBuilder();
    }
    public void flush(){
            appendBufferToFile();
            theLogStringBuilder = new StringBuilder();        
    }

    private void appendBufferToFile() {
        appendToFile(theLogStringBuilder.toString());
    }

    private void appendToFile(String newLogString) {
        if (theLogFile == null) {
            makeTheFile();
        }

        if (!failedWithError) {
            try {
                BufferedWriter out = new BufferedWriter(new FileWriter(theLogFile, true));
                out.write(newLogString);
                out.close();
            } catch (IOException e) {
            System.err.println("Error ::was unable to write to temporary log file in EpisodeLogger. Suppressing further log messages after Exception printout.");
            System.err.println(e);
            failedWithError = true;
            }
        }
    }

    private void makeTheFile() {
        try {
            theLogFile = File.createTempFile("episode", "log");
            //Make sure the file doesn't outlive the program's life
            theLogFile.deleteOnExit();
        } catch (IOException ex) {
            System.err.println("Error ::was unable to create temporary log file in EpisodeLogger. Suppressing further log messages after Exception printout.");
            System.err.println(ex);
            failedWithError = true;
        }
    }
}
