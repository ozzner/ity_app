package com.italkyou.gui.Testing;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.italkyou.beans.AppiTalkYou;
import com.italkyou.gui.BaseActivity;
import com.italkyou.gui.R;

public class TestingActivity extends BaseActivity implements View.OnClickListener {

    private static final String CONFIG_SUBJECT = "Testing report configuration";
    private TextView tvDeviceModel;
    private TextView tvCallAnswer;
    private TextView tvVersionAppAnswer;
    private TextView tvVersionOSAnswer;
    private Button btSendTesting;

    private AppiTalkYou app;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testing);
        app = ((AppiTalkYou) getApplication());

        //Init views
        initViews();

        //setUps
        setUp();
    }

    private void initViews() {
        tvDeviceModel = (TextView) findViewById(R.id.tv_device_model);
        tvCallAnswer = (TextView) findViewById(R.id.tv_label_call_answer);
        tvVersionAppAnswer = (TextView) findViewById(R.id.tv_label_version_app_answer);
        tvVersionOSAnswer = (TextView) findViewById(R.id.tv_label_version_os_answer);
        btSendTesting = (Button) findViewById(R.id.bt_send_testing);
    }

    private void setUp() {
        isCallEnabled();
        getDeviceModel();
        getOSversion();
        getAppVersion();
        setUpButton();
    }

    private void setUpButton() {
        btSendTesting.setOnClickListener(this);
    }

    private String getAppVersion() {
        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String version = pInfo.versionName;
        tvVersionAppAnswer.setText(version);
        return version;
    }

    private String getOSversion() {
        String osVersion = null;
        try {
            String release = Build.VERSION.RELEASE;
            int sdkVersion = Build.VERSION.SDK_INT;
            osVersion = "Android " + sdkVersion + " (" + release + ")";
            tvVersionOSAnswer.setText(osVersion);
        } catch (Exception ex) {

        }
        return osVersion;
    }

    private void isCallEnabled() {

        if (app.isSipEnabled()) {
            tvCallAnswer.setText("Call supported!");
            tvCallAnswer.setTextColor(getResources().getColor(R.color.my_green));
        } else {
            tvCallAnswer.setText("Call NO supported!");
            tvCallAnswer.setTextColor(getResources().getColor(R.color.my_red));
        }

    }

    private void getDeviceModel() {
        tvDeviceModel.setText(getDeviceName());
    }

    public String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }

    private String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }


    private void sendConfiguration() {
        String body = new String();

        body = "\n+------------ Configurations ------------";
        body += "\n Device run  : " + getDeviceName();
        body += "\n SIP support : " + app.isSipEnabled();
        body += "\n Android OS  : " + getOSversion();
        body += "\n Version APP : " + getAppVersion();
        body += "\n User name   : " + app.getUsuario().getNombres();
        body += "\n User Annex  : " + app.getUsuario().getAnexo();

        sendEmail(body);

    }

    private void sendEmail(String body) {
        Intent emailIntent = new Intent();

        emailIntent.setAction(Intent.ACTION_SEND);
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{getString(R.string.mail_error)});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, CONFIG_SUBJECT);
        emailIntent.putExtra(Intent.EXTRA_TEXT, body);

        if (emailIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(emailIntent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_testing, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bt_send_testing) {
            sendConfiguration();
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}
