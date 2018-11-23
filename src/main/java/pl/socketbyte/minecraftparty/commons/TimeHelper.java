package pl.socketbyte.minecraftparty.commons;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeHelper {

    private TimeHelper() {
    }

    public static String getFormattedTime(long time) {
        int minutes = (int) (time / (60 * 1000));
        int seconds = (int) ((time / 1000) % 60);
        return String.format("%d:%02d", minutes, seconds);
    }
}
