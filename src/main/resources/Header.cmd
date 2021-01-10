@Echo off
Echo ############################################################
Echo ##                                                        ##
Echo ##                   SymLink Installer                    ##
Echo ##                                                        ##
Echo ############################################################
chcp 861>nul

:: BatchGotAdmin BEGIN https://sites.google.com/site/eneerge/home/BatchGotAdmin | https://ss64.com/nt/rem.html | https://ss64.com/nt/cacls.html
:: Check for permissions
mkdir "%windir%\GotAdminTestCreateDir"
if '%errorlevel%' == '0' (
    rmdir "%windir%\GotAdminTestCreateDir" &goto gotAdmin
) else ( goto UACPrompt )


:UACPrompt
    echo Set UAC = CreateObject^("Shell.Application"^) > "%~dp0getadmin.vbs"
    echo UAC.ShellExecute "%~s0", "", "", "runas", 1 >> "%~dp0getadmin.vbs"

    "%~dp0getadmin.vbs"
    exit /B

:gotAdmin
    if exist "%~dp0getadmin.vbs" ( del "%~dp0getadmin.vbs" )
    pushd "%CD%"
    CD /D "%~dp0"
:: BatchGotAdmin END

:: x86? BEGIN
Set xOS=x64& If "%PROCESSOR_ARCHITECTURE%"=="x86" (
If Not Defined PROCESSOR_ARCHITEW6432 Set xOS=x86
)
Echo OS - %xOS%
If "%xOS%"=="x86" (
  goto x86Windows
) Else (
  goto x64Windows
)
:: x86? END

GOTO START_POINT
:: Source Directories Structure BEGIN

:: Source Directories Structure END
:START_POINT

:x64Windows
:: x64 Windows BEGIN

