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

Const ATTRACT_SPLASH_STATE = 1
Const ATTRACT_CREDITS_STATE = 2
Const ATTRACT_RULES_STATE = 3
Const ATTRACT_HIGHSCORES_STATE = 4
Const ATTRACT_GAME_DEMO_STATE = 5

Global g_displayFireMsg
Global g_attractState
Global g_attractStateTimer.Timer
Global g_enterState
Global g_leaveState
Global g_stateWait[5]
Global g_scrollTimer.Timer
Global g_creditState
Global g_creditStateTimer.Timer
Global g_ruleState
Global g_ruleStateChange
Global g_ruleStateTimer.Timer
Global g_firstCheck

Global g_textSprite.Sprite[2]
Global g_fireSprite.Sprite
Global g_smallTextSprite.Sprite
Global g_zenjiLogo

Global g_demoScript[20]
Global g_demoScriptIdx
Global g_demoTimer.Timer

Global g_demoPipes[3]
Global g_demoBalls[2]
Global g_demoFire.Fire
Global g_ballDirX#
Global g_ballDelta#
Global g_demoSource
Global g_ruleAnimPivot
Global g_sparkEndPivot
Global g_sparkTimer.Timer
Global g_shootSwitch
Global g_demoBolt.LightningBolt
Global g_demoRotateCount
Global g_demoBonusColor
Global g_demoBonusSprite.Sprite
Global g_demoBonusSeconds
Global g_demoBonusTimer.Timer
Global g_attractMusic.Sound

.demo_script
Data MOVE_DOWN,   ROTATE_LEFT, MOVE_UP,    ROTATE_LEFT
Data MOVE_RIGHT,  MOVE_RIGHT,  MOVE_UP,    MOVE_LEFT
Data ROTATE_LEFT, MOVE_LEFT,   MOVE_LEFT,  MOVE_DOWN
Data MOVE_UP,     MOVE_RIGHT,  MOVE_RIGHT, ROTATE_RIGHT
Data MOVE_RIGHT,  MOVE_DOWN,   MOVE_DOWN,  MOVE_LEFT

.state_waits
Data 5000, 24000, 15000, 12000, 11000

Function InitializeAttractSequence()
	Restore demo_script
	For i = 0 To 19
		Read g_demoScript[i]
	Next
	Restore state_waits
	For i = 0 To 4
		Read g_stateWait[i]
	Next
End Function

Function LoadAttractResources()
	g_attractMusic = SoundLoad( RarExtractFile( "res\sounds\attract.mp3" ), True )
    g_attractMusic\userFlag = SOUND_TYPE_MUSIC
    UpdateDiskAccessIcon
	g_zenjiLogo = LoadMesh( RarExtractFile( "res\models\zenji-logo.x" ) )
	RotateEntity g_zenjiLogo, 50, 0, 0
	TurnEntity g_zenjiLogo, 0, 30, 0
	HideEntity g_zenjiLogo
	For i=0 To 1
		g_textSprite[i] = SpriteCreate( (GraphicsWidth() - 512) / 2, ((GraphicsHeight() - 256) / 2) + 64, 512, 256 )
		SetSpriteHandle g_textSprite[i], -1, -1
        UpdateDiskAccessIcon
	Next
	g_smallTextSprite = SpriteCreate( GraphicsWidth() / 2, (GraphicsHeight() / 3) , 256, 16 )
	g_fireSprite = SpriteCreate( (GraphicsWidth() - 160) / 2, GraphicsHeight() - 16, 256, 32 )
	SetSpriteHandle g_fireSprite, -1, -1
	SetSpriteOrder g_fireSprite, -75
	DrawTextureFont g_finalFight16r, 0, 0, "PRESS START", g_fireSprite\texture
	HideSprite g_fireSprite
    UpdateDiskAccessIcon
	For i=0 To 1
		g_demoBalls[i] = CopyEntity( g_balls[i] )
		RotateEntity g_demoBalls[i], 50, 0, 0
		HideEntity g_demoBalls[i]
	Next
	SetupRuleAnimPivot()
    UpdateDiskAccessIcon
End Function

Function StartAttractSequence( startState = ATTRACT_SPLASH_STATE )
	g_attractStateTimer = StartTimer( 1000 )
	g_attractState = startState
	g_enterState = True
	g_leaveState = False
	CameraOverWater()
	ShowSprite g_fireSprite
	SetSpriteBlink g_fireSprite
