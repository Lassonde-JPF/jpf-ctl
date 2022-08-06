@echo off

set JPF_LOGIC_HOME=%~dp0..
set JVM_FLAGS=-Xmx1024m -ea

java %JVM_FLAGS% -jar "%JPF_LOGIC_HOME%\build\libs\jpf-logic.jar" %*