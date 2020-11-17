package com.android.custom.imagerecyclerview.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.custom.imagerecyclerview.R;
import com.android.custom.imagerecyclerview.adapter.GridImageAdapter;
import com.android.custom.imagerecyclerview.manager.FullyGridLayoutManager;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.permissions.Permission;
import com.luck.picture.lib.permissions.RxPermissions;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.functions.Consumer;

import static android.app.Activity.RESULT_OK;

/**
 * @ProjectName: CustomPictureSelect
 * @Package: com.android.custom.imagerecyclerview.view
 * @ClassName: ImageRecyclerView
 * @Author: 1984629668@qq.com
 * @CreateDate: 2020/11/17 9:26
 */
public class ImageRecyclerView extends RecyclerView implements GridImageAdapter.OnItemClickListener {

    private RecyclerView mRecyclerView;
    private GridImageAdapter adapter;
    private int maxSelectNum = 9;
    private List<LocalMedia> selectList = new ArrayList<>();
    private PopupWindow pop;
    private Activity activity;
    private boolean isShowDelete;

    public ImageRecyclerView(@NonNull Context context) {
        this(context, null);
    }

    public ImageRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImageRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        activity = (Activity) context;
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ImageRecyclerView);
        isShowDelete = typedArray.getBoolean(R.styleable.ImageRecyclerView_isShowDelete, true);
        typedArray.recycle();
        initWidget();
    }

    private void initWidget() {
        mRecyclerView = this;
        FullyGridLayoutManager manager = new FullyGridLayoutManager(activity, 3, GridLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
        adapter = new GridImageAdapter(activity, onAddPicClickListener, isShowDelete);
        adapter.setList(selectList);
        adapter.setSelectMax(maxSelectNum);
        mRecyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
    }

    private GridImageAdapter.onAddPicClickListener onAddPicClickListener = new GridImageAdapter.onAddPicClickListener() {
        @SuppressLint("CheckResult")
        @Override
        public void onAddPicClick() {
            //获取写的权限
            RxPermissions rxPermission = new RxPermissions(activity);
            rxPermission.requestEach(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .subscribe(new Consumer<Permission>() {
                        @Override
                        public void accept(Permission permission) {
                            if (permission.granted) {// 用户已经同意该权限
                                //第一种方式，弹出选择和拍照的dialog
                                showPop();
                                //第二种方式，直接进入相册，但是 是有拍照得按钮的
                                //showAlbum();
                            } else {
                                Toast.makeText(activity, "拒绝", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    };

    private void showAlbum() {
        //参数很多，根据需要添加
        PictureSelector.create(activity)
                .openGallery(PictureMimeType.ofImage())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                .maxSelectNum(maxSelectNum)// 最大图片选择数量
                .minSelectNum(1)// 最小选择数量
                .imageSpanCount(4)// 每行显示个数
                .selectionMode(PictureConfig.MULTIPLE)// 多选 or 单选PictureConfig.MULTIPLE : PictureConfig.SINGLE
                .previewImage(true)// 是否可预览图片
                .isCamera(true)// 是否显示拍照按钮
                .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                //.setOutputCameraPath("/CustomPath")// 自定义拍照保存路径
                .enableCrop(true)// 是否裁剪
                .compress(true)// 是否压缩
                //.sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
                .glideOverride(160, 160)// glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
                .withAspectRatio(1, 1)// 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                //.selectionMedia(selectList)// 是否传入已选图片
                //.previewEggs(false)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中)
                //.cropCompressQuality(90)// 裁剪压缩质量 默认100
                //.compressMaxKB()//压缩最大值kb compressGrade()为Luban.CUSTOM_GEAR有效
                //.compressWH() // 压缩宽高比 compressGrade()为Luban.CUSTOM_GEAR有效
                //.cropWH()// 裁剪宽高比，设置如果大于图片本身宽高则无效
                .rotateEnabled(false) // 裁剪是否可旋转图片
                //.scaleEnabled()// 裁剪是否可放大缩小图片
                //.recordVideoSecond()//录制视频秒数 默认60s
                .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
    }

    private void showPop() {
        View bottomView = View.inflate(activity, R.layout.layout_bottom_dialog, null);
        TextView mAlbum = bottomView.findViewById(R.id.tv_album);
        TextView mCamera = bottomView.findViewById(R.id.tv_camera);
        TextView mCancel = bottomView.findViewById(R.id.tv_cancel);

        pop = new PopupWindow(bottomView, -1, -2);
        pop.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pop.setOutsideTouchable(true);
        pop.setFocusable(true);
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.alpha = 0.5f;
        activity.getWindow().setAttributes(lp);
        pop.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
                lp.alpha = 1f;
                activity.getWindow().setAttributes(lp);
            }
        });
        pop.setAnimationStyle(R.style.main_menu_photo_anim);
        pop.showAtLocation(activity.getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int id = view.getId();
                if (id == R.id.tv_album) {//相册
                    PictureSelector.create(activity)
                            .openGallery(PictureMimeType.ofImage())
                            .maxSelectNum(maxSelectNum)
                            .minSelectNum(1)
                            .imageSpanCount(4)
                            .selectionMode(PictureConfig.MULTIPLE)
                            .forResult(PictureConfig.CHOOSE_REQUEST);
                } else if (id == R.id.tv_camera) {//拍照
                    PictureSelector.create(activity)
                            .openCamera(PictureMimeType.ofImage())
                            .forResult(PictureConfig.CHOOSE_REQUEST);
                } else if (id == R.id.tv_cancel) {//取消
                    closePopupWindow();
                }
                closePopupWindow();
            }
        };

        mAlbum.setOnClickListener(clickListener);
        mCamera.setOnClickListener(clickListener);
        mCancel.setOnClickListener(clickListener);
    }

    private void closePopupWindow() {
        if (pop != null && pop.isShowing()) {
            pop.dismiss();
            pop = null;
        }
    }

    public List<LocalMedia> getSelectList() {
        return selectList;
    }

    public void setList(List<LocalMedia> list) {
        selectList.addAll(list);
        adapter.notifyDataSetChanged();
        adapter.setOnItemClickListener(this);
    }

    public void setShowDelete(boolean showDelete) {
        isShowDelete = showDelete;
        initWidget();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        List<LocalMedia> images;
        if (resultCode == RESULT_OK) {
            if (requestCode == PictureConfig.CHOOSE_REQUEST) {// 图片选择结果回调
                images = PictureSelector.obtainMultipleResult(data);
                //selectList.addAll(images);
                ArrayList<String> list = new ArrayList<>();
                if (images != null && images.size() > 0) {
                    for (LocalMedia localMedia : images) {
                        if (localMedia != null) {
                            String path = localMedia.getPath();
                            if (!TextUtils.isEmpty(path)) {
                                list.add(path);
                            }
                        }
                    }
                }
                if (uploadListener != null) {
                    uploadListener.uploadFilesListener(list);
                }

                //selectList = PictureSelector.obtainMultipleResult(data);

                // 例如 LocalMedia 里面返回三种path
                // 1.media.getPath(); 为原图path
                // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true
                // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
                // 如果裁剪并压缩了，以取压缩路径为准，因为是先裁剪后压缩的
                /*adapter.setList(selectList);
                adapter.notifyDataSetChanged();
                adapter.setOnItemClickListener(this);*/
            }
        }
    }

    @Override
    public void onItemClick(int position, View v) {
        if (selectList.size() > 0) {
            LocalMedia media = selectList.get(position);
            String pictureType = media.getPictureType();
            int mediaType = PictureMimeType.pictureToVideo(pictureType);
            switch (mediaType) {
                case 1:
                    // 预览图片 可自定长按保存路径
                    //PictureSelector.create(MainActivity.this).externalPicturePreview(position, "/custom_file", selectList);
                    PictureSelector.create(activity).externalPicturePreview(position, selectList);
                    if (pictureClickListener != null) {
                        pictureClickListener.clickItem(position, selectList);
                    }
                    break;
                case 2:
                    // 预览视频
                    PictureSelector.create(activity).externalPictureVideo(media.getPath());
                    break;
                case 3:
                    // 预览音频
                    PictureSelector.create(activity).externalPictureAudio(media.getPath());
                    break;
            }
        }
    }

    public ArrayList<String> getImagesUrl() {
        ArrayList<String> list = new ArrayList<>();
        if (selectList != null && selectList.size() > 0) {
            for (LocalMedia localMedia : selectList) {
                if (localMedia != null) {
                    String path = localMedia.getPath();
                    if (!TextUtils.isEmpty(path)) {
                        list.add(path);
                    }
                }
            }
        }
        return list;
    }

    private OnPictureClickListener pictureClickListener;

    public void setPictureClickListener(OnPictureClickListener pictureClickListener) {
        this.pictureClickListener = pictureClickListener;
    }

    public interface OnPictureClickListener {
        void clickItem(int position, List<LocalMedia> list);
    }

    private OnUploadListener uploadListener;

    public void setUploadListener(OnUploadListener uploadListener) {
        this.uploadListener = uploadListener;
    }

    public interface OnUploadListener {
        void uploadFilesListener(List<String> list);

        void uploadFileListener(String path);
    }
}
