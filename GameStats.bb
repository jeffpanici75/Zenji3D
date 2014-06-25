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

Global g_statsSprite.Sprite[3]
Global g_pauseSprite.Sprite

Function LoadGameStatsResources( reload = False )
    If reload
        Delete g_pauseSprite
        For i = 0 To 2
            Delete g_statsSprite[i]
        Next
    EndIf
	g_pauseSprite = SpriteCreate( (GraphicsWidth() - 80) / 2, (GraphicsHeight() - 16) / 2, 128, 16 )
	SetSpriteHandle g_pauseSprite, -1, -1
	DrawTextureFont g_finalFight16r, 0, 0, "PAUSED", g_pauseSprite\texture
	HidePauseSprite()
	g_statsSprite[0] = SpriteCreate( 0, GraphicsHeight() - 16, 256, 32 )
	g_statsSprite[1] = SpriteCreate( GraphicsWidth() - 224 , GraphicsHeight() - 16, 256, 32 )
	g_statsSprite[2] = SpriteCreate( (GraphicsWidth() - 160) / 2, GraphicsHeight() - 16, 256, 32 )
	For i=0 To 2
		SetSpriteHandle g_statsSprite[i], -1, -1
	Next
	UpdateTimerLabel()
	UpdateSolvedLabel()
	UpdateHighScore()
	HideStatistics()
End Function

Function ShowPauseSprite()
	ShowSprite g_pauseSprite
	SetSpriteBlink g_pauseSprite
End Function

Function HidePauseSprite()
	HideSprite g_pauseSprite
	SetSpriteNop g_pauseSprite
End Function

Function FadeOutStatistics()
	For i=0 To 2 - (InAttractMode() = True)
		SetSpriteFadeOut g_statsSprite[i]
	Next
End Function

Function FadeInStatistics()
	For i=0 To 2 - (InAttractMode() = True)
		SetSpriteFadeIn g_statsSprite[i]
	Next
End Function

Function HideStatistics()
	For i=0 To 2 - (InAttractMode() = True)
		HideEntity g_statsSprite[i]\entity
	Next	
End Function

Function ShowStatistics()
	For i=0 To 2 - (InAttractMode() = True)
		ShowSprite g_statsSprite[i]
		EntityAlpha g_statsSprite[i]\entity, 1
	Next	
End Function

Function UpdateSolvedLabel()
	ClearTextureRect g_statsSprite[2]\texture, 0, 0, 112, 16
	DrawTextureFont g_finalFight16b, 0, 0, "SOLVED:", g_statsSprite[2]\texture
End Function

Function UpdateSolved()
	ClearTextureRect g_statsSprite[2]\texture, 114, 0, 48, 16
	DrawTextureFont g_finalFight16r, 114, 0, ZeroPadLeft( g_currentLevel - 1, 3 ), g_statsSprite[2]\texture
End Function

Function UpdateHighScore()
	ClearTextureRect g_statsSprite[1]\texture, 0, 0, 96, 16
	DrawTextureFont g_finalFight16r, 0, 0, ZeroPadLeft( g_sessionHighScore, 6 ), g_statsSprite[1]\texture
End Function

Function UpdateFaces()
	ClearTextureRect g_statsSprite[1]\texture, 208, 0, 16, 16
	DrawTextureFont g_finalFight16b, 112, 0, "FACES:", g_statsSprite[1]\texture
	DrawTextureFont g_finalFight16r, 208, 0, g_player\lives - 1, g_statsSprite[1]\texture
End Function

Function UpdateScore()
	ClearTextureRect g_statsSprite[0]\texture, 0, 0, 96, 16
	DrawTextureFont g_finalFight16r, 0, 0, ZeroPadLeft( g_player\score, 6 ), g_statsSprite[0]\texture
End Function

Function UpdateTimerLabel()
	DrawTextureFont g_finalFight16b, 112, 0, "TIME:", g_statsSprite[0]\texture
End Function

Function UpdateTimer()
	ClearTextureRect g_statsSprite[0]\texture, 192, 0, 32, 16 
	DrawTextureFont g_finalFight16r, 192, 0, ZeroPadLeft( g_gameSeconds, 2 ), g_statsSprite[0]\texture
End Function
