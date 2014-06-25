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

Const GAME_OVER_INTERLUDE = 1
Const GAME_OVER_NAME_ENTRY = 2
Const GAME_OVER_CLEANUP = 3

Global g_gameOver
Global g_newHighScore
Global g_gameOverState
Global g_gameOverMusic.Sound
Global g_highScoreMusic.Sound

Function LoadGameOverResources( reload = False )
    If reload = False
	    g_gameOverMusic = SoundLoad( RarExtractFile( "res\sounds\game-over.mp3" ) )
        g_gameOverMusic\userFlag = SOUND_TYPE_MUSIC
	    g_highScoreMusic = SoundLoad( RarExtractFile( "res\sounds\high-score.mp3" ), True )
        g_highScoreMusic\userFlag = SOUND_TYPE_MUSIC
    EndIf    
	g_gameOver = LoadMesh( RarExtractFile( "res\models\game-over.x" ) )
	HideEntity g_gameOver
End Function

Function FreeGameOverResources()
	SoundFree g_gameOverMusic
	SoundFree g_highScoreMusic
	FreeEntity g_gameOver
End Function

Function StartGameOverSequence()
	FlushJoy
	FlushKeys
	SoundStopAll
	If IsCameraOverhead()
		ToggleCameraView()
		UpdateCamera()
	EndIf
	SoundPlay g_gameOverMusic
	ScaleEntity g_gameOver, 15, 15, 15
	RotateEntity g_gameOver, 40, 0, 0
	PositionEntity g_gameOver, g_cameraVertex\x, BOARD_OFFSET_Y + 30, g_cameraVertex\z + 10
	g_newHighScore = IsNewHighScore( g_player\score )
	g_gameOverState = GAME_OVER_INTERLUDE
	FadeOutStatistics()
	ShowEntity g_gameOver
End Function

Function UpdateGameOverSequence()
	UpdateSprites()
	UpdateSounds()
	Select g_gameOverState
		Case GAME_OVER_INTERLUDE
			If SoundPlaying( g_gameOverMusic ) = False Or JoyMappedInput( g_inputMap( INPUT_START_BUTTON, JOYPAD ) ) Or KeyHit( g_inputMap( INPUT_START_BUTTON, KEYBOARD ) )
				If g_newHighScore
					g_gameOverState = GAME_OVER_NAME_ENTRY
					CameraOverWater()
					ResetSelectedName()
					StartNameEntry()
					SoundStop g_gameOverMusic
					SoundPlay g_highScoreMusic
				Else
					g_gameOverState = GAME_OVER_CLEANUP
				EndIf	
			Else	
				TurnEntity g_gameOver, 5, 0, 0
			EndIf
		Case GAME_OVER_NAME_ENTRY
			If SelectName()
				SetNewScore( GetSelectedName(), g_player\score )
				g_gameOverState = GAME_OVER_CLEANUP
			EndIf	
		Case GAME_OVER_CLEANUP
            EnableDiskAccessIcon
            CameraOverWater
			RemoveIllusions()
			FreeLevelResources()
			FreePlayerResources()
			If g_newHighScore
				SetAttractMode( ATTRACT_HIGHSCORES_STATE )
			Else
                SoundStopAll
				SetAttractMode( ATTRACT_SPLASH_STATE )
			EndIf
            DisableDiskAccessIcon
            HideEntity g_gameOver
            PositionEntity g_gameOver, 0, 0, 0
	End Select		
End Function
