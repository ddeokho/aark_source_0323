package automacticphone.android.com.casebook.activity.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import automacticphone.android.com.casebook.R;
import automacticphone.android.com.casebook.activity.HomeActivity;
import automacticphone.android.com.casebook.activity.activity.ImageViewActivity;
import automacticphone.android.com.casebook.activity.common.DataManager;
import automacticphone.android.com.casebook.activity.common.Define;
import automacticphone.android.com.casebook.activity.common.Util;
import automacticphone.android.com.casebook.activity.data.PromotionData;
import automacticphone.android.com.casebook.activity.data.PromotionSubData;
import automacticphone.android.com.casebook.activity.data.UserData;
import automacticphone.android.com.casebook.activity.fragment.PromoteRegisterFragment;
import automacticphone.android.com.casebook.activity.network.DownloadManager;
import automacticphone.android.com.casebook.activity.network.HttpConnectTask;
import automacticphone.android.com.casebook.activity.network.HttpTaskCallBack;
import automacticphone.android.com.casebook.activity.network.RequestHttpURLConnection;
import uk.co.senab.photoview.PhotoViewAttacher;

public class PromoteSubListAdapter extends BaseAdapter
{
    public static final int TEXT_TYPE = 0;
    public static final int POSTER_TYPE = 1;
    public static final int ADDRESS_TYPE = 2;

    private Context context;
    private Activity activity;
    private LayoutInflater inflater;
    private MapView mapView = null;
    private ArrayList<PromotionSubData> dataList = new ArrayList<PromotionSubData>();
    private HttpTaskCallBack mCallBack = null;
    private ArrayList<View> viewList = new ArrayList<View>();

    public PromoteSubListAdapter(Context context, Activity activity, ArrayList<PromotionSubData> arrayList)
    {
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.dataList = arrayList;
        this.context = context;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return dataList.get(position).getSeq();
    }

    class BtnOnClickListener implements Button.OnClickListener {
        @Override
        public void onClick(View view)
        {
            switch (view.getId()) {
                case R.id.promote_address_edit_btn:
                {
                    PromoteRegisterFragment fragment = new PromoteRegisterFragment();
                    fragment.setPromotionData(DataManager.inst().getSelectPromotionData());
                    HomeActivity.inst().ChangeFragment( fragment, "PromoteRegisterFragment");
                }
                break;
                case R.id.promote_address_hide_btn:
                {
                    HidePromotion();
                }
                break;
            }
        }
    }

