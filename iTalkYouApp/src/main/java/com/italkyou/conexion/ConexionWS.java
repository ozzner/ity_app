package com.italkyou.conexion;

import android.util.Log;

import com.italkyou.beans.entradas.ParametrosWS;
import com.italkyou.utils.Const;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class ConexionWS {


    private static final String TAG = ConexionWS.class.getSimpleName();

    public ConexionWS() {

    }

    public String conexionServidor(String ruta) {
        String respuesta = "";
        HttpClient httpClient = new DefaultHttpClient();
        HttpGet servicio = new HttpGet(ruta);
        servicio.setHeader("content-type", "application/json");

        try {

            HttpResponse response = httpClient.execute(servicio);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();

            if (statusCode == 200)
                respuesta = EntityUtils.toString(response.getEntity());
            else
                respuesta = Const.COD_ERROR_CONEXION_WS;

        } catch (Exception ex) {
            respuesta = Const.COD_ERROR_CONEXION_WS;
        }

        return respuesta;
    }


    public String conexionServidor(ParametrosWS parametro) {
        String respuesta = "";
        if (parametro.getMetodo().equals(Const.METODO_POST)) {

            try {

                Log.e(TAG, "ConexionWS2 ruta post " + parametro.getRuta());

                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(parametro.getRuta());
                httpPost.setEntity(new UrlEncodedFormEntity(parametro.getParametros(), HTTP.ISO_8859_1));

                if (!parametro.getMascara().equals(Const.CARACTER_VACIO)) {
                    httpPost.setHeader("consumer_key", parametro.getMascara());
                }

                if (parametro.isFlagJSON()) {
                    httpPost.setHeader("Content-Type","application/json; charset=UTF-8");
                    httpPost.setHeader("Accept", "application/json");

                    try {
                        JSONObject json = parametro.getJsonObjectParams();
                        StringEntity input = new StringEntity(json.toString());
                        input.setContentEncoding(HTTP.UTF_8);
                        input.setContentType("application/json; charset=UTF-8");
                        httpPost.setEntity(input);
                        Log.e(TAG, json.toString());
                    } catch (Exception ex) {
                        Log.e(TAG, "Error to parsing...");
                    }
                }

                try {

                    HttpResponse httpResponse = httpClient.execute(httpPost);
                    StatusLine statusLine = httpResponse.getStatusLine();
                    int statusCode = statusLine.getStatusCode();

                    if (statusCode == 200) {
                        respuesta = EntityUtils.toString(httpResponse.getEntity());
                    } else
                        respuesta = Const.COD_ERROR_CONEXION_WS;
                } catch (ClientProtocolException e) {
                    respuesta = Const.COD_ERROR_CONEXION_WS;
                    e.printStackTrace();
                } catch (IOException e) {
                    respuesta = Const.COD_ERROR_CONEXION_WS;
                    e.printStackTrace();
                }

            } catch (UnsupportedEncodingException e) {

                respuesta = Const.COD_ERROR_CONEXION_WS;
                e.printStackTrace();
            }

        } else if (parametro.getMetodo().equals(Const.METODO_GET)) {

            HttpClient httpClient = new DefaultHttpClient();
            String paramString = URLEncodedUtils.format(parametro.getParametros(), "utf-8");
            String ruta = parametro.getRuta() + "?" + paramString;
            HttpGet httpGet = new HttpGet(ruta);

            if (!parametro.getMascara().equals(Const.cad_vacia)) {
                httpGet.setHeader("consumer_key", parametro.getMascara());
            }
            try {
                HttpResponse httpResponse = httpClient.execute(httpGet);
                StatusLine statusLine = httpResponse.getStatusLine();
                int statusCode = statusLine.getStatusCode();
                if (statusCode == 200) {
                    respuesta = EntityUtils.toString(httpResponse.getEntity());
                } else
                    respuesta = Const.COD_ERROR_CONEXION_WS;

            } catch (Exception ex) {
                respuesta = Const.COD_ERROR_CONEXION_WS;

            } finally {

            }
        }
        return respuesta;
    }

    /*-----------------------------------------*/
    public String conexionServidorPost(ParametrosWS parametro) {
        String respuesta = "";
        if (parametro.getMetodo().equals(Const.METODO_POST)) {
            try {
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(parametro.getRuta());
                httpPost.setEntity(new UrlEncodedFormEntity(parametro.getParametros()));

                if (!parametro.getMascara().equals(Const.CARACTER_VACIO)) {
                    httpPost.setHeader("consumer_key", parametro.getMascara());
                }
                httpPost.setHeader("Content-Type", "application/json");
                httpPost.setHeader("Accept", "application/json");

                try {
                    HttpResponse httpResponse = httpClient.execute(httpPost);

                    StatusLine statusLine = httpResponse.getStatusLine();
                    int statusCode = statusLine.getStatusCode();
                    if (statusCode == 200) {
                        respuesta = EntityUtils.toString(httpResponse.getEntity());
                    } else
                        respuesta = Const.COD_ERROR_CONEXION_WS;
                } catch (ClientProtocolException e) {
                    respuesta = Const.COD_ERROR_CONEXION_WS;
                    e.printStackTrace();
                } catch (IOException e) {
                    respuesta = Const.COD_ERROR_CONEXION_WS;
                    e.printStackTrace();
                }
            } catch (UnsupportedEncodingException e) {

                respuesta = Const.COD_ERROR_CONEXION_WS;
                e.printStackTrace();
            }
        } else if (parametro.getMetodo().equals(Const.METODO_GET)) {

            HttpClient httpClient = new DefaultHttpClient();
            String paramString = URLEncodedUtils.format(parametro.getParametros(), "utf-8");
            String ruta = parametro.getRuta() + "?" + paramString;
            HttpGet httpGet = new HttpGet(ruta);

            if (!parametro.getMascara().equals(Const.cad_vacia)) {
                httpGet.setHeader("consumer_key", parametro.getMascara());
            }
            try {
                HttpResponse httpResponse = httpClient.execute(httpGet);
                StatusLine statusLine = httpResponse.getStatusLine();
                int statusCode = statusLine.getStatusCode();
                if (statusCode == 200) {
                    respuesta = EntityUtils.toString(httpResponse.getEntity());
                } else
                    respuesta = Const.COD_ERROR_CONEXION_WS;

            } catch (Exception ex) {
                respuesta = Const.COD_ERROR_CONEXION_WS;

            } finally {

            }
        }
        return respuesta;
    }

}
