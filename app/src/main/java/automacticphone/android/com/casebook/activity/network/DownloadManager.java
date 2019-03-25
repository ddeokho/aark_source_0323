package automacticphone.android.com.casebook.activity.network;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class DownloadManager extends Thread
{
    public static final int HANDLE_START_DOWNLOAD = 1;
    public static final int HANDLE_END_DOWNLOAD = 2;
    public static final int HANDLE_FAIL_DOWNLOAD = 3;


    private static DownloadManager m_oInstance = null;
    private static Object m_oSync = new Object();
    private String m_oStrPath = "";
    private LinkedBlockingQueue<String> m_qUrl;
    private boolean m_bIsThreadStoped = false;
    private Thread m_pThread = null;
    private Context m_oContext = null;
    private boolean m_isDownload = false;
    private DownloadCallBack mDownloadCallBack = null;

    public static DownloadManager getInstance() {
        synchronized (m_oSync) {

            return m_oInstance;
        }
    }

    public DownloadManager(Context _oContext)
    {
        m_oContext = _oContext;
        m_oInstance = this;

        m_qUrl = new LinkedBlockingQueue<String>(5);
        m_bIsThreadStoped = false;
        m_pThread = new Thread(this);
        m_pThread.start();
    }

    public void free()
    {
        m_bIsThreadStoped = true;
        for (int i=0; i<10; ++i)
        {
            try {
                m_pThread.join(1000);
            } catch (InterruptedException e) {

                break;
            }
            if (!m_pThread.isAlive())
            {
                break;
            }
        }
        m_pThread = null;

        if (m_qUrl != null)
        {
            m_qUrl.clear();
            m_qUrl = null;
        }
        m_oSync = null;
        m_oInstance = null;
    }

    // 경로 지정.
    public void setSavePath(String _strPath)
    {
        m_oStrPath = _strPath;
    }

    // URL 주소에서 파일명 가져오기
    protected String getFileName(String _strUrl)
    {
        String[] strData = _strUrl.split("/");

        if (strData.length == 0)
            return "";

        return strData[strData.length - 1];
    }

    // 다운로드 하려는 Url 지정 (여러개 한번에 지정)
    public void setDownloadUrl(String[] _strUrl)
    {
        try
        {
            for (int i=0; i<_strUrl.length; ++i)
            {
                m_qUrl.offer(_strUrl[i], 100, TimeUnit.MILLISECONDS);
            }
            mmHandler.sendEmptyMessage(DownloadManager.HANDLE_START_DOWNLOAD);
        }
        catch (InterruptedException e)
        {
            Log.e("DownloadManager", "setDownloadUrl : Queue Offer Fail!!!");
        }
    }

    // 다운로드 하려는 Url 지정 (여러개 한번에 지정)
    public void setDownloadUrl(ArrayList<String> _strUrlArray, DownloadCallBack callBack )
    {
        try
        {
            mDownloadCallBack = callBack;
            for (int i=0; i<_strUrlArray.size(); ++i)
            {
                m_qUrl.offer(_strUrlArray.get(i), 100, TimeUnit.MILLISECONDS);
            }
            mmHandler.sendEmptyMessage(DownloadManager.HANDLE_START_DOWNLOAD);
        }
        catch (InterruptedException e)
        {
            Log.e("DownloadManager", "setDownloadUrl : Queue Offer Fail!!!");
        }
    }

    // 다운로드 하려는 Url 지정.
    public void setDownloadUrl(String _strUrl)
    {
        try
        {
            m_qUrl.offer(_strUrl, 100, TimeUnit.MILLISECONDS);
            mmHandler.sendEmptyMessage(DownloadManager.HANDLE_START_DOWNLOAD);
        }
        catch (InterruptedException e)
        {
            Log.e("DownloadManager", "setDownloadUrl : Queue Offer Fail!!!");
        }
    }

    public String getUrlDecode(String _strFileName)
    {
        String strRet = null;
        try {
            strRet = URLDecoder.decode(_strFileName, "UTF-8");
        } catch (UnsupportedEncodingException e) {

            strRet = "";
        }

        return strRet;
    }

    // 다운로드 하려는 경로 확인 후 없으면 생성..
    private void checkDir()
    {
        File oFile = new File(getUrlDecode(m_oStrPath));
        if (!oFile.exists())
            oFile.mkdirs();
    }

    public String getPath()
    {
        File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        String strDir = file.getAbsolutePath();

        return strDir;
    }

    @Override
    public void run() {
        URL oUrl;
        int nRead;
        String strUrl;
        HttpURLConnection oConn;
        int nLen;
        byte[] byTmpByte;
        InputStream oIs;
        File oFile;
        FileOutputStream oFos;

        while(!m_bIsThreadStoped)
        {
            try
            {
                strUrl = m_qUrl.poll(500, TimeUnit.MILLISECONDS);
            }
            catch (InterruptedException e)
            {
                strUrl = null;
                e.printStackTrace();
            }

            if (strUrl != null && strUrl.length() > 0)
            {
                // 다운로드 생성..
                try {
                    checkDir();
                    oUrl = new URL(strUrl);
                    oConn = (HttpURLConnection) oUrl.openConnection();
                    nLen = oConn.getContentLength();
                    if (nLen == -1) // 사이즈를 얻어오지 못한다면 버퍼 크기를 임의로 지정.
                        nLen = 1000000;

                    byTmpByte = new byte[nLen];
                    oIs = oConn.getInputStream();
                    oFile = new File(getUrlDecode(m_oStrPath+"/"+getFileName(strUrl)));
                    oFos = new FileOutputStream(oFile);

                    for (;;)
                    {
                        nRead = oIs.read(byTmpByte);

                        if (nRead <= 0)
                            break;

                        oFos.write(byTmpByte, 0, nRead);
                    }

                    oIs.close();
                    oFos.close();
                    oConn.disconnect();

                } catch (MalformedURLException e1) {
                    Log.e("DownloadManager", e1.getMessage());
                    mmHandler.sendEmptyMessage(DownloadManager.HANDLE_FAIL_DOWNLOAD);
                } catch (IOException e2) {
                    Log.e("DownloadManager", e2.getMessage());
                    mmHandler.sendEmptyMessage(DownloadManager.HANDLE_FAIL_DOWNLOAD);
                }
            }
            else if (m_isDownload)
            {
                mmHandler.sendEmptyMessage(DownloadManager.HANDLE_END_DOWNLOAD);
            }

            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Log.e("DownloadManager", e.getMessage());
            }
        }
    }

    public Handler mmHandler = new Handler() {
        public void handleMessage(Message msg) {

            switch (msg.what)
            {
                case HANDLE_START_DOWNLOAD:
                    m_isDownload = true;
                    //Toast.makeText (m_oContext, "다운로드를 시작합니다.", Toast.LENGTH_SHORT).show();

                    break;

                case HANDLE_END_DOWNLOAD:
                    m_isDownload = false;
                    if( mDownloadCallBack != null )
                        mDownloadCallBack.onDownloadResult( "Success" );

                    //Toast.makeText (m_oContext, "다운로드를 완료하였습니다.", Toast.LENGTH_SHORT).show();

                    break;

                case HANDLE_FAIL_DOWNLOAD:
                    m_isDownload = false;
                    if( mDownloadCallBack != null )
                        mDownloadCallBack.onDownloadResult( "Fail" );

                    break;
            }
        }
    };
}
