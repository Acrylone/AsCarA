package com.example.acrylon3.ascara;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.FrameLayout;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView navigation;
    private FrameLayout content;

    private SearchFragment searchFragment;
    private RentsFragment rentsFragment;
    private AccountFragment accountFragment;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navigation = findViewById(R.id.navigation);
        content = findViewById(R.id.content);

        searchFragment = new SearchFragment();
        rentsFragment = new RentsFragment();
        accountFragment = new AccountFragment();

        setFragment(searchFragment);


        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.abs_layout);
        getSupportActionBar().setBackgroundDrawable(getDrawable(R.color.white));
//        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.logo));


        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.search_notification:
                        setFragment(searchFragment);
                        return true;
                    case R.id.rents_navigation:
                        setFragment(rentsFragment);
                        return true;
                    case R.id.account_notification:
                        setFragment(accountFragment);
                        return true;

                    default:
                        return false;
                }
            }
        });
    }
    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content, fragment);
        fragmentTransaction.commit();
    }
}
