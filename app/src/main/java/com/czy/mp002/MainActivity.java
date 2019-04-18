package com.czy.mp002;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.PermissionRequest;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;


public class MainActivity extends Activity {


    private LinkedList<Music> MusicList;
    private Context mContext;
    private MusicAdapter mAdapter=null;
    private ListView list_item;

    private MusicService musicService;

  private boolean isIntoMusicActivity=false;



  private boolean isRecording=false;
  private int timer=0;


    private ServiceConnection sc=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            musicService=((MusicService.MyBinder)iBinder).getSetvice();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            musicService=null;
        }
    };



    private void bindServiceConnection()
    {
        Intent intent=new Intent(MainActivity.this,MusicService.class);
        startService(intent);
        bindService(intent,sc,this.BIND_AUTO_CREATE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
       //底下自己写，上边不要动。

        //OnCreate会很乱……要不然不知道怎么把握方法调用链



        mContext=MainActivity.this;//找到上下文

        //权限相关代码
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP)
        {
            //如果版本号大于安卓5
            int RPermission, WPermission;
            RPermission = mContext.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            WPermission = mContext.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int SoundRecordPermission=mContext.checkSelfPermission(Manifest.permission.RECORD_AUDIO);
            if (RPermission == PackageManager.PERMISSION_DENIED)
                this.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

            if(WPermission==PackageManager.PERMISSION_DENIED)
                this.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2);

            if(SoundRecordPermission==PackageManager.PERMISSION_DENIED)
                this.requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 3);
        }
        //权限相关代码

        SoundRecorder.setContext(mContext);

        list_item=(ListView)findViewById(R.id.MainMusicListView);//找到控件


        MusicList=new LinkedList<Music>();//不new就挂（空指针）

        ScanMusic(MusicList);//扫描音乐库的所有歌曲，速度太慢。======================================

        SeekBar seekBar=findViewById(R.id.seekBar);


        mAdapter=new MusicAdapter(MusicList,mContext);//设置adapter
        list_item.setAdapter(mAdapter);



        final ImageButton PlayButton=findViewById(R.id.PlayButton);
        ImageButton BeforeButton=findViewById(R.id.BeforeButton);
        ImageButton NextButton=findViewById(R.id.NextButton);

        musicService=new MusicService(MusicList,seekBar,PlayButton,BeforeButton,NextButton);
       bindServiceConnection();

       PlayButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               musicService.playOrPause();
               musicService.updateUI(PlayButton);
               setListBack();
           }
       });

       BeforeButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               musicService.preMusic();
               musicService.updateUI(PlayButton);
               setListBack();
           }
       });

       NextButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               musicService.nextMusic();
               musicService.updateUI(PlayButton);
               setListBack();
           }
       });




       ListView MusicList=list_item;
       MusicList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
               if(musicService.MusicList.get(i).isPlaying)
               {
                   //如果此时这首歌正在播放
                   isIntoMusicActivity=true;
                   Intent intent=new Intent(MainActivity.this,MusicActivity.class);
                   Bundle bd=new Bundle();
                   bd.putBinder("binder",musicService.binder);
                   intent.putExtras(bd);
                  startActivity(intent);
               }
               else {
                   //如果没有播放
                   musicService.selectMusic(i);
                   musicService.updateUI(PlayButton);
                   setListBack();
               }
           }
       });


       ImageButton equalizer=findViewById(R.id.EqualizerButton);
       equalizer.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               final String[] modes=new String[]{"普通","低音增强","高音增强"};

               AlertDialog alert= null;
               AlertDialog.Builder builder=null;

               builder=new AlertDialog.Builder(mContext);
              builder.setTitle("选择声音模式");
              builder.setSingleChoiceItems(modes, musicService.getequalizerMode(), new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialogInterface, int i) {
                      musicService.setequalizerMode(i);
                     dialogInterface.dismiss();
                  }
              });

              alert=builder.create();
              alert.show();

           }
       });


           musicService.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
               @Override
               public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                /*
                if(b)//b就是是否来自user，这句很重要
                    musicService.mp.seekTo(seekBar.getProgress());
                    */
               }

               @Override
               public void onStartTrackingTouch(SeekBar seekBar) {

               }

               @Override
               public void onStopTrackingTouch(SeekBar seekBar) {

                   musicService.mp.seekTo(seekBar.getProgress());
               }

           });

        final ImageButton SeqButton=findViewById(R.id.SequenceButton);
        SeqButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                musicService.setSequenceMode();
                int temp=musicService.getSequenceMode();
                if(temp==0)
                {
                    SeqButton.setImageResource(R.mipmap.ic_repeat);
                    Toast.makeText(MainActivity.this,"顺序播放",Toast.LENGTH_SHORT).show();
                }
                else if(temp==1)
                {
                    SeqButton.setImageResource(R.mipmap.ic_repeat_one);
                    Toast.makeText(MainActivity.this,"单曲循环",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    SeqButton.setImageResource(R.mipmap.ic_shuffle);
                    Toast.makeText(MainActivity.this,"随机播放",Toast.LENGTH_SHORT).show();
                }
            }
        });

        ImageButton settingButton=findViewById(R.id.SettingButton);
        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick:settings");
                startActivity(new Intent(MainActivity.this,SettingActivity.class));
            }
        });

        settingButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(!isRecording) {
                    Toast.makeText(MainActivity.this, "开始录制", Toast.LENGTH_SHORT).show();
                    //Toast.makeText(MainActivity.this,"位于MainActivity.java 271行附近",Toast.LENGTH_SHORT).show();
                    SoundRecorder.startRecord();


                    isRecording=true;
                }
                else
                {
                    Toast.makeText(MainActivity.this, "正在录制中！", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });



    }

    public void setListBack()
    {
        if(!isIntoMusicActivity) {
            if (musicService.MusicPlaying.isPlaying) {
                ListView temp = findViewById(R.id.MainMusicListView);
                Music tempMusic = musicService.MusicPlaying;
                Drawable bd;
                bd = handleBackground(getAlbumArt(tempMusic.coverID));
                temp.setBackground(bd);
            }
        }
        else
        {
            ListView temp = findViewById(R.id.MainMusicListView);
            Music tempMusic = musicService.MusicPlaying;
            Drawable bd;
            bd = handleBackground(getAlbumArt(tempMusic.coverID));
            temp.setBackground(bd);
        }
    }

    public Drawable handleBackground(Bitmap bmp)
    {
        float hRadius=10;
        float vRadius=10;
        int iterations=3;

        int width = bmp.getWidth();
        int height = bmp.getHeight();
        int[] inPixels = new int[width * height];
        int[] outPixels = new int[width * height];
        Bitmap bitmap = Bitmap.createBitmap(width, height,Bitmap.Config.ARGB_8888);
        bmp.getPixels(inPixels, 0, width, 0, 0, width, height);
        for (int i = 0; i < iterations; i++) {
            blur(inPixels, outPixels, width, height, hRadius);
            blur(outPixels, inPixels, height, width, vRadius);
        }
        blurFractional(inPixels, outPixels, width, height, hRadius);
        blurFractional(outPixels, inPixels, height, width, vRadius);
        bitmap.setPixels(inPixels, 0, width, 0, 0, width, height);


        int start_x2,start_y2;
        start_x2=(int)((double)width*0.05);
        start_y2=(int)((double)height*0.05);

        width=(int)((double)width*0.9);
        height=(int)((double)height*0.9);

        bitmap=Bitmap.createBitmap(bitmap,start_x2,start_y2,width,height);

        double listWidth,listHeight;


            ListView tempList = findViewById(R.id.MainMusicListView);
            listWidth = tempList.getWidth();
            listHeight = tempList.getHeight();




        int expectedWidth=(int)((double)height*(listWidth/listHeight));//期望得到的宽度
        int start_x=(width-expectedWidth)/2;





        Bitmap tempBitmap=Bitmap.createBitmap(bitmap,start_x,0,expectedWidth,height);

        Drawable drawable = new BitmapDrawable(getResources(),tempBitmap);
        return drawable;


    }

    public static void blur(int[] in, int[] out, int width, int height,
                            float radius) {
        int widthMinus1 = width - 1;
        int r = (int) radius;
        int tableSize = 2 * r + 1;
        int divide[] = new int[256 * tableSize];

        for (int i = 0; i < 256 * tableSize; i++)
            divide[i] = i / tableSize;

        int inIndex = 0;

        for (int y = 0; y < height; y++) {
            int outIndex = y;
            int ta = 0, tr = 0, tg = 0, tb = 0;

            for (int i = -r; i <= r; i++) {
                int rgb = in[inIndex + clamp(i, 0, width - 1)];
                ta += (rgb >> 24) & 0xff;
                tr += (rgb >> 16) & 0xff;
                tg += (rgb >> 8) & 0xff;
                tb += rgb & 0xff;
            }

            for (int x = 0; x < width; x++) {
                out[outIndex] = (divide[ta] << 24) | (divide[tr] << 16)
                        | (divide[tg] << 8) | divide[tb];

                int i1 = x + r + 1;
                if (i1 > widthMinus1)
                    i1 = widthMinus1;
                int i2 = x - r;
                if (i2 < 0)
                    i2 = 0;
                int rgb1 = in[inIndex + i1];
                int rgb2 = in[inIndex + i2];

                ta += ((rgb1 >> 24) & 0xff) - ((rgb2 >> 24) & 0xff);
                tr += ((rgb1 & 0xff0000) - (rgb2 & 0xff0000)) >> 16;
                tg += ((rgb1 & 0xff00) - (rgb2 & 0xff00)) >> 8;
                tb += (rgb1 & 0xff) - (rgb2 & 0xff);
                outIndex += height;
            }
            inIndex += width;
        }
    }

    public static void blurFractional(int[] in, int[] out, int width,
                                      int height, float radius) {
        radius -= (int) radius;
        float f = 1.0f / (1 + 2 * radius);
        int inIndex = 0;

        for (int y = 0; y < height; y++) {
            int outIndex = y;

            out[outIndex] = in[0];
            outIndex += height;
            for (int x = 1; x < width - 1; x++) {
                int i = inIndex + x;
                int rgb1 = in[i - 1];
                int rgb2 = in[i];
                int rgb3 = in[i + 1];

                int a1 = (rgb1 >> 24) & 0xff;
                int r1 = (rgb1 >> 16) & 0xff;
                int g1 = (rgb1 >> 8) & 0xff;
                int b1 = rgb1 & 0xff;
                int a2 = (rgb2 >> 24) & 0xff;
                int r2 = (rgb2 >> 16) & 0xff;
                int g2 = (rgb2 >> 8) & 0xff;
                int b2 = rgb2 & 0xff;
                int a3 = (rgb3 >> 24) & 0xff;
                int r3 = (rgb3 >> 16) & 0xff;
                int g3 = (rgb3 >> 8) & 0xff;
                int b3 = rgb3 & 0xff;
                a1 = a2 + (int) ((a1 + a3) * radius);
                r1 = r2 + (int) ((r1 + r3) * radius);
                g1 = g2 + (int) ((g1 + g3) * radius);
                b1 = b2 + (int) ((b1 + b3) * radius);
                a1 *= f;
                r1 *= f;
                g1 *= f;
                b1 *= f;
                out[outIndex] = (a1 << 24) | (r1 << 16) | (g1 << 8) | b1;
                outIndex += height;
            }
            out[outIndex] = in[width - 1];
            inIndex += width;
        }
    }

    public static int clamp(int x, int a, int b) {
        return (x < a) ? a : (x > b) ? b : x;
    }




    public Handler handler=new Handler();//顾名思义
    public Handler Record_Submit_handler=new Handler();
    public Runnable Record_Submit_runnable=new Runnable() {
        @Override
        public void run() {


            if (isRecording)
                timer++;

            if (timer == 160) {
                isRecording = false;
                timer = 0;
                SoundRecorder.StopRecord();

                String temp = SoundRecorder.file.getAbsolutePath();
                //Toast.makeText(MainActivity.this, temp, Toast.LENGTH_SHORT).show();
                send_and_set();

                Record_Submit_handler.postDelayed(Record_Submit_runnable, 100);
            }



            Record_Submit_handler.postDelayed(Record_Submit_runnable, 100);
        }
    };

    public Runnable runnable=new Runnable() {
        @Override
        public void run() {
            if(musicService.MusicPlaying.isPlaying)
                musicService.playbutton.setImageResource(R.mipmap.ic_play_bar_btn_pause);
            else
                musicService.playbutton.setImageResource(R.mipmap.ic_play_bar_btn_play);


            musicService.seekBar.setMax( musicService.MusicPlaying.length);
            musicService.seekBar.setProgress( musicService.mp.getCurrentPosition());

            if(MusicService.isAutoNextFlag==true)
            {
                MusicService.isAutoNextFlag=false;
                musicService.updateUI((ImageButton) findViewById(R.id.PlayButton));
                setListBack();

            }




            handler.postDelayed(runnable,100);
            //延迟尽量小一些
        }
    };

    private void send_and_set()
    {
        //连接到神经网络的部分
        if(NetworkConfig.isValid==false) {
            Toast.makeText(MainActivity.this, "禁止使用联网的场景识别", Toast.LENGTH_SHORT).show();
            return;
        }
        String[] outputStr=new String[]{"真安静呢~你是在教室还是图书馆？","在教室就要好好学习，别玩手机","敲键盘的声音小一点，别影响到其他同学~"};
        int rand=(int)(Math.random()*100)%3;

        AlertDialog a=null;
        AlertDialog.Builder b=null;
        b=new AlertDialog.Builder(mContext);
        b.setTitle("声音模式：普通");
        musicService.setequalizerMode(0);
        b.setMessage(outputStr[rand]);
        a=b.create();
        a.show();


    }



    @Override
    protected void onResume()
    {
        //super.onResume();
        musicService.seekBar.setMax( musicService.MusicPlaying.length);
        musicService.seekBar.setProgress( musicService.mp.getCurrentPosition());

       setListBack();

        handler.post(runnable);
        Record_Submit_handler.post(Record_Submit_runnable);

        ImageButton SeqButton=findViewById(R.id.SequenceButton);
        int temp=musicService.getSequenceMode();
        if(temp==0)
        {
            SeqButton.setImageResource(R.mipmap.ic_repeat);
            //Toast.makeText(MainActivity.this,"顺序播放",Toast.LENGTH_SHORT).show();
        }
        else if(temp==1)
        {
            SeqButton.setImageResource(R.mipmap.ic_repeat_one);
            //Toast.makeText(MainActivity.this,"单曲循环",Toast.LENGTH_SHORT).show();
        }
        else
        {
            SeqButton.setImageResource(R.mipmap.ic_shuffle);
            //Toast.makeText(MainActivity.this,"随机播放",Toast.LENGTH_SHORT).show();
        }





        super.onResume();
    }




    public void ScanMusic(LinkedList<Music> MusicList)
    {
        Cursor cursor=getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        //设置了cursor
        cursor.moveToFirst();


        int def_count=1;

        while (!cursor.isAfterLast())//如果数据库不为空
        {

            String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
            String artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
            String album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
            int length = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
            int coverID = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));

            int size = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));






            //先不去用size这个变量。最后再用来计算（筛选）

            if(length>=60&&!artist.equals("<unknown>")) {

                Music music = new Music();

                music.length = length;
                //music.albumCover = getAlbumArt(coverID);
                music.title = title;
                music.artist = artist;
                music.album = album;
                music.path = path;
                music.count = def_count;
                music.isPlaying = false;//默认值总是为false
                music.coverID = coverID;
                def_count++;


                MusicList.add(music);
            }

            cursor.moveToNext();

        }

        cursor.close();

    }

    public Bitmap getAlbumArt(int album_id)
    {

        String mUriAlbums = "content://media/external/audio/albums";
        String[] projection = new String[]{"album_art"};
        Cursor cur = this.getContentResolver().query(Uri.parse(mUriAlbums + "/" + Integer.toString(album_id)), projection, null, null, null);
        String album_art = null;
        if (cur.getCount() > 0 && cur.getColumnCount() > 0) {
            cur.moveToNext();
            album_art = cur.getString(0);
        }
        cur.close();
        Bitmap bm = null;

        BitmapFactory.Options op=new BitmapFactory.Options();
        op.inPreferredConfig=Bitmap.Config.RGB_565;
        op.inSampleSize=4;

        if (album_art != null) {
            bm = BitmapFactory.decodeFile(album_art,op);
        } else {
            bm = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_play_bar_btn_play,op);
            //随便找一个图片搞上去
        }
        cur.close();
        projection=null;
        mUriAlbums=null;
        return bm;
    }






}
