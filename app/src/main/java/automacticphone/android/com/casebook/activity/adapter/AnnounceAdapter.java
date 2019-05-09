package automacticphone.android.com.casebook.activity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


import automacticphone.android.com.casebook.MainActivity;
import automacticphone.android.com.casebook.R;
import automacticphone.android.com.casebook.activity.common.DataManager;
import automacticphone.android.com.casebook.activity.common.Define;
import automacticphone.android.com.casebook.activity.data.AnnounceData;
import automacticphone.android.com.casebook.activity.data.PromotionData;
import automacticphone.android.com.casebook.activity.network.RequestHttpURLConnection;

public class AnnounceAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    public ArrayList<AnnounceData> getDataList() {
        return dataList;
    }
    private ArrayList<AnnounceData> dataList = new ArrayList<AnnounceData>();

    public AnnounceAdapter(Context context, ArrayList<AnnounceData> arrayList)
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
            view = inflater.inflate(R.layout.list_announce_bulletin, parent, false);
        }

        //화면에 표시될 View(Layout이 inflate된) 으로 부터 위젝에 대한 참조 획득
        TextView textView = (TextView) view.findViewById(R.id.list_announce_data);
        textView.setText( dataList.get(position).getContent() );


        //date 포맷 변경
        String dateText = "";
        try
        {
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse( dataList.get(position).getTimeStamp() );
            dateText = new SimpleDateFormat("yy.MM.dd HH:mm").format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        textView = (TextView) view.findViewById(R.id.list_announcd_date_data);
        textView.setText( dateText );

        try{
            if(DataManager.inst().getUserData().getGrade()==Define.GRADE_ADMIN) {
                textView = (TextView) view.findViewById(R.id.list_announcd_grade_data);
                textView.setText(dataList.get(position).getGrade());
        }
        }catch(NullPointerException e){
            textView = (TextView) view.findViewById(R.id.list_announcd_grade_data);
            textView.setText("");
        }

        return view;
    }

    public void ChangeData( ArrayList<AnnounceData> list )
    {
        dataList = list;
        notifyDataSetChanged();
    }


}
