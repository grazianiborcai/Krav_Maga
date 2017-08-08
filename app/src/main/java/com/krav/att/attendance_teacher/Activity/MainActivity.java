package com.krav.att.attendance_teacher.Activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.krav.att.attendance_teacher.FragmentLayout.MainFragment;
import com.krav.att.attendance_teacher.Parcelable.People;
import com.krav.att.attendance_teacher.R;
import com.krav.att.attendance_teacher.RecycleViewAdapter.ClassAdapter;
import com.krav.att.attendance_teacher.Requests.AsyncTask.HttpRequestTask;
import com.krav.att.attendance_teacher.Requests.FragmentRequest.ClassRequestFragment;
import com.krav.att.attendance_teacher.Requests.FragmentRequest.LoginRequestFragment;
import com.krav.att.attendance_teacher.Requests.Interface.OnTaskFinished;
import com.krav.att.attendance_teacher.Shared.UserDataShared;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnTaskFinished {

    private LoginRequestFragment mLoginRequestFragment;
    private ClassRequestFragment mClassRequestFragment;

    private UserDataShared user;
    private NavigationView navigationView;
    private boolean updateUser = false;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private MainFragment firstFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.GONE);
       /* fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        /*ArrayList<People> pList = getIntent().getParcelableArrayListExtra(HttpRequestTask.RESULTS);*/
        user = UserDataShared.carregar(this);

        setHeaderView(navigationView);

        FragmentManager fm = getSupportFragmentManager();
        mLoginRequestFragment = (LoginRequestFragment) fm.findFragmentByTag(LoginRequestFragment.TAG_TASK_SELECT_USER);
        mClassRequestFragment = (ClassRequestFragment) fm.findFragmentByTag(ClassRequestFragment.TAG_TASK_CLASS);

        // If the Fragment is non-null, then it is currently being
        // retained across a configuration change.
        if (mLoginRequestFragment == null) {
            mLoginRequestFragment = LoginRequestFragment.newInstance(this);
            fm.beginTransaction().add(mLoginRequestFragment, LoginRequestFragment.TAG_TASK_SELECT_USER).commit();
        }

        // If the Fragment is non-null, then it is currently being
        // retained across a configuration change.
        if (mClassRequestFragment == null) {
            mClassRequestFragment = ClassRequestFragment.newInstance(this);
            fm.beginTransaction().add(mClassRequestFragment, ClassRequestFragment.TAG_TASK_CLASS).commit();
        }

        updateUser = true;

/*        ArrayList<People> pList = getIntent().getParcelableArrayListExtra(HttpRequestTask.RESULTS);

        mRecyclerView = (RecyclerView) findViewById(R.id.classList);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new ClassAdapter(pList);
        mRecyclerView.setAdapter(mAdapter);*/

        firstFragment = (MainFragment) fm.findFragmentById(R.id.container_fragment);

        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.fragment_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            // Create a new Fragment to be placed in the activity layout
            firstFragment = MainFragment.newInstance();

            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            firstFragment.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, firstFragment).commit();

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (updateUser && !getIntent().hasExtra("from_login_activity")) {
            mLoginRequestFragment.execute(user.getoAuth());
            updateUser = !updateUser;
        }
        /*String params = "peopleID="+user.getPeopleID();
        mClassRequestFragment.execute(user.getoAuth(), params);*/
    }

    private void setHeaderView(NavigationView navView) {
        View v = navView.getHeaderView(0);
        TextView name = (TextView) v.findViewById(R.id.client_name);
        TextView email = (TextView) v.findViewById(R.id.client_email);
        name.setText(user.getName());
        email.setText(user.getEmail());
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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

        /*//noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void postExecute(Intent intent) {
        if (People.class.toString().equals(intent.getStringExtra("request_class"))) {
            try {
                ArrayList<People> pList = intent.getParcelableArrayListExtra(HttpRequestTask.RESULTS);
                People userData = pList.get(0);
                user.setPeopleID(userData.getPeopleID());
                user.setCountryID(userData.getCountryID());
                user.setName(userData.getName());
                user.setBirthDate(userData.getBirthDate());
                user.setEnrollmentNumber(userData.getEnrollmentNumber());
                user.setGradeDate(userData.getGradeDate());
                user.setEmail(userData.getEmail());
                user.setCelphone(userData.getCelphone());
                user.setPhone(userData.getPhone());
                user.setAddress1(userData.getAddress1());
                user.setAddress2(userData.getAddress2());
                user.setPostalCode(userData.getPostalCode());
                user.setBloodType(userData.getBloodType());
                user.setAllergy(userData.getAllergy());
                user.setAllergyDesc(userData.getAllergyDesc());
                user.setNextGradeExam(userData.getNextGradeExam());
                user.setWhereOther(userData.getWhereOther());
                user.setLookingOther(userData.getLookingOther());
                user.setPassword(userData.getPassword());
                user.setEnrTypeID(userData.getEnrTypeID());
                user.setoAuth(userData.getoAuth());
                user.setoAuthDate(userData.getoAuthDate());
                user.setUserAgent(userData.getUserAgent());
                user.setRegionID(userData.getRegionID());
                user.setGradeID(userData.getGradeID());
                user.setBirthDateS(userData.getBirthDateS());
                user.setGradeDateS(userData.getGradeDateS());
                user.setNextGradeExamS(userData.getNextGradeExamS());
                user.setGenderID(userData.getGenderID());
                user.setWhereID(userData.getWhereID());
                user.setLookID(userData.getLookID());
                user.save(this);
                setHeaderView(navigationView);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } else if (People.class.toString().equals(intent.getStringExtra("request_class"))){

        }
    }

    @Override
    public void cancelled() {

    }

    @Override
    public void animation(boolean b) {

    }
}
