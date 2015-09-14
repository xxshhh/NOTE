package net.xuwenhui.note.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

/**
 * 自定义的对话框工具类
 * Created by xwh on 2015/9/3.
 */
public class MyDialogUtils {

    public interface OnClickYesListener {
        public void onClickYes();
    }

    public interface OnClickNoListener {
        public void onClickNo();
    }

    public static void showMyDialog(Context context, String message,
                                    final OnClickYesListener listenerYes,
                                    final OnClickNoListener listenerNo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message);
        builder.setTitle("提示");
        builder.setPositiveButton("确定", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (listenerYes != null) {
                    listenerYes.onClickYes();
                }
            }
        });
        builder.setNegativeButton("取消", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (listenerNo != null) {
                    listenerNo.onClickNo();
                }

            }
        });

        // 控制这个dialog可不可以按返回键，true为可以，false为不可以
        builder.setCancelable(false);
        // 显示dialog
        builder.create().show();
    }

}

