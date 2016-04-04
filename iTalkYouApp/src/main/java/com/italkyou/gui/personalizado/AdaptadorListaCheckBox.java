package com.italkyou.gui.personalizado;

import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;

import com.italkyou.beans.BeanContact;
import com.italkyou.beans.BeanFavorito;
import com.italkyou.beans.BeanLlamada;
import com.italkyou.beans.BeanMensajeVoz;
import com.italkyou.gui.R;
import com.italkyou.utils.AppUtil;
import com.italkyou.utils.ChatITY;
import com.italkyou.utils.Const;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;

import java.io.InputStream;
import java.util.List;

public class AdaptadorListaCheckBox extends ArrayAdapter<Object> {

    private Context context;
    private int idCelda;
    private List<Object> datos;
    private String claseObj;
    private String otroDato;
    private boolean[] valores;
    private String activar;

    public AdaptadorListaCheckBox(Context context, int idCelda_personalizada, List<Object> datos, String clase, String otroDato) {
        super(context, idCelda_personalizada, datos);

        this.context = context;
        this.idCelda = idCelda_personalizada;
        this.datos = datos;
        this.claseObj = clase;
        this.otroDato = otroDato;
        this.valores = new boolean[datos.size()];
        this.activar = Const.mostrar_no_check;
    }

    public void setActivar(String valor) {
        this.activar = valor;
    }

    public boolean[] getValores() {
        return valores;
    }

    public void setValores() {
        valores = null;
        valores = new boolean[datos.size()];
    }

