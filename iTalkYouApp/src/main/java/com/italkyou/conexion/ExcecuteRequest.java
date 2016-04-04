package com.italkyou.conexion;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.italkyou.beans.BeanBase;
import com.italkyou.beans.BeanLlamada;
import com.italkyou.beans.BeanMensajeVoz;
import com.italkyou.beans.BeanPais;
import com.italkyou.beans.BeanRespuestaOperacion;
import com.italkyou.beans.BeanSaldo;
import com.italkyou.beans.BeanUsuario;
import com.italkyou.beans.entradas.EntradaBuscarContactos;
import com.italkyou.beans.entradas.EntradaCambiarClave;
import com.italkyou.beans.entradas.EntradaEliminarLlamadaMsj;
import com.italkyou.beans.entradas.EntradaEnviarSMS;
import com.italkyou.beans.entradas.EntradaInisionSesion;
import com.italkyou.beans.entradas.EntradaPerfilUsuario;
import com.italkyou.beans.entradas.EntradaRedireccionarAnexo;
import com.italkyou.beans.entradas.EntradaRegistarUsuario;
import com.italkyou.beans.entradas.ParametrosWS;
import com.italkyou.beans.salidas.OutputContact;
import com.italkyou.beans.salidas.SalidaDatos;
import com.italkyou.beans.salidas.SalidaMovimiento;
import com.italkyou.beans.salidas.SalidaPin;
import com.italkyou.beans.salidas.SalidaResultado;
import com.italkyou.beans.salidas.SalidaUsuario;
import com.italkyou.conexion.AsyncConexionWSTask.AsyncTaskListener;
import com.italkyou.gui.menu.SaldoActivity;
import com.italkyou.utils.Const;
import com.italkyou.utils.LogApp;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExcecuteRequest {

    private static final String TAG = ExcecuteRequest.class.getSimpleName();
    private static final String HTTP = "http://";
    //QA
    private static final String IP_SERVIDOR_SERVICIOS = "181.177.235.165";
    private static final String NOMBRE_APLICACION = "/WS_Itakyou_Oauth/SITalkYou.svc";

    //  SMS
    static String ruta_enviarSMS = HTTP + IP_SERVIDOR_SERVICIOS + NOMBRE_APLICACION + "/enviarsms";
    static final String URL_REQUEST_SMS = "http://206.190.135.92:8085/ITY_WS/webresources/envioSMS/sms";
    static final String URL_RECEIVER_PWD_BY_SMS = HTTP + IP_SERVIDOR_SERVICIOS + NOMBRE_APLICACION + "/SendSMSPassword";

    //Authentication and register
    static String ruta_inicioSesion = HTTP + IP_SERVIDOR_SERVICIOS + NOMBRE_APLICACION + "/InicioSesion/"; //
    static String ruta_recuperarClave = HTTP + IP_SERVIDOR_SERVICIOS + NOMBRE_APLICACION + "/RecuperarClave/"; //
    static String ENDPOINT_EMAIL_SUPPORT_REGISTER = HTTP + IP_SERVIDOR_SERVICIOS + NOMBRE_APLICACION + "/EnviarCorreoSoporte";
    static String ruta_validarTelefono = HTTP + IP_SERVIDOR_SERVICIOS + NOMBRE_APLICACION + "/PinFono/"; //
    static String ruta_registrarUsuario = HTTP + IP_SERVIDOR_SERVICIOS + NOMBRE_APLICACION + "/RegistroUsuario_1/";
    static String RUTA_VALIDAR_PIN_LLAMADA = HTTP + IP_SERVIDOR_SERVICIOS + NOMBRE_APLICACION + "/ValidarPinLlamada"; //




    static String RUTA_LISTADO_PAISES = HTTP + IP_SERVIDOR_SERVICIOS + NOMBRE_APLICACION + "/Listpais/"; //
    static String ruta_obtenerEstadoPin = HTTP + IP_SERVIDOR_SERVICIOS + NOMBRE_APLICACION + "/obtenerEstadoPin/"; //
    static String ruta_actualizaInformacion = HTTP + IP_SERVIDOR_SERVICIOS + NOMBRE_APLICACION + "/UpdateUser2/"; //
    static String ruta_cambiarClave = HTTP + IP_SERVIDOR_SERVICIOS + NOMBRE_APLICACION + "/UpdateUserPass"; //
    static String ruta_contactosItalkyou = HTTP + IP_SERVIDOR_SERVICIOS + NOMBRE_APLICACION + "/ListContactosComun/"; //
    static String RUTA_VALIDAR_CONTACTOS_ITALYOU = HTTP + IP_SERVIDOR_SERVICIOS + NOMBRE_APLICACION + "/ContactosComun"; //
    static String ruta_busquedaContactos = HTTP + IP_SERVIDOR_SERVICIOS + NOMBRE_APLICACION + "/GetContacto/"; //
    static String ruta_obtenerSaldo = HTTP + IP_SERVIDOR_SERVICIOS + NOMBRE_APLICACION + "/GetBalance/"; //
    static String ruta_historialSaldo = HTTP + IP_SERVIDOR_SERVICIOS + NOMBRE_APLICACION + "/ListHistorialSaldo/";
    static String ruta_redireccionarAnexo = HTTP + IP_SERVIDOR_SERVICIOS + NOMBRE_APLICACION + "/RedireccionarAnexo/";
    static String ruta_historialLlamadas = HTTP + IP_SERVIDOR_SERVICIOS + NOMBRE_APLICACION + "/ListLlamadas/";
    static String ruta_historialLlamadasVoz = HTTP + IP_SERVIDOR_SERVICIOS + NOMBRE_APLICACION + "/ListVoicemail/";
    static String ruta_eliminar_llamada = HTTP + IP_SERVIDOR_SERVICIOS + NOMBRE_APLICACION + "/DeleteLlamada/";
    static String ruta_eliminar_mensaje_voz = HTTP + IP_SERVIDOR_SERVICIOS + NOMBRE_APLICACION + "/DeleteMensajeVoz/";
    static String ruta_estado_mensaje_voz = HTTP + IP_SERVIDOR_SERVICIOS + NOMBRE_APLICACION + "/EstadoEscuchadoMensajeVoz/";
    static String ruta_consulta_datos = HTTP + IP_SERVIDOR_SERVICIOS + NOMBRE_APLICACION + "/GetData/User/";
    static String ruta_cerrar_sesion = HTTP + IP_SERVIDOR_SERVICIOS + NOMBRE_APLICACION + "/CerrarSesion/";
    static String URL_RELOAD_BALANCE = HTTP + IP_SERVIDOR_SERVICIOS + NOMBRE_APLICACION + "/GrabaCarga";


    public interface ResultadoOperacionListener {
        void onResultadoOperacion(BeanRespuestaOperacion respuesta);
    }

    private ResultadoOperacionListener resultadoOperacionListener;

    public ExcecuteRequest() {

    }

    public ExcecuteRequest(ResultadoOperacionListener operacionListener) {
        this.resultadoOperacionListener = operacionListener;
    }

    /**
     * *********************************************Listado de Paises***********************************************************************************
     */
    public void obtenerListadoPaises() {

        String ruta = RUTA_LISTADO_PAISES + "1";
        ParametrosWS parametro = new ParametrosWS();
        parametro.setMetodo(Const.METODO_GET);
        parametro.setRuta(ruta);

        AsyncConexionWSTask conexion = new AsyncConexionWSTask(new AsyncTaskListener() {

            @Override
            public void onCompletadoTask(String resultado) {

                BeanRespuestaOperacion respuesta = new BeanRespuestaOperacion();

                if (!resultado.equals(Const.COD_ERROR_CONEXION_WS)) {
                    Gson gson = new Gson();
                    List<BeanPais> listado;
                    listado = gson.fromJson(resultado, new TypeToken<ArrayList<BeanPais>>() {
                    }.getType());
                    respuesta.setObjeto(listado);
                } else {
                    respuesta.setError(resultado);
                }
                resultadoOperacionListener.onResultadoOperacion(respuesta);
            }
        });
        conexion.execute(parametro);
    }


    /**
     * ****************************************Registrar una cuenta**********************************************************************
     */
    public void validarTelefono(EntradaRegistarUsuario entradaRegistro) {
        String ruta = ruta_validarTelefono + entradaRegistro.getIdioma() + "/" + entradaRegistro.getIdPrefijo() + "/" + entradaRegistro.getTelefono() + "/" + entradaRegistro.getClave() + "/" + entradaRegistro.getCorreo();
        ParametrosWS parametro = new ParametrosWS();
        parametro.setMetodo(Const.METODO_GET);
        parametro.setRuta(ruta);

        AsyncConexionWSTask conexion = new AsyncConexionWSTask(new AsyncTaskListener() {

            @Override
            public void onCompletadoTask(String resultado) {

                BeanRespuestaOperacion bRptaOperacion = new BeanRespuestaOperacion();

                if (resultado.length() > 2) {

                    Gson gson = new Gson();
                    SalidaPin pinUsuario = gson.fromJson(resultado, SalidaPin.class);
                    //Cuando es correcto se pasa el resultado en el atributo Objeto
                    bRptaOperacion.setObjeto(pinUsuario);

                } else {
                    //Cuando no es correcto se pasa el resultado en el atributo Error.
                    bRptaOperacion.setError(resultado);
                }
                resultadoOperacionListener.onResultadoOperacion(bRptaOperacion);
            }

        });

        conexion.execute(parametro);
    }

    public void validarPin(EntradaRegistarUsuario entradaRegistro) {

        List<NameValuePair> parametros = new ArrayList<>();
        parametros.add(new BasicNameValuePair("ID_Idioma", entradaRegistro.getIdioma()));
        parametros.add(new BasicNameValuePair("id_llamada", entradaRegistro.getValorPin().getIdcall()));
        parametros.add(new BasicNameValuePair("pin_llamada", entradaRegistro.getValorPin().getPin()));
        parametros.add(new BasicNameValuePair("celular", entradaRegistro.getIdPrefijo() + entradaRegistro.getTelefono()));
        parametros.add(new BasicNameValuePair("password", entradaRegistro.getClave()));

        ParametrosWS parametro = new ParametrosWS();
        parametro.setMetodo(Const.METODO_POST);
        parametro.setRuta(RUTA_VALIDAR_PIN_LLAMADA);
        parametro.setParametros(parametros);

        AsyncConexionWSTask conexion = new AsyncConexionWSTask(new AsyncTaskListener() {

            @Override
            public void onCompletadoTask(String resultado) {

                BeanRespuestaOperacion respuesta = new BeanRespuestaOperacion();

                if (resultado.length() > 2) {
                    Gson gson = new Gson();
                    SalidaResultado salida = gson.fromJson(resultado, SalidaResultado.class);
                    respuesta.setObjeto(salida);
                } else {
                    respuesta.setError(resultado);
                }

                resultadoOperacionListener.onResultadoOperacion(respuesta);
            }
        });

        conexion.execute(parametro);
    }

    public SalidaResultado obtenerEstadoPin(String pin) {

        SalidaResultado salida = new SalidaResultado();
        String ruta = ruta_obtenerEstadoPin + pin;
        ConexionWS conexion = new ConexionWS();
        String respuesta = conexion.conexionServidor(ruta);
        Log.e(TAG, "[obtenerEstadoPin] respuesta " + respuesta);
        if (respuesta.length() > 2) {
            Gson gson = new Gson();
            salida = gson.fromJson(respuesta, SalidaResultado.class);
        }
        return salida;
    }

    public void registrarUsuario(EntradaRegistarUsuario entradaRegistro) {
        // Llenamos los parametros
        List<NameValuePair> parametros = new ArrayList<>();
        parametros.add(new BasicNameValuePair("ID_Pais", entradaRegistro.getIdPais()));
        parametros.add(new BasicNameValuePair("Celular", entradaRegistro.getTelefono()));
        parametros.add(new BasicNameValuePair("Password", entradaRegistro.getClave()));
        parametros.add(new BasicNameValuePair("ID_Idioma", entradaRegistro.getIdioma()));
        parametros.add(new BasicNameValuePair("Nombres", entradaRegistro.getNombre()));
        parametros.add(new BasicNameValuePair("ID_Zona_Horaria", entradaRegistro.getZonaHoraria()));
        parametros.add(new BasicNameValuePair("Correo", entradaRegistro.getCorreo()));
        parametros.add(new BasicNameValuePair("Pin", "0000")); //DEFAULT

        ParametrosWS parametro = new ParametrosWS();
        parametro.setMetodo(Const.METODO_GET);
        parametro.setRuta(ruta_registrarUsuario);
        parametro.setParametros(parametros);

        AsyncConexionWSTask conexion = new AsyncConexionWSTask(new AsyncTaskListener() {

            @Override
            public void onCompletadoTask(String resultado) {

                BeanRespuestaOperacion respuesta = new BeanRespuestaOperacion();
                if (resultado.length() > 2) {
                    SalidaUsuario salida;
                    Gson gson = new Gson();
                    salida = gson.fromJson(resultado, SalidaUsuario.class);
                    respuesta.setObjeto(salida);
                } else {
                    respuesta.setError(resultado);
                }
                resultadoOperacionListener.onResultadoOperacion(respuesta);
            }
        });

        conexion.execute(parametro);
    }

    /**
     * ***************************************Inicio de sesion **************************************************************
     */
    public void obtenerDatosSesion(EntradaInisionSesion entrada) {

        // Llenamos los parametros
        List<NameValuePair> parametros = new ArrayList<NameValuePair>();
        parametros.add(new BasicNameValuePair("ID_Idioma", entrada.getIdIdioma()));
        parametros.add(new BasicNameValuePair("Anexo", entrada.getAnexo()));
        parametros.add(new BasicNameValuePair("Password", entrada.getClave()));

        ParametrosWS parametro = new ParametrosWS();
        parametro.setMetodo(Const.METODO_GET);
        parametro.setParametros(parametros);
        parametro.setRuta(ruta_inicioSesion);


        AsyncConexionWSTask conexion = new AsyncConexionWSTask(new AsyncTaskListener() {

            @Override
            public void onCompletadoTask(String resultado) {

                LogApp.log("[obtenerDatosSesion] respuesta " + resultado);
                BeanRespuestaOperacion respuesta = new BeanRespuestaOperacion();

                if (resultado.length() > 2) {
                    Gson gson = new Gson();
                    BeanUsuario usuario = gson.fromJson(resultado, BeanUsuario.class);
                    respuesta.setObjeto(usuario);

                } else {
                    respuesta.setError(resultado);
                }

                resultadoOperacionListener.onResultadoOperacion(respuesta);
            }
        });

        conexion.execute(parametro);
    }


    public void recuperarClave(String correo) {
        String ruta = ruta_recuperarClave + correo;
        ParametrosWS parametro = new ParametrosWS();
        parametro.setMetodo(Const.METODO_GET);
        parametro.setRuta(ruta);

        AsyncConexionWSTask conexion = new AsyncConexionWSTask(new AsyncTaskListener() {

            @Override
            public void onCompletadoTask(String resultado) {

                LogApp.log("[recuperarClave] respuesta " + resultado);
                BeanRespuestaOperacion respuesta = new BeanRespuestaOperacion();
                if (resultado.length() > 2) {
                    BeanBase salida;
                    Gson gson = new Gson();
                    salida = gson.fromJson(resultado, BeanBase.class);
                    respuesta.setObjeto(salida);
                } else {
                    respuesta.setError(resultado);
                }
                resultadoOperacionListener.onResultadoOperacion(respuesta);
            }
        });

        conexion.execute(parametro);
    }

    /**
     * **********************************************Perfil de usuario******************************************************************************
     */
    public void actualizarPerfilUsuario(EntradaPerfilUsuario entrada) {

        String ruta = ruta_actualizaInformacion
                + entrada.getAnexo() + "/"
                + entrada.getNombre() + "/"
                + entrada.getCorreo() + "/"
                + entrada.getPin() + "/"
                + entrada.getIdioma() + "/"
                + entrada.getIdPais() + "/"
                + entrada.getFlagImagen() + "/"
                + entrada.getO_ck();

        ParametrosWS parametro = new ParametrosWS();

        parametro.setMetodo(Const.METODO_GET);
        parametro.setRuta(ruta);
        AsyncConexionWSTask conexion = new AsyncConexionWSTask(new AsyncTaskListener() {

            @Override
            public void onCompletadoTask(String resultado) {

                LogApp.log("[actualizarPerfilUsuario] respuesta " + resultado);
                BeanRespuestaOperacion respuesta = new BeanRespuestaOperacion();
                if (resultado.length() > 2) {
                    SalidaResultado salida;
                    Gson gson = new Gson();
                    salida = gson.fromJson(resultado, SalidaResultado.class);
                    respuesta.setObjeto(salida);
                } else {
                    respuesta.setError(resultado);
                }
                resultadoOperacionListener.onResultadoOperacion(respuesta);
            }
        });

        conexion.execute(parametro);
    }

    public void cambiarClave(EntradaCambiarClave entrada) {

        List<NameValuePair> parametros = new ArrayList<NameValuePair>();
        parametros.add(new BasicNameValuePair("anexo", entrada.getAnexo()));
        parametros.add(new BasicNameValuePair("clave_antigua", entrada.getClaveAntigua()));
        parametros.add(new BasicNameValuePair("clave_nueva", entrada.getClaveNueva()));
        parametros.add(new BasicNameValuePair("re_clave_nueva", entrada.getConfirmaClave()));
        ParametrosWS parametro = new ParametrosWS();
        parametro.setMetodo(Const.METODO_POST);
        parametro.setRuta(ruta_cambiarClave);
        parametro.setParametros(parametros);
        parametro.setMascara(entrada.getC_ok());

        AsyncConexionWSTask conexion = new AsyncConexionWSTask(new AsyncTaskListener() {

            @Override
            public void onCompletadoTask(String resultado) {

                LogApp.log("[CambiarClave] respuesta " + resultado);
                BeanRespuestaOperacion respuesta = new BeanRespuestaOperacion();
                if (resultado.length() > 2) {
                    SalidaResultado salida = new SalidaResultado();
                    Gson gson = new Gson();
                    salida = gson.fromJson(resultado, SalidaResultado.class);
                    respuesta.setObjeto(salida);
                } else {
                    respuesta.setError(resultado);
                }
                resultadoOperacionListener.onResultadoOperacion(respuesta);
            }
        });

        conexion.execute(parametro);
    }

    /**
     * ***********************************************Contactos************************************************************************
     */
    //Retorna los telephonos que estan en la red ITY (
//    public List<OutputContact> obtenerContactosItalkYou(String listaNumeros) {
//
//        List<OutputContact> listado = new ArrayList<OutputContact>();
//        String ruta = ruta_contactosItalkyou + listaNumeros;
//        ConexionWS conexion = new ConexionWS();
//        String respuesta = conexion.conexionServidor(ruta);
//
//        if (!respuesta.equals(Const.LISTA_VACIA) && !respuesta.equals(Const.COD_ERROR_CONEXION_WS)) {
//            Gson gson = new Gson();
//            listado = gson.fromJson(respuesta, new TypeToken<ArrayList<OutputContact>>() {
//            }.getType());
//        }
//
//        return listado;
//    }
    public static List<OutputContact> obtenerContactosAnexosItalkYou(String listaNumeros) {
//       listaNumeros =
//               "51989410513,51980840617,51940966292,940966292,012932960,51983426903,51989062638" +
//                       ",989062638,51989586924,989586924,51980563130,980563130,51949099965,949099965" +
//                       ",51989186155,51981183981,981183981,51976028659,976028659,1530033560,012933218" +
//                       ",014370708,0737425439,19426739807004,014377378,51991073240,962260271,991073240" +
//                       ",51988777433,51990319077,51966401241,51999242045,51999242045,51998337031,51987135777" +
//                       ",51977412986,51957535994,51963537738,51947147675,51954079466,51992823265,51974296365" +
//                       ",51974296365,51967755586,51979325393,51955738349,51997967942,51969054715,51951379128" +
//                       ",51990137665,51997503017,51997979711,51997979711,51991793929,51976652565,1991001402" +
//                       ",51986935204,51949936016,51951412275,51998271056,51999117119,51970048856,51954462139" +
//                       ",51963537204,51963537204,51990990949,51983715846,51997994991,51956345764,51982934646" +
//                       ",51994991985,51990990999,51991634472,51942636815,51989636131,51989754769,51975274591" +
//                       ",51984783184,51900900901,51919919919,51993489096,51991949675,51997994995,51997995991" +
//                       ",51991353535,51991992888,51956949410,51992379040,51963537530,51952699725,51963539306" +
//                       ",51969427525,51944209577,51956409145,51955065786,51993624417,51965724359,51988153550" +
//                       ",51900400100,51991004848,51986906559,51997998995,51989516510,51975056419,51984123856" +
//                       ",51981270227,51982770364,51946224700,51976813626,51984119953,51986871446,51985140117" +
//                       ",51987264191,51962237720,51946249196,51998224953,51991792272,51954126948,51954173470" +
//                       ",51962319845,51979912927,51998135219,51989827381,51984777070,51979392433,51979912927" +
//                       ",51956748862,908986564,51995513050,19426739807004,51990989894,51969549681,51945217800" +
//                       ",51940147778,51956412151,931685361,51907382233,51983700565,3203612574,51941122674" +
//                       ",51991396423,51941122674,51991396423,51946451708,51993297151,51994717927,51996957302" +
//                       ",51993352493,51997565782,51995513050,51971872915,51994919017,51943571668,51989590303,51989742982,51979912927,51962319845,51979912927,51962216126,51947031286,51981213536,51999214542,51981322465,51992997744,51972224019,51998228736,51993623180,51990991900,51998029649,51952391151,991336666,51997917540,51969685293,51940439661,51999407034,51999435546,51987950717,51977529577,51991998998,51987803476,51999146604,51954193105,51991104317,51990990001,51949716596,51941464419,51992804021,51947741184,995994994,51991536180,51991566605,51983261457,51980687469,51985166048,51945954631,51993053254,974478629,992688044,995090266,51980534431,51947387583,959776227,56986039463,68866758676677,014177100,414,983334446,51947387583,993297151,987196292,0123,987196292,51993297151,947387583, ";


        String respuesta;
        List<OutputContact> listado = new ArrayList();

        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(RUTA_VALIDAR_CONTACTOS_ITALYOU);
            httpPost.addHeader("Content-Type", "application/json");

            try {

                JSONObject jsonTelefonos = new JSONObject();
                jsonTelefonos.put("contactos", listaNumeros);
                JsonElement elementoTrama = new JsonParser().parse(jsonTelefonos.toString());
                Log.e(Const.DEBUG_CONTACTS, "elementoTrama: " + elementoTrama.toString());
                StringEntity cadena = new StringEntity(elementoTrama.toString());
                httpPost.setEntity(cadena);

            } catch (Exception es) {
            }

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
            LogApp.log("Error IOException " + e.getMessage());
            respuesta = Const.COD_ERROR_CONEXION_WS;
            e.printStackTrace();
        }

        if (!respuesta.equals(Const.LISTA_VACIA) && !respuesta.equals(Const.COD_ERROR_CONEXION_WS)) {
            Gson gson = new Gson();
            listado = gson.fromJson(respuesta, new TypeToken<ArrayList<OutputContact>>() {
            }.getType());

//            //Codigo duro.
//            BeanRespuestaOperacion op =
//            new BeanRespuestaOperacion();
//            op.setError(Const.TAG_NO_ERROR);
//            op.setObjeto("Correcto");
//            op.setNombreObjecto("Anexo encontrado");

        }
//        resultadoOperacionListener.onResultadoOperacion(null);
        return listado;
    }


    public void sendEmailSupport(EntradaRegistarUsuario entrada) {
        String respuesta;

        try {
            //Set Json header
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(ENDPOINT_EMAIL_SUPPORT_REGISTER);
            httpPost.addHeader("Content-Type", "application/json");


            JsonElement frameJson = new JsonParser().parse(buildJSONBody(entrada));
            Log.e(Const.DEBUG, "Element to send by email: " + frameJson.toString());
            StringEntity cadena = new StringEntity(frameJson.toString());
            httpPost.setEntity(cadena);


            HttpResponse httpResponse = httpClient.execute(httpPost);
            StatusLine statusLine = httpResponse.getStatusLine();
            int statusCode = statusLine.getStatusCode();

            if (statusCode == 200) {
                respuesta = EntityUtils.toString(httpResponse.getEntity());
            } else
                respuesta = Const.COD_ERROR_CONEXION_WS;

            Log.e(Const.DEBUG, "Response WS email support: " + respuesta);
        } catch (ClientProtocolException e) {
            respuesta = Const.COD_ERROR_CONEXION_WS;
            Log.e(Const.DEBUG, "Response WS email support: " + respuesta);
            e.printStackTrace();
        } catch (IOException e) {
            respuesta = Const.COD_ERROR_CONEXION_WS;
            Log.e(Const.DEBUG, "Response WS email support: " + respuesta);
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        resultadoOperacionListener.onResultadoOperacion(null);
    }

    private String buildJSONBody(EntradaRegistarUsuario entrada) throws JSONException {

        JSONObject body = new JSONObject();
        body.put("password", entrada.getClave());
        body.put("language", entrada.getIdioma());
        body.put("userName", entrada.getNombre());
        body.put("idCountry", entrada.getIdPais());
        body.put("phoneNumber", entrada.getTelefono());
        body.put("timeZone", entrada.getZonaHoraria());
        body.put("zipcode", entrada.getIdPrefijo());
        body.put("email", entrada.getCorreo());
        return body.toString();
    }


    public void busquedaContactos(EntradaBuscarContactos entrada) {

        List<NameValuePair> parametros = new ArrayList<NameValuePair>();
        parametros.add(new BasicNameValuePair("Anexo", entrada.getAnexo()));
        parametros.add(new BasicNameValuePair("TipoDato", entrada.getTipo()));
        parametros.add(new BasicNameValuePair("Dato", entrada.getDato()));

        ParametrosWS parametro = new ParametrosWS();
        parametro.setMetodo(Const.METODO_GET);
        parametro.setRuta(ruta_busquedaContactos);
        parametro.setParametros(parametros);

        AsyncConexionWSTask conexion = new AsyncConexionWSTask(new AsyncTaskListener() {

            @Override
            public void onCompletadoTask(String resultado) {

                LogApp.log("[busquedaContactos] respuesta " + resultado);
                BeanRespuestaOperacion respuesta = new BeanRespuestaOperacion();

                if (!resultado.equals(Const.COD_ERROR_CONEXION_WS)) {

                    Gson gson = new Gson();
                    List<OutputContact> listado = new ArrayList<OutputContact>();
                    listado = gson.fromJson(resultado, new TypeToken<ArrayList<OutputContact>>() {
                    }.getType());
                    List<Object> lista = new ArrayList<Object>();
                    lista.addAll(listado);
                    respuesta.setObjeto(lista);

                } else {
                    respuesta.setError(resultado);
                }
                resultadoOperacionListener.onResultadoOperacion(respuesta);
            }
        });

        conexion.execute(parametro);
    }

    /**
     * ******************************************************obtener saldo***********************************************************************************
     */
    public void obtenerSaldo(String anexo) {
        String ruta = ruta_obtenerSaldo + anexo;
        ParametrosWS parametro = new ParametrosWS();
        parametro.setMetodo(Const.METODO_GET);
        parametro.setRuta(ruta);

        AsyncConexionWSTask conexion = new AsyncConexionWSTask(new AsyncTaskListener() {

            @Override
            public void onCompletadoTask(String resultado) {

                BeanRespuestaOperacion respuesta = new BeanRespuestaOperacion();

                if (resultado.length() > 2) {
                    Gson gson = new Gson();
                    BeanSaldo saldo = new BeanSaldo();
                    saldo = gson.fromJson(resultado, BeanSaldo.class);
                    respuesta.setObjeto(saldo);
                } else {
                    respuesta.setError(resultado);
                }
                resultadoOperacionListener.onResultadoOperacion(respuesta);
            }
        });
        conexion.execute(parametro);
    }

    /**
     * ***********************************************************enviar SMS*****************************************************************************************
     */
    public void enviarSMS(EntradaEnviarSMS entrada, BeanUsuario user) {
        String ruta = ruta_enviarSMS;

        JSONObject bodySMS = null;

        try {
            bodySMS = new JSONObject();
            bodySMS.accumulate("ID_Usuario", user.getID_Usuario());
            bodySMS.accumulate("Celular", entrada.getCelular());
            bodySMS.accumulate("Mensaje", entrada.getMensaje());
            bodySMS.accumulate("fecha", entrada.getFecha());
            bodySMS.accumulate("hora", entrada.getHora());
            bodySMS.accumulate("minuto", entrada.getMinuto());
            bodySMS.accumulate("agendar", entrada.getAgendar());
            bodySMS.accumulate("ID_Idioma", entrada.getIdioma());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        ParametrosWS parametro = new ParametrosWS();
        parametro.setMetodo(Const.METODO_POST);
        parametro.setRuta(ruta);
        parametro.setMascara(user.getO_ck());
        parametro.setFlagJSON(true);
        parametro.setJsonObjectParams(bodySMS);

        AsyncConexionWSTask conexion = new AsyncConexionWSTask(new AsyncTaskListener() {

            @Override
            public void onCompletadoTask(String resultado) {
                BeanRespuestaOperacion respuesta = new BeanRespuestaOperacion();
                if (resultado.length() > 2) {
                    SalidaResultado salida = new SalidaResultado();
                    Gson gson = new Gson();
                    salida = gson.fromJson(resultado, SalidaResultado.class);
                    respuesta.setObjeto(salida);
                } else {
                    respuesta.setError(resultado);
                }
                resultadoOperacionListener.onResultadoOperacion(respuesta);
            }

        });
        conexion.execute(parametro);
    }

    /**
     * *********************************************************** historial movimientos de saldo *****************************************************************************************
     */
    public void obtenerHistorialSaldo(String idioma, String anexo, String mascara) {
        String ruta = ruta_historialSaldo + idioma + "/" + anexo;
        ParametrosWS parametro = new ParametrosWS();
        parametro.setMetodo(Const.METODO_GET);
        parametro.setRuta(ruta);
        parametro.setMascara(mascara);

        AsyncConexionWSTask conexion = new AsyncConexionWSTask(new AsyncTaskListener() {
            @Override
            public void onCompletadoTask(String resultado) {
                BeanRespuestaOperacion respuesta = new BeanRespuestaOperacion();
                if (!resultado.equals(Const.COD_ERROR_CONEXION_WS)) {
                    Gson gson = new Gson();
                    List<SalidaMovimiento> listado = new ArrayList<SalidaMovimiento>();
                    listado = gson.fromJson(resultado, new TypeToken<ArrayList<SalidaMovimiento>>() {
                    }.getType());
                    List<Object> lista = new ArrayList<Object>();
                    lista.addAll(listado);
                    respuesta.setObjeto(lista);
                } else {
                    respuesta.setError(resultado);
                }
                resultadoOperacionListener.onResultadoOperacion(respuesta);
            }

        });
        conexion.execute(parametro);
    }

    /**
     * ***********************************************************redireccionar anexo*****************************************************************************************
     */
    public void redireccionarAnexo(EntradaRedireccionarAnexo entrada) {
        String ruta = ruta_redireccionarAnexo
                + entrada.getIdUsuario() + "/"
                + entrada.getIdPais() + "/"
                + entrada.getTelefono() + "/-/"
                + entrada.getFechaIni() + "/"
                + entrada.getFechaFin() + "/"
                + entrada.getFlagPermanente() + "/"
                + entrada.getFlagActivo();

        Log.e(Const.DEBUG, "Ruta redireccionar Anexo: " + ruta);
        ParametrosWS parametro = new ParametrosWS();
        parametro.setMetodo(Const.METODO_GET);
        parametro.setRuta(ruta);

        AsyncConexionWSTask conexion = new AsyncConexionWSTask(new AsyncTaskListener() {

            @Override
            public void onCompletadoTask(String resultado) {
                BeanRespuestaOperacion respuesta = new BeanRespuestaOperacion();

                if (resultado.length() > 2) {
                    SalidaResultado salida;
                    Gson gson = new Gson();
                    salida = gson.fromJson(resultado, SalidaResultado.class);
                    respuesta.setObjeto(salida);
                } else {
                    respuesta.setError(resultado);
                }
                resultadoOperacionListener.onResultadoOperacion(respuesta);
            }
        });
        conexion.execute(parametro);
    }

    /**
     * *********************************************************Historial de LLamadas (llamadas y mensajes de voz)****************************************************************************************************
     */
    public void obtenerHistorialLlamadas(String idioma, String anexo) {
        String ruta = ruta_historialLlamadas + idioma + "/" + anexo;
        ParametrosWS parametro = new ParametrosWS();
        parametro.setMetodo(Const.METODO_GET);
        parametro.setRuta(ruta);

        AsyncConexionWSTask conexion = new AsyncConexionWSTask(new AsyncTaskListener() {

            @Override
            public void onCompletadoTask(String resultado) {

                BeanRespuestaOperacion respuesta = new BeanRespuestaOperacion();
                if (!resultado.equals(Const.COD_ERROR_CONEXION_WS)) {
                    Gson gson = new Gson();
                    List<BeanLlamada> listado;
                    listado = gson.fromJson(resultado, new TypeToken<ArrayList<BeanLlamada>>() {
                    }.getType());
                    List<Object> lista = new ArrayList<>();
                    lista.addAll(listado);
                    respuesta.setObjeto(lista);
                } else {
                    respuesta.setError(resultado);
                }
                resultadoOperacionListener.onResultadoOperacion(respuesta);
            }
        });
        conexion.execute(parametro);
    }

    public void borrarLlamadas(final List<EntradaEliminarLlamadaMsj> listado, String anexo, String mascara) {
        ParametrosWS parametro = new ParametrosWS();
        parametro.setMetodo(Const.METODO_GET);
        parametro.setMascara(mascara);
        ConexionWS conexion = new ConexionWS();

        for (int i = 0; i < listado.size(); i++) {
            String ruta = ruta_eliminar_llamada + listado.get(i).getIdMovimiento() + "/" + anexo;
            parametro.setRuta(ruta);
            String respuesta = conexion.conexionServidor(parametro);
            if (respuesta.length() > 2) {
                SalidaResultado salida = new SalidaResultado();
                Gson gson = new Gson();
                salida = gson.fromJson(respuesta, SalidaResultado.class);
                if (salida.getResultado().equals(Const.RESULTADO_OK))
                    listado.get(i).setRealizado(Const.RESULTADO_OK);
                else
                    listado.get(i).setRealizado(Const.RESULTADO_ERROR);
            } else {
                listado.get(i).setRealizado(Const.RESULTADO_ERROR);
            }
        }

        BeanRespuestaOperacion respuesta = new BeanRespuestaOperacion();
        List<Object> lista = new ArrayList<Object>();
        lista.addAll(listado);
        respuesta.setObjeto(lista);
        resultadoOperacionListener.onResultadoOperacion(respuesta);
    }

    public void obtenerHistorialMensajesVoz(String idioma, String anexo) {    /*************************************/
        //String ruta = ruta_historialLlamadasVoz+idioma+"/687642";
        String ruta = ruta_historialLlamadasVoz + idioma + anexo;
        ParametrosWS parametro = new ParametrosWS();
        parametro.setMetodo(Const.METODO_GET);
        parametro.setRuta(ruta);

        AsyncConexionWSTask conexion = new AsyncConexionWSTask(new AsyncTaskListener() {

            @Override
            public void onCompletadoTask(String resultado) {
                BeanRespuestaOperacion respuesta = new BeanRespuestaOperacion();
                if (!resultado.equals(Const.COD_ERROR_CONEXION_WS)) {
                    Gson gson = new Gson();
                    List<BeanMensajeVoz> listado = new ArrayList<BeanMensajeVoz>();
                    listado = gson.fromJson(resultado, new TypeToken<ArrayList<BeanMensajeVoz>>() {
                    }.getType());
                    List<Object> lista = new ArrayList<Object>();
                    lista.addAll(listado);
                    respuesta.setObjeto(lista);
                } else {
                    respuesta.setError(resultado);
                }
                resultadoOperacionListener.onResultadoOperacion(respuesta);
            }
        });
        conexion.execute(parametro);
    }

    public void borrarMensajes(final List<EntradaEliminarLlamadaMsj> listado, String anexo) {
        ParametrosWS parametro = new ParametrosWS();
        parametro.setMetodo(Const.METODO_GET);
        ConexionWS conexion = new ConexionWS();

        for (int i = 0; i < listado.size(); i++) {
            String ruta = ruta_eliminar_mensaje_voz + listado.get(i).getIdMovimiento() + "/" + anexo;
            parametro.setRuta(ruta);
            String respuesta = conexion.conexionServidor(parametro);
            if (respuesta.length() > 2) {
                Log.i("Intico", "eliminar mensaje >2");
                SalidaResultado salida = new SalidaResultado();
                Gson gson = new Gson();
                salida = gson.fromJson(respuesta, SalidaResultado.class);
                if (salida.getResultado().equals(Const.RESULTADO_OK))
                    listado.get(i).setRealizado(Const.RESULTADO_OK);
                else
                    listado.get(i).setRealizado(Const.RESULTADO_ERROR);
            } else {
                listado.get(i).setRealizado(Const.RESULTADO_ERROR);
            }
        }

        BeanRespuestaOperacion respuesta = new BeanRespuestaOperacion();
        List<Object> lista = new ArrayList<Object>();
        lista.addAll(listado);
        respuesta.setObjeto(lista);
        resultadoOperacionListener.onResultadoOperacion(respuesta);
    }

    public void modificarEstadoEscuchado(String idMensaje, String anexo, String mascara) {
        String ruta = ruta_estado_mensaje_voz + idMensaje + "/" + anexo;
        ParametrosWS parametro = new ParametrosWS();
        parametro.setMetodo(Const.METODO_GET);
        parametro.setRuta(ruta);
        parametro.setMascara(mascara);
        AsyncConexionWSTask conexion = new AsyncConexionWSTask(new AsyncTaskListener() {

            @Override
            public void onCompletadoTask(String resultado) {
                BeanRespuestaOperacion respuesta = new BeanRespuestaOperacion();
                if (resultado.length() > 2) {
                    SalidaResultado salida = new SalidaResultado();
                    Gson gson = new Gson();
                    salida = gson.fromJson(resultado, SalidaResultado.class);
                    respuesta.setObjeto(salida);
                } else {
                    respuesta.setError(resultado);
                }
                resultadoOperacionListener.onResultadoOperacion(respuesta);
            }
        });
        conexion.execute(parametro);
    }

    /**
     * ********************************************************Consulta de Datos************************************************************************************
     */
    public void consultarDatos(String anexo, String mascara) {
        if (!anexo.equals(Const.cad_vacia)) {
            String ruta = ruta_consulta_datos + anexo;
            ParametrosWS parametro = new ParametrosWS();
            parametro.setMetodo(Const.METODO_GET);
            parametro.setRuta(ruta);
            parametro.setMascara(mascara);

            AsyncConexionWSTask conexion = new AsyncConexionWSTask(new AsyncTaskListener() {

                @Override
                public void onCompletadoTask(String resultado) {
                    BeanRespuestaOperacion respuesta = new BeanRespuestaOperacion();
                    if (!resultado.equals(Const.COD_ERROR_CONEXION_WS)) {
                        SalidaDatos salida = new SalidaDatos();
                        Gson gson = new Gson();
                        salida = gson.fromJson(resultado, SalidaDatos.class);
                        respuesta.setObjeto(salida);
                    } else {
                        respuesta.setError(resultado);
                    }
                    resultadoOperacionListener.onResultadoOperacion(respuesta);
                }
            });
            conexion.execute(parametro);
        }
    }

    /**
     * **************************************************************Cerrar Sesion************************************************************************************
     */
    public void cerrarSesion(String anexo, String mascara) {

        String ruta = ruta_cerrar_sesion + anexo;
        ParametrosWS parametro = new ParametrosWS();
        parametro.setMetodo(Const.METODO_GET);
        parametro.setRuta(ruta);
        parametro.setMascara(mascara);

        AsyncConexionWSTask conexion = new AsyncConexionWSTask(new AsyncTaskListener() {

            @Override
            public void onCompletadoTask(String resultado) {

                BeanRespuestaOperacion respuesta = new BeanRespuestaOperacion();

                if (resultado.length() > 2) {

                    SalidaResultado salida;
                    Gson gson = new Gson();
                    salida = gson.fromJson(resultado, SalidaResultado.class);
                    respuesta.setObjeto(salida);

                } else {
                    respuesta.setError(resultado);
                }

                Log.i("Intico", "onResultadoOperacion antes");
                resultadoOperacionListener.onResultadoOperacion(respuesta);
            }
        });
        conexion.execute(parametro);
    }

    public void reloadBalance(BeanUsuario user, String paymentId, String description, double amount) {
        String url = URL_RELOAD_BALANCE;
        JSONObject jsonBalance = new JSONObject();

        try {

            jsonBalance.accumulate("Id_Usuario", user.getID_Usuario());
            jsonBalance.accumulate("Id_Transaccion", paymentId);
            jsonBalance.accumulate("DescripcionTransaccion", description);
            jsonBalance.accumulate("Monto", amount);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        ParametrosWS paramsWS = new ParametrosWS();
        paramsWS.setJsonObjectParams(jsonBalance);
        paramsWS.setMascara(user.getO_ck());
        paramsWS.setRuta(url);
        paramsWS.setMetodo(Const.METODO_POST);
        paramsWS.setFlagJSON(true);

        AsyncConexionWSTask task = new AsyncConexionWSTask(new AsyncTaskListener() {

            @Override
            public void onCompletadoTask(String resultado) {

                BeanRespuestaOperacion operation = new BeanRespuestaOperacion();
                if (resultado != Const.cad_vacia) {
                    Gson gson = new Gson();
                    SalidaResultado salida = gson.fromJson(resultado, SalidaResultado.class);
                    operation.setObjeto(salida);
                    operation.setError(salida.getResultado());
                    operation.setNombreObjecto(SaldoActivity.class.getSimpleName());
                }

                resultadoOperacionListener.onResultadoOperacion(operation);
            }
        });

        task.execute(paramsWS);

    }


    //----------------------------- request sms ---------------------------------

    public void requestSmsVerification(String phoneNumber, String pinNumber) {
        boolean isTest = false;

        String URL = URL_REQUEST_SMS;
        String field_phone = "celular";
        String field_pin = "pin";

        //Make params
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair(field_phone, phoneNumber));
        params.add(new BasicNameValuePair(field_pin, pinNumber));

        //Make ws params
        ParametrosWS wsParams = new ParametrosWS();
        wsParams.setMetodo(Const.METODO_POST);
        wsParams.setRuta(URL);
        wsParams.setParametros(params);


        if (isTest) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        } else {
            //Call ws
            AsyncConexionWSTask task = new AsyncConexionWSTask(new AsyncTaskListener() {
                @Override
                public void onCompletadoTask(String resultado) {
                    resultadoOperacionListener.onResultadoOperacion(null);
                }
            });

            task.execute(wsParams);
        }

    }

    //----------------------------- request password by sms ---------------------------------

    public void requestPasswordBySMS(String prefijo, String telefono) {
        String url = URL_RECEIVER_PWD_BY_SMS;
        JSONObject json = new JSONObject();
        Log.e("prefijo", prefijo);
        Log.e("Celular", telefono);
        try {

            json.accumulate("prefijo", prefijo);
            json.accumulate("celular", telefono);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        ParametrosWS paramsWS = new ParametrosWS();
        paramsWS.setJsonObjectParams(json);
        paramsWS.setRuta(url);
        paramsWS.setMetodo(Const.METODO_POST);
        paramsWS.setFlagJSON(true);


        new AsyncConexionWSTask(new AsyncTaskListener() {
            @Override
            public void onCompletadoTask(String resultado) {
                Log.e("onCompletadoTask-> ", resultado);
                resultadoOperacionListener.onResultadoOperacion(null);
            }

        }).execute(paramsWS);

    }


}
