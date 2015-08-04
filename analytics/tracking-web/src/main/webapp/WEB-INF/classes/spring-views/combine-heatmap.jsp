<!DOCTYPE html>
<%@page import="java.util.*"%>
<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%-- %@ page session="false" --%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <meta name="viewport" content="initial-scale=1.0, user-scalable=no" />
    <script type="text/javascript" src="http://api.map.baidu.com/api?v=2.0&ak=qSpCZZRSvwNt7Wm4ykHFN499"></script>
    <script type="text/javascript" src="http://api.map.baidu.com/library/Heatmap/2.0/src/Heatmap_min.js"></script>
    <title>热力图功能示例</title>
    <style type="text/css">
		ul,li{list-style: none;margin:0;padding:0;float:left;}
		html{height:100%}
		body{height:100%;margin:0px;padding:0px;font-family:"微软雅黑";}
		#container{height:100%;width:100%;}
		#r-result{width:100%;}
    </style>	
</head>
<body>
	<div id="container"></div>
	<div id="r-result">
		<input type="button"  onclick="openHeatmap();" value="显示热力图"/><input type="button"  onclick="closeHeatmap();" value="关闭热力图"/>
	</div>
</body>
</html>
<script type="text/javascript">
    var map = new BMap.Map("container");          // 创建地图实例

    var point = new BMap.Point(104.09, 30.67);//chengdu center
    map.centerAndZoom(point, 13);             // 初始化地图，设置中心点坐标和地图级别
    map.enableScrollWheelZoom(); // 允许滚轮缩放
  
    var points =[
             	<c:forEach var="driveTestData" items="${mr_datas}" varStatus="rowCounter">   
    {"lng":${driveTestData.longitude},"lat":${driveTestData.latitude},"count":${100-driveTestData.averageMrRscp}},
    </c:forEach>
 	<c:forEach var="driveTestData" items="${drive_test_datas}" varStatus="rowCounter">   
{"lng":${driveTestData.longitude},"lat":${driveTestData.latitude},"count":${-driveTestData.rscp}},
</c:forEach>
    ];
   
    if(!isSupportCanvas()){
    	alert('热力图目前只支持有canvas支持的浏览器,您所使用的浏览器不能使用热力图功能~')
    }
	//详细的参数,可以查看heatmap.js的文档 https://github.com/pa7/heatmap.js/blob/master/README.md
	//参数说明如下:
	/* visible 热力图是否显示,默认为true
     * opacity 热力的透明度,1-100
     * radius 势力图的每个点的半径大小   
     * gradient  {JSON} 热力图的渐变区间 . gradient如下所示
     *	{
			.2:'rgb(0, 255, 255)',
			.5:'rgb(0, 110, 255)',
			.8:'rgb(100, 0, 255)'
		}
		其中 key 表示插值的位置, 0~1. 
		    value 为颜色值. 
     */
	//heatmapOverlay = new BMapLib.HeatmapOverlay({"radius":20});
		

		heatmapOverlay = new BMapLib.HeatmapOverlay({"radius":20, "opacity":0, "gradient":{
//			.2:'rgb(0, 255, 255)',	.5:'rgb(0, 110, 255)', .8:'rgb(100, 0, 255)'
	  		//1:'rgb(102, 255, 0)', 	 	.5:'rgb(255, 170, 0)',	  	0:'rgb(255, 0, 0)'
		  		0.15: "rgb(0,0,255)", 0.45: "rgb(0,255,255)", 0.65: "rgb(0,255,0)", 0.85: "yellow", 1.0: "rgb(255,0,0)" //from https://github.com/pa7/heatmap.js/blob/master/README.md

			  //		 0: "rgb(255,0,0)", 0.95: "yellow",  1.0: "rgb(0,255,0)" 
		}
		});
	map.addOverlay(heatmapOverlay);
	heatmapOverlay.setDataSet({data:points,max:100});
	//是否显示热力图
    function openHeatmap(){
        heatmapOverlay.show();
    }
	function closeHeatmap(){
        heatmapOverlay.hide();
    }
//	closeHeatmap();
    function setGradient(){
     	/*格式如下所示:
		{
	  		0:'rgb(102, 255, 0)',
	 	 	.5:'rgb(255, 170, 0)',
		  	1:'rgb(255, 0, 0)'
		}*/
     	var gradient = {};
     	var colors = document.querySelectorAll("input[type='color']");
     	colors = [].slice.call(colors,0);
     	colors.forEach(function(ele){
			gradient[ele.getAttribute("data-key")] = ele.value; 
     	});
        heatmapOverlay.setOptions({"gradient":gradient});
    }
	//判断浏览区是否支持canvas
    function isSupportCanvas(){
        var elem = document.createElement('canvas');
        return !!(elem.getContext && elem.getContext('2d'));
    }
</script>