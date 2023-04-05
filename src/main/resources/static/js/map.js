const shopName = document.getElementById('shop-name').textContent;

function openMap(longitude, latitude) {
    var coords = new kakao.maps.LatLng(latitude, longitude);

    var mapContainer = document.getElementById('map'),
        mapOption = {
            center: coords, // 지도의 중심좌표
            level: 2 // 지도의 확대 레벨
        };

    var map = new kakao.maps.Map(mapContainer, mapOption);

    var marker = new kakao.maps.Marker({
        map: map,
        position: coords,
        clickable: true // 마커를 클릭했을 때 지도의 클릭 이벤트가 발생하지 않도록 설정
    });

    var infowindow = new kakao.maps.InfoWindow({
        content: '<div style="padding: 10px 5px 5px 5px;"><span class="label-bold-text">' + shopName + '</span></div>',
        removable : true
    });
    infowindow.open(map, marker);

    // 마커에 클릭이벤트를 등록
    kakao.maps.event.addListener(marker, 'click', function() {
        infowindow.open(map, marker);
    });

    map.setCenter();
}
