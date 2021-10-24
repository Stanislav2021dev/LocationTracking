package com.example.locationtask6.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;


import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.example.locationtask6.R;
import com.google.android.material.snackbar.Snackbar;

public class SnackBarViewClass extends Activity {

    public void createSnackBar(Context context, String mainText, String action, Intent intent){

        Snackbar.make(context,((Activity) context).findViewById(android.R.id.content) ,mainText,Snackbar.LENGTH_INDEFINITE)
                .setAction(action, v -> {
                    finish();
                    context.startActivity(intent);
                }).show();
    }
}
