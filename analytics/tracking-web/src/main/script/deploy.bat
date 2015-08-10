echo on
set CATALINA_HOME=C:\app\apache-tomcat-8.0.18
rem set CATALINA_HOME=C:\app\apache-tomcat-7.0.42

call %CATALINA_HOME%\bin\shutdown.bat

cd C:\git\cmti2\cmti\analytics
call mvn clean package

rmdir /s /q  %CATALINA_HOME%\webapps\map
rmdir /s /q  %CATALINA_HOME%\work\Catalina\localhost\map

copy /Y C:\git\cmti2\cmti\analytics\tracking-web\target\map.war %CATALINA_HOME%\webapps

call %CATALINA_HOME%\bin\startup.bat

cd C:\git\cmti2\cmti\analytics\tracking-web\src\main\script
