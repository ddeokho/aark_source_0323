package automacticphone.android.com.casebook.activity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import automacticphone.android.com.casebook.R;
import automacticphone.android.com.casebook.activity.data.RegulationMainData;

public class RegulationMainAdapter extends BaseAdapter
{
    private Context context;
    private LayoutInflater inflater;
    private ArrayList<RegulationMainData> dataList = new ArrayList<RegulationMainData>();
    public RegulationMainAdapter(Context context, ArrayList<RegulationMainData> arrayList) {
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
            view = inflater.inflate(R.layout.list_item_regul_menu, parent, false);
        }

        //화면에 표시될 View(Layout이 inflate된) 으로 부터 위젝에 대한 참조 획득
        TextView regulationName = (TextView) view.findViewById(R.id.regul_menu_item_text);
        regulationName.setText( dataList.get(position).getRegul() );

        return view;
    }
}
