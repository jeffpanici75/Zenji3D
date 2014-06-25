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

Const HIGH_SCORE_NOP = 0
Const HIGH_SCORE_SCROLL_UP = 1
Const HIGH_SCORE_SCROLL_DOWN = 2

Const HIGH_SCORE_NAME_LENGTH = 16
Const HIGH_SCORE_CURSOR_X_OFFSET = 216
Const HIGH_SCORE_CURSOR_Y_OFFSET = 115
Const HIGH_SCORE_CURSOR_X_SKIP = 51
Const HIGH_SCORE_CURSOR_Y_SKIP = 46

Type HighScore
	Field name$
	Field score
	Field solved
End Type

Global g_sessionHighScore = 0
Global g_boardSprite.Sprite
Global g_highScoreState
Global g_boardScrollUnits

; ---------------------------------------
; Globals for the High Score name entry.
; ---------------------------------------
Global g_selectSprite.Sprite
Global g_nameSprite.Sprite
Global g_cursorSprite.Sprite
Global g_selectedName$
Global g_charSelectX
Global g_charSelectY
Global g_selectLeft
Global g_selectTop

Function InitializeScoreBoard( reload = False )
    Local tempTex
    If reload
        Delete g_boardSprite
        Delete g_selectSprite
        Delete g_cursorSprite
        Delete g_nameSprite
    EndIf
	g_selectLeft = (GraphicsWidth() - 512) / 2
	g_selectTop = (GraphicsHeight() - 432) / 2
	g_boardSprite = SpriteCreate( (GraphicsWidth() - 512) / 2, GraphicsHeight(), 512, 256 )
    tempTex = LoadTextureNoFilters( RarExtractFile( "res\bitmaps\name-entry.png" ), 1 )
	g_selectSprite = SpriteCreate( g_selectLeft, g_selectTop, 512, 432, tempTex )
    tempTex = LoadTextureNoFilters( RarExtractFile( "res\bitmaps\cursor.png" ) )
	g_cursorSprite = SpriteCreate( g_selectLeft, g_selectTop, 30, 30, tempTex )
    g_nameSprite = SpriteCreate( g_selectLeft + 128, g_selectTop + 64, 256, 32 )
    ClearSprite g_nameSprite
    SetSpriteOrder g_nameSprite, -100
    SetSpriteOrder g_cursorSprite, -100
	SetSpriteHandle g_boardSprite, -1, -1
	SetSpriteHandle g_selectSprite, -1, -1
	SetSpriteHandle g_cursorSprite, -1, -1
	SetSpriteHandle g_nameSprite, -1, -1
	HideSprite g_boardSprite
	HideSprite g_selectSprite
	HideSprite g_cursorSprite
End Function

Function ClearHighScores()
	Delete Each HighScore
End Function

Function LoadHighScores()
    ClearHighScores
	If FileType( "high-scores.dat" ) = 0 
		LoadDummyHighScores()
		Return
	EndIf	
	file = OpenFile( "high-scores.dat" )
	While Not Eof( file )
		name$ = ReadString( file )
		scoreVal = ReadInt( file )
		solvedVal = ReadInt( file )
		score.HighScore = New HighScore
		score\name = name
		score\score = scoreVal
		score\solved = solvedVal
	Wend
	CloseFile file
End Function

Function SaveHighScores()
	SortHighScores()
	file = WriteFile( "high-scores.dat" )
	For score.HighScore = Each HighScore
		WriteString file, score\name
		WriteInt file, score\score
		WriteInt file, score\solved
	Next	
	CloseFile file
End Function

Function IsNewHighScore( newScore )
	For score.HighScore = Each HighScore
		If newScore > score\score
			Return True
		EndIf
	Next
	Return False
End Function

Function SetNewScore( name$, newScore )
	If newScore > g_sessionHighScore
		g_sessionHighScore = newScore
		UpdateHighScore()
	EndIf
	For score.HighScore = Each HighScore
		If newScore > score\score
			score\name = name
			score\score = newScore
			score\solved = g_currentLevel - 1
			SaveHighScores()
			Return
		EndIf
	Next
End Function

Function SortHighScores()
	Local item.HighScore
	Local nextItem.HighScore
	Local p.HighScore
	Local q.HighScore
	Local score

	nextItem = After First HighScore
	While nextItem <> Null
		item = nextItem
		nextItem = After item
		p = Item
		score = item\score
		Repeat
			q = Before p
			If q = Null Then Exit
			If score <= q\score Then Exit
			p = q
		Forever
		q = item
		Insert q Before p
	Wend
