package com.italkyou.utils;

public class Const {


	public static final String SIP_SESSION_ID = "session_sip";
	public static final String FROM_ACTIVITY = "from.activity.location";
	public static final String NO_ITY = "0";
	public static final String NEED_SYNCHRONIZE = "0";
	public static final String SYNCHRONIZE_DONE = "1";
	/*Codigos de Errores*/
	public static String COD_NO_RESPUESTA_SERVIDOR = "-1";
	public static String COD_ERROR_CONEXION_WS = "-2";
	
	/*Para la BD*/
	public static String BD_INSERTAR    = "INSERT";
	public static String bd_modificar   = "UPDATE";
	public static String BD_MODIFICARIS = "UPDATEIS";
	public static String BD_MODIFICARPU = "UPDATEPU";

	public static String TITULO_APP = "ITalkYou";
	
	public static String METODO_POST = "POST";
	public static String METODO_GET = "GET";
	
	public static String cad_vacia = "";
	public static String RESULTADO_OK = "1";
	public static String RESULTADO_ERROR = "0";
	public static String RESULTADO_OTRO_CASO = "2";
	
	public static String IDIOMA_ES = "1";
	public static String IDIOMA_EN = "2";

	public static String PANTALLA_CONTACTO = "CONTACTO";
	public static String PANTALLA_PERFIL = "PERFIL";
	public static String PANTALLA_HISTORIAL_LLAMADAS = "HISTORIAL_LLAMADAS";
	public static String PANTALLA_HISTORIAL_MENSAJES = "HISTORIAL_MENSAJES";
	public static String PANTALLA_PRINCIPAL = "PRINCIPAL";
	public static String PANTALLA_FAVORITOS = "FAVORITOS";
	public static String PANTALLA_CHAT_SIMPLE = "CHAT_1A1";
	public static String PANTALLA_CHAT_GRUPAL = "CHAT_GRUPAL";
	public static String PANTALLA_LISTA_CONTACTO = "LISTADO_CONTACTOS";
	public static String PANTALLA_LISTA_CONTACTO_ANEXO = "LISTADO_CONTACTOS_ANEXO";
	public static String PANTALLA_EDITAR_CHAT_GRUPAL = "EDITARCHATGRUPAL";
	
	public static String MENU_GENERAL = "MNGENERAL";
	public static String MENU_VACIA = "MNVACIA";

	public static String NOMBRE_LOG = "LogITalkYou_";
	public static boolean FLAG_ESCRIBIR_LOG = false; // 1 escribe 0 no escribe

    public static String TEXTO_NUMERO          = "NUMERO";
    public static String TEXTO_TIPO            = "TIPO";
    public static String TIPO_LLAMADA_ITALKYOU = "TIPO_LLAMADA";

	public static String DATOS_REGISTRO = "DATOS_REGISTRO";
	public static String DATOS_CONTACTO = "DATOS_CONTACTO";
	public static String DATOS_MOVIMIENTOS = "MOVIMIENTOS_SALDO";
	public static String DATOS_LLAMADAS = "HISTORIAL_LLAMADAS";
	public static String DATOS_TIPO = "TIPO_PANTALLA";
	public static String DATOS_IS_SMS = "es_sms";
	public static String DATOS_INDICE = "INDICE";
	public static String DATOS_MENSAJE = "DATOS_MENSAJE_VOZ";
	public static String datos_imagen = "DATOS_MENSAJE_IMAGEN";
	public static String DATOS_GRUPO_CHAT = "CHAT_GRUPAL";
	public static String CADENA_PREFIJO = "+";
	public static String DESCRIPCION_GALERIA = "Galeria";
	public static String DESCRIPCION_CAMARA = "Camara";
	public static String SOUND = "1";
	public static String VIBRATOR = "2";
	public static String WITHOUT_SOUND = "3";
	public static String ESPACIO_BLANCO_URL = "%20";
	public static String ESPACIO_BLANCO = " ";
	
	public static String SEPARADOR_COMA = ",";
	public static int CANTIDAD_MAXIMA = 15;
	public static String LISTA_VACIA = "[]";
	
	public static String TIPO_NOMBRE = "1";
	public static String TIPO_ANEXO = "2";
	public static String TIPO_CORREO = "3";
	public static String tipo_contacto = "4";
	public static String USER_ITY = "1";
	public static String NO_USER_ITY = "2";
	public static String USUARIO_ITALKYOU_SELECCIONADO = "3";
	
	public static String almacenar_paises = "guardar_paises";
	public static String almacenar_usuario = "guardar_usuario";
	public static String almacenar_contactos = "guardar_contactos";
	public static String almacenar_numeros = "guardar_numeros";
	
	public static String prefijo_saldo = "US$ ";
	
	public static String descripcion_llamada_gratis = "Llamada Gratis";
	public static String descripcion_llamada_pago = "Llamada con Costo";
	public static String descripcion_llamada_sms = "Enviar SMS";
	



	/***viewpager**/
	//public static String vista_chat = "chat";
	//public static String vista_llamada = "llamada";
	//public static String vista_favorito = "favorito";
	
