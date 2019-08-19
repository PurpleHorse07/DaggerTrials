package com.example.daggerexample.ui.auth;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.RequestManager;
import com.example.daggerexample.R;
import com.example.daggerexample.models.User;
import com.example.daggerexample.ui.main.MainActivity;
import com.example.daggerexample.viewmodels.ViewModelProviderFactory;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;

public class AuthActivity extends DaggerAppCompatActivity implements View.OnClickListener {

    private static final String TAG = "AuthActivity";
    @Inject
    Drawable logo;
    @Inject
    RequestManager requestManager;
    @Inject
    ViewModelProviderFactory viewModelProviderFactory;
    private AuthViewModel viewModel;
    private EditText userId;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_auth);
        userId = findViewById(R.id.text);
        progressBar = findViewById(R.id.progress);

        setLogo();

        //viewModel= ViewModelProviders.of(this,viewModelProviderFactory).get(AuthViewModel.class);     //Deprecated
        viewModel = new ViewModelProvider(this, viewModelProviderFactory).get(AuthViewModel.class);

        findViewById(R.id.button).setOnClickListener(this);

        subscribeObservers();
    }

    private void setLogo() {
        requestManager.load(logo).into((ImageView) findViewById(R.id.image));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button: {
                tryLogin();
                break;
            }
        }

    }

    private void tryLogin() {
        if (TextUtils.isDigitsOnly(userId.getText().toString()))
            viewModel.authenticateWithId(Integer.parseInt(userId.getText().toString()));
        else
            Toast.makeText(this, "Enter Proper ID Please", Toast.LENGTH_SHORT).show();
    }

    private void subscribeObservers() {
        viewModel.observeAuthState().observe(this, new Observer<AuthResource<User>>() {
            @Override
            public void onChanged(AuthResource<User> userAuthResource) {
                if (userAuthResource != null) {
                    switch (userAuthResource.status) {
                        case LOADING: {
                            showProgressBar(true);
                            break;
                        }
                        case AUTHENTICATED: {
                            showProgressBar(false);
                            Log.d(TAG, "onChanged: LOGGED IN: " + (userAuthResource.data == null ? "NULL" : userAuthResource.data.getEmail()));
                            logIn();
                            break;
                        }
                        case ERROR: {
                            showProgressBar(false);
                            Toast.makeText(AuthActivity.this, "Error Logging You In...", Toast.LENGTH_SHORT).show();
                            break;
                        }
                        case NOT_AUTHENTICATED: {
                            showProgressBar(false);
                            break;
                        }
                    }
                }
            }
        });
    }

    private void logIn() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void showProgressBar(boolean isVisible) {
        if (isVisible)
            progressBar.setVisibility(View.VISIBLE);
        else
            progressBar.setVisibility(View.GONE);
    }
}
