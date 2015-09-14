package net.xuwenhui.note.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import net.xuwenhui.note.R;
import net.xuwenhui.note.bean.Note;
import net.xuwenhui.note.ui.MainActivity;
import net.xuwenhui.note.utils.MyDateUtils;

import java.util.List;

/**
 * 自定义的适配器
 * Created by xwh on 2015/9/4.
 */
public class MyAdapter extends BaseAdapter {
    private List<Note> mListData;
    private Context context;
    public boolean isCheckBoxVisible;
    public boolean[] isCheckedGroup;

    public MyAdapter(Context context, List<Note> list) {
        this.context = context;
        mListData = list;
        isCheckBoxVisible = false;
        isCheckedGroup = new boolean[list.size()];
    }

    @Override
    public int getCount() {
        return mListData.size();
    }

    @Override
    public Object getItem(int position) {
        return mListData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_note_item, null);
            viewHolder = new ViewHolder();
            viewHolder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
            viewHolder.tv_date = (TextView) convertView.findViewById(R.id.tv_date);
            viewHolder.check_item = (CheckBox) convertView.findViewById(R.id.check_item);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String patternStr = MainActivity.LOCAL_RESOURCE_CATALOG + "\\d*\\.\\w{3}";
        viewHolder.tv_content.setText(mListData.get(position).getContent().replaceAll(patternStr, "[图片]"));
        viewHolder.tv_date.setText(MyDateUtils.convertForList(mListData.get(position).getDate()));
        viewHolder.check_item.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isCheckedGroup[position] = isChecked;
            }
        });
        viewHolder.check_item.setChecked(isCheckedGroup[position]);
        if (isCheckBoxVisible) {
            viewHolder.check_item.setVisibility(View.VISIBLE);
        } else {
            viewHolder.check_item.setVisibility(View.GONE);
        }
        return convertView;
    }

    public static class ViewHolder {
        public TextView tv_content;
        public TextView tv_date;
        public CheckBox check_item;
    }
}
