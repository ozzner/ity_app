package com.italkyou.gui.personalizado;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;

import com.italkyou.gui.R;
import com.italkyou.utils.Const;

import java.util.List;

/**
 * Created by RenzoD on 08/06/2015.
 */
public class CustomAlertDialog extends DialogFragment implements DialogInterface.OnClickListener {

    private static final String PARAM_DIALOG = "choose_dialog";
    private static final String PARAM_IS_ANNEX = "is_annex";
    private static final String PARAM_ARRAY = "my_array";
    private static final String PARAM_STRING = "my_title";
    private static final int NO_SELECTED = -1;
    private static final String TAG = CustomAlertDialog.class.getSimpleName();
    public static int TYPE = 19;//This is my birthday =)
    protected int mIndex = -19;//This is my birthday on negative =)

    private OnDialogListener mListener;
    private OnHightImportanceListener mHightImportanceListener;
    private static CustomAlertDialog instance;
    private static List<Object> actionsAlertList;
    private static String[] mArray;


    public CustomAlertDialog() {
    }


    /**
     * @param mDialog 1 = Actions; 2 = Contacts.
     * @param isAnnex If flag equals true then show alert with option to call the annex.
     */
    public static CustomAlertDialog newIntance(int mDialog, List<Object> items, boolean isAnnex) {
        if (instance == null)
            instance = new CustomAlertDialog();
        actionsAlertList = items;

        Bundle arg = new Bundle();
        arg.putInt(PARAM_DIALOG, mDialog);
        arg.putBoolean(PARAM_IS_ANNEX, isAnnex);
        instance.setArguments(arg);

        return instance;
    }


    /**
     * @param mDialog 1 = Actions; 2 = Contacts; -1 = default switch.
     * @param numbers Is an array to make alert with RadioButtons options.
     */
    public static CustomAlertDialog newIntance(int mDialog, boolean isAnnex, String... numbers) {
        instance = new CustomAlertDialog();

        Bundle arg = new Bundle();
        arg.putInt(PARAM_DIALOG, mDialog);
        arg.putBoolean(PARAM_IS_ANNEX, isAnnex);
        arg.putStringArray(PARAM_ARRAY, numbers);
        instance.setArguments(arg);

        return instance;
    }


    /**
     * @param mDialog any number.
     * @param items   list of objects to pick in an Alert
     */
    public static CustomAlertDialog newIntance(int mDialog, List<Object> items) {
        if (instance == null)
            instance = new CustomAlertDialog();
        actionsAlertList = items;

        Bundle arg = new Bundle();
        arg.putInt(PARAM_DIALOG, mDialog);
        instance.setArguments(arg);

        return instance;
    }

