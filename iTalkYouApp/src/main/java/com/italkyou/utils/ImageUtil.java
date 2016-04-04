package com.italkyou.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by rsantillanc on 15/09/2015.
 */
public class ImageUtil {

    private static final String TAG = ImageUtil.class.getSimpleName() + Const.ESPACIO_BLANCO;

    public ImageUtil(Activity act) {

    }

    /**
     * Este método permite crear el directorio y almacenar las imágenes de el chat.
     *
     * @param bm       Imagen en formato mapa de bits
     * @param filename nombre de el archivo a almanenar (Anexo+Fecha)
     * @return 0 = Success!, 1 = Image exist, 2 = Error processing bitmap.
     */
    public static int saveToSdCard(Bitmap bm, String filename) {
        File path = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), Const.ITY_MAIN_DIR + Const.ITY_CHAT_IMAGE_DIR);
        path.mkdirs();
        File file = new File(path, filename);

        if (file.exists())
            return 1;

        try {
            processingBitmap(file, bm);
            return 0;

        } catch (IOException e) {
            return 2;
        }
    }


    private static boolean processingBitmap(File file, Bitmap bm) throws IOException {
        FileOutputStream outputStream = new FileOutputStream(file);
        boolean on = bm.compress(Bitmap.CompressFormat.JPEG, 100,outputStream);
        outputStream.close();
        return on;
    }


}
