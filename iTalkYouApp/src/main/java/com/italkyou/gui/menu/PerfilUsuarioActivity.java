package com.italkyou.gui.menu;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;

import com.italkyou.beans.AppiTalkYou;
import com.italkyou.beans.BeanPais;
import com.italkyou.beans.BeanRespuestaOperacion;
import com.italkyou.beans.BeanUsuario;
import com.italkyou.beans.entradas.EntradaCambiarClave;
import com.italkyou.beans.entradas.EntradaPerfilUsuario;
import com.italkyou.beans.salidas.SalidaResultado;
import com.italkyou.conexion.ExcecuteRequest;
import com.italkyou.conexion.ExcecuteRequest.ResultadoOperacionListener;
import com.italkyou.controladores.LogicaPais;
import com.italkyou.controladores.LogicaPantalla;
import com.italkyou.controladores.LogicaUsuario;
import com.italkyou.dao.TablasBD;
import com.italkyou.gui.BaseActivity;
import com.italkyou.gui.R;
import com.italkyou.gui.chat.ChatMensajeActivity;
import com.italkyou.gui.personalizado.AdaptadorLista;
import com.italkyou.gui.personalizado.DialogoCambiarClave;
import com.italkyou.gui.personalizado.DialogoCambiarClave.onCambiarClave;
import com.italkyou.gui.personalizado.DialogoLista;
import com.italkyou.gui.personalizado.DialogoLista.onSeleccionarOpcionListener;
import com.italkyou.utils.AppUtil;
import com.italkyou.utils.ChatITY;
import com.italkyou.utils.Const;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;

import org.linphone.mediastream.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

@SuppressLint("NewApi")
public class PerfilUsuarioActivity extends BaseActivity implements OnClickListener, OnItemSelectedListener, OnCheckedChangeListener {

