;---------------------------------------------------------------------
; Zenji3D
;
; The MIT License (MIT)
;
; Copyright (c) 2014 Jeff Panici
;
; Permission is hereby granted, free of charge, to any person obtaining a copy
; of this software and associated documentation files (the "Software"), to deal
; in the Software without restriction, including without limitation the rights
; to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
; copies of the Software, and to permit persons to whom the Software is
; furnished to do so, subject to the following conditions:
;
; The above copyright notice and this permission notice shall be included in all
; copies or substantial portions of the Software.
;
; THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
; IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
; FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
; AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
; LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
; OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
; SOFTWARE.
;---------------------------------------------------------------------

Const SETTINGS_FILE$ = "config.dat"

Type Setting
    Field name$
    Field value$
End Type

Function GetSetting.Setting( name$ )
    For setting.Setting = Each Setting
        If setting\name = name
            Return setting
        EndIf
    Next
    Return Null
End Function

Function ClearSettings()
    Delete Each Setting
End Function

Function LoadSettings()
    Local file
    If FileType( SETTINGS_FILE ) = 0 
        ClearSettings
        LoadDefaultSettings
        Return
    EndIf   
    file = OpenFile( SETTINGS_FILE )
    While Not Eof( file )
        name$ = ReadString( file )
        value$ = ReadString( file )
        setting.Setting = New Setting
        setting\name = name
        setting\value = value
    Wend
    CloseFile file
End Function

Function SaveSettings()
    Local file = WriteFile( SETTINGS_FILE )
    For setting.Setting = Each Setting
        WriteString file, setting\name
        WriteString file, setting\value
    Next    
    CloseFile file
End Function

Function LoadDefaultSettings()
    setting.Setting = New Setting
    setting\name = "VideoMode"
    setting\value = "Auto"

    setting = New Setting
    setting\name = "ColorDepth"
    setting\value = "Auto"

    setting = New Setting
    setting\name = "SoundVolume"
    setting\value = "25"

    setting = New Setting
    setting\name = "MusicVolume"
    setting\value = "25"

    setting = New Setting
    setting\name = "RealTimeMirror"
    setting\value = "False"

    setting = New Setting
    setting\name = "ShowZenKoans"
    setting\value = "True"

    setting = New Setting
    setting\name = "AntiAlias"
    setting\value = "False"

    setting = New Setting
    setting\name = "UseDirectInput"
    setting\value = "False"

    LoadDefaultInputMappings
    SaveSettings
End Function

Function LoadDefaultInputMappings()
    setting.Setting = GetSetting( "MoveRight" )
    If setting = Null
        setting = New Setting
        setting\name = "MoveRight"
    EndIf
    setting\value = "KEY_CURSOR_RIGHT,JOY_DPAD_RIGHT"

    setting = GetSetting( "MoveLeft" )
    If setting = Null
        setting = New Setting
        setting\name = "MoveLeft"
    EndIf    
    setting\value = "KEY_CURSOR_LEFT,JOY_DPAD_LEFT"

    setting = GetSetting( "MoveUp" )
    If setting = Null
        setting = New Setting
        setting\name = "MoveUp"
    EndIf    
    setting\value = "KEY_CURSOR_UP,JOY_DPAD_UP"

    setting = GetSetting( "MoveDown" )
    If setting = Null
        setting = New Setting
        setting\name = "MoveDown"
    EndIf    
    setting\value = "KEY_CURSOR_DOWN,JOY_DPAD_DOWN"

    setting = GetSetting( "RotatePipe" )
    If setting = Null
        setting = New Setting
        setting\name = "RotatePipe"
    EndIf    
    setting\value = "KEY_RIGHT_CTRL,JOY_BUTTON_1"

    setting = GetSetting( "SwitchCamera" )
    If setting = Null
        setting = New Setting
        setting\name = "SwitchCamera"
    EndIf    
    setting\value = "KEY_RIGHT_SHIFT,JOY_BUTTON_2"

    setting = GetSetting( "PauseButton" )
    If setting = Null
        setting = New Setting
        setting\name = "PauseButton"
    EndIf    
    setting\value = "KEY_TAB,JOY_BUTTON_11"

    setting = GetSetting( "StartButton" )
    If setting = Null
        setting = New Setting
        setting\name = "StartButton"
    EndIf    
    setting\value = "KEY_RETURN,JOY_BUTTON_10"

    setting = GetSetting( "SelectButton" )
    If setting = Null
        setting = New Setting
        setting\name = "SelectButton"
    EndIf    
    setting\value = "KEY_SPACE,JOY_BUTTON_12"
End Function
