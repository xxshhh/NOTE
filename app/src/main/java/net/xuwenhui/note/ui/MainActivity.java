package net.xuwenhui.note.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;

import net.xuwenhui.note.R;
import net.xuwenhui.note.adapter.MyAdapter;
import net.xuwenhui.note.bean.Note;
import net.xuwenhui.note.db.DBManager;
import net.xuwenhui.note.utils.MyDialogUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 主界面
 * Created by xwh on 2015/8/26.
 */
public class MainActivity extends Activity {

    private ListView mListView;
    private Button mBtnNew, mBtnCancel, mBtnChoice, mBtnDelete;
    private RelativeLayout mLayoutHide;

    private DBManager mDBManager;
    private List<Note> mListData;
    private MyAdapter mMyAdapter;

    // 本地绘图、照片、录音资源的存储目录
    public static String LOCAL_RESOURCE_CATALOG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDBManager = new DBManager(this);

        initView();
        initData();
        initListener();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDBManager.closeDB();
    }

    private void initView() {
        mListView = (ListView) findViewById(R.id.list_note);
        mBtnNew = (Button) findViewById(R.id.btn_new);
        mLayoutHide = (RelativeLayout) findViewById(R.id.layout_hide);
        mBtnCancel = (Button) findViewById(R.id.btn_cancel);
        mBtnChoice = (Button) findViewById(R.id.btn_choice);
        mBtnDelete = (Button) findViewById(R.id.btn_delete);
    }

    private void initData() {
        try {
            if (isHasSDCard()) {
                LOCAL_RESOURCE_CATALOG = Environment.getExternalStorageDirectory()
                        .getCanonicalPath() + "/net.xuwenhui.note/resource/";
            } else {
                LOCAL_RESOURCE_CATALOG = Environment.getRootDirectory()
                        .getCanonicalPath() + "/net.xuwenhui.note/resource/";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 创建路径
        File file = new File(LOCAL_RESOURCE_CATALOG);
        if (!file.exists()) {
            file.mkdirs();
        }

        mListData = mDBManager.queryAll();
        mMyAdapter = new MyAdapter(this, mListData);
        mListView.setAdapter(mMyAdapter);
}

    private void initListener() {
        mBtnNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NoteActivity.class);
                startActivity(intent);
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, NoteActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("_id", mListData.get(position).get_id());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                mLayoutHide.setVisibility(View.VISIBLE);
                mMyAdapter.isCheckBoxVisible = true;
                mMyAdapter.notifyDataSetChanged();
                return true;
            }
        });

        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLayoutHide.setVisibility(View.GONE);
                mMyAdapter.isCheckBoxVisible = false;
                for (int i = 0; i < mMyAdapter.getCount(); i++) {
                    mMyAdapter.isCheckedGroup[i] = false;
                }
                mMyAdapter.notifyDataSetChanged();
            }
        });

        mBtnChoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < mMyAdapter.getCount(); i++) {
                    mMyAdapter.isCheckedGroup[i] = true;
                }
                mMyAdapter.notifyDataSetChanged();
            }
        });

        mBtnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean flag = false;
                for (boolean b : mMyAdapter.isCheckedGroup) {
                    if (b) {
                        flag = true;
                    }
                }
                if (flag) {
                    MyDialogUtils.showMyDialog(MainActivity.this, "是否确定删除？",
                            new MyDialogUtils.OnClickYesListener() {
                                @Override
                                public void onClickYes() {
                                    List<Note> delete_list = new ArrayList<>();
                                    for (int i = 0; i < mMyAdapter.getCount(); i++) {
                                        if (mMyAdapter.isCheckedGroup[i]) {
                                            delete_list.add((Note) mMyAdapter.getItem(i));
                                        }
                                    }
                                    mDBManager.delete(delete_list);
                                    mLayoutHide.setVisibility(View.GONE);
                                    initData();
                                }
                            }, null);
                }

            }
        });
    }

    /**
     * 判断手机是否有内存卡
     */
    private boolean isHasSDCard() {
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }
}
