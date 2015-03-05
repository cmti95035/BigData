<?xml version="1.0" encoding="utf-8" standalone="yes"?>
<%@page import="java.util.*"%>
<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%-- %@ page session="false" --%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<feed xmlns="http://www.w3.org/2005/Atom"
      xmlns:dc="http://purl.org/dc/elements/1.1/"
      xmlns:geo="http://www.w3.org/2003/01/geo/wgs84_pos#"
      xmlns:georss="http://www.georss.org/georss"
      xmlns:woe="http://where.yahooapis.com/v1/schema.rng"
      xmlns:flickr="urn:flickr:user"
      xmlns:media="http://search.yahoo.com/mrss/">

  <title>Sichuan Stations</title> 
  <subtitle>Sichuan Stations distribution</subtitle>
  <updated>2015-01-29T15:41:19Z</updated> 

	<c:forEach var="station" items="${stations}" varStatus="rowCounter">   
	
<entry><content type="html">
小区级唯一标识: &lt;a href="/map/html/data/sichuan/station/${station.id}"&gt;${station.id}&lt;/a&gt;&lt;br&gt;
所属BSC: ${station.bsc} &lt;br&gt;
CGI: ${station.cgi} &lt;br&gt;
覆盖类型: ${station.type} &lt;br&gt;
LAC: ${station.lac} &lt;br&gt;
CI: ${station.ci} &lt;br&gt;
方向角: ${station.angle}  
 </content>
<geo:lat>${station.lat}</geo:lat>
<geo:long>${station.lng}</geo:long>
</entry>
	 
    </c:forEach>
</feed>