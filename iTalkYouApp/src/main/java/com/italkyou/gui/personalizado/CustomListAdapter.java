package com.italkyou.gui.personalizado;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.italkyou.beans.popup.MenuActionsChat;
import com.italkyou.beans.popup.MenuActionsContact;
import com.italkyou.gui.R;
import com.italkyou.utils.Const;
import com.italkyou.utils.AppUtil;
import com.parse.ParseObject;

import java.util.List;

/**
 * Created by RenzoD on 05/06/2015.
 */
public class CustomListAdapter extends BaseAdapter {

    private static final String COLOR_GRAY = "#9E9E9E";
    private static final String COLOR_RED = "#EE2C2C";
    private static final String COLOR_BLUE = "#009ADA";
    private List<Object> listItems;
    private List<ParseObject> parseListItems;
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private int tag;
    private static OnClickAlertListItem mListener;


    public CustomListAdapter(int tag, Context mContext, List<Object> listItems) {
        this.tag = tag;
        this.mContext = mContext;
        this.mLayoutInflater = LayoutInflater.from(mContext);
        this.listItems = listItems;
    }

    public static void setAlertListItemListener(OnClickAlertListItem listener){
        mListener = listener;
    }

    @Override
    public int getCount() {
        return listItems.size();
    }

    @Override
    public Object getItem(int index) {
        return listItems.get(index);
    }

    @Override
    public long getItemId(int position) {
        return -1;
    }

    @Override
    public View getView(final int index, View convertView, ViewGroup parent) {

        switch (tag) {

            case Const.INDEX_DIALOG_CONTACT://Menu for action (Call ITY, call international, send sms)

                MenuActionsViewHolder actionsHolder = new MenuActionsViewHolder();

                if (convertView == null) {
                    convertView = mLayoutInflater.inflate(R.layout.row_popup_menu_actions, parent, false);
                    actionsHolder.ivMenuActions = (ImageView) convertView.findViewById(R.id.iv_menu_actions);
                    actionsHolder.tvMenuName = (TextView) convertView.findViewById(R.id.tv_menu_actions_name);

                    convertView.setTag(actionsHolder);
                } else
                    actionsHolder = (MenuActionsViewHolder) convertView.getTag();

                /*setup*/
                MenuActionsContact actions = (MenuActionsContact) listItems.get(index);
                actionsHolder.ivMenuActions.setImageResource(actions.getImage());
                actionsHolder.tvMenuName.setText(actions.getTitle());

                break;


            case Const.INDEX_DIALOG_CHAT:
                MenuActionsViewHolder actionsChatHolder = new MenuActionsViewHolder();

                if (convertView == null) {
                    convertView = mLayoutInflater.inflate(R.layout.row_popup_menu_actions, parent, false);
                    actionsChatHolder.ivMenuActions = (ImageView) convertView.findViewById(R.id.iv_menu_actions);
                    actionsChatHolder.tvMenuName = (TextView) convertView.findViewById(R.id.tv_menu_actions_name);

                    convertView.setTag(actionsChatHolder);
                } else
                    actionsChatHolder = (MenuActionsViewHolder) convertView.getTag();

                /*setup*/
                MenuActionsChat actionsChat = (MenuActionsChat) listItems.get(index);
                actionsChatHolder.ivMenuActions.setImageResource(actionsChat.getImageAction());

                int color;
                if (index == 0)
                    color = Color.parseColor(COLOR_BLUE);
                else
                    color = Color.parseColor(COLOR_RED);

                actionsChatHolder.ivMenuActions.setColorFilter(color);
                actionsChatHolder.tvMenuName.setText(actionsChat.getTitle());
                break;

            case Const.INDEX_DIALOG_CUSTOM_LIST:
                MenuActionsViewHolder customHolder = new MenuActionsViewHolder();

//                if (convertView == null) {
                    convertView = mLayoutInflater.inflate(R.layout.row_popup_menu_chat, parent, false);
                    customHolder.ivMenuActions = (ImageView) convertView.findViewById(R.id.iv_menu_chat_action);
                    customHolder.ivMenuStatus = (ImageView) convertView.findViewById(R.id.iv_menu_chat_status);
                    customHolder.tvMenuName = (TextView) convertView.findViewById(R.id.tv_menu_chat_name);
                    customHolder.tvMenuAnnex = (TextView) convertView.findViewById(R.id.tv_menu_chat_annex);

//                    convertView.setTag(customHolder);
//                } else
//                    customHolder = (MenuActionsViewHolder) convertView.getTag();


                /*setup*/
                final MenuActionsChat oMenu = (MenuActionsChat) listItems.get(index);
                customHolder.tvMenuName.setText(oMenu.getName());
                customHolder.tvMenuAnnex.setText(AppUtil.formatAnnex(oMenu.getAnnex()));

                //No está en linea
                if (!oMenu.isFagStatus())
                    customHolder.ivMenuStatus.setColorFilter(Color.parseColor(COLOR_GRAY));

                customHolder.ivMenuActions.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.onCallItemList(oMenu.getAnnex(),oMenu.getName());
                    }
                });


                break;
        }

        return convertView;
    }


    /*View holdes patron*/
    static class MenuActionsViewHolder {
        ImageView ivMenuActions;
        ImageView ivMenuStatus;
        TextView tvMenuName;
        TextView tvMenuAnnex;
    }

    public interface OnClickAlertListItem {
        void onCallItemList(String annex,String name);
    }



}
