echo on
set CATALINA_HOME=C:\app\apache-tomcat-8.0.18
rem set CATALINA_HOME=C:\app\apache-tomcat-7.0.42

set TOMCAT_HOME=%CATALINA_HOME%

call %CATALINA_HOME%\bin\shutdown.bat

cd C:\git\cmti2\cmti\analytics
call mvn clean package

rmdir /s /q  %CATALINA_HOME%\webapps\map
rmdir /s /q  %CATALINA_HOME%\work\Catalina\localhost\map

copy /Y C:\git\cmti2\cmti\analytics\tracking-web\target\map.war %CATALINA_HOME%\webapps

call %CATALINA_HOME%\bin\startup.bat

cd C:\git\cmti2\cmti\analytics\tracking-web\src\main\script

rem call sleep 30

pause

rem del /s /q  %CATALINA_HOME%\webapps\map\WEB-INF\lib\jsp-*
rem del /s /q  %CATALINA_HOME%\webapps\map\WEB-INF\lib\jasper-*

rem javax.servlet and org.apache.jasper are manually removed from %CATALINA_HOME%\webapps\map-manual\WEB-INF\lib\tracking-app-1.0-SNAPSHOT.jar
copy /y %CATALINA_HOME%\webapps\map-manual\WEB-INF\lib\tracking-app-1.0-SNAPSHOT.jar  %CATALINA_HOME%\webapps\map\WEB-INF\lib\
 
 
call %CATALINA_HOME%\bin\shutdown.bat

call %CATALINA_HOME%\bin\startup.bat

cd C:\git\cmti2\cmti\analytics\tracking-web\src\main\script\