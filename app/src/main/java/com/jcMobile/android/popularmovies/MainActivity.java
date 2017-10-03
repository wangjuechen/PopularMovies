package com.jcMobile.android.popularmovies;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.TextView;
import java.lang.reflect.Field;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        SearchResultFragment.OnFragmentInteractionListener {

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;

    private FrameLayout mSearchFragmentContainer;

    @BindView(com.jcMobile.android.popularmovies.R.id.tabs)
     TabLayout tabLayout;

    private static Context mContext;

    private final PopularListFragment mPopularListFragment = new PopularListFragment();

    private final TopRatedListFragment mTopRatedListFragment = new TopRatedListFragment();

    private final FavoriteListFragment mFavoriteListFragment = new FavoriteListFragment();

    private SearchResultFragment mSearchResultFragment = new SearchResultFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        mContext = getApplicationContext();
        setContentView(com.jcMobile.android.popularmovies.R.layout.nav_drawer);

        Toolbar toolbar = (Toolbar) findViewById(com.jcMobile.android.popularmovies.R.id.toolbar);

        setSupportActionBar(toolbar);

        getSupportActionBar().setElevation(0);

        ButterKnife.bind(this);

        View viewActionBar = getLayoutInflater().inflate(com.jcMobile.android.popularmovies.R.layout.action_bar, null);

        final ActionBar abar = getSupportActionBar();

        ActionBar.LayoutParams params = new ActionBar.LayoutParams(//Center the textview in the ActionBar !
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                Gravity.CENTER);
        abar.setCustomView(viewActionBar, params);
        abar.setDisplayShowCustomEnabled(true);
        abar.setDisplayShowTitleEnabled(false);
        abar.setDisplayHomeAsUpEnabled(false);
        abar.setHomeButtonEnabled(false);
        TextView textviewTitle = (TextView) viewActionBar.findViewById(com.jcMobile.android.popularmovies.R.id.toolBar_title);

        textviewTitle.setText(getString(com.jcMobile.android.popularmovies.R.string.app_name));

        DrawerLayout drawer = (DrawerLayout) findViewById(com.jcMobile.android.popularmovies.R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, com.jcMobile.android.popularmovies.R.string.navigation_drawer_open, com.jcMobile.android.popularmovies.R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(com.jcMobile.android.popularmovies.R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mSearchFragmentContainer = (FrameLayout) findViewById(com.jcMobile.android.popularmovies.R.id.search_page_container);
        mViewPager = (ViewPager) findViewById(com.jcMobile.android.popularmovies.R.id.container);

        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == com.jcMobile.android.popularmovies.R.id.nav_about) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
        } else if (id == com.jcMobile.android.popularmovies.R.id.TMDb_link) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.themoviedb.org/"));
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(com.jcMobile.android.popularmovies.R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }


    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return mPopularListFragment.newInstance();

                case 1:
                    return mTopRatedListFragment.newInstance();

                case 2:
                    return mFavoriteListFragment.newInstance();
            }

            return null;
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();

        inflater.inflate(com.jcMobile.android.popularmovies.R.menu.movie, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView =
                (SearchView) menu.findItem(com.jcMobile.android.popularmovies.R.id.search_button).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                mSearchResultFragment = SearchResultFragment.newInstance(query);

                searchView.clearFocus();

                getSupportFragmentManager().beginTransaction().
                        replace(com.jcMobile.android.popularmovies.R.id.search_page_container, mSearchResultFragment).
                        commit();

                mSearchFragmentContainer.setVisibility(View.VISIBLE);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });

        MenuItem searchButton = menu.findItem(com.jcMobile.android.popularmovies.R.id.search_button);

        MenuItemCompat.setOnActionExpandListener(searchButton, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {

                tabLayout.setVisibility(View.GONE);

                mViewPager.setVisibility(View.INVISIBLE);

                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {

                tabLayout.setVisibility(View.VISIBLE);

                mViewPager.setVisibility(View.VISIBLE);

                mSearchFragmentContainer.setVisibility(View.INVISIBLE);

                return true;
            }
        });

        final int textViewID = searchView.getContext().
                getResources().
                getIdentifier("android:id/search_src_text", null, null);
        final AutoCompleteTextView searchTextView = (AutoCompleteTextView) searchView.findViewById(textViewID);
        try {
            Field mCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
            mCursorDrawableRes.setAccessible(true);
            mCursorDrawableRes.set(searchTextView, 0);
            //This sets the cursor resource ID to 0 or @null which will make it visible on white background
        } catch (Exception e) {
        }

        return true;
    }

    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(com.jcMobile.android.popularmovies.R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            super.onBackPressed();
        }
    }

    public static Context getmContext() {
        return mContext;
    }


}
