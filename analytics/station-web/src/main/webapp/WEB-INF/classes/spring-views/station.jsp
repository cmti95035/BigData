<%@page import="java.util.*"%>
<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%-- %@ page session="false" --%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>


<html>
<body>

<a href="/map/map-simple.html">Home</a>
<table>
  <tr>    <td>小区级唯一标识</td>    <td>${station.id}</td>  </tr>
  <tr>    <td>所属BSC</td>    <td>${station.bsc}</td>  </tr>
  <tr>    <td>CGI</td>    <td>${station.cgi}</td>  </tr>
  <tr>    <td>覆盖类型</td>    <td>${station.type}</td>  </tr>
  <tr>    <td>LAC</td>    <td>${station.lac}</td>  </tr>
  <tr>    <td>CI</td>    <td>${station.ci}</td>  </tr>
  <tr>    <td>方向角</td>    <td>${station.angle}</td>  </tr>
  <tr>    <td>经度</td>    <td>${station.lng}</td>  </tr>
  <tr>    <td>纬度</td>    <td>${station.lat}</td>  </tr>
</table>


	<c:forEach var="eventCount" items="${station.eventCounts}" varStatus="rowCounter">   
	${eventCount.cell} 
	${eventCount.baseDate}
	${eventCount.count} <br>	 
    </c:forEach>
</body>
</html>