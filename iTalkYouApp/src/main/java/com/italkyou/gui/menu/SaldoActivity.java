package com.italkyou.gui.menu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.italkyou.beans.AppiTalkYou;
import com.italkyou.beans.BeanRespuestaOperacion;
import com.italkyou.beans.BeanSaldo;
import com.italkyou.beans.BeanUsuario;
import com.italkyou.beans.salidas.SalidaHistorialSaldo;
import com.italkyou.beans.salidas.SalidaMovimiento;
import com.italkyou.beans.salidas.SalidaResultado;
import com.italkyou.conexion.ExecuteRequest;
import com.italkyou.configuraciones.PayPal;
import com.italkyou.controladores.LogicaPantalla;
import com.italkyou.controladores.LogicaUsuario;
import com.italkyou.dao.UsuarioDAO;
import com.italkyou.gui.BaseActivity;
import com.italkyou.gui.R;
import com.italkyou.gui.personalizado.AdaptadorLista;
import com.italkyou.gui.personalizado.DialogoComprarSaldo;
import com.italkyou.gui.personalizado.DialogoComprarSaldo.onPayPalListener;
import com.italkyou.utils.AppUtil;
import com.italkyou.utils.Const;
import com.italkyou.utils.StringUtil;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import java.math.BigDecimal;
import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class SaldoActivity extends BaseActivity
        implements OnClickListener
        , ExecuteRequest.ResultadoOperacionListener
        , SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = SaldoActivity.class.getSimpleName();
    private SalidaHistorialSaldo saldosSalida;
    private Button btnComprar;
    private AppiTalkYou app;
    private UsuarioDAO daoUser;
    private Context context;
    private static double amount;
    private static PayPalConfiguration config;
    private TextView tvCurrentBalance;
    private Double currentBalance;
    private boolean flagOverrideLocalBalance = false;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private AdaptadorLista adaptador;
    private ListView lista;
    private List<Object> listMovements;
    private static final int[] ICONS_COLORS = {R.color.italkyou_primary_purple, R.color.iTalkYou_Azul_Suave};

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.historial_saldo);
        this.tipoMenu = Const.MENU_VACIA;
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        app = (AppiTalkYou) getApplication();
        saldosSalida = (SalidaHistorialSaldo) this.getIntent().getSerializableExtra(Const.DATOS_MOVIMIENTOS);
        iniciarComponentes();
        initPaypal();
        getBalanceIty();

    }

    private void initPaypal() {
        config = new PayPalConfiguration()
                .environment(PayPal.CONFIG_ENVIRONMENT)
                .clientId(PayPal.CONFIG_CLIENT_ID);

        if (AppUtil.obtenerIdiomaLocal() == Const.IDIOMA_ES)
            config.languageOrLocale(PayPal.ES);
        else
            config.languageOrLocale(PayPal.EN);


        //Init Paypal service
        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(intent);
    }

    private void iniciarComponentes() {


        setContext(getApplicationContext());
        lista = (ListView) findViewById(R.id.listMovimientosSaldo);
        tvCurrentBalance = (TextView) findViewById(R.id.tv_balance_current);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout_balance);
        mSwipeRefreshLayout.setColorSchemeResources(ICONS_COLORS);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        listMovements = saldosSalida.getListaMovimientos();

        adaptador = new AdaptadorLista(getApplicationContext()
                , R.layout.celda_historial_saldo
                , listMovements
                , SalidaMovimiento.class.getSimpleName()
                , app.getUsuario().getID_Idioma());

        lista.setAdapter(adaptador);

        daoUser = new UsuarioDAO(getContext());
        btnComprar = (Button) findViewById(R.id.btnComprarPayPal);
        btnComprar.setOnClickListener(this);


        /*Configurations*/
        configBalance();

    }

    private void configBalance() {
        if (app.getSaldo() == null) {
            currentBalance = LogicaUsuario.getLocalBalance(context, app.getUsuario().getID_Usuario());
            tvCurrentBalance.setText(currentBalance + Const.TAG_CURRENCY);
        } else {
            String saldo = app.getSaldo();

            if (saldo.equals("0.0")) {
                tvCurrentBalance.setText(Const.TAG_DEFAULT_BALANCE + Const.TAG_CURRENCY);
            } else {
                String sBalance = StringUtil.format(Double.parseDouble(app.getSaldo()));
                tvCurrentBalance.setText(sBalance + Const.TAG_CURRENCY);
            }
            currentBalance = Double.parseDouble(app.getSaldo());

        }
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        if (v == btnComprar) {
            FragmentManager fm = getSupportFragmentManager();
            DialogoComprarSaldo dialogo = new DialogoComprarSaldo();

            onPayPalListener mlistener = new onPayPalListener() {

                @Override
                public void setPagarListener(String monto) {
                    amount = Double.parseDouble(monto);
                    comprarPayPal(amount);
                }
            };

            dialogo.mlistener = mlistener;
            dialogo.show(fm, "");
        }
    }

    public void getBalanceIty() {
        String annex;
        try {
            annex = app.getUsuario().getAnexo();
        } catch (NullPointerException ExNull) {
            app = ((AppiTalkYou) getApplication());
            app.setUsuario(LogicaUsuario.obtenerUsuario(getApplicationContext()));
            annex = app.getUsuario().getAnexo();
        }

        final String finalAnnex = annex;
        ExecuteRequest ejecutar = new ExecuteRequest(new ExecuteRequest.ResultadoOperacionListener() {

            @Override
            public void onOperationDone(BeanRespuestaOperacion respuesta) {

                if (respuesta.getError().equals(Const.cad_vacia)) {
                    final BeanSaldo saldo = (BeanSaldo) respuesta.getObjeto();

                    if (saldo.getResultado().equals(Const.RESULTADO_OK)) {
                        app.setSaldo(saldo.getBalance());
                        LogicaUsuario.actualizarSaldo(getApplicationContext(), finalAnnex, saldo.getBalance());

                        //Update balance
                        printBalance();
                    }
                }

            }
        });
        ejecutar.obtenerSaldo(annex);
    }

    private PayPalPayment payToGetBalance(Double monto) {

        BigDecimal amount = BigDecimal.valueOf(monto);

        // If you have shipping cost, add it here
        BigDecimal shipping = new BigDecimal("0.0");

        // If you have tax, add it here
        BigDecimal tax = new BigDecimal("0.0");
//		String description = getString(R.string.title_activity_saldo);
        String description = "Saldo";
        PayPalPayment payment = new PayPalPayment(amount, PayPal.DEFAULT_CURRENCY, description, PayPal.PAYMENT_INTENT);
        payment.custom("Buy by ITY-App");
        return payment;
    }

    private void comprarPayPal(Double monto) {
        PayPalPayment buyBalance = payToGetBalance(monto);

        Intent intent = new Intent(SaldoActivity.this, PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, buyBalance);
        startActivityForResult(intent, PayPal.REQUEST_CODE_PAYMENT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);


            if (confirm != null) {
                Log.e(TAG, "Object: " + confirm.toJSONObject().toString());

                try {

                    ExecuteRequest op = new ExecuteRequest(this);
                    BeanUsuario user = app.getUsuario();

                    String paymentId = confirm.getProofOfPayment().getPaymentId();
                    String intent = confirm.getProofOfPayment().getIntent();
                    String status = confirm.getProofOfPayment().getState();
                    String createdAt = confirm.getProofOfPayment().getCreateTime();

                    //Remote, only amount to add
                    op.reloadBalance(user, paymentId, intent, amount);

                    //Local, amount plus current balance
                    amount = currentBalance + amount;
                    boolean flag = updateBalanceSQLite(amount);

                    if (flag) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //Update label
                                app.setSaldo(String.valueOf(amount));
                                app.getUsuario().setSaldo(String.valueOf(amount));
                                String label = StringUtil.format(amount);
                                tvCurrentBalance.setText(label + Const.TAG_CURRENCY);
                            }
                        });
                    }

                } catch (Exception ex) {
                    Log.e(TAG, ex.getMessage());
                } finally {

                }

            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Crouton.showText(SaldoActivity.this, getString(R.string.msj_cancelar_compra), Style.ALERT);
        } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
            Crouton.showText(SaldoActivity.this, getString(R.string.msj_compra_invalida), Style.ALERT);
        }
    }


    protected boolean updateBalanceSQLite(double inputBalance) {
        boolean result = false;

        try {

            String currentBalance = String.valueOf(inputBalance);
            int row = daoUser.actualizarSaldo(app.getUsuario().getAnexo(), currentBalance);
            if (row > 0) {
                result = true;
                app.setSaldo(currentBalance);
            }

        } catch (Exception ex) {
            Log.e(TAG, "Saldos: " + ex.getMessage());
        }


        return result;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        LogicaPantalla.personalizarIntentVistaPrincipal(SaldoActivity.this, Const.PANTALLA_PRINCIPAL, SaldoActivity.class.getSimpleName());
    }

    /* setter and getter*/

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public void onOperationDone(BeanRespuestaOperacion respuesta) {

        if (respuesta != null) {
            SalidaResultado result = (SalidaResultado) respuesta.getObjeto();
            String message = result.getMensaje();

            if (message != Const.RESULTADO_OK) {
                //TODO store paypayresponse, if was an some error.
                Log.e(TAG, "Store payment, because was an error");
            }
        }


    }

    @Override
    public void onRefresh() {
        getMovements();
    }

    private void getMovements() {
        if (AppUtil.existeConexionInternet(SaldoActivity.this)) {
            mSwipeRefreshLayout.setRefreshing(true);
            final AppiTalkYou app = (AppiTalkYou) getApplication();

            ExecuteRequest ejecutar = new ExecuteRequest(new ExecuteRequest.ResultadoOperacionListener() {
                @SuppressWarnings({"unchecked"})
                @Override
                public void onOperationDone(BeanRespuestaOperacion respuesta) {

                    if (respuesta.getError().equals(Const.cad_vacia)) {
                        List<Object> listaMovimiento = (List<Object>) respuesta.getObjeto();
                        listMovements.clear();
                        listMovements.addAll(listaMovimiento);
                        adaptador.notifyDataSetChanged();
                    }

                    mSwipeRefreshLayout.setRefreshing(false);
                }
            });

            ejecutar.obtenerHistorialSaldo(app.getUsuario().getID_Idioma(), app.getUsuario().getAnexo(), app.getUsuario().getO_ck());

        } else {
            mSwipeRefreshLayout.setRefreshing(false);
            Crouton.showText(SaldoActivity.this, getString(R.string.msj_error_conexion_internet), Style.ALERT);
        }
    }


    public void printBalance() {

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String sBalance = StringUtil.format(Double.parseDouble(app.getSaldo()));
                tvEstadoAnexo.setText(getString(R.string.bar_balance) + sBalance + Const.TAG_CURRENCY);
                Log.e(Const.DEBUG_BALANCE, TAG + "Saldo--> " + sBalance);
            }
        });
    }

}
