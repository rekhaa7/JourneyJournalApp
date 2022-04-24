package com.ismt.journeyjournal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CalendarView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class Calendar extends AppCompatActivity {

    private static final int REQUEST_CODE_ADD_NOTE = 1 ;
    BottomNavigationView calendarNavigation;
    FloatingActionButton message;
    private CalendarView mCalendarView;


    private static  final String TAG = "CalendarActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        message=findViewById(R.id.journalText);
        mCalendarView = findViewById(R.id.calendarView);
        calendarNavigation = findViewById(R.id.calendarNavigation);
        calendarNavigation.setSelectedItemId(R.id.calendar);

        calendarNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.calendar:
                        return true;
                    case R.id.ideas:
                        startActivity(new Intent(getApplicationContext(), Ideas.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(),Dashboard.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;

            }
        });

        mCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
                String date = (i1  + 1) + "/" +i2 + "/" +i;
                Log.d(TAG, "onSelectedDateChange:  dd/MMMM/yyyy: " + date);

                Intent intent = new Intent(Calendar.this,Journal.class);
                intent.putExtra("date",date);
                startActivity(intent);
            }
        });

        message.setOnClickListener(new View.OnClickListener() {
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