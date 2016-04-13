package com.italkyou.gui.contactos;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.italkyou.beans.AppiTalkYou;
import com.italkyou.beans.BeanContact;
import com.italkyou.beans.BeanContactUser;
import com.italkyou.beans.BeanFramesTelephones;
import com.italkyou.beans.BeanTelefono;
import com.italkyou.beans.popup.MenuActionsContact;
import com.italkyou.beans.salidas.OutputContact;
import com.italkyou.conexion.ExecuteRequest;
import com.italkyou.controladores.LogicContact;
import com.italkyou.controladores.LogicaPantalla;
import com.italkyou.controladores.LogicaUsuario;
import com.italkyou.gui.PrincipalFragment;
import com.italkyou.gui.R;
import com.italkyou.gui.llamada.DialActivity;
import com.italkyou.gui.personalizado.AdaptadorLista;
import com.italkyou.gui.personalizado.CustomAlertDialog;
import com.italkyou.utils.Const;

import java.util.ArrayList;
import java.util.List;

public class ListContactDeviceFragment extends Fragment implements
        OnItemClickListener,
        OnQueryTextListener,
        SwipeRefreshLayout.OnRefreshListener,
        CustomAlertDialog.OnDialogListener,
        AdaptadorLista.OnMenuItemListener,
        CustomAlertDialog.OnHightImportanceListener, CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    private static final String TAG = ListContactDeviceFragment.class.getSimpleName() + Const.ESPACIO_BLANCO;
    private static final int[] ICONS = {R.color.pantone_2405C, R.color.pantone_7684C};
    private static final int[] ICONS_CALL = {R.drawable.ic_action_call_free, R.drawable.ic_action_call, R.drawable.ic_email};
    private static final int CONTACT_EDIT = 1;
    private static String[] lables = null;
    private static final int IS_ANNEX = 0;
    private static final int IS_PHONE = 1;
    private static final int IS_SEND = 2;
    private SearchView searchView;
    private List<Object> listContacts;
    private AdaptadorLista adaptador;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private String pantalla;
    protected List<Object> listaMenu;
    protected boolean isAnnex = false;
    protected BeanContact contacto;
    protected CustomAlertDialog customDialog;
    protected String[] arrayPhones;
    protected String[] arrayAnnex;
    protected MenuActionsContact contactMenu;
    private AppiTalkYou app;
    protected int typeAction = 19;
    protected ImageView ivDial;
    protected ListView mListView;
    private TextView tvContactTitle;
    private SwitchCompat swContactsIty;
    private boolean isChecked = false;
    private ProgressBar pbLoader;
    private static boolean isSynchronizing = false;
    private List<Object> listSearchContact = new ArrayList<>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lables = getResources().getStringArray(R.array.array_menu_actions_contact);
        app = (AppiTalkYou) getActivity().getApplicationContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.listado_contacto, container, false);
        pantalla = getArguments().getString(Const.DATOS_TIPO);
