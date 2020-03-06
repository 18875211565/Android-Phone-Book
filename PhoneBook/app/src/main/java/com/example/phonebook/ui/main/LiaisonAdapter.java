package com.example.phonebook.ui.main;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.SimpleCursorTreeAdapter;
import android.widget.TextView;

import com.example.phonebook.DataBaseOperation;
import com.example.phonebook.R;
import com.zxy.tiny.Tiny;
import com.zxy.tiny.callback.FileWithBitmapCallback;

import org.w3c.dom.Text;

import java.io.File;
import java.util.HashSet;

public class LiaisonAdapter extends SimpleCursorTreeAdapter {
    private DataBaseOperation operation;
    public HashSet<String> Selected = new HashSet<String>();
    public boolean CheckBoxVisible = false;
    public String searchString = "";
    public LiaisonAdapter(Cursor cursor, Context context, int groupLayout, int childLayout, String[] groupfrom, int[] groupTo, String[] childrenFrom, int[] childrenTo) {
        super(context, cursor, groupLayout, groupfrom, groupTo, childLayout, childrenFrom, childrenTo);
        operation = new DataBaseOperation(context, "Record");
    }

    @Override
    protected Cursor getChildrenCursor(Cursor groupCursor){
        Cursor childCursor = operation.fetchChildren(groupCursor.getString(groupCursor.getColumnIndex("name")), searchString);
        childCursor.moveToFirst();
        return childCursor;
    }

    @Override
    protected void bindChildView(View view, Context context, Cursor ICursor, boolean IsLastChild)
    {
        super.bindChildView(view, context, ICursor, IsLastChild);
        RadioButton checkBox = view.findViewById(R.id.multiChoiceBox);
        if (CheckBoxVisible) {
            TextView text = view.findViewById(R.id.liaisonNumber);
            if (Selected.contains(text.getText().toString())) checkBox.setChecked(true); else checkBox.setChecked(false);
            checkBox.setVisibility(View.VISIBLE);
        }
        else
            checkBox.setVisibility(View.INVISIBLE);
        final ImageView imageView = view.findViewById(R.id.header);
        TextView textView = view.findViewById(R.id.file);
        String path = textView.getText().toString();
        /*
        if (!path.equals("")){
            Tiny.FileCompressOptions options = new Tiny.FileCompressOptions();
            Tiny.getInstance().source(Uri.parse(path)).asFile().withOptions(options).compress(new FileWithBitmapCallback() {
                @Override
                public void callback(boolean isSuccess, Bitmap bitmap, String outfile, Throwable t) {
                    saveImageToServer(bitmap, outfile, imageView);//显示图片到imgView上
                }
            });
        }
        else imageView.setImageResource(R.drawable.header);*/
    }

    private void saveImageToServer(final Bitmap bitmap, String outfile, ImageView imageView) {
        File file = new File(outfile);
        // TODO: 2018/12/4  这里就可以将图片文件 file 上传到服务器,上传成功后可以将bitmap设置给你对应的图片展示
        imageView.setImageBitmap(bitmap);
    }
}
