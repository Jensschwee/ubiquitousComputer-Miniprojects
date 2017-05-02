package uci.mmmi.sdu.dk.contextawarenessproject.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import uci.mmmi.sdu.dk.contextawarenessproject.common.BaseFragment;

public class SettingsFragment extends BaseFragment {

    public SettingsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public String getTagText() {
        return "Settings Fragment";
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public Integer getMenuResId() {
        return null;
    }

    @Override
    public String getTitle() {
        return "Settings";
    }

    @Override
    public void onOptionsMenuCreated(Menu menu) {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
