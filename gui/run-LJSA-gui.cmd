@echo off

if "%JAVA_HOME%"=="" goto NOJAVAHOME

set _CP=target/LJSA-gui-2.0.0.jar;lib/swing3rd.jar;lib/swingup-1.0.0.jar

"%JAVA_HOME%"\bin\java -cp %_CP% org.dew.swingup.main.Main

goto DONE

:NOJAVAHOME
echo JAVA_HOME not setted.
goto DONE

:DONE