package automacticphone.android.com.casebook.activity.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import automacticphone.android.com.casebook.R;
import automacticphone.android.com.casebook.activity.adapter.ContentViewPagerAdapter;
import automacticphone.android.com.casebook.activity.adapter.ImageViewPagerAdapter;
import automacticphone.android.com.casebook.activity.common.CustomViewpager;
import uk.co.senab.photoview.PhotoViewAttacher;

public class ImageViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);

        Intent intent = getIntent();
        int pos = intent.getIntExtra( "pos", 0 );
        String[] filePathArray = intent.getStringArrayExtra( "imagePathList" );
        ArrayList<String> urlList = new ArrayList<String>(Arrays.asList( filePathArray ));

        CustomViewpager viewPager = (CustomViewpager) findViewById( R.id.image_view_photo_viewpager );
        ImageViewPagerAdapter adapter = new ImageViewPagerAdapter( getApplicationContext(), urlList );
        viewPager.setAdapter( adapter );
        viewPager.setCurrentItem( pos );
    }
}
