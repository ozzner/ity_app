package com.italkyou.gui.personalizado;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

/**
 * Created by RenzoD on 05/06/2015.
 */
public class MenuActionsDialogFragment extends DialogFragment {

    private AlertDialog.Builder builder;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        builder = new AlertDialog.Builder(getActivity());
        builtCustomDialog(builder);

        return builder.create();
    }

    private void builtCustomDialog(AlertDialog.Builder builder) {
        builder.setTitle("Choice any option")
         .setPositiveButton("ok", new DialogInterface.OnClickListener() {
             @Override
             public void onClick(DialogInterface dialog, int which) {

             }
         });

    }


    public interface MenuActionsListener{
        void onItemClick();
    }

}
