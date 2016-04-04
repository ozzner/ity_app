package com.italkyou.utils;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


public class LogApp {

    public static String FORMATO_FECHA_HORA ="dd/MM/yyyy hh:mm";
    public static String NOMBRE_LOG = "LogITalkYou";
    public static String EXTENSION_LOG =".txt";
    public static String CARACTER_PUNTO = ".";
    public static String CARACTER_GUION = " - ";
    /**
     * Metodo para escribir un log en la SDCARD  dispositivo, tambien se visualizara en la consola
     * del dispositivo cuando este conectado a la PC en modo depuracion.
     * @param textoLog, es el texto que se va a imprimir en el log.
     */
	public static void log(String textoLog) {

		if (Const.FLAG_ESCRIBIR_LOG) {

            BufferedWriter bwSalidaArchivo  = null;
            FileWriter     fwEscribeArchivo = null;

			try {		
				
				File rutaSDCard = Environment.getExternalStorageDirectory().getAbsoluteFile();

				if (rutaSDCard.canWrite()){
                    //Imprimiendo en consola
                    Log.i(NOMBRE_LOG,textoLog);

                    //Imprimiento en el archivo log, en el dispositivo
					File archivoLog = new File(rutaSDCard,generarNombreArchivoLog());
					fwEscribeArchivo = new FileWriter(archivoLog,true);
					bwSalidaArchivo = new BufferedWriter(fwEscribeArchivo);

					bwSalidaArchivo.write(obtenerFecha() + CARACTER_GUION + textoLog + "\n");
					bwSalidaArchivo.close();
                    fwEscribeArchivo.close();

				}		    
		    
			} catch (IOException e) {
                if( e.getMessage()!=null)
                Log.i(Const.NOMBRE_LOG,e.getMessage());

			}finally{

                    if(bwSalidaArchivo!=null) {

                        try {
                            bwSalidaArchivo.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }

                    if(fwEscribeArchivo!=null){
                        try {
                            fwEscribeArchivo.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }

            }

		}
		
	}

    /**
     * Metodo para generar el nombre del archivo log, formado a partir de la fecha y hora actual.
     * @return el nombre del archivo log.
     */

    private static String generarNombreArchivoLog(){
        String formatoFechaLog= AppUtil.obtenerFecha().replace(CARACTER_PUNTO,
                                                                       Const.cad_vacia);
        return NOMBRE_LOG +formatoFechaLog+EXTENSION_LOG;
    }

    /**
     * Metodo para borrar el archivo log, cuando supera el maximo de 1M.
     * @param ruta es la ruta del archivo log.
     * @return retorna un boleano, que indica si se borro el archivo o no.
     */
	public static boolean borrarLog(String ruta) {

        boolean delete=false;

            try {

                File archivoLog = new File(ruta);
                //Se calcula el tamanio del log
                if (archivoLog.length() > 1024 * 1024)// si es mayor a 1M
                    delete = archivoLog.delete();

            } catch (Exception e) {
                e.printStackTrace();
            }

        return delete;
	}

    /**
     * Metodo para obtener la fecha actual del dispositivo
     * @return la fecha actual en formato dd/MM/yyyy hh:mm
     */
	private static String obtenerFecha(){
		Calendar calendarioGregoriano = new GregorianCalendar();
	    Date fechaActual = calendarioGregoriano.getTime();
	    SimpleDateFormat sdfFormatoFecha = new SimpleDateFormat(FORMATO_FECHA_HORA);
	    String strFecha = sdfFormatoFecha.format(fechaActual);
		return strFecha!=null?strFecha:"";
	}
	

}
