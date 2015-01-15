package com.chainsaw.organic;

import android.app.Activity;
import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.Toast;

import com.chainsaw.organic.math.NoiseGenerator;
import com.chainsaw.organic.math.NoiseMap;
import com.chainsaw.organic.widgets.HueSlider;
import com.chainsaw.organic.widgets.ValueSlider;
import com.gc.materialdesign.views.ButtonFloat;
import com.gc.materialdesign.views.Slider;

import java.io.IOException;


public class MainScreen extends Activity {

    public static final float BRIGHTNESS_FACTOR = 0.4f;

    //VALUES
    private static class MapParams {
        final static int MIN_BRIGHTNESS = 0;
        final static int MAX_BRIGHTNESS = 100;
        final static int MIN_TILESIZE = 2;
        final static int MAX_TILESIZE = 50;

        static int hue = 100;
        static int tileSize = 15;
        static int brightness = 80; // value is between 0-100
        static int saturation = 100;
    }

    ImageView imageView;
    int screenWidth;
    int screenHeight;

    // UI elements
    ButtonFloat buttonSettings;
    ButtonFloat buttonApply;
    HueSlider sliderHue;
    ValueSlider sliderBrihtness;
    ValueSlider sliderTileSize;
    ValueSlider sliderSaturation;


    //UI help
    float settingsButtonPos;
    float applyButtonPos;
    float sliderHuePos;
    float sliderBrightnessPos;
    float sliderTileSizePos;
    float sliderSaturationPos;
    boolean slidersVisible;

    private NoiseMap rawMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        getDisplayMetrics();


        imageView = (ImageView) findViewById(R.id.imgViewDisplay);
        imageView.setClickable(true);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (slidersVisible) {
                    dismissSliders();
                } else {
                    generateNoise();
                }
            }
        });


        buttonSettings = (ButtonFloat) findViewById(R.id.buttonSettings);
        buttonApply = (ButtonFloat) findViewById(R.id.buttonApply);
        sliderHue = (HueSlider) findViewById(R.id.bt0);
        sliderBrihtness = (ValueSlider) findViewById(R.id.bt1);
        sliderTileSize = (ValueSlider) findViewById(R.id.bt3);
        sliderSaturation = (ValueSlider) findViewById(R.id.bt2);

        sliderHue.setVisibility(View.INVISIBLE);

        sliderBrihtness.setVisibility(View.INVISIBLE);
        sliderTileSize.setVisibility(View.INVISIBLE);
        sliderSaturation.setVisibility(View.INVISIBLE);

        sliderBrihtness.setMin(MapParams.MIN_BRIGHTNESS);
        sliderBrihtness.setMax(MapParams.MAX_BRIGHTNESS);  //Brightness

        sliderSaturation.setMax(100);

        sliderTileSize.setMin(MapParams.MIN_TILESIZE);
        sliderTileSize.setMax(MapParams.MAX_TILESIZE);  //Complexity


        slidersVisible = false;
        buttonSettings.setVisibility(View.VISIBLE);
        buttonApply.setVisibility(View.VISIBLE);

        imageView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    buttonSettings.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    buttonSettings.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
                settingsButtonPos = buttonSettings.getY();
                applyButtonPos = buttonApply.getY();

                sliderHuePos = sliderHue.getY();
                sliderBrightnessPos = sliderBrihtness.getY();
                sliderTileSizePos = sliderTileSize.getY();
                sliderSaturationPos = sliderSaturation.getY();
            }
        });

        sliderSaturation.setOnValueChangedListener(new Slider.OnValueChangedListener() {
            @Override
            public void onValueChanged(int value) {
                MapParams.saturation = value;
                generateBitmap();
            }
        });

        sliderHue.setOnValueChangedListener(new Slider.OnValueChangedListener() {
            @Override
            public void onValueChanged(int value) {
                MapParams.hue = value;
                sliderHue.setBackgroundColor(sliderHue.getColor(value));
                generateBitmap();

            }
        });

        sliderBrihtness.setOnValueChangedListener(new Slider.OnValueChangedListener() {
            @Override
            public void onValueChanged(int value) {
                MapParams.brightness = value;
                generateBitmap();
            }
        });

        sliderTileSize.setOnValueChangedListener(new Slider.OnValueChangedListener() {
            @Override
            public void onValueChanged(int value) {
                MapParams.tileSize = value + 2; // offset for 0
                generateNoise();
            }
        });


        buttonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sliderBrihtness.getVisibility() != View.VISIBLE) {
                    showSliders();
                }
            }
        });

        buttonApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveImageToGallery();

            }
        });

    }

    private void saveImageToGallery() {
        if (imageView.getDrawable() != null) {
            Bitmap wall_bitmap = Bitmap.createBitmap(((BitmapDrawable) imageView.getDrawable()).getBitmap());
            try {
                WallpaperManager.getInstance(this).setBitmap(wall_bitmap);
            } catch (IOException e) {
                Toast.makeText(MainScreen.this, "Fucked! ", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            Toast.makeText(MainScreen.this, "saved! ", Toast.LENGTH_SHORT).show();
            wall_bitmap.recycle();
        }
    }


    private void getDisplayMetrics() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;
    }

    private void showSliders() {
        if (!slidersVisible) {
            sliderHue.setY(sliderHuePos);
            sliderBrihtness.setY(sliderBrightnessPos);
            sliderTileSize.setY(sliderTileSizePos);
            sliderSaturation.setY(sliderSaturationPos);
            buttonSettings.setY(settingsButtonPos);
            buttonApply.setY(applyButtonPos);

            //TODO set hue slider to the right spot
            sliderHue.setValue(MapParams.hue);
            sliderBrihtness.setValue(MapParams.brightness);
            sliderTileSize.setValue(MapParams.tileSize);
            sliderSaturation.setValue(MapParams.saturation);

            buttonSettings.hideMe(settingsButtonPos + buttonSettings.getHeight());
            buttonApply.hideMe(applyButtonPos + buttonApply.getHeight());
            sliderHue.showMe(settingsButtonPos);
            sliderBrihtness.showMe(settingsButtonPos);
            sliderTileSize.showMe(settingsButtonPos);
            sliderSaturation.showMe(settingsButtonPos);
            slidersVisible = true;
        }
    }

    private void dismissSliders() {
        if (slidersVisible) {
            buttonSettings.setY(settingsButtonPos);
            buttonSettings.showMe(settingsButtonPos + buttonSettings.getHeight());

            buttonApply.setY(applyButtonPos);
            buttonApply.showMe(applyButtonPos + buttonApply.getHeight());
            sliderHue.hideMe(settingsButtonPos);
            sliderBrihtness.hideMe(settingsButtonPos);
            sliderTileSize.hideMe(settingsButtonPos);
            sliderSaturation.hideMe(settingsButtonPos);
            slidersVisible = false;
        }

    }

    private void generateNoise() {
        Log.i("Generator", "Started generation...");

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

    private void generateBitmap() {
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
                int RGB = sliderHue.getColor(MapParams.hue) ^ (red | green | blue);


                //
                // TODO try to adjust saturation
                //

                float hsv[] = new float[3];
                Color.colorToHSV(RGB, hsv);
                hsv[1] = hsv[1] * (((float) sliderSaturation.getValue()) / 100);
                RGB = Color.HSVToColor(hsv);


                bitmap.setPixel(i, j, RGB);
            }
        }

        imageView.setImageBitmap(scaleToFit(bitmap, screenWidth, screenHeight));
        bitmap.recycle();
        Log.i("Generator", "DONE!!!");

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
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, false);
    }

}
