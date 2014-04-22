package info.androidhive.tabsswipe.adapter;

import info.androidhive.tabsswipe.Fragment_3;
import info.androidhive.tabsswipe.Fragment_2;
import info.androidhive.tabsswipe.Fragment_1;
import info.androidhive.tabsswipe.Fragment_4;
import info.androidhive.tabsswipe.Fragment_5;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabsPagerAdapter extends FragmentPagerAdapter {

	public TabsPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int index) {

		switch (index) {
		case 0:
			// Fragment 1
			return new Fragment_1();
		case 1:
			// Fragment 2
			return new Fragment_2();
		case 2:
			// Fragment 3
			return new Fragment_3();
		case 3:
			// Fragment 4
			return new Fragment_4();
		case 4:
			// Fragment 5
			return new Fragment_5();
		}
		return null;
	}

	@Override
	public int getCount() {
		// get item count - equal to number of tabs
		return 5;
	}

}
