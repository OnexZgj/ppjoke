package com.onexzgj.ppjoke.ui.home;

import androidx.lifecycle.ViewModel;
import androidx.paging.DataSource;

import com.onexzgj.ppjoke.base.BaseViewModel;
import com.onexzgj.ppjoke.model.Feed;

public class HomeViewModel extends BaseViewModel<Feed> {
    @Override
    public DataSource createDataSource() {
        return null;
    }
}
