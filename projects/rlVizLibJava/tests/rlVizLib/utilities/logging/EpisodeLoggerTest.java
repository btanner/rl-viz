package rlVizLib.utilities.logging;

import rlVizLib.utilities.logging.EpisodeLogger;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Brian Tanner
 */
public class EpisodeLoggerTest {

    @Test
    public void testEmptyString() {
        EpisodeLogger el = new EpisodeLogger();
        el.appendLogString("");
        String result = el.getLogSubString(0, 25);

        assertEquals(result, "");

    }

    @Test
    public void testShortString() {
        EpisodeLogger el = new EpisodeLogger();
        String origString = "tom is a fun boy @#$#@RFAWLSD*";
        el.appendLogString(origString);
        String result = el.getLogSubString(0, origString.length());
        assertEquals(result, origString);
        String result2 = el.getLogSubString(origString.length(), origString.length());
        assertEquals(result2, "");
    }

    @Test
    public void testLongString() {
        EpisodeLogger el = new EpisodeLogger();
        String origString = "abcdefghisdfsdlfdsfsfk;slfk;lk23l;k;3l5k63;3154989fsdabcdefghisdfsdlfdsfsfk;slfk;lk23l;k;3l5k63;3154989fsdabcdefghisdfsdlfdsfsfk;slfk;lk23l;k;3l5k63;3154989fsdabcdefghisdfsdlfdsfsfk;slfk;lk23l;k;3l5k63;3154989fsd";
        el.appendLogString(origString);
        String result = el.getLogSubString(0, 5);
        assertEquals(result, origString.substring(0, 5));

        String result2 = el.getLogSubString(0, origString.length());
        assertEquals(result2, origString);
    }

    @Test
    public void testMultipleStringsGetStart() {
        EpisodeLogger el = new EpisodeLogger();
        String origString = "abcde12345";
        String compositeString = origString + origString + origString + origString + origString;
        el.appendLogString(origString);
        el.appendLogString(origString);
        el.appendLogString(origString);
        el.appendLogString(origString);
        el.appendLogString(origString);

        String result = el.getLogSubString(0, 5);
        assertEquals(result, origString.substring(0, 5));
    }

    @Test
    public void testMultipleStringsGetAll() {
        EpisodeLogger el = new EpisodeLogger();
        String origString = "abcde12345";
        String compositeString = origString + origString + origString + origString + origString;
        el.appendLogString(origString);
        el.appendLogString(origString);
        el.appendLogString(origString);
        el.appendLogString(origString);
        el.appendLogString(origString);

        String result = el.getLogSubString(0, compositeString.length());
        assertEquals(result, compositeString);

    }
      @Test
    public void testMultipleStringsGetAllWithTinyBuffer() {
        EpisodeLogger el = new EpisodeLogger(3);
        String origString = "abcde12345";
        String compositeString = origString + origString + origString + origString + origString;
        el.appendLogString(origString);
        el.appendLogString(origString);
        el.appendLogString(origString);
        el.appendLogString(origString);
        el.appendLogString(origString);

        String result = el.getLogSubString(0, compositeString.length());
        assertEquals(result, compositeString);

    }
  
     @Test
    public void testMultipleStringsGetAllWithMidBuffer() {
        EpisodeLogger el = new EpisodeLogger(10);
        String origString = "abcde12345";
        String compositeString = origString + origString + origString + origString + origString;
        el.appendLogString(origString);
        el.appendLogString(origString);
        el.appendLogString(origString);
        el.appendLogString(origString);
        el.appendLogString(origString);

        String result = el.getLogSubString(0, compositeString.length());
        assertEquals(result, compositeString);

    }

    @Test
    public void testMultipleStringsGetMid() {
        EpisodeLogger el = new EpisodeLogger();
        String origString = "abcde12345";
        String compositeString = origString + origString + origString + origString + origString;
        el.appendLogString(origString);
        el.appendLogString(origString);
        el.appendLogString(origString);
        el.appendLogString(origString);
        el.appendLogString(origString);

        String result = el.getLogSubString(13, 27);
        assertEquals(result, compositeString.substring(13, 13+27));

    }
    @Test
    public void testMultipleStringsGetMidWithTinyBuffer() {
        EpisodeLogger el = new EpisodeLogger(3);
        String origString = "abcde12345";
        String compositeString = origString + origString + origString + origString + origString;
        el.appendLogString(origString);
        el.appendLogString(origString);
        el.appendLogString(origString);
        el.appendLogString(origString);
        el.appendLogString(origString);

        String result = el.getLogSubString(13, 27);
        assertEquals(result, compositeString.substring(13, 13+27));

    }
    @Test
    public void testMultipleStringsGetMidWithMidBuffer() {
        EpisodeLogger el = new EpisodeLogger(10);
        String origString = "abcde12345";
        String compositeString = origString + origString + origString + origString + origString;
        el.appendLogString(origString);
        el.appendLogString(origString);
        el.appendLogString(origString);
        el.appendLogString(origString);
        el.appendLogString(origString);

        String result = el.getLogSubString(13, 27);
        assertEquals(result, compositeString.substring(13, 13+27));
    }
}
