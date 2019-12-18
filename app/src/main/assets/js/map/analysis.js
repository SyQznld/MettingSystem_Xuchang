var fsdk_layerList = []; //附属地块
var fsdk_layerZJList = []; //附属地块注记

var wzt_polygon = null;
var wzt_dingweiMarker = null;
var wzt_dingweiMarkerPop = null;

//显示地块位置图
function showDKLocation(geometry, dkName, dkLocation) {

    clearWztPolygon();
   clearFsdkPolygon();

    if (geometry != "" && geometry.length > 0) {
        var zuobiaoArray = eval("(" + geometry + ")");
        if (zuobiaoArray != null && zuobiaoArray.length > 0) {
            var lll = [];
            for (var ii = 0; ii < zuobiaoArray.length; ii++) {
                var _ll1 = [];
                var _t1 = zuobiaoArray[ii];
                for (var ii2 = 0; ii2 < _t1.length; ii2++) {
                    var _ll12 = [];
                    var _t12 = _t1[ii2];
                    if (_t12.length > 0 && _t12[0] instanceof (Array)) {
                        for (var ij2 = 0; ij2 < _t12.length; ij2++) {
                            var _t22 = _t12[ij2];
                            var p2 = new AIMap.LatLng(_t22[1], _t22[0], true);
                            _ll12.push(p2);
                        }
                        _ll1.push(_ll12);
                    } else {
                        var p2 = new AIMap.LatLng(_t12[1], _t12[0], true);
                        _ll1.push(p2);
                    }
                }
                lll.push(_ll1);
            }
            if (zuobiaoArray.length == 1) {
                lll = lll[0];
            }

            wzt_polygon = new AIMap.Polygon(lll, {
                fillOpacity: 0.3,
                fillColor: '#0000FF',
                color: '#0000FF',
                clickable: false,
                weight: 7
            });
            map.addLayer(wzt_polygon);
            var polygonCenter = wzt_polygon.getBounds().getCenter();
            //添加弹框以及标记点
            var popHtml = "";
            popHtml += "<div class='ppt_property_div'>";
            popHtml += "<h2>" + dkName + "</h2>";
            popHtml += "<div><span style='font-size:18px;font-weight:bold;'>" + dkLocation + "</span></div>";
            popHtml += "</div>";
            var myIcon = AIMap.divIcon({
                className: 'my-div-icon',
                html: "<div class='zgPoupp'>" + popHtml + "</div>",
                iconAnchor: polygonCenter
            });
            wzt_dingweiMarkerPop = new AIMap.Marker(polygonCenter, {
                icon: myIcon
            });
            map.addLayer(wzt_dingweiMarkerPop);
            var _myIcon = AIMap.icon({
                iconUrl: 'images/icon.png',
                iconSize: [25, 41],
                iconAnchor: [12, 41],
                popupAnchor: [0, -17]
            });
            wzt_dingweiMarker = new AIMap.Marker(polygonCenter, {
                icon: _myIcon
            });
            map.addLayer(wzt_dingweiMarker);

            map.setView(polygonCenter, 0);
//            map.setView([polygonCenter.lat,polygonCenter.lng ], 0);
//            map.setView([polygonCenter.lat,polygonCenter.lng - 15000], 0);
        }
    }
}