    public void activarCheck(int position) {
        valores[position] = true;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (claseObj.equals(BeanLlamada.class.getSimpleName())) {
            opcionLlamadaHolder opcionLlamada = new opcionLlamadaHolder();
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(idCelda, null);
                opcionLlamada.itemImagen = (ImageView) v.findViewById(R.id.imgTipoLlamada);
                opcionLlamada.itemNombre = (TextView) v.findViewById(R.id.tvNombreLlamada);
                opcionLlamada.itemTelefono = (TextView) v.findViewById(R.id.tvNumeroLlamada);
                opcionLlamada.itemFecha = (TextView) v.findViewById(R.id.tvFechaLlamada);
                opcionLlamada.itemEliminar = (CheckedTextView) v.findViewById(R.id.chkEliminar);
                v.setTag(opcionLlamada);
            } else {
                opcionLlamada = (opcionLlamadaHolder) v.getTag();
            }

            BeanLlamada llamada = (BeanLlamada) datos.get(position);
            opcionLlamada.itemFecha.setText(llamada.getFecha());
            opcionLlamada.itemImagen.setBackgroundResource(0);
            if (llamada.getTipo_Llamada().equals(Const.llamada_realizada)) {
                opcionLlamada.itemImagen.setBackgroundResource(R.drawable.ic_call_made);
                opcionLlamada.itemNombre.setText(llamada.getNombre_Destino());


                if (llamada.getNro_Origen().length() == 6)
                    opcionLlamada.itemTelefono.setText(llamada.getNro_Destino());
                else
                    opcionLlamada.itemTelefono.setText(llamada.getNro_Destino());

            } else if (llamada.getTipo_Llamada().equals(Const.llamada_recibida)) {
                opcionLlamada.itemImagen.setBackgroundResource(R.drawable.ic_call_received);
                opcionLlamada.itemNombre.setText(llamada.getNombre_Origen());
                if (llamada.getNro_Origen().length() == 6)
                    opcionLlamada.itemTelefono.setText(llamada.getNro_Origen());
                else
                    opcionLlamada.itemTelefono.setText(llamada.getNro_Origen());

            } else if (llamada.getTipo_Llamada().equals(Const.ENVIO_SMS)) {
                opcionLlamada.itemImagen.setBackgroundResource(R.drawable.ic_email);
                opcionLlamada.itemNombre.setText(llamada.getNombre_Destino());


                if (llamada.getNro_Origen().length() == 6)
                    opcionLlamada.itemTelefono.setText(llamada.getNro_Destino());
                else
                    opcionLlamada.itemTelefono.setText(llamada.getNro_Destino());

            }

            if (activar.equals(Const.mostrar_check)) {
                opcionLlamada.itemEliminar.setVisibility(CheckedTextView.VISIBLE);
                opcionLlamada.itemEliminar.setChecked(valores[position]);
                opcionLlamada.itemEliminar.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        if (((CheckedTextView) v).isChecked()) {
                            Log.i("Intico", "checkbox true");
                            ((CheckedTextView) v).setChecked(false);
                            valores[position] = false;
                        } else {
                            Log.i("Intico", "checkbox false");
                            ((CheckedTextView) v).setChecked(true);
                            valores[position] = true;
                        }
                    }
                });
            } else {
                opcionLlamada.itemEliminar.setVisibility(CheckedTextView.GONE);
            }

        } else if (claseObj.equals(BeanMensajeVoz.class.getSimpleName())) {
            opcionLlamadaHolder opcionMensaje = new opcionLlamadaHolder();
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(idCelda, null);
                opcionMensaje.itemImagen = (ImageView) v.findViewById(R.id.imgTipoLlamada);
                opcionMensaje.itemNombre = (TextView) v.findViewById(R.id.tvNombreLlamada);
                opcionMensaje.itemTelefono = (TextView) v.findViewById(R.id.tvNumeroLlamada);
                opcionMensaje.itemFecha = (TextView) v.findViewById(R.id.tvFechaLlamada);
                opcionMensaje.itemEliminar = (CheckedTextView) v.findViewById(R.id.chkEliminar);
                v.setTag(opcionMensaje);
            } else {
                opcionMensaje = (opcionLlamadaHolder) v.getTag();
            }

            BeanMensajeVoz mensaje = (BeanMensajeVoz) datos.get(position);
            opcionMensaje.itemNombre.setText(mensaje.getNombre_Contacto());
            opcionMensaje.itemTelefono.setText(mensaje.getQuien_Llama());
            opcionMensaje.itemFecha.setText(mensaje.getFecha());
            opcionMensaje.itemImagen.setBackgroundResource(0);
            if (mensaje.getEscuchado().equals(Const.mensaje_escuchado)) {
                opcionMensaje.itemImagen.setBackgroundResource(R.drawable.msjvoz_escuchado);
            } else {
                opcionMensaje.itemImagen.setBackgroundResource(R.drawable.msjvoz_no_escuchado);
            }

            if (activar.equals(Const.mostrar_check)) {
                opcionMensaje.itemEliminar.setVisibility(CheckedTextView.VISIBLE);
                opcionMensaje.itemEliminar.setChecked(valores[position]);
                opcionMensaje.itemEliminar.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        if (((CheckedTextView) v).isChecked()) {
                            ((CheckedTextView) v).setChecked(false);
                            valores[position] = false;
                        } else {
                            ((CheckedTextView) v).setChecked(true);
                            valores[position] = true;
                        }
                    }
                });
            } else {
                opcionMensaje.itemEliminar.setVisibility(CheckedTextView.GONE);
            }
        } else if (claseObj.equals(BeanContact.class.getSimpleName())) {
            //if (otroDato.equals(Const.cad_vacia)){
            opcionContactoHolder opcContactoHolder = new opcionContactoHolder();
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(idCelda, null);
                opcContactoHolder.itemImagen = (ImageView) v.findViewById(R.id.imgContacto);
                opcContactoHolder.itemNombre = (TextView) v.findViewById(R.id.tvNombreContacto);
                opcContactoHolder.itemEsUsuario = (ImageView) v.findViewById(R.id.imgEsUsuario);
                opcContactoHolder.itemSeleccionar = (CheckedTextView) v.findViewById(R.id.chkEliminarFavorito);
            } else {
                opcContactoHolder = (opcionContactoHolder) v.getTag();
            }
            v.setTag(opcContactoHolder);
            BeanContact contacto = (BeanContact) datos.get(position);
            opcContactoHolder.itemNombre.setText(contacto.getNombre());
            opcContactoHolder.itemImagen.setBackgroundResource(0);
            opcContactoHolder.itemImagen.setImageBitmap(null);
            opcContactoHolder.itemEsUsuario.setBackgroundResource(0);

            if (contacto.getFoto() > 0) {
                Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contacto.getIdContacto());
                InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(context.getContentResolver(), uri);
                if (input != null) {
                    Bitmap bm = BitmapFactory.decodeStream(input);
                    opcContactoHolder.itemImagen.setImageBitmap(AppUtil.getRoundedCornerImage(bm, true));
                } else {
                    opcContactoHolder.itemImagen.setBackgroundResource(R.drawable.ic_contact);
                }

            } else {
                opcContactoHolder.itemImagen.setBackgroundResource(R.drawable.ic_contact);
            }
            if (contacto.getUsuarioITY().equals(Const.USER_ITY)) {
                opcContactoHolder.itemEsUsuario.setVisibility(View.VISIBLE);

            } else
                opcContactoHolder.itemEsUsuario.setVisibility(View.GONE);


            if (activar.equals(Const.mostrar_check)) {
                opcContactoHolder.itemSeleccionar.setVisibility(CheckedTextView.VISIBLE);
                opcContactoHolder.itemSeleccionar.setChecked(valores[position]);
                opcContactoHolder.itemSeleccionar.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        if (((CheckedTextView) v).isChecked()) {
                            ((CheckedTextView) v).setChecked(false);
                            valores[position] = false;
                        } else {
                            ((CheckedTextView) v).setChecked(true);
                            valores[position] = true;
                        }
                    }
                });
            } else {
                opcContactoHolder.itemSeleccionar.setVisibility(CheckedTextView.GONE);
            }
            //	}
        } else if (claseObj.equals(ParseObject.class.getSimpleName())) {

            try {
                opcionContactoItalkyouHolder opcContactoHolder = new opcionContactoItalkyouHolder();

                LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(idCelda, null);
                opcContactoHolder.itemImagen = (ImageView) v.findViewById(R.id.imgContactoItalkyou);
                opcContactoHolder.itemNombre = (TextView) v.findViewById(R.id.tvNombreContactoItalkyou);
                opcContactoHolder.itemTelefono = (TextView) v.findViewById(R.id.tvNumeroContactoItalkyou);
                opcContactoHolder.itemAnexo = (TextView) v.findViewById(R.id.tvAnexoContactoItalkyou);
                opcContactoHolder.itemSeleccionar = (CheckedTextView) v.findViewById(R.id.chkAgregarContacto);

                ParseObject contacto = (ParseObject) datos.get(position);
                opcContactoHolder.itemNombre.setText(contacto.getString(ChatITY.USER_USER));
                opcContactoHolder.itemTelefono.setText(contacto.getString(ChatITY.USER_PHONE));
                opcContactoHolder.itemAnexo.setText(AppUtil.formatAnnex(contacto.getString(ChatITY.USER_ANNEX)));
                opcContactoHolder.itemImagen.setBackgroundResource(0);
                opcContactoHolder.itemImagen.setImageBitmap(null);
                if (contacto.getBoolean(ChatITY.USER_FLAG_IMAGE)) {
                    ParseFile file = (ParseFile) contacto.get(ChatITY.USER_IMAGE);

                    try {
                        if (file == null)
                            return null;

                        byte[] data = file.getData();
                        Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length);
                        opcContactoHolder.itemImagen.setImageBitmap(AppUtil.getRoundedCornerImage(bm, true));
                    } catch (ParseException e) {
                        // TODO Auto-generated catch block
                        opcContactoHolder.itemImagen.setBackgroundResource(R.drawable.ic_contact);
                        e.printStackTrace();
                    }catch (NullPointerException ex){

                    }
                } else {
                    opcContactoHolder.itemImagen.setBackgroundResource(R.drawable.ic_contact);
                }
                if (activar.equals(Const.mostrar_check)) {
                    opcContactoHolder.itemSeleccionar.setVisibility(CheckedTextView.VISIBLE);
                    opcContactoHolder.itemSeleccionar.setChecked(valores[position]);
                    opcContactoHolder.itemSeleccionar.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub
                            if (((CheckedTextView) v).isChecked()) {
                                ((CheckedTextView) v).setChecked(false);
                                valores[position] = false;
                            } else {
                                ((CheckedTextView) v).setChecked(true);
                                valores[position] = true;
                            }
                        }
                    });
                }
            }catch (NullPointerException ex){
                datos.remove(position);
            }


        } else if (claseObj.equals(BeanFavorito.class.getSimpleName())) {
            opcionContactoHolder opcContactoHolder = new opcionContactoHolder();
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(idCelda, null);
                opcContactoHolder.itemImagen = (ImageView) v.findViewById(R.id.imgContacto);
                opcContactoHolder.itemNombre = (TextView) v.findViewById(R.id.tvNombreContacto);
                opcContactoHolder.itemEsUsuario = (ImageView) v.findViewById(R.id.imgEsUsuario);
                opcContactoHolder.itemSeleccionar = (CheckedTextView) v.findViewById(R.id.chkEliminarFavorito);
            } else {
                opcContactoHolder = (opcionContactoHolder) v.getTag();
            }

            v.setTag(opcContactoHolder);
            BeanContact contacto = (BeanContact) ((BeanFavorito) datos.get(position)).getContacto();

            opcContactoHolder.itemNombre.setText(contacto.getNombre());
            opcContactoHolder.itemImagen.setBackgroundResource(0);
            opcContactoHolder.itemImagen.setImageBitmap(null);
            opcContactoHolder.itemEsUsuario.setBackgroundResource(0);


            if (contacto.getFoto() > 0) {

                Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contacto.getIdContacto());
                InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(context.getContentResolver(), uri);

                if (input != null) {
                    Bitmap bm = BitmapFactory.decodeStream(input);
                    opcContactoHolder.itemImagen.setImageBitmap(AppUtil.getRoundedCornerImage(bm, true));
                } else {
                    opcContactoHolder.itemImagen.setBackgroundResource(R.drawable.ic_contact);
                }

            } else {
                opcContactoHolder.itemImagen.setBackgroundResource(R.drawable.ic_contact);
            }
            if (contacto.getUsuarioITY().equals(Const.USER_ITY)) {
                opcContactoHolder.itemEsUsuario.setVisibility(View.VISIBLE);
            } else {
                opcContactoHolder.itemEsUsuario.setVisibility(View.GONE);

            }

            if (activar.equals(Const.mostrar_check)) {
                opcContactoHolder.itemSeleccionar.setVisibility(CheckedTextView.VISIBLE);
                opcContactoHolder.itemSeleccionar.setChecked(valores[position]);
                opcContactoHolder.itemSeleccionar.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        if (((CheckedTextView) v).isChecked()) {
                            ((CheckedTextView) v).setChecked(false);
                            valores[position] = false;
                        } else {
                            ((CheckedTextView) v).setChecked(true);
                            valores[position] = true;
                        }
                    }
                });
            } else {
                opcContactoHolder.itemSeleccionar.setVisibility(CheckedTextView.GONE);
            }
        }

        return v;
    }

    private class opcionLlamadaHolder {
        public ImageView itemImagen;
        public TextView itemNombre;
        public TextView itemTelefono;
        public TextView itemFecha;
        public CheckedTextView itemEliminar;
    }

    private class opcionContactoHolder {
        public ImageView itemImagen;
        public TextView itemNombre;
        public ImageView itemEsUsuario;
        public CheckedTextView itemSeleccionar;
    }

    private class opcionContactoItalkyouHolder {
        public ImageView itemImagen;
        public TextView itemNombre;
        public TextView itemTelefono;
        public TextView itemAnexo;
        public CheckedTextView itemSeleccionar;
    }
}
