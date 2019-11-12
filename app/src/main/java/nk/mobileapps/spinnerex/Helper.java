package nk.mobileapps.spinnerex;

import android.content.Context;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/*
 * @author mobileapps.nk@gmail.com
 */

public class Helper {
    /**
     * method to read data residing in the text file
     *
     * @param context-Object      of Context, context from where the activity is going
     *                            to start.
     * @param resourceID-resource Id from which the text file is acessing from
     * @return
     */

    public static String readTextFile(Context context, int resourceID) {

        InputStream is = context.getResources().openRawResource(resourceID);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            int code = is.read();
            while (code != -1) {
                baos.write(code);
                code = is.read();
            }
            is.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return baos.toString();
    }
}
