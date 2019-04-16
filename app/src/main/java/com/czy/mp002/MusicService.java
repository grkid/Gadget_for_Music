package com.czy.mp002;

import android.app.Service;
import android.content.Intent;
import android.media.Image;
import android.media.MediaPlayer;
import android.media.audiofx.Equalizer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.LinkedList;
import java.util.Random;

/**
 * Created by CZY on 2019/2/9.
 */

public class MusicService extends Service
{
   public final IBinder binder=new MyBinder();
   public class MyBinder extends Binder
   {
       MusicService getSetvice()
       {
           return MusicService.this;
       }
   }

   @Override
    public IBinder onBind(Intent intent)
   {
       return binder;
   }

   public static MediaPlayer mp=new MediaPlayer();
   public static boolean isAutoNextFlag=false;//是否自动跳转
   public static Equalizer equalizer;
   public int musicIndex=0;
   public Music MusicPlaying;//正在播放的歌曲
   public LinkedList<Music> MusicList;
   public SeekBar seekBar;

   public ImageButton playbutton;
   public ImageButton prebutton;
   public ImageButton nextbutton;

   short minEQlevel;
   short maxEQlevel;//记录两个极限（增益幅度？
    short brands;//记录可选定的频率数量

   private int equalizerMode=0;//均衡器模式。不准备写均衡器可视化界面，内部处理即可。
    //目前：0是标准模式（不变），1是低音模式（低音大幅增强），2是高音模式（高音大幅增强）。2019/2/12

    private int sequenceMode=0;
    //播放模式，0为列表播放，1为单曲循环，2为随机播放

    private Random random=new Random();
    //随机数生成器


    public void setSequenceMode()
    {
        sequenceMode=(sequenceMode+1)%3;
        SongStack.main.clear();
       if(sequenceMode==2)
       {
           SongStack.main.add(musicIndex);
           SongStack.location=0;
           Log.d("MusicService", "位置：0  双向链表初始化！");
       }


    }

    public int getSequenceMode()
    {
        return sequenceMode;
    }


    public void setequalizerMode(int i)
    {
        equalizerMode=i;
        if(i==0)
        {
            for(short j=0;j<brands;j++)
            {
                short mid=(short)((minEQlevel+maxEQlevel)/2);
                equalizer.setBandLevel(j,mid);
            }
        }
        else if(i==1)
        {
            for(short j=0;j<brands;j++)
            {
                short mid=(short)((minEQlevel+maxEQlevel)/2);
                equalizer.setBandLevel(j,mid);
            }
            for(short j=0;j<brands;j++)
            {
                if(equalizer.getCenterFreq(j)/1000<=300)
                {
                    equalizer.setBandLevel(j,maxEQlevel);
                }

            }
        }
        else if(i==2)
        {
            for(short j=0;j<brands;j++)
            {
                short mid=(short)((minEQlevel+maxEQlevel)/2);
                equalizer.setBandLevel(j,mid);
            }
            for(short j=0;j<brands;j++)
            {
                if(equalizer.getCenterFreq(j)/1000>=3600)
                {
                    equalizer.setBandLevel(j,maxEQlevel);
                }

            }
        }
    }

    public int getequalizerMode()
    {
        return equalizerMode;
    }




