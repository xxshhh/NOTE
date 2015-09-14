package net.xuwenhui.note.ui;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import net.xuwenhui.note.R;
import net.xuwenhui.note.bean.Note;
import net.xuwenhui.note.biz.MyEditText;
import net.xuwenhui.note.biz.PaintDialog;
import net.xuwenhui.note.db.DBManager;
import net.xuwenhui.note.utils.MyBitmapUtils;
import net.xuwenhui.note.utils.MyDateUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

/**
 * 记事本编辑界面
 * Created by xwh on 2015/8/26.
 */
public class NoteActivity extends Activity {

    private TextView mTextViewDate;
    private MyEditText mMyEditText;
    private ImageButton mImgVoice, mImgGallery, mImgDrawing;
    private Button mBtnRecord;

    private DBManager mDBManager;
    private boolean mIsNewNote;
    private Note mNote;

    private MediaRecorder mRecorder;
    private String mFilePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        mDBManager = new DBManager(this);

        initView();
        initData();
        initListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDBManager.closeDB();
    }

    private void initView() {
        mTextViewDate = (TextView) findViewById(R.id.tv_date);
        mMyEditText = (MyEditText) findViewById(R.id.edit_content);
        mImgVoice = (ImageButton) findViewById(R.id.img_voice);
        mImgGallery = (ImageButton) findViewById(R.id.img_gallery);
        mImgDrawing = (ImageButton) findViewById(R.id.img_drawing);
        mBtnRecord = (Button) findViewById(R.id.btn_record);
    }

    private void initData() {
        Intent intent = getIntent();
        if (intent.getExtras() == null) { // 新增
            mIsNewNote = true;
            mTextViewDate.setText(MyDateUtils.convertForEdit(new Date().getTime()));
        } else { // 修改
            mIsNewNote = false;
            Bundle bundle = intent.getExtras();
            int id = (int) bundle.get("_id");
            mNote = mDBManager.queryById(id);
            mTextViewDate.setText(MyDateUtils.convertForEdit(mNote.getDate()));
            mMyEditText.setSpanContent(this, mNote.getContent());
        }
    }

    private void initListener() {
        mImgVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBtnRecord.setVisibility(View.VISIBLE);
            }
        });

        mImgGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 2);
            }
        });

        mImgDrawing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PaintDialog paintDialog = new PaintDialog(NoteActivity.this, new PaintDialog.PaintDialogListener() {
                    @Override
                    public void onPaintDone(Bitmap bitmap) {
                        if (bitmap != null) {
                            bitmap = MyBitmapUtils.resizeImage(bitmap, 160, 240);
                            mFilePath = MainActivity.LOCAL_RESOURCE_CATALOG + new Date().getTime() + ".jpg";
                            saveImage(bitmap);
                            mMyEditText.insertImage(NoteActivity.this, bitmap, mFilePath);
                        }
                    }
                });
                paintDialog.show();
            }
        });

        mBtnRecord.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == 0) {
                    mRecorder = new MediaRecorder();
                    mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                    mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                    mFilePath = MainActivity.LOCAL_RESOURCE_CATALOG + new Date().getTime() + ".3gp";
                    mRecorder.setOutputFile(mFilePath);
                    try {
                        mRecorder.prepare();
                    } catch (Exception e) {
                        Toast.makeText(NoteActivity.this, "请再试一次!", Toast.LENGTH_SHORT).show();
                    }
                    mRecorder.start();
                } else if (event.getAction() == 1) {
                    try {
                        mRecorder.stop();
                        mRecorder.reset();
                        mRecorder = null;
                        MediaPlayer mp = MediaPlayer.create(NoteActivity.this, Uri.parse(mFilePath));
                        int duration = mp.getDuration() / 1000 == 0 ? 1 : mp.getDuration() / 1000;
                        Bitmap bitmap = MyBitmapUtils.drawTextToBitmap(NoteActivity.this, duration + "s");
                        if (bitmap != null) {
                            mFilePath = mFilePath.substring(0, mFilePath.length() - 3) + "jpg";
                            saveImage(bitmap);
                            mMyEditText.insertImage(NoteActivity.this, bitmap, mFilePath);
                        }
                        mBtnRecord.setVisibility(View.GONE);
                    } catch (Exception e) {
                        Toast.makeText(NoteActivity.this, "请再试一次!", Toast.LENGTH_SHORT).show();
                    }
                }
                return false;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ContentResolver resolver = getContentResolver();
        if (resultCode == RESULT_OK) {
            if (requestCode == 2) {
                Bitmap bitmap = null;
                try {
                    bitmap = BitmapFactory.decodeStream(resolver.openInputStream(data.getData()));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                if (bitmap != null) {
                    bitmap = MyBitmapUtils.resizeImage(bitmap, 320, 480);
                    mFilePath = MainActivity.LOCAL_RESOURCE_CATALOG + new Date().getTime() + ".jpg";
                    saveImage(bitmap);
                    mMyEditText.insertImage(NoteActivity.this, bitmap, mFilePath);
                }
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            String nowContent = mMyEditText.getText().toString();
            if (mIsNewNote) { // 新增
                if (!nowContent.equals("")) {
                    mNote = new Note();
                    mNote.setDate(new Date().getTime());
                    mNote.setContent(nowContent);
                    mDBManager.add(mNote);
                }
            } else { // 修改
                if (!nowContent.equals(mNote.getContent())) {
                    mNote.setDate(new Date().getTime());
                    mNote.setContent(nowContent);
                    mDBManager.update(mNote);
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 保存图片资源到指定文件夹
     *
     * @param bmp
     */
    private void saveImage(Bitmap bmp) {
        File file = new File(mFilePath);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
