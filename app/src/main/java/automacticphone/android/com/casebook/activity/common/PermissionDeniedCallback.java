package automacticphone.android.com.casebook.activity.common;

/**
 * Permission Denied Callback Interface
 */
public interface PermissionDeniedCallback {
    /**
     * 필수 권한 중, 거부된 권한이 최소 1개 이상일 경우 호출되는 콜백 메서드
     * @param deniedLabels 거부된 권한에 대한 레이블
     * 거부된 권한이 2개 이상일 경우 개행문자(\n)로 구분되어 전달된다.
     *
     * 출력예)
     * SD카드 콘텐츠 수정/삭제
     * 장치 상태 및 ID 읽기
     */
    void onDenied(String deniedLabels);
}
