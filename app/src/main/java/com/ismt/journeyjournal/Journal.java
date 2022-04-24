
package com.ismt.journeyjournal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ismt.journeyjournal.journal.JournalDatabase;
import com.ismt.journeyjournal.journal.JournalEntities;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Journal extends AppCompatActivity {

    private static final int REQUEST_CAMERA = 1;
    private static final int REQUEST_CODE_STORAGE_PERMISSION = 2;
    private static final int REQUEST_GALLERY = 3;
    private static  final String TAG = "Journal";

    private AppCompatImageView image, arrow,delete;
    private EditText title, subTitle;
    private ImageView imageJournal, addLocation;
    private String selectedImagePath;
    private FloatingActionButton saveJournal;
    private RecyclerView journalRecycleView;
    private TextView dateTime, txtLocation;
    private JournalEntities alreadyAvailable;
    private static final int PERMISSION_CODE = 1001;
    DatePickerDialog.OnDateSetListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal);
        arrow = findViewById(R.id.bArrow);
        image = findViewById(R.id.clickImage);
        title = findViewById(R.id.title);
        subTitle = findViewById(R.id.paragraph);
        saveJournal = findViewById(R.id.saveJournal);
        dateTime = findViewById(R.id.dateTime);
        imageJournal = findViewById(R.id.imageJournal);
        selectedImagePath = "";

        dateTime.setText(
                new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a", Locale.getDefault()).format(new Date())
        );

        /*Intent intent = getIntent();
        String date = intent.getStringExtra("date");
        dateTime.setText(date);*/

        dateTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Journal.this, Calendar.class));
            }
        });

        arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Journal.this, Dashboard.class);
                startActivity(intent);
            }
        });

       saveJournal.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               saveJournal();
           }
       });


        if (getIntent().getBooleanExtra("isViewOrUpdate", false)){
            alreadyAvailable = (JournalEntities) getIntent().getSerializableExtra("journal");
            setViewOrUpdate();
        }

       /* findViewById(R.id.imageRemove).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageJournal.setImageBitmap(null);
                imageJournal.setVisibility(view.GONE);
                findViewById(R.id.imageRemove).setVisibility(view.GONE);
                selectedImagePath = "";
            }
        });*/

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,100);*/
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_DENIED) {
                        //permission not granted, request it.
                        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                        //show popup for runtime permission
                        requestPermissions(permissions, PERMISSION_CODE);
                    } else {
                        //permission already granted
                        chooseImageFromGallery();
                    }
                }

            }
        });


    }

    private void setViewOrUpdate() {
        title.setText(alreadyAvailable.getTitle());
        subTitle.setText(alreadyAvailable.getSubTitle());
        dateTime.setText(alreadyAvailable.getDateTime());

        if (alreadyAvailable.getImageJournal() != null && !alreadyAvailable.getImageJournal().trim().isEmpty()){
            imageJournal.setImageBitmap(BitmapFactory.decodeFile(alreadyAvailable.getImageJournal()));
            imageJournal.setVisibility(View.VISIBLE);
            findViewById(R.id.imageJournal).setVisibility(View.VISIBLE);
            findViewById(R.id.imageRemove).setVisibility(View.VISIBLE);
            selectedImagePath = alreadyAvailable.getImageJournal();
        }
    }
    private void chooseImageFromGallery() {
        final CharSequence[] items = {"Camera", "Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(Journal.this);
        builder.setTitle("Add image");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (items[i].equals("Camera")){
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent,REQUEST_CAMERA);
                }
                else if (items[i].equals("Gallery")){
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, REQUEST_GALLERY);
                }
                else if (items[i].equals("Cancel")){
                    dialogInterface.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
        } else if (requestCode == 3) {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, 1);
        }
        else {

            Intent intent = new Intent(Intent.ACTION_PICK);
            startActivityForResult(intent, 3);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //capture image and load to recycler view
        if(requestCode == REQUEST_CAMERA && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            imageJournal.setImageBitmap(photo);
            imageJournal.setVisibility(View.VISIBLE);
           //findViewById(R.id.imageRemove).setVisibility(View.VISIBLE);
            Uri tempUri = getImageUri(getApplicationContext(), photo);
            selectedImagePath = getPathFromUri(tempUri);
        }

        else if (requestCode == REQUEST_GALLERY && resultCode == RESULT_OK){
            if (data != null){
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null){
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        imageJournal.setImageBitmap(bitmap);
                        imageJournal.setVisibility(View.VISIBLE);
                        //findViewById(R.id.imageRemove).setVisibility(View.VISIBLE);
                        selectedImagePath = getPathFromUri(selectedImageUri);
                    }
                    catch (Exception exception){
                        Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

            }
        }
    }

    private Uri getImageUri(Context inContext, Bitmap photo) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), photo, "Title", null);
        return Uri.parse(path);
    }

    private String getPathFromUri(Uri contentUri){
        String filePath;
        Cursor cursor = getContentResolver()
                .query(contentUri, null, null, null, null);
        if (cursor == null){
            filePath = contentUri.getPath();
        }
        else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex("_data");
            filePath = cursor.getString(index);
            cursor.close();
        }
        return filePath;
    }


    private void saveJournal() {
        if (title.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Enter title", Toast.LENGTH_SHORT).show();
            return;
        } else if (subTitle.getText().toString().trim().isEmpty() && subTitle.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Enter title and description", Toast.LENGTH_SHORT).show();
            return;
        }

        final JournalEntities journalEntities = new JournalEntities();
        journalEntities.setTitle(title.getText().toString());
        journalEntities.setSubTitle(subTitle.getText().toString());
        journalEntities.setDateTime(dateTime.getText().toString());
        journalEntities.setImageJournal(selectedImagePath);

        if (alreadyAvailable != null){
            journalEntities.setId(alreadyAvailable.getId());
        }

        class saveJournal extends AsyncTask<Void, Void, Void> {

            @Override
            protected  Void doInBackground(Void... voids){
                JournalDatabase.getJournalDatabase(getApplicationContext()).journalDao().insertJournal(journalEntities);
                return null;
            }
            @Override
            protected void onPostExecute(Void aVoid){
                super.onPostExecute(aVoid);
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }

        }
        new saveJournal().execute();
        Toast.makeText(this,"Journal saved.",Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this,Dashboard.class));
    }


}