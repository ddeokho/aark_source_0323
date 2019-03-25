<?php
header("Content-Type: text/html; charset=UTF-8");
?>
<!-- services 라이브러리 불러오기 -->
<script type="text/javascript" src="http://dapi.kakao.com/v2/maps/sdk.js?appkey=0dd17a5a0cfddc2089f727b34c40d17c&libraries=services"></script>
<script src="http://dmaps.daum.net/map_js_init/postcode.v2.js"></script>
<script>   
    new daum.Postcode({
        oncomplete: function(data) 
        {
            var lnt;
            var lot;
            // 주소-좌표 변환 객체를 생성합니다
            var geocoder = new daum.maps.services.Geocoder();
            var fulladdr = data.roadAddress;
                            
            geocoder.addressSearch(fulladdr, function(result, status){
                // 정상적으로 검색이 완료되었으면
                if (status == daum.maps.services.Status.OK) 
                {
                    lnt = result[0].y;
                    lot = result[0].x;

                    if(data.userSelectedType=="R"){
                    // userSelectedType : 검색 결과에서 사용자가 선택한 주소의 타입

                    // return type : R - roadAddress, J : jibunAddress

                    // TestApp 은 안드로이드에서 등록한 이름

                        window.TestApp.setAddress(data.zonecode, data.roadAddress, data.buildingName, lot, lnt );
                    }
                    else{
                        window.TestApp.setAddress(data.zonecode, data.jibunAddress, data.buildingName, lot, lnt );
                    }
                }
                else
                {
                    alert('fail');
                }
            });            
        }

    }).open();

</script>
