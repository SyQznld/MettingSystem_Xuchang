var lat = 0; var lng = 0;
var ll = [];
var llname = "";
var dsq = "";
var jsonData="";
var xx = 2;
var layerList = [];

function GetQueryString(name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
    var r = window.location.search.substr(1).match(reg);
    if (r != null) return unescape(r[2]); return "";
}


//普通层级少 坐标系  80  2000  84
var mapConfig = {
    origin: null,
    maxExtent: null,
    crs: null,
    center: null,
    init: function () {
        mapConfig.origin = new AIMap.Point(32876800, 10002100);//中心点
        mapConfig.maxExtent = new AIMap.Bounds(new AIMap.Point(38471499.988358311, 3755999.9661332648), new AIMap.Point(38497585.972821943, 3785000.0772584872));
        mapConfig.crs = new AIMap.Proj.CRS(
            'EPSG:4490', '+proj=longlat +ellps=GRS80 +no_defs',
            {
                origin: [mapConfig.origin.x, mapConfig.origin.y],
//                resolutions: [76.351494369655413,38.175747184827706,19.087873592413853,9.5440690881381762,4.7720345440690881,2.3860172720345441,1.193008636017272,0.59637202607738549,0.29818601303869274],
                resolutions: [76.351494369655413,38.175747184827706,19.087873592413853,9.5440690881381762],
                bounds: mapConfig.maxExtent
            });
        mapConfig.center = mapConfig.maxExtent.getCenter(false);
        mapConfig.center = mapConfig.crs.unproject(mapConfig.center);
        mapConfig.center = [mapConfig.center.lat, mapConfig.center.lng];
        map = new AIMap.Map('map', { crs: mapConfig.crs, maxZoom: mapConfig.crs.options.resolutions.length - 1, center: mapConfig.center, zoom: 0, attributionControl: false, zoomControl: false });
//        AIMap.control.scale({
//                imperial: false
//                }).addTo(map);

    }
};



$(function () {
    layerDirName = GetQueryString("SDPath");
    mapConfig.init();
    layer("xc_yx", "true");
    map.off("click", mapClick);
    map.on("click", mapClick);
    map.off("dragend", mapDragEnd);
    map.on("dragend", mapDragEnd);
});








function layer(name, openFlag) {
    if (openFlag == "true") {
        //影像和矢量
        var yxurl = layerDirName + "/mapdata/" + name + "/Layers/_alllayers/";   //许昌
//        var yxurl = layerDirName + "/mapdatas/" + name + "/图层/_alllayers/";     //演示
        try{
            var yx = new AIMap.TileLayer.ArcGIS(yxurl, {
                minZoom: 0,
                maxZoom: map.getMaxZoom(),
                format: 'png',
                transparent: true,
                opacity: 1.0,
                tileSize: 256,
                tileOrigin: mapConfig.origin,
                resolutions: mapConfig.crs.options.resolutions,
                maxExtent: mapConfig.crs.options.bounds
            });
            var _layer = { layer: yx, name: name ,type:'sl'};
            layerList.push(_layer);
            map.addLayer(yx);
        }
        catch(eee){
            console.log("-----------eeeee---" + eee);
        }
    }
    else {
        unloadLayer(name);
    }
}


var dingweiPolygon = null;

function zhiding(name) {
     for (var i = 0 ; i < layerList.length ; i++) {
            if (layerList[i].name == name) {
                var lname = name;
                if(layerList[i].type == "shp"){
                   layer(lname, "false");
                   loadLayerShp(lname, true);
                }
                else{
                   layer(lname, "false");
                   layer(lname, "true");
                }
            }
        }
}



