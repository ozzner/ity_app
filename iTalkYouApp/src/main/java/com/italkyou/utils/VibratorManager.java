/**
 * Copyright (C) 2013 The Simlar Authors.
 *
 * This file is part of Simlar. (https://www.simlar.org)
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package com.italkyou.utils;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;
import android.os.Vibrator;


final class VibratorManager
{
	public static final long VIBRATE_LENGTH = 1000; // ms
	public static final long VIBRATE_PAUSE = 1000; // ms

	private Context mContext = null;
	private boolean mHasOnGoingAlarm = false;
	private VibratorManagerImpl mImpl = null;
	private final RingerModeReceiver mRingerModeReceiver = new RingerModeReceiver();

	private final class RingerModeReceiver extends BroadcastReceiver
	{
		public RingerModeReceiver()
		{
			super();
		}

		@Override
		public void onReceive(final Context context, final Intent intent)
		{
			VibratorManager.this.onRingerModeChanged();
		}
	}

	private final class VibratorManagerImpl
	{
		private final Handler mHandler;
		private final Vibrator mVibrator;

		public VibratorManagerImpl()
		{
			mHandler = new Handler();
			mVibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
		}

		void startVibration()
		{
			mVibrator.vibrate(VIBRATE_LENGTH);

			mHandler.postDelayed(new Runnable()
			{
				@Override
				public void run()
				{
					startVibration();
				}
			}, VIBRATE_LENGTH + VIBRATE_PAUSE);
		}

		public void stopVibration()
		{
			mHandler.removeCallbacksAndMessages(null);
			mVibrator.cancel();
		}

		@SuppressLint("NewApi")
		public boolean hasVibrator()
		{
			return mVibrator != null && (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB || mVibrator.hasVibrator());
		}
	}

	public VibratorManager(final Context context)
	{
		mContext = context;
	}

	private boolean shouldVibrate()
	{
		return ((AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE)).getRingerMode() != AudioManager.RINGER_MODE_SILENT;
	}

	public void start()
	{
		if (mHasOnGoingAlarm) {
			return;
		}

		mHasOnGoingAlarm = true;

		final IntentFilter filter = new IntentFilter();
		filter.addAction(AudioManager.RINGER_MODE_CHANGED_ACTION);
		mContext.registerReceiver(mRingerModeReceiver, filter);

		if (!shouldVibrate()) {
			return;
		}

		startVibrate();
	}

	private void startVibrate()
	{
		if (mImpl != null) {
			return;
		}

		mImpl = new VibratorManagerImpl();

		if (!mImpl.hasVibrator()) {
			mImpl = null;
			return;
		}

		mImpl.startVibration();
	}

	public void stop()
	{
		if (!mHasOnGoingAlarm) {
			return;
		}

		mHasOnGoingAlarm = false;

		mContext.unregisterReceiver(mRingerModeReceiver);

		stopVibrate();
	}

	private void stopVibrate()
	{
		if (mImpl == null) {
			return;
		}

		mImpl.stopVibration();
		mImpl = null;

	}

	private void onRingerModeChanged()
	{

		if (!mHasOnGoingAlarm) {
			return;
		}

		if (shouldVibrate()) {
			startVibrate();
		} else {
			stopVibrate();
		}
	}
}
