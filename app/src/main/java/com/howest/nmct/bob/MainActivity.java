package com.howest.nmct.bob;

import android.content.res.Configuration;
import android.media.Image;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.howest.nmct.bob.collections.Rides;
import com.howest.nmct.bob.fragments.EventsFragment;
import com.howest.nmct.bob.fragments.FeedFragment;
import com.howest.nmct.bob.fragments.ProfileFragment;
import com.howest.nmct.bob.fragments.RideDetailsFragment;
import com.howest.nmct.bob.fragments.RidesFragment;
import com.howest.nmct.bob.models.Ride;
import com.howest.nmct.bob.models.User;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String SELECTED_MENU_ITEM_ID = "selectedMenuItemId";

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.drawer_layout) DrawerLayout drawerLayout;
    @Bind(R.id.nav_view) NavigationView navigationView;

    private ActionBarDrawerToggle mDrawerToggle;
    private int mSelectedMenuItemId;

    public User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bundle b = getIntent().getExtras();
        mUser = b.getParcelable(Constants.USER_PROFILE);

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        initDrawer();
        initData();
        
        if (savedInstanceState == null) navigateToFeed();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putInt(SELECTED_MENU_ITEM_ID, mSelectedMenuItemId);
    }
    
    /**
     * Populates the Ride ArrayList
     */
    private void initData() {
        Rides.fetchData();
    }


    /**
     * Sets up the Drawer Layout and a toggle to open the navigation menu.
     */
    private void initDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        // Set profile
        Picasso picasso = Picasso.with(this);
        Log.d("MainActivity", mUser.getPicture());
        Log.d("MainActivity", mUser.getCover());
        if (!mUser.getPicture().isEmpty()) {
            picasso.load(mUser.getPicture())
                    .fit()
                    .centerCrop()
                    .into((ImageView) findViewById(R.id.nav_header_profile));
        }

        if (!mUser.getCover().isEmpty()) {
            picasso.load(mUser.getCover())
                    .fit()
                    .centerCrop()
                    .into((ImageView) findViewById(R.id.nav_header_background));
        }


        TextView tvName = (TextView) findViewById(R.id.nav_header_name);
        tvName.setText(mUser.getName());
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) drawerLayout.closeDrawer(GravityCompat.START);
        else super.onBackPressed();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        drawerLayout.closeDrawer(GravityCompat.START);

        mSelectedMenuItemId = item.getItemId();
        updateNavigation(mSelectedMenuItemId);

        return true;
    }

    private void updateNavigation(int itemId) {
        switch (itemId) {
            case R.id.nav_feed:
                Log.d("NavigationDrawer", "Click Feed");
                navigateToFeed();
                break;
            case R.id.nav_events:
                Log.d("NavigationDrawer", "Click Events");
                navigateToEvents();
                break;
            case R.id.nav_rides:
                Log.d("NavigationDrawer", "Click Rides");
                navigateToRides();
                break;
            case R.id.nav_profile:
                Log.d("NavigationDrawer", "Click Profile");
                navigateToProfile();
                break;
        }
    }

    public void navigateToProfile() {
        navigateToFragment(new ProfileFragment());
        toolbar.setTitle("Profile");
    }

    public void navigateToRides() {
        navigateToFragment(new RidesFragment());
        toolbar.setTitle("Rides");
    }

    public void navigateToEvents() {
        navigateToFragment(new EventsFragment());
        toolbar.setTitle("Events");
    }

    public void navigateToFeed() {
        navigateToFragment(new FeedFragment());
        toolbar.setTitle("Feed");
    }

    /**
     * Navigates to a fragment and places it in the container.
     * @param fragment A created fragment that is navigated to
     */
    public void navigateToFragment(Fragment fragment) {
        getSupportFragmentManager().popBackStack();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }

    public void navigateToFragment(Fragment fragment, Boolean addToManager) {
        getSupportFragmentManager().popBackStack();
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.fragment_enter, R.anim.fragment_exit, R.anim.pop_enter, R.anim.pop_exit)
                .add(R.id.container, fragment)
                .addToBackStack(fragment.getClass().toString())
                .commit();
    }

    public void navigatetoRideDetails(Ride ride) {
        navigateToFragment(RideDetailsFragment.newInstance(ride), true);
        toolbar.setTitle(ride.getTitle());
    }

    public void navigatetoRideDetails(int frameLayout, Ride ride) {
        Fragment fragment = RideDetailsFragment.newInstance(ride);
        getSupportFragmentManager().popBackStack();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(frameLayout, fragment)
                .addToBackStack(fragment.getClass().toString())
                .commit();

    }
}
