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

Const PLAYER_ROLL_SPEED# = .90
Const PLAYER_SPIN_SPEED# = 16.35
Const PLAYER_OFFSET_Y# = 2.45
Const PLAYER_TYPE = 2

Global g_currentPlayer = 0
Global g_player.Player
Global g_playerDeathTimer.Timer
Global g_controlDown = False
Global g_playerScale#
Global g_playerUpTimer.Timer
Global g_playerIdleTimer.Timer
Global g_playerUpMode
Global g_upSprite.Sprite[2]
Global g_balls[2]
Global g_calloutSprite.Sprite
Global g_calloutDisplay 
Global g_idleText$[3]
Global g_idleTextIdx
Global g_grantExtraLife
Global g_newLifeSound.Sound

Type Player
	Field entity[2]
	Field pos.Vertex
	Field bx
	Field by
    Field dir
	Field moving
	Field units#
	Field score
	Field lives
	Field halfMove
	Field halfMoveRev
	Field halfMoveReset
End Type

Function LoadPlayerResources( reload = False )
    Local tempTex
    If reload = False
        g_newLifeSound = SoundLoad( RarExtractFile( "res\sounds\new-life.wav" ) )
        g_newLifeSound\userFlag = SOUND_TYPE_FX
        g_idleText[0] = "..."
        g_idleText[1] = "...?"
        g_idletext[2] = "...?!"
    Else
        Delete g_upSprite[0]
        Delete g_upSprite[1]
        Delete g_calloutSprite
    EndIf    
	g_balls[0] = LoadMesh( RarExtractFile( "res\models\ball-inner.x" ) )
	g_balls[1] = LoadMesh( RarExtractFile( "res\models\ball-outter.x" ) )
	ScaleEntity g_balls[0], 4, 4, 4
	ScaleEntity g_balls[1], 3.3, 3.3, 3.3 
	EntityAlpha g_balls[1], .85
	RotateEntity g_balls[0], 33, 0, 0
    tempTex = LoadTextureNoFilters( RarExtractFile( "res\bitmaps\1up.png" ) )
	g_upSprite[0] = SpriteCreate( 0, 0, 128, 128, tempTex, SPRITE_NO_PARENT )
    tempTex = LoadTextureNoFilters( RarExtractFile( "res\bitmaps\2up.png" ) )
	g_upSprite[1] = SpriteCreate( 0, 0, 128, 128, tempTex, SPRITE_NO_PARENT )
	For i = 0 To 1
		RotateEntity g_upSprite[i]\entity, 65, 0, 0
		ResizeSpriteW g_upSprite[i], 4, 4, 1
		HideSprite g_upSprite[i]
		HideEntity g_balls[i]
	Next
    tempTex = LoadTextureNoFilters( RarExtractFile( "res\bitmaps\callout.png" ) )
	g_calloutSprite = SpriteCreate( 0, 0, 128, 128, tempTex, SPRITE_NO_PARENT )
	RotateEntity g_calloutSprite\entity, 65, 0, 0
	ResizeSpriteW g_calloutSprite, 5, 5, 1
	HideSprite g_calloutSprite
End Function

Function FreePlayerResources()
	SoundFree g_newLifeSound
	For i=0 To 1
		If g_player\entity[i] <> 0
			FreeEntity g_player\entity[i]
		EndIf
	Next
	If g_playerUpTimer <> Null Then Delete g_playerUpTimer
	If g_playerDeathTimer <> Null Then Delete g_playerDeathTimer
	If g_player <> Null Then Delete g_player
End Function

Function DisownPlayerSprites()
	For i = 0 To 1
		SetSpriteParent g_upSprite[i], 0
	Next	
	SetSpriteParent g_calloutSprite, 0
End Function

Function SetPlayerEntity()
	g_player\entity[0] = CopyEntity( g_balls[0], GetBoardPivot() )
	g_player\entity[1] = CopyEntity( g_balls[1], GetBoardPivot() )
	EntityType g_player\entity[0], PLAYER_TYPE
	EntityType g_player\entity[1], PLAYER_TYPE
	EntityRadius g_player\entity[0], 2.5
	EntityRadius g_player\entity[1], 2.5
End Function

