package com.example.vote_app.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.vote_app.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends AppCompatActivity {
private EditText userEmail,userPassword;
private Button LoginBtn;
private TextView forgetPassword;
private FirebaseAuth mAuth;


    public static final String PREFERENCES ="prefkeys";
    public static final String Name ="namekey";
    public static final String Email ="emailkey";
    public static final String Password ="passwordkey";
    public static final String NationalID ="nationalidkey";
    public static final String  Image="imagekey";
    public static final String  UploadData="uploaddata";
    SharedPreferences sharedPreferences;
    StorageReference reference ;
    FirebaseFirestore firebaseFirestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        sharedPreferences =getApplicationContext().getSharedPreferences(PREFERENCES,MODE_PRIVATE);
        reference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore= FirebaseFirestore.getInstance();



        findViewById(R.id.dont_have_acc).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,SignUpActivity.class));
            }
        });

        userEmail =findViewById(R.id.user_email);
        userPassword =findViewById(R.id.user_password);
        LoginBtn =findViewById(R.id.login_btn);
        forgetPassword=findViewById(R.id.forget_password);
        mAuth=FirebaseAuth.getInstance();

        LoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=userEmail.getText().toString().trim();
                String password=userPassword.getText().toString().trim();

                mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                   if (task.isSuccessful()){

                       verifyEmail();

                   }else {
                       Toast.makeText(LoginActivity.this, "User not found", Toast.LENGTH_SHORT).show();

                   }

                    }
                });

            }
        });
        forgetPassword.findViewById(R.id.forget_password).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            startActivity(new Intent(LoginActivity.this, ForgetPasswordActivity.class));
            }
        });

    }

    private void verifyEmail() {
        FirebaseUser user=mAuth.getCurrentUser();
        assert user != null;
        if (user.isEmailVerified()) {

            Boolean bol = sharedPreferences.getBoolean(UploadData, false);
            if (bol) {

                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                finish();
            } else {

                String name = sharedPreferences.getString(Name, null);
                String password = sharedPreferences.getString(Password, null);
                String email = sharedPreferences.getString(Email, null);
                String id = sharedPreferences.getString(NationalID, null);
                String image = sharedPreferences.getString(Image, null);


                if (name != null && password != null && email != null && id != null) {

                    String uid = mAuth.getUid();
                    StorageReference imagePath = reference.child("image_profile").child(uid + "jpg");
                    imagePath.putFile(Uri.parse(image)).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                            if (task.isSuccessful()) {

                                imagePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {

                                        Map<String, Object> map = new HashMap<>();
                                        map.put("name", name);
                                        map.put("email", email);
                                        map.put("password", password);
                                        map.put("nationalID", id);
                                        map.put("image", uri.toString());
                                        map.put("uid", uid);

                                        firebaseFirestore.collection("Users")
                                                .document(uid)
                                                .set(map)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            sharedPreferences = getApplicationContext().getSharedPreferences(PREFERENCES, MODE_PRIVATE);
                                                            SharedPreferences.Editor pref = sharedPreferences.edit();
                                                            pref.putBoolean(UploadData, true);
                                                            pref.commit();


                                                            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                                                            finish();
                                                        } else {

                                                            Toast.makeText(LoginActivity.this, "Data not store ", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    }
                                });

                            } else {
                                Toast.makeText(LoginActivity.this, "" + task.getException(), Toast.LENGTH_SHORT).show();

                            }

                        }
                    });
                }


            }
        }
            else {
             mAuth.signOut();
              Toast.makeText(LoginActivity.this, "Please verify your Email", Toast.LENGTH_SHORT).show();
        }

    }
}