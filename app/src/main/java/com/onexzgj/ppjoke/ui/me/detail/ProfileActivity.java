package com.onexzgj.ppjoke.ui.me.detail;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
/**
 * MyFragment中的详情页
 * @author OnexZgj
 */
import com.onexzgj.ppjoke.R;

public class ProfileActivity extends AppCompatActivity {

    public static final String KEY_TAB_TYPE = "key_tab_type";

    public static void startProfieActivity(Context context, String tabType) {
        Intent intent = new Intent(context,ProfileActivity.class);
        intent.putExtra(KEY_TAB_TYPE,tabType);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
    }
}
