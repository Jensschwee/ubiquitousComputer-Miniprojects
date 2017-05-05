package uci.mmmi.sdu.dk.contextawarenessproject.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import uci.mmmi.sdu.dk.contextawarenessproject.fragments.MapsFragment;
import uci.mmmi.sdu.dk.contextawarenessproject.fragments.SettingsFragment;

public class MapsViewPagerAdapter extends FragmentStatePagerAdapter {


    private Fragment settingsFragment;
    //private Fragment inNOutFragment;
    private Fragment mapsFragment;

    public MapsViewPagerAdapter(FragmentManager fm) {
        super(fm);
        settingsFragment = new SettingsFragment();
        //inNOutFragment = new Fragment();
        mapsFragment = new MapsFragment();
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return settingsFragment;
            case 1:
                return mapsFragment;
            //case 2:
            //    return inNOutFragment;
        }
        return null;
    }

    @Override
    public int getCount() {
        //return 3;
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Settings";
            //case 0:
            //    return "In and Out Board";
            case 1:
                return "Your position";
        }
        return super.getPageTitle(position);
    }
}