Function InitializePlayer()
	g_player = New Player
	g_player\score = 0
	g_player\lives = 4
	g_grantExtraLife = True
	g_playerUpTimer = StartTimer( 250, 2500 )
	g_playerDeathTimer = StartTimer( 500 )
	g_playerIdleTimer = StartTimer( 4000, 10000 )
    g_idleTextIdx = 0
End Function

Function SetPlayer( x, y )
	g_player\bx = x
	g_player\by = y
	g_player\moving = False
	g_player\dir = PLAYER_NOP
	g_player\units = 0
	g_player\halfMove = False
	g_player\pos = GetPlatformVertex( g_player\bx, g_player\by )
	g_player\pos\y = g_player\pos\y + PLAYER_OFFSET_Y
	SetPlayerEntity
	For i = 0 To 1
		SetSpriteParent g_upSprite[i], GetBoardPivot()
	Next	
	SetSpriteParent g_calloutSprite, GetBoardPivot()
	ResetTimer( g_playerUpTimer )
	ResetTimer( g_playerIdleTimer )
	g_playerUpMode = True
	g_calloutDisplay = False
	HideSprite g_calloutSprite
	SetSpriteBlink g_upSprite[g_currentPlayer], 250
	ShowSprite g_upSprite[g_currentPlayer]
	ShowPlayer()
	PositionPlayerEntity()
End Function

Function HidePlayer()
	HideEntity g_player\entity[0]
	HideEntity g_player\entity[1]
	HideUpSprites()
	HideSprite g_calloutSprite
	g_calloutDisplay = False
End Function

Function ShowPlayer()
	ShowEntity g_player\entity[0]
	ShowEntity g_player\entity[1]
End Function

Function HideUpSprites()
	For i=0 To 1
		HideSprite g_upSprite[i]
	Next	
End Function

Function HideCallout()
	SetSpriteFadeOut g_calloutSprite
	g_calloutDisplay = False
End Function

Function ShowIdleCallout()
	Local offsetx = 20 + ((88 - (Len( g_idletext[g_idleTextIdx] ) * 16)) / 2)
	If g_calloutDisplay Then Return
	ClearTextureRect g_calloutSprite\texture, 20, 16, 88, 42, $FFFFFFFF
	DrawTextureFont g_finalFight16r, offsetx, 30, g_idleText[g_idleTextIdx], g_calloutSprite\texture
	ShowSprite g_calloutSprite
	SetSpriteFadeIn g_calloutSprite
	g_calloutDisplay = True
	g_idleTextIdx = g_idleTextIdx + 1
	If g_idleTextIdx > 2 
		g_idleTextIdx = 0
	EndIf
End Function

Function ShowWarningCallout()
	If g_calloutDisplay Then Return
	ClearTextureRect g_calloutSprite\texture, 20, 16, 88, 42, $FFFFFFFF
	DrawTextureFont g_finalFight16r, 35, 30, "!!!", g_calloutSprite\texture
	ShowSprite g_calloutSprite
	SetSpriteFadeIn g_calloutSprite
	g_calloutDisplay = True
End Function

Function CheckPlayerCollisions()
	Return (CountCollisions( g_player\entity[1] ) > 0)
End Function

Function MovePlayer( newDir )
    If g_player\moving
        If newDir = GetOppositeDirection( g_player\dir ) And g_player\halfMove = False
            UpdatePlayerPosition
        Else    
            Return
        EndIf    
    EndIf
	g_player\dir = newDir
	If IsMoveValid( g_player\bx, g_player\by, g_player\dir ) And g_player\halfMove = False
		g_player\moving = True
		g_player\units = PLATFORM_DISTANCE - g_player\units
	Else
		If g_player\halfMove
			If g_player\dir = g_player\halfMoveRev
				g_player\moving = True
				g_player\halfMove = True
				g_player\halfMoveReset = True
				g_player\units = PLATFORM_DISTANCE / 2.00
			EndIf
		Else
			If IsDirectionValid( g_player\bx, g_player\by, g_player\dir )
				g_player\moving = True
				g_player\halfMove = True
				g_player\halfMoveReset = False
				g_player\halfMoveRev = GetOppositeDirection( g_player\dir )
                g_player\units = PLATFORM_DISTANCE / 2.00
			EndIf	
		EndIf	
	EndIf	