End Function

Function LoadDummyHighScores()
	score1.HighScore = New HighScore
	score1\name = "CHIEF CUTTER"
	score1\score = 65536
	score1\solved = 25

	score2.HighScore = New HighScore
	score2\name = "XACTO"
	score2\score = 55788
	score2\solved = 20

	score3.HighScore = New HighScore
	score3\name = "FATBOY JUNIOR"
	score3\score = 53755
	score3\solved = 20

	score4.HighScore = New HighScore
	score4\name = "PRISM"
	score4\score = 50125
	score4\solved = 15

	score5.HighScore = New HighScore
	score5\name = "LISA"
	score5\score = 32767
	score5\solved = 12

	score6.HighScore = New HighScore
	score6\name = "JOE BOY"
	score6\score = 63233
	score6\solved = 10

	score7.HighScore = New HighScore
	score7\name = "MR TONY"
	score7\score = 22331
	score7\solved = 15

	score8.HighScore = New HighScore
	score8\name = "FATBOY"
	score8\score = 1122
	score8\solved = 5

	score9.HighScore = New HighScore
	score9\name = "DUSTY KITTY"
	score9\score = 65431
	score9\solved = 3

	score10.HighScore = New HighScore
	score10\name = "FLOOPDAR MATHMAN"
	score10\score = 256
	score10\solved = 2

	SaveHighScores()
End Function

Function StartNameEntry()
    ShowPointerSprite
	ShowSprite g_nameSprite
	ShowSprite g_selectSprite
	ShowSprite g_cursorSprite
	SetSpriteBlink g_cursorSprite, 150
End Function

Function StopNameEntry()
    HidePointerSprite
	HideSprite g_nameSprite
	HideSprite g_selectSprite
	HideSprite g_cursorSprite
	SetSpriteNop g_cursorSprite
End Function

Function SelectName()
	Local joyButtonDown
	Local joyDir

    If MouseHit( 1 )
        For y = 0 To 6
            For x = 0 To 5
                leftEdge = (g_selectLeft + HIGH_SCORE_CURSOR_X_OFFSET) + x * HIGH_SCORE_CURSOR_X_SKIP
                topEdge = ((g_selectTop + HIGH_SCORE_CURSOR_Y_OFFSET) + y * HIGH_SCORE_CURSOR_Y_SKIP) - 1
                If RectsOverlap( leftEdge, topEdge, 30, 30, MouseX(), MouseY(), 2, 2 )
                    g_charSelectX = x
                    g_charSelectY = y
                    If HandleSelectedItem() Then Return True
                    Goto doneMouse
                EndIf
            Next
        Next
    EndIf    
    joyButtonDown = JoyMappedInput( g_inputMap( INPUT_ROTATE_PIPE, JOYPAD ) )
	joyButtonDown = joyButtonDown Or KeyHit( g_inputMap( INPUT_ROTATE_PIPE, KEYBOARD ) )
	If JoyHitLeft() Or KeyHit( g_inputMap( INPUT_MOVE_LEFT, KEYBOARD ) )
		joyDir = MOVE_LEFT
	ElseIf JoyHitRight() Or KeyHit( g_inputMap( INPUT_MOVE_RIGHT, KEYBOARD ) )
		joyDir = MOVE_RIGHT
	ElseIf JoyHitUp() Or KeyHit( g_inputMap( INPUT_MOVE_UP, KEYBOARD ) )
		joyDir = MOVE_UP
	ElseIf JoyHitDown() Or KeyHit( g_inputMap( INPUT_MOVE_DOWN, KEYBOARD ) )
		joyDir = MOVE_DOWN
	Else
		joyDir = 0
	EndIf	
	FlushJoy
	If joyDir = MOVE_UP
		If g_charSelectY > 0
			g_charSelectY = g_charSelectY - 1
		EndIf	
	ElseIf joyDir = MOVE_DOWN
		If g_charSelectY < 6
			g_charSelectY = g_charSelectY + 1
		EndIf
	ElseIf joyDir = MOVE_RIGHT
		If g_charSelectY = 6
			If g_charSelectX < 2
				g_charSelectX = g_charSelectX + 1
			EndIf
		Else
			If g_charSelectX < 5
				g_charSelectX = g_charSelectX + 1
			EndIf
		EndIf	
	ElseIf joyDir = MOVE_LEFT
		If g_charSelectX > 0
			g_charSelectX = g_charSelectX - 1
		EndIf	
	ElseIf joyButtonDown
        Return HandleSelectedItem()
	EndIf