    @Override
    public View getView(int position, View view, ViewGroup parent)
    {
            if( dataList.get(position).getPromotionData() == null )
            {
                if( dataList.get(position).getContent().length() > 0 )
                {
                    view = inflater.inflate(R.layout.list_item_promote_contents, parent, false);
                }
                else
                {
                    view = inflater.inflate(R.layout.list_item_promote_poster, parent, false);
                }
            }
            else
            {
                view = inflater.inflate(R.layout.list_item_promote_address, parent, false);
            }

        if(  dataList.get(position).getContentType() == TEXT_TYPE )
        {
            TextView textView = (TextView) view.findViewById(R.id.promote_content_title );
            textView.setText( dataList.get(position).getTitle() );

            textView = (TextView) view.findViewById(R.id.promote_content_text);
            textView.setText( dataList.get(position).getContent() );
        }
        else if( dataList.get(position).getContentType() == POSTER_TYPE )
        {
            TextView textView = (TextView) view.findViewById(R.id.promote_poster_title );
            textView.setText( dataList.get(position).getTitle() );

            ImageView imgView = (ImageView) view.findViewById(R.id.promote_poster_img );
            String url = RequestHttpURLConnection.serverIp + Define.downloadPath + dataList.get(position).getImgName();
            Glide.with(context)
                    .load(url)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            // log exception
                            Log.e("TAG", "Error loading image", e);
                            return false; // important to return false so the error placeholder can be placed
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            imgView.setTag( dataList.get(position).getImgName() );
                            return false;
                        }
                    })
                    .into(imgView);


            imgView.setOnClickListener(new View.OnClickListener()
            {
                public void onClick(View v)
                {
                    String[] imgArray = {  RequestHttpURLConnection.serverIp + Define.downloadPath + v.getTag().toString() };
                    Intent intent = new Intent(activity, ImageViewActivity.class);
                    intent.putExtra("pos", 0 );
                    intent.putExtra("imagePathList", imgArray );
                    activity.startActivity(intent);
                }
            });
        }
        else if( dataList.get(position).getContentType() == ADDRESS_TYPE )
        {
            PromoteSubListAdapter.BtnOnClickListener onClickListener = new PromoteSubListAdapter.BtnOnClickListener();
            Button editBtn = (Button)view.findViewById(R.id.promote_address_edit_btn);
            editBtn.setOnClickListener( onClickListener );

            Button hideBtn = (Button)view.findViewById(R.id.promote_address_hide_btn);
            hideBtn.setOnClickListener( onClickListener );
            UserData userData = DataManager.inst().getUserData();
            PromotionData promotionData = dataList.get(position).getPromotionData();
            if( userData != null )
            {
                if( userData.getGrade() == Define.GRADE_ADMIN  || userData.getSeq() == promotionData.getMemberSeq() )
                {
                    editBtn.setVisibility(View.VISIBLE);
                    hideBtn.setVisibility(View.VISIBLE);
                }
                else
                {
                    editBtn.setVisibility(View.INVISIBLE);
                    hideBtn.setVisibility(View.INVISIBLE);
                }
            }
            else
            {
                editBtn.setVisibility(View.INVISIBLE);
                hideBtn.setVisibility(View.INVISIBLE);
            }

            if( promotionData != null )
            {
                TextView textView = (TextView) view.findViewById(R.id.promote_address_text );
                String address = promotionData.getAddress();
                address = address.replace( "@", " ");
                textView.setText( address );

                TextView addressTextView = (TextView) view.findViewById(R.id.promote_address_phone );
                textView.setText( promotionData.getPhone() );
                addressTextView.setLongClickable(true);
                addressTextView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        TextView tv = (TextView)v;
                        final String m = tv.getText().toString();
                        if( m.length() == 0 )
                            return false;

                        AlertDialog.Builder dial = new AlertDialog.Builder(context);
                        dial.setTitle("전화번호");

                        final CharSequence[] items ={"복사"};

                        dial.setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ClipboardManager clip = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
                                clip.setPrimaryClip(ClipData.newPlainText( "text", m ));
                                dialog.dismiss();
                            }
                        });

                        dial.show();

                        return false;
                    }
                });


                TextView emailTextView = (TextView) view.findViewById(R.id.promote_address_email );
                emailTextView.setText( promotionData.getEmail() );
                emailTextView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        TextView tv = (TextView)v;
                        final String m = tv.getText().toString();
                        if( m.length() == 0 )
                            return false;

                        AlertDialog.Builder dial = new AlertDialog.Builder(context);
                        dial.setTitle("이메일");

                        final CharSequence[] items ={"복사"};

                        dial.setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ClipboardManager clip = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
                                clip.setPrimaryClip(ClipData.newPlainText( "text", m ));
                                dialog.dismiss();
                            }
                        });

                        dial.show();

                        return false;
                    }
                });

                // java code
                if( mapView == null )
                {
                    mapView = new MapView( activity );
                    ViewGroup mapViewContainer = (ViewGroup) view.findViewById( R.id.promote_address_mapview);
                    mapViewContainer.addView( mapView );

                    MapPoint MARKER_POINT = MapPoint.mapPointWithGeoCoord(promotionData.getLongitude(), promotionData.getLatitude());
                    // 중심점 변경  - 즐겨 찾기에서 받아 오거나 자신의 현위치를 받아서 설정하자.
                    mapView.setMapCenterPoint(MARKER_POINT, true);

                    MapPOIItem marker = new MapPOIItem();
                    marker.setItemName("Default Marker");
                    marker.setTag(0);
                    marker.setMapPoint(MARKER_POINT);
                    marker.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
                    marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

                    mapView.addPOIItem(marker);
                }
                else
                {
                    ViewGroup mapViewContainer = (ViewGroup) view.findViewById( R.id.promote_address_mapview);
                    mapViewContainer.addView( mapView );
                }
            }

            mCallBack = new HttpTaskCallBack()
            {
                @Override
                public void doProcess(JSONObject jsonObj)
                {
                    try
                    {
                        if(jsonObj.get("packet_id").equals("hide_promotion"))
                        {
                            if( jsonObj.get("result").equals("true") )
                            {

                            }
                        }
                    }
                    catch(JSONException e)
                    {
                        e.printStackTrace();
                    }
                }
            };
        }

        return view;
    }

    int getShowState()
    {
        PromotionData promotionData = DataManager.inst().getSelectPromotionData();
        if( promotionData.getType() == Define.PROMOTION_STUDENT )
            return 3;
        else
            return 4;
    }

    void HidePromotion()
    {
        JSONObject jsonObj = new JSONObject();
        try
        {
            PromotionData promotionData = DataManager.inst().getSelectPromotionData();
            jsonObj.put("packet_id", "hide_promotion");
            jsonObj.put("seq", promotionData.getSeq() );
            jsonObj.put("show_state", getShowState() );
            ContentValues values = new ContentValues();
            values.put("param", jsonObj.toString() );

            String url = RequestHttpURLConnection.serverIp + "/hide_promotion.php";
            HttpConnectTask httpConnectTask = new HttpConnectTask(url, values, context);
            httpConnectTask.SetCallBack(mCallBack);
            httpConnectTask.execute();

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }
}