    private static final String TAG = PerfilUsuarioActivity.class.getSimpleName();
    private ImageButton btnImagen;
    private Button btnActualizar;
    private EditText etNombre;
    private EditText etPin;
    private EditText etCorreo;
    private TextView tvCambiarClave;
    private Spinner cbxPaises;
    private RadioGroup rgIdioma;
    private RadioGroup rgNotificacion;
    private String ruta_imagen;
    private BeanUsuario usuario;
    private EntradaPerfilUsuario entrada;
    private String idiomaAnt;
    private ParseObject usuarioChat;
    private Bitmap currentPhoto = null;
    private AppiTalkYou app;
    private static int RESULT_LOAD_IMAGE = 1;
    private static final int CAMERA_REQUEST = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.perfil_usuario);
        this.tipoMenu = Const.MENU_VACIA;
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        app = (AppiTalkYou) getApplication();
        usuario = app.getUsuario();
        usuarioChat = app.getUsuarioChat();
        iniciarComponentes();
    }

    private void iniciarComponentes() {

        btnImagen = (ImageButton) findViewById(R.id.btnImagen);
        btnImagen.setOnClickListener(this);
        btnActualizar = (Button) findViewById(R.id.btnActualizar);
        btnActualizar.setOnClickListener(this);
        etNombre = (EditText) findViewById(R.id.etNombreUsuario);
        etPin = (EditText) findViewById(R.id.etPinUsuario);
        etCorreo = (EditText) findViewById(R.id.etCorreoUsuario);
        tvCambiarClave = (TextView) findViewById(R.id.tvCambiarClave);
        tvCambiarClave.setOnClickListener(this);
        rgIdioma = (RadioGroup) findViewById(R.id.rgIdioma);
        cbxPaises = (Spinner) this.findViewById(R.id.cbxPaisesUsuario);
        rgNotificacion = (RadioGroup) findViewById(R.id.rgNotificacion);

        String idiomaSeleccionado = AppUtil.obtenerIdiomaLocal();
        List<Object> listaPaises = LogicaPais.obtenerListadoPaises(getApplicationContext(), idiomaSeleccionado);
        AdaptadorLista adaptadorPaises = new AdaptadorLista(getApplicationContext(), R.layout.celda_pais, listaPaises, BeanPais.class.getSimpleName(), usuario.getID_Idioma());
        adaptadorPaises.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        cbxPaises.setAdapter(adaptadorPaises);
        cbxPaises.setOnItemSelectedListener(this);
        rgIdioma.setOnCheckedChangeListener(this);
        rgNotificacion.setOnCheckedChangeListener(this);
        int pos = obtenerPaisUsuario(listaPaises);
        cbxPaises.setSelection(pos);
        entrada = new EntradaPerfilUsuario();
        entrada.setIdPais(((BeanPais) listaPaises.get(pos)).getID_Pais());
        entrada.setIdioma(usuario.getID_Idioma());
        ruta_imagen = "";
        idiomaAnt = usuario.getID_Idioma();
        cargarDatos();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    private void cargarDatos() {
        if (usuario == null) {
            usuario = LogicaUsuario.obtenerUsuario(this);
            app.setUsuario(usuario);
        }

        etNombre.setText(usuario.getNombres());
        ruta_imagen = usuario.getImagen_Usuario().trim();
        btnImagen.setImageBitmap(null);
        btnImagen.setBackgroundResource(0);

        if (usuario.getPin_Llamada().equals(Const.cad_vacia))
            etPin.setText("0000");
        else
            etPin.setText(usuario.getPin_Llamada());

        etCorreo.setText(usuario.getCorreo());
        if (usuario.getID_Idioma().equals(Const.IDIOMA_ES))
            rgIdioma.check(R.id.rbEspanol);
        else
            rgIdioma.check(R.id.rbIngles);

        if (usuario.getNotificacion().equals(Const.SOUND))
            rgNotificacion.check(R.id.rbSonido);
        else if (usuario.getNotificacion().equals(Const.VIBRATOR))
            rgNotificacion.check(R.id.rbVibrador);
        else
            rgNotificacion.check(R.id.rbSinSonido);


        if (usuarioChat.getBoolean(ChatITY.USER_FLAG_IMAGE)) {

            ParseFile file = (ParseFile) usuarioChat.get(ChatITY.USER_IMAGE);
            try {
                byte[] data = file.getData();
                Bitmap avatar = BitmapFactory.decodeByteArray(data, 0, data.length);
                btnImagen.setImageBitmap(avatar);
            } catch (ParseException e) {
                e.printStackTrace();
                btnImagen.setBackgroundResource(R.drawable.ic_contact);
            }

        } else {
            btnImagen.setBackgroundResource(R.drawable.ic_contact);
        }
    }

    @Override
    public void onClick(View v) {

        if (v == btnImagen) {

            FragmentManager fm = getSupportFragmentManager();
            DialogoLista dialogo = new DialogoLista();

            onSeleccionarOpcionListener listener = new onSeleccionarOpcionListener() {

                @Override
                public void setSeleccionarOpcionListener(String texto) {

                    if (texto.equals(Const.DESCRIPCION_GALERIA)) {
                        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(i, RESULT_LOAD_IMAGE);
                    } else {
                        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent, CAMERA_REQUEST);
                    }
                }
            };

            dialogo.onSeleccionarOpcionListener = listener;
            dialogo.titulo = getString(R.string.titulo_seleccionar);
            dialogo.pantalla = Const.PANTALLA_PERFIL;
            dialogo.show(fm, "");

        } else if (v == btnActualizar) {
            validarDatos();
        } else if (v == tvCambiarClave) {
            FragmentManager fm = getSupportFragmentManager();
            DialogoCambiarClave dialogo = new DialogoCambiarClave();
            onCambiarClave listener = new onCambiarClave() {
                @Override
                public void setCambiarClaveListener(EntradaCambiarClave entrada) {

                    cambiarClave(entrada);
                }
            };
            dialogo.mlistener = listener;
            dialogo.show(fm, "");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap rotated = null;
        if (data != null) {

            Uri selectedImage = data.getData();
            String picturePath = getRuta(selectedImage);
            ruta_imagen = picturePath;


            if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {


                try {
                    rotated = AppUtil.getCorrectlyOrientedImage(getApplicationContext(), selectedImage);
                } catch (IOException e) {
                    e.printStackTrace();
                }


//				ExifInterface ei = null;
//				try {
//					ei = new ExifInterface(picturePath);
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//
//				int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
//				Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
//				bitmap = Bitmap.createScaledBitmap(bitmap, 250, 250, false);
//
//				switch(orientation) {
//					case ExifInterface.ORIENTATION_ROTATE_90:
//						rotated = AppUtil.rotarBitmap(bitmap, 90);
//						break;
//					case ExifInterface.ORIENTATION_ROTATE_180:
//						rotated = AppUtil.rotarBitmap(bitmap, 180);
//						break;
//					case ExifInterface.ORIENTATION_ROTATE_270:
//						rotated = AppUtil.rotarBitmap(bitmap, 270);
//						break;
//					// etc.
//				}
//

                if (rotated != null) {
                    cargarImagen(rotated);
                }

            } else if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK && null != data) {
//                Bitmap photo = (Bitmap) data.getExtras().get("data");
                selectedImage = data.getData();


                try {
                    rotated = AppUtil.getCorrectlyOrientedImage(getApplicationContext(), selectedImage);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (rotated != null) {
                    cargarImagen(rotated);
                }
            }
        }
    }


    private void cargarImagen(Bitmap bmFoto) {
        btnImagen.setImageBitmap(null);
        btnImagen.setBackgroundResource(0);
        btnImagen.setImageBitmap(bmFoto);
        currentPhoto = bmFoto;
    }

    private String getRuta(Uri selectedImage) {
        String[] filePathColumn = {MediaStore.Images.Media.DATA};

        Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();

        return picturePath;
    }

    private void validarDatos() {
        String nombre = etNombre.getText().toString();
        String pin = etPin.getText().toString();
        String correo = etCorreo.getText().toString().trim();
        if (nombre.trim().equals(Const.cad_vacia) && pin.trim().equals(Const.cad_vacia)) {
            Crouton.showText(PerfilUsuarioActivity.this, getString(R.string.msj_error_datos_incompletos), Style.ALERT);
        } else if (nombre.trim().equals(Const.cad_vacia)) {
            Crouton.showText(PerfilUsuarioActivity.this, getString(R.string.msjrc_error_falta_nombre), Style.ALERT);
        } else if (pin.trim().equals(Const.cad_vacia)) {
            Crouton.showText(PerfilUsuarioActivity.this, getString(R.string.msjcp_error_falta_pin), Style.ALERT);
        } else {

            usuario.setNombres(nombre);
            usuario.setPin_Llamada(pin);
            usuario.setCorreo(correo);
            usuario.setImagen_Usuario(ruta_imagen);
            entrada.setAnexo(usuario.getAnexo());
            entrada.setNombre(nombre.replace(Const.ESPACIO_BLANCO, Const.ESPACIO_BLANCO_URL));
            entrada.setPin(pin.replace(Const.ESPACIO_BLANCO, Const.ESPACIO_BLANCO_URL));
            entrada.setO_ck(usuario.getO_ck());

            if (correo.equals(Const.cad_vacia)) entrada.setCorreo("-");
            else
                entrada.setCorreo(correo.replace(Const.ESPACIO_BLANCO, Const.ESPACIO_BLANCO_URL));

            //envia los datos
            if (AppUtil.existeConexionInternet(PerfilUsuarioActivity.this)) {
                pd = ProgressDialog.show(this, Const.TITULO_APP,
                        getString(R.string.msjcp_enviando_actualizar_perfil), true, true);
                pd.setCanceledOnTouchOutside(false);
                pd.setCancelable(false);

                actualizarPerfil();
            } else {
                Crouton.showText(PerfilUsuarioActivity.this, getString(R.string.msj_error_conexion_internet), Style.ALERT);
            }
        }
    }

    private void actualizarPerfil() {
        ExcecuteRequest ejecutar = new ExcecuteRequest(new ResultadoOperacionListener() {

            @Override
            public void onResultadoOperacion(BeanRespuestaOperacion respuesta) {

                if (respuesta.getError().equals(Const.cad_vacia)) {
                    SalidaResultado resultado = (SalidaResultado) respuesta.getObjeto();

                    if (resultado.getResultado().equals(Const.RESULTADO_ERROR)) {
                        pd.dismiss();
                        Crouton.showText(PerfilUsuarioActivity.this, getString(R.string.msj_error_producido_servidor), Style.ALERT);

                    } else if (resultado.getResultado().equals(Const.RESULTADO_OTRO_CASO)) {
                        pd.dismiss();
                        Crouton.showText(PerfilUsuarioActivity.this, getString(R.string.msjcp_error_correo_existente), Style.ALERT);

                    } else if (resultado.getResultado().equals(Const.RESULTADO_OK)) {
                        LogicaUsuario.guardarUsuario(getApplicationContext(), usuario, Const.BD_MODIFICARPU);

                        //actualizar datos en el servidor del chat
                        updateUserOnParse();
                        AppiTalkYou app = (AppiTalkYou) getApplication();
                        app.setUsuario(usuario);
                        pd.dismiss();
                        Crouton.showText(PerfilUsuarioActivity.this, getString(R.string.msjcp_info_actualizar_perfil), Style.INFO);
//						if (!idiomaAnt.equals(usuario.getID_Idioma()))
//							configurarIdioma(usuario.getID_Idioma(), true);
                    }
                } else {
                    pd.dismiss();
                    Crouton.showText(PerfilUsuarioActivity.this, getString(R.string.msj_error_conexion_ws), Style.ALERT);
                }
            }
        });
        ejecutar.actualizarPerfilUsuario(entrada);
    }

    @SuppressLint("NewApi")
    private void updateUserOnParse() {

//        ParseQuery query = ParseQuery.getQuery(ChatITY.TABLE_USER);
//        query.whereEqualTo(ChatITY.USER_ANNEX, usuario.getAnexo());
//        query.findInBackground();
        ParseObject ob = ParseObject.createWithoutData(ChatITY.TABLE_USER,app.getUsuarioChat().getObjectId());
        ob.fetchIfNeededInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject userToUpdate, ParseException e) {
                if (e == null) {
                    //Update name
                    userToUpdate.put(ChatITY.USER_USER, usuario.getNombres());
                    userToUpdate.put(ChatITY.USER_EMAIL,usuario.getCorreo());

                    //Update photo
                    if (currentPhoto != null) {
                        Bitmap imagenFin = currentPhoto;
                        ByteArrayOutputStream stream = new ByteArrayOutputStream(imagenFin.getWidth() * imagenFin.getHeight());
                        imagenFin.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        byte[] data = stream.toByteArray();

                        ParseFile file = new ParseFile(usuario.getAnexo() + ".png", data);
                        userToUpdate.put(ChatITY.USER_IMAGE, file);
                        userToUpdate.put(ChatITY.USER_FLAG_IMAGE, true);

                    }
                    userToUpdate.saveInBackground();
                } else {
                    Log.e(TAG, "Error parse, updating profile--> " + e.getMessage());
                }

            }
        });


    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View v, int pos, long id) {
        if (parent.getId() == R.id.cbxPaises) ;
        {
            entrada.setIdPais(((BeanPais) cbxPaises.getItemAtPosition(pos)).getID_Pais());
            usuario.setId_prefijo(((BeanPais) cbxPaises.getItemAtPosition(pos)).getID_Prefijo());
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {

    }

    private int obtenerPaisUsuario(List<Object> listado) {
        int pos = 0;
        for (int i = 0; i < listado.size(); i++) {
            BeanPais pais = (BeanPais) listado.get(i);
            if (pais.getID_Prefijo().equals(usuario.getId_prefijo())) {
                pos = i;
                break;
            }
        }
        return pos;
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (group == rgIdioma) {
            if (checkedId == R.id.rbEspanol) {
                usuario.setID_Idioma(Const.IDIOMA_ES);
                entrada.setIdioma(Const.IDIOMA_ES);

            } else {
                usuario.setID_Idioma(Const.IDIOMA_EN);
                entrada.setIdioma(Const.IDIOMA_EN);

            }
        } else if (group == rgNotificacion) {

            if (checkedId == R.id.rbSonido) {
                usuario.setNotificacion(Const.SOUND);
            } else if (checkedId == R.id.rbVibrador) {
                usuario.setNotificacion(Const.VIBRATOR);
            } else {
                usuario.setNotificacion(Const.WITHOUT_SOUND);
            }
        }
    }


    private void cambiarClave(final EntradaCambiarClave entrada) {
        entrada.setAnexo(usuario.getAnexo());
        entrada.setC_ok(usuario.getO_ck());
        if (AppUtil.existeConexionInternet(PerfilUsuarioActivity.this)) {
            pd = ProgressDialog.show(this, Const.TITULO_APP,
                    getString(R.string.msjcp_enviando_cambio_clave), true, true);
            pd.setCanceledOnTouchOutside(false);
            ExcecuteRequest ejecutar = new ExcecuteRequest(new ResultadoOperacionListener() {

                @Override
                public void onResultadoOperacion(BeanRespuestaOperacion respuesta) {
                    pd.dismiss();
                    if (respuesta.getError().equals(Const.cad_vacia)) {
                        SalidaResultado salida = (SalidaResultado) respuesta.getObjeto();
                        if (salida.getResultado().equals(Const.RESULTADO_ERROR)) {
                            Crouton.showText(PerfilUsuarioActivity.this, getString(R.string.msj_error_producido_servidor), Style.ALERT);
                        } else {
                            //actualizamos la clave
                            LogicaUsuario.modificarDato(getApplicationContext(), TablasBD.COLUMNA_CLAVE, entrada.getClaveNueva(), usuario.getAnexo());
                            AppiTalkYou app = (AppiTalkYou) getApplication();
                            app.setUsuario(usuario);
                            Crouton.showText(PerfilUsuarioActivity.this, getString(R.string.msjcp_info_cambio_clave), Style.INFO);
                        }
                    } else {
                        Crouton.showText(PerfilUsuarioActivity.this, getString(R.string.msj_error_conexion_ws), Style.ALERT);
                    }
                }
            });
            ejecutar.cambiarClave(entrada);
        } else {
            Crouton.showText(PerfilUsuarioActivity.this, getString(R.string.msj_error_conexion_internet), Style.ALERT);
        }
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                cargarPantallaPrincipal();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    private void cargarPantallaPrincipal() {
        //	if(!idiomaAnt.equals(usuario.getID_Idioma()))
        //LogicaPantalla.personalizarIntent(PerfilUsuarioActivity.this, VistaPrincipalActivity.class);
    /*	else
            super.onBackPressed();*/
        LogicaPantalla.personalizarIntentVistaPrincipal(PerfilUsuarioActivity.this, Const.PANTALLA_PRINCIPAL, PerfilUsuarioActivity.class.getSimpleName());
    }

    public void onBackPressed() {
        cargarPantallaPrincipal();
    }
}
