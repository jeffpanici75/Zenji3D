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

Const CONTROL_VIDEO_MODE_640_480 = 1
Const CONTROL_VIDEO_MODE_800_600 = 2
Const CONTROL_VIDEO_MODE_1024_768 = 3
Const CONTROL_COLOR_DEPTH_16_BIT = 4
Const CONTROL_COLOR_DEPTH_32_BIT = 5
Const CONTROL_MUSIC_VOLUME = 6
Const CONTROL_SOUND_VOLUME = 7
Const CONTROL_REALTIME_MIRROR = 8
Const CONTROL_SHOW_ZEN_KOANS = 9
Const CONTROL_ANTI_ALIAS = 10
Const CONTROL_USE_DIRECTINPUT = 11
Const CONTROL_RESET_HIGH_SCORES = 12
Const CONTROL_SAVE_AND_CLOSE = 13
Const CONTROL_RESET_MAPPINGS = 14
Const CONTROL_SETTINGS_TAB = 98
Const CONTROL_CONTROLS_TAB = 99

Const INPUT_MAP_NOP_STATE = 0
Const INPUT_MAP_WAIT_STATE = 1
Const INPUT_MAP_REMAP_STATE = 2

Type SettingControl
    Field id
    Field page
    Field image
    Field point.Point
    Field hotspot.Rectangle
    Field hitPoint.Point
    Field settingName$
    Field settingValue$
    Field isActive
    Field imageFilename$
End Type

Global g_currentPage
Global g_backImage[2]
Global g_controlImages[100]
Global g_inputSettings.Setting[9]
Global g_dialogSprite.Sprite
Global g_leftEdge
Global g_topEdge
Global g_videoResetRequired
Global g_inputMapState
Global g_selectedEntry
Global g_inputType
Global g_blinkTimer.Timer
Global g_showQuestionMarks

.inputSettingNames
Data "MoveRight", "MoveLeft", "MoveUp", "MoveDown", "RotatePipe", "SwitchCamera", "PauseButton", "StartButton", "SelectButton"

.controls
; Page #0 Controls
Data CONTROL_VIDEO_MODE_640_480 ,  0, 42, 26, 73,103,122, 24, "VideoMode"     , "0"   , False, "res\bitmaps\640x480.png"
Data CONTROL_VIDEO_MODE_800_600 ,  0,172, 26,203,103,131, 24, "VideoMode"     , "1"   , False, "res\bitmaps\800x600.png"
Data CONTROL_VIDEO_MODE_1024_768,  0,313, 26,344,103,131, 24, "VideoMode"     , "2"   , False, "res\bitmaps\1024x768.png"
Data CONTROL_COLOR_DEPTH_16_BIT ,  0, 42,102, 73,162,133, 20, "ColorDepth"    , "0"   , False, "res\bitmaps\16bit.png"
Data CONTROL_COLOR_DEPTH_32_BIT ,  0,185,102,216,162,143, 20, "ColorDepth"    , "1"   , False, "res\bitmaps\32bit.png"
Data CONTROL_MUSIC_VOLUME       ,  0,  8,187, 39,228,170,  8, "MusicVolume"   , ""    , True , "res\bitmaps\slider-tick.png"
Data CONTROL_SOUND_VOLUME       ,  0,267,187,298,228,170,  8, "SoundVolume"   , ""    , True , "res\bitmaps\slider-tick.png"
Data CONTROL_REALTIME_MIRROR    ,  0,171,259,202,285, 12, 10, "RealTimeMirror", "True", False, "res\bitmaps\check.png"
Data CONTROL_SHOW_ZEN_KOANS     ,  0,432,259,463,285, 12, 10, "ShowZenKoans"  , "True", False, "res\bitmaps\check.png"
Data CONTROL_ANTI_ALIAS         ,  0,171,297,202,314, 12, 10, "AntiAlias"     , "True", False, "res\bitmaps\check.png"
Data CONTROL_USE_DIRECTINPUT    ,  0,432,297,464,314, 12, 10, "UseDirectInput", "True", False, "res\bitmaps\check.png"
Data CONTROL_RESET_HIGH_SCORES  ,  0,  6,349, 37,355,182, 20, ""              , ""    , False, "res\bitmaps\reseths-button.png"
Data CONTROL_SAVE_AND_CLOSE     ,  0,264,349,295,355,182, 20, ""              , ""    , False, "res\bitmaps\saveclose-button.png"
Data CONTROL_SETTINGS_TAB       ,  0,279,  0,285,  4,108, 35, ""              , ""    , True , "res\bitmaps\settings-tab.png"
Data CONTROL_CONTROLS_TAB       ,  0,279,  0,404,  4,108, 35, ""              , ""    , False, "res\bitmaps\controls-tab.png"

