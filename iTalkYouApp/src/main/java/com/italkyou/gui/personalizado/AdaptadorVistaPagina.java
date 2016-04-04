package com.italkyou.gui.personalizado;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.italkyou.gui.chat.ConversacionFragment;
import com.italkyou.gui.contactos.ListContactDeviceFragment;
import com.italkyou.gui.mensajes.FragmentoMensajeria;
import com.viewpagerindicator.IconPagerAdapter;

public class AdaptadorVistaPagina extends FragmentStatePagerAdapter implements IconPagerAdapter{

	private int [] iconos;
	private int mCount;
	private Bundle bundle;

	
	public AdaptadorVistaPagina(FragmentManager fm, int[] iconos,Bundle args) {
		super(fm);
		this.bundle = args;
		this.iconos = iconos;
		this.mCount = iconos.length;
	}

	@Override
	public int getIconResId(int index) {
		return iconos[index % iconos.length];
	}

	@Override
	public Fragment getItem(int posicion) {
		Fragment fragmento;

		switch(posicion) {
        case 0: fragmento =  ConversacionFragment.nuevaInstancia();
            break;
        case 1: fragmento = ListContactDeviceFragment.nuevaInstancia();
//			fragmento.setArguments(bundle);
            break;
//        case 2:
//			fragmento = FavoritoFragment.nuevaInstancia();
//            break;
        case 2: fragmento = FragmentoMensajeria.nuevaInstancia();
//			fragmento.setArguments(bundle);
			break;
        default :  fragmento = null;
        }

		fragmento.setArguments(bundle);
		return fragmento;
	}

	@Override
	public int getCount() {
		return mCount;
	}
	
	public void setCount(int count) {
        if (count > 0 && count <= 10) {
            mCount = count;
            notifyDataSetChanged();
        }
    }


}
