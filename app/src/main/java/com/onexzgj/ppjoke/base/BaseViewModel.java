package com.onexzgj.ppjoke.base;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.DataSource;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

public abstract class BaseViewModel<T> extends ViewModel {

    private DataSource dataSource;
    private LiveData<PagedList<T>> pageData;

    public DataSource getDataSource() {
        return dataSource;
    }

    public LiveData<PagedList<T>> getPageData() {
        return pageData;
    }

    public MutableLiveData<Boolean> getBoundaryPageData() {
        return boundaryPageData;
    }

    private MutableLiveData<Boolean> boundaryPageData = new MutableLiveData<>();


    private DataSource.Factory factory = new DataSource.Factory() {
        @NonNull
        @Override
        public DataSource create() {
            if (dataSource == null || dataSource.isInvalid()) {
                dataSource = createDataSource();
            }
            return dataSource;
        }
    };

    public abstract DataSource createDataSource();

    private PagedList.Config mConfig;

    public BaseViewModel() {
        mConfig = new PagedList.Config.Builder().setPageSize(10)
                .setInitialLoadSizeHint(12)
                .build();

        new LivePagedListBuilder(factory, mConfig)
                .setInitialLoadKey(0)
                .setBoundaryCallback(callback)
                .build();
    }

    private PagedList.BoundaryCallback callback = new PagedList.BoundaryCallback() {
        @Override
        public void onZeroItemsLoaded() {
            //新提交的PagedList中没有数据
            boundaryPageData.postValue(false);
        }

        @Override
        public void onItemAtFrontLoaded(@NonNull Object itemAtFront) {
            //第一条数据加载到列表上
            boundaryPageData.postValue(true);
        }

        @Override
        public void onItemAtEndLoaded(@NonNull Object itemAtEnd) {
            super.onItemAtEndLoaded(itemAtEnd);
        }
    };
}
