package com.example.daggerexample;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import com.example.daggerexample.models.User;
import com.example.daggerexample.ui.auth.AuthActivity;
import com.example.daggerexample.ui.auth.AuthResource;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;

public abstract class BaseActivity extends DaggerAppCompatActivity {

    private static final String TAG = "BaseActivity";

    @Inject
    public SessionManager sessionManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        subscribeObservers();
    }

    private void subscribeObservers() {
        sessionManager.getAuthUser().observe(this, new Observer<AuthResource<User>>() {
            @Override
            public void onChanged(AuthResource<User> userAuthResource) {
                if (userAuthResource != null) {
                    switch (userAuthResource.status) {
                        case LOADING: {
                            break;
                        }
                        case AUTHENTICATED: {
                            Log.d(TAG, "onChanged: LOGGED IN: " + (userAuthResource.data == null ? "NULL" : userAuthResource.data.getEmail()));
                            break;
                        }
                        case ERROR: {
                            Log.d(TAG, "onChanged: ERROR OCCURED");
                            break;
                        }
                        case NOT_AUTHENTICATED: {
                            navLogOut();
                            break;
                        }
                    }
                }
            }
        });
    }

    private void navLogOut() {
        startActivity(new Intent(this, AuthActivity.class));
        finish();
    }
}
