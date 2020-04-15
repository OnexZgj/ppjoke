package com.onexzgj.ppjoke.ui.publish;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.mooc.libcommon.utils.StatusBar;
import com.mooc.libnavannotation.ActivityDestination;
import com.onexzgj.ppjoke.R;

/**
 * 发布activity
 * @author onexzgj
 * @time 4/15
 */
@ActivityDestination(pageUrl = "main/tabs/publish", needLogin = true)
public class PublishActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        StatusBar.fitSystemBar(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);

    }
}
