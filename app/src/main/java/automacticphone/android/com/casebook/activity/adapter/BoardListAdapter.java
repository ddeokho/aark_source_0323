package automacticphone.android.com.casebook.activity.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import automacticphone.android.com.casebook.R;
import automacticphone.android.com.casebook.activity.common.Define;
import automacticphone.android.com.casebook.activity.data.CaseData;
import automacticphone.android.com.casebook.activity.network.DownloadManager;
import automacticphone.android.com.casebook.activity.network.RequestHttpURLConnection;

public class BoardListAdapter extends BaseAdapter
{
    private Context context;
    private LayoutInflater inflater;

    public ArrayList<CaseData> getDataList() {
        return dataList;
    }

    private ArrayList<CaseData> dataList = new ArrayList<CaseData>();

    public BoardListAdapter(Context context, ArrayList<CaseData> arrayList)
    {
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.dataList = arrayList;
        this.context = context;
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
        //return dataList.get(position).getSeq();
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent)
    {
        CaseData data = dataList.get(position);
        if(view == null)
        {
            if( data.IsExistImg() )
            {
                view = inflater.inflate(R.layout.list_item_bulletin, parent, false);
                view.setTag( "list_item_bulletin" );
            }
            else
            {
                view = inflater.inflate(R.layout.list_item_bulletin_text, parent, false);
                view.setTag( "list_item_bulletin_text" );
            }
        }

        if( data != null )
        {
            if( data.IsExistImg() )
            {
                if( view.getTag().equals( "list_item_bulletin_text" ))
                {
                    view = inflater.inflate(R.layout.list_item_bulletin, parent, false);
                    view.setTag( "list_item_bulletin" );
                }

                ImageView img = view.findViewById(R.id.list_bulletin_img );
                String url = RequestHttpURLConnection.serverIp + Define.downloadPath + dataList.get(position).getImg_1();
                Glide.with(context)
                        .load(url)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(img);

                TextView textView = view.findViewById(R.id.list_bulletin_title);
                textView.setText( dataList.get(position).getTitle() );

                textView = view.findViewById(R.id.list_bulletin_category);
                textView.setText( dataList.get(position).getCategoryText() );

                textView = view.findViewById(R.id.list_bulletin_content);
                textView.setText( dataList.get(position).getContent() );


                //사례 년도
                textView = view.findViewById(R.id.list_bulletin_year);
                String data_year = String.format( "%d년 사례", dataList.get(position).getYear());
                textView.setText(data_year);
                //

                textView = view.findViewById(R.id.list_bulletin_hits);
                String hitsText = String.format( "조회수: %d", dataList.get(position).getFeq() );
                textView.setText( hitsText );


                //댓글
                textView = view.findViewById(R.id.list_bulletin_comment);
                try{
                    String hitsComment = String.format("댓글수: %d", dataList.get(position).getComment_count());//댓글
                    textView.setText(hitsComment);

                }catch(NullPointerException e){
                    textView.setText("댓글수: 0");
                }


                textView = view.findViewById(R.id.list_bulletin_date);
                String dateText = "";
                try
                {
                    Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse( dataList.get(position).getTimestamp() );
                    dateText = new SimpleDateFormat("yy.MM.dd").format(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                textView.setText( dateText );


                //현재 시간과 코멘트 또는 저장된 시간과 비교했을 때 new 판단
                textView = view.findViewById(R.id.list_bulletin_new);
                try
                {
                    //업로드 시간
                    Date up_date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dataList.get(position).getTimestamp());
                    //String up_dateText = new SimpleDateFormat("yyyyMMdd").format(up_date);//최종

                    //코멘트 등록 시간
                    Date com_date =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dataList.get(position).getComment_timestamp());
                    //String com_dateText = new SimpleDateFormat("yyyyMMdd").format(com_date);//최종

                    //현재 시간
                    long cur_ms_time= System.currentTimeMillis();
                    SimpleDateFormat day_time=new SimpleDateFormat("yyyy-MM-dd HH:mm:dd");
                    String cur_date_time= day_time.format(new Date(cur_ms_time));
                    Date cur_time=day_time.parse(cur_date_time);
                    //String cur_time_cal = new SimpleDateFormat("yyyyMMdd").format(cur_time);//최종

                    //System.out.print(Integer.parseInt(cur_time_cal)-
                    //android:background="@drawable/round_button_red"
                    //            android:textColor="#FFFFFF"
                    //            android:textStyle="bold"

                    //업로드와 현재 비교
                    long aa=60*1000*60*24*4;
                    long diff1=cur_time.getTime()-up_date.getTime();
                    long diff2=cur_time.getTime()-com_date.getTime();

                    if(diff1<aa && diff2<aa){
                        //초기 업로드 상황
                        textView.setText("new");
                        textView.setTextColor(Color.RED);

                    }else if(diff1<aa && diff2>=aa){
                        //코멘추 추가 상황 - timestamp만 최신화
                        textView.setText("new");
                        textView.setTextColor(Color.BLUE);

                    }else{
                        //둘 다 4일을 넘겼을 떄
                        textView.setText("");

                    }

                }catch(ParseException e){
                    e.printStackTrace();
                }


            }
            else
            {
                if( view.getTag().equals( "list_item_bulletin" ))
                {
                    view = inflater.inflate(R.layout.list_item_bulletin_text, parent, false);
                    view.setTag( "list_item_bulletin_text" );
                }

                TextView textView = view.findViewById(R.id.list_bulletin_text_title);
                textView.setText( dataList.get(position).getTitle() );

                textView = view.findViewById(R.id.list_bulletin_text_category);
                textView.setText( dataList.get(position).getCategoryText() );

                textView = view.findViewById(R.id.list_bulletin_text_content);
                textView.setText( dataList.get(position).getContent() );


                //사례 년도
                textView = view.findViewById(R.id.list_bulletin_text_year);
                String data_year = String.format( "%d년 사례", dataList.get(position).getYear());
                textView.setText(data_year);
                //


                textView = view.findViewById(R.id.list_bulletin_text_hits);
                String hitsText = String.format( "조회수: %d", dataList.get(position).getFeq() );
                textView.setText( hitsText );

                //댓글
                textView = view.findViewById(R.id.list_bulletin_text_comment);
                try{
                    String hitsComment = String.format("댓글수: %d", dataList.get(position).getComment_count());//댓글
                    textView.setText(hitsComment);

                }catch(NullPointerException e){
                    textView.setText("댓글수: 0");
                }

                textView = view.findViewById(R.id.list_bulletin_text_date);
                String dateText = "";
                try
                {
                    Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse( dataList.get(position).getTimestamp() );
                    dateText = new SimpleDateFormat("yy.MM.dd").format(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                textView.setText( dateText );




                //현재 시간과 코멘트 또는 저장된 시간과 비교했을 때 new 판단
                textView = view.findViewById(R.id.list_bulletin_new_text);
                try
                {
                    //업로드 시간
                    Date up_date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dataList.get(position).getTimestamp());
                    //String up_dateText = new SimpleDateFormat("yyyyMMdd").format(up_date);//최종

                    //코멘트 등록 시간
                    Date com_date =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dataList.get(position).getComment_timestamp());
                    //String com_dateText = new SimpleDateFormat("yyyyMMdd").format(com_date);//최종

                    //현재 시간
                    long cur_ms_time= System.currentTimeMillis();
                    SimpleDateFormat day_time=new SimpleDateFormat("yyyy-MM-dd HH:mm:dd");
                    String cur_date_time= day_time.format(new Date(cur_ms_time));
                    Date cur_time=day_time.parse(cur_date_time);
                    //String cur_time_cal = new SimpleDateFormat("yyyyMMdd").format(cur_time);//최종

                    //System.out.print(Integer.parseInt(cur_time_cal)-
                    //android:background="@drawable/round_button_red"
                    //            android:textColor="#FFFFFF"
                    //            android:textStyle="bold"

                    //업로드와 현재 비교
                    long aa=60*1000*60*24*3;
                    long diff1=cur_time.getTime()-up_date.getTime();
                    long diff2=cur_time.getTime()-com_date.getTime();//코멘트 업로드 시간 -> 업로드 저장 상태로 변경

                    if(diff1<aa && diff2<aa){
                        //초기 업로드 상황
                        textView.setText("new");
                        textView.setTextColor(Color.RED);

                    }else if(diff1<aa && diff2>=aa){
                        //코멘추 추가 상황 - timestamp만 최신화
                        textView.setText("new");
                        textView.setTextColor(Color.BLUE);
                    }
                    else{

                        textView.setText("");

                    }

                }catch(ParseException e){
                    e.printStackTrace();
                }

            }

        }

        return view;
    }

    public void ChangeData( ArrayList<CaseData> list )
    {
        dataList = list;
        notifyDataSetChanged();
    }
}
