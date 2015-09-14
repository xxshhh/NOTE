package net.xuwenhui.note.bean;

/**
 * 记事本包装类
 * Created by xwh on 2015/8/26.
 */
public class Note {

    private int _id;
    private long date;
    private String content;

    public Note() {

    }

    public Note(int _id, long date, String content) {
        this._id = _id;
        this.date = date;
        this.content = content;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
