package net.xuwenhui.note.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import net.xuwenhui.note.bean.Note;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据库的“增删改查”
 * Created by xwh on 2015/8/26.
 */
public class DBManager {

    private SQLiteDatabase db;

    public DBManager(Context context) {
        DBHelper dbHelper = new DBHelper(context);
        db = dbHelper.getReadableDatabase();
    }

    /**
     * 添加一条数据
     *
     * @param note
     */
    public void add(Note note) {
        String sql = "insert into my_note values(null, ?, ?)";
        db.execSQL(sql, new Object[]{note.getDate(), note.getContent()});
    }

    /**
     * 删除若干条数据
     *
     * @param list
     */
    public void delete(List<Note> list) {
        for (Note note : list) {
            String sql = "delete from my_note where _id = ?";
            db.execSQL(sql, new Object[]{note.get_id()});
        }
    }

    /**
     * 更新一条数据
     *
     * @param note
     */
    public void update(Note note) {
        String sql = "update my_note set date = ? ,content = ? where _id = ?";
        db.execSQL(sql, new Object[]{note.getDate(), note.getContent(), note.get_id()});
    }

    /**
     * 查询所有
     *
     * @return
     */
    public List<Note> queryAll() {
        String sql = "select * from my_note order by date desc";
        Cursor cursor = db.rawQuery(sql, null);
        List<Note> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            int _id = cursor.getInt(cursor.getColumnIndex("_id"));
            long date = cursor.getLong(cursor.getColumnIndex("date"));
            String content = cursor.getString(cursor.getColumnIndex("content"));

            Note note = new Note(_id, date, content);
            list.add(note);
        }
        cursor.close();
        return list;
    }

    /**
     * 通过ID查询
     *
     * @param id
     * @return
     */
    public Note queryById(int id) {
        String sql = "select * from my_note where _id = ?";
        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(id)});
        Note note = null;
        while (cursor.moveToNext()) {
            int _id = cursor.getInt(cursor.getColumnIndex("_id"));
            long date = cursor.getLong(cursor.getColumnIndex("date"));
            String content = cursor.getString(cursor.getColumnIndex("content"));

            note = new Note(_id, date, content);
        }
        return note;
    }

    public void closeDB() {
        if (db != null && db.isOpen()) {
            db.close();
        }
        Log.e("TAG", "数据库关了！！！");
    }
}