End Function

Function StopAttractSequence()
	If g_attractState = ATTRACT_GAME_DEMO_STATE
		FreeLevelResources()
		FreePlayerResources()
	EndIf	
	HideScoreBoard()
	HideZenjiLogo()
	EraseFires()
    FreeSprite g_demoBonusSprite
	FreeEntity g_ruleAnimPivot
	For i=0 To 1
        FreeSprite g_textSprite[i]
	Next
	FreeEntity g_zenjiLogo
    FreeSprite g_fireSprite
    FreeSprite g_smallTextSprite
    SoundStopAll
	SoundFree g_attractMusic
    Delete g_attractStateTimer
    Delete g_demoBonusTimer
End Function

Function ShutdownAttractSequence()
	If g_attractMusic <> Null
		SoundStop g_attractMusic
	EndIf	
End Function

Function UpdateAttractSequence()
	Local x, y

	Select g_attractState
		Case ATTRACT_SPLASH_STATE
			If g_enterState
				HideZenjiLogo
                ShowSplashScreen
				SoundPlay g_attractMusic
			EndIf

			If g_leaveState
				HideSplashScreen
			EndIf

		Case ATTRACT_CREDITS_STATE
			If g_enterState
				g_creditStateTimer = StartTimer( 3000 )
				g_scrollTimer = StartTimer( 1500 ) 
				g_creditState = 0
				g_firstCheck = 1
				For i=0 To 1
					ShowEntity g_textSprite[i]\entity
					EntityAlpha g_textSprite[i]\entity, 0
				Next
			EndIf

			If CheckTimer( g_creditStateTimer, g_firstCheck )
				g_firstCheck = 0
				g_creditState = g_creditState + 1
				If g_creditState > 8
					g_creditState = 1
				EndIf
				ClearSprite g_textSprite[0]
				ClearSprite g_textSprite[1]
				For i=0 To 1
					g_textSprite[i]\pos\x = ( (GraphicsWidth() - 512) / 2 )  + 128
					g_textSprite[i]\pos\y = ( (GraphicsHeight() - 256) / 2 ) + 128
				Next	
				g_textSprite[0]\pos\x = g_textSprite[0]\pos\x - 32
				g_textSprite[1]\pos\x = g_textSprite[1]\pos\x + 32
				PositionSprite g_textSprite[0]
				PositionSprite g_textSprite[1]
				Select g_creditState
					Case 1
						y = 0
						DrawTextureFont g_finalFight16r, 0, y, "EPYX SOFTWARE", g_textSprite[0]\texture: y = y + 24
						DrawTextureFont g_finalFight16b, 0, y, "PRESENTS", g_textSprite[1]\texture
						SetSpriteFadeIn( g_textSprite[0] )
						SetSpriteFadeIn( g_textSprite[1] )
						ResetTimer( g_scrollTimer )
						
					Case 2
						y = 0
						DrawTextureFont g_finalFight16b, 0, y, "A", g_textSprite[0]\texture: y = y + 24
						DrawTextureFont g_finalFight16r, 0, y, "WORKER BEE ENTERTAINMENT", g_textSprite[1]\texture: y = y + 24
						DrawTextureFont g_finalFight16b, 0, y, "PRODUCTION", g_textSprite[0]\texture
						SetSpriteFadeIn( g_textSprite[0] )
						SetSpriteFadeIn( g_textSprite[1] )
						ResetTimer( g_scrollTimer )

					Case 3
						y = 0
						DrawTextureFont g_finalFight16b, 0, y, "BASED ON ZENJI BY", g_textSprite[0]\texture: y = y + 24
						DrawTextureFont g_finalFight16r, 0, y, "MATTHEW HUBBARD", g_textSprite[1]\texture
						SetSpriteFadeIn( g_textSprite[0] )
						SetSpriteFadeIn( g_textSprite[1] )
						ResetTimer( g_scrollTimer )
						ShowZenjiLogo()

					Case 4
						y = 0
						DrawTextureFont g_finalFight16b, 0, y, "PROGRAMMING", g_textSprite[0]\texture: y = y + 24
						DrawTextureFont g_finalFight16r, 0, y, "JEFF PANICI", g_textSprite[1]\texture
						SetSpriteFadeIn( g_textSprite[0] )
						SetSpriteFadeIn( g_textSprite[1] )
						ResetTimer( g_scrollTimer )

					Case 5
						y = 0
						DrawTextureFont g_finalFight16b, 0, y, "ARTWORK", g_textSprite[0]\texture: y = y + 24
						DrawTextureFont g_finalFight16r, 0, y, "EDGAR IBARRA", g_textSprite[1]\texture
						SetSpriteFadeIn( g_textSprite[0] )
						SetSpriteFadeIn( g_textSprite[1] )
						ResetTimer( g_scrollTimer )
						
					Case 6
						y = 0
						DrawTextureFont g_finalFight16b, 0, y, "SOUND AND MUSIC", g_textSprite[0]\texture: y = y + 24
						DrawTextureFont g_finalFight16r, 0, y, "AARON ACKERSON", g_textSprite[1]\texture
						SetSpriteFadeIn( g_textSprite[0] )
						SetSpriteFadeIn( g_textSprite[1] )
						ResetTimer( g_scrollTimer )

					Case 7
						y = 0
						DrawTextureFont g_finalFight16b, 0, y, "GAME TESTING", g_textSprite[0]\texture: y = y + 24
						DrawTextureFont g_finalFight16r, 0, y, "JOSHUA PANICI", g_textSprite[1]\texture: y = y + 24
						DrawTextureFont g_finalFight16r, 0, y, "JOE EBERLE", g_textSprite[1]\texture
						SetSpriteFadeIn( g_textSprite[0] )
						SetSpriteFadeIn( g_textSprite[1] )
						ResetTimer( g_scrollTimer )

					Case 8
						y = 0
						DrawTextureFont g_finalFight16b, 0, y, "SPECIAL THANKS", g_textSprite[0]\texture: y = y + 24
						DrawTextureFont g_finalFight16r, 0, y, "MATTHEW HUBBARD", g_textSprite[1]\texture: y = y + 24
						DrawTextureFont g_finalFight16r, 0, y, "MARK SIBLY", g_textSprite[1]\texture: y = y + 24
						DrawTextureFont g_finalFight16r, 0, y, "JOE EBERLE", g_textSprite[1]\texture: y = y + 24
						DrawTextureFont g_finalFight16r, 0, y, "LISA PANICI", g_textSprite[1]\texture
						SetSpriteFadeIn( g_textSprite[0] )
						SetSpriteFadeIn( g_textSprite[1] )
						ResetTimer( g_scrollTimer )
				End Select
			EndIf

			g_textSprite[0]\pos\x = g_textSprite[0]\pos\x + .35
			g_textSprite[1]\pos\x = g_textSprite[1]\pos\x - .35
			PositionSprite g_textSprite[0]
			PositionSprite g_textSprite[1]

			If CheckTimer( g_scrollTimer )
				SetSpriteFadeOut( g_textSprite[0] )
				SetSpriteFadeOut( g_textSprite[1] )
			EndIf

			If g_leaveState
				Delete g_creditStateTimer 
				Delete g_scrollTimer
				For i=0 To 1
					ClearSprite g_textSprite[i]
					HideEntity g_textSprite[i]\entity
				Next
			EndIf

		Case ATTRACT_RULES_STATE
			If g_enterState
				ShowZenjiLogo()
				SetupRuleAnimations()
				g_ruleStateTimer = StartTimer( 3000 )
				g_ruleState = 0
				g_firstCheck = 1
				For i=0 To 1
					g_textSprite[i]\pos\x = (GraphicsWidth() - 256) / 2
                    g_textSprite[i]\pos\y = ( (GraphicsHeight() - 256) / 2 ) + 115
					ClearSprite g_textSprite[i]
					ShowEntity g_textSprite[i]\entity
					EntityAlpha g_textSprite[i]\entity, 0
				Next
			EndIf	

			If CheckTimer( g_ruleStateTimer, g_firstCheck )
				g_firstCheck = 0
				g_ruleState = g_ruleState + 1
				If g_ruleState > 5
					g_ruleState = 5
				EndIf
				For i=0 To 1
					ClearSprite g_textSprite[i]
				Next	
				g_ruleStateChange = True
				Select g_ruleState
					Case 1
						y = 0
						DrawTextureFont g_finalFight16b, 0, y, "MOVE", g_textSprite[0]\texture
						DrawTextureFont g_finalFight16r, 80, y, "THE FACE", g_textSprite[0]\texture: y = y + 17
						DrawTextureFont g_finalFight16b, 0, y, "WITH JOYSTICK", g_textSprite[0]\texture
						SetSpriteFadeIn( g_textSprite[0] )
					Case 2
						y = 0
						DrawTextureFont g_finalFight16b, 0, y, "WITH", g_textSprite[1]\texture
						DrawTextureFont g_finalFight16r, 80, y, "BUTTON", g_textSprite[1]\texture
						DrawTextureFont g_finalFight16b, 192, y, "DOWN", g_textSprite[1]\texture: y = y + 17
						DrawTextureFont g_finalFight16b, 0, y, "THE JOYSTICK CAN SPIN", g_textSprite[1]\texture: y = y + 17
						DrawTextureFont g_finalFight16b, 0, y, "THE PATHS RIGHT AND LEFT", g_textSprite[1]\texture
						SetSpriteFadeOut( g_textSprite[0] )
						SetSpriteFadeIn( g_textSprite[1] )
					Case 3
						y = 0
						DrawTextureFont g_finalFight16b, 0, y, "TURN THE MAZE", g_textSprite[0]\texture
						DrawTextureFont g_finalFight16r, 224, y, "GREEN", g_textSprite[0]\texture: y = y + 17
						DrawTextureFont g_finalFight16b, 0, y, "BY CONNECTING PATHS TO", g_textSprite[0]\texture: y = y + 17
						DrawTextureFont g_finalFight16r, 0, y, "THE SOURCE", g_textSprite[0]\texture
						SetSpriteFadeOut( g_textSprite[1] )
						SetSpriteFadeIn( g_textSprite[0] )
					Case 4
						y = 0
						DrawTextureFont g_finalFight16b, 0, y, "AVOID", g_textSprite[1]\texture
						DrawTextureFont g_finalFight16r, 96, y, "THE FLAMES", g_textSprite[1]\texture: y = y + 17
						DrawTextureFont g_finalFight16b, 0, y, "AND THEIR SPARKS", g_textSprite[1]\texture
						SetSpriteFadeOut( g_textSprite[0] )
						SetSpriteFadeIn( g_textSprite[1] )
					Case 5	
						y = 0
						DrawTextureFont g_finalFight16b, 0, y, "PICK UP", g_textSprite[0]\texture
						DrawTextureFont g_finalFight16r, 128, y, "THE NUMBERS", g_textSprite[0]\texture: y = y + 17
						DrawTextureFont g_finalFight16b, 0, y, "AND EARN BONUS POINTS", g_textSprite[0]\texture
						SetSpriteFadeOut( g_textSprite[1] )
						SetSpriteFadeIn( g_textSprite[0] )
				End Select
				For i=0 To 1
					PositionSprite g_textSprite[i]
				Next	
			EndIf
			UpdateRuleAnimations()
			g_ruleStateChange = False
			If g_leaveState
				Delete g_ruleStateTimer 
				For i=0 To 1
					ClearSprite g_textSprite[i]
					HideEntity g_textSprite[i]\entity
				Next
				TeardownRuleAnimations()
			EndIf

		Case ATTRACT_HIGHSCORES_STATE
			If g_enterState
				ShowZenjiLogo
				ClearSprite g_smallTextSprite
				DrawTextureFont g_finalFight16r, 40, 0, "HIGH SCORES", g_smallTextSprite\texture
				SetSpriteFadeIn g_smallTextSprite
				DrawHighScores
				ScrollScoresUp
				ShowScoreBoard
				g_scrollTimer = StartTimer( 8500 ) 
			EndIf

			If CheckTimer( g_scrollTimer )
				ScrollScoresDown()
				SetSpriteFadeOut( g_smallTextSprite )
			EndIf

			UpdateScoreBoard()

			If g_leaveState
				HideScoreBoard()
				ClearSprite g_smallTextSprite
				Delete g_scrollTimer
			End If

		Case ATTRACT_GAME_DEMO_STATE
			If g_enterState
				HideZenjiLogo()
				g_currentLevel = 1
				InitializePlayer()
				PrepareCurrentLevel()
				g_demoScriptIdx = 0
				g_demoTimer = StartTimer( 500 )
			EndIf

			If CheckTimer( g_demoTimer )
				HandlePlayerInput( g_demoScript[g_demoScriptIdx] )
				g_demoScriptIdx = g_demoScriptIdx + 1
				If g_demoScriptIdx > 19
					g_demoScriptIdx = 19
				EndIf
			EndIf

			UpdateIllusions()
			UpdatePlayer()
			UpdatePipes()
			UpdateTheSource()
			UpdateBonuses()
			UpdateGameTimer()
			UpdateCamera()
			CheckGameCollisions()

			If g_leaveState
				FreeLevelResources()
				FreePlayerResources()
				CameraOverWater()
			EndIf

	End Select

	TurnEntity g_balls[0], 0, 6, 0

	If KeyHit( g_inputMap( INPUT_START_BUTTON, KEYBOARD ) ) Or JoyMappedInput( g_inputMap( INPUT_START_BUTTON, JOYPAD ) ) Or MouseHit( 1 )
		StopAttractSequence
        SetMainMenuMode
		Return
	EndIf

	If g_enterState Then g_enterState = False
	If g_leaveState
		g_leaveState = False
		g_enterState = True
		If g_attractState = ATTRACT_HIGHSCORES_STATE And g_newHighScore
			g_newHighScore = False
			g_attractState = ATTRACT_SPLASH_STATE
			SoundStopAll
		Else	
			g_attractState = g_attractState + 1
			If g_attractState > ATTRACT_GAME_DEMO_STATE
				g_attractState = ATTRACT_SPLASH_STATE
			EndIf
		EndIf	
	End If

	If CheckTimer( g_attractStateTimer, g_stateWait[g_attractState-1] ) Then g_leaveState = True

