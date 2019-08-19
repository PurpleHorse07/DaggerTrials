package com.example.daggerexample.di;

import androidx.lifecycle.ViewModelProvider;

import com.example.daggerexample.viewmodels.ViewModelProviderFactory;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class ViewModelFactoryModule {

    @Binds
    public abstract ViewModelProvider.Factory bindViewModelFactory(ViewModelProviderFactory viewModelProviderFactory);
    //Same as annotating with @Provides
}
