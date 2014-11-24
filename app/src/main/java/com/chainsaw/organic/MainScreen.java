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

    private boolean allowChange = false;

    private enum SeekBarFunc {
        BRIGHTNESS, TILESIZE
    }

    public static final float BRIGHTNESS_FACTOR = 0.4f;

    //VALUES
    private static class MapParams {
        static int tileSize = 15;
        static int brightness = 100; // value is between 0-100
    }

    private SeekBarFunc seekBoxFunc = SeekBarFunc.BRIGHTNESS;
    private SeekBarHint seekBarWidget;
    View seekBox;
    ImageView imageView;
    TextView seekText;
    int screenWidth;
    int screenHeight;

    private NoiseMap rawMap;


    SeekBarHint.OnSeekBarHintProgressChangeListener onSeekChange = new SeekBarHint.OnSeekBarHintProgressChangeListener() {
        @Override
        public String onHintTextChanged(SeekBarHint seekBarHint, int progress) {
            if (allowChange) {
                if (seekBoxFunc == SeekBarFunc.BRIGHTNESS) {
                    MapParams.brightness = progress;
                    generateBitmap();
                    Log.i("SEEK", "Adjusting brightness to " + progress);
                }
                if (seekBoxFunc == SeekBarFunc.TILESIZE) {
                    MapParams.tileSize = progress + 2; // offset for 0
                    generateNoise();
                    return (MapParams.tileSize + " x " + MapParams.tileSize);
                }
            }
            return null;
        }
    };

    View.OnClickListener dismissSeekBoxListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dismissSeekBox();
        }
    };

    private void dismissSeekBox() {
        seekBox.setVisibility(View.GONE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        seekBox = findViewById(R.id.seekBox);
        seekBox.setVisibility(View.GONE);

        seekBarWidget = (SeekBarHint) findViewById(R.id.seekBar);
        seekBarWidget.setOnProgressChangeListener(onSeekChange);

        seekText = (TextView) findViewById(R.id.seekText);
        seekText.setOnClickListener(dismissSeekBoxListener);

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
        int x = MapParams.tileSize;
        int y = x;

        int randomize = (int) (Math.random() * 789221);

        rawMap = new NoiseMap(x, y);
        int index = 0;
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                rawMap.values[index] = NoiseGenerator.noise(i / 64f, j / 64f, 4, randomize);
                index++;
            }
        }

        generateBitmap();


    }

    private Bitmap generateBitmap() {
        NoiseMap localMap = new NoiseMap(rawMap);

        Bitmap bitmap = Bitmap.createBitmap(rawMap.width, rawMap.height, Bitmap.Config.ARGB_8888);
//        Normalize values to the selected brightness
        localMap.normalize((int) (MapParams.brightness / BRIGHTNESS_FACTOR));
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
        // Disable progress bar updates
        // until new one settles
        allowChange = false;

        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.action_brightness) {
            seekText.setText("done adjusting BRIGHTNESS");
            seekBarWidget.setMax(100);
            seekBarWidget.setProgress(MapParams.brightness);

            seekBox.setVisibility(View.VISIBLE);

            seekBoxFunc = SeekBarFunc.BRIGHTNESS;
            allowChange = true;
            return true;
        }

        if (id == R.id.action_colorpicker) {
            Toast.makeText(this, "COLOR PICKER", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (id == R.id.action_tilesize) {
            seekText.setText("done adjusting COMPLEXITY");

            seekBarWidget.setMax(48);
            seekBarWidget.setProgress(MapParams.tileSize);

            seekBox.setVisibility(View.VISIBLE);

            seekBoxFunc = SeekBarFunc.TILESIZE;
            allowChange = true;
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
