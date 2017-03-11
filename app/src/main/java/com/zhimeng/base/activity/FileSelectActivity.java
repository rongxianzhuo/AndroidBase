package com.zhimeng.base.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.zhimeng.base.R;
import com.zhimeng.base.base.BaseActivity;
import com.zhimeng.base.base.BaseContext;
import com.zhimeng.base.base.BaseFragment;
import com.zhimeng.base.util.BitmapUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Stack;

public class FileSelectActivity extends BaseActivity {

    private static class FileAdapter extends RecyclerView.Adapter<FileAdapter.ViewHolder> {

        class ViewHolder extends RecyclerView.ViewHolder {

            private View view;
            private TextView name;
            private SimpleDraweeView image;

            public ViewHolder(View itemView) {
                super(itemView);
                view = itemView;
                image = (SimpleDraweeView) view.findViewById(R.id.zhimeng_thumbnail_1234);
                name = (TextView) view.findViewById(R.id.zhimeng_file_name_7831);
            }
        }

        private Stack<ArrayList<File>> stack = new Stack<>();
        private FileSelectActivity activity;

        public FileAdapter(FileSelectActivity activity) {
            this.activity = activity;
            stack.push(removeEmptyDirectory(Environment.getExternalStorageDirectory().listFiles()));
        }

        @Override
        public FileAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new FileAdapter.ViewHolder(LayoutInflater.from(parent.getContext()) .inflate(R.layout.zhimeng_item_file, parent, false));
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    File file = stack.peek().get(holder.getAdapterPosition());
                    if (file.isFile()) {
                        activity.setResult(file);
                        activity.finish();
                        return;
                    }
                    stack.push(removeEmptyDirectory(file.listFiles()));
                    notifyDataSetChanged();
                }
            });
            File file = stack.peek().get(position);
            holder.name.setText(file.getName());
            if (file.isDirectory()) {
                holder.image.setImageURI(Uri.parse("res://" + activity.getPackageName() + "/" + R.drawable.ic_folder_black_24dp));
                holder.image.setColorFilter(Color.parseColor("#FFC107"));
            }
            else {
                if (file.getName().toLowerCase().endsWith(".png") || file.getName().toLowerCase().endsWith(".jpg")) {
                    File tf = BitmapUtil.getThumbnail(activity.getContentResolver(), file.getAbsolutePath());
                    if (tf == null || !tf.exists()) {
                        holder.image.setImageURI(Uri.parse("res://" + activity.getPackageName() + "/" + R.drawable.ic_insert_photo_black_24dp));
                        holder.image.setColorFilter(Color.parseColor("#F44336"));
                    }
                    else {
                        holder.image.clearColorFilter();
                        holder.image.setImageURI(Uri.parse("file://" + tf.getAbsolutePath()));
                    }
                }
                else {
                    holder.image.setImageURI(Uri.parse("res://" + activity.getPackageName() + "/" + R.drawable.ic_insert_drive_file_black_24dp));
                    holder.image.setColorFilter(Color.parseColor("#8BC34A"));
                }
            }
        }

        @Override
        public int getItemCount() {
            return stack.peek().size();
        }

        private static ArrayList<File> removeEmptyDirectory(File[] files) {
            ArrayList<File> list = new ArrayList<>();
            if (files == null) return list;
            for (File f : files) if (!f.isHidden()) if (f.isFile() && isCorrectFile(f) || f.isDirectory() && f.listFiles().length > 0) list.add(f);
            return list;
        }

        public boolean back() {
            if (stack.size() == 1) return false;
            stack.pop();
            notifyDataSetChanged();
            return true;
        }

        private static boolean isCorrectFile(File file) {
            for (String s : FileSelectActivity.limit) if (file.getName().endsWith(s)) return true;
            return false;
        }

    }

    public static String[] limit = null;
    private FileAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_select);
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        if (getSupportActionBar() == null) {
            setSupportActionBar(toolbar);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
        else toolbar.setVisibility(View.GONE);
        RecyclerView fileList = (RecyclerView) findViewById(R.id.zhimeng_file_list_4831);
        adapter = new FileAdapter(this);
        fileList.setLayoutManager(new LinearLayoutManager(this));
        fileList.setHasFixedSize(true);
        fileList.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        if (!adapter.back()) finish();
    }

    public static void startActivity(BaseActivity context, String[] limit, BaseContext.OnResultListener listener) {
        FileSelectActivity.limit = limit;
        startActivity(context, FileSelectActivity.class, null, listener);
    }

    public static void startActivity(BaseFragment context, String[] limit, BaseContext.OnResultListener listener) {
        FileSelectActivity.limit = limit;
        startActivity(context, FileSelectActivity.class, null, listener);
    }
}
