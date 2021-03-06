package com.chainsaw.organic;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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

import java.io.File;
import java.io.FileOutputStream;
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
    ButtonFloat buttonShare;
    HueSlider sliderHue;
    ValueSlider sliderBrightness;
    ValueSlider sliderTileSize;
    ValueSlider sliderSaturation;


    //UI help
    float buttonSettingsPos;
    float buttonApplyPos;
    float buttonSharePos;
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
        buttonShare = (ButtonFloat) findViewById(R.id.buttonShare);
        sliderHue = (HueSlider) findViewById(R.id.bt0);
        sliderBrightness = (ValueSlider) findViewById(R.id.bt1);
        sliderTileSize = (ValueSlider) findViewById(R.id.bt3);
        sliderSaturation = (ValueSlider) findViewById(R.id.bt2);

        sliderHue.setVisibility(View.INVISIBLE);

        sliderBrightness.setVisibility(View.INVISIBLE);
        sliderTileSize.setVisibility(View.INVISIBLE);
        sliderSaturation.setVisibility(View.INVISIBLE);

        sliderBrightness.setMin(MapParams.MIN_BRIGHTNESS);
        sliderBrightness.setMax(MapParams.MAX_BRIGHTNESS);  //Brightness

        sliderSaturation.setMax(100);

        sliderTileSize.setMin(MapParams.MIN_TILESIZE);
        sliderTileSize.setMax(MapParams.MAX_TILESIZE);  //Complexity


        slidersVisible = false;
        buttonSettings.setVisibility(View.VISIBLE);
        buttonApply.setVisibility(View.VISIBLE);
        buttonShare.setVisibility(View.VISIBLE);

        imageView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    buttonSettings.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    buttonSettings.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
                buttonSettingsPos = buttonSettings.getY();
                buttonApplyPos = buttonApply.getY();
                buttonSharePos = buttonShare.getY();

                sliderHuePos = sliderHue.getY();
                sliderBrightnessPos = sliderBrightness.getY();
                sliderTileSizePos = sliderTileSize.getY();
                sliderSaturationPos = sliderSaturation.getY();

                generateNoise();
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

        sliderBrightness.setOnValueChangedListener(new Slider.OnValueChangedListener() {
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
                if (sliderBrightness.getVisibility() != View.VISIBLE) {
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

        buttonShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onShareItem(imageView);
            }
        });

    }

    private void saveImageToGallery() {
        if (imageView.getDrawable() != null) {
            Bitmap wall_bitmap = Bitmap.createBitmap(((BitmapDrawable) imageView.getDrawable()).getBitmap());
            try {
                WallpaperManager.getInstance(this).setBitmap(wall_bitmap);
            } catch (IOException e) {
                Toast.makeText(MainScreen.this, "Unable to set Wallpaper :( ", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            Toast.makeText(MainScreen.this, "Wallpaper set! ", Toast.LENGTH_SHORT).show();
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
            sliderBrightness.setY(sliderBrightnessPos);
            sliderTileSize.setY(sliderTileSizePos);
            sliderSaturation.setY(sliderSaturationPos);
            buttonSettings.setY(buttonSettingsPos);
            buttonApply.setY(buttonApplyPos);
            buttonShare.setY(buttonSharePos);

            sliderHue.setValue(MapParams.hue);
            sliderBrightness.setValue(MapParams.brightness);
            sliderTileSize.setValue(MapParams.tileSize);
            sliderSaturation.setValue(MapParams.saturation);

            buttonSettings.hideMe(buttonSettingsPos);
            buttonApply.hideMe(buttonSettingsPos);
            buttonShare.hideMe(buttonSettingsPos);
            sliderHue.showMe(buttonSettingsPos);
            sliderBrightness.showMe(buttonSettingsPos);
            sliderTileSize.showMe(buttonSettingsPos);
            sliderSaturation.showMe(buttonSettingsPos);
            slidersVisible = true;
        }
    }

    private void dismissSliders() {
        if (slidersVisible) {
            buttonSettings.setY(buttonSettingsPos);
            buttonSettings.showMe(buttonSettingsPos + buttonSettings.getHeight());

            buttonApply.setY(buttonApplyPos);
            buttonApply.showMe(buttonApplyPos + buttonApply.getHeight());

            buttonShare.setY(buttonSharePos);
            buttonShare.showMe(buttonSharePos + buttonShare.getHeight());

            sliderHue.hideMe(buttonSettingsPos);
            sliderBrightness.hideMe(buttonSettingsPos);
            sliderTileSize.hideMe(buttonSettingsPos);
            sliderSaturation.hideMe(buttonSettingsPos);
            slidersVisible = false;
        }

    }

    private void generateNoise() {
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

                float hsv[] = new float[3];
                Color.colorToHSV(RGB, hsv);
                hsv[1] = hsv[1] * (((float) MapParams.saturation) / 100);
                RGB = Color.HSVToColor(hsv);

                bitmap.setPixel(i, j, RGB);
            }
        }

        imageView.setImageBitmap(scaleToFit(bitmap, screenWidth, screenHeight));
        bitmap.recycle();
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


    // Can be triggered by a view event such as a button press
    public void onShareItem(ImageView v) {
        Uri bmpUri = getLocalBitmapUri(v);
        if (bmpUri != null) {
            // Construct a ShareIntent with link to image
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Created with OrganicPixel. Check it out! http://milenkojovanovic.com/OrganicPixelApp");
            shareIntent.setType("image/*");
            // Launch sharing dialog for image
            startActivity(Intent.createChooser(shareIntent, "Share Image"));
        } else {
            Toast.makeText(this, "Sorry, can't share! :(", Toast.LENGTH_SHORT).show();
        }
    }

    // Returns the URI path to the Bitmap displayed in specified ImageView
    public Uri getLocalBitmapUri(ImageView imageView) {
        // Extract Bitmap from ImageView drawable
        Drawable drawable = imageView.getDrawable();
        Bitmap bmp = null;
        if (drawable instanceof BitmapDrawable) {
            bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        } else {
            return null;
        }
        // Store image to default external storage directory
        Uri bmpUri = null;
        try {
            File file = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), "organic_pixel_" + System.currentTimeMillis() + ".png");
            file.getParentFile().mkdirs();
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }

}