End Function

Function ShowZenjiLogo()
	For i=0 To 1
		PositionEntity g_balls[i], g_cameraVertex\x, g_cameraVertex\y - 10, g_cameraVertex\z + 10
		ShowEntity g_balls[i]
	Next
	PositionEntity g_zenjiLogo, g_cameraVertex\x, g_cameraVertex\y - 3.65, g_cameraVertex\z + 4
	ShowEntity g_zenjiLogo
End Function

Function HideZenjiLogo()
	For i=0 To 1
		HideEntity g_balls[i]
	Next
	HideEntity g_zenjiLogo
End Function

Function SetupRuleAnimations()
	g_demoRotateCount = 90
	g_sparkTimer = StartTimer( 1500 )
	g_demoFire = AddFire( 0, 0, 0 )
	EntityParent g_demoFire\piv, g_ruleAnimPivot
	PositionEntity g_demoFire\piv, 26, ILLUSION_OFFSET_Y, 0
	RotateEntity g_demoFire\piv, 60, 0, 0
	HideEntity g_demoFire\piv
	For i=0 To 1
		EntityParent g_demoBalls[i], g_ruleAnimPivot
		PositionEntity g_demoBalls[i], 0, 0, 0
	Next
	g_ballDirX# = .5
	g_ballDelta# = 26
	PositionEntity g_ruleAnimPivot, g_cameraVertex\x - 15, g_cameraVertex\y - 45, g_cameraVertex\z + 10
	ShowEntityAndChildren g_ruleAnimPivot
