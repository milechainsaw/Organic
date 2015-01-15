package com.chainsaw.organic.widgets;

import android.content.Context;
import android.util.AttributeSet;
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
public class ValueSlider extends Slider {

    int ANIMATION_SPEED = 120;
    public ValueSlider(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void hideMe(final float endPos) {
        post(new Runnable() {
            @Override
            public void run() {
                final float originalY = ViewHelper.getY(ValueSlider.this);
                ObjectAnimator move = ObjectAnimator.ofFloat(ValueSlider.this, "y", endPos);
                move.setInterpolator(new DecelerateInterpolator());

                ObjectAnimator alpha = ObjectAnimator.ofFloat(ValueSlider.this, "alpha", 0);

                AnimatorSet set = new AnimatorSet();
                set.setDuration(ANIMATION_SPEED);
                set.play(move).with(alpha);

                move.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        ValueSlider.this.setVisibility(View.INVISIBLE);
                        ViewHelper.setY(ValueSlider.this, originalY);
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
                float originalY = ViewHelper.getY(ValueSlider.this);
                ViewHelper.setY(ValueSlider.this, startPos);
                ObjectAnimator move = ObjectAnimator.ofFloat(ValueSlider.this, "y", originalY);
                move.setInterpolator(new DecelerateInterpolator());

                ViewHelper.setAlpha(ValueSlider.this, 0);
                ObjectAnimator alpha = ObjectAnimator.ofFloat(ValueSlider.this, "alpha", 1);

                AnimatorSet set = new AnimatorSet();
                set.setDuration(ANIMATION_SPEED);
                set.play(move).with(alpha);

                move.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {
                        ValueSlider.this.setVisibility(View.VISIBLE);
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
