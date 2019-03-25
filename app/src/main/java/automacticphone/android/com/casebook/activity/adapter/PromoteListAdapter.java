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
import java.util.ArrayList;

import automacticphone.android.com.casebook.R;
import automacticphone.android.com.casebook.activity.common.Define;
import automacticphone.android.com.casebook.activity.common.Util;
import automacticphone.android.com.casebook.activity.data.PromotionData;
import automacticphone.android.com.casebook.activity.network.DownloadManager;
import automacticphone.android.com.casebook.activity.network.RequestHttpURLConnection;

public class PromoteListAdapter extends BaseAdapter
{
    private Context context;
    private LayoutInflater inflater;

    public ArrayList<PromotionData> getDataList() {
        return dataList;
    }

    private ArrayList<PromotionData> dataList = new ArrayList<PromotionData>();

    public PromoteListAdapter(Context context, ArrayList<PromotionData> arrayList)
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
        if(view == null) {
            view = inflater.inflate(R.layout.list_item_promote, parent, false);
        }

        //화면에 표시될 View(Layout이 inflate된) 으로 부터 위젝에 대한 참조 획득
        TextView textView = (TextView) view.findViewById(R.id.item_promote_name);
        textView.setText( dataList.get(position).getName() );

        textView = (TextView) view.findViewById(R.id.item_promote_date);
        textView.setText( dataList.get(position).getTimestamp() );

        textView = (TextView) view.findViewById(R.id.item_promote_title);
        textView.setText( dataList.get(position).getTitle() );

        ImageView logoView = (ImageView) view.findViewById(R.id.item_promote_logo);
        String url = RequestHttpURLConnection.serverIp + Define.downloadPath + dataList.get(position).getLogImg();
        Glide.with(context)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(logoView);

        return view;
    }

    public void ChangeData( ArrayList<PromotionData> list )
    {
        dataList = list;
        notifyDataSetChanged();
    }
}
