package com.gc.materialdesign.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gc.materialdesign.R;
import com.gc.materialdesign.utils.Utils;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;


public class ButtonFloat extends Button {

    int sizeIcon = 24;
    int sizeRadius = 28;
    private int ANIMATION_SPEED = 120;

    ImageView icon; // Icon of float button
    Drawable drawableIcon;


    public ButtonFloat(Context context, AttributeSet attrs) {
        super(context, attrs);
        setBackgroundResource(R.drawable.background_button_float);
        setVisibility(View.INVISIBLE);
        sizeRadius = 28;
        setDefaultProperties();
        icon = new ImageView(context);
        icon.setAdjustViewBounds(true);
        icon.setScaleType(ScaleType.CENTER_CROP);
        if (drawableIcon != null) {
            icon.setImageDrawable(drawableIcon);
//			try {
//				icon.setBackground(drawableIcon);
//			} catch (NoSuchMethodError e) {
//				icon.setBackgroundDrawable(drawableIcon);
//			}
        }
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(Utils.dpToPx(sizeIcon, getResources()), Utils.dpToPx(sizeIcon, getResources()));
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        icon.setLayoutParams(params);
        addView(icon);

    }

    protected void setDefaultProperties() {
        rippleSpeed = Utils.dpToPx(2, getResources());
        rippleSize = Utils.dpToPx(5, getResources());
        setMinimumWidth(Utils.dpToPx(sizeRadius * 2, getResources()));
        setMinimumHeight(Utils.dpToPx(sizeRadius * 2, getResources()));
        super.background = R.drawable.background_button_float;
//		super.setDefaultProperties();
    }


    // Set atributtes of XML to View
    protected void setAttributes(AttributeSet attrs) {
        //Set background Color
        // Color by resource
        int bacgroundColor = attrs.getAttributeResourceValue(ANDROIDXML, "background", -1);
        if (bacgroundColor != -1) {
            setBackgroundColor(getResources().getColor(bacgroundColor));
        } else {
            // Color by hexadecimal
            background = attrs.getAttributeIntValue(ANDROIDXML, "background", -1);
            if (background != -1)
                setBackgroundColor(background);
        }

        // Set Ripple Color
        // Color by resource
        int rippleColor = attrs.getAttributeResourceValue(MATERIALDESIGNXML,
                "rippleColor", -1);
        if (rippleColor != -1) {
            setRippleColor(getResources().getColor(rippleColor));
        } else {
            // Color by hexadecimal
            int background = attrs.getAttributeIntValue(MATERIALDESIGNXML, "rippleColor", -1);
            if (background != -1)
                setRippleColor(background);
            else
                setRippleColor(makePressColor());
        }
        // Icon of button
        int iconResource = attrs.getAttributeResourceValue(MATERIALDESIGNXML, "iconFloat", -1);
        if (iconResource != -1)
            drawableIcon = getResources().getDrawable(iconResource);
//        boolean animate = attrs.getAttributeBooleanValue(MATERIALDESIGNXML, "animate", false);
//        if (animate) {
//            showMe();
//        }

    }

    public void showMe() {
        ButtonFloat.this.setVisibility(View.VISIBLE);
        post(new Runnable() {
            @Override
            public void run() {
                float originalY = ViewHelper.getY(ButtonFloat.this);
                ViewHelper.setY(ButtonFloat.this, ViewHelper.getY(ButtonFloat.this) + getHeight() * 3);
                ObjectAnimator animator = ObjectAnimator.ofFloat(ButtonFloat.this, "y", originalY);
                animator.setInterpolator(new DecelerateInterpolator());
                animator.setDuration(ANIMATION_SPEED);
                animator.start();
            }
        });
    }

    public void showMe(final float startPos) {
        post(new Runnable() {
            @Override
            public void run() {
                float originalY = ViewHelper.getY(ButtonFloat.this);
                ViewHelper.setY(ButtonFloat.this, startPos);
                ObjectAnimator move = ObjectAnimator.ofFloat(ButtonFloat.this, "y", originalY);
                move.setInterpolator(new DecelerateInterpolator());

                ViewHelper.setAlpha(ButtonFloat.this, 0);
                ObjectAnimator alpha = ObjectAnimator.ofFloat(ButtonFloat.this, "alpha", 1);

                AnimatorSet set = new AnimatorSet();
                set.setDuration(ANIMATION_SPEED);
                set.play(move).with(alpha);

                move.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {
                        ButtonFloat.this.setVisibility(View.VISIBLE);
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

    public void hideMe() {
        post(new Runnable() {
            @Override
            public void run() {
                final float originalY = ViewHelper.getY(ButtonFloat.this);
                float finalY = ViewHelper.getY(ButtonFloat.this) + getHeight() * 3;
                ObjectAnimator animator = ObjectAnimator.ofFloat(ButtonFloat.this, "y", finalY);
                animator.setInterpolator(new DecelerateInterpolator());
                animator.setDuration(ANIMATION_SPEED);
                animator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        ButtonFloat.this.setVisibility(View.INVISIBLE);
                        ViewHelper.setY(ButtonFloat.this, originalY);
                        animator.removeAllListeners();
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {

                    }
                });
                animator.start();
            }
        });
    }

    public void hideMe(final float endPos) {
        post(new Runnable() {
            @Override
            public void run() {
                final float originalY = ViewHelper.getY(ButtonFloat.this);
                ObjectAnimator move = ObjectAnimator.ofFloat(ButtonFloat.this, "y", endPos);
                move.setInterpolator(new DecelerateInterpolator());

                ObjectAnimator alpha = ObjectAnimator.ofFloat(ButtonFloat.this, "alpha", 0);

                AnimatorSet set = new AnimatorSet();
                set.setDuration(ANIMATION_SPEED);
                set.play(move).with(alpha);

                move.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        ButtonFloat.this.setVisibility(View.INVISIBLE);
                        ViewHelper.setY(ButtonFloat.this, originalY);
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

    Integer height;
    Integer width;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (x != -1) {
            Rect src = new Rect(0, 0, getWidth(), getHeight());
            Rect dst = new Rect(Utils.dpToPx(1, getResources()), Utils.dpToPx(2, getResources()), getWidth() - Utils.dpToPx(1, getResources()), getHeight() - Utils.dpToPx(2, getResources()));
            canvas.drawBitmap(cropCircle(makeCircle()), src, dst, null);
        }
        invalidate();
    }


    public ImageView getIcon() {
        return icon;
    }

    public void setIcon(ImageView icon) {
        this.icon = icon;
    }

    public Drawable getDrawableIcon() {
        return drawableIcon;
    }

    public void setDrawableIcon(Drawable drawableIcon) {
        this.drawableIcon = drawableIcon;
        try {
            icon.setBackground(drawableIcon);
        } catch (NoSuchMethodError e) {
            icon.setBackgroundDrawable(drawableIcon);
        }
    }

    public Bitmap cropCircle(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    @Override
    public TextView getTextView() {
        return null;
    }

    public void setRippleColor(int rippleColor) {
        this.rippleColor = rippleColor;
    }
}
