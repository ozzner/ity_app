package com.italkyou.gui.mensajes;

import android.support.v4.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TimePicker;

import com.italkyou.gui.R;

import java.util.Calendar;


public class FragmentoHoraSMS extends Fragment implements TimePicker.OnTimeChangedListener{

    private TimePicker tpHora;
    private OnFragmentInteractionListener mListener;


    public static FragmentoHoraSMS newInstance(String param1, String param2) {
        FragmentoHoraSMS fragment = new FragmentoHoraSMS();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    public FragmentoHoraSMS() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View vista=inflater.inflate(R.layout.fragmento_hora_sms, container, false);
        inicializarFragmentoHoraSMS(vista);
        return vista;
    }

    private void inicializarFragmentoHoraSMS(View vista){
        setTpHora((TimePicker)vista.findViewById(R.id.tpHora));

        Calendar c = Calendar.getInstance();
        int hora = c.get(Calendar.HOUR_OF_DAY);
        int minuto = c.get(Calendar.MINUTE);


        getTpHora().setCurrentHour(hora);
        getTpHora().setCurrentMinute(minuto);
        getTpHora().setOnTimeChangedListener(this);
    }

    @Override
    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
        getTpHora().setCurrentHour(hourOfDay);
        getTpHora().setCurrentMinute(minute);
    }

    public TimePicker getTpHora() {
        return tpHora;
    }

    public void setTpHora(TimePicker tpHora) {
        this.tpHora = tpHora;
    }


    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(Uri uri);
    }

}
