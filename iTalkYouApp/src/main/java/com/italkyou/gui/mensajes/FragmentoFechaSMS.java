package com.italkyou.gui.mensajes;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import com.italkyou.gui.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class FragmentoFechaSMS extends Fragment implements DatePicker.OnDateChangedListener{

    private DatePicker dpFecha;
    private String strFecha;

    private OnFragmentInteractionListener mListener;

    public static FragmentoFechaSMS newInstance(String param1, String param2) {
        FragmentoFechaSMS fragment = new FragmentoFechaSMS();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    public FragmentoFechaSMS() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View vista=inflater.inflate(R.layout.fragmento_fecha_sms, container, false);
        inicializarFragmentoFecha(vista);
        inicializarFechaActual();
        return vista;
    }

    private void inicializarFragmentoFecha(View vista){
        dpFecha = (DatePicker) vista.findViewById(R.id.dpFechaSMS);
    }

    @Override
    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        setStrFecha(obtenerFecha(year,monthOfYear,dayOfMonth));
        //LogApp.log("Fecha Fecha="+ getStrFecha());
    }

    private void inicializarFechaActual(){
        Calendar c = Calendar.getInstance();
        int anho = c.get(Calendar.YEAR);
        int mes = c.get(Calendar.MONTH);
        int dia = c.get(Calendar.DAY_OF_MONTH);
        dpFecha.init(anho, mes, dia, this);
        strFecha = obtenerFecha(anho, mes, dia);

    }

    private String obtenerFecha(int anho, int mes, int dia){

        GregorianCalendar cal = new GregorianCalendar(anho, mes, dia);
        Date date = cal.getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        String fecha = df.format(date);
        //LogApp.log("FechaMensaje=" + fecha);

        return fecha;
    }

    public String getStrFecha() {
        return strFecha;
    }

    public void setStrFecha(String strFecha) {
        this.strFecha = strFecha;
    }


    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(Uri uri);
    }

}
