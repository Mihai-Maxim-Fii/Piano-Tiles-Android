package com.example.myapplication;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.ActionMenuItemView;

import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.media.Image;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ActionMenuView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class Game extends AppCompatActivity {
    private Timer timer=new Timer();
    private Handler handler=new Handler();
    private List<ImageView> imageViewList;
    private RelativeLayout relativeLayout;
    private float tileWidth,tileHeight;
    private float speed=17;
    private int score=0;
    private int lastSc=0;
    private int childCount=0;
    private List<Float> availablePositions;
    private Vibrator vibe;
    private ActionMenuItemView titleView;
    private boolean paused=false;
    private int period=225;
    private boolean updateScore=false;
    private float backupSpeed=speed;
    private boolean gameRunning=false;
    private SharedPreferences sharedPreferences;
    private String difficultyName;
    private int difficulty;
    private MediaPlayer b1,b2,b3,b4;

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }
    private void initDifficulty()
    {
        System.out.println("diff is"+difficulty);

        if(difficulty==0)
        {
            speed=10;
            period=350;
        }
        else
            if(difficulty==1)
            {
                speed=12;
                period=300;
            }
            else
            {
                speed=14;
                period=250;
            }
            backupSpeed=speed;

    }
    private void increaseDifficulty()
    {
        System.out.println(speed);
        if(score-lastSc>25)
        {
            if(speed<=17)
                speed+=1;
            else
            {
                period-=5;
            }
            lastSc=score;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        relativeLayout=findViewById(R.id.parentRelative);
        tileHeight=getScreenWidth()/2;
        tileWidth=getScreenWidth()/4;
        titleView= (ActionMenuItemView)findViewById(R.id.ScoreId);
        vibe = (Vibrator) Game.this.getSystemService(Context.VIBRATOR_SERVICE);
        sharedPreferences=getSharedPreferences("my_prefs",0);
        switch (Integer.valueOf(sharedPreferences.getString("PDifficulty","sn")))
        {
            case 0:
                difficultyName="Easy";
                difficulty=0;
                break;
            case 1:
                difficulty=1;
                difficultyName="Medium";
                break;
            case 2:
                difficultyName="Hard";
                difficulty=2;
                break;
        }


        if(gameRunning) {
            initGame();
        }


    }

    public void initGame()
    {   paused=false;
        score=0;
        initDifficulty();
        imageViewList=new ArrayList<>();
        availablePositions=new ArrayList<>();
        b1 = MediaPlayer.create(getApplicationContext(), R.raw.b1);
        b2=MediaPlayer.create(getApplicationContext(),R.raw.b2);
        b3=MediaPlayer.create(getApplicationContext(),R.raw.b3);
        b4=MediaPlayer.create(getApplicationContext(),R.raw.b4);

        titleView = findViewById(R.id.ScoreId);
        titleView.setTitle("Score:" + Integer.toString(score));


        for (int i=0;i<4;i++)
        {
            availablePositions.add(tileHeight);
        }

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        moveTiles();
                        if (updateScore) {
                            increaseDifficulty();
                            titleView = findViewById(R.id.ScoreId);
                            score += 1;
                            titleView.setTitle("Score:" + Integer.toString(score));
                            updateScore = false;
                        }

                    }
                });
            }
        }, 0, 10);



        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(!paused)
                            pickNextTileToDraw();
                    }
                });
            }
        },0,period);

    }
    public void showStats()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Results:");
        String message="Player:"+sharedPreferences.getString("PName","Player")+"\n"+"Difficulty:"+difficultyName+"\nScore:"+score;
        System.out.println(sharedPreferences.getString("PDifficulty","sn"));
        builder.setMessage(message);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {  }
        });

        builder.setNeutralButton("Share", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {  }
    });
    AlertDialog alertDialog = builder.create();
