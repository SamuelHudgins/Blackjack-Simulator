@echo off 
for %%f in (*.jar) do (if not defined firstFile set "firstFile=%%f")
java -jar %firstFile%
for /l %%i in (1, 1, 4) do echo:
Pause
