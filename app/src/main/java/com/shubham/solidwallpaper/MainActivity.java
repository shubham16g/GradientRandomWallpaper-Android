package com.shubham.solidwallpaper;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.WallpaperManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    Bitmap bitmap;
    int widthPixels, heightPixels;
    ImageView imageView;
    int[] randomValues;
    ImageButton refreshButton, angleButton, topColorButton, bottomColorButton;
    Toolbar toolbar;
    View wallpaperCard, extraTop, extraBottom;
    LinearGradient gradient;
    int fixTopColor, fixBottomColor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);

        imageView = findViewById(R.id.random_image);
        refreshButton = findViewById(R.id.refresh_button);
        wallpaperCard = findViewById(R.id.wallpaper_card);

        widthPixels = getResources().getDisplayMetrics().widthPixels;
        heightPixels = getResources().getDisplayMetrics().heightPixels;
        topColorButton = findViewById(R.id.top_color_button);
        bottomColorButton = findViewById(R.id.bottom_color_button);
        extraTop = findViewById(R.id.extra_top_color_view);
        extraBottom = findViewById(R.id.extra_bottom_color_view);
        angleButton = findViewById(R.id.angle_button);

        refreshColor();
        refreshWallpaper();

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonAnimation(refreshButton);
                resetColorButton();
                refreshColor();
                refreshWallpaper();

            }
        });
        wallpaperCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, MainActivity.class);
                i.putExtra("gradient", randomValues);
                startActivity(i);
            }
        });

        topColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fixTopColor = getRandomColor();
                topColorButton.setBackgroundColor(fixTopColor);
                extraTop.setBackgroundColor(fixTopColor);
                refreshWallpaper();
            }
        });

        bottomColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fixBottomColor = getRandomColor();
                bottomColorButton.setBackgroundColor(fixBottomColor);
                extraBottom.setBackgroundColor(fixBottomColor);
                refreshWallpaper();
            }
        });

        angleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonAnimation(angleButton);
                refreshWallpaper();
            }
        });
        toolbar.inflateMenu(R.menu.full_view_one_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.set_wallpaper) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        String[] options = {"Home screen", "Lock screen", "Both"};
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("SET WALLPAPER AS");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, final int which) {
                                AsyncTask.execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        setWallpaper(which + 1);
                                    }
                                });
                            }
                        });
                        builder.show();
                    } else {
                        AsyncTask.execute(new Runnable() {
                            @Override
                            public void run() {
                                setWallpaper(0);
                            }
                        });
                    }


                }
                return true;
            }
        });

    }

    private void resetColorButton() {
        topColorButton.setBackgroundResource(R.drawable.random);
        extraTop.setBackgroundResource(R.drawable.random_top);
        bottomColorButton.setBackgroundResource(R.drawable.random);
        extraBottom.setBackgroundResource(R.drawable.random_bottom);
    }

    private void buttonAnimation(final ImageButton button) {
        button.animate().scaleX(1.3f).scaleY(1.3f).rotation(180).setDuration(250);

        button.setEnabled(false);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                button.setEnabled(true);
                button.animate().scaleX(1).scaleY(1).rotation(0).setDuration(0);
            }
        }, 250);
        button.clearAnimation();
    }

    private void refreshWallpaper() {
        randomValues = getRandomValues();
        gradient = createGradient(randomValues);
        bitmap = createDynamicGradient(gradient);
        imageView.setImageBitmap(bitmap);
    }

    private void refreshColor() {
        fixTopColor = getRandomColor();
        fixBottomColor = getRandomColor();
    }

    public int getRandomColor() {
        Random rnd = new Random();
        return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
    }

    private Bitmap createDynamicGradient(LinearGradient gradient) {
        Paint p = new Paint();
        p.setDither(true);
        p.setShader(gradient);
        Bitmap bitmap = Bitmap.createBitmap(widthPixels, heightPixels, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawRect(new RectF(0, 0, widthPixels, heightPixels), p);
        return bitmap;
    }

    private int[] getRandomValues() {
        Random random = new Random();
        int[] randomValues = new int[5];
        randomValues[0] = random.nextInt(1500);
        randomValues[1] = random.nextInt(1500);
        randomValues[2] = random.nextInt(1000) + 1000;
        return randomValues;
    }

    public LinearGradient createGradient(int[] randomValues) {
        return new LinearGradient(randomValues[0], 0, randomValues[1], randomValues[2], fixTopColor, fixBottomColor, Shader.TileMode.CLAMP);
    }

    private void setWallpaper(int where) {
        WallpaperManager manager = WallpaperManager.getInstance(getApplicationContext());

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                if (where == 1) {
                    manager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_SYSTEM);
                } else if (where == 2) {
                    manager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_LOCK);//For Lock screen
                } else {
                    manager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_SYSTEM);
                    manager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_LOCK);//For Lock screen
                }
            } else {
                manager.setBitmap(bitmap);
            }

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /*
    @Override
    protected void onStart() {
        super.onStart();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.fade_out);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.fade_out);
    }*/
}
