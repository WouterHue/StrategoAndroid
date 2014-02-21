package com.example.GitHub;

import android.app.*;

/**
 * Created by wouter on 16/02/14.
 */
public class TabListener<T extends Fragment> implements ActionBar.TabListener {
    private Fragment mFragment;
    private final Activity mActivity;
    private final String mTag;
    private final Class<T> mClass;

    public TabListener(Activity mActivity, String mTag, Class<T> mClass) {
        this.mActivity = mActivity;
        this.mTag = mTag;
        this.mClass = mClass;

    }


    //Show tab
    //fragmentTransaction.addToBackstack() not allowed here!(probably because of tab usage ?)
    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        FragmentManager fragmentManager = mActivity.getFragmentManager();
        //Hide previous fragment
        if(fragmentManager.getBackStackEntryCount()>0){
            FragmentManager.BackStackEntry backStackEntry = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount()-1);
            String currentFragmentTag = backStackEntry.getName();
            Fragment currentFragment = fragmentManager.findFragmentByTag(currentFragmentTag);
            fragmentTransaction.remove(currentFragment);
            fragmentManager.popBackStackImmediate();
        }

        if (mFragment == null){
           mFragment = Fragment.instantiate(mActivity, mClass.getName());
            fragmentTransaction.add(android.R.id.content, mFragment, mTag);
        }
        else fragmentTransaction.attach(mFragment);
    }
    //Hide tab
    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        if (mFragment!=null){
            fragmentTransaction.detach(mFragment);
        }
    }
    //DoSomething when tab reselected(probably do nothing)
    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }


}
