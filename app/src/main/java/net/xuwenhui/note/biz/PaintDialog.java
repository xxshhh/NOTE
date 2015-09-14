package net.xuwenhui.note.biz;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;

import net.xuwenhui.note.R;

/**
 * 对话框式的绘图界面
 * Created by xwh on 2015/9/5.
 */
public class PaintDialog extends Dialog {
    private Context mContext;
    private PaintDialogListener mPaintDialogListener;

    private PaintView mPaintView;
    private RelativeLayout mRelativeLayout;
    private Button mBtnOK, mBtnClear, mBtnCancel;

    public PaintDialog(Context context, PaintDialogListener paintDialogListener) {
        super(context);
        this.mContext = context;
        this.mPaintDialogListener = paintDialogListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //无标题
        setContentView(R.layout.paint);

        initView();
        initData();
        initListener();
    }

    private void initView() {
        mRelativeLayout = (RelativeLayout) findViewById(R.id.layout_paint);
        mBtnOK = (Button) findViewById(R.id.btn_paint_ok);
        mBtnClear = (Button) findViewById(R.id.btn_paint_clear);
        mBtnCancel = (Button) findViewById(R.id.btn_paint_cancel);
    }

    private void initData() {
        mPaintView = new PaintView(mContext, 540, 770);
        mRelativeLayout.addView(mPaintView);
        mPaintView.requestFocus();
    }

    private void initListener() {
        mBtnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPaintView.getPath().isEmpty()) {
                    return;
                }
                mPaintDialogListener.onPaintDone(mPaintView.getPaintBitmap());
                dismiss();
            }
        });

        mBtnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPaintView.clear();
            }
        });

        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });
    }

    /**
     * 绘图完成的回调接口
     */
    public interface PaintDialogListener {
        void onPaintDone(Bitmap bitmap);
    }
}
