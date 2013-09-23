package com.glabz.Presendroid;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import com.glabz.Presendroid.R;

public class Settings extends PreferenceActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.xml.settings);
	}
	
	public static String getTargetIp(Context context){
		return PreferenceManager.getDefaultSharedPreferences(context).getString("targetIP", "");
	}

	public static int getTargetPort(Context context){
		return Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString("targetPort", ""));
	}
	
	public static int getLocalPort(Context context){
		return Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString("localPort", ""));
	}
}
