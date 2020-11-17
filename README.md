引入module

allprojects {

		repositories {

			maven { url 'https://jitpack.io' }

		}

	}

implementation 'com.github.gyadministrator:CustomPictureSelect:1.0'

主要代码
package com.android.custom.picture;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.android.custom.imagerecyclerview.view.ImageRecyclerView;
import com.luck.picture.lib.entity.LocalMedia;

import java.util.ArrayList;
import java.util.List;

public class LocalActivity extends AppCompatActivity implements ImageRecyclerView.OnPictureClickListener {
    private ImageRecyclerView imageRecyclerView;
    private ArrayList<String> list = new ArrayList<>();
    private static final String TAG = "LocalActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local);
        initView();
        initData();
    }

    private void initData() {
        list.add("https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=1926976273,1612334744&fm=26&gp=0.jpg");
        list.add("https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=2668764066,1478918522&fm=26&gp=0.jpg");
        list.add("https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=3684317898,3304187139&fm=26&gp=0.jpg");
        ArrayList<LocalMedia> arrayList = new ArrayList<>();
        for (String s : list) {
            LocalMedia localMedia = new LocalMedia();
            localMedia.setPath(s);
            arrayList.add(localMedia);
        }
        imageRecyclerView.setList(arrayList);
        imageRecyclerView.setShowDelete(true);
        imageRecyclerView.setPictureClickListener(this);
        Log.e(TAG, "initData: "+imageRecyclerView.getImagesUrl().toString());
    }

    private void initView() {
        imageRecyclerView = findViewById(R.id.recycler);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (imageRecyclerView != null) {
            if (data == null) return;
            imageRecyclerView.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void clickItem(int position, List<LocalMedia> list) {
        Log.e(TAG, "clickItem: " + position + "");
        Log.e(TAG, "clickItem: "+list.get(position).getPath());
    }
}