; Page #1 Controls
Data CONTROL_SETTINGS_TAB       ,  1,279,  0,285,  4,108, 35, ""              , ""    , False ,"res\bitmaps\settings-tab.png"
Data CONTROL_CONTROLS_TAB       ,  1,279,  0,404,  4,108, 35, ""              , ""    , True  ,"res\bitmaps\controls-tab.png"
Data CONTROL_RESET_MAPPINGS     ,  1,  5,349, 37,355,182, 20, ""              , ""    , False, "res\bitmaps\resetmap-button.png"
Data CONTROL_SAVE_AND_CLOSE     ,  1,293,354,  0,  0,  0,  0, ""              , ""    , False, "res\bitmaps\saveclose-button.png"
Data -999

Function LoadSettingsDialogResources()
    Local id, page, x, y, hx, hy, width, height, isActive
    Local settingName$, settingValue$, imageFilename$
    Restore controls
    Repeat
        Read id
        If id = -999 Then Exit
        Read page, x, y, hx, hy, width, height, settingName$, settingValue$, isActive, imageFilename$
        control.SettingControl = New SettingControl
        control\id = id
        control\page = page
        control\settingName = settingName
        control\settingValue = settingValue
        control\isActive = isActive
        control\point = New Point
        control\point\x = x
        control\point\y = y
        control\hitPoint = New Point
        If Len( imageFilename ) > 0
            sharedControl.SettingControl = GetControlByImageFile( imageFilename )
            If sharedControl <> Null
                control\image = sharedControl\image
            Else
                control\image = LoadImage( RarExtractFile( imageFilename ) )
                UpdateDiskAccessIcon
            EndIf
        Else
            control\image = 0
        EndIf    
        control\hotspot = New Rectangle
        If hx <> 0 And hy <> 0 And width <> 0 And height <> 0
            control\hotspot\x = g_leftEdge + hx
            control\hotspot\y = g_topEdge + hy
            control\hotspot\width = width
            control\hotspot\height = height
        Else
            control\hotspot\x = g_leftEdge + x
            control\hotspot\y = g_topEdge + y
            control\hotspot\width = ImageWidth( control\image )
            control\hotspot\height = ImageHeight( control\image )
        EndIf
        control\imageFilename = imageFilename
        UpdateDiskAccessIcon
    Forever
End Function

Function FreeSettingsDialogResources()
    For control.SettingControl = Each SettingControl
        Repeat
            sharedControl.SettingControl = GetControlByImageFile( control\imageFilename )
            If sharedControl = Null Then Exit
            If sharedControl\id = control\id Then Exit
            Delete sharedControl\point
            Delete sharedControl\hotspot
            Delete sharedControl
        Forever
        FreeImage control\image
        Delete control\point
        Delete control\hotspot
        Delete control
        UpdateDiskAccessIcon
    Next
    FreeSprite g_dialogSprite
    FreeImage g_backImage[0]
    FreeImage g_backImage[1]
    HidePointerSprite
End Function

