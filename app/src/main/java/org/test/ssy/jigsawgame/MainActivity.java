package org.test.ssy.jigsawgame;


import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.test.ssy.jigsawgame.view.GameView;


public class MainActivity extends AppCompatActivity {

    private GameView gameView;

    private TextView tv_level, tv_time;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_level = (TextView) findViewById(R.id.tv_level);
        tv_time = (TextView) findViewById(R.id.tv_time);

        gameView = (GameView) findViewById(R.id.gameView);

        //显示时间
        gameView.setTimeEnabled(true);

        gameView.setOnGamemListener(new GameView.GamePintuListener() {
            @Override
            public void nextLevel(final int nextLevel) {
                new AlertDialog.Builder(MainActivity.this).setTitle("你完成了老王！").setMessage("再接再厉")
                        .setPositiveButton("下一关", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                gameView.nextLevel();
                                tv_level.setText("当前关卡" + nextLevel);
                            }
                        }).show();
            }

            @Override
            public void timeChanged(int time) {
                tv_time.setText("倒计时" + time);
            }

            @Override
            public void gameOver() {
                new AlertDialog.Builder(MainActivity.this).setTitle("游戏结束").setMessage("很遗憾，需再接再厉")
                        .setPositiveButton("重新开始", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                gameView.restartGame();
                            }
                        }).setNegativeButton("退出", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        finish();
                    }
                }).show();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        gameView.pauseGame();
    }

    @Override
    protected void onResume() {
        super.onResume();
        gameView.resumeGame();
    }

}
