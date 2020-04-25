package com.onexzgj.ppjoke.ui.publish;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkContinuation;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.alibaba.fastjson.JSONObject;
import com.google.android.material.button.MaterialButton;
import com.mooc.libcommon.dialog.LoadingDialog;
import com.mooc.libcommon.utils.FileUtils;
import com.mooc.libcommon.utils.StatusBar;
import com.mooc.libnavannotation.ActivityDestination;
import com.mooc.libnetwork.ApiResponse;
import com.mooc.libnetwork.ApiService;
import com.mooc.libnetwork.JsonCallback;
import com.onexzgj.ppjoke.R;
import com.onexzgj.ppjoke.model.Feed;
import com.onexzgj.ppjoke.model.TagList;
import com.onexzgj.ppjoke.ui.detail.CaptureActivity;
import com.onexzgj.ppjoke.ui.detail.PreviewActivity;
import com.onexzgj.ppjoke.ui.login.UserManager;
import com.onexzgj.ppjoke.view.PPImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 发布activity
 *
 * @author onexzgj
 * @time 4/15
 */
@ActivityDestination(pageUrl = "main/tabs/publish", needLogin = true)
public class PublishActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView publishClose;
    private AppCompatImageView publishDeleteFile;
    private ImageView publishVideoIcon;
    private AppCompatImageView publishAddFile;
    private MaterialButton btnPublish;
    private EditText etPublishContent;
    private FrameLayout fileContainer;
    private String filePath = "";
    private int width = 0;
    private int height = 0;
    private boolean isVideo = false;
    private PPImageView publishCover;
    private String coverFilePath = "";
    private UUID coverUploadUUID;
    private UUID fileUploadUUID;
    private String coverUploadUrl;
    private String fileUploadUrl;
    private LoadingDialog mLoadingDialog;

    private TagList mTagList;
    private MaterialButton publishAddTag;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        StatusBar.fitSystemBar(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);
        publishClose = findViewById(R.id.publish_close);
        publishDeleteFile = findViewById(R.id.publish_delete_file);
        publishVideoIcon = findViewById(R.id.publish_video_icon);
        publishAddFile = findViewById(R.id.action_publish_add_file);
        btnPublish = findViewById(R.id.btn_publish);
        etPublishContent = findViewById(R.id.et_publish_content);
        fileContainer = findViewById(R.id.file_container);
        publishCover = findViewById(R.id.publish_cover);
        publishAddTag = findViewById(R.id.publish_add_tag);

        publishAddFile.setOnClickListener(this);
        btnPublish.setOnClickListener(this);
        publishClose.setOnClickListener(this);
        publishDeleteFile.setOnClickListener(this);
        publishAddTag.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.publish_add_tag:
                TagBottomSheetDialogFragment fragment = new TagBottomSheetDialogFragment();
                fragment.setOnTagItemSelectedListener(new TagBottomSheetDialogFragment.OnTagItemSelectedListener() {
                    @Override
                    public void onTagItemSelected(TagList tagList) {
                        mTagList = tagList;
                        publishAddTag.setText(tagList.title);
                    }
                });
                fragment.show(getSupportFragmentManager(), "tag_dialog");
                break;
            case R.id.btn_publish:
                publish();
                break;
            case R.id.action_publish_add_file:
                CaptureActivity.startActivityForResult(this);
                break;
            case R.id.publish_close:
                showExitDialog();
                break;
            case R.id.publish_delete_file:
                publishAddFile.setVisibility(View.VISIBLE);
                fileContainer.setVisibility(View.GONE);
                publishCover.setImageDrawable(null);
                filePath = null;
                width = 0;
                height = 0;
                isVideo = false;
                break;
        }
    }

    /**
     * 发布帖子
     */
    private void publish() {
        showLoading();
        List<OneTimeWorkRequest> workRequests = new ArrayList<>();
        if (!TextUtils.isEmpty(filePath)) {
            if (isVideo) {
                FileUtils.generateVideoCover(filePath).observe(this, new Observer<String>() {
                    @Override
                    public void onChanged(String coverPath) {
                        coverFilePath = coverPath;
                        OneTimeWorkRequest request = getOneTimeWorkRequest(coverFilePath);
                        coverUploadUUID = request.getId();
                        workRequests.add(request);
                        enqueue(workRequests);
                    }
                });
            }

            OneTimeWorkRequest videoRequest = getOneTimeWorkRequest(filePath);
            fileUploadUUID = videoRequest.getId();
            workRequests.add(videoRequest);

            if (!isVideo) {
                enqueue(workRequests);
            }
        } else {
            publishFeed();
        }
    }

    /**
     * 发表文本的方法
     */
    private void publishFeed() {
        ApiService.post("/feeds/publish")
                .addParam("coverUrl", coverUploadUrl)
                .addParam("fileUrl", fileUploadUrl)
                .addParam("fileWidth", width)
                .addParam("fileHeight", height)
                .addParam("userId", UserManager.get().getUserId())
                .addParam("tagId", mTagList == null ? 0 : mTagList.tagId)
                .addParam("tagTitle", mTagList == null ? "" : mTagList.title)
                .addParam("feedText", etPublishContent.getText().toString())
                .addParam("feedType", isVideo ? Feed.TYPE_VIDEO : Feed.TYPE_IMAGE_TEXT)
                .execute(new JsonCallback<JSONObject>() {
                    @Override
                    public void onSuccess(ApiResponse<JSONObject> response) {
                        showToast(getString(R.string.feed_publisj_success));
                        PublishActivity.this.finish();
                        dismissLoading();
                    }

                    @Override
                    public void onError(ApiResponse<JSONObject> response) {
                        showToast(response.message);
                        dismissLoading();
                    }
                });

    }

    private void enqueue(List<OneTimeWorkRequest> workRequests) {
        WorkContinuation workContinuation = WorkManager.getInstance(PublishActivity.this)
                .beginWith(workRequests);

        workContinuation.enqueue();

        workContinuation.getWorkInfosLiveData().observe(PublishActivity.this, new Observer<List<WorkInfo>>() {
            @Override
            public void onChanged(List<WorkInfo> workInfos) {
                int completedCount = 0;
                int failedCount = 0;

                for (WorkInfo workInfo : workInfos) {
                    WorkInfo.State state = workInfo.getState();
                    Data outputData = workInfo.getOutputData();
                    UUID uuid = workInfo.getId();
                    if (state == WorkInfo.State.FAILED) {
                        if (uuid.equals(coverUploadUUID)) {
                            showToast(getString(R.string.cover_upload_faild));
                        } else if (uuid.equals(WorkInfo.State.FAILED)) {
                            showToast(getString(R.string.video_upload_failed));
                        }
                        failedCount++;
                    } else if (state == WorkInfo.State.SUCCEEDED) {
                        String fileUrl = outputData.getString("fileUrl");
                        if (uuid.equals(coverUploadUUID)) {
                            coverUploadUrl = fileUrl;
                        } else if (uuid.equals(fileUploadUUID)) {
                            fileUploadUrl = fileUrl;
                        }
                        completedCount++;
                    }
                }
                if (completedCount >= workInfos.size()) {
                    publishFeed();
                } else if (failedCount > 0) {
                    dismissLoading();
                }
            }
        });

    }

    private void showLoading() {
        if (mLoadingDialog == null) {
            mLoadingDialog = new LoadingDialog(this);
            mLoadingDialog.setLoadingText(getString(R.string.feed_publish_ing));
        }
        mLoadingDialog.show();
    }

    private void dismissLoading() {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            if (mLoadingDialog != null) {
                mLoadingDialog.dismiss();
            }
        } else {
            runOnUiThread(() -> {
                if (mLoadingDialog != null) {
                    mLoadingDialog.dismiss();
                }
            });
        }
    }

    private void showToast(String message) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(PublishActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    /**
     * 生成OneTimeWorkRequest
     *
     * @param filePath
     * @return
     */
    private OneTimeWorkRequest getOneTimeWorkRequest(String filePath) {
        Data inputData = new Data.Builder()
                .putString("file", filePath)
                .build();

        OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(UploadFileWorker.class)
                .setInputData(inputData)
                .build();

        return request;
    }

    private void showExitDialog() {
        new AlertDialog.Builder(this)
                .setMessage(getString(R.string.publish_exit_message))
                .setPositiveButton("退出", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).setNegativeButton("取消", null)
                .create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CaptureActivity.REQ_CAPTURE && resultCode == RESULT_OK) {
            Intent intent = new Intent();
            filePath = data.getStringExtra(CaptureActivity.RESULT_FILE_PATH);
            width = data.getIntExtra(CaptureActivity.RESULT_FILE_WIDTH, 0);
            height = data.getIntExtra(CaptureActivity.RESULT_FILE_HEIGHT, 0);
            isVideo = data.getBooleanExtra(CaptureActivity.RESULT_FILE_TYPE, false);
            showFileThumbnail();
        }
    }

    private void showFileThumbnail() {
        if (TextUtils.isEmpty(filePath)) {
            return;
        }
        publishAddFile.setVisibility(View.GONE);
        fileContainer.setVisibility(View.VISIBLE);
        publishCover.setImageUrl(filePath);
        publishVideoIcon.setVisibility(isVideo ? View.VISIBLE : View.GONE);
        publishCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreviewActivity.startActivityForResult(PublishActivity.this, filePath, isVideo, null);
            }
        });
    }
}
