package com.openogy.classes;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.openogy.www.anif.R;

import java.util.ArrayList;

public class AdapterViewPager extends PagerAdapter {

    private Context context;
    private LayoutInflater layoutInflater;

    private ArrayList<Bitmap> IMAGES;

    public AdapterViewPager(Context context, ArrayList<Bitmap> IMAGES) {
        this.context = context;
        this.IMAGES = IMAGES;
        layoutInflater = LayoutInflater.from(context);
    }



    @Override
    public int getCount() {
        return IMAGES.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {


        View imageLayout =  layoutInflater.inflate(R.layout.viewpager_layout, container, false);

        assert imageLayout != null;
        final ImageView imageView = (ImageView) imageLayout
                .findViewById(R.id.ImageViewSlider);


        imageView.setImageBitmap(IMAGES.get(position));

        container.addView(imageLayout);

        return imageLayout;


       /* layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.viewpager_layout,null);
        ImageView iv = view.findViewById(R.id.ImageViewSlider);
        iv.setImageResource(images[position]);
        ViewPager vp = (ViewPager) container;
        vp.addView(iv,0);
        return vp;*/
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }


}
