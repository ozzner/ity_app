package com.italkyou.gui.personalizado;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.italkyou.beans.BeanMensajeChat;
import com.italkyou.controladores.LogicaPantalla;
import com.italkyou.gui.R;
import com.italkyou.utils.Const;
import com.italkyou.utils.ChatITY;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;

import java.util.List;

public class AdaptadorMensajeChat extends ArrayAdapter<BeanMensajeChat> {


    private Activity activity;
    private List<BeanMensajeChat> datos;
    private String tipo;
    private int currentPosition;
    private View mView;
    private static int ID_SENDER = R.layout.cel_message_sender;
    private static int ID_RECIEVER = R.layout.cel_message_receiver;


    public AdaptadorMensajeChat(Activity activity, List<BeanMensajeChat> datos, String tipo) {
        super(activity.getApplicationContext(), 0, datos);
        this.activity = activity;
        this.datos = datos;
        this.tipo = tipo;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        currentPosition = position;
        mView = v;

        if (datos.size()>0)
        if (datos.get(position).isEnviado()) {
            OpcionMensaje oSender;

            if (mView == null)
                oSender = initSenderObject();
            else
                oSender = ((OpcionMensaje) v.getTag(ID_SENDER));


            BeanMensajeChat mensaje = datos.get(position);
            if (oSender == null)
                oSender = initSenderObject();

            setMensaje(oSender, mensaje);
            if (tipo.equals(ChatITY.tipo_privado)) {
                oSender.itemFecha.setText(mensaje.getFecha());
            } else {
                obtenerDatos(oSender, mensaje.getPropietarioMsj(), mensaje.getFecha());
            }

        } else {
            OpcionMensaje oReciever;
            if (mView == null)
                oReciever = initRecieverObject();
            else
                oReciever = ((OpcionMensaje) v.getTag(ID_RECIEVER));

            BeanMensajeChat mensaje = datos.get(position);
            if (oReciever == null)
                oReciever = initRecieverObject();

            setMensaje(oReciever, mensaje);
            if (tipo.equals(ChatITY.tipo_privado)) {
                oReciever.itemFecha.setText(mensaje.getFecha());
            } else {
                obtenerDatos(oReciever, mensaje.getPropietarioMsj(), mensaje.getFecha());
            }
        }

        return mView;
    }

    OpcionMensaje initRecieverObject() {

        OpcionMensaje oReciever = new OpcionMensaje();

        LayoutInflater vi = (LayoutInflater) activity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = vi.inflate(ID_RECIEVER, null);

        oReciever.itemMensaje = (TextView) mView.findViewById(R.id.txtMessage);
        oReciever.itemFecha = (TextView) mView.findViewById(R.id.txtInfo);
        oReciever.content = (LinearLayout) mView.findViewById(R.id.content);
        oReciever.contentWithBG = (LinearLayout) mView.findViewById(R.id.contentWithBackground);
        oReciever.itemIvAttach = (ImageView) mView.findViewById(R.id.iv_attached);

        mView.setTag(ID_RECIEVER, oReciever);

        return oReciever;
    }


    OpcionMensaje initSenderObject() {
        OpcionMensaje oSender = new OpcionMensaje();

        LayoutInflater vi = (LayoutInflater) activity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = vi.inflate(ID_SENDER, null);

        oSender.itemMensaje = (TextView) mView.findViewById(R.id.txtMessage);
        oSender.itemFecha = (TextView) mView.findViewById(R.id.txtInfo);
        oSender.content = (LinearLayout) mView.findViewById(R.id.content);
        oSender.contentWithBG = (LinearLayout) mView.findViewById(R.id.contentWithBackground);
        oSender.itemIvAttach = (ImageView) mView.findViewById(R.id.iv_attached);

        mView.setTag(ID_SENDER, oSender);
        return oSender;
    }

    private void obtenerDatos(final OpcionMensaje opcMensaje, ParseObject contacto, final String fecha) {

        contacto.fetchIfNeededInBackground(new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                opcMensaje.itemFecha.setText(object.getString(ChatITY.USER_USER) + Const.SEPARADOR_COMA + Const.ESPACIO_BLANCO + fecha);
            }
        });
    }

    private void setMensaje(OpcionMensaje optionMessage, final BeanMensajeChat oMessage) {


        if (oMessage.getTipo().equals(ChatITY.mensaje_texto)) {
            CharSequence msj = oMessage.getMensaje();

            optionMessage.itemMensaje.setVisibility(View.VISIBLE);
            optionMessage.itemMensaje.setText(msj);
            optionMessage.itemFecha.setText(oMessage.getFecha());
            optionMessage.itemIvAttach.setVisibility(View.GONE);

        } else {

            optionMessage.itemMensaje.setVisibility(View.GONE);
            optionMessage.itemIvAttach.setVisibility(View.VISIBLE);
            optionMessage.itemFecha.setText(oMessage.getFecha());
            optionMessage.itemIvAttach.setImageBitmap(oMessage.getPhoto());
            optionMessage.itemIvAttach.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
//                    Toast.makeText(activity.getApplicationContext(),activity.getResources().getString(R.string.label_opening_image), Toast.LENGTH_LONG).show();
                    LogicaPantalla.personalizarIntentVisualizarImagen(activity, oMessage.getPhoto(),oMessage.getChatMessage().getObjectId());
                }
            });
        }
    }


    static final class OpcionMensaje {
        TextView itemFecha;
        TextView itemMensaje;
        ImageView itemIvAttach;
        LinearLayout content;
        LinearLayout contentWithBG;
    }

}
