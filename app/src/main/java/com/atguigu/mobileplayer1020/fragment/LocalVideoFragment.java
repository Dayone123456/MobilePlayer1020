package com.atguigu.mobileplayer1020.fragment;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.atguigu.mobileplayer1020.R;
import com.atguigu.mobileplayer1020.activity.SystemVideoPlayerActivity;
import com.atguigu.mobileplayer1020.adapter.LocalVideoAdapter;
import com.atguigu.mobileplayer1020.base.BaseFragment;
import com.atguigu.mobileplayer1020.bean.MediaItem;

import java.util.ArrayList;

/**
 * 作者：尚硅谷-杨光福 on 2017/1/6 16:46
 * 微信：yangguangfu520
 * QQ号：541433511
 * 作用：本地视频
 */
public class LocalVideoFragment extends BaseFragment {
    private ListView listview;
    private TextView tv_no_media;
    private LocalVideoAdapter adapter;

    /**
     * 数据集合
     */
    private ArrayList<MediaItem> mediaItems;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //设置适配器
            if(mediaItems != null && mediaItems.size() >0){

                //有数据
                //文本隐藏
                tv_no_media.setVisibility(View.GONE);
                adapter = new LocalVideoAdapter(mContext,mediaItems, true);
                //设置适配器
                listview.setAdapter(adapter);

            }else{
                //没有数据
                //文本显示
                tv_no_media.setVisibility(View.VISIBLE);
            }
        }
    };

    @Override
    public View initView() {
        Log.e("TAG", "本地视频ui初始化了。。");
        View view = View.inflate(mContext, R.layout.fragment_local_video, null);
        listview = (ListView) view.findViewById(R.id.listview);
        tv_no_media = (TextView) view.findViewById(R.id.tv_no_media);

        //设置item的监听
        listview.setOnItemClickListener(new MyOnItemClickListener());
        return view;
    }

    class MyOnItemClickListener implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent,View view, int position, long id) {
            MediaItem mediaItem = mediaItems.get(position);
//            Toast.makeText(mContext, "mediaItem=="+mediaItem.toString(), Toast.LENGTH_SHORT).show();
            //
            //1.调起系统的播放器播放视频--隐式意图
//            Intent intent = new Intent();
//            //第一参数：播放路径
//            //第二参数：路径对应的类型
//            intent.setDataAndType(Uri.parse(mediaItem.getData()),"video/*");
//            startActivity(intent);
            //2.调起自定义播放器
//            Intent intent = new Intent(mContext,SystemVideoPlayerActivity.class);
//            //第一参数：播放路径
//            //第二参数：路径对应的类型
//            intent.setDataAndType(Uri.parse(mediaItem.getData()),"video/*");//一个播放地址
//            startActivity(intent);
            //3.传递列表数据
            Intent intent = new Intent(mContext,SystemVideoPlayerActivity.class);

            Bundle bundle = new Bundle();
            //列表数据
            bundle.putSerializable("videolist",mediaItems);
            intent.putExtras(bundle);
            //传递点击的位置
            intent.putExtra("position",position);
            startActivity(intent);

        }
    }

    @Override
    public void initData() {
        super.initData();
        Log.e("TAG", "本地视频数据初始化了。。");
        //在子线程中加载视频
        getDataFromLocal();
    }

    /**
     * 子线程中得到视频
     */
    private void getDataFromLocal() {
        new Thread() {
            @Override
            public void run() {
                super.run();

                //初始化集合
                mediaItems = new ArrayList<MediaItem>();
                ContentResolver resolver = mContext.getContentResolver();
                //sdcard 的视频路径
                Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                String[] objs = {
                        MediaStore.Video.Media.DISPLAY_NAME,//在sdcard显示的视频名称
                        MediaStore.Video.Media.DURATION,//视频的时长,毫秒
                        MediaStore.Video.Media.SIZE,//文件大小-byte
                        MediaStore.Video.Media.DATA,//在sdcard的路径-播放地址
                        MediaStore.Video.Media.ARTIST//艺术家
                };
                Cursor cusor = resolver.query(uri, objs, null, null, null);
                if (cusor != null) {

                    while (cusor.moveToNext()) {

                        MediaItem mediaItem = new MediaItem();

                        //添加到集合中
                        mediaItems.add(mediaItem);//可以

                        String name = cusor.getString(0);
                        mediaItem.setName(name);
                        long duration = cusor.getLong(1);
                        mediaItem.setDuration(duration);
                        long size = cusor.getLong(2);
                        mediaItem.setSize(size);
                        String data = cusor.getString(3);//播放地址
                        mediaItem.setData(data);
                        String artist = cusor.getString(4);//艺术家
                        mediaItem.setArtist(artist);

                    }

                    cusor.close();
                }


                //发消息-切换到主线程
                handler.sendEmptyMessage(2);


            }
        }.start();

    }


    @Override
    public void onRefrshData() {
        super.onRefrshData();
//        Log.e("TAG","onHiddenChanged。。"+this.toString());
    }


}
