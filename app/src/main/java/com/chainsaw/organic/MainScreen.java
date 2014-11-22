package com.chainsaw.organic;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.chainsaw.organic.math.NoiseGenerator;


public class MainScreen extends Activity {
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
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
        int width = imageView.getWidth();
        int height = imageView.getHeight();

        int x = height;
        int y = width;

        Bitmap bitmap = Bitmap.createBitmap(x, y, Bitmap.Config.ARGB_8888);
        int randomize = (int) (Math.random() * 789221);
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                int pixel = NoiseGenerator.noise(i / 128f, j / 128f, 7, randomize);

                int red = pixel;
                int green = pixel;
                int blue = pixel;

                red = (red << 16) & 0x00FF0000;
                green = (green << 8) & 0x0000FF00;
                blue = blue & 0x000000FF;

                int RGB = 0xFF000000 | red | green | blue;

                bitmap.setPixel(i, j, RGB);
            }
        }

        imageView.setImageBitmap(scaleToFit(bitmap, width, height));
        bitmap.recycle();
        Log.i("Generator", "Image size " + width + " x " + height);
        Log.i("Generator", "DONE!!! " + randomize);
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
        return super.onOptionsItemSelected(item);
    }
}
