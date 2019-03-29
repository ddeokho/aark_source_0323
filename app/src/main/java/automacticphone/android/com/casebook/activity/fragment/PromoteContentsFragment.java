package automacticphone.android.com.casebook.activity.fragment;

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
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.kakao.kakaolink.v2.KakaoLinkResponse;
import com.kakao.kakaolink.v2.KakaoLinkService;
import com.kakao.message.template.LinkObject;
import com.kakao.message.template.TextTemplate;
import com.kakao.network.ErrorResult;
import com.kakao.network.callback.ResponseCallback;
import com.kakao.util.helper.log.Logger;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import automacticphone.android.com.casebook.R;
import automacticphone.android.com.casebook.activity.HomeActivity;
import automacticphone.android.com.casebook.activity.activity.ImageViewActivity;
import automacticphone.android.com.casebook.activity.adapter.PromoteListAdapter;
import automacticphone.android.com.casebook.activity.adapter.PromoteSubListAdapter;
import automacticphone.android.com.casebook.activity.common.DataManager;
import automacticphone.android.com.casebook.activity.common.Define;
import automacticphone.android.com.casebook.activity.data.PromotionData;
import automacticphone.android.com.casebook.activity.data.PromotionSubData;
import automacticphone.android.com.casebook.activity.data.UserData;
import automacticphone.android.com.casebook.activity.network.DownloadManager;
import automacticphone.android.com.casebook.activity.network.HttpConnectTask;
import automacticphone.android.com.casebook.activity.network.HttpTaskCallBack;
import automacticphone.android.com.casebook.activity.network.NetworkManager;
import automacticphone.android.com.casebook.activity.network.RequestHttpURLConnection;

public class PromoteContentsFragment extends Fragment
{
    private PromotionData promotionData;
    private MapView mapView = null;
    private HttpTaskCallBack mCallBack = null;

    public void setPromotionData(PromotionData promotionData) {
        this.promotionData = promotionData;
    }

    public PromoteContentsFragment()
    {

    }

    class BtnOnClickListener implements Button.OnClickListener {
        @Override
        public void onClick(View view)
        {
            switch (view.getId()) {
                case R.id.promote_contents_share_btn:
                    OnShareBtnClick();
                    break;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_promote_contents, container, false);
        TextView textView = view.findViewById(R.id.promote_content_name);
        textView.setText( promotionData.getName() );

        PromoteContentsFragment.BtnOnClickListener onClickListener = new PromoteContentsFragment.BtnOnClickListener();
        Button btn = (Button)view.findViewById(R.id.promote_contents_share_btn);
        btn.setOnClickListener( onClickListener );

        ArrayList<PromotionSubData> subList = new ArrayList<PromotionSubData>();
        for( int i = 0; i < DataManager.inst().getPromotionSubDataList().size(); ++i )
        {
            subList.add( DataManager.inst().getPromotionSubDataList().get(i) );
        }
        PromotionSubData addressData = new PromotionSubData();
        addressData.setPromotionData( promotionData );
        subList.add( addressData );

        LinearLayout subLayout = view.findViewById( R.id.promote_view_linerlayout);
        for( int i = 0; i < subList.size(); ++i )
        {
            PromotionSubData subData = subList.get(i);
            inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View subView = null;
            if( subData.getContentType() == PromoteSubListAdapter.TEXT_TYPE)
            {
                subView = inflater.inflate( R.layout.list_item_promote_contents, container, false );
                textView = (TextView) subView.findViewById(R.id.promote_content_title );
                textView.setText( subData.getTitle() );

                textView = (TextView) subView.findViewById(R.id.promote_content_text);
                textView.setText( subData.getContent() );
            }
            else if(subData.getContentType() == PromoteSubListAdapter.POSTER_TYPE)
            {
                subView = inflater.inflate( R.layout.list_item_promote_poster, container, false );
                textView = (TextView) subView.findViewById(R.id.promote_poster_title );
                textView.setText( subData.getTitle() );

                ImageView imgView = (ImageView) subView.findViewById(R.id.promote_poster_img );
                String url = RequestHttpURLConnection.serverIp + Define.downloadPath + subData.getImgName();
                Glide.with(getContext())
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
                                imgView.setTag( subData.getImgName() );
                                return false;
                            }
                        })
                        .into(imgView);

