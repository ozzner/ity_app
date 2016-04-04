package com.italkyou.gui.Testing;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.italkyou.gui.R;
import com.italkyou.utils.Const;

public class ReportActivity extends ActionBarActivity {


    private static final String LABEL_TITLE = "Reporte ITY - Error";
    private static final String ERROR_SUBJECT = "Reporte error ITYAPP";
    private Button butSendError;
    private TextView tviMessage;
    private String error = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        initUI();
        loadData();
    }

    private void initUI(){
        butSendError = (Button) findViewById(R.id.butSendReport);
        tviMessage = (TextView) findViewById(R.id.tviMessage);
    }

    private void loadData(){
        error = getIntent().getExtras().getString(Const.TAG_ERROR);
        getSupportActionBar().setTitle(LABEL_TITLE);
        tviMessage.setText(error);

        butSendError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                //rsantillan@intico.com.pe
                i.putExtra(Intent.EXTRA_EMAIL, new String[]{getString(R.string.mail_error)});
                i.putExtra(Intent.EXTRA_SUBJECT, ERROR_SUBJECT);
                i.putExtra(Intent.EXTRA_TEXT, error);
                try {
                    startActivity(Intent.createChooser(i, getString(R.string.label_sending_error)));
                } catch (android.content.ActivityNotFoundException ex) {

                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
