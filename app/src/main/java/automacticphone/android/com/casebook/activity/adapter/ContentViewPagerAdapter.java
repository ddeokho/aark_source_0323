package automacticphone.android.com.casebook.activity.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ssomai.android.scalablelayout.ScalableLayout;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;

import automacticphone.android.com.casebook.R;
import automacticphone.android.com.casebook.activity.activity.ImageViewActivity;
import automacticphone.android.com.casebook.activity.common.DataManager;
import automacticphone.android.com.casebook.activity.common.Define;
import automacticphone.android.com.casebook.activity.common.Util;
import automacticphone.android.com.casebook.activity.network.DownloadManager;
import automacticphone.android.com.casebook.activity.network.RequestHttpURLConnection;

public class ContentViewPagerAdapter extends PagerAdapter
{
    private ArrayList<String> imageList;
    private LayoutInflater inflater;
    private Context context;
    private Activity activity;
    private ArrayList<String> bitmapList = new ArrayList<String>();

    public ContentViewPagerAdapter(Context context, Activity activity, ArrayList<String> list )
    {
        this.context = context;
        this.imageList = list;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return imageList.size();
    }

    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position)
    {
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate( R.layout.layout_slider_photo, container, false );
        v.setTag( position );
        v.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                //this will log the page number that was click

                int pos = Integer.parseInt( v.getTag().toString() );
                String[] arr = new String[bitmapList.size()];
                arr = bitmapList.toArray(arr);
                Intent intent = new Intent(activity, ImageViewActivity.class);
                intent.putExtra("pos", pos );
                intent.putExtra("imagePathList", arr );
                activity.startActivity(intent);
            }
        });

        ImageView imageView = (ImageView)v.findViewById(R.id.slider_photo_img );
        String url = RequestHttpURLConnection.serverIp + Define.downloadPath + imageList.get(position);
        Glide.with(context)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
        bitmapList.add( url );

        container.addView( v );
        return v;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == ((ScalableLayout)o);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ((ViewPager)container).removeView((View)object);
    }
}
