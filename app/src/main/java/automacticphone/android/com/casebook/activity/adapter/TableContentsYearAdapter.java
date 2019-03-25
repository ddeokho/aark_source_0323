package automacticphone.android.com.casebook.activity.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import java.util.ArrayList;

import automacticphone.android.com.casebook.R;
import automacticphone.android.com.casebook.activity.HomeActivity;
import automacticphone.android.com.casebook.activity.common.DataManager;

public class TableContentsYearAdapter extends RecyclerView.Adapter<TableContentsYearAdapter.ViewHolder>
{
    private Context context;
    private LayoutInflater inflater;

    public ArrayList<Integer> getDataList() {
        return dataList;
    }

    private ArrayList<Integer> dataList = new ArrayList<Integer>();

    public TableContentsYearAdapter(Context context, ArrayList<Integer> arrayList)
    {
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.dataList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public TableContentsYearAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_year, viewGroup, false );
        return new TableContentsYearAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TableContentsYearAdapter.ViewHolder viewHolder, final int i)
    {
        viewHolder.yearBtn.setText( dataList.get(i).toString() );
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void ChangeData( ArrayList<Integer> yearList )
    {
        this.dataList = yearList;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder
    {
        public Button yearBtn;

        public ViewHolder(View itemView) {
            super(itemView);
            yearBtn = itemView.findViewById(R.id.list_item_year_btn);
            yearBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    int itemPosition = getAdapterPosition();
                    int year = dataList.get(itemPosition);
                    DataManager.inst().setSelectYear( year );
                    HomeActivity.inst().UpdateTableContentsFragment();
                }
            });
            setIsRecyclable( false );
        }
    }
}