//        VistaPrincipalActivity.setPantalla(Const.PANTALLA_LISTA_CONTACTO);
        AdaptadorLista.setOnMenuItemListener(this);

        ivDial = (ImageView) rootView.findViewById(R.id.iv_contact_dial);
        mListView = (ListView) rootView.findViewById(R.id.listcontactos);
        tvContactTitle = (TextView) rootView.findViewById(R.id.tv_contact_title);
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        pbLoader = (ProgressBar) rootView.findViewById(R.id.pbLoader);
        swContactsIty = (SwitchCompat) rootView.findViewById(R.id.sw_contacts_ity);

        startLoadingData();
        return rootView;
    }


    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        ivDial.setOnClickListener(this);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(ICONS);
        mListView.setOnItemClickListener(this);
        swContactsIty.setOnCheckedChangeListener(this);
        swContactsIty.setVisibility(View.VISIBLE);
        swContactsIty.setEnabled(!isSynchronizing);
        setCheckedSW(false);
    }


    @Override
    public void onStart() {
        super.onStart();
    }


    private void openDialActivity() {
        Intent dial = new Intent(getActivity(), DialActivity.class);
        dial.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(dial);
    }


    private void fetchContacts() {
        new AsyncFetchContacts().execute();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        this.isChecked = isChecked;
        //    Toast.makeText(getActivity(), "isChecked? = " + isChecked, Toast.LENGTH_SHORT).show();
        showItyContacts(isChecked);
    }


    private void showItyContacts(boolean isChecked) {

        if (isChecked)
            reloadItyList();
        else
            reloadList();

    }

    private void reloadItyList() {
        List<Object> objects = LogicContact.obtenerListadoContactosAnexo(getActivity());
        listContacts.clear();
        listContacts.addAll(objects);
        notifyChanges();
    }

    public void reloadList() {

        List<Object> objects = LogicContact.obtenerListadoContactos(getActivity());
        listContacts.clear();
        listContacts.addAll(objects);
        if (listContacts.size() > 0) {
            pbLoader.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);
        }
        notifyChanges();


    }

    private void setCheckedSW(boolean on) {
        swContactsIty.setChecked(on);
    }

    private void startSyncContacts() {

        if (!LogicContact.checkIfContactsExist(getActivity())) {
            //Contacts doesn't exist.
            new AsyncSavingData().execute();
        } else {
            startLoadingData();
            Log.e(TAG, "Contacts ready exist!");
        }
    }

    @Override
    public void onClick(View v) {
        openDialActivity();
    }


    class AsyncSavingData extends AsyncTask<BeanContactUser, Boolean, BeanContactUser> {
        BeanContactUser listDeviceContacts;
        Context _context;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            isSynchronizing = true;
            mSwipeRefreshLayout.setRefreshing(true);
            swContactsIty.setEnabled(false);
        }

        @Override
        protected BeanContactUser doInBackground(BeanContactUser... params) {
            listDeviceContacts = new LogicContact().listarContactoDispositivo(getActivity());

            int rows = LogicContact.guardarListadoContactos(_context, listDeviceContacts.getListaContactos(), false);
            if (rows > 0)
                LogicContact.guardarListadoTelefono(_context, listDeviceContacts.getListaTelefonos());

            return listDeviceContacts;
        }

        @Override
        protected void onPostExecute(BeanContactUser bean) {
            super.onPostExecute(bean);

            mSwipeRefreshLayout.setRefreshing(false);
            if (bean.getListaContactos().size() > 0) {
                Toast.makeText(getActivity(), R.string.synchronizing, Toast.LENGTH_SHORT).show();
                prepararSincronizacionContactos(bean);
            } else
                Toast.makeText(getActivity(), R.string.contacts_no_found, Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * @param bContactos lista de
     */
    public void prepararSincronizacionContactos(final BeanContactUser bContactos) {
        SyncContactsToServer(bContactos);
    }

    private void SyncContactsToServer(BeanContactUser bContactos) {
        new SyncContactsToServerTask().execute(bContactos);
    }

    public BeanContactUser syncContacts(boolean flagTable) {
        BeanContactUser bcu = new LogicContact().listarContactoDispositivo(getActivity());
        int i = LogicContact.guardarListadoContactos(getActivity(), bcu.getListaContactos(), flagTable);
        if (i > 0)
            LogicContact.guardarListadoTelefono(getActivity(), bcu.getListaTelefonos());

        populateListView();
        return bcu;
    }

    private void populateListView() {
        listContacts = LogicContact.obtenerListadoContactos(getActivity());
        adaptador = new AdaptadorLista(getActivity(), R.layout.row_contact_device, listContacts, BeanContact.class.getSimpleName());
        setAdapter();
    }


    @Override
    public void onResume() {
        super.onResume();
//
//        if (!isSynchronizing) {
//            startSyncContacts();
//            Log.e(Const.DEBUG_CONTACTS, "ON RESUME");
//        }
    }

    protected void loadList() {
        new AsyncFetchContacts().execute();
    }


    void notifyChanges() {
        adaptador.notifyDataSetChanged();
        showNumberContacts(isChecked);
    }


    private void setAdapter() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mListView.setAdapter(adaptador);
            }
        });
    }


    private void showNumberContacts(boolean isChecked) {
        String title;

        if (isChecked)
            title = getActivity().getResources().getString(R.string.title_contact_ity);
        else
            title = getActivity().getResources().getString(R.string.title_contact);

        tvContactTitle.setText(title + ":" + Const.ESPACIO_BLANCO + listContacts.size());
    }


    private void startLoadingData() {
        if (!LogicContact.checkIfContactsExist(getActivity())) {
            loadList();
        } else {
            showLoader(false);
            populateListView();
            notifyChanges();
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        getActivity().getMenuInflater().inflate(R.menu.mncontacto, menu);
        MenuItem buscarItem = menu.findItem(R.id.itBuscar);

        searchView = (SearchView) MenuItemCompat.getActionView(buscarItem);
        searchView.setQueryHint(getString(R.string.hnt_buscar_contactos));
        searchView.setOnQueryTextListener(this);

    }

    String tempChain = "";
    boolean isSearched = false;

    @Override
    public boolean onQueryTextChange(String chain) {

        if (!chain.trim().equals(Const.cad_vacia)) {
            List<Object> tempList = new ArrayList();

            if (chain.length() > tempChain.length() && isSearched == false) {
                listSearchContact.addAll(listContacts);
                isSearched = true;
            } else if (chain.length() <= tempChain.length()) {
                listContacts.clear();
                listContacts.addAll(listSearchContact);
            }

            for (Object item : listContacts) {
                BeanContact contact = (BeanContact) item;
                String name = (contact.getNombre()).toUpperCase();

                if (name.toUpperCase().contains(chain.toUpperCase()))
                    tempList.add(contact);
            }

            tempChain = chain;
            listContacts.clear();
            listContacts.addAll(tempList);
            notifyChanges();

        } else {
            tempChain = "";
            if (isChecked) {
                reloadItyList();
            } else
                reloadList();

        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Log.i("intico", "cargar pantalla principal");
                break;
            case R.id.itBuscarContacto:
                LogicaPantalla.personalizarIntentActivity(getActivity(), ContactoItalkYouActivity.class);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return super.onOptionsItemSelected(item);

    }

    @Override
    public boolean onQueryTextSubmit(String arg0) {
        return false;
    }


    public static ListContactDeviceFragment nuevaInstancia() {
        return new ListContactDeviceFragment();
    }

    protected void showCustomDialog(int type, String... array) {

        switch (type) {
            case Const.INDEX_DIALOG_CONTACT:
                CustomAlertDialog.TYPE = Const.INDEX_DIALOG_CONTACT;
                customDialog = CustomAlertDialog.newIntance(Const.INDEX_DIALOG_CONTACT, listaMenu, isAnnex);
                customDialog.setDialogListener(this);
                customDialog.show(getFragmentManager(), Const.TAG_MENU_ACTIONS);
                break;

            case Const.INDEX_DIALOG_RADIOBUTTON:
                CustomAlertDialog.TYPE = Const.INDEX_DIALOG_RADIOBUTTON;
                customDialog = CustomAlertDialog.newIntance(-1, isAnnex, array);
                customDialog.setDialogListener(this);
                customDialog.show(getFragmentManager(), Const.TAG_MENU_RADIO_BUTTON);
                break;
        }

    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View view, int posicion, long id) {
        listaMenu = new ArrayList<>();
        isAnnex = false;

        Log.e(TAG, "Position Lista contactos: " + posicion);

        contacto = (BeanContact) listContacts.get(posicion);
        if (contacto.getUsuarioITY().equals(Const.USER_ITY))
            isAnnex = true;

        for (int idx = 0; idx < lables.length; idx++) {
            listaMenu.add(new MenuActionsContact(ICONS_CALL[idx], lables[idx], contacto.getIdContacto(), contacto.getLookUpKey(), contacto.getTelefono()));
        }

        showCustomDialog(Const.INDEX_DIALOG_CONTACT);
    }

    @Override
    public void onRefresh() {
        if (!isSynchronizing)
            fetchContacts();
    }


    @Override
    public void onPositive(DialogInterface dialog, int index) {
    }

    @Override
    public void onNegative(DialogInterface dialog) {
        dialog.cancel();
    }

    @Override
    public void onNeutral(DialogInterface dialog, int index) {

    }

    @Override
    public void onItemClickAlert(int index) {
        Double dBalance;

        contactMenu = (MenuActionsContact) listaMenu.get(index);
        String balance = app.getSaldo();

        if (balance == null) {
            dBalance = LogicaUsuario.getLocalBalance(getActivity(), app.getUsuario().getID_Usuario());
        } else {
            dBalance = Double.parseDouble(balance);
        }

        //Init values
        arrayPhones = new String[contactMenu.count()];
        //Set values
        arrayPhones = contactMenu.getNumbers();

        //Casos en los que puede ser SMS
        if ((isAnnex && index == 2) || (isAnnex == false && index == 1)) {
            typeAction = IS_SEND;
            isAnnex = false;

            if (dBalance > 0) {

                //Envía automaticamente en número para los mensajes.
                if (contactMenu.count() == 1) {
                    app.setAnyNumber(arrayPhones[0]);
                    sendToSMSFragment();
                } else {

                    arrayPhones = contactMenu.getNumbers();
                    //Show Alert with RadioButtons Options.
                    showCustomDialog(Const.INDEX_DIALOG_RADIOBUTTON, arrayPhones);
                }

            } else {
                String message = getResources().getString(R.string.message_alert_balance_sms);
                CustomAlertDialog.showSingleAlert(getActivity(), message);
            }


            //Casos en los que puede ser phone.
        } else if ((isAnnex && index == 1) || (isAnnex == false && index == 0)) {

            typeAction = IS_PHONE;
            isAnnex = false;

            if (dBalance > 0) {
                app.setAnyNumber(arrayPhones[0]);

                //Llama directamente si solo hay 1 anexo asociado al contacto.
                if (contactMenu.count() == 1) {
//                    sipManager.makeAudioCall(Const.CALL_PHONE_CODE + ((BeanTelefono) beans.get(0)).getNumero());
                    LogicaPantalla.makeAudioCallIntent(
                            getActivity(),
                            contactMenu.getNumbers()[0],
                            contacto.getNombre()
                    );
                } else {
                    //Show Alert with RadioButtons Options.
                    showCustomDialog(Const.INDEX_DIALOG_RADIOBUTTON, arrayPhones);
                }

            } else {
                String message = getResources().getString(R.string.message_alert_balance_none);
                CustomAlertDialog.showSingleAlert(getActivity(), message);
            }


        } else {

            typeAction = IS_ANNEX;
            isAnnex = true;
            arrayAnnex = new String[contacto.getPhones().size()];

            int c = 0;
            for (int i = 0; i < contacto.getPhones().size(); i++) {
                String annex = ((BeanTelefono) contacto.getPhones().get(i)).getAnexo();
                if (annex != null)
                    if (annex.length() > 0) {
                        arrayAnnex[c] = annex;
                        c++;
                    }


            }

            //Llama directamente si solo hay 1 anexo asociado al contacto.
            if (c == 1) {
                LogicaPantalla.makeAudioCallIntent(
                        getActivity(),
                        (arrayAnnex[0]),
                        contacto.getNombre());
            } else {
                //Show Alert with RadioButtons Options.
                showCustomDialog(Const.INDEX_DIALOG_RADIOBUTTON, arrayAnnex);
            }
        }


    }//End onItemClickAlert. AlertDialog

    @Override
    public void onRadioButtonClickAlert(int index) {

        if (index == -19) {
            Toast.makeText(getActivity(), getString(R.string.title_menu_choose_actions), Toast.LENGTH_LONG).show();
            return;
        }

//        int id = contactMenu.getContactID();
        app.setAnyNumber(arrayPhones[index]);

        if (isAnnex) {

            switch (typeAction) {
                case IS_ANNEX:
                    LogicaPantalla.makeAudioCallIntent(
                            getActivity(),
                            arrayAnnex[index],
                            contacto.getNombre());
                    break;
                case IS_PHONE:
                    LogicaPantalla.makeAudioCallIntent(
                            getActivity(),
                            arrayPhones[index],
                            contacto.getNombre());
                    break;
                case IS_SEND:
                    sendToSMSFragment();

                default:
                    break;
            }


        } else {

            if (typeAction == IS_PHONE) {
                LogicaPantalla.makeAudioCallIntent(
                        getActivity(),
                        arrayPhones[index],
                        contacto.getNombre());

                Toast.makeText(getActivity(), arrayPhones[index], Toast.LENGTH_SHORT).show();

            } else
                sendToSMSFragment();
        }
    }

    @Override
    public void onWarningClick(DialogInterface dialog, int index) {

    }

    //Menú popup
    @Override
    public void onMenuItemClick(MenuItem item, int index) {
        int id = item.getItemId();
        contacto = (BeanContact) listContacts.get(index);

        switch (id) {
            case R.id.ic_acc_profile:
                app.setCurrentContact(contacto);
                LogicaPantalla.personalizarIntentDatosContacto(getActivity(), contacto);
                break;
            case R.id.ic_acc_edit:
                actionEdit();
                break;
//            case R.id.ic_acc_delete:
//                actionDelete();
//                break;
            default:
                break;
        }
    }


    protected void actionEdit() {

        Uri.Builder newUriBuilder = ContactsContract.Contacts.CONTENT_LOOKUP_URI.buildUpon();
        newUriBuilder.appendPath(contacto.getLookUpKey());

        Intent intent = new Intent(Intent.ACTION_EDIT);
        intent.setData(newUriBuilder.build());
        intent.putExtra("finishActivityOnSaveCompleted", true);
        intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(intent, CONTACT_EDIT);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {

            case CONTACT_EDIT:
                if (resultCode == Activity.RESULT_OK) {
                    LogicContact dao = new LogicContact();
                    BeanContact beanContact = dao.editContactDevice(getActivity(), data, contacto);

                    if (beanContact != null) {
                        reloadList();
                        Toast.makeText(getActivity(), getActivity().getString(R.string.synchronizing), Toast.LENGTH_SHORT).show();
                        syncContactEdited(beanContact);
                    } else {
                        Toast.makeText(getActivity(), getActivity().getString(R.string.error_unknow), Toast.LENGTH_SHORT).show();
                    }

                }

                break;

            default:
                break;
        }


    }

    private void syncContactEdited(final BeanContact beanContact) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                List<OutputContact> ityAnnex = ExecuteRequest.obtenerContactosAnexosItalkYou(beanContact.getTelefono());

                if (ityAnnex.size() > 0) {
                    Log.e(TAG, "Yes, synchronized!");
                    LogicContact.updateContactSynchronized(getActivity(), contacto, ityAnnex);
                    LogicContact.actualizarFlagITYContacto(beanContact.getLookUpKey(), getActivity());
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            reloadList();
                        }
                    });
                } else {
                    Log.e(TAG, "No synchronized");
                }
            }
        }).start();


    }

    protected void sendToSMSFragment() {

        Fragment fg = PrincipalFragment.nuevaInstancia(Const.indice_3);
        FragmentManager mg = getFragmentManager();
        FragmentTransaction transaction = mg.beginTransaction();
        transaction.replace(R.id.contenedor_fragmentos, fg);
        transaction.commit();

    }


    void showLoader(boolean flag) {
        //Set visibility
        if (flag) {
            pbLoader.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.GONE);
        } else {
            mListView.setVisibility(View.VISIBLE);
            pbLoader.setVisibility(View.INVISIBLE);
        }
    }


