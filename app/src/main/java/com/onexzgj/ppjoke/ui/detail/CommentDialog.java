package com.onexzgj.ppjoke.ui.detail;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.arch.core.executor.ArchTaskExecutor;
import androidx.lifecycle.Observer;

import com.mooc.libcommon.global.AppGlobals;
import com.mooc.libcommon.utils.FileUploadManager;
import com.mooc.libcommon.utils.FileUtils;
import com.mooc.libcommon.utils.PixUtils;
import com.mooc.libcommon.view.PPEditTextView;
import com.mooc.libcommon.view.ViewHelper;
import com.mooc.libnetwork.ApiResponse;
import com.mooc.libnetwork.ApiService;
import com.mooc.libnetwork.JsonCallback;
import com.onexzgj.ppjoke.R;
import com.onexzgj.ppjoke.model.Comment;
import com.onexzgj.ppjoke.ui.login.UserManager;
import com.onexzgj.ppjoke.view.PPImageView;

import java.util.concurrent.atomic.AtomicInteger;

public class CommentDialog extends AppCompatDialogFragment implements View.OnClickListener {


    private long itemId;

    private String filePath;
    private int width=0, height=0;
    private boolean isVideo=false;
    private String coverUrl="";
    private String fileUrl="";

    private static final String KEY_ITEM_ID = "key_item_id";
    private PPEditTextView inputView;
    private commentAddListener mListener;
    private FrameLayout commentExtLayout;
    private PPImageView commentCover;
    private AppCompatImageView commentIconVideo;
    private AppCompatImageView commentDelete;
    private AppCompatImageView commentVideo;

    public static CommentDialog newInstance(long itemId) {
        Bundle args = new Bundle();
        args.putLong(KEY_ITEM_ID, itemId);
        CommentDialog fragment = new CommentDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Window window = getDialog().getWindow();
        window.setWindowAnimations(0);

        View view = inflater.inflate(R.layout.layout_comment_dialog,  window.findViewById(android.R.id.content), false);

        ViewHelper.setViewOutline(view, PixUtils.dp2px(10), ViewHelper.RADIUS_TOP);

        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        inputView = view.findViewById(R.id.input_view);
        commentCover = view.findViewById(R.id.comment_cover);
        commentIconVideo = view.findViewById(R.id.comment_icon_video);
        commentDelete = view.findViewById(R.id.comment_delete);
        commentExtLayout = view.findViewById(R.id.comment_ext_layout);

        this.itemId = getArguments().getLong(KEY_ITEM_ID);
        view.findViewById(R.id.comment_delete).setOnClickListener(this);
        commentVideo = view.findViewById(R.id.comment_video);
        commentVideo.setOnClickListener(this);
        view.findViewById(R.id.comment_send).setOnClickListener(this);

        view.post(() -> showSoftInputMethod());

        return view;
    }


    private void showSoftInputMethod() {
        inputView.setFocusable(true);
        inputView.setFocusableInTouchMode(true);
        //请求获得焦点
        inputView.requestFocus();
        InputMethodManager manager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.showSoftInput(inputView, 0);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.comment_delete:
                break;
            case R.id.comment_video:
                CaptureActivity.startActivityForResult(getActivity());
                break;
            case R.id.comment_send:
                publishComment();
                break;
        }
    }

    /**
     * 发表评论
     */
    private void publishComment() {

        if (TextUtils.isEmpty(inputView.getText())) {
            return;
        }
        if (isVideo && !TextUtils.isEmpty(filePath)) {
            FileUtils.generateVideoCover(filePath).observe(this, new Observer<String>() {
                @Override
                public void onChanged(String coverPath) {
                    uploadFile(coverPath, filePath);
                }
            });
        } else if (!TextUtils.isEmpty(filePath)) {
            uploadFile(null, filePath);
        } else {
            publish();
        }
    }

    @SuppressLint("RestrictedApi")
    private void uploadFile(String coverPath, String filePath) {
        //AtomicInteger, CountDownLatch, CyclicBarrier
//        showLoadingDialog();
        AtomicInteger count = new AtomicInteger(1);
        if (!TextUtils.isEmpty(coverPath)) {
            count.set(2);
            ArchTaskExecutor.getIOThreadExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    int remain = count.decrementAndGet();
                    coverUrl = FileUploadManager.upload(coverPath);
                    if (remain <= 0) {
                        if (!TextUtils.isEmpty(fileUrl) && !TextUtils.isEmpty(coverUrl)) {
                            publish();
                        } else {
//                            dismissLoadingDialog();
                            showToast(getString(R.string.file_upload_failed));
                        }
                    }
                }
            });
        }
        ArchTaskExecutor.getIOThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                int remain = count.decrementAndGet();
                fileUrl = FileUploadManager.upload(filePath);
                if (remain <= 0) {
                    if (!TextUtils.isEmpty(fileUrl) || !TextUtils.isEmpty(coverPath) && !TextUtils.isEmpty(coverUrl)) {
                        publish();
                    } else {
//                        dismissLoadingDialog();
                        showToast(getString(R.string.file_upload_failed));
                    }
                }
            }
        });

    }

    private void publish() {
        String commentText = inputView.getText().toString();
        ApiService.post("/comment/addComment")
                .addParam("userId", UserManager.get().getUserId())
                .addParam("itemId", itemId)
                .addParam("commentText", commentText)
                .addParam("image_url", isVideo ? coverUrl : fileUrl)
                .addParam("video_url", isVideo ? fileUrl : null)
                .addParam("width", width)
                .addParam("height", height)
                .execute(new JsonCallback<Comment>() {
                    @Override
                    public void onSuccess(ApiResponse<Comment> response) {
                        onCommentSuccess(response.body);
                    }

                    @Override
                    public void onError(ApiResponse<Comment> response) {
                        showToast("评论失败:" + response.message);
                    }
                });
    }

    @SuppressLint("RestrictedApi")
    private void showToast(String s) {
        //showToast几个可能会出现在异步线程调用
        if (Looper.myLooper() == Looper.getMainLooper()) {
            Toast.makeText(AppGlobals.getApplication(), s, Toast.LENGTH_SHORT).show();
        } else {
            ArchTaskExecutor.getMainThreadExecutor().execute(() -> Toast.makeText(AppGlobals.getApplication(), s, Toast.LENGTH_SHORT).show());
        }
    }

    @SuppressLint("RestrictedApi")
    private void onCommentSuccess(Comment body) {
        showToast("评论发布成功");
        ArchTaskExecutor.getMainThreadExecutor().execute(() -> {
            if (mListener != null) {
                mListener.onAddComment(body);
            }
            dismiss();
        });
    }

    public interface commentAddListener {
        void onAddComment(Comment comment);
    }

    public void setCommentAddListener(commentAddListener listener) {
        mListener = listener;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CaptureActivity.REQ_CAPTURE && resultCode == Activity.RESULT_OK) {
            filePath = data.getStringExtra(CaptureActivity.RESULT_FILE_PATH);
            width = data.getIntExtra(CaptureActivity.RESULT_FILE_WIDTH, 0);
            height = data.getIntExtra(CaptureActivity.RESULT_FILE_HEIGHT, 0);
            isVideo = data.getBooleanExtra(CaptureActivity.RESULT_FILE_TYPE, false);

            if (!TextUtils.isEmpty(filePath)) {
                commentExtLayout.setVisibility(View.VISIBLE);
                commentCover.setImageUrl(filePath);
                if (isVideo) {
                    commentIconVideo.setVisibility(View.VISIBLE);
                }
            }
            commentVideo.setEnabled(false);
            commentVideo.setAlpha(80);
        }
    }

}
