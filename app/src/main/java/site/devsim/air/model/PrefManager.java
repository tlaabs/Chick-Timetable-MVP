package site.devsim.air.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import site.devsim.air.R;

public class PrefManager {
    private PrefManager(){}
    private static PrefManager prefManager;

    public static PrefManager getInstance(){
        if(prefManager == null) {
            prefManager = new PrefManager();
        }
        return prefManager;
    }

    public String get(Context context){
        SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(context);
        String savedData = mPref.getString(context.getResources().getString(R.string.timetable_repo),"");
        return savedData;
    }

    public void save(Context context, String data){
        SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = mPref.edit();
        editor.putString(context.getResources().getString(R.string.timetable_repo), data);
        editor.commit();
    }
}
