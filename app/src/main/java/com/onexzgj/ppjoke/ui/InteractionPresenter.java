package com.onexzgj.ppjoke.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.arch.core.executor.ArchTaskExecutor;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.alibaba.fastjson.JSONObject;
import com.mooc.libcommon.extention.LiveDataBus;
import com.mooc.libcommon.global.AppGlobals;
import com.mooc.libnetwork.ApiResponse;
import com.mooc.libnetwork.ApiService;
import com.mooc.libnetwork.JsonCallback;
import com.onexzgj.ppjoke.model.Feed;
import com.onexzgj.ppjoke.model.User;
import com.onexzgj.ppjoke.ui.home.ToggleCallback;
import com.onexzgj.ppjoke.ui.login.UserManager;

import org.jetbrains.annotations.NotNull;

/**
 * 所有点赞评论和分享的操作
 */
public class InteractionPresenter {
    public static final String DATA_FROM_INTERACTION = "data_from_interaction";

    private static final String URL_TOGGLE_FEED_LIK = "/ugc/toggleFeedLike";

    private static final String URL_TOGGLE_FEED_DISS = "/ugc/dissFeed";

    private static final String URL_SHARE = "/ugc/increaseShareCount";

    private static final String URL_TOGGLE_COMMENT_LIKE = "/ugc/toggleCommentLike";

