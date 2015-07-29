package com.scar.android;

import android.app.Activity;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;

/* helper class for saving files
 *
 */
public class FileSaveUtil implements MediaScannerConnection.MediaScannerConnectionClient {
    private final String name;
    private final String tag;
    private final byte[] data;

    public FileSaveUtil(String fname, String ty, byte[] data) {
        name = fname;
        tag = ty;
        this.data = data;
    }

    public String save(Activity act) {
        File f = new File(Environment.getExternalStorageDirectory(), "SCAR");
        f.mkdirs();
        FileOutputStream fos;
        f = new File(f, name+"."+tag);
        try {
            fos = new FileOutputStream(f);
            fos.write(data);
            fos.flush();
            fos.close();
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }

        if(tag.equals("png") || tag.equals("jpg") || tag.equals("gif")) {
            scanIntoGallery(act, f.getAbsolutePath());
        }

        return f.getPath();
    }

    private MediaScannerConnection ms;
    private String filestr;
    public void onMediaScannerConnected() {
        ms.scanFile(filestr, null);
    }

    public void onScanCompleted(String path, Uri uri) {
        ms.disconnect();
    }

    private void scanIntoGallery(Activity act, final String file) {
        ms = new MediaScannerConnection(act, this);
        ms.connect();
        filestr = file;
    }

}
