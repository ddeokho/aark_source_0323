package automacticphone.android.com.casebook.activity.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
        return dataList.get(position).getSeq();
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

                textView = view.findViewById(R.id.list_bulletin_hits);
                String hitsText = String.format( "조회수: %d", dataList.get(position).getFeq() );
                textView.setText( hitsText );

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

                textView = view.findViewById(R.id.list_bulletin_text_hits);
                String hitsText = String.format( "조회수: %d", dataList.get(position).getFeq() );
                textView.setText( hitsText );

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
