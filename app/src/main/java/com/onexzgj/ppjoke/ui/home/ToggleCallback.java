package com.onexzgj.ppjoke.ui.home;

import com.onexzgj.ppjoke.model.Feed;

/**
 * 用于点赞，踩之后的回调接口
 */
public interface ToggleCallback {
    void toggleSuccess(Feed feed);

    void toggleFail(String message);
}
