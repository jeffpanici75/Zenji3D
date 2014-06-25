;---------------------------------------------------------------------
; Zenji3D
; Copyright (C) 2007, 2002 Worker Bee Solutions, Inc.
; All Rights Reserved.
; Written by Jeffrey D. Panici
;
; Original version Copyright (C) 1983 Activision, Inc.
; All Rights Reserved.
;---------------------------------------------------------------------

Const STANDARD_CHARSET$    = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!£$%^&*()_+-=;'#:@~,./<>?\|"
Const FINAL_FIGHT_CHARSET$ = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789:.!?^&*$"

Type BitmapFont
	Field charset$
	Field image
	Field numFrames
	Field charWidth
	Field charHeight
	Field spaceAdjust
	Field tempImage
End Type

Function LoadBitmapFont.BitmapFont( fileName$, charset$=STANDARD_CHARSET, spaceAdjust=0 )
	tempImage = LoadImage( fileName )
	font.BitmapFont = New BitmapFont
	font\charset = charset
	font\charWidth = ImageWidth( tempImage ) / Len( font\charset )
	font\charHeight = ImageHeight( tempImage )
	font\numFrames = ImageWidth( tempImage ) / font\charWidth
	font\spaceAdjust = spaceAdjust
	FreeImage tempImage
	font\image = LoadAnimImage( fileName, font\charWidth, font\charHeight, 0, font\numFrames )
	font\tempImage = CreateImage( font\charWidth, font\charHeight )
	MaskImage font\image, 255, 0, 255
	MaskImage font\tempImage, 255, 0, 255
	Return font
End Function

Function FreeBitmapFont( font.BitmapFont )
	FreeImage font\image
	FreeImage font\tempImage
	Delete font
End Function

Function CentreBitmapFont( font.BitmapFont, y, s$ )
	DrawBitmapFont( font, (GraphicsWidth() - ((Len( s ) - 1) * font\charWidth)) / 2, y, s )
End Function

Function CentreTextureFont( font.BitmapFont, y, s$, texture )
	DrawTextureFont( font, (GraphicsWidth() - ((Len( s ) - 1) * font\charWidth)) / 2, y, s, texture )
End Function

Function DrawBitmapFont( font.BitmapFont, x, y, s$ )
	For i = 0 To Len( s ) - 1
		frame = Instr( font\charset, Mid( s, i + 1, 1 ), 1 )
		If frame > 0 Then
			DrawImage font\image, x + ((font\charWidth - font\spaceAdjust) * i), y, (frame - 1)
		EndIf
	Next
End Function

Function DrawTextureFont( font.BitmapFont, x, y, s$, texture )
	Local maskColor = GetMaskColor()
	For i = 0 To Len( s ) - 1
		frame = Instr( font\charset, Mid( s, i + 1, 1 ), 1 )
		If frame > 0 Then
			offset = x + (font\charWidth * i)
			CopyTextureRectS 0, 0, font\charWidth, font\charHeight, offset, y, ImageBuffer( font\image, (frame - 1) ), TextureBuffer( texture ), maskColor
		EndIf
	Next
End Function
