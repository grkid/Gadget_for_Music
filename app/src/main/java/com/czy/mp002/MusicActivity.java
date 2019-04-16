package com.czy.mp002;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class MusicActivity extends AppCompatActivity {

    private MusicService musicService;

    private boolean isTouchFlag=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        Intent it2=getIntent();
        Bundle bd=it2.getExtras();


       IBinder binder=bd.getBinder("binder");
        musicService=((MusicService.MyBinder)binder).getSetvice();
        SetListener();

        Setnames();



    }


    @Override
    protected void onStart()
    {
        super.onStart();
        setBackground();
    }

    private void SetListener()
    {
        final ImageButton playOrPause=findViewById(R.id.PlayButton2);
        playOrPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                musicService.playOrPause();
                if(musicService.MusicPlaying.isPlaying)
                    playOrPause.setImageResource(R.mipmap.ic_play_bar_btn_pause);
                else
                    playOrPause.setImageResource(R.mipmap.ic_play_bar_btn_play);

                setBackground();
            }
        });

        final ImageButton PreButton=findViewById(R.id.PreButton2);
        PreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                musicService.preMusic();
                playOrPause.setImageResource(R.mipmap.ic_play_bar_btn_pause);
                setBackground();
                Setnames();
            }
        });

        final ImageButton NextButton=findViewById(R.id.NextButton2);
        NextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                musicService.nextMusic();
                playOrPause.setImageResource(R.mipmap.ic_play_bar_btn_pause);
                setBackground();
                Setnames();
            }
        });

        final ImageButton SeqButton=findViewById(R.id.SeqButton2);
        SeqButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                musicService.setSequenceMode();
                int temp=musicService.getSequenceMode();
                if(temp==0)
                {
                    SeqButton.setImageResource(R.mipmap.ic_repeat);
                    Toast.makeText(MusicActivity.this,"顺序播放",Toast.LENGTH_SHORT).show();
                }
                else if(temp==1)
                {
                    SeqButton.setImageResource(R.mipmap.ic_repeat_one);
                    Toast.makeText(MusicActivity.this,"单曲循环",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    SeqButton.setImageResource(R.mipmap.ic_shuffle);
                    Toast.makeText(MusicActivity.this,"随机播放",Toast.LENGTH_SHORT).show();
                }
            }
        });

        ImageButton equalizerButton=findViewById(R.id.EqualizerButton2);
        equalizerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String[] modes=new String[]{"普通","低音增强","高音增强"};

                AlertDialog alert= null;
                AlertDialog.Builder builder=null;

                builder=new AlertDialog.Builder(MusicActivity.this);
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

        SeekBar seekBar=findViewById(R.id.seekBar2);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(b)
                {
                    TextView temp=findViewById(R.id.TimePlayed);
                    int time=i/1000;
                    int t1,t2;
                    t1=time/60;
                    t2=time%60;
                    if(t2<10)
                        temp.setText(t1+":0"+t2);
                    else
                        temp.setText(t1+":"+t2);
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isTouchFlag=true;


            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                musicService.mp.seekTo(seekBar.getProgress());
                isTouchFlag=false;
            }
        });
    }

    public Handler handler=new Handler();
    public Runnable runnable=new Runnable() {
        @Override
        public void run() {
            SeekBar seekBar=findViewById(R.id.seekBar2);
            seekBar.setMax(musicService.MusicPlaying.length);
            seekBar.setProgress(musicService.mp.getCurrentPosition());

            int length=musicService.MusicPlaying.length/1000;
            int cur=musicService.mp.getCurrentPosition()/1000;

            int L1,L2,R1,R2;
            L1=cur/60;
            L2=cur%60;
            R1=length/60;
            R2=length%60;

            TextView t1,t2;
            t1=findViewById(R.id.TimePlayed);
            t2=findViewById(R.id.TimeSum);
            if(!isTouchFlag) {
                if (L2 < 10)
                    t1.setText(L1 + ":0" + L2);
                else
                    t1.setText(L1 + ":" + L2);
            }

            if(R2<10)
                t2.setText(R1+":0"+R2);
            else
                t2.setText(R1+":"+R2);

            if(MusicService.isAutoNextFlag==true)
            {
                MusicService.isAutoNextFlag=false;
               setBackground();
               Setnames();

            }

            handler.postDelayed(runnable,100);
        }
    };

    private void setBackground()
    {
        //Bitmap bmp=musicService.MusicPlaying.albumCover;
        Bitmap bmp=getAlbumArt(musicService.MusicPlaying.coverID);
        ImageButton coverimage=findViewById(R.id.CoverImage);

        coverimage.setImageBitmap(getAlbumArt(musicService.MusicPlaying.coverID));

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
            MainActivity.blur(inPixels, outPixels, width, height, hRadius);
            MainActivity.blur(outPixels, inPixels, height, width, vRadius);
        }
        MainActivity.blurFractional(inPixels, outPixels, width, height, hRadius);
        MainActivity.blurFractional(outPixels, inPixels, height, width, vRadius);
        bitmap.setPixels(inPixels, 0, width, 0, 0, width, height);

        int start_x2,start_y2;
        start_x2=(int)((double)width*0.05);
        start_y2=(int)((double)height*0.05);

        width=(int)((double)width*0.9);
        height=(int)((double)height*0.9);

        bitmap=Bitmap.createBitmap(bitmap,start_x2,start_y2,width,height);

        WindowManager wm=(WindowManager)this.getSystemService(Context.WINDOW_SERVICE);

        ConstraintLayout tempList=findViewById(R.id.MusicLayout);
        DisplayMetrics dm=new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        double listWidth=dm.widthPixels;
        double listHeight=dm.heightPixels;
        int expectedWidth=(int)((double)height*(listWidth/listHeight));//期望得到的宽度
        int start_x=(width-expectedWidth)/2;





        Bitmap tempBitmap=Bitmap.createBitmap(bitmap,start_x,0,expectedWidth,height);

        Drawable drawable = new BitmapDrawable(getResources(),tempBitmap);
        tempList.setBackground(drawable);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        handler.post(runnable);

        ImageButton SeqButton=findViewById(R.id.SeqButton2);
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
    }

    private void Setnames()
    {
        TextView theme,artist,album;
        theme=findViewById(R.id.SongName);
        artist=findViewById(R.id.ArtistName);
        album=findViewById(R.id.AlbumName);

        theme.setText(musicService.MusicPlaying.title);
        artist.setText(musicService.MusicPlaying.artist);
        album.setText(musicService.MusicPlaying.album);
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
        //op.inSampleSize=4;
        //这里不进行压缩。

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
