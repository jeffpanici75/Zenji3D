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

Const KEYBOARD = 0
Const JOYPAD = 1
Const HOLD = 1
Const HIT = 2

Const INPUT_MOVE_RIGHT = 0
Const INPUT_MOVE_LEFT = 1
Const INPUT_MOVE_UP = 2
Const INPUT_MOVE_DOWN = 3
Const INPUT_ROTATE_PIPE = 4
Const INPUT_SWITCH_CAMERA = 5
Const INPUT_PAUSE_BUTTON = 6
Const INPUT_START_BUTTON = 7
Const INPUT_SELECT_BUTTON = 8

Const JOY_DPAD_UP = 1
Const JOY_DPAD_DOWN = 2
Const JOY_DPAD_LEFT = 3
Const JOY_DPAD_RIGHT = 4
Const JOY_BUTTON_1 = 5
Const JOY_BUTTON_2 = 6
Const JOY_BUTTON_3 = 7
Const JOY_BUTTON_4 = 8
Const JOY_BUTTON_5 = 9
Const JOY_BUTTON_6 = 10
Const JOY_BUTTON_7 = 11
Const JOY_BUTTON_8 = 12
Const JOY_BUTTON_9 = 13
Const JOY_BUTTON_10 = 14
Const JOY_BUTTON_11 = 15

Type KeyMapEntry
    Field symbol$
    Field scancode
End Type

Type JoypadMapEntry
    Field symbol$
    Field code
End Type

Dim g_inputMap( 8, 2 )

Global g_joyHit[5]

.joystickMap
Data "JOY_DPAD_UP", 1
Data "JOY_DPAD_DOWN", 2
Data "JOY_DPAD_LEFT", 3
Data "JOY_DPAD_RIGHT", 4
Data "JOY_BUTTON_1", 5
Data "JOY_BUTTON_2", 6
Data "JOY_BUTTON_3", 7
Data "JOY_BUTTON_4", 8
Data "JOY_BUTTON_5", 9
Data "JOY_BUTTON_6", 10
Data "JOY_BUTTON_7", 11
Data "JOY_BUTTON_8", 12
Data "JOY_BUTTON_9", 13
Data "JOY_BUTTON_10", 14
Data "JOY_BUTTON_11", 15
Data "###"

Function InitializeInputSystem()
    Restore scanKeyMap
    SetDelimitChar ","
    Repeat
        Read symbol$
        If symbol = "###" Then Exit
        Read code
        entry.KeyMapEntry = New KeyMapEntry
        entry\symbol = Word( symbol, 1 )
        entry\scancode = code
    Forever
    Restore joystickMap
    Repeat
        Read symbol$
        If symbol = "###" Then Exit
        Read code
        joymap.JoypadMapEntry = New JoypadMapEntry
        joymap\symbol = symbol
        joymap\code = code
    Forever
    SetInputMap
End Function

Function GetKeyMapScanCode( symbol$ )
    For entry.KeyMapEntry = Each KeyMapEntry
        If entry\symbol = symbol
            Return entry\scancode
        EndIf
    Next
    Return 0
End Function

Function GetJoypadMapCode( symbol$ )
    For entry.JoypadMapEntry = Each JoypadMapEntry
        If entry\symbol = symbol
            Return entry\code
        EndIf
    Next
    Return 0
End Function

Function IsMappingUsed( code, itype, ignoreIdx )
    For i = 0 To 9
        If i <> ignoreIdx
            Select itype
                Case KEYBOARD
                    If g_inputMap( i, KEYBOARD ) = code
                        Return i
                    EndIf
                Case JOYPAD
                    If g_inputMap( i, JOYPAD ) = code
                        Return i
                    EndIf
            End Select
        EndIf        
    Next
    Return -1
End Function

Function SetInputMap()
    Local setting.Setting = Null
    SetDelimitChar ","
    setting = GetSetting( "MoveRight" )
    g_inputMap( INPUT_MOVE_RIGHT, KEYBOARD ) = GetKeyMapScanCode( Word( setting\value, 1 ) )
    g_inputMap( INPUT_MOVE_RIGHT, JOYPAD ) = GetJoypadMapCode( Word( setting\value, 2 ) )

    setting = GetSetting( "MoveLeft" )
    g_inputMap( INPUT_MOVE_LEFT, KEYBOARD ) = GetKeyMapScanCode( Word( setting\value, 1 ) )
    g_inputMap( INPUT_MOVE_LEFT, JOYPAD ) = GetJoypadMapCode( Word( setting\value, 2 ) )

    setting = GetSetting( "MoveUp" )
    g_inputMap( INPUT_MOVE_UP, KEYBOARD ) = GetKeyMapScanCode( Word( setting\value, 1 ) )
    g_inputMap( INPUT_MOVE_UP, JOYPAD ) = GetJoypadMapCode( Word( setting\value, 2 ) )

    setting = GetSetting( "MoveDown" )
    g_inputMap( INPUT_MOVE_DOWN, KEYBOARD ) = GetKeyMapScanCode( Word( setting\value, 1 ) )
    g_inputMap( INPUT_MOVE_DOWN, JOYPAD ) = GetJoypadMapCode( Word( setting\value, 2 ) )

    setting = GetSetting( "RotatePipe" )
    g_inputMap( INPUT_ROTATE_PIPE, KEYBOARD ) = GetKeyMapScanCode( Word( setting\value, 1 ) )
    g_inputMap( INPUT_ROTATE_PIPE, JOYPAD ) = GetJoypadMapCode( Word( setting\value, 2 ) )

    setting = GetSetting( "SwitchCamera" )
    g_inputMap( INPUT_SWITCH_CAMERA, KEYBOARD ) = GetKeyMapScanCode( Word( setting\value, 1 ) )
    g_inputMap( INPUT_SWITCH_CAMERA, JOYPAD ) = GetJoypadMapCode( Word( setting\value, 2 ) )

    setting = GetSetting( "PauseButton" )
    g_inputMap( INPUT_PAUSE_BUTTON, KEYBOARD ) = GetKeyMapScanCode( Word( setting\value, 1 ) )
    g_inputMap( INPUT_PAUSE_BUTTON, JOYPAD ) = GetJoypadMapCode( Word( setting\value, 2 ) )

    setting = GetSetting( "StartButton" )
    g_inputMap( INPUT_START_BUTTON, KEYBOARD ) = GetKeyMapScanCode( Word( setting\value, 1 ) )
    g_inputMap( INPUT_START_BUTTON, JOYPAD ) = GetJoypadMapCode( Word( setting\value, 2 ) )

    setting = GetSetting( "SelectButton" )
    g_inputMap( INPUT_SELECT_BUTTON, KEYBOARD ) = GetKeyMapScanCode( Word( setting\value, 1 ) )
    g_inputMap( INPUT_SELECT_BUTTON, JOYPAD ) = GetJoypadMapCode( Word( setting\value, 2 ) )
