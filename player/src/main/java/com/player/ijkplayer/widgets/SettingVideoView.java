package com.player.ijkplayer.widgets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.player.ijkplayer.R;
import com.player.ijkplayer.media.IRenderView;
import com.player.ijkplayer.media.VideoInfoTrack;
import com.player.ijkplayer.utils.AnimHelper;
import com.player.ijkplayer.utils.TrackAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xyy on 2019/2/25.
 */

public class SettingVideoView extends LinearLayout implements View.OnClickListener{
    private LinearLayout speedCtrlLL;
    private TextView speed50Tv, speed75Tv,speed100Tv,speed125Tv, speed150Tv, speed200Tv;

    private RecyclerView audioRv;
    private RecyclerView subtitleRv;
    private LinearLayout audioRl;
    private LinearLayout subtitleRl;
    private TrackAdapter audioAdapter;
    private TrackAdapter subtitleAdapter;
    private RadioGroup mAspectRatioOptions;
    private boolean isExoPlayer = false;

    private List<VideoInfoTrack> audioTrackList = new ArrayList<>();
    private List<VideoInfoTrack> subtitleTrackList = new ArrayList<>();
    private SettingVideoListener listener;

    public SettingVideoView(Context context) {
        this(context, null);
    }

    @SuppressLint("ClickableViewAccessibility")
    public SettingVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        View.inflate(context, R.layout.view_setting_video, this);

        speedCtrlLL = this.findViewById(R.id.speed_ctrl_ll);
        speed50Tv = this.findViewById(R.id.speed50_tv);
        speed75Tv = this.findViewById(R.id.speed75_tv);
        speed100Tv = this.findViewById(R.id.speed100_tv);
        speed125Tv = this.findViewById(R.id.speed125_tv);
        speed150Tv = this.findViewById(R.id.speed150_tv);
        speed200Tv = this.findViewById(R.id.speed200_tv);
        audioRv = this.findViewById(R.id.audio_track_rv);
        subtitleRv = this.findViewById(R.id.subtitle_track_rv);
        audioRl = this.findViewById(R.id.audio_track_ll);
        subtitleRl = this.findViewById(R.id.subtitle_track_ll);
        mAspectRatioOptions = findViewById(R.id.aspect_ratio_group);

        mAspectRatioOptions.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.aspect_fit_parent) {
                    listener.setAspectRatio(IRenderView.AR_ASPECT_FIT_PARENT);
                } else if (checkedId == R.id.aspect_fit_screen) {
                    listener.setAspectRatio(IRenderView.AR_ASPECT_FILL_PARENT);
                } else if (checkedId == R.id.aspect_16_and_9) {
                    listener.setAspectRatio(IRenderView.AR_16_9_FIT_PARENT);
                } else if (checkedId == R.id.aspect_4_and_3) {
                    listener.setAspectRatio(IRenderView.AR_4_3_FIT_PARENT);
                }
            }
        });

        speed50Tv.setOnClickListener(this);
        speed75Tv.setOnClickListener(this);
        speed100Tv.setOnClickListener(this);
        speed125Tv.setOnClickListener(this);
        speed150Tv.setOnClickListener(this);
        speed200Tv.setOnClickListener(this);

        if (audioTrackList == null || audioTrackList.size() <= 0){
            audioTrackList = new ArrayList<>();
            audioRl.setVisibility(GONE);
        }
        if (subtitleTrackList == null || subtitleTrackList.size() <= 0){
            subtitleTrackList = new ArrayList<>();
            subtitleRl.setVisibility(GONE);
        }

        audioAdapter = new TrackAdapter(R.layout.item_video_track, audioTrackList);
        audioRv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        audioRv.setItemViewCacheSize(10);
        audioRv.setAdapter(audioAdapter);

        subtitleAdapter = new TrackAdapter(R.layout.item_video_track, subtitleTrackList);
        subtitleRv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        subtitleRv.setItemViewCacheSize(10);
        subtitleRv.setAdapter(subtitleAdapter);

        audioAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (isExoPlayer){
                    for (int i = 0; i < audioTrackList.size(); i++) {
                        if (i == position)
                            audioTrackList.get(i).setSelect(true);
                        else
                            audioTrackList.get(i).setSelect(false);
                    }
                    listener.selectTrack(-1, audioTrackList.get(position).getLanguage(), true);
                }else {
                    //deselectAll except position
                    for (int i = 0; i < audioTrackList.size(); i++) {
                        if (i == position)continue;
                        listener.deselectTrack(audioTrackList.get(i).getStream(), audioTrackList.get(i).getLanguage(), true);
                        audioTrackList.get(i).setSelect(false);
                    }
                    //select or deselect position
                    if (audioTrackList.get(position).isSelect()){
                        listener.deselectTrack(audioTrackList.get(position).getStream(), audioTrackList.get(position).getLanguage(), true);
                        audioTrackList.get(position).setSelect(false);
                    }else {
                        listener.selectTrack(audioTrackList.get(position).getStream(), audioTrackList.get(position).getLanguage(), true);
                        audioTrackList.get(position).setSelect(true);
                    }
                }
                audioAdapter.notifyDataSetChanged();
            }
        });

        subtitleAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                //ijk播放器暂不提供字幕流管理
                if (isExoPlayer){
                    for (int i = 0; i < subtitleTrackList.size(); i++) {
                        if (i == position)
                            subtitleTrackList.get(i).setSelect(true);
                        else
                            subtitleTrackList.get(i).setSelect(false);
                    }
                    listener.selectTrack(-1, subtitleTrackList.get(position).getLanguage(), false);
                    subtitleAdapter.notifyDataSetChanged();
                }

