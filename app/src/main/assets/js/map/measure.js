var pointArray = [];//存放坐标点
var markerArray = [];//存放坐标点marker
var polylineArray = [];//存放每条线
var polygon = null;//存放画的面
var polygonMarker = null;
var measureFlag = "";//测量距离还是测量面积标识

function measureEnd() {
    measureFlag = "";
  $(".measure-accordinate").hide();
    map.off("click",mapClickPoint);
    map.off("click",mapClick);
    map.off("dragend", mapDragEnd);
}

function measureBack() {
  measureClear();
  $(".measure-accordinate").hide();
  $("#measure_toolbar").remove();
  $("#measure_result").remove();
    //mainView.showToolbar();
    //$$(".navbar").show();
  map.off("click",mapClickPoint);
    map.off("click",mapClick);
    map.off("dragend", mapDragEnd);
    map.on("click",mapClick);
    map.on("dragend", mapDragEnd);
}
function measure_distance() {
    measureClear();
    measureFlag = "distance";
  $(".measure-accordinate").show();
  map.off("click", mapClickPoint);
  map.on("click", mapClickPoint);
    map.off("click",mapClick);
    map.off("dragend", mapDragEnd);
}
function measure_area() {
    measureClear();
    measureFlag = "area";
  $(".measure-accordinate").show();
  map.off("click", mapClickPoint);
    map.on("click", mapClickPoint);
    map.off("click",mapClick);
    map.off("dragend", mapDragEnd);
}
function measureClear() {
    map.off("click",mapClick);
    map.off("dragend", mapDragEnd);
  $("#measure_result").html("");
    measureEnd();
    pointArray = [];
    for (var i = 0; i < polylineArray.length; i++) {
        if (map.hasLayer(polylineArray[i])) {
            map.removeLayer(polylineArray[i]);
        }
    }
    polylineArray = [];
    for (var i = 0; i < markerArray.length; i++) {
        if (map.hasLayer(markerArray[i])) {
            map.removeLayer(markerArray[i]);
        }
    }
    markerArray = [];
    if (polygon != null && map.hasLayer(polygon)) {
        map.removeLayer(polygon);
        polygon = null;
    }
    if (polygonMarker != null && map.hasLayer(polygonMarker)) {
        map.removeLayer(polygonMarker);
        polygonMarker = null;
    }
}
function getAccordinate() {
        var mapContainer = $("#map");
        //alert(map.containerPointToLatLng([mapContainer.width() / 2, mapContainer.height() / 2]));
        var obj = { latlng: map.containerPointToLatLng([mapContainer.width() / 2, mapContainer.height() / 2]) };
        //obj.latlng = map.containerPointToLatLng([mapContainer.width() / 2, mapContainer.height() / 2]);
        mapClickPoint(obj);
}
function mapClickPoint(e) {
    //e.latlng;
    if (measureFlag == "distance") {
        pointArray.push(e.latlng);
       var newAddMarker = new AIMap.Marker(e.latlng, { icon: new AIMap.DivIcon({ html: "<div class='measure_marker'><img src='images/measure_marker" + (pointArray.length == 1 ? "1" : "") + ".png'/><span>" + pointArray.length + "</span></div>" }) });
        if (pointArray.length > 1) {
            var _distance = "";
            if (e.latlng.lat > 180 || e.latlng.lat < -180) {
                _distance = e.latlng.distance80To(pointArray[pointArray.length - 2]);//米
            } else {
                _distance = e.latlng.distanceTo(pointArray[pointArray.length - 2]);//经纬度 byxiaoxf
            }
            var distanceStr = _distance > 1000 ? (_distance / 1000).toFixed(2) + ' 千米' : Math.ceil(_distance) + ' 米';
            //distanceStr = "距离：" + distanceStr;

            newAddMarker = new AIMap.Marker(e.latlng, { icon: new AIMap.DivIcon({ html: "<div class='measure_marker'><img src='images/measure_marker" + (pointArray.length == 1 ? "1" : "") + ".png'/><span>" + pointArray.length + "</span><label>" + distanceStr + "</label></div>" }) });
        }
        newAddMarker.addTo(map);
        markerArray.push(newAddMarker);
        if (pointArray.length > 1) {
            var newAddPolyline = new AIMap.Polyline([e.latlng, pointArray[pointArray.length - 2]], { color: 'red' });
            newAddPolyline.addTo(map);
            polylineArray.push(newAddPolyline);
            //计算并添加周长//计算面积和周长
            var len = pointArray.length;
            var distance_sum = 0;
            for (var k = 0; k < len - 1; k++) {
                var _distance = 0;
                if (pointArray[k].lat > 180 || pointArray[k].lat < -180) {
                    _distance = pointArray[k].distance80To(pointArray[k + 1]);//米
                } else {
                    _distance = pointArray[k].distanceTo(pointArray[k + 1]);//经纬度 byxiaoxf
                }
                distance_sum += _distance;
            }
            var distanceStr = distance_sum > 1000 ? (distance_sum / 1000).toFixed(2) + ' 千米' : Math.ceil(distance_sum) + ' 米';
            
            //以下是测量距离总长度放到指定的固定位置
            var polygonStr = "总长度：" + distanceStr;
            //console.log($("#measure_result").length  + "============================");
            if($("#measure_result").length == 0){
                $("body").append('<div id="measure_result"></div>');
                $("#measure_result").show().html(polygonStr);
            }else{
                $("#measure_result").show().html(polygonStr);}
        }
    } else if (measureFlag == "area") {
        pointArray.push(e.latlng);
        var newAddMarker = new AIMap.Marker(e.latlng, { icon: new AIMap.DivIcon({ html: "<div class='measure_marker'><img src='images/measure_marker" + (pointArray.length == 1 ? "1" : "") + ".png'/><span>" + pointArray.length + "</span></div>" }) });
        if (pointArray.length > 1) {
            var _distance = "";
            if (e.latlng.lat > 180 || e.latlng.lat < -180) {
                _distance = e.latlng.distance80To(pointArray[pointArray.length - 2]);//米
            } else {
                _distance = e.latlng.distanceTo(pointArray[pointArray.length - 2]);//经纬度 byxiaoxf
            }
            var distanceStr = _distance > 1000 ? (_distance / 1000).toFixed(2) + ' 千米' : Math.ceil(_distance) + ' 米';
            ////distanceStr = "距离：" + distanceStr;
            newAddMarker = new AIMap.Marker(e.latlng, { icon: new AIMap.DivIcon({ html: "<div class='measure_marker'><img src='images/measure_marker" + (pointArray.length == 1 ? "1" : "") + ".png'/><span>" + pointArray.length + "</span><label>" + distanceStr + "</label></div>" }) });
        }
        if (pointArray.length == 2) {
            var newAddPolyline = new AIMap.Polyline([e.latlng, pointArray[pointArray.length - 2]], { color: 'red' });
            newAddPolyline.addTo(map);
            polylineArray.push(newAddPolyline);
        }
        if (pointArray.length > 2) {
            //先把线清空
            for (var i = 0; i < polylineArray.length; i++) {
                if (map.hasLayer(polylineArray[i])) {
                    map.removeLayer(polylineArray[i]);
                }
            }
            polylineArray = [];

            //画面
            if (polygon != null && map.hasLayer(polygon)) {
                map.removeLayer(polygon);
                polygon = null;
            }
            polygon = new AIMap.Polygon(pointArray, { color: 'red', opacity: 0.5 });
            polygon.addTo(map);
            //计算面积和周长
            if (polygonMarker != null && map.hasLayer(polygonMarker)) {
                map.removeLayer(polygonMarker);
                polygonMarker = null;
            }
            //计算面积和周长
            var sum = 0.0;
            var len = pointArray.length;
            if (e.latlng.lat > 180 || e.latlng.lat < -180) {
                sum += pointArray[0].lat * (pointArray[len - 1].lng - pointArray[1].lng);
            }
            else {
                sum += pointArray[0].lat * (pointArray[len - 1].lng - pointArray[1].lng) * 111194.872221777 * 111194.872221777;
            }
            for (var k = 1; k < len; k++) {
                if (e.latlng.lat > 180 || e.latlng.lat < -180) {
                    sum += pointArray[k].lat * (pointArray[k - 1].lng - pointArray[(k + 1) % len].lng);
                }

                else {
                    sum += pointArray[k].lat * (pointArray[k - 1].lng - pointArray[(k + 1) % len].lng) * 111194.872221777 * 111194.872221777;
                }
            }
            area = -sum / 2.0;
            area = Math.abs(area);
            var areaStr = (area > 1000000 ? (area / 1000000).toFixed(2) + ' 平方千米' : Math.ceil(area) + ' 平方米') + "<br /><span style='margin-left:39px;font-size:30px;'>" + (Number(area) * 0.0015).toFixed(2) + "</span> 亩<br /><span style='margin-left:39px;font-size:30px;'>" + (Number(area) * 0.0001).toFixed(2) + "</span> 公顷";
            var distance_sum = 0;
            for (var k = 0; k < len - 1; k++) {
                var _distance = 0;
                if (pointArray[k].lat > 180 || pointArray[k].lat < -180) {
                    _distance = pointArray[k].distance80To(pointArray[k + 1]);//米
                } else {
                    _distance = pointArray[k].distanceTo(pointArray[k + 1]);//经纬度 byxiaoxf
                }
                distance_sum += _distance;
            }
            var lastDis = 0;
            if (pointArray[0].lat > 180 || pointArray[0].lat < -180) {
                lastDis = pointArray[0].distance80To(pointArray[len - 1]);//米
            } else {
                lastDis = pointArray[0].distanceTo(pointArray[len - 1]);//经纬度 byxiaoxf
            }
            distance_sum += lastDis;
            var distanceStr = distance_sum > 1000 ? (distance_sum / 1000).toFixed(2) + ' 千米' : Math.ceil(distance_sum) + ' 米';
            ////以下是面积放到面积图形中心点
            //var polygonStr = "周长：" + distanceStr + "<br />面积：" + areaStr;
            //polygonMarker = new AIMap.Marker(polygon.getBounds().getCenter(), { icon: new AIMap.DivIcon({ html: "<div class='polygon_marker'><label>" + polygonStr + "</label></div>" }) });
            //polygonMarker.addTo(map);
            //以下是面积放到指定的固定位置
            var polygonStr = "周长：" + distanceStr + "<br />面积：" + areaStr;
            console.log(polygonStr +　"------------------");
            if($("#measure_result").length == 0){
                $("body").append('<div id="measure_result"></div>');
            $("#measure_result").html(polygonStr);
            }else{
            $("#measure_result").html(polygonStr);
            }

        }
        newAddMarker.addTo(map);
        markerArray.push(newAddMarker);

        if (pointArray.length > 2) {
            //重绘第一个点，注明最后一段距离长度
            if (markerArray[0] != null && map.hasLayer(markerArray[0])) {
                map.removeLayer(markerArray[0]);
                var distanceStr = lastDis > 1000 ? (lastDis / 1000).toFixed(2) + ' 千米' : Math.ceil(lastDis) + ' 米';
                newAddMarker = new AIMap.Marker(pointArray[0], { icon: new AIMap.DivIcon({ html: "<div class='measure_marker'><img src='images/measure_marker1.png'/><span>1</span><label style='color:#2ABD2A;'>" + distanceStr + "</label></div>" }) })
                markerArray[0] = newAddMarker;
                map.addLayer(newAddMarker);
            }
        }
    }
    //alert(e.latlng);
}