package app.rayscast.air.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;
import java.util.List;

public class ImageSliderAdapter extends PagerAdapter {

    private Context mContext;
    private File imagesFile;
    private List<File> currentImagesInDirectory;;



    public ImageSliderAdapter(Context context, File file) {
        this.mContext = context;
        this.imagesFile = file;
    }

    public ImageSliderAdapter(Context context, List<File> list) {
        this.mContext = context;
        this.currentImagesInDirectory = list;

    }


    @Override
    public int getCount() {
        //return imagesFile.listFiles().length;
        return currentImagesInDirectory.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((ImageView) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        ImageView imageView = new ImageView(mContext);
        int padding = 8;
        imageView.setPadding(padding, padding, padding, padding);
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

        //File imageFile = imagesFile.listFiles()[position];
        File imageFile = currentImagesInDirectory.get(position);

        String url = "file://" + imageFile.getAbsolutePath();

        Glide.with(mContext)
                .load(imageFile)
                .fitCenter()
                //.thumbnail(0.5f)                            //provide the size multiplier for thumbnail size
                .crossFade()                                //for a smoothly fading in the loading image into ImageView
                //.placeholder(R.drawable.noimage)
                //.override(200,200)
                .diskCacheStrategy(DiskCacheStrategy.ALL)   //saves the source and result data (images) into cache. AKA faster loading, but larger cache. Alternatively use DiskCacheStrategy.RESULT
                .into(imageView);


        ((ViewPager) container).addView(imageView, 0);
        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((ImageView) object);
    }
}
