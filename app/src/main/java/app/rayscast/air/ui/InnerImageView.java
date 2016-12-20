package app.rayscast.air.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class InnerImageView extends ImageView {
    public InnerImageView(Context context) {
        super(context);
    }

    public InnerImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InnerImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        setMeasuredDimension(getMeasuredWidth(), (int)(getMeasuredWidth()/1.78)); //Snap to width
    }
}
