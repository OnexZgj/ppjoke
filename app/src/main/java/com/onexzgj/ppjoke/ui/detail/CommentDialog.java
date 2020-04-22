package com.onexzgj.ppjoke.ui.detail;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.arch.core.executor.ArchTaskExecutor;

import com.mooc.libcommon.global.AppGlobals;
import com.mooc.libcommon.utils.PixUtils;
import com.mooc.libcommon.view.PPEditTextView;
import com.mooc.libcommon.view.ViewHelper;
import com.mooc.libnetwork.ApiResponse;
import com.mooc.libnetwork.ApiService;
import com.mooc.libnetwork.JsonCallback;
import com.onexzgj.ppjoke.R;
import com.onexzgj.ppjoke.model.Comment;
import com.onexzgj.ppjoke.ui.login.UserManager;

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
        this.itemId = getArguments().getLong(KEY_ITEM_ID);
        view.findViewById(R.id.comment_delete).setOnClickListener(this);
        view.findViewById(R.id.comment_video).setOnClickListener(this);
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

        if (TextUtils.isEmpty(inputView.getText().toString())){
            return;
        }

        publish();
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


}
