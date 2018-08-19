package com.example.acrylon3.ascara;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private Button location, start, end, search;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.search_notification:
                    mTextMessage.setText(R.string.title_left);
                    return true;
                case R.id.rents_navigation:
                    mTextMessage.setText(R.string.title_middle);
                    return true;
                case R.id.account_notification:
                    mTextMessage.setText(R.string.title_right);
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        location = (Button) findViewById(R.id.location_btn);
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                FragmentManager myfragmentManager = getFragmentManager ();
//                FragmentTransaction myfragmentTransaction = myfragmentManager.beginTransaction ();
//
//                Location_Fragment myfragment = new Location_Fragment();

//                myfragmentTransaction.add(R.id.myFrame, myfragment);
//                myfragmentTransaction.commit();
            }
        });
        start = (Button) findViewById(R.id.start_location_btn);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Location_Fragment.class);
                startActivity(intent);
            }
        });
        end = (Button) findViewById(R.id.end_location_btn);
        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Location_Fragment.class);
                startActivity(intent);
            }
        });
        search = (Button) findViewById(R.id.search_btn);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Location_Fragment.class);
                startActivity(intent);
            }
        });
    }


}
