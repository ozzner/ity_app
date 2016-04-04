package com.italkyou.gui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.italkyou.utils.Const;

import java.util.Locale;

public class BaseInicioActivity extends ActionBarActivity{

	protected ProgressDialog pdDialogoEspera;
	protected String idiomaSeleccionado;

    private static String PREFIJO_ESPANHOL = "es";
    private static String PREFIJO_INGLES   = "en";

  //  protected static boolean FLAG_ELIMINAR_PANTALLA ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
       // FLAG_ELIMINAR_PANTALLA=false;
       // LogApp.log("Creando "+FLAG_ELIMINAR_PANTALLA);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
        if(getMenuInflater()!=null && menu!=null)
		getMenuInflater().inflate(R.menu.mnidiomas, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

	    switch(item.getItemId()) {
	    	case android.R.id.home:
	    		onBackPressed();
	    		break;

	        case R.id.ites:
	        	cambiarIdiomaEspanhol();
	            break;

	        case R.id.iten:
	        	cambiarIdiomaIngles();
	            break;

	        default:
	           return super.onOptionsItemSelected(item);
	    }
	 
	    return true;
	    
	}

    @Override
    protected void onPostResume() {
        super.onPostResume();
//         SE ELIMINAN AUOTMATICAMENTE LAS PANTALLAS CUANDO REGRESEN DE LA PAUSA
//        if(FLAG_ELIMINAR_PANTALLA) {
//                 LogApp.log("despues del post resumen " + getClass().toString());
//            finalizarPantallas();
//        }
    }

    private void cambiarIdiomaIngles(){

        idiomaSeleccionado = Const.IDIOMA_EN;
        Locale locale_en = new Locale(PREFIJO_INGLES);
        Locale.setDefault(locale_en);

        Configuration config1 = new Configuration();
        config1.locale = locale_en;
        getBaseContext().getResources().updateConfiguration(config1,getBaseContext().getResources().getDisplayMetrics());
        Intent intent_en = getIntent();
        startActivity(intent_en);
        finish();
    }

    protected void personalizarBarraAcciones(){
        ActionBar barraAcciones=getSupportActionBar();
        barraAcciones.setBackgroundDrawable(getResources().getDrawable(R.color.pantone_240C));
    }

    protected void ocultarBarraAcciones(){
        getSupportActionBar().hide();
    }

    private void cambiarIdiomaEspanhol(){

        idiomaSeleccionado = Const.IDIOMA_ES;
        Locale locale_es = new Locale(PREFIJO_ESPANHOL);
        Locale.setDefault(locale_es);

        Configuration config = new Configuration();
        config.locale = locale_es;
        getBaseContext().getResources().updateConfiguration(config,getBaseContext().getResources().getDisplayMetrics());
        Intent intent_es = getIntent();
        finish();
        startActivity(intent_es);
    }

    protected  void finalizarPantallas(){
        finish();
    }

}