	/*****/
	public static String llamada_realizada = "8";
	public static String llamada_recibida  = "9";
	public static String ENVIO_SMS         = "2";
	public static String mensaje_escuchado    = "1";
	public static String mensaje_no_escuchado = "0";
	
	public static String descripcion_llamar   = "Llamar";
	public static String descripcion_eliminar = "Eliminar";
	public static String descripcion_escuchar = "Escuchar";
	
	public static String tipo_llamada_internacional = "1";
	public static String tipo_llamada_anexoVOIP = "3";
	
	public static String mostrar_check="1";
	public static String mostrar_no_check="0";
	
	public static String indice_0 = "0";
	public static String indice_1 = "1";
	public static String indice_2 = "2";
	public static String indice_3 = "3";

	public static String ESTADO_USUARIO_CONECTADO    = "1";
	public static String ESTADO_USUARIO_DESCONECTADO = "0";
	
	public static String no_mensaje_voz = "0";
	
	public static String SMS_AGENDAR = "1";
	public static String SMS_NO_AGENDAR = "0";
	
	public static String ANEXO_CONECTADO           = "1";
	public static String ANEXO_NO_CONECTADO        = "0";
	
	public static String status_connected = "conectado";
	public static String status_is_typing = "escribiendo";
	public static String status_no_connected = "no conectado";
	
	public static String identificador_chat        = "chatID";
	public static String identificador_notificador = "EstadoNotificacion";
	public static String muestra_notificacion      = "1";
	public static String no_muestra_notificacion   = "0";

    public static String CARACTER_VACIO="";
    //Flag para la verificacion de los servicios ITY

    public static int FLAG_VERIFICACION_ITY = 1;
    public static int FLAG_SIN_VERIFICACION_ITY = 0;

	/*custom const rsantillanc*/
	public static final int INDEX_DIALOG_CONTACT = 0;
	public static final int INDEX_DIALOG_CUSTOM_LIST = 1;
	public static final int INDEX_DIALOG_CHAT = 20;
	public static final int INDEX_DIALOG_RADIOBUTTON = -1;
	public static final String KEY_PHONENUMBER = "phoneNumber";
	public static final String SPLIT_COMA = ",";
	public static final String PANTALLA_CONTACTO_LLAMADA = "CONTACTO_LLAMADA";
	public static final String PANTALLA_CONTACTO_SMS = "CONTACTO_SMS";
	public static final String NONE = "none";

	//TAG
	public static final String TAG_MENU_ACTIONS = "menu_actions";
	public static final String TAG_MENU_RADIO_BUTTON = "menu_radio_button";
	public static final String TAG_MENU_ACTIONS_CHAT = "menu_chat";
	public static final String TAG_IS_PUSH = "is_push";
	public static final String TAG_CHANNEL_PRIVATE = "ch_p_";
	public static final String TAG_MY_CHANNEL = "ch_";
	public static final String TAG_CHANNEL_GROUP = "ch_g_";
	public static final String TAG_CURRENCY = " USD";

	public static final String TAG_DEFAULT_BALANCE = "0.0000";

    public static final String TAG_FLAG_ARCHIVED = "is_archived";
	public static final String TAG_TYPE_CHAT = "TypeChat";
	public static final String TAG_ERROR = "report_error";
	public static final String TAG_NO_ERROR = "no_error";
	public static final String TAG_CHATMESSAGE_ID="chat_message_id";
	public static final String TAG_CHAT_MESSAGE_LOCAL_STORE = "chat_message_localstore";
	public static final String TAG_DASH = "-";
	public static final String TAG_DOTS = ":";



	//DEBUG
	public static final String DEBUG = "_DEBUG_";
	public static final String DEBUG_PUSH = "_PUSH_";
	public static final String DEBUG_CONTACTS = "_CONTACT_";
	public static final String DEBUG_SIP = "_SIP_";
	public static final String DEBUG_CHAT = "_CHAT_";
	public static final String DEBUG_REQUEST = "_REQUEST_";
	public static final String DEBUG_BALANCE = "_BALANCE_";
	public static final String DEBUG_SERVICE = "_SERVICE_";
	public static final String DEBUG_DATABASE = "_DATABASE_";
	public static final String DEBUG_CALLS = "_CALL_";


	//PARSE
	public static final String APPLICATION_ID = "3AP4fcLfIdECpxm9oQ4TUL4LrioFoYaClQPU1vpZ";
	public static final String CLIENT_KEY = "Ts5sIDdkuSjy1NwUA6pXPwEbjZ7P1gbyvlAu29R4";
	public static final char CHAR_COMMA = ',';
	public static final char CHAR_DOT = '.';

	//CHAT_USER_CREATE
	public static final int CHATUSER_STATUS_ACTIVE = 0;
	public static final int CHATUSER_STATUS_ARCHIVED = 1;
	public static final int CHATUSER_STATUS_DELETED = 2;

	//DIRECTORY
	public static final String ITY_MAIN_DIR= "ItalkYou App";
	public static final String ITY_CHAT_IMAGE_DIR= "/ItalkYou chat";

	public static final String PREFIJO_ITALKYOU = "9916";
	public static final String CALL_ANNEX_CODE = "3";
	public static final String CALL_PHONE_CODE = "1";
}

