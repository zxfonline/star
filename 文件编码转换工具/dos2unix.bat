@echo off
echo start convert....
set work_path=E:\eclipse-workspace\star\src

for /R %work_path% %%c in (*) do (
	dos2unix.exe -k %%c
	rem echo %%c
)
echo ok
pause