package com.onexzgj.ppjoke.ui.login;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.mooc.libnetwork.ApiResponse;
import com.mooc.libnetwork.ApiService;
import com.mooc.libnetwork.JsonCallback;
import com.onexzgj.ppjoke.MainActivity;
import com.onexzgj.ppjoke.R;
import com.onexzgj.ppjoke.model.User;
import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQToken;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private MaterialButton btnActiongLogin;
    private ImageView ivClose;
    private Tencent mTencent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout_login);
        btnActiongLogin = findViewById(R.id.action_login);
        ivClose = findViewById(R.id.action_close);

        btnActiongLogin.setOnClickListener(this);
        ivClose.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.action_close:
                finish();
                break;
            case R.id.action_login:
                login();
                break;
        }
    }

    private void login() {
        if (mTencent == null) {
            //
            mTencent = Tencent.createInstance("101870754", this.getApplicationContext());
        }
        mTencent.login(this, "all", loginListener);
    }

    IUiListener loginListener = new IUiListener() {
        @Override
        public void onComplete(Object o) {
            JSONObject response = (JSONObject) o;
            try {
                String openid = response.getString("openid");
                String access_token = response.getString("access_token");
                String expires_in = response.getString("expires_in");
                long expires_time = response.getLong("expires_time");

                mTencent.setOpenId(openid);
                mTencent.setAccessToken(access_token, expires_in);
                QQToken qqToken = mTencent.getQQToken();
                getUserInfo(qqToken, expires_time, openid);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(UiError uiError) {
            Toast.makeText(getApplicationContext(), "登录失败:reason" + uiError.toString(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel() {
            Toast.makeText(getApplicationContext(), "登录取消", Toast.LENGTH_SHORT).show();
        }
    };

    private void getUserInfo(QQToken qqToken, long expires_time, String openid) {
        UserInfo userInfo = new UserInfo(getApplicationContext(), qqToken);
        userInfo.getUserInfo(new IUiListener() {
            @Override
            public void onComplete(Object o) {
                JSONObject response= (JSONObject) o;

                try {
                   String nickname =  response.getString("nickname");
                   String figureurl_2 =  response.getString("figureurl_2");
                    saveUserInfo(nickname, figureurl_2, openid, expires_time);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(UiError uiError) {
                Toast.makeText(getApplicationContext(), "登录失败:reason" + uiError.toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(), "登录取消", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 保存用户信息
     * @param nickname 用户名
     * @param figureurl_2 用户头像
     * @param expires_time
     */
    private void saveUserInfo(String nickname, String figureurl_2, String openid, long expires_time) {
        ApiService.get("/user/insert")
                .addParam("name", nickname)
                .addParam("avatar", figureurl_2)
                .addParam("qqOpenId", openid)
                .addParam("expires_time", expires_time)
                .execute(new JsonCallback<User>() {
                    @Override
                    public void onSuccess(ApiResponse<User> response) {
                        if (response!=null){
                            UserManager.get().saveUserInfo(response.body);
                            finish();
                        }else{
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "登陆失败", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onError(ApiResponse response) {
                        super.onError(response);
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_LOGIN) {
            Tencent.onActivityResultData(requestCode, resultCode, data, loginListener);
        }
    }

}
