package com.chainsaw.organic;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Point;
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


public class MainScreen extends Activity {

    public static final float BRIGHTNESS_FACTOR = 0.4f;

    //VALUES
    private static class MapParams {
        static int tintColor = 0xFFFFFFFF;
        static int tileSize = 15;
        static int brightness = 100; // value is between 0-100
    }

    ImageView imageView;
    int screenWidth;
    int screenHeight;

    // UI elements
    ButtonFloat buttonFloat;
    HueSlider slider0;
    ValueSlider slider1;
    ValueSlider slider2;

    //UI help
    float floatButtonPos;
    float slider0pos;
    float slider1pos;
    float slider2pos;
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
                dismissSliders();
                generateNoise();
            }
        });


        buttonFloat = (ButtonFloat) findViewById(R.id.buttonFloat);
        slider0 = (HueSlider) findViewById(R.id.bt0);
        slider1 = (ValueSlider) findViewById(R.id.bt1);
        slider2 = (ValueSlider) findViewById(R.id.bt2);

        slider0.setVisibility(View.INVISIBLE);
        slider1.setVisibility(View.INVISIBLE);
        slider2.setVisibility(View.INVISIBLE);

        slider1.setMax(100);  //Brightness
        slider2.setMax(300);  //Complexity


        slidersVisible = false;
        buttonFloat.setVisibility(View.VISIBLE);

        imageView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    buttonFloat.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    buttonFloat.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
                floatButtonPos = buttonFloat.getY();
                slider0pos = slider0.getY();
                slider1pos = slider1.getY();
                slider2pos = slider2.getY();
                Toast.makeText(MainScreen.this, "=" + slider1pos, Toast.LENGTH_SHORT).show();

            }
        });

        slider0.setOnValueChangedListener(new Slider.OnValueChangedListener() {
            @Override
            public void onValueChanged(int value) {
                MapParams.tintColor = 0xFFFFFFFF & slider0.getColor(value);
                slider0.setBackgroundColor(slider0.getColor(value));
                generateBitmap();

            }
        });

        slider1.setOnValueChangedListener(new Slider.OnValueChangedListener() {
            @Override
            public void onValueChanged(int value) {
                MapParams.brightness = value;
                generateBitmap();
            }
        });

        slider2.setOnValueChangedListener(new Slider.OnValueChangedListener() {
            @Override
            public void onValueChanged(int value) {
                MapParams.tileSize = value + 2; // offset for 0
                generateNoise();
            }
        });


        buttonFloat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (slider1.getVisibility() != View.VISIBLE) {
                    showSliders();
                }
            }
        });


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
            slider0.setY(slider0pos);
            slider1.setY(slider1pos);
            slider2.setY(slider2pos);
            buttonFloat.setY(floatButtonPos);

            //TODO set hue slider to the right spot

            slider1.setValue(MapParams.brightness);
            slider2.setValue(MapParams.tileSize);

            buttonFloat.hideMe(floatButtonPos + buttonFloat.getHeight());
            slider0.showMe(floatButtonPos);
            slider1.showMe(floatButtonPos);
            slider2.showMe(floatButtonPos);
            slidersVisible = true;
        }
    }

    private void dismissSliders() {
        if (slidersVisible) {
            buttonFloat.setY(floatButtonPos);
            buttonFloat.showMe(floatButtonPos + buttonFloat.getHeight());
            slider0.hideMe(floatButtonPos);
            slider1.hideMe(floatButtonPos);
            slider2.hideMe(floatButtonPos);
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

}
