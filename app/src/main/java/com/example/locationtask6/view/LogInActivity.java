package com.example.locationtask6.view;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.locationtask6.R;
import com.example.locationtask6.databinding.LoginBinding;
import com.example.locationtask6.presenter.LogInPresenter;

import moxy.MvpAppCompatActivity;
import moxy.presenter.InjectPresenter;

public class LogInActivity extends MvpAppCompatActivity implements LogInInterface {

    @InjectPresenter
    LogInPresenter logInPresenter;

    private LoginBinding binding;
    static LogInActivity logInActivity;

    private static LogInActivity instance;
    public LogInActivity() {
        instance = this;
    }
    public static Context getContext() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        logInActivity=this;
        binding = DataBindingUtil.setContentView(this, R.layout.login);

        binding.singUpButton.setOnClickListener(v ->{
            if (!(validateEmail()) | !(validatePassword())){
                return;
            }
            logInPresenter.singUpUser(getEmail(),getPassword());
        });

        binding.loginButton.setOnClickListener(v ->
                {
                    if (!(validateEmail())| !(validatePassword())){
                        return;
                    }
                    logInPresenter.loginUser(getEmail(),getPassword());
                });
    }

    public static LogInActivity getInstance() {
        return logInActivity; }

    public String getEmail(){
        return binding.textInputEmail.getEditText().getText().toString().trim();
    }

    public boolean validateEmail() {
        if (getEmail().isEmpty()){
            binding.textInputEmail.setError("Enter email");
            return false;
        }
        else binding.textInputEmail.setError("");
       return true;
    }

    public String getPassword(){
        return binding.textInputPassword.getEditText().getText().toString().trim();
    }
    public boolean validatePassword() {
        if (getPassword().isEmpty()){
            binding.textInputPassword.setError("Enter password");
            return false;
        }
        else binding.textInputPassword.setError("");
        return true;
    }


    @Override
    public void makeToast(String toast) {
        Toast.makeText(LogInActivity.this,toast,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onSuccessAuth() {
        startActivity(new Intent(LogInActivity.this, TrackActivity.class));
    }



}