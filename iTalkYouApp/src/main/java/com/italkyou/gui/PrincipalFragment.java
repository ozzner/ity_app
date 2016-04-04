package com.italkyou.gui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.italkyou.beans.AppiTalkYou;
import com.italkyou.gui.personalizado.AdaptadorVistaPagina;
import com.italkyou.utils.Const;
import com.viewpagerindicator.TabPageIndicator;

public class PrincipalFragment extends Fragment {

    private static final String TAG = PrincipalFragment.class.getSimpleName() + Const.ESPACIO_BLANCO;
    private ViewPager vistaPagina;
    private TabPageIndicator indicadorTabPagina;
    private AdaptadorVistaPagina adaptadorVistaPagina;
    private static AppiTalkYou app;
    private static final int[] ICONOS = new int[]{R.drawable.ic_chat, R.drawable.ic_call, R.drawable.ic_sms};
    private static Activity mActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View vistaRaiz = inflater.inflate(R.layout.vista_principal, container, false);
        app = ((AppiTalkYou) getActivity().getApplication());
        VistaPrincipalActivity.setPantalla(Const.PANTALLA_PRINCIPAL);
        return vistaRaiz;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        inicializarComponentes(getArguments().getString(Const.DATOS_INDICE));
        ActionBar mActionBar = ((BaseActivity) getActivity()).getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(false);
        mActivity = getActivity();
//		 configBalance();

    }

    private void inicializarComponentes(String indice) {

        adaptadorVistaPagina = new AdaptadorVistaPagina(getActivity().getSupportFragmentManager(), ICONOS, getArguments());
        vistaPagina = (ViewPager) getActivity().findViewById(R.id.pager);
        vistaPagina.setCurrentItem(Integer.parseInt(indice));
        vistaPagina.setAdapter(adaptadorVistaPagina);

        indicadorTabPagina = (TabPageIndicator) getActivity().findViewById(R.id.indicator);
        indicadorTabPagina.setViewPager(vistaPagina);
        indicadorTabPagina.setCurrentItem(Integer.parseInt(indice));

    }

    public static PrincipalFragment nuevaInstancia(String indice) {
        PrincipalFragment fragmento = new PrincipalFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Const.DATOS_INDICE, indice);
        fragmento.setArguments(bundle);
        return fragmento;

    }

    public static PrincipalFragment nuevaInstancia(String indice, Bundle ars) {
        PrincipalFragment fragmento = new PrincipalFragment();
        ars.putString(Const.DATOS_INDICE, indice);
        fragmento.setArguments(ars);
        return fragmento;

    }

}
