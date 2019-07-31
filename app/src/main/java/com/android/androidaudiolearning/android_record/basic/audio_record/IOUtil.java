package com.android.androidaudiolearning.android_record.basic.audio_record;

import java.io.Closeable;
import java.io.IOException;


public class IOUtil {
    public static void close(Closeable... closeableList) {
        try {
            for (Closeable closeable : closeableList) {
                if (closeable != null) {
                    closeable.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