Function StartSettingsDialog()
    Local cx = GraphicsWidth() / 2
    Local cy = GraphicsHeight() / 2
    Local width, height, temp, srcBuffer, destBuffer
    MoveMouse cx, cy
    SetPointerPosition cx, cy
    g_leftEdge = (GraphicsWidth() - 512) / 2
    g_topEdge = (GraphicsHeight() - 400 ) / 2
    LoadSettingsDialogResources
    UpdateDiskAccessIcon
    g_dialogSprite = SpriteCreate( g_leftEdge, g_topEdge, 512, 512, 0, 0, 1 )
    SetSpriteHandle g_dialogSprite, -1, -1
    ResizeSprite g_dialogSprite, 512, 400
    UpdateDiskAccessIcon
    temp = LoadImage( RarExtractFile( "res\bitmaps\settings-trim.png" ) )
    CopyRect 0, 0, ImageWidth( temp ), ImageHeight( temp ), 0, 0, ImageBuffer( temp ), TextureBuffer( g_dialogSprite\texture )
    FreeImage temp
    UpdateDiskAccessIcon
    g_backImage[0] = LoadImage( RarExtractFile( "res\bitmaps\settings-inner.png" ) )
    g_backImage[1] = LoadImage( RarExtractFile( "res\bitmaps\controls-inner.png" ) )
    Restore inputSettingNames
    For i = 0 To 8
        Read settingName$
        g_inputSettings[i] = GetSetting( settingName$ )
    Next
    g_blinkTimer = StartTimer( 250 )
    g_videoResetRequired = False
    g_currentPage = 0
    CopyCurrentPage
    UpdateDiskAccessIcon
    UpdateControls
    UpdateDiskAccessIcon
    CameraOverWater
    ShowPointerSprite
    FlushMouse
End Function