End Function

Function TeardownRuleAnimations()
	HideEntityAndChildren g_ruleAnimPivot
	EraseFires()
End Function

Function UpdateRuleAnimations()
	Select g_ruleState
		Case 1
            If g_ruleStateChange
                HideSprite g_demoBonusSprite
            EndIf
			For i=0 To 1
				TranslateEntity g_demoBalls[i], g_ballDirX, 0, 0
			Next
			TurnEntity g_demoBalls[1], 0, 6, 0
			g_ballDelta = g_ballDelta - .5
			If g_ballDelta <= 0
				g_ballDelta = 0
				g_ballDirX = 0
			EndIf
		Case 2
			If g_ruleStateChange
				SetPipeColor g_demoPipes[2], False
			EndIf	
			If g_demoRotateCount > 0
				TurnEntity g_demoPipes[2], 0, -10, 0		
				g_demoRotateCount = g_demoRotateCount - 10
			EndIf
		Case 3
			If g_demoRotateCount < 90
				TurnEntity g_demoPipes[2], 0, 10, 0
				g_demoRotateCount = g_demoRotateCount + 10
			Else
				SetPipeColor g_demoPipes[2], True
			EndIf
		Case 4
			If g_ruleStateChange
				HideEntity g_demoBalls[0]
				HideEntity g_demoBalls[1]
				ShowEntity g_demoFire\piv
				g_shootSwitch = False
				ResetTimer g_sparkTimer
			EndIf
			If CheckTimer( g_sparkTimer )
				g_shootSwitch = Not g_shootSwitch
				g_demoBolt  = CreateLightningBolt( g_demoFire\piv, g_sparkEndPivot, 13, 1.5 )
			EndIf		
			If g_shootSwitch = False
				AddParticle( g_demoFire\piv )
			Else
				UpdateLightning( g_demoBolt )
			EndIf
			UpdateFires()
		Case 5	
			If g_ruleStateChange
				FreeLightningBolt( g_demoBolt )
				HideEntity g_demoFire\piv
				ShowEntity g_demoBalls[0]
				ShowEntity g_demoBalls[1]
                g_demoBonusSeconds = 9
                g_demoBonusColor = 1
                ClearSprite g_demoBonusSprite
                DrawTextureFont g_finalFight16r, 0, 0, g_demoBonusSeconds, g_demoBonusSprite\texture
                ShowSprite g_demoBonusSprite
				g_ballDirX = - .5
				g_ballDelta = 26
			EndIf
			For i=0 To 1
				TranslateEntity g_demoBalls[i], g_ballDirX, 0, 0
			Next
			TurnEntity g_demoBalls[1], 0, 6, 0
			g_ballDelta = g_ballDelta - .5
			If g_ballDelta <= 0
				g_ballDelta = 0
				g_ballDirX = 0
			EndIf
            If CheckTimer( g_demoBonusTimer )
                g_demoBonusColor = Not g_demoBonusColor
                g_demoBonusSeconds = g_demoBonusSeconds - 1
                ClearSprite g_demoBonusSprite
                If g_demoBonusColor
                    DrawTextureFont g_finalFight16r, 0, 0, g_demoBonusSeconds, g_demoBonusSprite\texture
                Else
                    DrawTextureFont g_finalFight16b, 0, 0, g_demoBonusSeconds, g_demoBonusSprite\texture
                EndIf
            EndIf
	End Select
	g_spriteScale = g_spriteScale + g_scaleDelta
	If g_spriteScale < 2 Or g_spriteScale > 8
		g_scaleDelta = -g_scaleDelta
	EndIf
	ScaleSprite g_demoSource, g_spriteScale, g_spriteScale
	TurnEntity g_demoBalls[0], 0, 6, 0