function mapClick(e, lat, lng) {
    lname = "";
    ll = [];
    window.clearInterval(dsq);
    xx = 2;
    if (layerList.length > 0 && layerList[layerList.length - 1].name != "") {
        var curPoint = null;
        if (e == null) {
            curPoint = [lng, lat];
        }
        else {
            lng = e.latlng.lng; lat = e.latlng.lat;
            curPoint = [lng, lat];
        }
        for (var f = 0; f < layerList.length; f++) {
            if (layerList[f].name != "xc_yx"
                &&layerList[f].name != "xc_lmlw"
                &&layerList[f].name != "xc_zgt"){

//               var res = "";
//               if (layerList[f].name == "yuCheng_ztgh") {
//                   res = "yuCheng_ztgh";
//               }


        try{
            $.ajax({
                url: 'content/'+res+'.txt',
                dataType: "json",
                async: false,
                success: function (data) {
                    res = data;
                    for (var i = 0; i < res.features.length; i++) {
                           var geoJson = AIMap.geoJson(res.features[i].geometry);
                           var _lengthRes = AIMapPip.pointInLayer(curPoint, geoJson);
                           if (_lengthRes.length) {
                               removeLocation();

                           var zuobiaoArray = res.features[i].geometry.coordinates;

                           var lll = [];
                           for (var ii = 0; ii < zuobiaoArray.length; ii++) {
                               var _ll1 = [];
                               var _t1 = zuobiaoArray[ii];
                               for (var ii2 = 0; ii2 < _t1.length; ii2++) {
                                   var _ll12 = [];
                                   var _t12 = _t1[ii2];
                                   if(_t12.length > 0 && _t12[0] instanceof(Array)){
                                       for (var ij2 = 0; ij2 < _t12.length; ij2++) {
                                           var _t22 = _t12[ij2];
                                           var p2 = new AIMap.LatLng(_t22[1], _t22[0], true);
                                           _ll12.push(p2);
                                       }
                                       _ll1.push(_ll12);
                                   }else{
                                       var p2 = new AIMap.LatLng(_t12[1], _t12[0], true);
                                       _ll1.push(p2);
                                   }
                               }
                               lll.push(_ll1);
                           }
                           if (zuobiaoArray.length == 1) {
                               lll = lll[0];
                           }

                           dingweiPolygon = new AIMap.Polygon(lll, { fillOpacity: 0.3, fillColor: '#00FFFF', color: '#00FFFF', clickable: false, weight: 10 });
                           map.addLayer(dingweiPolygon);
                           var shuxing = res.features[i].properties;
                           var _returnstr = "{";
                           for (var key in shuxing) {
                               _returnstr += "\"" + key + "\":\"" + shuxing[key] + "\",";
                           }
                           _returnstr = _returnstr.substr(0, _returnstr.length - 1);
                           _returnstr += "}";


                          switch (layerList[f].name) {
                                case "yuCheng_ztgh":var name = "总体规划";break;
                              }
                            lname = lname + "=" + name;
                            if (lname != ""){
                                android.skip(lname, _returnstr);
                            }
                            break;
                        }
                        else{
                            removeLocation();
                        }
                    }
                }
            });
          }
          catch(eeeee){
              console.log("--------eeeeeeeeeeee-------------" +eeeee);
                        }
                 }
         }
     }
 }

function DatasIsLoading(res,curPoint){
        for (var i = 0; i < res.features.length; i++) {
        if(p_contains_p(curPoint, res.features[i].geometry.coordinates[0])){
           return true;
           }
        }
        return false;
}


var name1 = "";
function mapDragEnd(e) {
    var _a1 = e.target.dragging._draggable._startPos;
    var _b1 = e.target.dragging._draggable._newPos;
    var _a = Math.abs(_a1.x - _b1.x);
    var _b = Math.abs(_a1.y - _b1.y);
    if (_a < 30 && _b < 30) {
        var _sreenPoint = e.target.dragging._draggable._startPoint;
        var test = map.containerPointToLayerPoint(_sreenPoint);
        var layerPoint = map.layerPointToLatLng(test);
        lat = layerPoint.lat; lng = layerPoint.lng;
        mapClick(null, layerPoint.lat, layerPoint.lng);
    }
}

function removeLocation() {
    if (dingweiPolygon && map.hasLayer(dingweiPolygon)) {
        map.removeLayer(dingweiPolygon);
        dingweiPolygon = null;
    }
}
function p_contains_p(point, polygon) {
    var x = point[0], y = point[1];
    var inside = false;
    for (var i = 0, j = polygon.length - 1; i < polygon.length; j = i++) {
        var xi = polygon[i][0], yi = polygon[i][1];
        var xj = polygon[j][0], yj = polygon[j][1];
        var intersect = ((yi > y) != (yj > y))
            && (x < (xj - xi) * (y - yi) / (yj - yi) + xi);
        if (intersect) inside = !inside;
    }
    return inside;
};