End Function

Function UpdatePlayer()
	AnimatePlayer
	If g_player\moving
		If IsMoveValid( g_player\bx, g_player\by, g_player\dir ) Or g_player\halfMove = True
			g_player\units = g_player\units - PLAYER_ROLL_SPEED
			If g_player\units < 0
				If g_player\halfMove = False
					UpdatePlayerPosition
				EndIf	
				If g_player\halfMoveReset
					g_player\halfMoveReset = False
					g_player\halfMove = False
				EndIf
				g_player\moving = False
				g_player\units = 0
				g_player\dir = PLAYER_NOP
                If g_player\halfMove = False
                    g_player\pos = GetPlatformVertex( g_player\bx, g_player\by )
                    g_player\pos\y = g_player\pos\y + PLAYER_OFFSET_Y
                EndIf    
			Else
				Select g_player\dir
					Case MOVE_UP
						g_player\pos\z = g_player\pos\z + PLAYER_ROLL_SPEED
						TurnEntity g_player\entity[1], PLAYER_SPIN_SPEED, 0, 0
					Case MOVE_DOWN
						g_player\pos\z = g_player\pos\z - PLAYER_ROLL_SPEED
						TurnEntity g_player\entity[1], -PLAYER_SPIN_SPEED, 0, 0
					Case MOVE_RIGHT
						g_player\pos\x = g_player\pos\x + PLAYER_ROLL_SPEED
						TurnEntity g_player\entity[1], 0, 0, -PLAYER_SPIN_SPEED
					Case MOVE_LEFT
						g_player\pos\x = g_player\pos\x - PLAYER_ROLL_SPEED
						TurnEntity g_player\entity[1], 0, 0, PLAYER_SPIN_SPEED
				End Select
			EndIf
			PositionPlayerEntity()
		Else
			g_player\units = PLATFORM_DISTANCE - g_player\units
			g_player\dir = GetOppositeDirection( g_player\dir )
			g_player\halfMove = True
			g_player\halfMoveReset = True
			g_player\halfMoveRev = 0
		EndIf
	EndIf
End Function

Function UpdatePlayerPosition()
	Select g_player\dir
		Case MOVE_UP
			g_player\by = g_player\by + 1
			If g_player\by > g_boardHeight
				g_player\by = g_boardHeight
			EndIf	
		Case MOVE_DOWN
			g_player\by = g_player\by - 1
			If g_player\by < 1
				g_player\by = 1
			EndIf
		Case MOVE_RIGHT
			g_player\bx = g_player\bx + 1
			If g_player\bx > g_boardWidth
				g_player\bx = g_boardWidth
			EndIf
		Case MOVE_LEFT
			g_player\bx = g_player\bx - 1
			If g_player\bx < 1
				g_player\bx = 1
			EndIf
	End Select
End Function

Function StartPlayerDeathSequence()
	HideUpSprites()
	SoundStopAll
	SoundPlay g_introSound
	g_playerScale = 3.8
	ResetTimer( g_playerDeathTimer )
End Function

Function UpdatePlayerDeathSequence()
	If CheckTimer( g_playerDeathTimer )
		If g_playerScale > .5
			g_playerScale = g_playerScale - .40
			ScaleEntity g_player\entity[0], g_playerScale, g_playerScale, g_playerScale
		Else
			g_player\lives = g_player\lives - 1
			If g_player\lives = 0
				SetGameOverMode
			Else
				SoundStopAll
				FreeLevelResources
				SetGameMode True
			EndIf	
		EndIf	
	EndIf	
End Function

Function AnimatePlayer()
	TurnEntity g_player\entity[0], 0, 8, 0
	If TimerExpired( g_playerUpTimer ) And g_playerUpMode = True
		g_playerUpMode = False
		SetSpriteNop g_upSprite[g_currentPlayer]
		HideSprite g_upSprite[g_currentPlayer]
		EnableCollisions()
	EndIf
	If TimerExpired( g_playerIdleTimer )
		ResetTimer g_playerIdleTimer
		ShowIdleCallout()
	EndIf
	If CheckTimer( g_playerIdleTimer )
		If g_calloutDisplay Then HideCallout()
	EndIf
End Function