End Function

Function SetupRuleAnimPivot()
	g_ruleAnimPivot = CreatePivot()
	g_demoPipes[0] = CopyEntity( g_pipes[1], g_ruleAnimPivot )
	For i=1 To 2
		g_demoPipes[i] = CopyEntity( g_pipes[0], g_ruleAnimPivot )
		RotateEntity g_demoPipes[i], 0, 90, 0
	Next
	offsetx# = 0
	g_demoSource = CopyEntity( g_energySprite, g_ruleAnimPivot )
	PositionEntity g_demoSource, offsetx + 1.5, 2, 0
	EntityAlpha g_demoSource, .8
	ScaleSprite g_demoSource, 8, 8
	For i=0 To 2
		PositionEntity g_demoPipes[i], offsetx, 0, 0
		SetPipeColor g_demoPipes[i], True
		offsetx = offsetx + 13
	Next
	g_sparkEndPivot = CreatePivot( g_ruleAnimPivot )
	PositionEntity g_sparkEndPivot, 1.5, ILLUSION_OFFSET_Y, 0
    g_demoBonusSprite = SpriteCreate( 0, 0, 32, 32, 0, g_ruleAnimPivot )
    RotateEntity g_demoBonusSprite\entity, 65, 0, 0
    ResizeSpriteW g_demoBonusSprite, 3, 3, 1
    ClearSprite g_demoBonusSprite
    g_demoBonusSprite\pos\x = 14.5
    g_demoBonusSprite\pos\y = -1.7
    g_demoBonusSprite\pos\z = 0
    PositionSpriteW g_demoBonusSprite
    HideSprite g_demoBonusSprite
    g_demoBonusTimer = StartTimer( 1000 )
    HideEntityAndChildren g_ruleAnimPivot
End Function