//                for (int i = 0; i < subtitleTrackList.size(); i++) {
//                    if (i == position)continue;
//                    listener.deselectTrack(subtitleTrackList.get(i).getStream(), subtitleTrackList.get(i).getLanguage(), false);
//                    subtitleTrackList.get(i).setSelect(false);

//                if (subtitleTrackList.get(position).isSelect()){
//                    listener.deselectTrack(subtitleTrackList.get(position).getStream(), subtitleTrackList.get(position).getLanguage(), false);
//                    subtitleTrackList.get(position).setSelect(false);
//                }else {
//                    listener.selectTrack(subtitleTrackList.get(position).getStream(), subtitleTrackList.get(position).getLanguage(), false);
//                    subtitleTrackList.get(position).setSelect(true);
//                }
            }
        });

        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        setPlayerSpeedView(3);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.speed50_tv){
            listener.setSpeed(0.5f);
            setPlayerSpeedView(1);
        }else if (id == R.id.speed75_tv){
            listener.setSpeed(0.75f);
            setPlayerSpeedView(2);
        }else if (id == R.id.speed100_tv){
            listener.setSpeed(1.0f);
            setPlayerSpeedView(3);
        }else if (id == R.id.speed125_tv){
            listener.setSpeed(1.25f);
            setPlayerSpeedView(4);
        }else if (id == R.id.speed150_tv){
            listener.setSpeed(1.5f);
            setPlayerSpeedView(5);
        }else if (id == R.id.speed200_tv){
            listener.setSpeed(2.0f);
            setPlayerSpeedView(6);
        }
    }

    public SettingVideoView setSettingListener(SettingVideoListener listener){
        this.listener = listener;
        return this;
    }

    public SettingVideoView setExoPlayerType(){
        this.isExoPlayer = true;
        return this;
    }

    public void setVideoTrackList(List<VideoInfoTrack> audioTrackList){
        this.audioTrackList.clear();
        this.audioTrackList.addAll(audioTrackList);
        this.audioAdapter.notifyDataSetChanged();
        this.audioRl.setVisibility(audioTrackList.size() < 1 ? GONE : VISIBLE);
    }

    public void setSubtitleTrackList(List<VideoInfoTrack> subtitleTrackList){
        this.subtitleTrackList.clear();
        this.subtitleTrackList.addAll(subtitleTrackList);
        this.subtitleAdapter.notifyDataSetChanged();
        this.subtitleRl.setVisibility(subtitleTrackList.size() < 1 ? GONE : VISIBLE);
    }

    public void setSpeedCtrlLLVis(boolean visibility){
        speedCtrlLL.setVisibility(visibility ? VISIBLE : GONE);
    }

    public void setPlayerSpeedView(int type){
        switch (type){
            case 1:
                speed50Tv.setBackgroundColor(Color.parseColor("#33ffffff"));
                speed75Tv.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.sel_item_background));
                speed100Tv.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.sel_item_background));
                speed125Tv.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.sel_item_background));
                speed150Tv.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.sel_item_background));
                speed200Tv.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.sel_item_background));
                break;
            case 2:
                speed50Tv.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.sel_item_background));
                speed75Tv.setBackgroundColor(Color.parseColor("#33ffffff"));
                speed100Tv.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.sel_item_background));
                speed125Tv.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.sel_item_background));
                speed150Tv.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.sel_item_background));
                speed200Tv.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.sel_item_background));
                break;
            case 3:
                speed50Tv.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.sel_item_background));
                speed75Tv.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.sel_item_background));
                speed100Tv.setBackgroundColor(Color.parseColor("#33ffffff"));
                speed125Tv.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.sel_item_background));
                speed150Tv.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.sel_item_background));
                speed200Tv.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.sel_item_background));
                break;
            case 4:
                speed50Tv.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.sel_item_background));
                speed75Tv.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.sel_item_background));
                speed100Tv.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.sel_item_background));
                speed125Tv.setBackgroundColor(Color.parseColor("#33ffffff"));
                speed150Tv.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.sel_item_background));
                speed200Tv.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.sel_item_background));
                break;
            case 5:
                speed50Tv.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.sel_item_background));
                speed75Tv.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.sel_item_background));
                speed100Tv.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.sel_item_background));
                speed125Tv.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.sel_item_background));
                speed150Tv.setBackgroundColor(Color.parseColor("#33ffffff"));
                speed200Tv.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.sel_item_background));
                break;
            case 6:
                speed50Tv.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.sel_item_background));
                speed75Tv.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.sel_item_background));
                speed100Tv.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.sel_item_background));
                speed125Tv.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.sel_item_background));
                speed150Tv.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.sel_item_background));
                speed200Tv.setBackgroundColor(Color.parseColor("#33ffffff"));
                break;
        }
    }

    public interface SettingVideoListener{
        void selectTrack(int streamId, String language, boolean isAudio);
        void deselectTrack(int streamId, String language, boolean isAudio);
        void setSpeed(float speed);
        void setAspectRatio(int type);
    }
}
