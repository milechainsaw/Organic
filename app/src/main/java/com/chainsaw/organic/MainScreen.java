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
        Log.i("Generator", "Image size " + width + " x " + height);

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        int randomize = (int)(Math.random() * 789221);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int pixel = NoiseGenerator.noise(i / 128f, j / 128f, 7, randomize);
                int red = pixel;
                int green = pixel;
                int blue = pixel;

                red = (red << 16) & 0x00FF0000; //Shift red 16-bits and mask out other stuff
                green = (green << 8) & 0x0000FF00; //Shift Green 8-bits and mask out other stuff
                blue = blue & 0x000000FF; //Mask out anything not blue.

                int RGB = 0xFF000000 | red | green | blue;

                bitmap.setPixel(i, j, RGB);
            }
        }
        Log.i("Generator", "DONE!!! " + randomize);
        imageView.setImageBitmap(bitmap);

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
