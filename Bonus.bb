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

Const BONUS_COUNT_STATE = 1
Const BONUS_SCORE_STATE = 2

Global g_bonusSound.Sound

Type Bonus
	Field bx
	Field by
	Field state
	Field seconds
	Field prevType
	Field sprite.Sprite
	Field piece.PuzzlePiece
	Field timer.Timer
	Field colorFlag
End Type

Function LoadBonusResources()
	g_bonusSound = SoundLoad( RarExtractFile( "res\sounds\bonus.wav" ) )
    g_bonusSound\userFlag = SOUND_TYPE_FX
End Function

Function CheckLevelAddBonuses()
	Local cx, cy
    SeedRnd MilliSecs()
	If( Rand( 2, 40 ) Mod 2 ) = 0
        cx = Rand( 1, g_currentPuzzle\width )
        cy = Rand( 1, g_currentPuzzle\height )
        If IsValidBonusPosition( cx, cy ) 
			CreateBonus( GetPuzzlePiece( cx, cy ), cx, cy )
		EndIf	
	EndIf	
End Function

Function IsValidBonusPosition( x, y )
	Local thisPiece.PuzzlePiece = GetPuzzlePiece( x, y )
	Local realType = thisPiece\piece
	Local okay = False
	If x = g_currentPuzzle\sx And y = g_currentPuzzle\sy 
		Return False
	EndIf	
	thisPiece\piece = PUZZLE_PIECE_N
	If IsPuzzleSolvable() 
		okay = True
	EndIf	
	thisPiece\piece = realType
	Return okay
End Function

Function UpdateBonuses()
	For bonus.Bonus = Each Bonus
		If bonus\seconds > 0
			Select bonus\state
				Case BONUS_COUNT_STATE
					If CheckTimer( bonus\timer )
						bonus\seconds = bonus\seconds - 1
						UpdateBonusTexture( bonus )
					EndIf
					If PlayerAtLocation( bonus\bx, bonus\by )
						bonus\state = BONUS_SCORE_STATE
						If Not InAttractMode()
							SoundPlay g_bonusSound
						EndIf	
					EndIf
				Case BONUS_SCORE_STATE
					If CheckTimer( bonus\timer, 100 )
						UpdatePlayerScore( 100 )
						bonus\seconds = bonus\seconds - 1
						UpdateBonusTexture( bonus )
					EndIf	
			End Select	
		Else
			If PlayerAtLocation( bonus\bx, bonus\by ) Or IllusionAtLocation( bonus\bx, bonus\by )
				If bonus\state = BONUS_COUNT_STATE
					Return
				EndIf
			EndIf	
			If bonus\state <> BONUS_SCORE_STATE
				bonus\piece\piece = PUZZLE_PIECE_N
				FreeEntity bonus\piece\entity
			EndIf	
			bonus\piece\bonus = False
			FreeSprite bonus\sprite
			Delete bonus\timer
			Delete bonus
			CheckForPuzzleCompletion()
		EndIf
	Next
End Function

Function FreeBonuses()
	For bonus.Bonus = Each Bonus
		If bonus\sprite <> Null Then FreeSprite bonus\sprite
		Delete bonus\timer
		Delete bonus
	Next
End Function

Function HideBonuses()
	For bonus.Bonus = Each Bonus
		HideSprite bonus\sprite
	Next
End Function

Function UpdateBonusTexture( bonus.Bonus )
	ClearTexture bonus\sprite\texture
	If bonus\colorFlag
		DrawTextureFont g_finalFight16r, 0, 0, bonus\seconds, bonus\sprite\texture
	Else
		DrawTextureFont g_finalFight16b, 0, 0, bonus\seconds, bonus\sprite\texture
	EndIf
	bonus\colorFlag = Not bonus\colorFlag
End Function

Function CreateBonus.Bonus( piece.PuzzlePiece, x, y )
	bonus.Bonus = New Bonus
	bonus\bx = x
	bonus\by = y
	bonus\prevType = piece\piece
	bonus\piece = piece
	bonus\piece\bonus = True
	bonus\seconds = 9
	bonus\state = BONUS_COUNT_STATE
	If bonus\piece\piece = PUZZLE_PIECE_N
		bonus\piece\piece = PUZZLE_PIECE_S
		bonus\piece\entity = CopyEntity( g_pipes[bonus\piece\piece], GetBoardPivot() )
		bonus\piece\rotation = Rand( 0, 1 )
	EndIf	
	PositionPuzzlePiece( bonus\piece, x, y )
	bonus\colorFlag = 0
	bonus\sprite = SpriteCreate( 0, 0, 32, 32, 0, GetBoardPivot() )
	RotateEntity bonus\sprite\entity, 65, 0, 0
	ResizeSpriteW bonus\sprite, 3, 3, 1
	ClearTexture bonus\sprite\texture
	DrawTextureFont g_finalFight16r, 0, 0, bonus\seconds, bonus\sprite\texture
	bonus\sprite\pos\x = EntityX( bonus\piece\entity ) + 2
	bonus\sprite\pos\y = EntityY( bonus\piece\entity )
	bonus\sprite\pos\z = EntityZ( bonus\piece\entity )
	PositionSpriteW bonus\sprite
	bonus\timer = StartTimer( 1000 )
	Return bonus
End Function
