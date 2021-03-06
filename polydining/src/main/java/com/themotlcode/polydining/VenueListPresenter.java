package com.themotlcode.polydining;

//VenueListPresenter holds the ItemSet for the current page in the ViewPager

import android.support.v4.app.Fragment;

import com.themotlcode.polydining.models.ItemList;

public class VenueListPresenter extends MealPresenter
{
    protected Fragment fragment;

    public VenueListPresenter(Fragment fragment, ItemList items)
    {
        this.items = items;
        this.fragment = fragment;
        setFragment(fragment);
    }
}
