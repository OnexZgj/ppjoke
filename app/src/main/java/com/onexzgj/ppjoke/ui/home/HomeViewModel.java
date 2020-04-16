package com.onexzgj.ppjoke.ui.home;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.arch.core.executor.ArchTaskExecutor;
import androidx.lifecycle.ViewModel;
import androidx.paging.DataSource;
import androidx.paging.ItemKeyedDataSource;

import com.alibaba.fastjson.TypeReference;
import com.mooc.libnetwork.ApiResponse;
import com.mooc.libnetwork.ApiService;
import com.mooc.libnetwork.JsonCallback;
import com.mooc.libnetwork.Request;
import com.onexzgj.ppjoke.base.BaseViewModel;
import com.onexzgj.ppjoke.model.Feed;
import com.onexzgj.ppjoke.ui.login.UserManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class HomeViewModel extends BaseViewModel<Feed> {
    private String mFeedType;

    /**
     * 标识是否正在上拉加载的逻辑
     */
    private AtomicBoolean loadAfter = new AtomicBoolean(false);

    public void setmFeedType(String mFeedType) {
        this.mFeedType = mFeedType;
    }

    @Override
    public DataSource createDataSource() {
        return new FeedDataSource();
    }

    /**
     * paging不在处理加载更多时，自己进行处理
     *
     * @param id
     * @param callback
     */
    @SuppressLint("RestrictedApi")
    public void loadAfter(int id, ItemKeyedDataSource.LoadCallback<Feed> callback) {
        if (loadAfter.get()) {
            callback.onResult(Collections.emptyList());
            return;
        }
        ArchTaskExecutor.getIOThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                loadData(id, mConfig.pageSize, callback);
            }
        });
    }

    private void loadData(int key, int count, ItemKeyedDataSource.LoadCallback<Feed> callback) {
        if (key > 0) {
            loadAfter.set(true);
        }
        //feeds/queryHotFeedsList
        Request request = ApiService.get("/feeds/queryHotFeedsList")
                .addParam("feedType", mFeedType)
                .addParam("userId", UserManager.get().getUserId())
                .addParam("feedId", key)
                .addParam("pageCount", count)
                .responseType(new TypeReference<ArrayList<Feed>>() {
                }.getType());

        ApiResponse<List<Feed>> response = request.execute();
        List<Feed> data = response.body == null ? Collections.emptyList() : response.body;

        callback.onResult(data);

        if (key > 0) {
            getBoundaryPageData().postValue(data.size() > 0);
            loadAfter.set(false);
        }

        Log.e("loadData", "loadData: key:" + key);

    }


    class FeedDataSource extends ItemKeyedDataSource<Integer, Feed> {

        @Override
        public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull LoadInitialCallback<Feed> callback) {
            Log.d("TAG", "HomeViewModel + loadInitial: ");
            loadData(0, params.requestedLoadSize, callback);
        }

        @Override
        public void loadAfter(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Feed> callback) {

            Log.d("TAG", "HomeViewModel + loadAfter: " + params.key);
            loadData(params.key, params.requestedLoadSize, callback);
        }

        @Override
        public void loadBefore(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Feed> callback) {
            callback.onResult(Collections.emptyList());
            //能够向前加载数据的
        }

        @NonNull
        @Override
        public Integer getKey(@NonNull Feed item) {
            return item.id;
        }


    }
}
