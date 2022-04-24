package com.ismt.journeyjournal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ismt.journeyjournal.journal.JournalAdapter;
import com.ismt.journeyjournal.journal.JournalDatabase;
import com.ismt.journeyjournal.journal.JournalEntities;
import com.ismt.journeyjournal.journal.JournalListeners;

import java.util.ArrayList;
import java.util.List;

public class Dashboard extends AppCompatActivity implements JournalListeners{
    private FloatingActionButton message;
    public static final int REQUEST_CODE_ADD = 1;
    public static final int REQUEST_CODE_UPDATE = 2;
    public static final int REQUEST_SHOW_NOTES = 3;
    private RecyclerView recyclerView;
    private List<JournalEntities> journalEntitiesList;
    private JournalAdapter journalAdapter;
    BottomNavigationView homeNavigation;
    private int journalClicked = -1;
    private ImageView optSignOut;
    private ImageView imageCentre;
    private TextView nothing;
    boolean backPressed = false;

    SharedPreferences sharedPreferences;

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;

    @Override
    public void onBackPressed() {
        if(backPressed){
            finishAffinity();
        }
        else {
            backPressed = true;
            Toast.makeText(this, "Press again to exit",Toast.LENGTH_SHORT).show();
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    backPressed= false;
                }
            },2000);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        message = findViewById(R.id.journalText);
        optSignOut = findViewById(R.id.optSignOut);
        imageCentre = findViewById(R.id.imageCentre);
        nothing = findViewById(R.id.nothing);



        homeNavigation = findViewById(R.id.homeNavigation);
        homeNavigation.setSelectedItemId(R.id.home);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this,gso);

        sharedPreferences = getSharedPreferences("journal_prefs", Context.MODE_PRIVATE);

        /*GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            String personName = acct.getDisplayName();
            String personGivenName = acct.getGivenName();
            String personEmail = acct.getEmail();


            Toast.makeText(this, "Email :" +personEmail, Toast.LENGTH_SHORT).show();
        }*/

     //  showImage();

        optSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
                popupMenu.getMenuInflater().inflate(R.menu.signout, popupMenu.getMenu());
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId())
                        {
                            case R.id.signOut:
                                showSignOutPopup();
                        }
                        return false;
                    }
                });
            }
        });


       homeNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
           @Override
           public boolean onNavigationItemSelected(@NonNull MenuItem item) {
               switch(item.getItemId()){
                   case R.id.calendar:
                       startActivity(new Intent(getApplicationContext(),Calendar.class));
                       overridePendingTransition(0,0);
                       return true;
                   case R.id.ideas:
                       startActivity(new Intent(getApplicationContext(), Ideas.class));
                       overridePendingTransition(0,0);
                       return true;
                   case R.id.home:
                       return true;
               }
               return false;

           }
       });

        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(
                        new Intent(getApplicationContext(), Journal.class),
                        REQUEST_CODE_ADD
                );
            }
        });


        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        );
        journalEntitiesList = new ArrayList<>();
        journalAdapter = new JournalAdapter(journalEntitiesList, this,this);
        recyclerView.setAdapter(journalAdapter);
        getjournals(REQUEST_SHOW_NOTES, false);

        EditText search = findViewById(R.id.search);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                journalAdapter.cancelTimer();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(journalEntitiesList.size() != 0){
                    journalAdapter.searchJournals(editable.toString());
                }
            }
        });
    }

    void showSignOutPopup(){
        Dialog dialog = new Dialog(this);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations
                = android.R.style.Animation_Dialog;
        dialog.setContentView(R.layout.popup_signout);

        TextView textCancel = dialog.findViewById(R.id.textCancel);
        TextView textSignOut = dialog.findViewById(R.id.textSignOut);

        textCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.setCanceledOnTouchOutside(false);

        textSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
                Intent intent = new Intent(Dashboard.this,SignIn.class);
                startActivity(intent);
                sharedPreferences.edit().remove("user_login").commit();
                finish();
                Toast.makeText(view.getContext(), "Sign Out successfully", Toast.LENGTH_SHORT).show();

            }
        });
        dialog.show();
    }

  /*  private void showImage(){

        final RelativeLayout layout = findViewById(R.id.dashboard);

        if (recyclerView == null){
            layout.findViewById(R.id.imageCentre).setVisibility(View.VISIBLE);
            layout.findViewById(R.id.nothing).setVisibility(View.VISIBLE);
        }
        else {
            layout.findViewById(R.id.imageCentre).setVisibility(View.GONE);
            layout.findViewById(R.id.nothing).setVisibility(View.GONE);
        }
    }*/

    void signOut(){
        gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                finish();
                Intent intent = new Intent(Dashboard.this, SignIn.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onJournalClicked(JournalEntities journalEntities, int position) {
        journalClicked = position;
        Intent intent = new Intent(getApplicationContext(), Journal.class);
        intent.putExtra("isViewOrUpdate", true);
        intent.putExtra("journal", journalEntities);
        startActivityForResult(intent, REQUEST_CODE_UPDATE);
    }

    @Override
    public void onDelete(JournalEntities entities) {
        // call delete method of dao
        JournalDatabase.getJournalDatabase(getApplicationContext()).journalDao().deleteJournal(entities);
        return;

    }


    private void getjournals(final int requestCode, final boolean isJournalDeleted)
    {
        class GetJournals extends AsyncTask<Void, Void, List<JournalEntities>> {
            @Override
            protected List<JournalEntities> doInBackground(Void... voids) {
                return JournalDatabase
                        .getJournalDatabase(getApplicationContext())
                        .journalDao().getAllJournals();
            }

            @Override
            protected void onPostExecute(List<JournalEntities> entities) {
                super.onPostExecute(entities);
                final RelativeLayout layout = findViewById(R.id.dashboard);

                if (requestCode == REQUEST_SHOW_NOTES) {
                    journalEntitiesList.addAll(entities);
                    journalAdapter.notifyDataSetChanged();
                }
                else if (requestCode == REQUEST_CODE_ADD) {
                    journalEntitiesList.add(0, entities.get(0));
                    journalAdapter.notifyItemInserted(0);
                    recyclerView.smoothScrollToPosition(0);
                }
                else if (requestCode == REQUEST_CODE_UPDATE) {
                    journalEntitiesList.remove(journalClicked);
                    if (isJournalDeleted) {
                        journalAdapter.notifyItemRemoved(journalClicked);

                    } else {
                        journalEntitiesList.add(journalClicked, entities.get(journalClicked));
                        journalAdapter.notifyItemChanged(journalClicked);
                    }
                }
                else if(recyclerView == null){
                    layout.findViewById(R.id.imageCentre).setVisibility(View.VISIBLE);
                    layout.findViewById(R.id.nothing).setVisibility(View.VISIBLE);
                }

                else if(recyclerView != null){
                    imageCentre.setVisibility(View.GONE);
                    nothing.setVisibility((View.GONE));
                }
              /*  imageCentre.setVisibility(View.GONE);
                nothing.setVisibility((View.GONE));*/
            }
        }
        new GetJournals().execute();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD && resultCode == RESULT_OK){
            getjournals(REQUEST_CODE_ADD, false);
        }
        else if (requestCode == REQUEST_CODE_UPDATE && resultCode == RESULT_OK){
            if (data != null){
                getjournals(REQUEST_CODE_UPDATE, data.getBooleanExtra("isJournalDeleted", false));
            }
        }
    }

}