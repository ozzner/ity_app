package com.italkyou.gui.personalizado;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.italkyou.beans.BeanContact;
import com.italkyou.beans.BeanPais;
import com.italkyou.beans.BeanTelefono;
import com.italkyou.beans.entradas.CeldaImagen;
import com.italkyou.beans.salidas.OutputContact;
import com.italkyou.beans.salidas.SalidaMovimiento;
import com.italkyou.gui.R;
import com.italkyou.gui.chat.CrearChatGrupalFragment;
import com.italkyou.utils.AppUtil;
import com.italkyou.utils.ChatITY;
import com.italkyou.utils.Const;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdaptadorLista extends ArrayAdapter<Object> {
    public static final String TAG = AdaptadorLista.class.getSimpleName();
    private Context context;
    private int idCelda;
    private List<Object> datos;
    private String claseObj;
    private String otroDato;
    private List<String> lstAnexos;
    private static OnMenuItemListener mListener;
    private static int index = 0;

    public AdaptadorLista(Context context, int idCelda_personalizada, List<Object> datos, String clase) {
        super(context, idCelda_personalizada, datos);

        this.context = context;
        this.idCelda = idCelda_personalizada;
        this.datos = datos;
        this.claseObj = clase;
    }

    public AdaptadorLista(Context context, int idCelda_personalizada, List<Object> datos, String clase, String otroDato) {
        super(context, idCelda_personalizada, datos);

        this.context = context;
        this.idCelda = idCelda_personalizada;
        this.datos = datos;
        this.claseObj = clase;
        this.otroDato = otroDato;

    }

    public AdaptadorLista(Context context, int idCelda_personalizada, List<Object> datos, String clase, String otroDato, List<String> lstAnexos) {
        super(context, idCelda_personalizada, datos);

        this.context = context;
        this.idCelda = idCelda_personalizada;
        this.datos = datos;
        this.claseObj = clase;
        this.otroDato = otroDato;
        this.lstAnexos = lstAnexos;

    }


    @Override
    public int getPosition(Object item) {
        return super.getPosition(item);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View v = convertView;
        index = position;

        if (claseObj.equals(BeanPais.class.getSimpleName())) {
            opcionSimpleHolder opcSimpleHolder = new opcionSimpleHolder();

            if (v == null) {
                LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(idCelda, null);
                opcSimpleHolder.itemTexto = (TextView) v.findViewById(R.id.tvTexto);
                v.setTag(opcSimpleHolder);
            } else {
                opcSimpleHolder = (opcionSimpleHolder) v.getTag();
            }

            BeanPais pais = (BeanPais) datos.get(position);

            if (otroDato.equals(Const.IDIOMA_ES))
                opcSimpleHolder.itemTexto.setText(pais.getDescripcionES());
            else
                opcSimpleHolder.itemTexto.setText(pais.getDescripcionEN());


        } else if (claseObj.equals(CeldaImagen.class.getSimpleName())) {

            opcionImagenHolder opcionHolder = new opcionImagenHolder();

            if (v == null) {

                LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(idCelda, null);
                opcionHolder.itemDescripcion = (TextView) v.findViewById(R.id.tvTextoCelda);
                opcionHolder.itemImagen = (ImageView) v.findViewById(R.id.imgCelda);

            } else {
                opcionHolder = (opcionImagenHolder) v.getTag();
            }

            v.setTag(opcionHolder);
            CeldaImagen opcion = (CeldaImagen) datos.get(position);
            opcionHolder.itemDescripcion.setText(opcion.getTextoCelda());
//            opcionHolder.itemImagen.setBackgroundResource(opcion.getIdImagen());
            opcionHolder.itemImagen.setImageBitmap(
                    AppUtil.getRoundedCornerImage(
                            context.getResources().getDrawable(opcion.getIdImagen()),
                            false));
            ;

        } else if (claseObj.equals(BeanContact.class.getSimpleName())) {

            final BeanContact contacto = (BeanContact) datos.get(position);
            OpcionContactoHolder opcContactoHolder = new OpcionContactoHolder();

            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(idCelda, null);
            opcContactoHolder.itemImagen = (ImageView) v.findViewById(R.id.imgContacto);
            opcContactoHolder.itemNombre = (TextView) v.findViewById(R.id.tvNombreContacto);
            opcContactoHolder.itemEsUsuario = (ImageView) v.findViewById(R.id.imgEsUsuario);
            opcContactoHolder.itemNumero = (TextView) v.findViewById(R.id.tv_number);
            opcContactoHolder.ivPopupMenu = (ImageView) v.findViewById(R.id.iv_popup_menu);
            opcContactoHolder.itemControls = (LinearLayout) v.findViewById(R.id.lay_controls);
            opcContactoHolder.itemCounter = (TextView) v.findViewById(R.id.txt_count_phones);

            opcContactoHolder.itemNombre.setText(contacto.getNombre());
            String[] parts = contacto.getTelefono().split(Const.SPLIT_COMA);
            String phoneNumber = parts[0];

            if (parts.length > 1) {
                opcContactoHolder.itemControls.setVisibility(View.VISIBLE);
                opcContactoHolder.itemCounter.setText(Const.CADENA_PREFIJO + parts.length);
            }


            opcContactoHolder.itemNumero.setText(phoneNumber);
            opcContactoHolder.itemImagen.setBackgroundResource(0);
            opcContactoHolder.itemImagen.setImageBitmap(null);
            opcContactoHolder.itemEsUsuario.setBackgroundResource(0);

            final OpcionContactoHolder finalOpcContactoHolder1 = opcContactoHolder;
            opcContactoHolder.ivPopupMenu.setOnClickListener(new OnClickListener() {


                @Override
                public void onClick(View v) {

                    Context wrapper = new ContextThemeWrapper(getContext(), R.style.PopupMenu);
                    final PopupMenu popup = new PopupMenu(wrapper, finalOpcContactoHolder1.ivPopupMenu);
                    popup.getMenuInflater().inflate(R.menu.menu_contact_actions, popup.getMenu());
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            Log.e(TAG, "Position adapter popup: " + position);
                            mListener.onMenuItemClick(item, position);
                            return false;
                        }
                    });

                    // Force icons to show
                    Object menuHelper;
                    Class[] argTypes;

                    try {
                        Field fMenuHelper = PopupMenu.class.getDeclaredField("mPopup");
                        fMenuHelper.setAccessible(true);
                        menuHelper = fMenuHelper.get(popup);
                        argTypes = new Class[]{boolean.class};
                        menuHelper.getClass().getDeclaredMethod("setForceShowIcon", argTypes).invoke(menuHelper, true);
                    } catch (Exception e) {
                        // Possible exceptions are NoSuchMethodError and NoSuchFieldError
                        //
                        // In either case, an exception indicates something is wrong with the reflection code, or the
                        // structure of the PopupMenu class or its dependencies has changed.
                        //
                        // These exceptions should never happen since we're shipping the AppCompat library in our own apk,
                        // but in the case that they do, we simply can't force icons to display, so log the error and
                        // show the menu normally.

                        Log.w(TAG, "error forcing menu icons to show", e);
                        popup.show();
                        return;
                    }


                    popup.show();


                    // Try to force some horizontal offset
                    try {
                        Field fListPopup = menuHelper.getClass().getDeclaredField("mPopup");
                        fListPopup.setAccessible(true);
                        Object listPopup = fListPopup.get(menuHelper);
                        argTypes = new Class[]{int.class};
                        Class listPopupClass = listPopup.getClass();

                        // Get the width of the popup window
                        int width = (Integer) listPopupClass.getDeclaredMethod("getWidth").invoke(listPopup);

                        // Invoke setHorizontalOffset() with the negative width to move left by that distance
                        listPopupClass.getDeclaredMethod("setHorizontalOffset", argTypes).invoke(listPopup, -width);

                        // Invoke show() to update the window's position
                        listPopupClass.getDeclaredMethod("show").invoke(listPopup);
                    } catch (Exception e) {
                        // Again, an exception here indicates a programming error rather than an exceptional condition
                        // at runtime
                    }


                }
            });

            if (contacto.getFoto() > 0) {

                Uri.Builder newUriBuilder = ContactsContract.Contacts.CONTENT_LOOKUP_URI.buildUpon();
                newUriBuilder.appendPath(contacto.getLookUpKey());
//                Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contacto.getIdContacto());
                InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(context.getContentResolver(), newUriBuilder.build());

                if (input != null) {
                    //RoundedCorner
                    Bitmap bm = BitmapFactory.decodeStream(input);
                    opcContactoHolder.itemImagen.setImageBitmap(AppUtil.getRoundedCornerImage(bm, true));
                } else {
                    opcContactoHolder.itemImagen.setBackgroundResource(R.drawable.ic_contact);
                }

            } else {
                opcContactoHolder.itemImagen.setBackgroundResource(R.drawable.ic_contact);
            }

            if (contacto.getUsuarioITY().equals(Const.USER_ITY)) {
                opcContactoHolder.itemNumero.setTextColor(context.getResources().getColor(R.color.italkyou_primary_purple));
                opcContactoHolder.itemEsUsuario.setVisibility(View.VISIBLE);
                opcContactoHolder.itemEsUsuario.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(context, context.getResources().getString(R.string.is_on_ity), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                opcContactoHolder.itemNumero.setTextColor(context.getResources().getColor(R.color.gray_color_4));
                opcContactoHolder.itemEsUsuario.setVisibility(View.GONE);
            }

        } else if (claseObj.equals(ParseObject.class.getSimpleName())) {

            if (otroDato.equals(Const.USER_ITY)) {

                opcionContactoItalkyouHolder opcContactoHolder = new opcionContactoItalkyouHolder();

//                if (v == null) {
                LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(idCelda, null);
                opcContactoHolder.itemImagen = (CircleImageView) v.findViewById(R.id.imgContactoItalkyou);
                opcContactoHolder.itemNombre = (TextView) v.findViewById(R.id.tvNombreContactoItalkyou);
                opcContactoHolder.itemTelefono = (TextView) v.findViewById(R.id.tvNumeroContactoItalkyou);
                opcContactoHolder.itemAnexo = (TextView) v.findViewById(R.id.tvAnexoContactoItalkyou);
                opcContactoHolder.itemImagenChatStatus = (ImageView) v.findViewById(R.id.iv_chat_status);

//                    v.setTag(opcContactoHolder);
//
//                } else {
//                    opcContactoHolder = (opcionContactoItalkyouHolder) v.getTag();
//                }
//

                try {


                    ParseObject contacto = (ParseObject) datos.get(position);
                    String name = contacto.getString(ChatITY.USER_USER);
                    String phone = contacto.getString(ChatITY.USER_PHONE);
                    String annex = contacto.getString(ChatITY.USER_ANNEX);

                    if (name == null || phone == null || annex == null) {
                        datos.remove(position);
                        return null;
                    }

                    opcContactoHolder.itemNombre.setText(name);
                    opcContactoHolder.itemTelefono.setText(phone);
                    opcContactoHolder.itemAnexo.setText(AppUtil.formatAnnex(annex));
                    opcContactoHolder.itemImagen.setBackgroundResource(0);
                    opcContactoHolder.itemImagen.setImageBitmap(null);

                    if (contacto.getBoolean(ChatITY.USER_FLAG_IMAGE)) {
                        ParseFile file = (ParseFile) contacto.get(ChatITY.USER_IMAGE);

                        try {

                            // RoundedCorner
                            byte[] data = file.getData();
                            Glide.with(context).load(data).into(opcContactoHolder.itemImagen);
//                            Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length);
//                            bm = Bitmap.createScaledBitmap(bm, 256, 256, false);
//                            bm = AppUtil.getRoundedCornerImage(bm, true);
//                            opcContactoHolder.itemImagen.setImageBitmap(bm);

                        } catch (ParseException e) {
                            opcContactoHolder.itemImagen.setBackgroundResource(R.drawable.ic_contact);
                            e.printStackTrace();
                        } catch (NullPointerException ex) {
                            opcContactoHolder.itemImagen.setBackgroundResource(R.drawable.ic_contact);
                        }


                    } else {
                        opcContactoHolder.itemImagen.setBackgroundResource(R.drawable.ic_contact);
                    }

                    if (contacto.getString(ChatITY.USER_STATUS).equals(Const.status_no_connected)) {
                        opcContactoHolder.itemImagenChatStatus.setColorFilter(context.getResources().getColor(R.color.gray_color_3));
                    }


                } catch (NullPointerException e) {
                    Log.e(TAG, "Exception-> " + e.getMessage());
                    datos.remove(position);

                } catch (Exception ex) {
                    Log.e(TAG, "Ex-> " + ex.getMessage());
                    datos.remove(position);
                }

            } else if (otroDato.equals(Const.USUARIO_ITALKYOU_SELECCIONADO)) {
                opcionContactoSeleccionadoHolder opcContactoHolder = new opcionContactoSeleccionadoHolder();

                if (v == null) {
                    LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(idCelda, null);
                    opcContactoHolder.itemImagen = (ImageView) v.findViewById(R.id.imgParticipante);
                    opcContactoHolder.itemNombre = (TextView) v.findViewById(R.id.tvNombreParticipante);
                    opcContactoHolder.itemAnexo = (TextView) v.findViewById(R.id.tvAnexoParticipante);
                    opcContactoHolder.itemBoton = (ImageButton) v.findViewById(R.id.imgEliminarParticipante);
                    v.setTag(opcContactoHolder);

                } else {
                    opcContactoHolder = (opcionContactoSeleccionadoHolder) v.getTag();
                }


                ParseObject contacto = (ParseObject) datos.get(position);
                opcContactoHolder.itemNombre.setText(contacto.getString(ChatITY.USER_USER));
                opcContactoHolder.itemAnexo.setText(contacto.getString(ChatITY.USER_ANNEX));
                opcContactoHolder.itemImagen.setBackgroundResource(0);
                opcContactoHolder.itemImagen.setImageBitmap(null);

                if (contacto.getBoolean(ChatITY.USER_FLAG_IMAGE)) {
                    ParseFile file = (ParseFile) contacto.get(ChatITY.USER_IMAGE);

                    try {
                        // RoundedCorner
                        byte[] data = file.getData();
                        Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length);
                        opcContactoHolder.itemImagen.setImageBitmap(AppUtil.getRoundedCornerImage(bm, true));
                    } catch (ParseException e) {
                        opcContactoHolder.itemImagen.setBackgroundResource(R.drawable.ic_contact);
                        e.printStackTrace();
                    }

                } else {
                    opcContactoHolder.itemImagen.setBackgroundResource(R.drawable.ic_contact);
                }


                if (lstAnexos != null) {
                    if (position < lstAnexos.size()) {
                        opcContactoHolder.itemBoton.setVisibility(View.INVISIBLE);
                    }
                } else {

                    final int pos = position;
                    opcContactoHolder.itemBoton.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            CrearChatGrupalFragment.actualizarParticipantes(pos);
                        }
                    });
                }
            }


        } else if (claseObj.equals(OutputContact.class.getSimpleName())) {

            opcionContactoItalkyouHolder opcContactoHolder = new opcionContactoItalkyouHolder();
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(idCelda, null);
                opcContactoHolder.itemImagen = (CircleImageView) v.findViewById(R.id.imgContactoItalkyou);
                opcContactoHolder.itemNombre = (TextView) v.findViewById(R.id.tvNombreContacto);
                opcContactoHolder.itemTelefono = (TextView) v.findViewById(R.id.tv_number);
                opcContactoHolder.itemAnexo = (TextView) v.findViewById(R.id.tv_anexo);
            } else {
                opcContactoHolder = (opcionContactoItalkyouHolder) v.getTag();
            }
            v.setTag(opcContactoHolder);
            OutputContact contacto = (OutputContact) datos.get(position);
            opcContactoHolder.itemNombre.setText(contacto.getNombre());
            opcContactoHolder.itemTelefono.setText(contacto.getCelular());
            opcContactoHolder.itemAnexo.setText(AppUtil.formatAnnex(contacto.getAnexo()));