//调整图层透明度 包括切片和shp两种
function layerOpacity(value) {
    if (layerList.length > 0) {
        try {
            value = parseFloat(value);
            var IsShp=true;
            if(layerList[layerList.length - 1].layer.options){
                IsShp=false;
            }
            //如果为true说明是切片，否则是shp
            if(!IsShp){
                map.removeLayer(layerList[layerList.length - 1].layer);
                layerList[layerList.length - 1].layer.options.opacity = (100 - value) / 100;
                 map.addLayer(layerList[layerList.length - 1].layer);
            }
            else{
                for(var i=0;i<layerList[layerList.length - 1].layer.length;i++){
                    map.removeLayer(layerList[layerList.length - 1].layer[i]);
                    layerList[layerList.length - 1].layer[i].options.fillOpacity = ((100 - value) / 100) / 2;
                    map.addLayer(layerList[layerList.length - 1].layer[i]);
                }
            }

        } catch (e) {
        }
    }
}

function m_c() {
    measureClear();
}
function m() {
    measure();
}
function m_b() {
    measureBack();
}
function m_d() {
    measure_distance();
}
function m_e() {
    measureEnd();
}
function m_a() {
    measure_area();
}
function m_get() {
    getAccordinate();
}

function m_ed(x, y) {
    mapDragEnd(x, y);
}
var dingweiMarker = null;
function ClearTimeoutMarker() {
    if (dingweiMarker != null && map.hasLayer(dingweiMarker)) {
        map.removeLayer(dingweiMarker);
        dingweiMarker = null;
    }
}

function setMapCenter(){
    map.setView(mapConfig.center, 0);
}


function m_dw1(x, y) {
    ClearTimeoutMarker();
    m_dw2(y, x);
}



function m_dw2(x, y) {
   ClearTimeoutMarker();
      var ss = [x, y];
      map.setView(ss, map.getZoom());
      var _point = new AIMap.LatLng(ss[0], ss[1], true);
      startPoint = _point;//导航起点 即当前实时位置
      dingweiMarker = AIMap.marker(_point, {
                                 icon: AIMap.icon({
                                     iconUrl: 'images/marker_now.png',
                                     iconSize: [50, 50],
                                     iconAnchor: [20, 30]
                                 })
                             });
      map.addLayer(dingweiMarker);
}




//定位到当前位置
function clickDingwei(x, y) {
      ClearTimeoutMarker();

       var ss = [y, x];
       map.setView(ss, map.getMaxZoom());
       var _point = new AIMap.LatLng(ss[0], ss[1], true);
        startPoint = _point;//导航起点 即当前实时位置

       dingweiMarker = AIMap.marker(_point, {
                               icon: AIMap.icon({
                                   iconUrl: 'images/marker_now.png',
                                   iconSize: [50, 50],
                                   iconAnchor: [20, 30]
                               })
                           });
       map.addLayer(dingweiMarker);
}




function fangda() {
    map.zoomIn();
}

