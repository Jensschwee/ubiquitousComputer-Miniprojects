package uci.mmmi.sdu.dk.contextawarenessproject.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.ListFragment;

import uci.mmmi.sdu.dk.contextawarenessproject.common.BaseFragment;
import uci.mmmi.sdu.dk.contextawarenessproject.fragments.MapsFragment;
import uci.mmmi.sdu.dk.contextawarenessproject.fragments.SettingsFragment;

public class MapsViewPagerAdapter extends FragmentStatePagerAdapter {


    private BaseFragment settingsFragment;
    private Fragment inNOutFragment;
    private BaseFragment mapsFragment;

    public MapsViewPagerAdapter(FragmentManager fm) {
        super(fm);
        settingsFragment = new SettingsFragment();
        inNOutFragment = new Fragment();
        mapsFragment = new MapsFragment();
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return inNOutFragment;
            case 1:
                return mapsFragment;
            case 2:
                return settingsFragment;
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "In and Out Board";
            case 1:
                return "Your position";
            case 2:
                return "Settings";
        }
        return super.getPageTitle(position);
    }
}
