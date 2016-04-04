package com.italkyou.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.italkyou.beans.entradas.CeldaImagen;
import com.italkyou.gui.BaseActivity;
import com.italkyou.gui.R;
import com.italkyou.services.ItalkYouService;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

@SuppressLint("SimpleDateFormat")
public class AppUtil {


    private static int MAX_IMAGE_DIMENSION = 512;

    /**
     * Metodo para finalizar el servicio de la aplicacion.
     *
     * @param contextApp es el contexto de la aplicacion.
     */

    public static void detenerServicio(Context contextApp) {

        Intent intent = new Intent(contextApp, ItalkYouService.class);
        contextApp.stopService(intent);

    }

    private static void loadFibonacci(int i) {
        int current = 1, before = 0, result;

        for (int a = 0; a < i; a++) {
            result = current + before;
            Log.e("Fibonacci", "Result: " + result);

            if (a > 0) {
                before = current;
                current = result;
            }
        }
    }

    public static void MostrarMensaje(final Activity actividad, final String msj) {
        actividad.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(actividad, msj, Toast.LENGTH_LONG).show();
            }
        });
    }

    public static String formatAnnex(String annex) {
        annex = annex.substring(0, 3) + Const.TAG_DASH + annex.substring(3);
        return annex;
    }

    public static InputStream openDisplayPhoto(Context c, Uri uri) {
//        Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId);
        Uri displayPhotoUri = Uri.withAppendedPath(uri, ContactsContract.Contacts.Photo.DISPLAY_PHOTO);
        try {
            AssetFileDescriptor fd =
                    c.getContentResolver().openAssetFileDescriptor(displayPhotoUri, "r");
            return fd.createInputStream();
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Metodo para mostrar un aviso informativo al usuario, sobre un evento importante
     *
     * @param actividad,     es la actividad donde aparecera este mensaje
     * @param mensajeAlerta, es el texto que se le mostrara al usuario.
     */
    public static void mostrarAvisoInformativo(final Activity actividad, String mensajeAlerta) {

        new AlertDialog.Builder(actividad).setTitle(actividad.getResources().getString(R.string.app_name))
                .setMessage(mensajeAlerta)
                .setNeutralButton(R.string.btn_aceptar, new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
//                        actividad.finish();

                    }

                }).create().show();
    }

    public static void MostrarAlerta(final Activity actividad, final String msj) {
        Toast.makeText(actividad, msj, Toast.LENGTH_LONG).show();

    }

    public static void showMessage(final Context c, CharSequence message, boolean isLong) {
        if (isLong)
            Toast.makeText(c, message, Toast.LENGTH_LONG).show();
        else
            Toast.makeText(c, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Metodo para validar que el dispositivo tiene internet.
     * //@param activity es la actividad que invoca al metodo.
     *
     * @return
     */
    /*
    public static boolean existeConexionInternet(Activity activity) {

        ConnectivityManager cmRed =  (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo infoRed = cmRed.getActiveNetworkInfo();

        if (infoRed != null && infoRed.isConnectedOrConnecting()) {
            return true;
        }

        return false;
    }
*/
    public static boolean existeConexionInternet(Context contexto) {

        ConnectivityManager cm = (ConnectivityManager) contexto.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {

            NetworkInfo infoRed[] = cm.getAllNetworkInfo();
            if (infoRed != null) {

                for (int i = 0; i < infoRed.length; i++) {
                    LogApp.log("InfoRed=" + infoRed[i].getTypeName() + " desconectada");
                    if (infoRed[i].getState() == NetworkInfo.State.CONNECTED) {
                        LogApp.log("InfoRed=" + infoRed[i].getTypeName() + " conectada");
                        return true;
                    }
                }

            }

        }
        return false;
    }

    public static String obtenerIdiomaLocal() {
        String language;
        String valLanguage = Locale.getDefault().getLanguage();
        if (valLanguage.contains("es")) {
            language = Const.IDIOMA_ES;
        } else {
            language = Const.IDIOMA_EN;
        }
        return language;
    }

    public static List<Object> obtenerListaImagen(Context contexto) {

        List<Object> lista = new ArrayList<Object>();

        CeldaImagen celda1 = new CeldaImagen();
        celda1.setIdImagen(R.drawable.galeria);
        celda1.setTextoCelda(contexto.getString(R.string.texto_galeria));
        celda1.setDescripcion(Const.DESCRIPCION_GALERIA);
        lista.add(celda1);

        CeldaImagen celda2 = new CeldaImagen();
        celda2.setIdImagen(R.drawable.camara);
        celda2.setTextoCelda(contexto.getString(R.string.texto_camara));
        celda2.setDescripcion(Const.DESCRIPCION_CAMARA);
        lista.add(celda2);

        return lista;
    }

    public static List<Object> obtenerListaLlamadas(Context contexto, String tipoUsuario) {

        List<Object> lista = new ArrayList<Object>();
        if (tipoUsuario.equals(Const.USER_ITY)) {
            CeldaImagen celGratis = new CeldaImagen();
            celGratis.setIdImagen(R.drawable.llamada_gratis);
            celGratis.setTextoCelda(contexto.getString(R.string.texto_llamada_gratis));
            celGratis.setDescripcion(Const.descripcion_llamada_gratis);
            lista.add(celGratis);
        }
        CeldaImagen celPago = new CeldaImagen();
        celPago.setIdImagen(R.drawable.llamada_pago);
        celPago.setTextoCelda(contexto.getString(R.string.texto_llamada_pago));
        celPago.setDescripcion(Const.descripcion_llamada_pago);
        lista.add(celPago);
        CeldaImagen celSMS = new CeldaImagen();
        celSMS.setIdImagen(R.drawable.mensaje_sms);
        celSMS.setTextoCelda(contexto.getString(R.string.texto_enviar_sms));
        celSMS.setDescripcion(Const.descripcion_llamada_sms);
        lista.add(celSMS);
        return lista;
    }

    public static void pausar(int valor) {
        try {
            Thread.sleep(valor); //4000
        } catch (final Exception ex) {
        }
    }

    public static void obtenerBarraAcciones(BaseActivity vista, String anexo, String saldo, boolean flag) {
        ActionBar mActionBar = vista.getSupportActionBar();
        mActionBar.setDisplayShowHomeEnabled(true);
        mActionBar.setDisplayShowTitleEnabled(false);
        if (flag) mActionBar.setDisplayHomeAsUpEnabled(true);
        LayoutInflater mInflater = LayoutInflater.from(vista);

        View mCustomView = mInflater.inflate(R.layout.barra_acciones, null);
        TextView tvAnexo = (TextView) mCustomView.findViewById(R.id.tvAnexoUsuario);
        tvAnexo.setText(anexo);

        mActionBar.setCustomView(mCustomView);
        mActionBar.setDisplayShowCustomEnabled(true);
    }

    public static String formatoDescripcion(String descripcion, String idioma) {

        int cantidad = descripcion.length();

        if (idioma.equals(Const.IDIOMA_ES)) {
            if (cantidad == 9) descripcion = "        " + descripcion;
            else if (cantidad == 16) descripcion = " " + descripcion;
        } else {
            if (cantidad == 8) descripcion = "          " + descripcion;
            else if (cantidad == 12) descripcion = "    " + descripcion;
            else if (cantidad == 13) descripcion = "   " + descripcion;
        }
        return descripcion;
    }

    public static List<Object> obtenerListaOpcionesLlamada(Context contexto) {

        List<Object> lista = new ArrayList<Object>();
        CeldaImagen cdLlamar = new CeldaImagen();
        cdLlamar.setIdImagen(R.drawable.llamar);
        cdLlamar.setTextoCelda(contexto.getString(R.string.texto_llamar));
        cdLlamar.setDescripcion(Const.descripcion_llamar);
        lista.add(cdLlamar);

        CeldaImagen cdEliminar = new CeldaImagen();
        cdEliminar.setIdImagen(R.drawable.cerrar);
        cdEliminar.setTextoCelda(contexto.getString(R.string.texto_eliminar));
        cdEliminar.setDescripcion(Const.descripcion_eliminar);
        lista.add(cdEliminar);

        return lista;
    }

    public static List<Object> obtenerListaOpcionesMensaje(Context contexto) {
        List<Object> lista = new ArrayList<Object>();
        CeldaImagen cdEscuchar = new CeldaImagen();
        cdEscuchar.setIdImagen(R.drawable.escuchar_mensaje);
        cdEscuchar.setTextoCelda(contexto.getString(R.string.texto_escuchar));
        cdEscuchar.setDescripcion(Const.descripcion_escuchar);
        lista.add(cdEscuchar);

        CeldaImagen cdEliminar = new CeldaImagen();
        cdEliminar.setIdImagen(R.drawable.cerrar);
        cdEliminar.setTextoCelda(contexto.getString(R.string.texto_eliminar));
        cdEliminar.setDescripcion(Const.descripcion_eliminar);
        lista.add(cdEliminar);
        return lista;
    }

    public static String obtenerFecha() {
        Calendar cal = new GregorianCalendar();
        Date date = cal.getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        String fecha = df.format(date);
        // LogApp.log("obtener fecha "+fecha);
        return fecha;
    }

    public static String obtenerHora() {
        Calendar cal = new GregorianCalendar();
        Date date = cal.getTime();
        SimpleDateFormat df = new SimpleDateFormat("HH");
        String hora = df.format(date);
        // LogApp.log("obtener hora "+hora);
        return hora;
    }

    public static String obtenerMinutos() {
        Calendar cal = new GregorianCalendar();
        Date date = cal.getTime();
        SimpleDateFormat df = new SimpleDateFormat("mm");
        String minuto = df.format(date);
        // LogApp.log("obtener hora "+hora);
        return minuto;
    }

//    /**
//     * Metodo para obtener la imagen desde una ruta
//     *
//     * @param rutaImagen , ruta de la imagen a cargar
//     * @return
//     */

//    public static Bitmap obtenerImagen(String rutaImagen, boolean rotarImagen) {
//        Bitmap bmImgen = null;
//        BitmapFactory.Options bmOpciones = new BitmapFactory.Options();
//        bmOpciones.inJustDecodeBounds = false;
//        bmOpciones.inTempStorage = new byte[32 * 2048];
//        bmOpciones.inSampleSize = 2;
//
//
//
//        try {
//            File archivoImagen = new File(rutaImagen);
//            if (archivoImagen.exists())
//                bmImgen = BitmapFactory.decodeStream(new FileInputStream(archivoImagen), null, bmOpciones);
//
//        } catch (FileNotFoundException e) {
//
//        } catch (NullPointerException ex) {
//
//        }
//
//        if (rotarImagen) {
//
//            ExifInterface ei = null;
//            try {
//                ei = new ExifInterface(rutaImagen);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
//            Bitmap rotated = null;
//
//            switch (orientation) {
//
//                case ExifInterface.ORIENTATION_ROTATE_90:
//                    rotated = rotarBitmap(bmImgen, 90);
//                    break;
//                case ExifInterface.ORIENTATION_ROTATE_180:
//                    rotated = rotarBitmap(bmImgen, 180);
//                    break;
//                case ExifInterface.ORIENTATION_ROTATE_270:
//                    rotated = rotarBitmap(bmImgen, 270);
//                    break;
//
//            }
//            return rotarBitmap(rotated, 0);
//        }
//        return bmImgen;
//    }

    public static Bitmap rotarBitmap(Bitmap bmImagen, int angulo) {

        if (bmImagen != null) {

            Matrix matrix = new Matrix();
            matrix.postRotate(angulo);
            return Bitmap.createBitmap(bmImagen, 0, 0, bmImagen.getWidth(), bmImagen.getHeight(), matrix, true);

        } else {
            return bmImagen;
        }
    }

    public static Drawable obtenerDrawableDesdeBitmap(Resources recurosApp, Bitmap bitmap) {
        return new BitmapDrawable(recurosApp, bitmap);
    }

    /*testing*/

    public static int getOrientation(Context context, Uri photoUri) {
    /* it's on the external media. */
        Cursor cursor = context.getContentResolver().query(photoUri,
                new String[]{MediaStore.Images.ImageColumns.ORIENTATION}, null, null, null);

        if (cursor.getCount() != 1) {
            return -1;
        }

        cursor.moveToFirst();
        return cursor.getInt(0);
    }

    public static Bitmap getCorrectlyOrientedImage(Context context, Uri photoUri) throws IOException {
        InputStream is = context.getContentResolver().openInputStream(photoUri);
        BitmapFactory.Options dbo = new BitmapFactory.Options();
        dbo.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(is, null, dbo);
        is.close();

        int rotatedWidth, rotatedHeight;
        int orientation = getOrientation(context, photoUri);

        if (orientation == 90 || orientation == 270) {
            rotatedWidth = dbo.outHeight;
            rotatedHeight = dbo.outWidth;
        } else {
            rotatedWidth = dbo.outWidth;
            rotatedHeight = dbo.outHeight;
        }

        Bitmap srcBitmap;
        is = context.getContentResolver().openInputStream(photoUri);

        if (rotatedWidth > MAX_IMAGE_DIMENSION || rotatedHeight > MAX_IMAGE_DIMENSION) {
            float widthRatio = ((float) rotatedWidth) / ((float) MAX_IMAGE_DIMENSION);
            float heightRatio = ((float) rotatedHeight) / ((float) MAX_IMAGE_DIMENSION);
            float maxRatio = Math.max(widthRatio, heightRatio);

            // Create the bitmap from file
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = (int) maxRatio;
            srcBitmap = BitmapFactory.decodeStream(is, null, options);
        } else {
            srcBitmap = BitmapFactory.decodeStream(is);
        }
        is.close();

    /*
     * if the orientation is not 0 (or -1, which means we don't know), we
     * have to do a rotation.
     */
        if (orientation > 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(orientation);

            srcBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(),
                    srcBitmap.getHeight(), matrix, true);
        }

        return srcBitmap;
    }


    public static Bitmap getRoundedCornerImage(Drawable drawable, boolean square) {
        int width = 0;
        int height = 0;

        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();

        if (square) {
            if (bitmap.getWidth() < bitmap.getHeight()) {
                width = bitmap.getWidth();
                height = bitmap.getWidth();
            } else {
                width = bitmap.getHeight();
                height = bitmap.getHeight();
            }
        } else {
            height = bitmap.getHeight();
            width = bitmap.getWidth();
        }

        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, width, height);
        final RectF rectF = new RectF(rect);
        final float roundPx = 90;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    public static Bitmap getRoundedCornerImage(Bitmap bitmap, boolean square) {
        int width;
        int height;

        if (square) {
            if (bitmap.getWidth() < bitmap.getHeight()) {
                width = bitmap.getWidth();
                height = bitmap.getWidth();
            } else {
                width = bitmap.getHeight();
                height = bitmap.getHeight();
            }
        } else {
            height = bitmap.getHeight();
            width = bitmap.getWidth();
        }

        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, width, height);
        final RectF rectF = new RectF(rect);
        final float roundPx = 360;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    public static String clearNumberToCall(String phoneNumber) {
        phoneNumber = phoneNumber.replace(Const.CADENA_PREFIJO, Const.CARACTER_VACIO);
        phoneNumber = phoneNumber.replace(Const.ESPACIO_BLANCO, Const.CARACTER_VACIO);
        phoneNumber = phoneNumber.replace(Const.TAG_DASH, Const.CARACTER_VACIO);
        return phoneNumber;
    }


    private static final int MAX_BUFFER_SIZE = 1024 * 1024;

    private AppUtil()
    {
        throw new AssertionError("This class was not meant to be instantiated");
    }

    public static boolean isNullOrEmpty(final String string)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            return string == null || string.isEmpty();
        }

        return string == null || string.length() == 0;
    }

    public static int compareString(final String lhs, final String rhs)
    {
        if (AppUtil.isNullOrEmpty(lhs) && AppUtil.isNullOrEmpty(rhs)) {
            return 0;
        }

        if (AppUtil.isNullOrEmpty(lhs)) {
            return -1;
        }

        if (AppUtil.isNullOrEmpty(rhs)) {
            return 1;
        }

        return lhs.compareToIgnoreCase(rhs);
    }

    public static boolean equalString(final String lhs, final String rhs)
    {
        return compareString(lhs, rhs) == 0;
    }

    public static boolean equals(final Object lhs, final Object rhs)
    {
        return lhs == rhs || lhs != null && lhs.equals(rhs);
    }

    public static void copyStream(final InputStream is, final OutputStream os) throws IOException
    {
        final byte[] buffer = new byte[MAX_BUFFER_SIZE];
        int length;
        while ((length = is.read(buffer)) != -1) {
            os.write(buffer, 0, length);
        }
    }

    public static void setBackgroundCompatible(final View view, final Drawable drawable)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackground(drawable);
        } else {
            //noinspection deprecation
            view.setBackgroundDrawable(drawable);
        }
    }

    @SuppressWarnings("SameParameterValue")
    public static Drawable getDrawableCompatible(final Resources resources, final int id)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return resources.getDrawable(id, null);
        } else {
            //noinspection deprecation
            return resources.getDrawable(id);
        }
    }

    public static String formatMilliSeconds(final long milliSeconds)
    {
        if (milliSeconds >= 0) {
            return formatPositiveMilliSeconds(milliSeconds);
        }

        return "-" + formatPositiveMilliSeconds(-1 * milliSeconds);
    }

    private static String formatPositiveMilliSeconds(final long milliSeconds)
    {
        final SimpleDateFormat sdf = createSimpleDateFormat(milliSeconds);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+0"));
        return sdf.format(new Date(milliSeconds));
    }

    private static SimpleDateFormat createSimpleDateFormat(final long milliSeconds)
    {
        if (milliSeconds >= 3600000) {
            return new SimpleDateFormat("HH:mm:ss", Locale.US);
        }

        return new SimpleDateFormat("mm:ss", Locale.US);
    }

    @SuppressWarnings("SameParameterValue")
    public static void setFinishOnTouchOutsideCompatible(final Activity activity, final boolean finish)
    {
        // versions before HONEYCOMB do not support FinishOnTouchOutsides
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            activity.setFinishOnTouchOutside(finish);
        }
    }

}
