package com.example.jianming.views;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.example.jianming.Utils.DIOptionsNoneScaled;
import com.example.jianming.myapplication.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.download.ImageDownloader;

/**
 * Created by Jianming on 2015/9/22.
 */
public class YImageSlider extends ViewGroup implements YImageView.EdgeListener {
    public YImageSlider(Context context) {
        super(context);
        init(context);
    }

    public final static int SPLITE_W = 48;

    public YImageSlider(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public YImageSlider(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    private void init(Context context) {
        contentView = new YImageView(context, this, 0);
        contentView.setEdgeListener(this);
        hideLeft = new YImageView(context, this, -1);
        hideRight = new YImageView(context, this, 1);

        hideLeft.setEdgeListener(this);
        hideRight.setEdgeListener(this);

        backButton = new ImageView(context);
        nextButton = new ImageView(context);


        backButton.setImageResource(R.drawable.ic_keyboard_arrow_left_black_48dp);
        nextButton.setImageResource(R.drawable.ic_keyboard_arrow_right_black_48dp);
        backButton.setBackgroundColor(Color.parseColor("#80000000"));
        nextButton.setBackgroundColor(Color.parseColor("#80000000"));

        nextButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                contentView.doNextImgAnim();
            }
        });

        backButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                contentView.doBackImgAnim();
            }
        });

        addView(contentView);
        addView(hideLeft);
        addView(hideRight);

        addView(backButton);
        addView(nextButton);

        line31 = new View(context);
        line32 = new View(context);

        line31.setBackgroundColor(Color.parseColor("green"));
        line32.setBackgroundColor(Color.parseColor("blue"));

//        addView(line31);
//        addView(line32);
//        ImageLoader.getInstance().displayImage(ImageDownloader.Scheme.DRAWABLE.wrap(pics[index] + ""), hideLeft, DIOptionsNoneScaled.getInstance().getOptions());
//        ImageLoader.getInstance().displayImage(ImageDownloader.Scheme.DRAWABLE.wrap(pics[index + 1] + ""), contentView, DIOptionsNoneScaled.getInstance().getOptions());
//        ImageLoader.getInstance().displayImage(ImageDownloader.Scheme.DRAWABLE.wrap(pics[index + 2] + ""), hideRight, DIOptionsNoneScaled.getInstance().getOptions());
    }

    private YImageView contentView, hideLeft, hideRight;

    public YImageView getHideLeft() {
        return hideLeft;
    }

    public YImageView getHideRight() {
        return hideRight;
    }

    private ImageView backButton;

    private ImageView nextButton;

    private View line31, line32;

    public YImageView getContentView() {
        return contentView;
    }

    int index = 0;
    int pics[] = {R.drawable.su27_1, R.drawable.su27_2, R.drawable.su27_3, R.drawable.su27_4,
            R.drawable.su27_1, R.drawable.su27_2, R.drawable.su27_3, R.drawable.su27_4};

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int width = r - l;
        int height = b - t;
        contentView.layout(0, 0, width, height);
        hideLeft.layout(0, 0, width, height);
        hideRight.layout(0, 0, width, height);

        backButton.layout(0, 0, 48, 48);
        nextButton.layout(width - 48, 0, width, 48);

//        line31.layout(width / 3, 0, width / 3 + 1, height);
//        line32.layout(width * 2 / 3, 0, width * 2 / 3 + 1, height);
    }

    @Override
    public void onXEdge(YImageView yImageView) {

    }

    @Override
    public void onYEdge(YImageView yImageView) {

    }

    public void setHideLeftSrc(int index) {
        if (imgChangeListener != null) {
            String src = imgChangeListener.getImgSrcByIndex(index - 1, this);
            YImageView yImageView = getHideLeft();
            if (src != null) {
                ImageLoader.getInstance().displayImage(src, yImageView, DIOptionsNoneScaled.getInstance().getOptions());
                yImageView.setDisplay();
            } else {
                yImageView.setNoDisplay();
            }
        }
    }

    public void setContentSrc(int index) {

        if (imgChangeListener != null) {
            String src = imgChangeListener.getImgSrcByIndex(index, this);
            YImageView yImageView = getContentView();
            if (src != null) {
                ImageLoader.getInstance().displayImage(src, yImageView, DIOptionsNoneScaled.getInstance().getOptions());
                yImageView.setDisplay();
            } else {
                yImageView.setNoDisplay();
            }
        }
    }

    public void setHideRightSrc(int index) {
        if (imgChangeListener != null) {
            String src = imgChangeListener.getImgSrcByIndex(index + 1, this);
            YImageView yImageView = getHideRight();
            if (src != null) {
                ImageLoader.getInstance().displayImage(src, yImageView, DIOptionsNoneScaled.getInstance().getOptions());
                yImageView.setDisplay();
            } else {
                yImageView.setNoDisplay();
            }
        }
    }

    public interface ImgChangeListener {
        String onGetBackImg(YImageSlider yImageSlider);

        String onGetNextImg(YImageSlider yImageSlider);

        String getImgSrcByIndex(int index, YImageSlider yImageSlider);
    }

    public void setImgChangeListener(ImgChangeListener imgChangeListener) {
        this.imgChangeListener = imgChangeListener;
    }

    ImgChangeListener imgChangeListener = null;

    @Override
    public void onGetBackImg(YImageView yImageView) {
        YImageView tmp = contentView;
        contentView = hideLeft;
        hideLeft = hideRight;
        hideRight = tmp;

        contentView.setLocationIndex(0);
        hideLeft.setLocationIndex(-1);
        hideRight.setLocationIndex(1);
        alingLeftOrRight = 0;
        String imgUrl;

        if (imgChangeListener != null) {
            imgUrl = imgChangeListener.onGetBackImg(this);
        } else {
            index--;
            if (index < 0) {
                index = 3;
            }
            imgUrl = ImageDownloader.Scheme.DRAWABLE.wrap(pics[index] + "");
        }
        if (imgUrl != null) {
            ImageLoader.getInstance().displayImage(imgUrl, getHideLeft(), DIOptionsNoneScaled.getInstance().getOptions());
            getHideLeft().setDisplay();
        } else {
            getHideLeft().setNoDisplay();
        }

    }

    @Override
    public void onGetNextImg(YImageView yImageView) {
        YImageView tmp = contentView;
        contentView = hideRight;
        hideRight = hideLeft;
        hideLeft = tmp;

        contentView.setLocationIndex(0);
        hideLeft.setLocationIndex(-1);
        hideRight.setLocationIndex(1);
        alingLeftOrRight = 1;
        String imgUrl;
        if (imgChangeListener != null) {
            imgUrl = imgChangeListener.onGetNextImg(this);
        } else {
            index++;
            if (index > 2) {
                index = 0;
            }
            imgUrl = ImageDownloader.Scheme.DRAWABLE.wrap(pics[index + 2] + "");
        }
        if (imgUrl != null) {
            ImageLoader.getInstance().displayImage(imgUrl, hideRight, DIOptionsNoneScaled.getInstance().getOptions());
            hideRight.setDisplay();
        } else {
            hideRight.setNoDisplay();
        }
    }

    public int getAlingLeftOrRight() {
        return alingLeftOrRight;
    }

    private int alingLeftOrRight = 0;


}
