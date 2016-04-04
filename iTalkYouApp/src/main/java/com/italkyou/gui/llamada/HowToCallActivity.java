package com.italkyou.gui.llamada;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.Menu;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.widget.TextView;

import com.italkyou.gui.R;
import com.italkyou.utils.AppUtil;
import com.italkyou.utils.Const;

public class HowToCallActivity extends AppCompatActivity {

    private static final java.lang.String HTML_BODY_ES =
            "              <p style=\"text-align: justify;\">" +
                    "        <center><h1>Para marcar un número</h1></center>" +
                    "        <h4><font color='#385E9D'>Anexo</font></h4>" +
                    "        Si desea marcar a un anexo de <font color='#B0008E'>Italkyou</font> debe digitar los <b>6 números</b> correspondientes a la extensión.<br>\n" +
                    "        Por ejemplo: <font color='#02A902'><b>470200</b></font><br><br><br>" +
                    "        <h4><font color='#385E9D'>Internacional</font></h4>" +
                    "        Si desea realizar un llamado a celular debe anteponer <b>siempre</b> el código del país <b>51 (Perú) </b> más el número de destino.<br>\n" +
                    "        Por ejemplo: <font color='#02A902'><b>51</b>993297151</font><br><br><br>" +
//                    "         <h4><font color='#385E9D'>Especiales</font></h4>" +
//                    "        Para llamar a su buzon de voz de <font color='#B0008E'>Italkyou</font> debe marcar: <font color='#02A902'><b>*98</b></font>\n" +
                    "        </p>" +
                    "";

    private static final java.lang.String HTML_BODY_EN =
            "              <p style=\"text-align: justify;\">" +
                    "        <center><h1>To call a number</h1></center>" +
                    "        <h4><font color='#385E9D'>Annex</font></h4>" +
                    "        To call an annex <font color='#B0008E'>Italkyou</font> you must dial <b>6 numbers</b> corresponding to the extension.<br>\n" +
                    "        For example: <font color='#02A902'><b>470200</b></font><br><br><br>" +
                    "        <h4><font color='#385E9D'>International</font></h4>" +
                    "        If you want to make a call to a phone number <b>always</b> preceded the country code <b>51 (Peru) </b> followed the destination number.<br>\n" +
                    "        For example: <font color='#02A902'><b>51</b>993297151</font><br><br><br>" +
//                    "         <h4><font color='#385E9D'>Especiales</font></h4>" +
//                    "        Para llamar a su buzon de voz de <font color='#B0008E'>Italkyou</font> debe marcar: <font color='#02A902'><b>*98</b></font>\n" +
                    "        </p>" +
                    "";

    private TextView tvInstructions;
    private ActionBar mActionBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_how_to_call);
        initViews();
        config();
    }

    private void config() {
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void initViews() {
        tvInstructions = (TextView) findViewById(R.id.tv_instructions);
        tvInstructions.setText(Html.fromHtml(AppUtil.obtenerIdiomaLocal().equals(Const.IDIOMA_ES) ? HTML_BODY_ES : HTML_BODY_EN));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_how_to_call, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        finish();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
