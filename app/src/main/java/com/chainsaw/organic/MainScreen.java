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
import com.chainsaw.organic.widgets.HueSlider;
import com.chainsaw.organic.widgets.ValueSlider;
import com.gc.materialdesign.views.ButtonFloat;
import com.gc.materialdesign.views.Slider;
import com.gc.materialdesign.widgets.ColorSelector;


public class MainScreen extends Activity {

    private boolean allowChange = false;

    private enum SeekBarFunc {
        BRIGHTNESS, TILESIZE
    }

    public static final float BRIGHTNESS_FACTOR = 0.4f;

    //VALUES
    private static class MapParams {
        static int tintColor = 0xFFFFFFFF;
        static int tileSize = 15;
        static int brightness = 100; // value is between 0-100
    }

    private SeekBarFunc seekBoxFunc = SeekBarFunc.BRIGHTNESS;
    private Slider seekBarWidget;
    View seekBox;
    ImageView imageView;
    TextView seekText;
    int screenWidth;
    int screenHeight;

    // UI elements
    ButtonFloat buttonFloat;
    HueSlider slider0;
    ValueSlider slider1;
    ValueSlider slider2;

    private NoiseMap rawMap;

    Slider.OnValueChangedListener onSeekChange = new Slider.OnValueChangedListener() {
        @Override
        public void onValueChanged(int value) {
            if (allowChange) {
                if (seekBoxFunc == SeekBarFunc.BRIGHTNESS) {
                    MapParams.brightness = value;
                    generateBitmap();
                }
                if (seekBoxFunc == SeekBarFunc.TILESIZE) {
                    MapParams.tileSize = value + 2; // offset for 0
                    generateNoise();
                }
            }
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

        seekBarWidget = (Slider) findViewById(R.id.seekBar);
        seekBarWidget.setOnValueChangedListener(onSeekChange);

        seekText = (TextView) findViewById(R.id.seekText);
        seekText.setOnClickListener(dismissSeekBoxListener);

        imageView = (ImageView) findViewById(R.id.imgViewDisplay);
        imageView.setClickable(true);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissSliders();
                generateNoise();
            }
        });


        buttonFloat = (ButtonFloat) findViewById(R.id.buttonFloat);
        slider0 = (HueSlider) findViewById(R.id.bt0);
        slider1 = (ValueSlider) findViewById(R.id.bt1);
        slider2 = (ValueSlider) findViewById(R.id.bt2);

        dismissSliders();


        slider0.setOnValueChangedListener(new Slider.OnValueChangedListener() {
            @Override
            public void onValueChanged(int value) {
                MapParams.tintColor = 0xFFFFFFFF & slider0.getColor();;
                //Toast.makeText(MainScreen.this, String.format("#%06X", (0xFFFFFFFF & color)), Toast.LENGTH_SHORT).show();
                generateBitmap();

            }
        });

        buttonFloat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (slider1.getVisibility() != View.VISIBLE) {
                    buttonFloat.hideMe(buttonFloat.getY());
                    slider0.showMe(buttonFloat.getY());
                    slider1.showMe(buttonFloat.getY());
                    slider2.showMe(buttonFloat.getY());
                } else {
//                    buttonFloat.showMe(buttonFloat.getY());
//                    slider0.hideMe(buttonFloat.getY());
//                    slider1.hideMe(buttonFloat.getY());
//                    slider2.hideMe(buttonFloat.getY());
                }
            }
        });


    }

    private void dismissSliders() {
        buttonFloat.showMe(buttonFloat.getY());
        slider0.hideMe(buttonFloat.getY());
        slider1.hideMe(buttonFloat.getY());
        slider2.hideMe(buttonFloat.getY());

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

                //   int RGB = 0xFF000000 | red | green | blue;
                int RGB = MapParams.tintColor ^ (red | green | blue);
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

    private void pickColor() {
        ColorSelector colorSelector = new ColorSelector(this, MapParams.tintColor, new ColorSelector.OnColorSelectedListener() {
            @Override
            public void onColorSelected(int color) {
                MapParams.tintColor = 0xFFFFFFFF & color;
                Toast.makeText(MainScreen.this, String.format("#%06X", (0xFFFFFFFF & color)), Toast.LENGTH_SHORT).show();
                generateBitmap();
            }
        });
        colorSelector.show();
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
            seekBarWidget.setValue(MapParams.brightness);

            seekBox.setVisibility(View.VISIBLE);

            seekBoxFunc = SeekBarFunc.BRIGHTNESS;
            allowChange = true;
            return true;
        }

        if (id == R.id.action_colorpicker) {
            Toast.makeText(this, "COLOR PICKER", Toast.LENGTH_SHORT).show();
            pickColor();
            return true;
        }
        if (id == R.id.action_tilesize) {
            seekText.setText("done adjusting COMPLEXITY");

            seekBarWidget.setMax(300);
            seekBarWidget.setValue(MapParams.tileSize);

            seekBox.setVisibility(View.VISIBLE);

            seekBoxFunc = SeekBarFunc.TILESIZE;
            allowChange = true;
            return true;

        }
        return super.onOptionsItemSelected(item);
    }
}
