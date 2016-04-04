package com.italkyou.gui.contactos;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
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
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.italkyou.beans.BeanContact;
import com.italkyou.controladores.LogicContact;
import com.italkyou.controladores.LogicaPantalla;
import com.italkyou.gui.BaseActivity;
import com.italkyou.gui.PrincipalFragment;
import com.italkyou.gui.R;
import com.italkyou.gui.VistaPrincipalActivity;
import com.italkyou.gui.personalizado.AdaptadorLista;
import com.italkyou.gui.personalizado.AdaptadorListaCheckBox;
import com.italkyou.utils.Const;

import java.util.ArrayList;
import java.util.List;

public class ListaContactoFragment extends Fragment implements OnItemClickListener, OnQueryTextListener, SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = ListaContactoFragment.class.getSimpleName();
    private static final int[] ICONS = {R.color.italkyou_primary_purple, R.color.iTalkYou_Azul_Suave};
    private SearchView searchView;
    private List<Object> listaContactos;
    private List<Object> listaContactosBusq;
    private AdaptadorListaCheckBox adaptadorchk;
    private AdaptadorLista adaptador;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ProgressBar pbLoader;
    private String pantalla;
    private Activity mActivity;
    private ListView mListView;
    private SwitchCompat swContacts;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
        Log.e(TAG, "[LISTACONTACTOFRAGMENT] onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.listado_contacto, container, false);

        Log.e(TAG, "[LISTACONTACTOFRAGMENT] createview");
        pantalla = (String) getArguments().getString(Const.DATOS_TIPO);
        VistaPrincipalActivity.setPantalla(Const.PANTALLA_LISTA_CONTACTO);
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        pbLoader = (ProgressBar) rootView.findViewById(R.id.pbLoader);
        mListView = (ListView) rootView.findViewById(R.id.listcontactos);
        swContacts = (SwitchCompat) rootView.findViewById(R.id.sw_contacts_ity);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(ICONS);
        swContacts.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Toast.makeText(getActivity(),"isChecked? = " + isChecked, Toast.LENGTH_SHORT).show();
                showItyContacts(isChecked);
            }
        });

        return rootView;
    }

    private void fetchContacts() {
        reloadList();
    }


    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.e(TAG, "[LISTACONTACTOFRAGMENT] onActivityCreated");
        setHasOptionsMenu(true);
        ActionBar mActionBar = ((BaseActivity) getActivity()).getSupportActionBar();
        showLoader(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        inicializarComponentes();

    }


    protected void loadList() {

        listaContactos = LogicContact.obtenerListadoContactos(getActivity());
        listaContactosBusq = new ArrayList();
        listaContactosBusq.addAll(listaContactos);
        Log.e(TAG, "[LISTACONTACTOFRAGMENT] listaContactos size: " + listaContactos.size());
        showLoader(false);
    }

    protected void reloadList() {

        listaContactos = LogicContact.obtenerListadoContactos(getActivity());
        listaContactosBusq.clear();
        listaContactosBusq.addAll(listaContactos);
        adaptadorchk.notifyDataSetChanged();
        showRefresh(false);

    }


    private void inicializarComponentes() {

        loadList();
        if (pantalla.equals(Const.PANTALLA_FAVORITOS)) {
            adaptadorchk = new AdaptadorListaCheckBox(getActivity().getApplicationContext(), R.layout.celda_contacto, listaContactosBusq, BeanContact.class.getSimpleName(), "");
            adaptadorchk.setActivar(Const.mostrar_check);
            mListView.setAdapter(adaptadorchk);

        } else if (pantalla.equals(Const.PANTALLA_CONTACTO_LLAMADA)) {

            adaptador = new AdaptadorLista(getActivity().getApplicationContext(), R.layout.row_contact_device, listaContactosBusq, BeanContact.class.getSimpleName());
            mListView.setAdapter(adaptador);
            mListView.setItemsCanFocus(false);
            mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
            mListView.setOnItemClickListener(this);

        } else if (pantalla.equals(Const.PANTALLA_CONTACTO_SMS)) {

            adaptador = new AdaptadorLista(getActivity().getApplicationContext(), R.layout.row_contact_device, listaContactosBusq, BeanContact.class.getSimpleName());
            mListView.setAdapter(adaptador);
            mListView.setItemsCanFocus(false);
            mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
            mListView.setOnItemClickListener(this);

        } else {
            //TODO HACER LA INTEFAZ PARA PANTALLA DE TELEFONO PARA AGREGAR CONTACTOS LOCALES Y DE ITY
        }

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();

        if (pantalla.equals(Const.PANTALLA_FAVORITOS))
            getActivity().getMenuInflater().inflate(R.menu.mnbuscar_agregar, menu);
        else
            getActivity().getMenuInflater().inflate(R.menu.mncontacto, menu);

        MenuItem buscarItem = menu.findItem(R.id.itBuscar);

        searchView = (SearchView) MenuItemCompat.getActionView(buscarItem);
        searchView.setQueryHint(getString(R.string.hnt_buscar_contactos));
        searchView.setOnQueryTextListener(this);

    }

    @Override
    public boolean onQueryTextChange(String texto) {

        if (!texto.trim().equals(Const.cad_vacia)) {

            List<Object> lista = new ArrayList<Object>();

            for (Object item : listaContactos) {

                BeanContact contacto = (BeanContact) item;
                String nombre = (contacto.getNombre()).toUpperCase();
                String text = texto.toUpperCase();

                if (nombre.contains(text)) {
                    lista.add(contacto);
                } else if ((contacto.getTelefono()).contains(texto)) {
                    lista.add(contacto);
                }
            }

            listaContactosBusq.clear();
            if (pantalla.equals(Const.PANTALLA_FAVORITOS)) {

                adaptadorchk.notifyDataSetChanged();
                listaContactosBusq.addAll(lista);
                adaptadorchk.notifyDataSetChanged();

            } else {

                adaptador.notifyDataSetChanged();
                listaContactosBusq.addAll(lista);
                adaptador.notifyDataSetChanged();

            }

        } else {
            Log.i("Intico", "[ContactoActivity]= muestra lista de contacto  ");

            if (pantalla.equals(Const.PANTALLA_FAVORITOS)) {
                listaContactosBusq.clear();
                adaptadorchk.notifyDataSetChanged();
                listaContactosBusq.addAll(listaContactos);
                adaptadorchk.notifyDataSetChanged();

            } else {

                listaContactosBusq.clear();
                adaptador.notifyDataSetChanged();
                listaContactosBusq.addAll(listaContactos);
                adaptador.notifyDataSetChanged();
            }

        }
        return false;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                Log.i("intico", "cargar pantalla principal");
                cargarFragmento();
                break;
//            case R.id.itAgregar:
//                agregarFavoritos();
//                break;
            case R.id.itBuscarContacto:
                LogicaPantalla.personalizarIntentActivity(getActivity(), ContactoItalkYouActivity.class);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

//    private void agregarFavoritos() {
//
//        boolean[] valores = adaptadorchk.getValores();
//        List<Integer> lista = new ArrayList<Integer>();
//
//        for (int i = 0; i < valores.length; i++) {
//            if (valores[i]) {
//                BeanContact contacto = (BeanContact) listaContactosBusq.get(i);
//                lista.add(contacto.getIdContacto());
//            }
//        }
//
//        AppiTalkYou app = (AppiTalkYou) getActivity().getApplication();
//        LogicContact.guardarFavoritos(getActivity().getApplicationContext(), lista, app.getUsuario().getAnexo());
//        cargarFragmento();
//
//    }

    @Override
    public boolean onQueryTextSubmit(String arg0) {
        return false;
    }


    private void cargarFragmento() {

        String indice = "";
        if (pantalla.equals(Const.PANTALLA_FAVORITOS))
            indice = Const.indice_2;
        else if (pantalla.equals(Const.PANTALLA_CONTACTO_LLAMADA))
            indice = Const.indice_1;
        else
            indice = Const.indice_0;


        Fragment fragmento = PrincipalFragment.nuevaInstancia(indice);
        FragmentManager fragmentManager = getFragmentManager();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.contenedor_fragmentos, fragmento);
        fragmentTransaction.commit();

    }

    public static ListaContactoFragment nuevaInstancia(String pantalla) {

        ListaContactoFragment fragmento = new ListaContactoFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Const.DATOS_TIPO, pantalla);
        fragmento.setArguments(bundle);

        return fragmento;
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View view, int posicion, long id) {

        Fragment mFragment = null;
        BeanContact contact = (BeanContact) listaContactosBusq.get(posicion);
        String num = contact.getTelefono();
        Bundle arg = new Bundle();

        arg.putString(Const.KEY_PHONENUMBER, num);

        if (pantalla.equals(Const.PANTALLA_CONTACTO_LLAMADA)) {

            mFragment = PrincipalFragment.nuevaInstancia(Const.indice_1, arg);
            mFragment.setArguments(arg);

        } else if (pantalla.equals(Const.PANTALLA_CONTACTO_SMS)) {

            mFragment = PrincipalFragment.nuevaInstancia(Const.indice_3, arg);
            mFragment.setArguments(arg);

        } else {
            BeanContact opcion = (BeanContact) listaContactosBusq.get(posicion);
            LogicaPantalla.personalizarIntentDatosContacto(getActivity(), opcion);
        }

        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.contenedor_fragmentos, mFragment);
        transaction.commit();

    }

    @Override
    public void onRefresh() {
        showRefresh(true);
        fetchContacts();
    }


    private void showRefresh(boolean flag) {

        try {
            mSwipeRefreshLayout.setRefreshing(flag);
        } catch (Exception ex) {
            Log.e("Refresh fail", "false");
        }
    }

    private void showLoader(final boolean isVisible) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (isVisible) {
                    mListView.setVisibility(View.GONE);
                    pbLoader.setVisibility(View.VISIBLE);
                } else {
                    mListView.setVisibility(View.VISIBLE);
                    pbLoader.setVisibility(View.GONE);
                }

            }
        });


    }

//    @Override
//    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//
//    }

    private void showItyContacts(boolean isChecked) {
        if (isChecked)
            reloadListIty();
        else
            reloadList();
    }

    private void reloadListIty() {
        List<Object> objects = LogicContact.obtenerListadoContactosAnexo(getActivity());
        listaContactosBusq.clear();
        listaContactosBusq.addAll(objects);
        adaptador.notifyDataSetChanged();
    }
}