    public static void toggleFeedLike(LifecycleOwner owner, Feed feed, ToggleCallback toggleCallback) {
        if (!isLogin(owner, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                toggleFeedLikeInternal(feed,toggleCallback);
            }
        })) {

        } else {
            toggleFeedLikeInternal(feed,toggleCallback);
        }
    }


    private static void toggleFeedLikeInternal(Feed feed, ToggleCallback toggleCallback) {
        ApiService.get(URL_TOGGLE_FEED_LIK)
                .addParam("userId", UserManager.get().getUserId())
                .addParam("itemId", feed.itemId)
                .execute(new JsonCallback<JSONObject>() {
                    @Override
                    public void onSuccess(ApiResponse<JSONObject> response) {
                        if (response.body != null) {
                            boolean hasLiked = response.body.getBoolean("hasLiked").booleanValue();
                            feed.getUgc().setHasLiked(hasLiked);
                            toggleCallback.toggleSuccess(feed);
                            LiveDataBus.get().with(DATA_FROM_INTERACTION)
                                    .postValue(feed);

                        }
                    }

                    @Override
                    public void onError(ApiResponse<JSONObject> response) {
                        showToast(response.message);
                        toggleCallback.toggleFail(response.message);
                    }
                });
    }

    //给一个帖子点踩一踩/取消踩一踩,它和给帖子点赞是互斥的
    public static void toggleFeedDiss(LifecycleOwner owner, Feed feed,ToggleCallback toggleCallback) {
        if (!isLogin(owner, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                toggleFeedDissInternal(feed,toggleCallback);
            }
        })) {
        } else {
            toggleFeedDissInternal(feed,toggleCallback);
        }
    }

    private static void toggleFeedDissInternal(Feed feed,ToggleCallback toggleCallback) {
        ApiService.get(URL_TOGGLE_FEED_DISS).addParam("userId", UserManager.get().getUserId())
                .addParam("itemId", feed.itemId)
                .execute(new JsonCallback<JSONObject>() {
                    @Override
                    public void onSuccess(ApiResponse<JSONObject> response) {
                        if (response.body != null) {
                            boolean hasLiked = response.body.getBoolean("hasLiked").booleanValue();
                            feed.getUgc().setHasdiss(hasLiked);
                            toggleCallback.toggleSuccess(feed);
                        }
                    }

                    @Override
                    public void onError(ApiResponse<JSONObject> response) {
                        showToast(response.message);
                        toggleCallback.toggleFail(response.message);
                    }
                });
    }


    public static boolean isLogin(LifecycleOwner owner, Observer<User> observer) {
        if (UserManager.get().isLogin()) {
            return true;
        } else {
            LiveData<User> liveData = UserManager.get().login(AppGlobals.getApplication());
            if (owner == null) {
                liveData.observeForever(loginObserver(observer, liveData));
            } else {
                liveData.observe(owner, loginObserver(observer, liveData));
            }
        }
        return false;
    }


    @SuppressLint("RestrictedApi")
    private static void showToast(String message) {
        ArchTaskExecutor.getMainThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(AppGlobals.getApplication(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @NotNull
    private static Observer<User> loginObserver(Observer<User> observer, LiveData<User> liveData) {
        return new Observer<User>() {
            @Override
            public void onChanged(User user) {
                liveData.removeObserver(this);
                if (user != null && observer != null) {
                    observer.onChanged(user);
                }
            }
        };
    }

    //收藏/取消收藏一个帖子
    public static void toggleFeedFavorite(LifecycleOwner owner, Feed feed) {
        if (!isLogin(owner, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                toggleFeedFavorite(feed);
            }
        })) {
        } else {
            toggleFeedFavorite(feed);
        }
    }

    /**
     * 收藏帖子
     * @param feed
     */
    private static void toggleFeedFavorite(Feed feed) {
        ApiService.get("/ugc/toggleFavorite")
                .addParam("itemId", feed.itemId)
                .addParam("userId", UserManager.get().getUserId())
                .execute(new JsonCallback<JSONObject>() {
                    @Override
                    public void onSuccess(ApiResponse<JSONObject> response) {
                        if (response.body != null) {
                            boolean hasFavorite = response.body.getBooleanValue("hasFavorite");
                            feed.getUgc().setHasFavorite(hasFavorite);
                            LiveDataBus.get().with(DATA_FROM_INTERACTION)
                                    .postValue(feed);
                        }
                    }

                    @Override
                    public void onError(ApiResponse<JSONObject> response) {
                        showToast(response.message);
                    }
                });
    }

    //关注/取消关注一个用户
    public static void toggleFollowUser(LifecycleOwner owner, Feed feed) {
        if (!isLogin(owner, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                toggleFollowUser(feed);
            }
        })) {
        } else {
            toggleFollowUser(feed);
        }
    }

    private static void toggleFollowUser(Feed feed) {
        ApiService.get("/ugc/toggleUserFollow")
                .addParam("followUserId", UserManager.get().getUserId())
                .addParam("userId", feed.author.userId)
                .execute(new JsonCallback<JSONObject>() {
                    @Override
                    public void onSuccess(ApiResponse<JSONObject> response) {
                        if (response.body != null) {
                            boolean hasFollow = response.body.getBooleanValue("hasLiked");
                            feed.getAuthor().setHasFollow(hasFollow);
                            LiveDataBus.get().with(DATA_FROM_INTERACTION)
                                    .postValue(feed);
                        }
                    }

                    @Override
                    public void onError(ApiResponse<JSONObject> response) {
                        showToast(response.message);
                    }
                });
    }


    //删除某个帖子的一个评论
    public static LiveData<Boolean> deleteFeedComment(Context context, long itemId, long commentId) {
        MutableLiveData<Boolean> liveData = new MutableLiveData<>();
        new AlertDialog.Builder(context)
                .setNegativeButton("删除", (dialog, which) -> {
                    dialog.dismiss();
                    deleteFeedCommentInternal(liveData, itemId, commentId);
                }).setPositiveButton("取消", (dialog, which) -> dialog.dismiss()).setMessage("确定要删除这条评论吗？").create().show();
        return liveData;
    }

    private static void deleteFeedCommentInternal(LiveData liveData, long itemId, long commentId) {
        ApiService.get("/comment/deleteComment")
                .addParam("userId", UserManager.get().getUserId())
                .addParam("commentId", commentId)
                .addParam("itemId", itemId)
                .execute(new JsonCallback<JSONObject>() {
                    @Override
                    public void onSuccess(ApiResponse<JSONObject> response) {
                        if (response.body != null) {
                            boolean result = response.body.getBooleanValue("result");
                            ((MutableLiveData) liveData).postValue(result);
                            showToast("评论删除成功");
                        }
                    }

                    @Override
                    public void onError(ApiResponse<JSONObject> response) {
                        showToast(response.message);
                    }
                });
    }


}