   public MusicService(LinkedList<Music> MusicList, SeekBar seekBar, ImageButton PlayButton, ImageButton PreButton, ImageButton NextButton)
   {
       //这个函数肯定在调用链上。
       try
       {
           this.MusicList=MusicList;
           musicIndex=0;
           if(musicIndex<=MusicList.size()) {
               MusicPlaying = this.MusicList.get(musicIndex);
               MusicPlaying.isPlaying=false;
               mp.setDataSource(MusicPlaying.path);
               mp.prepare();

               mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                   @Override
                   public void onCompletion(MediaPlayer mediaPlayer) {

                       //因为存在多个线程，这边存在时序问题。
                       //两条语句的顺序不能改变。
                       nextMusic();
                       isAutoNextFlag=true;
                   }
               });

               this.seekBar=seekBar;
               this.seekBar.setProgress(0);//设为0
               this.playbutton=PlayButton;
               this.prebutton=PreButton;
               this.nextbutton=NextButton;//按钮绑定上

               equalizer=new Equalizer(0,mp.getAudioSessionId());
               equalizer.setEnabled(true);
               minEQlevel=equalizer.getBandLevelRange()[0];
               maxEQlevel=equalizer.getBandLevelRange()[1];
               brands=equalizer.getNumberOfBands();
           }
       }
       catch(Exception e)
       {
           e.printStackTrace();
       }

       mp.setOnErrorListener(new MediaPlayer.OnErrorListener() {
           @Override
           public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {

               mediaPlayer.reset();
               nextMusic();
               return true;
           }
       });
   }

   public void playOrPause()
   {
       if(MusicPlaying.isPlaying)
       {
           mp.pause();
           MusicPlaying.isPlaying=false;

       }
       else
       {
           mp.start();
           MusicPlaying.isPlaying=true;

       }
   }

   public void updateUI(ImageButton Playbutton)
   {
       if(MusicPlaying.isPlaying)
           Playbutton.setImageResource(R.mipmap.ic_play_bar_btn_pause);
       else
           Playbutton.setImageResource(R.mipmap.ic_play_bar_btn_play);



   }
   public void nextMusic()
   {
       try {
           if (musicIndex >= MusicList.size())
               return;
           else {
               mp.stop();
               mp.reset();
               MusicPlaying.isPlaying = false;

               //musicIndex++;
               if(sequenceMode==0)
               {
                   musicIndex=(musicIndex+1)%MusicList.size();
               }
               else if(sequenceMode==1)
               {

               }
               else
               {
                   if(SongStack.location==SongStack.main.size()-1) {
                       musicIndex = random.nextInt(MusicList.size());
                       SongStack.main.addLast(musicIndex);
                       SongStack.location++;
                       Log.d("MusicService", "nextMusic: 位置："+SongStack.location+"随机选取下一首歌！X");
                   }
                   else
                   {
                       SongStack.location++;
                       musicIndex=SongStack.main.get(SongStack.location);
                       Log.d("MusicService", "nextMusic: 位置："+SongStack.location+"顺序选取下一首歌！O");
                   }


               }
               mp.setDataSource(MusicList.get(musicIndex).path);
               mp.prepare();
               mp.seekTo(0);
               mp.start();
               MusicPlaying=MusicList.get(musicIndex);
               MusicPlaying.isPlaying=true;
           }
       }
       catch(Exception e)
       {
           e.printStackTrace();
       }

   }

   public void preMusic()
   {
       try
       {
           if(mp!=null&&musicIndex>0)
           {
               mp.stop();
               mp.reset();
               MusicPlaying.isPlaying = false;

               if(sequenceMode==0)
               {
                   //特殊情况特殊处理
                       musicIndex--;
                       if(musicIndex<0)
                           musicIndex=MusicList.size()-1;
               }
               else if(sequenceMode==1)
               {

               }
               else
               {
                   if(SongStack.location==0) {
                       musicIndex = random.nextInt(MusicList.size());//处在初始状态
                       Log.d("MusicService", "preMusic:位置："+SongStack.location+" 随机选取上一首歌！X");
                       SongStack.main.addFirst(musicIndex);
                       //SongStack.location++;
                   }
                   else
                   {
                       SongStack.location--;
                       musicIndex=SongStack.main.get(SongStack.location);
                       Log.d("MusicService", "preMusic:位置："+SongStack.location+" 顺序选取上一首歌！O");
                   }


               }
               mp.setDataSource(MusicList.get(musicIndex).path);
               mp.prepare();
               mp.seekTo(0);
               mp.start();


               MusicPlaying=MusicList.get(musicIndex);
               MusicPlaying.isPlaying=true;
           }
       }
       catch(Exception e)
       {
           e.printStackTrace();
       }
   }

    public void selectMusic(int p)//手动点击某个条目
    {
        try {

                mp.stop();
                mp.reset();
                MusicPlaying.isPlaying = false;

                musicIndex=p;
                mp.setDataSource(MusicList.get(musicIndex).path);
                mp.prepare();
                mp.seekTo(0);
                mp.start();
                MusicPlaying=MusicList.get(musicIndex);
                MusicPlaying.isPlaying=true;

                if(sequenceMode==2)
                {
                    SongStack.main.addLast(musicIndex);
                   SongStack.location=SongStack.main.size()-1;
                    Log.d("MusicService", "nextMusic: 位置："+SongStack.location+"点击选取下一首歌！X");
                }

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

    }


}