.doneMouse
	If g_charSelectY = 6 And g_charSelectX > 2
		g_charSelectX = 2
	EndIf
	g_cursorSprite\pos\x = (g_selectLeft + HIGH_SCORE_CURSOR_X_OFFSET) + g_charSelectX * HIGH_SCORE_CURSOR_X_SKIP
	g_cursorSprite\pos\y = ((g_selectTop + HIGH_SCORE_CURSOR_Y_OFFSET) + g_charSelectY * HIGH_SCORE_CURSOR_Y_SKIP) - 1
	PositionSprite g_cursorSprite
	Return False
End Function

Function HandleSelectedItem()
    If g_charSelectY = 6 And g_charSelectX = 0
        If Len( g_selectedName ) > 0
            g_selectedName = Left( g_selectedName, Len( g_selectedName ) - 1 )
            DrawSelectedName
        EndIf
    ElseIf g_charSelectY = 6 And g_charSelectX = 1
        If Len( g_selectedName ) < HIGH_SCORE_NAME_LENGTH
            g_selectedName = g_selectedName + " "
            DrawSelectedName
        EndIf   
    ElseIf g_charSelectY = 6 And g_charSelectX = 2
        StopNameEntry
        Return True
    Else
        If Len( g_selectedName ) < HIGH_SCORE_NAME_LENGTH
            cidx = (g_charSelectY * 6 + g_charSelectX) + 1
            g_selectedName = g_selectedName + Mid( FINAL_FIGHT_CHARSET, cidx, 1 )
            DrawSelectedName
        EndIf
    EndIf
    Return False
End Function

Function DrawSelectedName()
	ClearSprite g_nameSprite
	DrawTextureFont g_finalFight16b, (256 - (Len(g_selectedName) * 16)) / 2, 0, g_selectedName, g_nameSprite\texture
End Function

Function GetSelectedName$()
	Return g_selectedName
End Function

Function ResetSelectedName()
	g_selectedName = ""
	g_charSelectX = 0
	g_charSelectY = 0
	ClearSprite g_nameSprite
End Function

Function HideScoreBoard()
	HideSprite g_boardSprite
End Function

Function ShowScoreBoard()
	ShowEntity g_boardSprite\entity
End Function

Function ScrollScoresUp()
    g_highScoreState = HIGH_SCORE_SCROLL_UP
    g_boardScrollUnits = ((GraphicsHeight() - (GraphicsHeight() / 2)) / 6) + 10
    g_boardSprite\pos\y = GraphicsHeight()
    PositionSprite g_boardSprite
End Function

Function ScrollScoresDown()
    g_highScoreState = HIGH_SCORE_SCROLL_DOWN
    g_boardScrollUnits = ((GraphicsHeight() - (GraphicsHeight() / 2)) / 6) + 10
End Function

Function DrawHighScores()
	ClearSprite g_boardSprite
	offset = 0
	For score.HighScore = Each HighScore
		DrawTextureFont g_finalFight16b,   0, offset, score\name, g_boardSprite\texture
		DrawTextureFont g_finalFight16r, 352, offset, ZeroPadLeft( score\score, 6 ), g_boardSprite\texture
		DrawTextureFont g_finalFight16r, 464, offset, ZeroPadLeft( score\solved, 3 ), g_boardSprite\texture
		offset = offset + 24
	Next	
End Function	

Function UpdateScoreBoard()
	Select g_highScoreState
		Case HIGH_SCORE_SCROLL_UP
            If g_boardScrollUnits > 0
			    g_boardSprite\pos\y = g_boardSprite\pos\y - 6
                g_boardScrollUnits = g_boardScrollUnits - 1
            Else    
				g_highScoreState = HIGH_SCORE_NOP
            EndIf    
		Case HIGH_SCORE_SCROLL_DOWN
            If g_boardScrollUnits > 0
                g_boardSprite\pos\y = g_boardSprite\pos\y + 6
                g_boardScrollUnits = g_boardScrollUnits - 1
            Else
				g_highScoreState = HIGH_SCORE_NOP
			EndIf
	End Select
	PositionSprite g_boardSprite
End Function
