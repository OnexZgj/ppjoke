package com.onexzgj.ppjoke.ui.home;

import com.onexzgj.ppjoke.model.Feed;

public interface ToggleCallback {
    void toggleSuccess();

    void toggleFail(String message);
}