Function UpdateSettingsDialog()
    Local mx = MouseX()
    Local my = MouseY()
    If MouseHit( 1 )
        Local picked.SettingControl = GetClickedControl( mx, my )
        If picked <> Null
            Local ctrl.SettingControl
            Local setting.Setting
            picked\isActive = True
            If picked\id = CONTROL_SETTINGS_TAB
                ctrl = GetControlById( CONTROL_SETTINGS_TAB )
                ctrl\isActive = False
                g_currentPage = 0
                CopyCurrentPage
            Else If picked\id = CONTROL_CONTROLS_TAB
                ctrl = GetControlById( CONTROL_CONTROLS_TAB )
                ctrl\isActive = False
                g_currentPage = 1
                CopyCurrentPage
                DisplayInputMap
            Else If picked\id = CONTROL_RESET_HIGH_SCORES
                DeleteFile "high-scores.dat"
                LoadHighScores
                picked\isActive = False
            Else If picked\id = CONTROL_RESET_MAPPINGS
                LoadDefaultInputMappings
                CopyCurrentPage
                DisplayInputMap
            Else If picked\id = CONTROL_SAVE_AND_CLOSE
                EnableDiskAccessIcon
                SaveSettings: UpdateDiskAccessIcon
                SetInputMap: UpdateDiskAccessIcon
                FreeSettingsDialogResources: UpdateDiskAccessIcon
                UpdateSystemSettings
                SetAttractMode
                DisableDiskAccessIcon
                Return
            Else
                setting = GetSetting( picked\settingName )
                If setting\value = "True"
                    setting\value = "False"
                Else If Len( picked\settingValue ) = 0
                    setting\value = Str( Int( Float( picked\hitPoint\x / 1.77 ) ) )
                    SetVolumeLevels
                Else
                    If setting\name = "VideoMode" Or setting\name = "ColorDepth"
                        g_videoResetRequired = True
                    EndIf
                    setting\value = picked\settingValue
                EndIf    
            EndIf
            UpdateControls
            SoundPlay g_clickSound
        Else
            If g_currentPage = 1 And g_inputMapState = INPUT_MAP_NOP_STATE
                offsety = g_topEdge + 133
                For i = 0 To 8
                    If RectsOverlap( g_leftEdge + 178, offsety, 175, 16, mx, my, 2, 2 )
                        g_inputMapState = INPUT_MAP_WAIT_STATE
                        g_selectedEntry = i
                        g_inputType = KEYBOARD
                        FlushKeys
                        SoundPlay g_clickSound
                    ElseIf RectsOverlap( g_leftEdge + 357, offsety, 128, 16, mx, my, 2, 2 )
                        g_inputMapState = INPUT_MAP_WAIT_STATE
                        g_selectedEntry = i
                        g_inputType = JOYPAD
                        FlushJoy
                        SoundPlay g_clickSound
                    EndIf
                    offsety = offsety + 23
                Next
            EndIf
        EndIf
    Else
        If g_currentPage = 1
            Select g_inputMapState
                Case INPUT_MAP_WAIT_STATE
                    If CheckTimer( g_blinkTimer )
                        g_showQuestionMarks = Not g_showQuestionMarks
                        If g_showQuestionMarks
                            RestoreInputMapEntry g_selectedEntry, g_inputType
                            DisplayEntry g_selectedEntry, g_inputType, "???"
                        Else
                            RestoreInputMapEntry g_selectedEntry, g_inputType
                        EndIf
                        If g_inputType = KEYBOARD
                            kbEntry.KeyMapEntry = TestValidKeyHit()
                            If kbEntry <> Null
                                usedIdx = IsMappingUsed( kbEntry\scancode, KEYBOARD, g_selectedEntry )
                                If usedIdx = -1
                                    g_inputSettings[g_selectedEntry]\value = kbEntry\symbol + "," + Word( g_inputSettings[g_selectedEntry]\value, 2 )
                                    RestoreInputMapEntry g_selectedEntry, g_inputType
                                    DisplayEntry g_selectedEntry, g_inputType
                                    g_inputMapState = INPUT_MAP_NOP_STATE
                                Else    
                                EndIf    
                            EndIf
                        Else If g_inputType = JOYPAD
                            jpEntry.JoypadMapEntry = TestValidJoyHit()
                            If jpEntry <> Null
                                usedIdx = IsMappingUsed( jpEntry\code, JOYPAD, g_selectedEntry )
                                If usedIdx = -1
                                    g_inputSettings[g_selectedEntry]\value = Word( g_inputSettings[g_selectedEntry]\value, 1 ) + "," + jpEntry\symbol
                                    RestoreInputMapEntry g_selectedEntry, g_inputType
                                    DisplayEntry g_selectedEntry, g_inputType
                                    g_inputMapState = INPUT_MAP_NOP_STATE
                                Else
                                EndIf
                            EndIf
                        EndIf
                    EndIf
                    If KeyHit( KEY_ESCAPE )
                        RestoreInputMapEntry g_selectedEntry, g_inputType
                        DisplayEntry g_selectedEntry, g_inputType
                        g_inputMapState = INPUT_MAP_NOP_STATE
                    EndIf    
                Case INPUT_MAP_REMAP_STATE
            End Select
        EndIf
    EndIf
End Function

Function CopyCurrentPage()
    Local width = ImageWidth( g_backImage[g_currentPage] )
    Local height = ImageHeight( g_backImage[g_currentPage] )
    Local srcBuffer = ImageBuffer( g_backImage[g_currentPage] )
    Local destBuffer = TextureBuffer( g_dialogSprite\texture )
    CopyRect 0, 0, width, height, 30, 104, srcBuffer, destBuffer
End Function

Function UpdateControls()
    Local x, y, width, height, srcBuffer, destBuffer
    Local xoffset
    destBuffer = TextureBuffer( g_dialogSprite\texture )
    For control.SettingControl = Each SettingControl
        If control\page = g_currentPage
            xoffset = 0
            width = ImageWidth( control\image )
            height = ImageHeight( control\image )
            If Len( control\settingName ) > 0
                setting.Setting = GetSetting( control\settingName )
                If Len( control\settingValue ) = 0
                    xoffset = Int( Float( Int( setting\value ) * 1.77 ) ) - width
                    If control\point\x + xoffset < control\hotspot\x
                        xoffset = xoffset + width
                    Else If xoffset > (control\hotspot\width - (width * 1.77))
                        xoffset = xoffset - width
                    EndIf
                Else
                    If setting\value = control\settingValue 
                        control\isActive = True
                    Else
                        control\isActive = False
                    EndIf    
                EndIf    
            EndIf
            If control\isActive
                srcBuffer = ImageBuffer( control\image )
                If xoffset > 0
                    RestoreControlImage control
                EndIf
                left = control\point\x + xoffset
                top = control\point\y
                If control\id < 80
                    left = 30 + left
                    top = 104 + top
                EndIf
                CopyRect 0, 0, width, height, left, top, srcBuffer, destBuffer
            Else
                If control\id < 80
                    RestoreControlImage control
                EndIf
            End If
        EndIf        
    Next
