echo on

call C:\app\apache-tomcat-8.0.18\bin\shutdown.bat

cd C:\git\cmti2\cmti\analytics
call mvn clean install

rmdir /s /q  C:\app\apache-tomcat-8.0.18\webapps\map
rmdir /s /q  C:\app\apache-tomcat-8.0.18\work\Catalina\localhost\map

copy /Y C:\git\cmti2\cmti\analytics\station-web\target\map.war C:\app\apache-tomcat-8.0.18\webapps

call C:\app\apache-tomcat-8.0.18\bin\startup.bat

cd C:\git\cmti2\cmti\analytics\station-web\src\main\script

call sleep 20
del /s /q  C:\app\apache-tomcat-8.0.18\webapps\map\WEB-INF\lib\jsp-*
del /s /q  C:\app\apache-tomcat-8.0.18\webapps\map\WEB-INF\lib\jasper-*
 
 
call C:\app\apache-tomcat-8.0.18\bin\shutdown.bat

call C:\app\apache-tomcat-8.0.18\bin\startup.bat