//    AsyncTasking


    class SyncContactsToServerTask extends AsyncTask<BeanContactUser, Boolean, Boolean> {

        @Override
        protected Boolean doInBackground(BeanContactUser... params) {

            BeanContactUser bcu = params[0];
            if (LogicContact.verificarExistenciaContactosPorSincronizar(getActivity())) {
                LogicContact logic = new LogicContact();
                List<BeanFramesTelephones> framesList = logic.getElementsToSyncInITY(getActivity());

                //Send frames to server.
                for (int index = 0; index < framesList.size(); index++) {
                    String frame = framesList.get(index).getFrameTelephone();
                    Log.e(Const.DEBUG_CONTACTS, TAG + "Frame " + index + " : " + frame);
                    List<OutputContact> ityAnnex = ExecuteRequest.obtenerContactosAnexosItalkYou(frame);

                    if (ityAnnex.size() > 0) {
                        logic.actualizarContactosITalkYou(bcu, getActivity(), ityAnnex, false);
                        List<Object> objects = LogicContact.obtenerListadoContactos(getActivity());
                        listContacts.clear();
                        listContacts.addAll(objects);
                        publishProgress(true);
                    }

                    Log.e(Const.DEBUG_CONTACTS, TAG + "Tenemos " + ityAnnex.size() + " Anexos obtenidos----!!!!!!!!!");
                }

            } else
                Log.e(Const.DEBUG_CONTACTS, TAG + "No hay contactos por sincronizar");

            Log.e(Const.DEBUG_CONTACTS, TAG + "Terminando actualizacion de contactos");

            return null;
        }

        @Override
        protected void onProgressUpdate(Boolean... values) {
            super.onProgressUpdate(values);
            notifyChanges();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            swContactsIty.setEnabled(true);
            showLoader(false);
            if (mSwipeRefreshLayout.isRefreshing())
                mSwipeRefreshLayout.setRefreshing(false);

            isSynchronizing = false;
        }
    }


    /*AsyncTask*/
    class AsyncFetchContacts extends AsyncTask<Boolean, Integer, BeanContactUser> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            isSynchronizing = true;
            mSwipeRefreshLayout.setRefreshing(true);
            swContactsIty.setEnabled(false);
        }


        @Override
        protected BeanContactUser doInBackground(Boolean... params) {
            return syncContacts(false);
        }

        @Override
        protected void onPostExecute(BeanContactUser result) {
            super.onPostExecute(result);
            mSwipeRefreshLayout.setRefreshing(false);

            if (result.getListaContactos().size() > 0) {
                notifyChanges();
                prepararSincronizacionContactos(result);
                Toast.makeText(getActivity(), R.string.synchronizing, Toast.LENGTH_SHORT).show();
            }


        }

    } //end AsyncTask

}



