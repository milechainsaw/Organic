package com.chainsaw.organic;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chainsaw.organic.math.NoiseGenerator;
import com.chainsaw.organic.math.NoiseMap;
import com.chainsaw.organic.widgets.SeekBarHint;


public class MainScreen extends Activity {

    private enum SeekBarFunc {
        BRIGHTNESS, TILESIZE
    }

    public static final int BRIGHTNESS = 254;
    private boolean seekBarVisible = false;
    private SeekBarFunc seekBarFunc = SeekBarFunc.BRIGHTNESS;
    private SeekBarHint seekBar;
    ImageView imageView;
    TextView seekText;
    int screenWidth;
    int screenHeight;

    private NoiseMap rawMap;

    SeekBarHint.OnSeekBarHintProgressChangeListener onSeekChange = new SeekBarHint.OnSeekBarHintProgressChangeListener() {
        @Override
        public String onHintTextChanged(SeekBarHint seekBarHint, int progress) {
            if (seekBarFunc == SeekBarFunc.BRIGHTNESS) {
                generateBitmap((int)(progress/0.4f));
                Log.i("SEEK", "Adjusting brightness to " + progress);
            }
            if (seekBarFunc == SeekBarFunc.TILESIZE) {

            }
            return null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        seekBar = (SeekBarHint) findViewById(R.id.seekBar);
        seekBar.setVisibility(View.GONE);
        seekBar.setOnProgressChangeListener(onSeekChange);

        seekText = (TextView) findViewById(R.id.seekText);


        imageView = (ImageView) findViewById(R.id.imgViewDisplay);
        imageView.setClickable(true);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateNoise();
            }
        });
    }

    private void generateNoise() {
        Log.i("Generator", "Started generation...");

        screenHeight = imageView.getHeight();
        screenWidth = imageView.getWidth();
        int x = 15;
        int y = 15;

        int randomize = (int) (Math.random() * 789221);

        rawMap = new NoiseMap(x, y);
        int index = 0;
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                rawMap.values[index] = NoiseGenerator.noise(i / 64f, j / 64f, 4, randomize);
                index++;
            }
        }

        generateBitmap(BRIGHTNESS);


    }

    private Bitmap generateBitmap(int brightness) {
        NoiseMap localMap = new NoiseMap(rawMap);

        Bitmap bitmap = Bitmap.createBitmap(rawMap.width, rawMap.height, Bitmap.Config.ARGB_8888);
//        Normalize values to the selected brightness
        localMap.normalize(brightness);
        int index = 0;
        for (int i = 0; i < localMap.width; i++) {
            for (int j = 0; j < localMap.height; j++) {
                int red = localMap.values[index];
                int green = localMap.values[index];
                int blue = localMap.values[index];
                index++;

                red = (red << 16) & 0x00FF0000;
                green = (green << 8) & 0x0000FF00;
                blue = blue & 0x000000FF;

                int RGB = 0xFF000000 | red | green | blue;
                bitmap.setPixel(i, j, RGB);
            }
        }

        imageView.setImageBitmap(scaleToFit(bitmap, screenWidth, screenHeight));
        bitmap.recycle();
        Log.i("Generator", "DONE!!!");

        return bitmap;
    }


    private Bitmap scaleToFit(Bitmap bitmap, int width, int height) {
        int k;
        if (height > width) {
            k = height / bitmap.getHeight();
        } else {
            k = width / bitmap.getWidth();
        }
        int newWidth = bitmap.getWidth() * k;
        int newHeight = bitmap.getHeight() * k;
        Log.i("Scaler", "x=" + newWidth + " y=" + newHeight);
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, false);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.action_brightness) {
            Toast.makeText(this, "BRIGHTNESS", Toast.LENGTH_SHORT).show();
            if (seekBarVisible) {
                seekBarVisible = false;
                seekBar.setVisibility(View.GONE);
            } else {
                seekBarVisible = true;
                seekBar.setMax(100);
                seekBar.setVisibility(View.VISIBLE);
                seekBarFunc = SeekBarFunc.BRIGHTNESS;
            }
            return true;
        }

        if (id == R.id.action_colorpicker) {
            Toast.makeText(this, "COLOR PICKER", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (id == R.id.action_tilesize) {
            Toast.makeText(this, "TILE SIZE", Toast.LENGTH_SHORT).show();

            if (seekBarVisible) {
                seekBarVisible = false;
                seekBar.setVisibility(View.GONE);
            } else {
                seekBarVisible = true;
                seekBar.setVisibility(View.VISIBLE);
                seekBarFunc = SeekBarFunc.TILESIZE;
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