End Function

Function TestValidKeyHit.KeyMapEntry()
    For entry.KeyMapEntry = Each KeyMapEntry
        If KeyHit( entry\scancode )
            Return entry
        EndIf
    Next
    Return Null
End Function

Function TestValidJoyHit.JoypadMapEntry()
    For entry.JoypadMapEntry = Each JoypadMapEntry
        If entry\code > 4
            If JoyHit( entry\code - 4 )
                Return entry
            EndIf
        Else
            Select entry\code
                Case JOY_DPAD_UP
                    If JoyYDir() = - 1 Then Return entry
                Case JOY_DPAD_DOWN
                    If JoyYDir() = 1 Then Return entry
                Case JOY_DPAD_LEFT
                    If JoyXDir() = -1 Then Return entry
                Case JOY_DPAD_RIGHT
                    If JoyXDir() = 1 Then Return entry
            End Select
        EndIf    
    Next
    Return Null
End Function

Function JoyMappedInput( code, mode = HIT )
    If code > JOY_DPAD_RIGHT
        If mode = HIT
            Return JoyHit( code - JOY_DPAD_RIGHT )
        Else
            Return JoyDown( code - JOY_DPAD_RIGHT )
        EndIf
    Else
        Select code
            Case JOY_DPAD_LEFT: Return (JoyXDir() = -1)
            Case JOY_DPAD_RIGHT:Return (JoyXDir() = 1)
            Case JOY_DPAD_UP:   Return (JoyYDir() = -1)
            Case JOY_DPAD_DOWN: Return (JoyYDir() = 1)
        End Select    
    EndIf
    Return False
End Function

Function JoyHitLeft()
	Local hit = False
	If JoyMappedInput( g_inputMap( INPUT_MOVE_LEFT, JOYPAD ) )
		If Not g_joyHit[MOVE_LEFT]
			g_joyHit[MOVE_LEFT] = True
		EndIf	
	ElseIf Not JoyMappedInput( g_inputMap( INPUT_MOVE_LEFT, JOYPAD ) )
		If g_joyHit[MOVE_LEFT]
			g_joyHit[MOVE_LEFT] = False
			hit = True
		EndIf
	EndIf
	Return hit
End Function

Function JoyHitRight()
	Local hit = False
	If JoyMappedInput( g_inputMap( INPUT_MOVE_RIGHT, JOYPAD ) )
		If Not g_joyHit[MOVE_RIGHT]
			g_joyHit[MOVE_RIGHT] = True
		EndIf	
	ElseIf Not JoyMappedInput( g_inputMap( INPUT_MOVE_RIGHT, JOYPAD ) )
		If g_joyHit[MOVE_RIGHT]
			g_joyHit[MOVE_RIGHT] = False
			hit = True
		EndIf
	EndIf
	Return hit
End Function

Function JoyHitUp()
	Local hit = False
	If JoyMappedInput( g_inputMap( INPUT_MOVE_UP, JOYPAD ) )
		If Not g_joyHit[MOVE_UP]
			g_joyHit[MOVE_UP] = True
		EndIf	
	ElseIf Not JoyMappedInput( g_inputMap( INPUT_MOVE_UP, JOYPAD ) )
		If g_joyHit[MOVE_UP]
			g_joyHit[MOVE_UP] = False
			hit = True
		EndIf
	EndIf
	Return hit
End Function

Function JoyHitDown()
	Local hit = False
	If JoyMappedInput( g_inputMap( INPUT_MOVE_DOWN, JOYPAD ) )
		If Not g_joyHit[MOVE_DOWN]
			g_joyHit[MOVE_DOWN] = True
		EndIf	
	ElseIf Not JoyMappedInput( g_inputMap( INPUT_MOVE_DOWN, JOYPAD ) )
		If g_joyHit[MOVE_DOWN]
			g_joyHit[MOVE_DOWN] = False
			hit = True
		EndIf
	EndIf
	Return hit
End Function
