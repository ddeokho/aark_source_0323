package automacticphone.android.com.casebook.activity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;

import automacticphone.android.com.casebook.R;
import automacticphone.android.com.casebook.activity.data.CaseData;
import automacticphone.android.com.casebook.activity.data.UserData;
import automacticphone.android.com.casebook.activity.network.HttpTaskCallBack;

public class SearchCaseListAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private ArrayList<CaseData> dataList = new ArrayList<CaseData>();
    public ArrayList<CaseData> getDataList() {
        return dataList;
    }

    public SearchCaseListAdapter(Context context, ArrayList<CaseData> arrayList)
    {
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
        if( arrayList != null )
            this.dataList = arrayList;
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
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null)
        {
            convertView = inflater.inflate(R.layout.list_item_admin_contents, parent, false);
        }

        CaseData data = dataList.get(position);
        TextView textView = convertView.findViewById( R.id.admin_contents_index );
        textView.setText( String.valueOf( position  ));

        textView = convertView.findViewById( R.id.admin_contents_title);
        textView.setText( data.getTitle() );

        return convertView;
    }

    public void ChangeData( ArrayList<CaseData> list )
    {
        dataList = list;
        notifyDataSetChanged();
    }
}
