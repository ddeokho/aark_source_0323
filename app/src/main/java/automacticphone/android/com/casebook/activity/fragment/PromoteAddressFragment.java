package automacticphone.android.com.casebook.activity.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;
import automacticphone.android.com.casebook.R;

public class PromoteAddressFragment extends Fragment {

    public PromoteAddressFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_promote_address, container, false);
        // java code
        MapView mapView = new MapView( getActivity() );
        ViewGroup mapViewContainer = (ViewGroup) view.findViewById( R.id.promote_address_mapview);
        mapViewContainer.addView( mapView );

        MapPoint MARKER_POINT = MapPoint.mapPointWithGeoCoord(37.3684425354, 127.30614471436);
        // 중심점 변경  - 즐겨 찾기에서 받아 오거나 자신의 현위치를 받아서 설정하자.
        mapView.setMapCenterPoint(MARKER_POINT, true);

        MapPOIItem marker = new MapPOIItem();
        marker.setItemName("Default Marker");
        marker.setTag(0);
        marker.setMapPoint(MARKER_POINT);
        marker.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

        mapView.addPOIItem(marker);

        return view;
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
