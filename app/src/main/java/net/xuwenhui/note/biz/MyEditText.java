package net.xuwenhui.note.biz;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import net.xuwenhui.note.ui.MainActivity;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 自定义的文本框
 * Created by xwh on 2015/9/4.
 */
public class MyEditText extends EditText {

    public MyEditText(Context context) {
        super(context);
    }

    public MyEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 插入图片
     *
     * @param context
     * @param bitmap
     * @param path
     */
    public void insertImage(Context context, Bitmap bitmap, String path) {
        ImageSpan span = new ImageSpan(context, bitmap);
        SpannableString ss = new SpannableString(path);
        ss.setSpan(span, 0, path.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        append(" ");
        append(ss);
        append(" ");
        append("\n");
        setSelection(this.getText().toString().length());
        setSpanContent(context, this.getText().toString());
    }

    /**
     * 为文本框设置带图文混排的内容
     *
     * @param content
     */
    public void setSpanContent(Context context, String content) {
        // 触发事件的话，要加上下面这句话
        setMovementMethod(LinkMovementMethod.getInstance());
        String patternStr = MainActivity.LOCAL_RESOURCE_CATALOG + "\\d*\\.\\w{3}";
        Pattern pattern = Pattern.compile(patternStr);
        Matcher m = pattern.matcher(content);
        SpannableString ss = new SpannableString(content);
        while (m.find()) {
            Bitmap bmp = BitmapFactory.decodeFile(m.group());
            ImageSpan imgSpan = new ImageSpan(context, bmp);
            ss.setSpan(imgSpan, m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            // 点击触发事件
            ss.setSpan(new RecordClickPlay(m.group()), m.start(), m.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        this.setText(ss);
    }

    /**
     * 实现录音图片点击播放的类
     */
    class RecordClickPlay extends ClickableSpan {
        private String m3gpPath;

        public RecordClickPlay(String path) {
            this.m3gpPath = path.substring(0, path.length() - 3) + "3gp";
        }

        @Override
        public void onClick(View widget) {
            clearFocus();
            MediaPlayer mPlayer = new MediaPlayer();
            try {
                mPlayer.setDataSource(m3gpPath);
                mPlayer.prepare();
                mPlayer.start();
            } catch (IOException e) {
                Log.e("TAG", "播放失败");
            }
        }
    }
}
