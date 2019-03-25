package automacticphone.android.com.casebook.activity.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import automacticphone.android.com.casebook.R;
import automacticphone.android.com.casebook.activity.common.Define;
import automacticphone.android.com.casebook.activity.data.UploadPhotoData;
import automacticphone.android.com.casebook.activity.network.RequestHttpURLConnection;



public class UpLoadPhotoAdapter extends RecyclerView.Adapter<UpLoadPhotoAdapter.ViewHolder>
{
    Context context;
    private ArrayList<UploadPhotoData> listViewItemList = new ArrayList<UploadPhotoData>() ;

    public UpLoadPhotoAdapter(Context context, ArrayList<UploadPhotoData> arrayList )
    {
        this.context = context;
        this.listViewItemList = arrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_photo_upload, viewGroup, false );
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i)
    {
        if( listViewItemList.get(i).uploadFileUri != null )
        {
            InputStream in = null;
            try
            {
                in = context.getContentResolver().openInputStream( listViewItemList.get(i).uploadFileUri );
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }

            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            //inJustDecodeBounds = true <-- will not load the bitmap into memory
            bmOptions.inJustDecodeBounds = true;
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = 6;

            Bitmap img = BitmapFactory.decodeStream(in, null, bmOptions);

            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // 이미지 표시
            viewHolder.photoImageView.setImageBitmap( rotate(img, listViewItemList.get(i).exifDegree) );
        }
        else
        {
            String url = RequestHttpURLConnection.serverIp + Define.downloadPath + listViewItemList.get(i).filePath;
            Glide.with(context)
                    .load(url)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(viewHolder.photoImageView);
        }

        viewHolder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d( "UpLoadPhotoAdapter: ", "delete =>" + i );
                remove( i );
            }
        });
    }

    private Bitmap rotate(Bitmap bitmap, float degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    @Override
    public int getItemCount() {
        return listViewItemList.size();
    }

    public void add( UploadPhotoData newData )
    {
        listViewItemList.add(newData);
        notifyDataSetChanged();
    }

    public void remove( int index )
    {
        listViewItemList.remove(index);
        notifyDataSetChanged();
    }

    public void ChangeData( ArrayList<UploadPhotoData> list )
    {
        listViewItemList = list;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder
    {
        public Button deleteBtn;
        public ImageView photoImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            deleteBtn = itemView.findViewById(R.id.photo_upload_delete_btn);
            photoImageView = itemView.findViewById(R.id.photo_upload_image_view);
            setIsRecyclable( false );
        }
    }
}
