package com.example.locationtask6;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.locationtask6.databinding.LoginBinding;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.BuildConfig;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.DateFormat;
import java.util.Collections;
import java.util.Date;

public class LogInActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private LoginBinding binding;
    private String email;
    private String password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        binding = DataBindingUtil.setContentView(this, R.layout.login);
        auth=FirebaseAuth.getInstance();

        binding.singUpButton.setOnClickListener(v -> singUpUser());
        binding.loginButton.setOnClickListener(v -> loginUser());
    }

    public void loginUser() {

        email = binding.textInputEmail.getEditText().getText().toString().trim();
        password=binding.textInputPassword.getEditText().getText().toString().trim();

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d("Auth", "signInWithEmail:success");
                        FirebaseUser user = auth.getCurrentUser();
                        Toast.makeText(LogInActivity.this,"Success",Toast.LENGTH_LONG).show();
                        startActivity(new Intent(LogInActivity.this,MapsActivity.class));
                      //  updateUI(user);
                    } else {
                        Log.w("Auth", "signInWithEmail:failure", task.getException());
                        Toast.makeText(LogInActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                       // updateUI(null);
                    }
                });
    }

    public void singUpUser(){
        email = binding.textInputEmail.getEditText().getText().toString().trim();
        password=binding.textInputPassword.getEditText().getText().toString().trim();
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d("Auth", "createUserWithEmail:success");
                        FirebaseUser user = auth.getCurrentUser();
                        Toast.makeText(LogInActivity.this,"Account created",Toast.LENGTH_LONG).show();
                        startActivity(new Intent(LogInActivity.this,MapsActivity.class));
                      //  updateUI(user);
                    } else {

                        Log.w("Auth", "createUserWithEmail:failure", task.getException());
                        Toast.makeText(LogInActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                      //  updateUI(null);
                    }
                });
    }





}