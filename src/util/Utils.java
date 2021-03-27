package util;

import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {

    public static void closeQuietly(Closeable... closeables) {
        for (Closeable closeable : closeables) {
            if (null != closeable) {
                try {
                    closeable.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void sendMsg(Message msg, ObjectOutputStream out) throws IOException {
        out.writeObject(msg);
        out.flush();
    }

    public static String formatDate(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }
}
