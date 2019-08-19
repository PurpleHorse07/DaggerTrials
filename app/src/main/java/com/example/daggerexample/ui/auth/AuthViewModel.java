package com.example.daggerexample.ui.auth;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.example.daggerexample.SessionManager;
import com.example.daggerexample.models.User;
import com.example.daggerexample.network.auth.AuthApi;

import javax.inject.Inject;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class AuthViewModel extends ViewModel {

    private static final String TAG = "AuthViewModel";
    private final AuthApi authApi;
    private SessionManager sessionManager;
    
    @Inject
    AuthViewModel(AuthApi authApi, SessionManager sessionManager){
        Log.d(TAG, "AuthViewModel:  ViewModel is working...");
        
        this.authApi=authApi;
        if(authApi==null){
            Log.d(TAG, "AuthViewModel: AUTH API IS NULL");
        }else
            Log.d(TAG, "AuthViewModel: AUTH API IS NOT NULL");

        this.sessionManager=sessionManager;

    }

    public void authenticateWithId(int userId){
        sessionManager.authenticateWithId(queryUserId(userId));
    }

    private LiveData<AuthResource<User>> queryUserId(int userId){
        return LiveDataReactiveStreams.fromPublisher(
                authApi.getUser(userId)
                        .onErrorReturn(new Function<Throwable, User>() {
                            @Override
                            public User apply(Throwable throwable) throws Exception {
                                return User.builder().id(-1).build();
                            }
                        })
                        .map(new Function<User, AuthResource<User>>() {
                            @Override
                            public AuthResource<User> apply(User user) throws Exception {
                                if(user.getId()==-1)
                                    return AuthResource.error("Could Not Authenticate",null);
                                return AuthResource.authenticated(user);
                            }
                        })
                        .subscribeOn(Schedulers.io())
        );
    }

    public LiveData<AuthResource<User>> observeAuthState(){
        return sessionManager.getAuthUser();
    }
}
