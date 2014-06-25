[Files]
Source: ..\redist\beepak.dll; DestDir: {app}
Source: ..\redist\unrar.dll; DestDir: {app}
Source: ..\redist\Zenji3D.exe; DestDir: {app}
Source: ..\redist\Zenji3D.pak; DestDir: {app}
[Dirs]
Name: {app}\screenshots
[Icons]
Name: {commondesktop}\Zenji 3D; Filename: {app}\Zenji3D.exe; WorkingDir: {app}; IconIndex: 0; MinVersion: 0,5.01.2600sp1
[Setup]
MinVersion=0,5.01.2600sp1
AppCopyright=Copyright © 2003-2007 Epyx Software LLC
AppName=Zenji 3D
AppVerName=1.0.0.0
DefaultDirName={pf}\Zenji 3D
WizardImageFile=C:\Projects\Zenji 3D [trunk]\setup\setup-wizard-left.bmp
UninstallDisplayIcon={app}\Zenji3D.exe
DisableProgramGroupPage=true
DefaultGroupName=Zenji 3D
UsePreviousGroup=false
AppendDefaultGroupName=false
AppPublisher=Epyx Software LLC
AppVersion=1.0.0.0
UninstallDisplayName=Zenji 3D
