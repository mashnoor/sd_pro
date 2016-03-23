package com.smartdsepro.android;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;

public class DevTools {
    // Method for reading from file
    public static String read_file(Activity current_activity, String file) {
        FileInputStream fis;
        String final_data = null;
        try {
            fis = current_activity.openFileInput(file);
            byte[] data_from_file = new byte[fis.available()];
            while (fis.read(data_from_file) != -1) {

                final_data = new String(data_from_file);

            }
            fis.close();
            return final_data;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "";
        }
    }

    // Method for writting to file
    public static void write_file(Activity current_activity, String file,
                                  String data) throws IOException {
        FileOutputStream fos;
        try {
            fos = current_activity.openFileOutput(file, Context.MODE_PRIVATE);
            fos.write(data.getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
    public static void write_file(Context current_activity, String file,
                                  String data) throws IOException {
        FileOutputStream fos;
        try {
            fos = current_activity.openFileOutput(file, Context.MODE_PRIVATE);
            fos.write(data.getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    // Method for checking file existance
    public static boolean fileExistance(Activity current_activity, String file) {
        File check_file = current_activity.getBaseContext().getFileStreamPath(
                file);
        return check_file.exists();
    }

}
