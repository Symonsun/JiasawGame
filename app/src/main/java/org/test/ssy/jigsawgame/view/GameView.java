package org.test.ssy.jigsawgame.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Image;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;


import org.test.ssy.jigsawgame.R;
import org.test.ssy.jigsawgame.util.ImagePiece;
import org.test.ssy.jigsawgame.util.ImageSplitterUtil;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 自定义容器
 * Created by sunsiyuan on 2017/10/12.
 */
public class GameView extends RelativeLayout implements View.OnClickListener {

    private int mColumn = 3;

    private int mPadding;

    private int mMagin = 3;

    private ImageView[] mGameOintuItems;

    private int mItemWidth;

    private Bitmap mBitmap;

    private List<ImagePiece> mItemBitmaps;

    private boolean once;

    private int mWidth;

    // 点击的第一张图和第二张图，进行交换
    private ImageView mFirst;
    private ImageView mSecond;

    //动画曾，覆盖在viewGroup中
    private RelativeLayout mAnimLayout;

    public GameView(Context context) {
        this(context, null);
    }

    public GameView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        mMagin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3,
                getResources().getDisplayMetrics());
        mPadding = min(getPaddingLeft(), getPaddingRight(), getPaddingTop(), getPaddingBottom());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mWidth = Math.min(getMeasuredHeight(), getMeasuredWidth());

        if (!once) {
            initBitmap();

            initItem();
            once = true;
        }
        setMeasuredDimension(mWidth, mWidth);
    }


    private void initBitmap() {
        if (mBitmap == null) {
            mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.xiaomai);
        }

        mItemBitmaps = ImageSplitterUtil.splitImage(mBitmap, mColumn);

        Collections.sort(mItemBitmaps, new Comparator<ImagePiece>() {
            @Override
            public int compare(ImagePiece o1, ImagePiece o2) {
                return Math.random() > 0.5 ? 1 : -1;
            }
        });
    }

    private void initItem() {
        mItemWidth = (mWidth - mPadding * 2 - mMagin * (mColumn - 1)) / mColumn;

        mGameOintuItems = new ImageView[mColumn * mColumn];

        for (int i = 0; i < mGameOintuItems.length; i++) {
            ImageView item = new ImageView(getContext());
            item.setOnClickListener(this);

            item.setImageBitmap(mItemBitmaps.get(i).getBitmap());

            mGameOintuItems[i] = item;

            item.setId(i + 1);
            item.setTag(i + "_" + mItemBitmaps.get(i).getIndex());

            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(mItemWidth, mItemWidth);

            if (i + 1 % mColumn != 0) {
                lp.rightMargin = mMagin;
            }

            if (i % mColumn != 0) {
                lp.addRule(RelativeLayout.RIGHT_OF, mGameOintuItems[i - 1].getId());
            }

            if ((i + 1) > mColumn) {
                lp.topMargin = mMagin;
                lp.addRule(RelativeLayout.BELOW, mGameOintuItems[i - mColumn].getId());
            }
            addView(item, lp);
        }
    }

    /**
     *
     * @param params
     * @return
     */
    private int min(int... params) {
        int min = params[0];

        for (int param : params) {
            if (param < min) {
                min = param;
            }
        }
        return min;
    }

    /**
     * 点击事件
     * @param v
     */
    @Override
    public void onClick(View v) {

        //重复点击
        if (mFirst == v) {
            mFirst.setColorFilter(null);
            mFirst = null;
            return;
        }

        if (mFirst == null) {
            mFirst = (ImageView) v;
            //设置选中效果
            mFirst.setColorFilter(Color.parseColor("#55FF0000"));
        } else {
            mSecond = (ImageView) v;
            //交换
            exchangeView();
        }
    }

    /**
     * 图片交换
     */
    private void exchangeView() {
        //先去掉颜色
        mFirst.setColorFilter(null);
        //构造动画层
        setUpAnimLayout();

        ImageView first = new ImageView(getContext());
        final Bitmap firstBitmap = mItemBitmaps.get(
                getImageIdByTag((String) mFirst.getTag())).getBitmap();
        first.setImageBitmap(firstBitmap);
        LayoutParams lp = new LayoutParams(mItemWidth, mItemWidth);
        lp.leftMargin = mFirst.getLeft() - mPadding;
        lp.topMargin = mFirst.getTop() - mPadding;
        first.setLayoutParams(lp);
        mAnimLayout.addView(first);

        ImageView second = new ImageView(getContext());
        final Bitmap secondBitmap = mItemBitmaps.get(
                getImageIdByTag((String) mSecond.getTag())).getBitmap();
        second.setImageBitmap(firstBitmap);
        LayoutParams lp2 = new LayoutParams(mItemWidth, mItemWidth);
        lp2.leftMargin = mSecond.getLeft() - mPadding;
        lp2.topMargin = mSecond.getTop() - mPadding;
        second.setLayoutParams(lp2);
        mAnimLayout.addView(second);

        //设置动画
        TranslateAnimation anim = new TranslateAnimation(0, mSecond.getLeft() - mFirst.getLeft(), 0,
                mSecond.getTop() - mFirst.getTop());
        anim.setDuration(300);
        anim.setFillAfter(true);
        first.startAnimation(anim);

        TranslateAnimation animSecond = new TranslateAnimation(0, -mSecond.getLeft() +
                mFirst.getLeft(), 0, -mSecond.getTop() + mFirst.getTop());
        animSecond.setDuration(300);
        animSecond.setFillAfter(true);
        second.startAnimation(animSecond);

        //监听动画
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mFirst.setVisibility(View.INVISIBLE);
                mSecond.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                String firstTag = (String) mFirst.getTag();
                String secondTag = (String) mSecond.getTag();

                mFirst.setImageBitmap(secondBitmap);
                mSecond.setImageBitmap(firstBitmap);

                mFirst.setTag(secondTag);
                mSecond.setTag(firstTag);

                mFirst.setVisibility(View.VISIBLE);
                mSecond.setVisibility(View.VISIBLE);

                mFirst = mSecond = null;
                mAnimLayout.removeAllViews();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        String firstTag = (String) mFirst.getTag();
        String secondTag = (String) mSecond.getTag();

        String[] firstParams = firstTag.split("_");
        String[] secondParams = secondTag.split("_");

        //获取到bitmap并且交换
        mSecond.setImageBitmap(mItemBitmaps.get(Integer.parseInt(firstParams[0])).getBitmap());
        mFirst.setImageBitmap(mItemBitmaps.get(Integer.parseInt(secondParams[0])).getBitmap());
        //改变tag
        mFirst.setTag(secondTag);
        mSecond.setTag(firstTag);
        //回到最初状态
        mFirst = mSecond = null;
    }

    /**
     * 获取tag
     * @param tag
     * @return
     */
    public int getImageIdByTag(String tag) {
        String[] split = tag.split("_");
        return Integer.parseInt(split[0]);
    }

    /**
     * 获取图片的tag
     * @param tag
     * @return
     */
    public int getImageIndex(String tag) {
        String[] split = tag.split("_");
        return Integer.parseInt(split[1]);
    }

    /**
     * 交互动画
     */
    private void setUpAnimLayout() {
        if (mAnimLayout == null) {
            mAnimLayout = new RelativeLayout(getContext());
            addView(mAnimLayout);
        }
    }
}