                imgView.setOnClickListener(new View.OnClickListener()
                {
                    public void onClick(View v)
                    {
                        String[] imgArray = { RequestHttpURLConnection.serverIp + Define.downloadPath + v.getTag().toString() };
                        Intent intent = new Intent(getActivity(), ImageViewActivity.class);
                        intent.putExtra("pos", 0 );
                        intent.putExtra("imagePathList", imgArray );
                        getActivity().startActivity(intent);
                    }
                });
            }
            else
            {
                subView = inflater.inflate(R.layout.list_item_promote_address, container, false);
                Button editBtn = (Button) subView.findViewById(R.id.promote_address_edit_btn);
                editBtn.setOnClickListener(onClickListener);

                Button hideBtn = (Button) subView.findViewById(R.id.promote_address_hide_btn);
                hideBtn.setOnClickListener(onClickListener);
                UserData userData = DataManager.inst().getUserData();
                PromotionData promotionData = subData.getPromotionData();
                if (userData != null) {
                    if (userData.getGrade() == Define.GRADE_ADMIN || userData.getSeq() == promotionData.getMemberSeq()) {
                        editBtn.setVisibility(View.VISIBLE);
                        hideBtn.setVisibility(View.VISIBLE);
                    } else {
                        editBtn.setVisibility(View.INVISIBLE);
                        hideBtn.setVisibility(View.INVISIBLE);
                    }
                } else {
                    editBtn.setVisibility(View.INVISIBLE);
                    hideBtn.setVisibility(View.INVISIBLE);
                }

                if (promotionData != null) {
                    textView = (TextView) subView.findViewById(R.id.promote_address_text);
                    String address = promotionData.getAddress();
                    address = address.replace("@", " ");
                    textView.setText(address);

                    /*주소를 오래 클릭했을 때 복사처리 되는 문 수정*/
                    textView.setLongClickable(true);
                    textView.setOnLongClickListener(new View.OnLongClickListener(){
                        @Override
                        public boolean onLongClick(View v){
                          TextView tv = (TextView) v;
                          final  String m = tv.getText().toString();
                          if(m.length() ==0)
                              return false;

                          AlertDialog.Builder dial = new AlertDialog.Builder(getContext());
                          dial.setTitle("주소");

                          final CharSequence[] items = {"복사"};

                          dial.setItems(items, new DialogInterface.OnClickListener() {
                              @Override
                              public void onClick(DialogInterface dialog, int which) {
                                  ClipboardManager clip = (ClipboardManager)getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                                  clip.setPrimaryClip(ClipData.newPlainText("text",m));
                                  dialog.dismiss();
                              }
                          });
                            dial.show();
                            return false;
                        }

                    });


                    //이메일, 전화번호 복사
                    TextView phoneTextView = (TextView) subView.findViewById(R.id.promote_address_phone);
                    phoneTextView.setText(promotionData.getPhone());
                    phoneTextView.setLongClickable(true);
                    phoneTextView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            TextView tv = (TextView) v;
                            final String m = tv.getText().toString();
                            if (m.length() == 0)
                                return false;

                            AlertDialog.Builder dial = new AlertDialog.Builder(getContext());
                            dial.setTitle("전화번호");

                            final CharSequence[] items = {"복사"};

                            dial.setItems(items, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ClipboardManager clip = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                                    clip.setPrimaryClip(ClipData.newPlainText("text", m));
                                    dialog.dismiss();
                                }
                            });

                            dial.show();

                            return false;
                        }
                    });


                    TextView emailTextView = (TextView) subView.findViewById(R.id.promote_address_email);
                    emailTextView.setText(promotionData.getEmail());
                    emailTextView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            TextView tv = (TextView) v;
                            final String m = tv.getText().toString();
                            if (m.length() == 0)
                                return false;

                            AlertDialog.Builder dial = new AlertDialog.Builder(getContext());
                            dial.setTitle("이메일");

                            final CharSequence[] items = {"복사"};

                            dial.setItems(items, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ClipboardManager clip = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                                    clip.setPrimaryClip(ClipData.newPlainText("text", m));
                                    dialog.dismiss();
                                }
                            });

                            dial.show();

                            return false;
                        }
                    });

                    // java code
                    if (mapView == null)
                    {
                        mapView = new MapView(getActivity());
                        ViewGroup mapViewContainer = (ViewGroup) subView.findViewById(R.id.promote_address_mapview);
                        mapViewContainer.addView(mapView);

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
                }
            }
            subLayout.addView( subView );
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
                            HomeActivity.inst().ShowAlertDialog( "홍보글이 숨김 처리 되었습니다." );
                            PromotionData promotionData = DataManager.inst().getSelectPromotionData();
                            DataManager.inst().DeletePromotionData( promotionData.getSeq() );
                            HomeActivity.inst().ChangeFragment( new PromoteListFragment(), "PromoteListFragment");
                        }
                    }
                }
                catch(JSONException e)
                {
                    e.printStackTrace();
                }
            }
        };

        return view;
    }

    void OnShareBtnClick()
    {
        String param = String.format( "promotion_seq=%d", promotionData.getSeq() );
        TextTemplate params = TextTemplate.newBuilder(promotionData.getTitle(), LinkObject.newBuilder().setAndroidExecutionParams( param ).build()).setButtonTitle("앱에서 바로 확인").build();

        Map<String, String> serverCallbackArgs = new HashMap<String, String>();
        serverCallbackArgs.put("user_id", "${current_user_id}");
        serverCallbackArgs.put("product_id", "${shared_product_id}");

        KakaoLinkService.getInstance().sendDefault(getContext(), params, serverCallbackArgs, new ResponseCallback<KakaoLinkResponse>() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                Logger.e(errorResult.toString());
            }

            @Override
            public void onSuccess(KakaoLinkResponse result) {
                // 템플릿 밸리데이션과 쿼터 체크가 성공적으로 끝남. 톡에서 정상적으로 보내졌는지 보장은 할 수 없다. 전송 성공 유무는 서버콜백 기능을 이용하여야 한다.
            }
        });
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
            HttpConnectTask httpConnectTask = new HttpConnectTask(url, values, getContext());
            httpConnectTask.SetCallBack(mCallBack);
            httpConnectTask.execute();

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }
}
