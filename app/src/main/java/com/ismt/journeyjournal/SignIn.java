package com.ismt.journeyjournal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.room.Room;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

/*import com.facebook.share.Share;*/
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ismt.journeyjournal.userlogin.UserLogin;
import com.ismt.journeyjournal.userlogin.UserLoginDao;
import com.ismt.journeyjournal.userlogin.UserLoginDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignIn extends AppCompatActivity {

    private Button btnSignin;
    private TextView btnSignup, or;
    private AppCompatEditText fullName;
    private EditText email, password, phone, rePassword;
    private Button btnCancel, btSignin, btSignup, btCancel;
    private SignInButton signInButton;
    ProgressDialog progressDialog;
    UserLoginDao db;
    UserLoginDao userLoginDao;
    UserLoginDatabase database;
    ImageButton hidePwdBtn1, hidePwdBtn2, hidePwdBtn3;
    SharedPreferences sharedPreferences;

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        sharedPreferences = getSharedPreferences("journal_prefs", Context.MODE_PRIVATE);
        btnSignin = findViewById(R.id.btnSignin);
        btnSignup = findViewById(R.id.btnSignup);
        or = findViewById(R.id.or);
        fullName = findViewById(R.id.fullname);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        phone = findViewById(R.id.phone);
        rePassword = findViewById(R.id.repassword);
        btCancel = findViewById(R.id.btcancel);
        signInButton = findViewById(R.id.sign_in_button);


        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this,gso);

        database = Room.databaseBuilder(this, UserLoginDatabase.class, "UserLogin")
                .allowMainThreadQueries()
                .build();
        userLoginDao = database.getUserLoginDao();
        db = database.getUserLoginDao();

        userLoginDao = Room.databaseBuilder(this, UserLoginDatabase.class, "UserLogin")
                .allowMainThreadQueries()
                .build().getUserLoginDao();


        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            navigateToDashboard();
        }

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }

        });

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog1();
            }
        });

    }

    void signIn(){
        Intent signInIntent = gsc.getSignInIntent();
        startActivityForResult(signInIntent, 1000);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == 1000) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try{
                task.getResult(ApiException.class);
                navigateToDashboard();
            }catch(ApiException e){
                Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
            }

        }
    }

    void navigateToDashboard(){
        finish();
        Intent intent = new Intent(SignIn.this, Dashboard.class);
        startActivity(intent);
    }

    private void showDialog1() {

        AlertDialog.Builder alert;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            alert = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            alert = new AlertDialog.Builder(this);
        }
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.signup_popup, null);

        fullName = view.findViewById(R.id.fullname);
        email = view.findViewById(R.id.email);
        phone = view.findViewById(R.id.phone);
        password = view.findViewById(R.id.password);
        rePassword = view.findViewById(R.id.repassword);
        btSignup = view.findViewById(R.id.btsignup);
        btCancel = view.findViewById(R.id.btcancel);
        progressDialog = new ProgressDialog(this);
        hidePwdBtn1 =view.findViewById(R.id.hidePassword1);
        hidePwdBtn2 =view.findViewById(R.id.hidePassword2);

        hidePwdBtn1.setOnClickListener( v -> {
            System.out.println("clicked");
            if(password.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                //if password is visible then hide it
                password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                //change icon
                hidePwdBtn1.setImageResource(R.drawable.ic_baseline_visibility_off_24);
            }else{
                password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                hidePwdBtn1.setImageResource(R.drawable.ic_baseline_remove_red_eye_24);
            }
        });

        hidePwdBtn2.setOnClickListener( v -> {
            System.out.println("clicked");
            if(rePassword.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                //if password is visible then hide it
                rePassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                //change icon
                hidePwdBtn2.setImageResource(R.drawable.ic_baseline_visibility_off_24);
            }else{
                rePassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                hidePwdBtn2.setImageResource(R.drawable.ic_baseline_remove_red_eye_24);
            }
        });


        alert.setView(view);
        alert.setCancelable(false);

        btSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String e = email.getText().toString();
                String p = password.getText().toString();

                if (!validateFullName() || !validateEmail() || !validatePhone() || !validatePassword() || !validateRePwd()) {
                    return;
                }
                else /*if (password.equals(rePassword))*/ {
                    UserLogin userLogin = new UserLogin(e, p);
                    userLoginDao.insert(userLogin);
                    progressDialog.setMessage("Please wait while signing up");
                    progressDialog.setTitle("Sign Up");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                        }
                    },7000);
                    Toast.makeText(SignIn.this, "Sign Up Successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignIn.this, SignIn.class);
                    startActivity(intent);
                }
            }

        });
        AlertDialog dialog = alert.create();
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.show();
        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

    }

    private boolean validateFullName() {
        String fName = fullName.getText().toString();

        if (fName.isEmpty()) {
            fullName.setError("Field cannot be empty");
            /*fullName.setHintTextColor(ContextCompat.getColor(this,R.color.red));*/
            return false;
        } /*else if (fName.matches("(\\b[A-Z]{1}[a-z]+)( )([A-Z]{1}[a-z]+\\b)")) {
            fullName.setError("Please enter your FullName");
            return false;
        }*/ else {
            fullName.setError(null);
            return true;
        }
    }

    private boolean validateEmail() {
        String e = email.getText().toString();
        if (e.isEmpty()) {
            email.setError("Field cannot be empty");
            return false;
        } else if (!e.matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")) {
            email.setError("Please enter valid Email");
            return false;
        } else {
            email.setError(null);
            return true;
        }
    }

    private boolean validatePhone() {
        String ph = phone.getText().toString();
        if (ph.isEmpty()) {
            phone.setError("Field cannot be empty");
            return false;
        } else if (!ph.matches("^[789]{1}\\d{9}$")) {
            phone.setError("Please enter valid PhoneNumber");
            return false;
        } else {
            phone.setError(null);
            return true;
        }
    }

    private boolean validatePassword() {
        String p = password.getText().toString();
        if (p.isEmpty()) {
            password.setError("Field cannot be empty");
            return false;
        } else if (p.length() < 6) {
            password.setError("Password must be minimum of 6 characters.");
            return false;
        } else {
            password.setError(null);
            return true;
        }
    }

    private boolean validateRePwd() {
        String re = rePassword.getText().toString();
        String p = password.getText().toString();
        if (re.isEmpty()) {
            rePassword.setError("Field cannot be empty");
            return false;
        } else if (!re.equals(p)) {
            rePassword.setError("Password does not match.");
            return false;
        } else {
            rePassword.setError(null);
            return true;
        }
    }

    private void showDialog() {

        AlertDialog.Builder alert;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            alert = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            alert = new AlertDialog.Builder(this);
        }
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.signin_popup, null);

        email = view.findViewById(R.id.email);
        password = view.findViewById(R.id.password);
        btSignin = view.findViewById(R.id.btsignin);
        btnCancel = view.findViewById(R.id.btncancel);
        hidePwdBtn3 =view.findViewById(R.id.hidePassword3);

        hidePwdBtn3.setOnClickListener( v -> {
            System.out.println("clicked");
            if(password.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                //if password is visible then hide it
                password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                //change icon
                hidePwdBtn3.setImageResource(R.drawable.ic_baseline_visibility_off_24);
            }else{
                password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                hidePwdBtn3.setImageResource(R.drawable.ic_baseline_remove_red_eye_24);
            }
        });

        alert.setView(view);
        alert.setCancelable(false);
       progressDialog = new ProgressDialog(this);


        btSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                validation();
            }
        });
        AlertDialog dialog = alert.create();
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.show();
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

    }

    private void validation() {
        String e = email.getText().toString().trim();
        String p = password.getText().toString().trim();

        UserLogin userLogin = db.getUserLogin(e, p);

        if (e.isEmpty()) {
            email.setError("All fields are mandatory.");
        } else if (p.isEmpty()) {
            password.requestFocus();
            password.setError("All fields are mandatory.");
        }
        else if (userLogin != null) {
            progressDialog.setMessage("Please wait while signing in");
            progressDialog.setTitle("Sign In");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            finish();
            Intent intent = new Intent(SignIn.this, Dashboard.class);
            intent.putExtra("UserLogin", userLogin);
            startActivity(intent);
            Toast.makeText(SignIn.this, "Sign In Successful", Toast.LENGTH_SHORT).show();


            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("user_login", true).apply();

        } else {
            progressDialog.dismiss();
           Toast.makeText(SignIn.this, "Incorrect email or password", Toast.LENGTH_SHORT).show();
        }

    }
}






