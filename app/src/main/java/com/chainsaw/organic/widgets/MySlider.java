package com.chainsaw.organic.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.gc.materialdesign.views.Slider;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;

/**
 * Created by Chainsaw on 29-Nov-14.
 */
public class MySlider extends Slider {

    int ANIMATION_SPEED = 120;
    public MySlider(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void hideMe(final float endPos) {
        post(new Runnable() {
            @Override
            public void run() {
                final float originalY = ViewHelper.getY(MySlider.this);
                ObjectAnimator move = ObjectAnimator.ofFloat(MySlider.this, "y", endPos);
                move.setInterpolator(new DecelerateInterpolator());

                ObjectAnimator alpha = ObjectAnimator.ofFloat(MySlider.this, "alpha", 0);

                AnimatorSet set = new AnimatorSet();
                set.setDuration(ANIMATION_SPEED);
                set.play(move).with(alpha);

                move.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        MySlider.this.setVisibility(View.INVISIBLE);
                        ViewHelper.setY(MySlider.this, originalY);
                        animator.removeAllListeners();
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {

                    }
                });
                set.start();
            }
        });
    }


    public void showMe(final float startPos) {
        post(new Runnable() {
            @Override
            public void run() {
                float originalY = ViewHelper.getY(MySlider.this);
                ViewHelper.setY(MySlider.this, startPos);
                Log.i("SHOW ME ANIMATOR", "Y pos = " + originalY);
                ObjectAnimator move = ObjectAnimator.ofFloat(MySlider.this, "y", originalY);
                move.setInterpolator(new DecelerateInterpolator());

                ViewHelper.setAlpha(MySlider.this, 0);
                ObjectAnimator alpha = ObjectAnimator.ofFloat(MySlider.this, "alpha", 1);

                AnimatorSet set = new AnimatorSet();
                set.setDuration(ANIMATION_SPEED);
                set.play(move).with(alpha);

                move.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {
                        MySlider.this.setVisibility(View.VISIBLE);
                        animator.removeAllListeners();
                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {

                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {

                    }
                });

                set.start();
            }
        });
    }
}