function suoxiao() {
    map.zoomOut();
}
var locationObject = {
    xh: 0,
    removeLocationInterval: null,
    locationIntervalPolygon: function () {
        locationObject.xh = parseInt(locationObject.xh) + 1;
        if (locationObject.xh > 8) {
            locationObject.removeLocationInterval = clearInterval(locationObject.removeLocationInterval);
            locationObject.removeLocationInterval = null;
            locationObject.xh = 0;
            //clearLocationPolygon();
        } else {
            if (map && locationPolygon && map.hasLayer(locationPolygon)) {
                map.removeLayer(locationPolygon);
            } else {
                if (locationPolygon && !map.hasLayer(locationPolygon)) {
                    map.addLayer(locationPolygon);
                }
            }
        }
    }
}
var zj=null;
var myIcon;
var layerZJList = [];
function loadLayerShp(name, checkFlag) {
console.log("---------loadLayerShp-------" + name);
    if(checkFlag){
        $.ajax({
//           url: 'content/'+name+'.txt',
           url: name+'.txt',
           dataType: "json",
           async: false,
           success: function (data) {
              if (data != null && data.features.length > 0) {
                          var cur_function_polys = [];
                           var cur_function_zj = [];
                          var feature_length = data.features.length;
                          try{
                              for (var i = 0; i < feature_length; i++) {
                              if(data.features[i].geometry!=null){
                              var zuobiaoArray = data.features[i].geometry.coordinates;
                              var lll = [];
                              for (var ii = 0; ii < zuobiaoArray.length; ii++) {
                                  var _ll1 = [];
                                  var _t1 = zuobiaoArray[ii];
                                  for (var ii2 = 0; ii2 < _t1.length; ii2++) {
                                      var _ll12 = [];
                                      var _t12 = _t1[ii2];
                                      if(_t12.length > 0 && _t12[0] instanceof(Array)){
                                          for (var ij2 = 0; ij2 < _t12.length; ij2++) {
                                              var _t22 = _t12[ij2];
                                              var p2 = new AIMap.LatLng(_t22[1], _t22[0], true);
                                              _ll12.push(p2);
                                          }
                                          _ll1.push(_ll12);
                                      }else{
                                          var p2 = new AIMap.LatLng(_t12[1], _t12[0], true);
                                          _ll1.push(p2);
                                      }
                                  }
                                  lll.push(_ll1);
                              }
                              if (zuobiaoArray.length == 1) {
                                  lll = lll[0];
                              }

                               var suijise = "red";
                               var pg = new AIMap.Polygon(lll, { weight: 0.8, fillOpacity: 0.5, fillColor: suijise, color: "black", clickable: false });
                               cur_function_polys.push(pg);
//                               map.addLayer(pg);
                               if(name.indexOf("许昌上会地块总")){
                               var zdbh =  data.features[i].properties.ZDBH ;
                                     if(zdbh != null && zdbh != "" && zdbh != "null"){
                                      myIcon = new AIMap.divIcon({ className: 'my-div-icon', html: "<div class='Icontitle' style='color:black; font-weight: bold; width:100px'>" + zdbh + "</div>" });
                                     }
                                zj = new AIMap.Marker(pg.getBounds().getCenter(), { icon: myIcon });
                                cur_function_zj.push(zj);
                                map.addLayer(zj);
                               }

                                 }
                              }
                              var _layer = { layer: cur_function_polys, name: name,type:'shp' };
                              layerList.push(_layer);
                              var _zjLayer = { layer: cur_function_zj, name: name,type:'shp' };
                              layerZJList.push(_zjLayer);
                          }
                          catch(eeee){
                          console.log("--------------layerList000--" + eeee);
                  }
              }
           }});
    }else{
        unloadLayer(name);
        unloadZJLayer(name);
    }
}


function unloadLayer(name){
console.log("--------------unload--" + name);
    removeLocation();
    for (var i = 0; i < layerList.length; i++) {
        if (layerList[i].name == name) {
            var _features = layerList[i].layer;
            if(_features instanceof Array){
                for (var j = 0; j < _features.length; j++) {
                    if (_features[j] != null && map.hasLayer(_features[j])) {
                        map.removeLayer(_features[j]);
                    }
                }
            }else{
                if (map.hasLayer(layerList[i].layer)) {
                    map.removeLayer(layerList[i].layer);
                }
            }
            layerList.splice(i, 1);
        }
    }
}


function unloadZJLayer(name){
console.log("--------layerZJList-------" + layerZJList.length);
    removeLocation();
    for (var i = 0; i < layerZJList.length; i++) {
        if (layerZJList[i].name == name) {
            var _features = layerZJList[i].layer;
            if(_features instanceof Array){
                for (var j = 0; j < _features.length; j++) {
                    if (_features[j] != null && map.hasLayer(_features[j])) {
                        map.removeLayer(_features[j]);
                    }
                }
            }else{
                if (map.hasLayer(layerZJList[i].layer)) {
                    map.removeLayer(layerZJList[i].layer);
                }
            }
            layerZJList.splice(i, 1);
        }
    }
}


function zoomToLatlng(lat,lng){
    map.setView(new AIMap.LatLng(lat, lng), 6);
}





