package uci.mmmi.sdu.dk.contextawarenessproject.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by bullari on 5/2/17.
 */

public class MapsViewPagerAdapter extends FragmentStatePagerAdapter {


    private Fragment settingsFragment;
    private Fragment inNOutFragment;
    private Fragment mapsFragment;

    public MapsViewPagerAdapter(FragmentManager fm) {
        super(fm);
        settingsFragment = new Fragment();
        inNOutFragment = new Fragment();
        mapsFragment = new Fragment();
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
