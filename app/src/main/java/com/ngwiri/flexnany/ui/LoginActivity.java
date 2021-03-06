package com.ngwiri.flexnany.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ngwiri.flexnany.Network;
import com.ngwiri.flexnany.R;
import com.ngwiri.flexnany.Utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static View view;
    private static Animation shakeAnimation;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private ProgressDialog progressDialog;



    boolean doubleBackToExitPressedOnce = false;

    @BindView(R.id.loginLayout) LinearLayout mLoginLayout;
    @BindView(R.id.loginbuton) Button mLoginbuton;
    @BindView(R.id.SignUp_Text) LinearLayout mSignUp_Text;
    @BindView(R.id.loginEmail) EditText mLoginEmail;
    @BindView(R.id.password) EditText mPassword;
    @BindView(R.id.loginAppName) TextView mLoginAppName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();
        createAuthProgressDialog();
        addAuth();



        mLoginbuton.setOnClickListener(this);
        mSignUp_Text.setOnClickListener(this);

        shakeAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);


        Typeface fredokaOneFonts = Typeface.createFromAsset(getAssets(), "fonts/fredoka_one/FredokaOne-Regular.ttf" );
        mLoginAppName.setTypeface(fredokaOneFonts);

        //<--- CHECKING INTERNET CONNECTION START
        if(Network.isInternetAvailable(LoginActivity.this)) //returns true if internet available
        {

        }
        else
        {
            new CustomToast().Show_Toast(getApplicationContext(), view,
                    "No Internet Connection");

        }

        //CHECKING INTERNET CONNECTION END --->


    }

    @Override
    public void onClick(View v) {
        if (v == mLoginbuton) {
//            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//            startActivity(intent);

            checkValidation();


        }

        if (v == mSignUp_Text){
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
        }
    }

    // Check Validation before login
    private void checkValidation () {
        // Get email id and password
        String getEmailId = mLoginEmail.getText().toString();
        String getPassword = mPassword.getText().toString();

        // Check patter for email id
        Pattern p = Pattern.compile(Utils.regEx);

        Matcher m = p.matcher(getEmailId);

        // Check for both field is empty or not

        if (getEmailId.equals("") || getEmailId.length() == 0
                || getPassword.equals("") || getPassword.length() == 0) {
            mLoginLayout.startAnimation(shakeAnimation);
            new CustomToast().Show_Toast(getBaseContext(), view,
                    "Enter both credentials.");

        }
        // Check if email id is valid or not
        else if (!m.find())
            new CustomToast().Show_Toast(getApplicationContext(), view,
                    "Your Email Id is Invalid.");
            // Else do login and do your stuff
        else
            login(mLoginEmail.getText().toString(),mPassword.getText().toString());

    }

    //<--- PROGRESSDIALOG START
    //setCancelable() to "false" so that users cannot close the dialog manually.
    private void createAuthProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading ...");
        progressDialog.setMessage("Authenticating in Progress...");
        progressDialog.setCancelable(false);

    }
    //PROGRESSDIALOG END --->





    public  void login (String email,String password){
        progressDialog.show();
        mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();
                if(task.isSuccessful()){
                    Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                    startActivity(intent);
                }
                else{
                    new CustomToast().Show_Toast(getApplicationContext(), view,
                            "Authentication Failed");
                }
            }
        });
    }

    public void addAuth(){
        authStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
                if(user!=null){
                    Intent intent=new Intent( LoginActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        };
    }



        @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    protected  void onStart(){
        super.onStart();
        FirebaseUser user=mAuth.getCurrentUser();
        if(user!=null){
            mAuth.addAuthStateListener(authStateListener);
        }
    }

    @Override
    protected  void  onStop(){
        super.onStop();
        if(authStateListener!=null){
            mAuth.removeAuthStateListener(authStateListener);
        }
    }
}
