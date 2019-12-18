var startPoint,//导航初始位置
    dingweiMarker;//定位点

//地图投影坐标系，从切片文件中获取
var mapCoordinateSystem = 'PROJCS["WGS_1984_Web_Mercator_Auxiliary_Sphere",GEOGCS["GCS_WGS_1984",DATUM["D_WGS_1984",SPHEROID["WGS_1984",6378137.0,298.257223563]],PRIMEM["Greenwich",0.0],UNIT["Degree",0.0174532925199433]],PROJECTION["Mercator_Auxiliary_Sphere"],PARAMETER["False_Easting",0.0],PARAMETER["False_Northing",0.0],PARAMETER["Central_Meridian",0.0],PARAMETER["Standard_Parallel_1",0.0],PARAMETER["Auxiliary_Sphere_Type",0.0],UNIT["Meter",1.0],AUTHORITY["EPSG",3857]]';

/**传入WGS84坐标，在影像地图上定位*/
function WGS84Tomap(lng, lat) {

    if (map.hasLayer(dingweiMarker)) {
        map.removeLayer(dingweiMarker);
    }
    //没有第二坐标系时，默认是WGS84坐标系。forward:转成定义的坐标系；inverse：由定义的坐标系转成WGS84
    var map_xy = proj4(mapCoordinateSystem,[lng, lat]);
    var point = AIMap.latLng(map_xy[1], map_xy[0]);

    dingweiMarker = AIMap.marker(point, {
        icon: AIMap.icon({
            iconUrl: 'images/marker_now.png',
            iconSize: [40, 40],
            iconAnchor: [20, 30]
        })
    });
    map.addLayer(dingweiMarker);
//    map.setView(startPoint,map.getMaxZoom());
    return point;
}



/**传入影像地图坐标，转换成WGS84*/
function AimapToWGS84(lng, lat) {
    var wgs84_xy = proj4(mapCoordinateSystem).inverse([lng, lat]);
    return wgs84_xy;
}





function nowLocationView(){
  map.setView(startPoint,map.getMaxZoom());
}

