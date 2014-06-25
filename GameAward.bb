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

Const SCORE_TIMER = 75

Global g_winSound.Sound[2]
Global g_scoreTimer.Timer
Global g_playFinale

Function LoadGameAwardResources()
	g_winSound[0] = SoundLoad( RarExtractFile( "res\sounds\level-clear.mp3" ) )
	g_winSound[1] = SoundLoad( RarExtractFile( "res\sounds\level-clear-finale.mp3" ) )
    g_winSound[0]\userFlag = SOUND_TYPE_MUSIC
    g_winSound[1]\userFlag = SOUND_TYPE_MUSIC
End Function

Function StartAwardSequence()
	g_awardState = AWARD_INIT
	SoundStopAll
	If ((g_gameSeconds * SCORE_TIMER) + ((g_boardWidth * g_boardHeight - 1) * SCORE_TIMER)) > 3800
		SoundPlay g_winSound[0]
		g_playFinale = True
	Else
		SoundPlay g_winSound[1]
		g_playFinale = False
	EndIf
End Function

Function UpdateAwardSequence()
	Local totalPieces = g_boardWidth * g_boardHeight - 1

	If SoundPlaying( g_winSound[0] ) = False And g_playFinale
		If g_awardState >= AWARD_INIT And g_awardState <= AWARD_SCORE_PIPES
			SoundPlay g_winSound[1]
		EndIf	
	EndIf

	Select g_awardState
		Case AWARD_INIT
			HidePlayer()
			HideBonuses()
			RemoveIllusions()
			g_scoreTimer = StartTimer( SCORE_TIMER )
			g_awardState = AWARD_SCORE_TIMER
		
		Case AWARD_SCORE_TIMER
			If g_gameSeconds > 0
				If CheckTimer( g_scoreTimer )
					UpdatePlayerScore( 5 )
					g_gameSeconds = g_gameSeconds - 1
				EndIf	
			Else
				ResetTimer( g_scoreTimer )
				g_pipeIdx = 0
				g_wasteFrameCount = 0
				g_awardState = AWARD_SCORE_PIPES
			EndIf
			UpdateTimer()
			
		Case AWARD_SCORE_PIPES	
			If g_pipeIdx <= totalPieces
				If CheckTimer( g_scoreTimer )
					If g_currentPuzzle\pieces[g_pipeIdx]\piece <> PUZZLE_PIECE_N
						UpdatePlayerScore( 10 )
						SetPipeColor g_currentPuzzle\pieces[g_pipeIdx]\entity, False
					EndIf	
					g_pipeIdx = g_pipeIdx + 1
				EndIf	
			Else
				g_awardState = AWARD_PAUSE
				ResetTimer( g_scoreTimer )
			EndIf

		Case AWARD_PAUSE
			If SoundPlaying( g_winSound[0] ) = False And SoundPlaying( g_winSound[1] ) = False
				g_awardState = AWARD_FINI
			EndIf

		Case AWARD_FINI
			SoundStopAll
			FreeLevelResources()
			g_currentLevel = g_currentLevel + 1
			If g_currentLevel > g_puzzleCount
                #ifdef DEMO_BUILD
                    ; This is temporary; need to
                    ; have a "thanks for playing"
                    ; and take them back to the attract sequence
                    g_currentLevel = 1
                #else
                    g_currentLevel = 1
                #endif
			EndIf
			Delete g_scoreTimer
			g_winChannel = 0
			ResetLightning
            SetZenKoanMode True
	End Select
End Function
