package com.example.locationtask6.presenter;

import android.util.Log;

import com.example.locationtask6.view.LogInActivity;
import com.example.locationtask6.view.LogInInterface;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import moxy.MvpPresenter;

public class LogInPresenter extends MvpPresenter<LogInInterface>   {

    private final FirebaseAuth auth = FirebaseAuth.getInstance();

    public void loginUser(String email, String password) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("Auth", "signInWithEmail:success");
                FirebaseUser user = auth.getCurrentUser();
                getViewState().makeToast("Login success");
                getViewState().onSuccessAuth();

            } else {
                Log.w("Auth", "signInWithEmail:failure", task.getException());
                getViewState().makeToast("Login fail");
            }
        });
    }

    public void singUpUser(String email,String password){
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("Auth", "createUserWithEmail:success");
                        FirebaseUser user = auth.getCurrentUser();
                        getViewState().makeToast("Account created");
                        getViewState().onSuccessAuth();
                    } else {
                        Log.w("Auth", "createUserWithEmail:failure", task.getException());
                        getViewState().makeToast("Registration failed");
                    }
                });
    }
}
