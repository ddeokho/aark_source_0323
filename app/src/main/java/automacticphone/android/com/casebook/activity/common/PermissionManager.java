package automacticphone.android.com.casebook.activity.common;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class PermissionManager {
    /**
     * 클래스 태그 이름
     */
    private final String TAG = PermissionManager.class.getName();

    /**
     * 기본 권한 요청 코드
     */
    public static final int DEFAULT_REQUEST_CODE = 1009;

    /**
     * 상위 액티비티
     */
    private Activity mActivity = null;

    /**
     * 권한 문자열 목록
     */
    private List<String> mPermissions = null;

    /**
     * 권한의 필수 여부
     */
    private List<Boolean> mEssentials = null;

    /**
     * 허용/거부를 물어볼 권한 문자열 목록
     */
    private List<String> mAskingPermissions = null;

    /**
     * 기존에 사용자로부터 한 번 거부된 권한 문자열 목록
     */
    private List<String> mDeniedPermissions = null;

    /**
     * 권한 요청 코드
     */
    private int mRequestCode = DEFAULT_REQUEST_CODE;

    /**
     * 권한 요청 거부에 대한 콜백
     */
    private PermissionDeniedCallback mCallback = null;

    /**
     * 거부된 권한이 있을 경우 앱 실행할 때마다 계속 필요하다고 AlertDialog 띄울 것인가?
     */
    private boolean mAlertDeniedPermissions = true;

    /**
     * 생성자
     */
    public PermissionManager() {
        mActivity = null;
        mPermissions = new ArrayList<>();
        mEssentials = new ArrayList<>();
        mAskingPermissions = new ArrayList<>();
        mDeniedPermissions = new ArrayList<>();
    }

    /**
     * 생성자
     * @param activity 상위 액티비티
     */
    public PermissionManager(Activity activity) {
        mActivity = activity;
        mPermissions = new ArrayList<>();
        mEssentials = new ArrayList<>();
        mAskingPermissions = new ArrayList<>();
        mDeniedPermissions = new ArrayList<>();
    }

    /**
     * 신규 인스턴스를 반환한다.
     * @return 신규 인스턴스
     */
    public static PermissionManager getInstance() {
        return new PermissionManager();
    }

    /**
     * 신규 인스턴스를 반환한다.
     * @param activity 상위 액티비티
     * @return 신규 인스턴스
     */
    public static PermissionManager getInstance(Activity activity) {
        return new PermissionManager(activity);
    }

    /**
     * 상위 액티비티를 반환한다.
     * @return 상위 액티비티
     */
    public Activity getParentActivity() {
        return mActivity;
    }

    /**
     * 상위 액티비티를 설정한다.
     * @param activity 상위 액티비티
     */
    public void setParentActivity(Activity activity) {
        mActivity = activity;
    }

    /**
     * 거부된 권한이 있을 경우 앱 실행할 때마다 계속 필요하다고 AlertDialog 띄울 것인지 설정한다.
     * 기본값은 true이다.
     * @param alert true이면 AlertDialog를 띄우고, false이면 띄우지 않는다.
     */
    public void setAlertDeniedPermissions(boolean alert) {
        mAlertDeniedPermissions = alert;
    }

    /**
     * 거부된 권한이 있을 경우 앱 실행할 때마다 계속 필요하다고 AlertDialog 띄울 것인지 여부를 반환한다.
     * @return true이면 AlertDialog를 띄우고, false이면 띄우지 않는다.
     */
    public boolean isAlertDeniedPermissions() {
        return mAlertDeniedPermissions;
    }

    /**
     * 권한을 추가한다.
     * @param permission 권한 문자열
     * @param essential 필수 권한 여부
     * @return 추가에 성공하면 true, 실패하면 false를 반환한다.
     * 이미 추가된 권한을 다시 추가 시도할 경우 실패하게 된다.
     */
    public boolean addPermission(String permission, boolean essential) {
        for (String p : mPermissions)
        {
            if (p.equals(permission))
                return false;
        }
        mPermissions.add(permission);
        mEssentials.add(essential);
        return true;
    }

    /**
     * 권한 요청 코드를 반환한다.
     * @return 권한 요청 코드
     */
    public int getRequestCode() {
        return mRequestCode;
    }

    /**
     * 권한 요청 코드를 설정한다.
     * @param requestCode 권한 요청 코드
     */
    public void setRequestCode(int requestCode) {
        mRequestCode = requestCode;
    }

    /**
     * 권한 명칭을 반환한다.
     * @param permission 권한 문자열
     * @return 권한 명칭(2차원 String 배열)
     * str[0] - 단일 권한 명칭
     * str[1] - 그룹 권한 명칭
     */
    public String[] getPermissionLabel(String permission) {
        PackageManager pkgMgr = mActivity.getPackageManager();
        PermissionInfo pInfo = null;
        PermissionGroupInfo pgInfo = null;

        String[] result = { null, null };

        try {
            pInfo = pkgMgr.getPermissionInfo(permission, PackageManager.GET_META_DATA);
            result[0] = pInfo.loadLabel(pkgMgr).toString();
        }
        catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if (pInfo != null) {
            try {
                pgInfo = pkgMgr.getPermissionGroupInfo(pInfo.group, PackageManager.GET_META_DATA);
                result[1] = pgInfo.loadLabel(pkgMgr).toString();
            }
            catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    /**
     * 안드로이드 OS로 권한 요청을 시작한다.
     */
    public void startRequest(PermissionDeniedCallback callback) {
        // 추가한 권한만큼 반복하여 권한 획득 상태를 검사하고 필요하면 요청한다.
        for (String p : mPermissions) {
            // 권한이 없을 때
            if (ContextCompat.checkSelfPermission(mActivity, p) != PackageManager.PERMISSION_GRANTED) {
                // 과거에 거부된 권한일 때
                if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity, p)) {
                    mDeniedPermissions.add(p);
                }
                // 처음으로 요청하는 권한일 때
                else {
                    mAskingPermissions.add(p);
                }
            }
        }

        // 콜백을 저장한다.
        mCallback = callback;

        // 가장 먼저, 거부된 권한이 1개 이상일 경우를 처리한다.
        if (mDeniedPermissions.size() > 0) {
            boolean essential;
            String[] labels = { null, null };
            StringBuffer deniedLabel = new StringBuffer();
            for (String p : mDeniedPermissions) {
                essential = mEssentials.get(mPermissions.indexOf(p));
                if (essential) { // 필수 권한일 경우에만 추가
                    labels = getPermissionLabel(p);
                    deniedLabel.append("\n\u2022 " + labels[1] + " (" + labels[0] + ")");

                    // 블릿 문자 참고
                    // • = \u2022, ● = \u25CF, ○ = \u25CB, ▪ = \u25AA, ■ = \u25A0, □ = \u25A1, ► = \u25BA
                }
            }

            // 거부된 권한에 대한 AlertDialog를 띄워야 할 경우
            if (mAlertDeniedPermissions && deniedLabel.toString().length() > 0) {
                // 콜백이 지정된 경우 콜백 메서드 호출
                if (mCallback != null) {
                    mCallback.onDenied(deniedLabel.toString());
                }
            }
        }

        // 처음으로 요청하는 권한이 1개 이상일 경우 실제 요청
        if (mAskingPermissions.size() > 0) {
            String[] askingPerms = new String[mAskingPermissions.size()];
            for (int i = 0; i < mAskingPermissions.size(); i++) {
                askingPerms[i] = mAskingPermissions.get(i);
            }
            ActivityCompat.requestPermissions(mActivity, askingPerms, mRequestCode);
        }
    }

    /**
     * 최초로 권한을 요청하는 경우, 그 결과를 처리하는 콜백용 메서드
     * 본 패키지를 사용하는 상위 액티비티 등에서 Override되는 onRequestPermissionResult(int, String[], int[]) 콜백용
     * 메서드를 위한 것이다.
     * @param requestCode 권한 요청코드
     * @param permissions 권한 문자열 배열
     * @param grantResults 요청에 대한 결과 배열
     */
    public void onRequestPermissionResult(int requestCode, String permissions[], int[] grantResults) {
        // 요청 코드가 일치할 경우에만 처리
        if (requestCode == mRequestCode && permissions.length > 0 && grantResults.length > 0) {
            boolean essential;
            String[] labels = { null, null };
            StringBuffer deniedLabel = new StringBuffer();
            for (int i = 0; i < permissions.length; i++) {
                essential = mEssentials.get(mPermissions.indexOf(permissions[i]));
                if (grantResults[i] == PackageManager.PERMISSION_DENIED && essential) {
                    labels = getPermissionLabel(permissions[i]);
                    deniedLabel.append("\n\u2022 " + labels[1] + " (" + labels[0] + ")");
                }
            }
            if (mAlertDeniedPermissions && deniedLabel.toString().length() > 0) {
                if (mCallback != null) {
                    mCallback.onDenied(deniedLabel.toString());
                }
            }
        }
    }
}

