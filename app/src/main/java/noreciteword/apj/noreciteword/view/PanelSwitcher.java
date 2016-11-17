package noreciteword.apj.noreciteword.view;

import android.app.Dialog;
import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;

import noreciteword.apj.noreciteword.Listener.MyAnimationListener;

/**
 * Created by CGT on 2016/11/16.
 */
public class PanelSwitcher extends FrameLayout {
    private static final int MAJOR_MOVE = 100;
    private static final int ANIM_DURATION = 200;
    private int mHeight = 0;
    private GestureDetector mGestureDetector;
    private TranslateAnimation outTop;
    private TranslateAnimation inBottom;
    private int maxHeight;
    private int startY;
    private boolean isScroll = false;
    private CURRENTVIEW mCurrentView = CURRENTVIEW.ABOVEVIEW;
    private Dialog dialog;

    private enum CURRENTVIEW {
        ABOVEVIEW, BEHINDVIEW;
    }

    public PanelSwitcher(Context context) {
        super(context);
        initContronl(context);
    }
    public PanelSwitcher(Context context, AttributeSet attrs) {
        super(context, attrs);
        initContronl(context);
    }

    private void initContronl(Context context) {
//        dialog = new Dialog(context, R.style.transparent_dialog);
        dialog = new Dialog(context);
        dialog.setCanceledOnTouchOutside(false);
        mGestureDetector = new GestureDetector(context,
                new GestureDetector.SimpleOnGestureListener() {
                    public boolean onFling(MotionEvent e1, MotionEvent e2,
                                           float velocityX, float velocityY) {
                        int dy = (int) (e2.getY() - e1.getY());
                        if (Math.abs(dy) > MAJOR_MOVE
                                && Math.abs(velocityX) < Math.abs(velocityY)) {
                            if (velocityY > 0) {
                                if (mCurrentView == CURRENTVIEW.BEHINDVIEW) {
                                    isScroll = true;
                                    moveBottomByFliping();
                                }


                            } else {
                                if (mCurrentView == CURRENTVIEW.ABOVEVIEW) {
                                    isScroll = true;
                                    moveTopByFliping();
                                }


                            }
                            return true;
                        } else {
                            return false;
                        }
                    }


                });
    }