Function PositionPlayerEntity()
	PositionEntity g_player\entity[0], g_player\pos\x, g_player\pos\y, g_player\pos\z
	PositionEntity g_player\entity[1], g_player\pos\x, g_player\pos\y, g_player\pos\z
	If TimerExpired( g_playerUpTimer ) = False
		g_upSprite[g_currentPlayer]\pos\x = g_player\pos\x
		g_upSprite[g_currentPlayer]\pos\y = g_player\pos\y + 8
		g_upSprite[g_currentPlayer]\pos\z = g_player\pos\z
		PositionSpriteW g_upSprite[g_currentPlayer]
	EndIf
	g_calloutSprite\pos\x = g_player\pos\x
	g_calloutSprite\pos\y = g_player\pos\y + 10
	g_calloutSprite\pos\z = g_player\pos\z
	PositionSpriteW g_calloutSprite
End Function

Function PlayerAtLocation( x, y )
	If g_player\bx = x And g_player\by = y
		Return True
	Else
		Return False
	EndIf
End Function

Function HandlePlayerInput( replayInput = 0 )
	Local joyButtonDown = 0
	Local joyDir = 0

	If Not InAttractMode()
        joyButtonDown = JoyMappedInput( g_inputMap( INPUT_ROTATE_PIPE, JOYPAD ), HOLD )
		joyButtonDown = joyButtonDown Or KeyDown( g_inputMap( INPUT_ROTATE_PIPE, KEYBOARD ) )
		If JoyMappedInput( g_inputMap( INPUT_MOVE_LEFT, JOYPAD ) ) Or KeyDown( g_inputMap( INPUT_MOVE_LEFT, KEYBOARD ) )
			joyDir = MOVE_LEFT
		ElseIf JoyMappedInput( g_inputMap( INPUT_MOVE_RIGHT, JOYPAD ) ) Or KeyDown( g_inputMap( INPUT_MOVE_RIGHT, KEYBOARD ) )
			joyDir = MOVE_RIGHT
		ElseIf JoyMappedInput( g_inputMap( INPUT_MOVE_UP, JOYPAD ) ) Or KeyDown( g_inputMap( INPUT_MOVE_UP, KEYBOARD ) )
			joyDir = MOVE_UP
		ElseIf JoyMappedInput( g_inputMap( INPUT_MOVE_DOWN, JOYPAD ) ) Or KeyDown( g_inputMap( INPUT_MOVE_DOWN, KEYBOARD ) )
			joyDir = MOVE_DOWN
		Else
			joyDir = 0
		EndIf
        switchCameraHit = JoyMappedInput( g_inputMap( INPUT_SWITCH_CAMERA, JOYPAD ) )
        switchCameraHit = switchCameraHit Or KeyHit( g_inputMap( INPUT_SWITCH_CAMERA, KEYBOARD ) )
		If switchCameraHit Then ToggleCameraView()
	Else
		If replayInput = 0 Then Return
		If replayInput <= 4
			joyDir = replayInput
		Else
			joyButtonDown = True
			joyDir = replayInput - 4
		EndIf	
	EndIf

	If joyDir = MOVE_UP
		MovePlayer( MOVE_UP )
	ElseIf joyDir = MOVE_DOWN
		MovePlayer( MOVE_DOWN )
	ElseIf joyDir = MOVE_RIGHT
		If joyButtonDown
			RotatePipeRight()
		Else
			MovePlayer( MOVE_RIGHT )
		EndIf	
	ElseIf joyDir = MOVE_LEFT
		If joyButtonDown
			RotatePipeLeft()
		Else
			MovePlayer( MOVE_LEFT )
		EndIf	
	EndIf

	If joyButtonDown <> 0 Or joyDir <> 0
		If g_calloutDisplay
			HideCallout
		EndIf
		ResetTimer g_playerIdleTimer
	EndIf
End Function

Function UpdatePlayerScore( amount )
	g_player\score = g_player\score + amount
	UpdateScore()
	If (g_player\score / 10000 > 0) And ( g_player\score / 10000 Mod 2 <> 0 )
		If g_grantExtraLife
			g_player\lives = g_player\lives + 1
			UpdateFaces()
			SoundPlay g_newLifeSound
			g_grantExtraLife = False
		EndIf
	Else
		g_grantExtraLife = True
	EndIf
End Function
