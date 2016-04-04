package com.italkyou.gui.chat;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.italkyou.beans.AppiTalkYou;
import com.italkyou.gui.R;
import com.italkyou.utils.Const;
import com.italkyou.utils.ImageUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class VisualizarImagenActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = VisualizarImagenActivity.class.getSimpleName() + Const.ESPACIO_BLANCO;
    private byte[] data;
    private Bitmap currentImage;
    private AppiTalkYou app;
    private String chatMessageId;

    //Views
    private ImageView ivDataImage;
    private LinearLayout layDownload;
    private LinearLayout layClose;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.visualizar_imagen);

        app = ((AppiTalkYou) getApplication());

        initViews();
        configComponents();
    }


    private void initViews() {
        ivDataImage = (ImageView) findViewById(R.id.iv_data_image);
        layDownload = (LinearLayout) findViewById(R.id.lay_download_image);
        layClose = (LinearLayout) findViewById(R.id.lay_close_image);
    }

    private void configComponents() {

        //Set data to ImageView
        data = getIntent().getByteArrayExtra(Const.datos_imagen);
        currentImage = BitmapFactory.decodeByteArray(data, 0, data.length);
        ivDataImage.setImageBitmap(currentImage);

        //get objectId
        chatMessageId = getIntent().getExtras().getString(Const.TAG_CHATMESSAGE_ID);

        //Set listeners Layouts
        layDownload.setOnClickListener(this);
        layClose.setOnClickListener(this);

        //Setting layout buttons alpha/opacity
        setOpacityLayout(75);
    }

    private void setOpacityLayout(int opacity) {
        layDownload.getBackground().setAlpha(opacity);
        layClose.getBackground().setAlpha(opacity);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.visualizar_imagen, menu);
        return true;
    }


    /*Callbacks*/
    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.lay_download_image) {
            layDownload.getBackground().setAlpha(150);

            int result = ImageUtil.saveToSdCard(currentImage, generateFilename());

            if (result == 0)
                Toast.makeText(getApplicationContext(), getString(R.string.msj_image_download_success), Toast.LENGTH_SHORT).show();
            else if (result == 1)
                Toast.makeText(getApplicationContext(), getString(R.string.msj_image_download_exist), Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(getApplicationContext(), getString(R.string.msj_image_download_error), Toast.LENGTH_SHORT).show();

        } else {
            layClose.getBackground().setAlpha(150);
            finish();
        }
    }

    public String generateFilename() {

        Calendar cal = new GregorianCalendar();
        Date date = cal.getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        String fecha = df.format(date);

        String fileName;
        fileName = "IMG_";
        fileName += chatMessageId;
        fileName += "_";
        fileName += fecha;
        fileName += ".jpg";

        return fileName;
    }
}