End Function

Function DisplayInputMap()
    For i = 0 To 8
        DisplayEntry i, KEYBOARD
        DisplayEntry i, JOYPAD
    Next
End Function

Function DisplayEntry( idx, itype, value$ = "" )
    Local x, y
    SetDelimitChar ","
    If itype = KEYBOARD
        If Len( value ) = 0 
            value = Word( g_inputSettings[idx]\value, 1 )
            value = Right( value, Len( value ) - 4 )
        EndIf    
        x = 320 - ( Len( value ) * 12 )
        DrawTextureFont g_finalFight8r, 30 + x, 104 + (62 + (idx * 30)), value, g_dialogSprite\texture
    Else If itype = JOYPAD
        If Len( value ) = 0
            value = Word( g_inputSettings[idx]\value, 2 )
            value = Right( value, Len( value ) - 4 )
        EndIf    
        x = 450 - ( Len( value ) * 12 )
        DrawTextureFont g_finalFight8r, 30 + x, 104 + (62 + (idx * 30)), value, g_dialogSprite\texture
    EndIf
End Function

Function RestoreInputMapEntry( idx, itype )
    Local srcBuffer = ImageBuffer( g_backImage[g_currentPage] )
    Local destBuffer = TextureBuffer( g_dialogSprite\texture )
    offsety = 62 + (idx * 30)
    If itype = KEYBOARD
        offsetx = 147
        width = 175
    ElseIf itype = JOYPAD
        offsetx = 322
        width = 128
    EndIf
    CopyRect offsetx, offsety, width, 20, 30 + offsetx, 104 + offsety, srcBuffer, destBuffer
End Function

Function RestoreControlImage( control.SettingControl )
    Local srcBuffer = ImageBuffer( g_backImage[g_currentPage] )
    Local destBuffer = TextureBuffer( g_dialogSprite\texture )
    Local leftAdjust = 30 + control\point\x
    Local topAdjust = 104 + control\point\y
    Local width = control\hotspot\width + 16
    Local height = control\hotspot\height + 16
    CopyRect control\point\x, control\point\y, width, height, leftAdjust, topAdjust, srcBuffer, destBuffer
End Function

Function GetControlByImageFile.SettingControl( s$ )
    For control.SettingControl = Each SettingControl
        If control\imageFilename = s$
            Return control
        EndIf
    Next
    Return Null
End Function

Function GetControlById.SettingControl( id )
    For control.SettingControl = Each SettingControl
        If control\id = id And control\page = g_currentPage
            Return control
        EndIf
    Next
    Return Null
End Function

Function GetClickedControl.SettingControl( x, y )
    Local leftAdjust = 0, topAdjust = 0
    For control.SettingControl = Each SettingControl
        If control\page = g_currentPage
            w = control\hotspot\width
            h = control\hotspot\height
            If RectsOverlap( control\hotspot\x, control\hotspot\y, w, h, x, y, 2, 2 )
                control\hitPoint\x = x - control\hotspot\x
                control\hitPoint\y = y - control\hotspot\y
                If control\hitPoint\x > 0
                    Return control
                EndIf    
            EndIf
        EndIf        
    Next
    Return Null
End Function

Function IsVideoResetRequired()
    Return g_videoResetRequired
End Function

Function ResetPerformed()
    g_videoResetRequired = False
End Function
