package com.onexzgj.ppjoke.ui.find.detail;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;

import com.onexzgj.ppjoke.R;
import com.onexzgj.ppjoke.model.TagList;

public class TagFeedListActivity extends AppCompatActivity {

    public static void startActivity(Context mContext, TagList item) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_feed_list);
    }
}