//            opcContactoHolder.itemImagen.setBackgroundResource(0);
//            opcContactoHolder.itemImagen.setBackgroundResource(R.drawable.im);

        } else if (claseObj.equals(BeanTelefono.class.getSimpleName())) {
            opcionTelefonoHolder telefonoHolder = new opcionTelefonoHolder();

            if (v == null) {

                LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(idCelda, null);
                telefonoHolder.itemTelefono = (TextView) v.findViewById(R.id.tvNumeroTelefono);
                telefonoHolder.itemAnexo = (TextView) v.findViewById(R.id.tvAnexoTelefono);
                telefonoHolder.itemImagen = (ImageView) v.findViewById(R.id.imgUsuario);

            } else {
                telefonoHolder = (opcionTelefonoHolder) v.getTag();
            }

            telefonoHolder.itemImagen.setBackgroundResource(0);
            v.setTag(telefonoHolder);

            BeanTelefono telefono = (BeanTelefono) datos.get(position);
            telefonoHolder.itemTelefono.setText(telefono.getNumero());

            if (!telefono.getAnexo().isEmpty())
                telefonoHolder.itemAnexo.setText(AppUtil.formatAnnex(telefono.getAnexo()));

            if (telefono.getAnexo().equals(Const.cad_vacia) || telefono.getAnexo() == null) {
                telefonoHolder.itemImagen.setBackgroundResource(R.drawable.no_es_usuario);

            } else {
                telefonoHolder.itemAnexo.setVisibility(View.VISIBLE);

                if (telefono.getConectado().equals(Const.ANEXO_NO_CONECTADO)) {
                    telefonoHolder.itemImagen.setBackgroundResource(R.drawable.es_usuario);
                } else if (telefono.getConectado().equals(Const.ANEXO_CONECTADO)) {
                    telefonoHolder.itemImagen.setBackgroundResource(R.drawable.contacto_italkyou);
                }
            }

        } else if (claseObj.equals(SalidaMovimiento.class.getSimpleName())) {
            opcionMovimientoHolder opcionMovimiento = new opcionMovimientoHolder();
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(idCelda, null);
                opcionMovimiento.itemFecha = (TextView) v.findViewById(R.id.tvFechaMovimiento);
                opcionMovimiento.itemDescripcion = (TextView) v.findViewById(R.id.tvDescripcionMovimiento);
                opcionMovimiento.itemMonto = (TextView) v.findViewById(R.id.tvMontoMovimiento);
                v.setTag(opcionMovimiento);
            } else {
                opcionMovimiento = (opcionMovimientoHolder) v.getTag();
            }

            SalidaMovimiento movimiento = (SalidaMovimiento) datos.get(position);
            opcionMovimiento.itemFecha.setText(movimiento.getFecha());
            opcionMovimiento.itemDescripcion.setText(AppUtil.formatoDescripcion(movimiento.getDescripcion(), otroDato));
            opcionMovimiento.itemMonto.setText(movimiento.getMonto());

        }

        return v;
    }


    public View getDropDownView(final int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (claseObj.equals(BeanPais.class.getSimpleName())) {
            opcionSimpleHolder listaSimpleHolder = new opcionSimpleHolder();
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(idCelda, null);
                listaSimpleHolder.itemTexto = (TextView) v.findViewById(R.id.tvTexto);
                v.setTag(listaSimpleHolder);
            } else {
                listaSimpleHolder = (opcionSimpleHolder) v.getTag();
            }

            BeanPais pais = (BeanPais) datos.get(position);
            if (otroDato.equals(Const.IDIOMA_ES))
                listaSimpleHolder.itemTexto.setText(pais.getDescripcionES());
            else
                listaSimpleHolder.itemTexto.setText(pais.getDescripcionEN());
        } else if (claseObj.equals(BeanContact.class.getSimpleName())) {
//
//
//                //TODO AQUI CARGA LOS CONTACTOS DEL TELEFONO.
//                final BeanContact contacto = (BeanContact) datos.get(position);
//                OpcionContactoHolder opcContactoHolder = new OpcionContactoHolder();
//                if (v == null) {
//                    LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                    v = vi.inflate(idCelda, null);
//                    opcContactoHolder.itemImagen = (ImageView) v.findViewById(R.id.imgContacto);
//                    opcContactoHolder.itemNombre = (TextView) v.findViewById(R.id.tvNombreContacto);
//                    opcContactoHolder.itemEsUsuario = (ImageView) v.findViewById(R.id.imgEsUsuario);
//                    opcContactoHolder.itemNumero = (TextView) v.findViewById(R.id.tv_number);
//                    opcContactoHolder.ivPopupMenu = (ImageView)v.findViewById(R.id.iv_popup_menu);
//                    final OpcionContactoHolder finalOpcContactoHolder = opcContactoHolder;
//
//                    opcContactoHolder.ivPopupMenu.setOnClickListener(new OnClickListener() {
//
//                        @Override
//                        public void onClick(View v) {
//
//                            Context wrapper = new ContextThemeWrapper(getContext(), R.style.PopupMenu);
//                            final PopupMenu popup = new PopupMenu(wrapper, finalOpcContactoHolder.ivPopupMenu);
//                            popup.getMenuInflater().inflate(R.menu.menu_contact_actions,popup.getMenu());
//                            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                                @Override
//                                public boolean onMenuItemClick(MenuItem item) {
//                                    Log.e(TAG,"Position adapter popup: "+position);
//                                    mListener.onMenuItemClick(item);
//                                    return false;
//                                }
//                            });
//
//                            // Force icons to show
//                            Object menuHelper;
//                            Class[] argTypes;
//
//                            try {
//                                Field fMenuHelper = PopupMenu.class.getDeclaredField("mPopup");
//                                fMenuHelper.setAccessible(true);
//                                menuHelper = fMenuHelper.get(popup);
//                                argTypes = new Class[] { boolean.class };
//                                menuHelper.getClass().getDeclaredMethod("setForceShowIcon", argTypes).invoke(menuHelper, true);
//                            } catch (Exception e) {
//                                // Possible exceptions are NoSuchMethodError and NoSuchFieldError
//                                //
//                                // In either case, an exception indicates something is wrong with the reflection code, or the
//                                // structure of the PopupMenu class or its dependencies has changed.
//                                //
//                                // These exceptions should never happen since we're shipping the AppCompat library in our own apk,
//                                // but in the case that they do, we simply can't force icons to display, so log the error and
//                                // show the menu normally.
//
//                                Log.w(TAG, "error forcing menu icons to show", e);
//                                popup.show();
//                                return;
//                            }
//
//
//                            popup.show();
//                        }
//                    });
//
//
//                } else {
//                    opcContactoHolder = (OpcionContactoHolder) v.getTag();
//                }
//
//                v.setTag(opcContactoHolder);
//
//
//                opcContactoHolder.itemNombre.setText(contacto.getNombre());
//                String[] parts = contacto.getTelefono().split(Const.SPLIT_COMA);
//                String phoneNumber = parts[0];
//                opcContactoHolder.itemNumero.setText(phoneNumber);
//                opcContactoHolder.itemImagen.setBackgroundResource(0);
//                opcContactoHolder.itemImagen.setImageBitmap(null);
//                opcContactoHolder.itemEsUsuario.setBackgroundResource(0);
//
//                if (contacto.getFoto() > 0) {
//
//                    Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contacto.getIdContacto());
//                    InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(context.getContentResolver(), uri);
//
//                    if (input != null) {
//                        opcContactoHolder.itemImagen.setImageBitmap(BitmapFactory.decodeStream(input));
//                    } else {
//                        opcContactoHolder.itemImagen.setBackgroundResource(R.drawable.ic_contact);
//                    }
//
//                } else {
//                    opcContactoHolder.itemImagen.setBackgroundResource(R.drawable.ic_contact);
//                }
//
//                if (contacto.getUsuarioITY().equals(Const.USER_ITY)) {
//                    opcContactoHolder.itemNumero.setTextColor(context.getResources().getColor(R.color.iTalkYou_Morado_Suave));
//                    opcContactoHolder.itemEsUsuario.setVisibility(View.VISIBLE);
//                    opcContactoHolder.itemEsUsuario.setOnClickListener(new OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            Toast.makeText(context, context.getResources().getString(R.string.is_on_ity),Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                } else {
//                    opcContactoHolder.itemNumero.setTextColor(context.getResources().getColor(R.color.gray_color_4));
//                    opcContactoHolder.itemEsUsuario.setVisibility(View.GONE);
//                }
//
//            }
        }
        return v;
    }

    private class opcionSimpleHolder {
        public TextView itemTexto;
    }

    private class opcionImagenHolder {
        public TextView itemDescripcion;
        public ImageView itemImagen;
    }

    private class OpcionContactoHolder {
        public ImageView itemImagen;
        public TextView itemNombre;
        public TextView itemNumero;
        public ImageView itemEsUsuario;
        public ImageView ivPopupMenu;
        public LinearLayout itemControls;
        public TextView itemCounter;
    }

    private class opcionContactoItalkyouHolder {
        public CircleImageView itemImagen;
        public ImageView itemImagenChatStatus;
        public TextView itemNombre;
        public TextView itemTelefono;
        public TextView itemAnexo;
    }

    private class opcionContactoSeleccionadoHolder {
        public ImageView itemImagen;
        public TextView itemNombre;
        public TextView itemAnexo;
        public ImageButton itemBoton;
    }

    private class opcionTelefonoHolder {
        public ImageView itemImagen;
        public TextView itemTelefono;
        public TextView itemAnexo;
    }

    private class opcionMovimientoHolder {
        public TextView itemFecha;
        public TextView itemMonto;
        public TextView itemDescripcion;
    }

    //To menu popups events
    public interface OnMenuItemListener {
        void onMenuItemClick(MenuItem item, int index);
    }

    public static void setOnMenuItemListener(OnMenuItemListener listener) {
        mListener = listener;
    }
}
