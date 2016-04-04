package com.italkyou.conexion;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.italkyou.beans.entradas.ParametrosWS;
import com.italkyou.utils.Const;

public class AsyncConexionWSTask extends AsyncTask<ParametrosWS, Integer, String> {

    private static final String TAG = AsyncConexionWSTask.class.getSimpleName() + Const.ESPACIO_BLANCO;

    public interface AsyncTaskListener {
        void onCompletadoTask(String resultado);
    }

    private AsyncTaskListener asyncTaskListener;

    public AsyncConexionWSTask(AsyncTaskListener asyncTaskListener, Activity activity) {
        this.asyncTaskListener = asyncTaskListener;
    }

    public AsyncConexionWSTask(AsyncTaskListener asyncTaskListener) {
        this.asyncTaskListener = asyncTaskListener;
    }

    public AsyncConexionWSTask(AsyncTaskListener asyncTaskListener, Activity activity, ProgressDialog dialog) {
        this.asyncTaskListener = asyncTaskListener;
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected String doInBackground(ParametrosWS... params) {
        String respuesta;
        ParametrosWS parametro = params[0];
        Log.e(Const.DEBUG_REQUEST, TAG + "Parametros: " + parametro.getParametros().toString());
        ConexionWS conexion = new ConexionWS();
        respuesta = conexion.conexionServidor(parametro);
        Log.e(Const.DEBUG_REQUEST, TAG + "Respuesta: " + respuesta);
        return respuesta;
    }

    @Override
    protected void onPostExecute(String result) {
        asyncTaskListener.onCompletadoTask(result);
    }
}