    /**
     * @param mDialog any number.
     * @param items   list of objects to pick in Alert
     */
    public static CustomAlertDialog newIntance(int mDialog, List<Object> items, String title) {
        if (instance == null)
            instance = new CustomAlertDialog();
        actionsAlertList = items;

        Bundle arg = new Bundle();
        arg.putInt(PARAM_DIALOG, mDialog);
        arg.putString(PARAM_STRING, title);
        instance.setArguments(arg);

        return instance;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        int choose = getArguments().getInt(PARAM_DIALOG);
        boolean isAnnex = getArguments().getBoolean(PARAM_IS_ANNEX);
        mArray = getArguments().getStringArray(PARAM_ARRAY);

        AlertDialog.Builder buider;

        switch (choose) {

            //Show alert in contacts menu
            case Const.INDEX_DIALOG_CONTACT:
                if (!isAnnex)
                    actionsAlertList.remove(0);//Delete first row

                buider = new AlertDialog.Builder(getActivity());
                buider.setTitle(getResources().getString(R.string.title_menu_choose_actions));
                buider.setCancelable(false);
                buider.setAdapter(new CustomListAdapter(Const.INDEX_DIALOG_CONTACT, getActivity(), actionsAlertList), this);
                break;

            //Show alert in chat menu
            case Const.INDEX_DIALOG_CHAT:
                buider = new AlertDialog.Builder(getActivity());
                buider.setTitle(getResources().getString(R.string.title_menu_choose_actions));
                buider.setCancelable(false);
                buider.setAdapter(new CustomListAdapter(Const.INDEX_DIALOG_CHAT, getActivity(), actionsAlertList), this);
                break;

            //Show an alert from chat using a custom view.
            case Const.INDEX_DIALOG_CUSTOM_LIST:
                buider = new AlertDialog.Builder(getActivity());
                buider.setTitle(getArguments().getString(PARAM_STRING));
                buider.setCancelable(true);
                buider.setAdapter(new CustomListAdapter(Const.INDEX_DIALOG_CUSTOM_LIST, getActivity(), actionsAlertList), this);
                break;

            default:
                String title;
                if (isAnnex)
                    title = getResources().getString(R.string.title_menu_choose_annex);
                else
                    title = getResources().getString(R.string.title_menu_choose_telephone);

                buider = new AlertDialog.Builder(getActivity());
                buider.setTitle(title).setSingleChoiceItems(mArray
                        , NO_SELECTED
                        , this).setCancelable(true);

                break;
        }

        return buider.create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {


        switch (which) {

            case Dialog.BUTTON_NEGATIVE:
                mListener.onNegative(dialog);
                break;

            case Dialog.BUTTON_POSITIVE:
                mListener.onPositive(dialog, which);
                break;

            case Dialog.BUTTON_NEUTRAL:
                mListener.onNeutral(dialog, mIndex);
                break;

            default: // choice selected click

                try {
                    if (TYPE == Const.INDEX_DIALOG_RADIOBUTTON) {
                        mListener.onRadioButtonClickAlert(which);
                        mIndex = which;
                    } else
                        mListener.onItemClickAlert(which);
                }catch (NullPointerException ex){
                    Log.e("Error exception",ex.getMessage());
                }


                break;
        }

    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (OnDialogListener) activity;
            Log.e(TAG, "Correcto! listener");
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            Log.e(TAG, "Error!  listener " + e.getMessage());
        }
    }


    public void showWarningAlert(Context ctx, String message) {
        Resources src = ctx.getResources();
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setMessage(message)
                .setIcon(R.drawable.ic_warning)
                .setTitle(src.getString(R.string.title_alert_warning))
                .setPositiveButton(src.getString(R.string.title_alert_yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mHightImportanceListener.onWarningClick(dialog, which);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        Dialog mDialog = builder.create();
        mDialog.show();

    }

    public static void showSingleAlert(Context ctx, String message) {
        Resources src = ctx.getResources();
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setMessage(message)
                .setIcon(R.drawable.ic_info)
                .setTitle(src.getString(R.string.title_alert_info))
                .setNeutralButton(src.getString(R.string.title_alert_button_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        Dialog mDialog = builder.create();
        mDialog.show();

    }


    public static void showPositiveNegativeAlert(Context ctx, CharSequence body, CharSequence title, final OnPositiveNegativeListener listener) {
        Resources src = ctx.getResources();
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setMessage(body)
                .setIcon(R.drawable.ic_warning)
                .setTitle(title)
                .setNegativeButton(src.getString(R.string.title_alert_edit), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onNegative();
                    }
                })
                .setPositiveButton(src.getString(R.string.title_alert_yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onPositive();
                    }
                });

        Dialog mDialog = builder.create();
        mDialog.show();
    }

    public interface OnDialogListener {
        void onPositive(DialogInterface dialog, int index);

        void onNegative(DialogInterface dialog);

        void onNeutral(DialogInterface dialog, int index);

        void onItemClickAlert(int index);

        void onRadioButtonClickAlert(int index);
    }

    public interface OnPositiveNegativeListener {
        void onPositive();
        void onNegative();
    }


    public void setDialogListener(OnDialogListener listener)     {
        this.mListener = listener;
    }

    public void setOnHightImportanceListener(OnHightImportanceListener listener) {
        this.mHightImportanceListener = listener;
    }

    public interface OnHightImportanceListener {
        void onWarningClick(DialogInterface dialog, int index);
    }

}