alertDialog.show();


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.game_menu,menu);

        return true;
    }

    private void moveTiles()
    {
        if(!paused) {

            if(speed<backupSpeed) {
                speed += 0.05;
                System.out.println(speed);
            }
            if(imageViewList!=null) {

                boolean quit=false;
                for (ImageView imageView : imageViewList) {
                    imageView.setY(imageView.getY() + speed);
                    if(quit) break;
                    if (imageView.getY() > getScreenHeight()) {

                        if(imageView.getMinimumHeight()==0) {
                            resetGame();
                            showStats();
                            quit=true;
                        }
                        relativeLayout.removeView(imageView);


                    }
                }
                for (int i = 0; i < 4; i++) {
                    availablePositions.set(i, availablePositions.get(i) + speed);
                }
            }
        }


    }
    private void pickNextTileToDraw()
    {

        Random rd = new Random(); // creating Random object
        boolean run=true;
        while (run) {
            int rnd = Math.abs(rd.nextInt()) % 4;
            float f = rd.nextFloat();
            if (availablePositions.get(rnd) >= tileHeight + speed) {
                DrawTile(f>0.25?true:false, rnd * tileWidth, 0);
                availablePositions.set(rnd, 0f);
                run=false;
            }
        }





        }




    private void DrawTile(boolean black,float x,float y)
    {

        ImageView imageView=new ImageView(getApplicationContext());
        addvieW(imageView,(int)tileWidth,(int)tileHeight);
        imageView.setX(x);
        imageView.setY(-2*tileHeight);
        if(black)
        {
            imageView.setBackgroundColor(Color.BLACK);
            imageView.setMinimumHeight(0);
        }
        else
        {
            imageView.setBackgroundColor(Color.WHITE);
            imageView.setMinimumHeight(1);


        }
        enableListeners(imageView,black);
        imageViewList.add(imageView);
        childCount+=1;


    }

    private void enableListeners(ImageView imageView,boolean black)
    {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTileClick(v,black);
            }
        });

        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onTileClick(v,black);
                return false;
            }

        });

        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                onTileClick(v,black);
                imageView.setOnTouchListener(null);
                return true;
            }
        });

        imageView.setOnGenericMotionListener(new View.OnGenericMotionListener() {
            @Override
            public boolean onGenericMotion(View v, MotionEvent event) {
                onTileClick(v,black);
                return false;
            }
        });



        imageView.setOnHoverListener(new View.OnHoverListener() {
            @Override
            public boolean onHover(View v, MotionEvent event) {
                onTileClick(v,black);
                return false;
            }
        });


        imageView.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                onTileClick(v,black);
                return false;
            }
        });



    }

    private void onTileClick(View view,boolean black)
    {
        if(!paused) {
            if (black) {

                relativeLayout.removeView(view);
                imageViewList.remove(view);
                vibe.vibrate(25);
                updateScore = true;
                /*
                Random rnd=new Random();
                int r = Math.abs(rnd.nextInt()) % 4;
                if(r==0)b1.start();
                if(r==1)b2.start();
                if(r==2)b3.start();
                if(r==3)b4.start();
                 */
                b3.start();


            } else {
                System.out.println("aici");
                resetGame();
                showStats();

            }

        }
    }

    private void addvieW(ImageView imageView, int width, int height) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);

        params.setMargins(0, 0, 0, 0);
        imageView.setLayoutParams(params);

        RelativeLayout relativeLayout=findViewById(R.id.parentRelative);
        relativeLayout.addView(imageView);
    }




    public void makeToast()
    {
        Log.d("wtf",Double.toString(getScreenWidth()));
    }

    public void disableTouchListeners()
    {
        for (ImageView imageView:imageViewList)
        {
            imageView.setOnTouchListener(null);
            imageView.setOnClickListener(null);
            imageView.setOnGenericMotionListener(null);
            imageView.setOnHoverListener(null);
            imageView.setOnLongClickListener(null);
            imageView.setOnDragListener(null);
        }
    }

    public void pauseGame(MenuItem item) {
        if(!paused)
        {
            paused=true;
            speed=10;
        }
        else
        {
            paused=false;

        }
    }
    public void resetGame()
    {
        timer.cancel();
        timer.purge();
        timer=new Timer();


        for (ImageView imageView : imageViewList) {

                relativeLayout.removeView(imageView);
            }
        Button beginButton=findViewById(R.id.beginButton);
        beginButton.setVisibility(View.VISIBLE);
    }

    public void startGame(View view) {
        initGame();
        Button beginButton=findViewById(R.id.beginButton);
        beginButton.setVisibility(View.GONE);

    }

}