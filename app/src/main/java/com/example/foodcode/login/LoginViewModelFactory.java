package com.example.foodcode.login;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.foodcode.data.LoginDataSource;
import com.example.foodcode.data.LoginRepository;

/**
 * ViewModel provider factory to instantiate LoginViewModel.
 * Required given LoginViewModel has a non-empty constructor
 */
public class LoginViewModelFactory implements ViewModelProvider.Factory {

//    RxDataStore<Preferences> dataStore;
//
//    public LoginViewModelFactory(RxDataStore<Preferences> dataStore) {
//        this.dataStore = dataStore;
//    }

    private Context context;

    public LoginViewModelFactory(Context context){
        this.context = context;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(LoginViewModel.class)) {
            return (T) new LoginViewModel(LoginRepository.getInstance(new LoginDataSource()), context);
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}