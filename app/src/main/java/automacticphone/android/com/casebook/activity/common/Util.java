package automacticphone.android.com.casebook.activity.common;

import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;

import com.zyao89.view.zloading.ZLoadingDialog;
import com.zyao89.view.zloading.Z_TYPE;

import java.io.File;
import java.util.ArrayList;

import automacticphone.android.com.casebook.activity.network.DownloadCallBack;
import automacticphone.android.com.casebook.activity.network.DownloadManager;
import automacticphone.android.com.casebook.activity.network.RequestHttpURLConnection;

public class Util
{
    // Uri에서 파일명 찾기
    public static String getFileName(Activity activity, Uri uri)
    {
        String[] projection = { MediaStore.Images.ImageColumns.DISPLAY_NAME };
        Cursor cursor = activity.getContentResolver().query(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DISPLAY_NAME);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public static boolean ExistFile( String filePath )
    {
        //path 부분엔 파일 경로를 지정해주세요.
        File files = new File(filePath);
        //파일 유무를 확인합니다.
        if(files.exists()==true)
        {
           return true;
        }
        else
        {
            return false;
        }
    }

    public static void StartImageDownload(ArrayList<String> imgList )
    {
        StartImageDownload( imgList, null );
    }

    public static boolean StartImageDownload(ArrayList<String> imgList, DownloadCallBack callBack )
    {
        ArrayList<String> pathImgList = new ArrayList<String>();
        String strDir = DownloadManager.getInstance().getPath();
        for( int i = 0; i < imgList.size(); ++i )
        {
            if(imgList.get(i).length() > 0 )
            {
                String filePath = strDir + "/" + imgList.get(i);
                if(Util.ExistFile( filePath ) == false )
                {
                    String fullUrl = RequestHttpURLConnection.serverIp + "/uploads/logo/" + imgList.get(i);
                    pathImgList.add( fullUrl );
                }
            }
        }

        if( imgList.size() > 0 )
        {
            DownloadManager.getInstance().setDownloadUrl( pathImgList, callBack );
            return true;
        }
        else
            callBack.onDownloadResult( "Success" );

        return false;
    }

    public static String getEmailById( String email )
    {
        return email.split( "@")[0];
    }

    public static Uri getImageContentUri(Context context, File imageFile)
    {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Images.Media._ID },
                MediaStore.Images.Media.DATA + "=? ",
                new String[] { filePath }, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            cursor.close();
            return Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    public static Bitmap getResizedBitmap( String imagePath, int targetW, int targetH )
    {

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        //inJustDecodeBounds = true <-- will not load the bitmap into memory
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, bmOptions);
        return(bitmap);
    }

    public static ZLoadingDialog ShowLoading(Context context, Z_TYPE type, boolean cancelOnTouch )
    {
        ZLoadingDialog dialog = new ZLoadingDialog( context );
        dialog.setLoadingBuilder( Z_TYPE.ROTATE_CIRCLE )
                .setLoadingColor(Color.BLACK)
                .setHintText("Loading...")
                .setHintTextSize(16)
                .setHintTextColor(Color.GRAY)
                .setDurationTime(1)
                .setDialogBackgroundColor(Color.parseColor("#FFFFFF"))
                .setCanceledOnTouchOutside( cancelOnTouch )
                .show();

        return dialog;
    }
}
