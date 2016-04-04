package com.italkyou.gui.personalizado;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.italkyou.controladores.LogicChat;
import com.italkyou.gui.R;
import com.italkyou.utils.ChatITY;
import com.italkyou.utils.Const;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;

import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdaptadorListaChat extends ArrayAdapter<ParseObject> {
    private Context context;
    private int idCelda;
    private List<ParseObject> datos;
    private String identificador_usuario;
    private boolean isArchived;
    private static final String TAG = AdaptadorListaChat.class.getSimpleName();

    public AdaptadorListaChat(Context context, int idCelda, List<ParseObject> datos, String identificador_usuario) {
        super(context, idCelda, datos);

        this.context = context;
        this.idCelda = idCelda;
        this.datos = datos;
        this.identificador_usuario = identificador_usuario;
        this.isArchived = false;
    }

    public AdaptadorListaChat(Context context, int idCelda, List<ParseObject> datos, String identificador_usuario, boolean flag) {
        super(context, idCelda, datos);

        this.context = context;
        this.idCelda = idCelda;
        this.datos = datos;
        this.identificador_usuario = identificador_usuario;
        this.isArchived = flag;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        opcionContacto opcContacto = new opcionContacto();

//        if (v == null) {
//
//            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            v = vi.inflate(idCelda, null);
//            opcContacto.itemImagen = (ImageView) v.findViewById(R.id.imgContacto);
//            opcContacto.itemNombre = (TextView) v.findViewById(R.id.tvNombre);
//            opcContacto.itemUltimoMensaje = (TextView) v.findViewById(R.id.tvUltimoMensaje);
//            opcContacto.itemFecha = (TextView) v.findViewById(R.id.tvFecha);
//            opcContacto.itemImageIsArchived = (ImageView) v.findViewById(R.id.iv_row_chat_archive);
//            v.setTag(opcContacto);
//
//        } else {
//            opcContacto = (opcionContacto) v.getTag();
//        }

        LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = vi.inflate(idCelda, null);
        opcContacto.itemImagen = (CircleImageView) v.findViewById(R.id.imgContacto);
        opcContacto.itemNombre = (TextView) v.findViewById(R.id.tvNombre);
        opcContacto.itemUltimoMensaje = (TextView) v.findViewById(R.id.tvUltimoMensaje);
        opcContacto.itemFecha = (TextView) v.findViewById(R.id.tvFecha);
        opcContacto.itemImageIsArchived = (ImageView) v.findViewById(R.id.iv_row_chat_archive);


        ParseObject chat = datos.get(position);
        Date lastDate = chat.getDate(LogicChat.COLUMN_LAST_DATE_MESSAGE);
        if (lastDate == null || lastDate.equals(Const.cad_vacia)) {
            opcContacto.itemFecha.setText(ChatITY.formatoFecha(new Date()));
        } else
            opcContacto.itemFecha.setText(ChatITY.formatoFecha(lastDate));

//        resetImageView(opcContacto.itemImagen);


        String ultimomsj = chat.getString(LogicChat.CHATUSER_COLUMN_LASTMESSAGE);

        if (ultimomsj == null)
            ultimomsj = "";

        if (ultimomsj.length() > 25)
            ultimomsj = ultimomsj.substring(0, 25) + "...";

        opcContacto.itemUltimoMensaje.setText(ultimomsj);
        String tipo = chat.getString(LogicChat.CHATUSER_COLUMN_TYPE);

        if (isArchived) {
            int color = Color.parseColor("#009ADA");
            opcContacto.itemImageIsArchived.setImageResource(R.drawable.ic_archive);
            opcContacto.itemImageIsArchived.clearColorFilter();
            opcContacto.itemImageIsArchived.setColorFilter(color);
        }

        //Chat grupal
        if (tipo.equals(ChatITY.tipo_grupal)) {

            opcContacto.itemNombre.setText(chat.getString(LogicChat.CHATUSER_COLUMN_NAME));
            boolean existe = chat.getBoolean(LogicChat.CHATUSER_COLUMN_FLAGIMAGE);

            if (existe) {
                final opcionContacto opc = opcContacto;
                ParseFile file = (ParseFile) chat.get(LogicChat.CHATUSER_COLUMN_IMAGE);
                file.getDataInBackground(new GetDataCallback() {
                    public void done(byte[] data, ParseException e) {

                        if (e == null) {
                            setImagen(opc, data);
                        } else {
                            opc.itemImagen.setImageResource(R.drawable.ic_group);
                        }


                    }
                });
            } else {
                opcContacto.itemImagen.setImageResource(R.drawable.ic_group);
            }
        } else {

            //Chat Privado
            List<String> lstpa = chat.getList(LogicChat.CHATUSER_COLUMN_MEMBERSID);
            List<ParseObject> lstcontacto = chat.getList(LogicChat.CHATUSER_COLUMN_MEMBERS);
            String contacto = lstpa.get(0);
            ParseObject sel;

            if (contacto.equals(identificador_usuario))
                sel = lstcontacto.get(1);
            else
                sel = lstcontacto.get(0);

            obtenerDatosContacto(opcContacto, sel);
        }

        return v;
    }

    private void resetImageView(ImageView ivImagen) {
        ivImagen.setImageResource(0);
        ivImagen.setImageDrawable(null);
        ivImagen.setImageBitmap(null);
    }

    private void obtenerDatosContacto(final opcionContacto v, ParseObject contacto) {
        contacto.fetchIfNeededInBackground(new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {

                if (e == null) {
                    v.itemNombre.setText(object.getString(ChatITY.USER_USER));
                    boolean existe = object.getBoolean(ChatITY.USER_FLAG_IMAGE);

                    if (existe) {
                        ParseFile file = (ParseFile) object.get(ChatITY.USER_IMAGE);
                        file.getDataInBackground(new GetDataCallback() {
                            public void done(byte[] data, ParseException e) {
                                if (e == null) {
                                    Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length);
                                    if(bm == null)
                                        v.itemImagen.setImageResource(R.drawable.ic_chat_user);
                                    else {
//                                        bm = AppUtil.getRoundedCornerImage(bm, true);
//                                        bm = Bitmap.createScaledBitmap(bm, 256, 256, false);
                                        setImagen(v, data);
                                    }

                                } else {
                                    v.itemImagen.setImageResource(R.drawable.ic_chat_user);
                                }
                            }
                        });
                    } else {
                        v.itemImagen.setImageResource(R.drawable.ic_chat_user);
                    }
                } else {
                    Log.e(TAG, "ERROR EN ADAPTAOR CHAT_CREATE PARSE");
                }

            }
        });

    }

    private void setImagen(opcionContacto v, byte[] bm) {
        Glide.with(getContext())
                .load(bm)
//                .placeholder(R.drawable.ic_logo_ity)
                .centerCrop()
                .into(v.itemImagen);
    }

    static class opcionContacto {
        private CircleImageView itemImagen;
        private TextView itemNombre;
        private TextView itemUltimoMensaje;
        private TextView itemFecha;
        private ImageView itemImageIsArchived;
    }

}
