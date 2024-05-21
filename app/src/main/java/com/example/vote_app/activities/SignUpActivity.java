package com.example.vote_app.activities;

import static android.os.Build.VERSION.SDK_INT;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PatternMatcher;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import com.example.vote_app.R;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignUpActivity extends AppCompatActivity {
    private CircleImageView userProfile;
    private EditText userName,userPassword,userEmail,userNationalID;
    private Button signUpBtn;
    private FirebaseAuth mAuth;

    public static final String PREFERENCES ="prefkeys";
    public static final String Name ="namekey";
    public static final String Email ="emailkey";
    public static final String Password ="passwordkey";
    public static final String NationalID ="nationalidkey";
    public static final String Image="imagekey";
    SharedPreferences sharedPreferences;

    String name,password,email,nationalID,image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        sharedPreferences =getApplicationContext().getSharedPreferences(PREFERENCES,MODE_PRIVATE);

        findViewById(R.id.have_acc).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        userProfile =findViewById(R.id.profile_image);
        userName =findViewById(R.id.user_name);
        userPassword =findViewById(R.id.user_password);
        userEmail =findViewById(R.id.user_email);
        userNationalID =findViewById(R.id.user_national_id);
        signUpBtn =findViewById(R.id.signup_btn);

        mAuth=FirebaseAuth.getInstance();


        userProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){

                    if (ContextCompat.checkSelfPermission(SignUpActivity.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(SignUpActivity.this,
                                new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},1);

                    }else{
                        cropImage();
                    }

                }else {
                    cropImage();
                }
            }
        });
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 name=userName.getText().toString().trim();
                 password=userPassword.getText().toString().trim();
                 email=userEmail.getText().toString().trim();
                 nationalID=userNationalID.getText().toString().trim();


                if (!TextUtils.isEmpty(name)&&!TextUtils.isEmpty(password)&&
                        !TextUtils.isEmpty(email)&& Patterns.EMAIL_ADDRESS.matcher(email).matches() &&!TextUtils.isEmpty(nationalID)){

                        createUser(email,password);
                }else {
                    Toast.makeText(SignUpActivity.this, "Please enter your credentials", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void createUser(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()){

                    Toast.makeText(SignUpActivity.this, "User created", Toast.LENGTH_SHORT).show();
                    
                    veifyEmail();
                    
                }else {
                    Toast.makeText(SignUpActivity.this, "Fail try again", Toast.LENGTH_SHORT).show();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SignUpActivity.this, "Some thing went wrong", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void veifyEmail() {

        FirebaseUser user= mAuth.getCurrentUser();
        if(user!=null){

            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()){
                    SharedPreferences.Editor pref =sharedPreferences.edit();
                    pref.putString(Name,name);
                    pref.putString(Password,password);
                    pref.putString(Email,email);
                    pref.putString(NationalID,nationalID);
                    pref.putString(Image,image);
                    pref.commit();




                    //email sent
                    Toast.makeText(SignUpActivity.this, "Email Sent", Toast.LENGTH_SHORT).show();
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(SignUpActivity.this,LoginActivity.class));
                    finish();

                }else {
                    mAuth.signOut();
                    finish();
                }
                }
            });
            

        }

    }



    private void cropImage() {

    }
}