    @Override
    public void onSizeChanged(int w, int h, int oldW, int oldH) {
        maxHeight = h;
        outTop = new TranslateAnimation(0, 0, mHeight, -h);
        inBottom = new TranslateAnimation(0, 0, mHeight, 0);
        outTop.setDuration(ANIM_DURATION);
        inBottom.setDuration(ANIM_DURATION);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                doActionDown(event);
                break;


            case MotionEvent.ACTION_MOVE:
                doActionMove(event);
                break;
            case MotionEvent.ACTION_UP:
                doActionUp(event);
                break;
        }
        return true;
    }

    private void doActionDown(MotionEvent event) {
        startY = (int) event.getY();
    }

    private void doActionMove(MotionEvent event) {
        int endY = (int) event.getY();
        int dy = endY - startY;
        moveView(dy);
        startY = endY;
    }


    private void doActionUp(MotionEvent event) {
        if (isScroll) {
            return;
        }
        if (mCurrentView == CURRENTVIEW.ABOVEVIEW) {
            if (Math.abs(mHeight) >= maxHeight / 3) {
                moveTopByRelease();
            } else {
                moveBottomByRelease();
            }
        }
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }


    /**
     * 松开手指移动到底部
     */
    private void moveBottomByRelease() {
        final View behindView = getChildAt(0);
        final View aboveView = getChildAt(1);
        aboveView.setVisibility(View.VISIBLE);
        inBottom = new TranslateAnimation(0, 0, mHeight, 0);
        inBottom.setDuration(800);
        aboveView.startAnimation(inBottom);
        lock();//屏蔽当前界面所有触摸事件
        inBottom.setAnimationListener(new MyAnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                transAnim(aboveView, 0, mHeight / 8, 100, 0,
                        new MyAnimationListener() {
                            @Override
                            public void onAnimationEnd(Animation animation) {
                                transAnim(aboveView, 0, mHeight / 16, 50, 0,
                                        new MyAnimationListener() {
                                            @Override
                                            public void onAnimationEnd(
                                                    Animation animation) {
                                                mCurrentView = CURRENTVIEW.ABOVEVIEW;
                                                mHeight = 0;
                                                behindView
                                                        .setVisibility(View.GONE);
                                                isScroll = false;
                                                unLock();
                                            }
                                        });
                            }
                        });
            }
        });
    }


    /**
     * 快速滑动移动到底部
     */
    public void moveBottomByFliping() {
        final View behindView = getChildAt(0);
        final View aboveView = getChildAt(1);
        aboveView.setVisibility(View.VISIBLE);
        inBottom = new TranslateAnimation(0, 0, mHeight, 0);
        inBottom.setDuration(200);
        aboveView.startAnimation(inBottom);
        lock();
        inBottom.setAnimationListener(new MyAnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                transAnim(aboveView, 0, -maxHeight / 32, 50, 0,
                        new MyAnimationListener() {
                            @Override
                            public void onAnimationEnd(Animation animation) {
                                transAnim(aboveView, 0, mHeight / 64, 25, 0,
                                        new MyAnimationListener() {
                                            @Override
                                            public void onAnimationEnd(
                                                    Animation animation) {
                                                mCurrentView = CURRENTVIEW.ABOVEVIEW;
                                                mHeight = 0;
                                                behindView
                                                        .setVisibility(View.GONE);
                                                isScroll = false;
                                                unLock();
                                            }
                                        });
                            }
                        });
            }
        });
    }


    /**
     *
     */
    private void transAnim(final View view, final int fromH, final int toH,
                           final int duration, int count, MyAnimationListener listener) {
        TranslateAnimation anim = new TranslateAnimation(0, 0, fromH, toH);
        anim.setDuration(duration);
        anim.setRepeatCount(1);
        anim.setFillAfter(true);
        anim.setRepeatMode(TranslateAnimation.REVERSE);
        view.startAnimation(anim);


        if (listener != null) {
            anim.setAnimationListener(listener);
        }
    }


    /***
     * 松开手指移动到顶部
     */
    private void moveTopByRelease() {
        final View aboveView = getChildAt(1);
        final View behindView = getChildAt(0);
        behindView.setVisibility(View.VISIBLE);
        outTop = new TranslateAnimation(0, 0, mHeight, -maxHeight);
        outTop.setDuration(800);
        lock();
        outTop.setAnimationListener(new MyAnimationListener() {


            @Override
            public void onAnimationEnd(Animation animation) {
                mCurrentView = CURRENTVIEW.BEHINDVIEW;
                mHeight = -maxHeight;
                aboveView.setVisibility(View.GONE);
                isScroll = false;
                unLock();
            }
        });
        aboveView.startAnimation(outTop);
    }


    /**
     * 快速滑动移动到顶部
     */
    private void moveTopByFliping() {
        final View aboveView = getChildAt(1);
        final View behindView = getChildAt(0);
        behindView.setVisibility(View.VISIBLE);
        outTop = new TranslateAnimation(0, 0, mHeight, -maxHeight);
        outTop.setDuration(ANIM_DURATION);
        lock();
        outTop.setAnimationListener(new MyAnimationListener() {

            public void onAnimationEnd(Animation animation) {
                mCurrentView = CURRENTVIEW.BEHINDVIEW;
                mHeight = -maxHeight;
                aboveView.setVisibility(View.GONE);
                isScroll = false;
                unLock();
            }
        });
        aboveView.startAnimation(outTop);
    }


    private void moveView(int distanceY) {
        if (mCurrentView == CURRENTVIEW.ABOVEVIEW) {
            View begindView = getChildAt(0);
            View aboveView = getChildAt(1);
            int endY = mHeight + distanceY;
            endY = mHeight + distanceY;
            endY = Math.min(Math.max(endY, -maxHeight), 0);
            TranslateAnimation anim = new TranslateAnimation(0, 0, mHeight,
                    endY);
            mHeight = endY;
            anim.setFillAfter(true);
            anim.setRepeatCount(-1);
            begindView.setVisibility(View.VISIBLE);
            aboveView.startAnimation(anim);
        }
    }


    public void setAboveView(View aboveView) {
        this.addView(aboveView);


    }


    public void setBehindView(View behindView) {
        this.addView(behindView, 0);
        behindView.setVisibility(View.GONE);
    }

    /**
     * 滑动时,屏蔽当前界面所有touch事件
     */
    private void lock() {
        if (dialog.isShowing()) {
            return;
        }
        dialog.show();
    }

    private void unLock() {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}

