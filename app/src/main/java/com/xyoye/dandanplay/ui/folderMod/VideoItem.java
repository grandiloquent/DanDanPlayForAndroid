package com.xyoye.dandanplay.ui.folderMod;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xyoye.core.interf.AdapterItem;
import com.xyoye.core.utils.StringUtils;
import com.xyoye.dandanplay.R;
import com.xyoye.dandanplay.bean.VideoBean;
import com.xyoye.dandanplay.event.OpenDanmuSettingEvent;
import com.xyoye.dandanplay.event.OpenVideoEvent;
import com.xyoye.dandanplay.utils.BitmapUtil;
import com.xyoye.dandanplay.utils.TimeUtil;
import com.xyoye.dandanplay.weight.CircleImageView;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

import butterknife.BindView;

/**
 * Created by YE on 2018/6/30 0030.
 */


public class VideoItem implements AdapterItem<VideoBean> {
    @BindView(R.id.cover_iv)
    ImageView coverIv;
    @BindView(R.id.duration_tv)
    TextView durationTv;
    @BindView(R.id.title_tv)
    TextView titleTv;
    @BindView(R.id.danmu_tips_iv)
    ImageView danmuTipsIv;
    @BindView(R.id.danmu_setting_rl)
    RelativeLayout danmuSetting;

    private View mView;

    @Override
    public int getLayoutResId() {
        return R.layout.item_video;
    }

    @Override
    public void initItemViews(View itemView) {
        mView = itemView;
    }

    @Override
    public void onSetViews() {

    }

    @Override
    public void onUpdateViews(final VideoBean model, final int position) {
        String videoName = model.getVideoName();
        int last = videoName.lastIndexOf(".");
        videoName = videoName.substring(0, last);
        titleTv.setText(videoName);

        long duration = Long.parseLong(model.getVideoDuration());
        durationTv.setText(TimeUtil.formatDuring(duration));

        Bitmap bitmap = BitmapUtil.base64ToBitmap(model.getVideoCover());
        coverIv.setImageBitmap(bitmap);

        if (StringUtils.isEmpty(model.getDanmuPath())){
            danmuTipsIv.setImageResource(R.mipmap.ic_danmu_inexist);
        }else {
            danmuTipsIv.setImageResource(R.mipmap.ic_danmu_exist);
        }

        danmuSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenDanmuSettingEvent event = new OpenDanmuSettingEvent(model.getVideoPath(), position);
                EventBus.getDefault().post(event);
            }
        });

        mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenVideoEvent event = new OpenVideoEvent(model);
                EventBus.getDefault().post(event);
            }
        });
    }
}