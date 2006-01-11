@echo off
rem set classpath=C:\Program\eclipse\workspace\XMLCast\bin;C:\Program\eclipse\workspace\XMLCast\mif.jar;C:\Program\eclipse\workspace\XMLCast\saxon.jar;C:\Program\eclipse\workspace\XMLCast\swt.jar
set classpath=.\bin;saxon.jar;
rem mif.jar;swt.jar
start javaw -classpath %classpath% -Xmx256m -DproxySet=true -DproxyHost=193.11.19.191 -DproxyPort=8080 XCast

