package com.italkyou.gui.llamada;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;

import com.italkyou.beans.AppiTalkYou;
import com.italkyou.beans.BeanLlamada;
import com.italkyou.beans.BeanMensajeVoz;
import com.italkyou.beans.BeanRespuestaOperacion;
import com.italkyou.beans.entradas.EntradaEliminarLlamadaMsj;
import com.italkyou.beans.salidas.SalidaHistorialLlamadas;
import com.italkyou.conexion.ExecuteRequest;
import com.italkyou.conexion.ExecuteRequest.ResultadoOperacionListener;
import com.italkyou.controladores.LogicaPantalla;
import com.italkyou.gui.BaseActivity;
import com.italkyou.gui.R;
import com.italkyou.gui.personalizado.AdaptadorListaCheckBox;
import com.italkyou.gui.personalizado.DialogoLista;
import com.italkyou.gui.personalizado.DialogoLista.onSeleccionarOpcionListener;
import com.italkyou.gui.personalizado.DialogoReproducirMensaje;
import com.italkyou.gui.personalizado.DialogoReproducirMensaje.onEstadoEscuchadoListener;
import com.italkyou.sip.SipManager;
import com.italkyou.utils.Const;
import com.italkyou.utils.AppUtil;

import java.util.ArrayList;
import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class HistorialLlamadasActivity extends BaseActivity implements OnItemClickListener, OnClickListener {

    private SalidaHistorialLlamadas historial;
    private ListView lvLlamadas, lvMensajesVoz;
    private AppiTalkYou app;
    private ImageView btnEliminarLlamadas;
    private ImageView btnEliminarMensajes;
    private AdaptadorListaCheckBox adaptadorLlamadas, adaptadorMensajes;
    private boolean activoEliminarLlamada, activoEliminarMensajes;
    private SipManager sipManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.historial_llamadas);
        this.tipoMenu = Const.MENU_VACIA;
        app = (AppiTalkYou) getApplication();
        historial = (SalidaHistorialLlamadas) this.getIntent().getSerializableExtra(Const.DATOS_LLAMADAS);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        iniciarComponentes();
    }

    @Override
    public void onItemClick(AdapterView<?> adv, View v, int posicion, long id) {

        Object objeto = adv.getItemAtPosition(posicion);
        if (adv == lvLlamadas) {

            if (!activoEliminarLlamada) {
                BeanLlamada llamada = (BeanLlamada) objeto;
                cargarOpcionesLlamadas(llamada, posicion);
            }

        } else {

            if (!activoEliminarMensajes) {
                BeanMensajeVoz mensaje = (BeanMensajeVoz) historial.getListaMensajesVoz().get(posicion);
                cargarOpcionesMensaje(mensaje, posicion);
            }
        }
    }


    @Override
    public void onClick(View v) {

        if (v == btnEliminarLlamadas) {

            if (activoEliminarLlamada) {

                boolean[] valores = adaptadorLlamadas.getValores();
                adaptadorLlamadas.setActivar(Const.mostrar_no_check);
                btnEliminarLlamadas.setImageResource(0);
                btnEliminarLlamadas.setImageResource(R.drawable.ic_delete_white);
                activoEliminarLlamada = false;
                realizarEliminarLlamadas(valores);

            } else {

                btnEliminarLlamadas.setImageResource(0);
                btnEliminarLlamadas.setImageResource(R.drawable.ic_done);
                adaptadorLlamadas.setActivar(Const.mostrar_check);
                adaptadorLlamadas.notifyDataSetChanged();
                activoEliminarLlamada = true;

            }

        } else if (v == btnEliminarMensajes) {

            if (activoEliminarMensajes) {

                boolean[] valores = adaptadorMensajes.getValores();
                adaptadorMensajes.setActivar(Const.mostrar_no_check);
                btnEliminarMensajes.setImageResource(0);
                btnEliminarMensajes.setImageResource(R.drawable.ic_delete_white);
                activoEliminarMensajes = false;
                realizarEliminarMensajes(valores);

            } else {

                btnEliminarMensajes.setImageResource(0);
                btnEliminarMensajes.setImageResource(R.drawable.ic_done);
                adaptadorMensajes.setActivar(Const.mostrar_check);
                adaptadorMensajes.notifyDataSetChanged();
                activoEliminarMensajes = true;

            }

        }
    }

    private void iniciarComponentes() {

        lvLlamadas = (ListView) findViewById(R.id.listLlamadasRecientes);
        adaptadorLlamadas = new AdaptadorListaCheckBox(getApplicationContext(), R.layout.celda_llamada, historial.getListaLlamadas(),
                BeanLlamada.class.getSimpleName(), "");

        adaptadorLlamadas.setActivar(Const.mostrar_no_check);
        lvLlamadas.setAdapter(adaptadorLlamadas);
        lvLlamadas.setOnItemClickListener(this);
        lvMensajesVoz = (ListView) findViewById(R.id.listMensajesVoz);
        adaptadorMensajes = new AdaptadorListaCheckBox(getApplicationContext(), R.layout.celda_llamada, historial.getListaMensajesVoz(),
                BeanMensajeVoz.class.getSimpleName(), "");

        adaptadorMensajes.setActivar(Const.mostrar_no_check);
        lvMensajesVoz.setAdapter(adaptadorMensajes);
        lvMensajesVoz.setOnItemClickListener(this);
        btnEliminarLlamadas = (ImageView) findViewById(R.id.btnEliminarLlamadas);
        btnEliminarLlamadas.setImageResource(R.drawable.ic_delete_white);
        btnEliminarLlamadas.setOnClickListener(this);
        btnEliminarMensajes = (ImageView) findViewById(R.id.btnEliminarMensajes);
        btnEliminarMensajes.setImageResource(R.drawable.ic_delete_white);
        btnEliminarMensajes.setOnClickListener(this);
        activoEliminarLlamada = false;
        activoEliminarMensajes = false;

    }

    private void cargarOpcionesLlamadas(final BeanLlamada llamada, final int posicion) {
        FragmentManager fm = getSupportFragmentManager();
        DialogoLista dialogo = new DialogoLista();

        onSeleccionarOpcionListener listener = new onSeleccionarOpcionListener() {

            @Override
            public void setSeleccionarOpcionListener(String texto) {

                if (texto.equals(Const.descripcion_llamar)) {
                    if (llamada.getTipo_Llamada().equals(Const.llamada_recibida)) {

                            //Call Phone
                            LogicaPantalla.makeAudioCallIntent(
                                    HistorialLlamadasActivity.this,
                                    llamada.getNro_Origen(),
                                    llamada.getNombre_Origen());

                    } else {
                            LogicaPantalla.makeAudioCallIntent(
                                    HistorialLlamadasActivity.this,
                                            llamada.getNro_Destino(),
                                            llamada.getNombre_Destino()
                                    );
                    }

                } else if (texto.equals(Const.descripcion_eliminar)) {

                    List<EntradaEliminarLlamadaMsj> lista = new ArrayList();
                    EntradaEliminarLlamadaMsj entrada = new EntradaEliminarLlamadaMsj();
                    entrada.setIdMovimiento(llamada.getID_Movimiento());
                    lista.add(entrada);
                    borrarLlamadas(lista);
                }
            }
        };
        dialogo.onSeleccionarOpcionListener = listener;
        dialogo.titulo = getString(R.string.titulo_seleccionar);
        dialogo.pantalla = Const.PANTALLA_HISTORIAL_LLAMADAS;
        dialogo.show(fm, "");
    }

    private void cargarOpcionesMensaje(final BeanMensajeVoz mensaje, final int posicion) {
        FragmentManager fm = getSupportFragmentManager();
        DialogoLista dialogo = new DialogoLista();
        onSeleccionarOpcionListener listener = new onSeleccionarOpcionListener() {

            @Override
            public void setSeleccionarOpcionListener(String texto) {

                if (texto.equals(Const.descripcion_escuchar)) {
                    reproducirAudio(mensaje, posicion);
                } else if (texto.equals(Const.descripcion_eliminar)) {
                    List<EntradaEliminarLlamadaMsj> lista = new ArrayList<EntradaEliminarLlamadaMsj>();
                    EntradaEliminarLlamadaMsj entrada = new EntradaEliminarLlamadaMsj();
                    entrada.setIdMovimiento(mensaje.getID_Mensajes_Voz());
                    lista.add(entrada);
                    borrarMensajes(lista);
                }
            }
        };
        dialogo.onSeleccionarOpcionListener = listener;
        dialogo.titulo = getString(R.string.titulo_seleccionar);
        dialogo.pantalla = Const.PANTALLA_HISTORIAL_MENSAJES;
        dialogo.show(fm, "");
    }

    private void reproducirAudio(final BeanMensajeVoz mensaje, final int posicion) {

        FragmentManager fm = getSupportFragmentManager();
        DialogoReproducirMensaje dialogo = new DialogoReproducirMensaje();

        onEstadoEscuchadoListener listener = new onEstadoEscuchadoListener() {

            @Override
            public void setEscuchadoListener(boolean estado) {
                if (estado) {
                    ((BeanMensajeVoz) historial.getListaMensajesVoz().get(posicion)).setEscuchado(Const.mensaje_escuchado);
                    adaptadorMensajes.notifyDataSetChanged();
                }
            }
        };
        dialogo.mlistener = listener;
        Bundle args = new Bundle();
        args.putSerializable(Const.DATOS_MENSAJE, mensaje);
        dialogo.setArguments(args);
        dialogo.show(fm, "");
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                break;

            default:
                return super.onOptionsItemSelected(item);

        }
        return true;
    }

    private void realizarEliminarLlamadas(boolean[] valores) {

        List<EntradaEliminarLlamadaMsj> lista = new ArrayList<EntradaEliminarLlamadaMsj>();
        for (int i = 0; i < valores.length; i++) {
            if (valores[i]) {
                EntradaEliminarLlamadaMsj entrada = new EntradaEliminarLlamadaMsj();
                BeanLlamada llamada = (BeanLlamada) historial.getListaLlamadas().get(i);
                entrada.setIdMovimiento(llamada.getID_Movimiento());
                lista.add(entrada);
            }
        }

        if (lista.size() > 0) {
            borrarLlamadas(lista);
        } else {
            adaptadorLlamadas.notifyDataSetChanged();
            adaptadorLlamadas.setValores();
        }
    }

    private void borrarLlamadas(final List<EntradaEliminarLlamadaMsj> lista) {

        pd = ProgressDialog.show(this, Const.TITULO_APP, getString(R.string.msj_eliminando_llamadas), true, true);
        pd.setCanceledOnTouchOutside(false);
        pd.setCancelable(false);

        if (AppUtil.existeConexionInternet(HistorialLlamadasActivity.this)) {

            new Thread(new Runnable() {

                public void run() {

                    ExecuteRequest ejecutar = new ExecuteRequest(new ResultadoOperacionListener() {

                        @Override
                        public void onOperationDone(BeanRespuestaOperacion respuesta) {
                            @SuppressWarnings("unchecked")
                            final List<EntradaEliminarLlamadaMsj> listado = (List<EntradaEliminarLlamadaMsj>) respuesta.getObjeto();
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    actualizarListadoLlamadas(listado);
                                }
                            });
                        }

                    });
                    ejecutar.borrarLlamadas(lista, app.getUsuario().getAnexo(), app.getUsuario().getO_ck());

                }
            }).start();
        } else {
            pd.dismiss();
            Crouton.showText(HistorialLlamadasActivity.this, getString(R.string.msj_error_conexion_internet), Style.ALERT);
        }
    }

    private void actualizarListadoLlamadas(List<EntradaEliminarLlamadaMsj> lista) {
        Log.i("Intico", "actualizarListadoLlamadas");
        int cont = historial.getListaLlamadas().size();

        for (int i = 0; i < lista.size(); i++) {
            EntradaEliminarLlamadaMsj entrada = lista.get(i);
            Log.i("Intico", "actualizar llamadas" + entrada.getRealizado());
            if (entrada.getRealizado().equals(Const.RESULTADO_OK)) {
                removerLlamada(entrada.getIdMovimiento());
            }
        }

        pd.dismiss();
        adaptadorLlamadas.notifyDataSetChanged();
        adaptadorLlamadas.setValores();

        if (cont == historial.getListaLlamadas().size()) {
            Crouton.showText(HistorialLlamadasActivity.this, getString(R.string.msj_error_eliminar_llamadas), Style.INFO);
        } else {
            Crouton.showText(HistorialLlamadasActivity.this, getString(R.string.msj_eliminar_llamadas), Style.INFO);
        }
    }

    private void removerLlamada(String idMovimiento) {

        for (int i = 0; i < historial.getListaLlamadas().size(); i++) {
            BeanLlamada llamada = (BeanLlamada) historial.getListaLlamadas().get(i);
            if (llamada.getID_Movimiento().equals(idMovimiento))
                historial.getListaLlamadas().remove(i);
        }
    }

    /***************************************************************************************************************************/

    private void realizarEliminarMensajes(boolean[] valores) {

        List<EntradaEliminarLlamadaMsj> lista = new ArrayList<EntradaEliminarLlamadaMsj>();

        for (int i = 0; i < valores.length; i++) {

            if (valores[i]) {
                EntradaEliminarLlamadaMsj entrada = new EntradaEliminarLlamadaMsj();
                BeanMensajeVoz mensaje = (BeanMensajeVoz) historial.getListaMensajesVoz().get(i);
                entrada.setIdMovimiento(mensaje.getID_Mensajes_Voz());
                lista.add(entrada);
            }
        }

        if (lista.size() > 0) {
            borrarMensajes(lista);
        } else {
            adaptadorMensajes.notifyDataSetChanged();
            adaptadorMensajes.setValores();
        }
    }

    private void borrarMensajes(final List<EntradaEliminarLlamadaMsj> lista) {

        pd = ProgressDialog.show(this, Const.TITULO_APP, getString(R.string.msj_eliminando_mensajes), true, true);
        pd.setCanceledOnTouchOutside(false);
        pd.setCancelable(false);

        if (AppUtil.existeConexionInternet(HistorialLlamadasActivity.this)) {

            new Thread(new Runnable() {
                public void run() {

                    ExecuteRequest ejecutar = new ExecuteRequest(new ResultadoOperacionListener() {
                        @Override
                        public void onOperationDone(BeanRespuestaOperacion respuesta) {

                            @SuppressWarnings("unchecked")
                            final List<EntradaEliminarLlamadaMsj> listado = (List<EntradaEliminarLlamadaMsj>) respuesta.getObjeto();
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    actualizarListadoMensajes(listado);
                                }
                            });
                        }
                    });

                    ejecutar.borrarMensajes(lista, app.getUsuario().getAnexo());
                }
            }).start();

        } else {
            pd.dismiss();
            Crouton.showText(HistorialLlamadasActivity.this, getString(R.string.msj_error_conexion_internet), Style.ALERT);
        }
    }

    private void actualizarListadoMensajes(List<EntradaEliminarLlamadaMsj> lista) {
        int cont = historial.getListaMensajesVoz().size();
        for (int i = 0; i < lista.size(); i++) {
            EntradaEliminarLlamadaMsj entrada = lista.get(i);
            Log.i("Intico", "actualizar Mensajes" + entrada.getRealizado());
            if (entrada.getRealizado().equals(Const.RESULTADO_OK)) {
                removerMensaje(entrada.getIdMovimiento());
            }
        }
        pd.dismiss();

        adaptadorMensajes.notifyDataSetChanged();
        adaptadorMensajes.setValores();

        if (cont == historial.getListaLlamadas().size()) {
            Crouton.showText(HistorialLlamadasActivity.this, getString(R.string.msj_error_eliminar_llamadas), Style.INFO);
        } else {
            Crouton.showText(HistorialLlamadasActivity.this, getString(R.string.msj_eliminar_llamadas), Style.INFO);
        }
    }

    private void removerMensaje(String idMovimiento) {

        for (int i = 0; i < historial.getListaMensajesVoz().size(); i++) {
            BeanLlamada llamada = (BeanLlamada) historial.getListaMensajesVoz().get(i);
            if (llamada.getID_Movimiento().equals(idMovimiento))
                historial.getListaMensajesVoz().remove(i);
        }

    }
}
