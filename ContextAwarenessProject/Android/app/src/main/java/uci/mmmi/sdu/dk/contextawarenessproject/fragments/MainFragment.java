package uci.mmmi.sdu.dk.contextawarenessproject.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import uci.mmmi.sdu.dk.contextawarenessproject.adapters.MapsViewPagerAdapter;

/**
 * Created by bullari on 5/2/17.
 */

public class MainFragment extends Fragment {

    private MapsViewPagerAdapter mapsViewPagerAdapter;
    private ViewPager mainViewPager;

    public MainFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.mapsViewPagerAdapter = new MapsViewPagerAdapter(getChildFragmentManager());
        /*mainViewPager = (ViewPager) rootView.findViewById(R.id.fragment_main_viewpager);*/
    }

}
