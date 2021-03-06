package misc;

//import com.orhanobut.logger.Logger;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;


public class CopyFile {
    private static String mytag = "misc.Copyfile";

    // If targetLocation does not exist, it will be created.
    public static void copyDirectory(File sourceLocation, File targetLocation)
            throws IOException {
        //com.orhanobut.logger.Logger.init("CopyFile").methodCount(0);

        if (sourceLocation.isDirectory()) {
            try {
                if (!targetLocation.exists()) {
                    boolean sucess = targetLocation.mkdir();
                    if (sucess == false)
                        Log.i("CopyFile", "No " + targetLocation.toString() + " created");
                }
                String[] children = sourceLocation.list();
                for (String aChildren : children) {
                    copyDirectory(new File(sourceLocation, aChildren),
                            new File(targetLocation, aChildren));
                }
            } catch (Exception e) {
                Log.e("CopyFile", "creating " + targetLocation.toString() + " error " + e.toString());
            }
        } else {

            InputStream in = new FileInputStream(sourceLocation);
            OutputStream out = new FileOutputStream(targetLocation);

            // Copy the bits from instream to outstream
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.flush();
            out.close();
            System.gc();
        }
    }

}