var dkContentPop = null;
var fsdk_myIcon = null;
//显示现状影像  附属地块编号 范围线
function showZDKAndFushuDK( geometry, dkContent,fsdk_bh,dkType) {
    console.log("=======geometry==========" + geometry);
    console.log("=======dkContent==========" + dkContent);
    console.log("=======fsdk_bh==========" + fsdk_bh);

    clearWztPolygon();
//    clearFsdkPolygon();

    if (geometry != "" && geometry.length > 0) {
        var zuobiaoArray = eval("(" + geometry + ")");
        if (zuobiaoArray != null && zuobiaoArray.length > 0) {
            var lll = [];
            for (var ii = 0; ii < zuobiaoArray.length; ii++) {
                var _ll1 = [];
                var _t1 = zuobiaoArray[ii];
                for (var ii2 = 0; ii2 < _t1.length; ii2++) {
                    var _ll12 = [];
                    var _t12 = _t1[ii2];
                    if (_t12.length > 0 && _t12[0] instanceof (Array)) {
                        for (var ij2 = 0; ij2 < _t12.length; ij2++) {
                            var _t22 = _t12[ij2];
                            var p2 = new AIMap.LatLng(_t22[1], _t22[0], true);
                            _ll12.push(p2);
                        }
                        _ll1.push(_ll12);
                    } else {
                        var p2 = new AIMap.LatLng(_t12[1], _t12[0], true);
                        _ll1.push(p2);
                    }
                }
                lll.push(_ll1);
            }

            if (zuobiaoArray.length == 1) {
                lll = lll[0];
            }

            var fsdk_pg = null;
            if (fsdk_bh == "" || fsdk_bh == null || fsdk_bh == "null") {
                //范围线 紫色 不显示地块编号注记
                fsdk_pg = new AIMap.Polygon(lll, {
                    fillOpacity: 0.1,
                    fillColor: '#8A2BE2',
                    color: '#8A2BE2',
                    clickable: false,
                    weight: 2
                });
            } 
            else 
            {
                //附属地块，红色范围线，显示编号注记
                fsdk_pg = new AIMap.Polygon(lll, {
                    fillOpacity: 0.3,
                    fillColor: '#FF0000',
                    color: '#FF0000',
                    clickable: (dkType == "zdk" ? true : false),
                    weight: (dkType == "zdk" ? 3 : 1)
                });
                fsdk_myIcon = new AIMap.divIcon({
                    className: 'my-div-icon',
                    html: "<div class='Icontitle' style='color:black; font-weight: bold; width:100px';>" + fsdk_bh + "</div>"
                });

                var fsdk_zj = new AIMap.Marker(fsdk_pg.getBounds().getCenter(), {
                    icon: fsdk_myIcon
                });
                map.addLayer(fsdk_zj);
                fsdk_layerZJList.push(fsdk_zj);
            }
            map.addLayer(fsdk_pg);
            fsdk_layerList.push(fsdk_pg);

            if(dkType == "zdk"){
            //将移动端传过来的地块信息 空格替换为换行符
            var split = dkContent.split(",");
            var reg = /[,，]/g;
            var str ="";
            dkContent = dkContent.replace(reg,"</br>");

            //第一次默认打开主地块信息
            dkContentPop = AIMap.popup()
                           .setLatLng(fsdk_pg.getBounds().getCenter())
                           .setContent(" <div style='padding:7px;'><span style='font-size: 1.0rem ;'> " + dkContent +   " </span> </div>")
                           .addTo(map);
            map.setView(fsdk_pg.getBounds().getCenter(), map.getBoundsZoom(fsdk_pg.getBounds())-1);

            //地块弹框关闭以后，可以再次点击显示
            fsdk_pg.on('click', function(e) {
            dkContentPop = AIMap.popup()
                            .setLatLng(fsdk_pg.getBounds().getCenter())
                            .setContent(" <div style='padding:7px;'><span style='font-size: 1.0rem ;'> " + dkContent +   " </span> </div>")
                            .addTo(map);
            });
            }


        }
    }
}

//清除附属地块 轨迹面
function clearFsdkPolygon() {
    if (fsdk_layerList.length > 0) {
        for (var i = 0; i < fsdk_layerList.length; i++) {
            if (fsdk_layerList[i] && map.hasLayer(fsdk_layerList[i])) {
                map.removeLayer(fsdk_layerList[i]);
            }
        }
        fsdk_layerList = [];
    }
    if (fsdk_layerZJList.length > 0) {
        for (var i = 0; i < fsdk_layerZJList.length; i++) {
            if (fsdk_layerZJList[i] && map.hasLayer(fsdk_layerZJList[i])) {
                map.removeLayer(fsdk_layerZJList[i]);
            }
        }
        fsdk_layerZJList = [];
    }


    if (dkContentPop != null && map.hasLayer(dkContentPop)) {
        map.removeLayer(dkContentPop);
        dkContentPop = null;
    }

}



//清除位置图范围信息
function clearWztPolygon() {
    if (wzt_polygon && map.hasLayer(wzt_polygon)) {
        map.removeLayer(wzt_polygon);
        wzt_polygon = null;
    }
    if (wzt_dingweiMarker && map.hasLayer(wzt_dingweiMarker)) {
        map.removeLayer(wzt_dingweiMarker);
        wzt_dingweiMarker = null;
    }
    if (wzt_dingweiMarkerPop && map.hasLayer(wzt_dingweiMarkerPop)) {
        map.removeLayer(wzt_dingweiMarkerPop);
        wzt_dingweiMarkerPop = null;
    }

    //地块基本情况弹框显示属性
    if (dkContentPop != null && map.hasLayer(dkContentPop)) {
        map.removeLayer(dkContentPop);
        dkContentPop = null;
    }

}





function clearAll() {
    clearFsdkPolygon();
    clearWztPolygon();
}