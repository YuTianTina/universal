/*
 * 杭州绿漫科技有限公司
 * Copyright (c) 16-6-27 下午12:44.
 */

package uama.hangzhou.image.photochoose;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import uama.hangzhou.image.R;


/**
 * 选择图片页面
 * Created by GuJiaJia
 */
public class PhotoWallActivity extends FragmentActivity {
    TextView albumTitleBarCancel;
    TextView albumTitleBarConfirm;
    private ArrayList<String> list;
    private GridView mPhotoWall;
    private PhotoWallAdapter adapter;
    private int maxNum;
    public static String MaxCounts = "MaxCounts";
    public static String SelectedCounts = "SelectCounts";
    private Toast mToast;
    private ArrayList<String> selectedImageList;
    private RelativeLayout title;
    public static String PHOTO_WALL_COLOR = "PHOTO_WALL_COLOR";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_wall);
        int titleColor = getIntent().getIntExtra(PHOTO_WALL_COLOR,-1);//必须为资源
        title = (RelativeLayout) findViewById(R.id.ll_comm_topbar);
        if(titleColor != -1){
            title.setBackgroundColor(titleColor);
        }
        albumTitleBarCancel = (TextView) findViewById(R.id.album_title_bar_cancel);
        albumTitleBarConfirm = (TextView) findViewById(R.id.album_title_bar_confirm);
        albumTitleBarCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        albumTitleBarConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> paths = adapter.getSelectList();
                Intent intent = new Intent();
                intent.putStringArrayListExtra("paths", paths);
                setResult(1991, intent);
                finish();
            }
        });
        maxNum = getIntent().getIntExtra(MaxCounts, 9);
        selectedImageList = getIntent().getStringArrayListExtra(SelectedCounts);
        mPhotoWall = (GridView) findViewById(R.id.photo_wall_grid);
        list = getImagePathsByContentProvider();
        adapter = new PhotoWallAdapter(this, list, selectedImageList, maxNum);
        mPhotoWall.setAdapter(adapter);
        setChooseCounts(selectedImageList.size());
    }

    @Override
    public void finish() {
        super.finish();
    }

    /**
     * 使用ContentProvider读取SD卡所有图片。
     */
    private ArrayList<String> getImagePathsByContentProvider() {
        Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String key_MIME_TYPE = MediaStore.Images.Media.MIME_TYPE;
        String key_DATA = MediaStore.Images.Media.DATA;

        ContentResolver mContentResolver = getContentResolver();

        // 只查询jpg和png的图片
        Cursor cursor = mContentResolver.query(mImageUri, new String[]{key_DATA},
                key_MIME_TYPE + "=? or " + key_MIME_TYPE + "=? or " + key_MIME_TYPE + "=?",
                new String[]{"image/jpg", "image/jpeg", "image/png"},
                MediaStore.Images.Media.DATE_MODIFIED);

        ArrayList list = null;
        if (cursor != null) {
            //从最新的图片开始读取.
            //当cursor中没有数据时，cursor.moveToLast()将返回false
            if (cursor.moveToLast()) {
                list = new ArrayList();

                while (true) {
                    // 获取图片的路径
                    String path = cursor.getString(0);
                    list.add(path);

                    if (!cursor.moveToPrevious()) {
                        break;
                    }
                }
            }
            cursor.close();
        }

        return list;
    }

    public void showErrorDialog() {
        if (mToast == null) {
            mToast.makeText(this, "最多可以选择" + maxNum + "张照片", Toast.LENGTH_SHORT).show();
        } else {
            mToast.cancel();
            mToast.makeText(this, "最多可以选择" + maxNum + "张照片", Toast.LENGTH_SHORT).show();
        }
    }

    //显示选择进度
    public void setChooseCounts(int chooseCounts) {
        albumTitleBarConfirm.setText("确定 (" + chooseCounts + "/" + maxNum + ")");
    }
}
