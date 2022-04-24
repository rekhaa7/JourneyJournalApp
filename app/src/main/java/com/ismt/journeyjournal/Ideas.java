package com.ismt.journeyjournal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class Ideas extends AppCompatActivity {

    private static final int REQUEST_CODE_ADD_NOTE = 1 ;
    BottomNavigationView ideasNavigation;
    FloatingActionButton journalText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ideas);

        journalText = findViewById(R.id.journalText);
        ideasNavigation = findViewById(R.id.ideasNavigation);
        ideasNavigation.setSelectedItemId(R.id.ideas);

        ideasNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.calendar:
                        startActivity(new Intent(getApplicationContext(), Calendar.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.ideas:
                        return true;
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(), Dashboard.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;

            }
        });

        journalText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(
                        new Intent(getApplicationContext(), Journal.class),
                        REQUEST_CODE_ADD_NOTE
                );
            }
        });


    }

}