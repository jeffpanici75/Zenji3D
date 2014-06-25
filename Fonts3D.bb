;---------------------------------------------------------------------
; Zenji3D
; Copyright (C) 2007, 2002 Worker Bee Solutions, Inc.
; All Rights Reserved.
; Written by Jeffrey D. Panici
;
; Original version Copyright (C) 1983 Activision, Inc.
; All Rights Reserved.
;---------------------------------------------------------------------

Const TEXT_ANIM_FADE_OUT = 1
Const TEXT_ANIM_FADE_IN = 2
Const TEXT_ANIM_EXPLODE = 3

.letter_data
Data "A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"
Data "a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"
Data "!","?",",","@",".","(",")","£","$","&",":",";"
Data "/","\","%","-","+","#","*","^","=","_","'",">","<","{","}","[","]","~","`","¬","|"
Data "0","1","2","3","4","5","6","7","8","9"
Data "a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"
Data "a2","b2","c2","d2","e2","f2","g2","h2","i2","j2","k2","l2","m2","n2","o2","p2","q2","r2","s2","t2","u2"
Data "v2","w2","x2","y2","z2"
Data "PUNC1","PUNC2","PUNC3","PUNC4","PUNC5","PUNC6","PUNC7","PUNC8","PUNC9","PUNC10","PUNC11","PUNC12"
Data "PUNC13","PUNC14","PUNC15","PUNC16","PUNC17","PUNC18","PUNC19","PUNC20","PUNC21","PUNC22","PUNC23"
Data "PUNC24","PUNC25","PUNC26","PUNC27","PUNC28","PUNC29","PUNC30","PUNC31","PUNC32","PUNC33"
Data "0","1","2","3","4","5","6","7","8","9"

Type Text3D
	Field entity
	Field pos.Vertex
	Field char$
	Field group
	Field part
	Field dirVec.Vector
	Field rotVec.Vector
	Field rotFac.Vector
	Field alpha#
	Field isAnimating
	Field animTimer#
	Field animType
	Field animStartTime
	Field animFrames
End Type

Type Letter3D
	Field char$
	Field modelFile$
	Field entity
End Type

Type BitmapFont
	Field charset$
	Field image
	Field numFrames
	Field charWidth
	Field charHeight
	Field spaceAdjust
	Field tempImage
End Type

Global g_groupNumber = 1
Global g_partNumber = 1
Global g_tinFoilTexture
Global g_skyTexture
Global g_lightSkyTexture

Function Initialize3DFontSystem()
	Restore letter_data
	For i = 1 To 95
		Read char$
		letter.Letter3D = New Letter3D
		letter\char = char
	Next
	For letter.Letter3D = Each Letter3D
		Read modelFile$
		letter\modelFile = "res\font\" + modelFile + ".3DS"
		letter\entity = LoadMesh( letter\modelFile )
		HideEntity letter\entity
	Next
	g_tinFoilTexture = LoadTexture( RarExtractFile( "res\bitmaps\tin-foil.bmp" ) )
	g_skyTexture = LoadTexture( RarExtractFile( "res\bitmaps\sky.bmp" ) )
	g_lightSkyTexture = LoadTexture( RarExtractFile( "res\bitmaps\light-sky.bmp" ) )
End Function

Function Shutdown3DFontSystem()
	For letter.Letter3D = Each Letter3D
		FreeEntity letter\entity
		Delete letter
	Next
End Function

Function Text3D( text$, x#, y#, z#, texture, spacer#, shine#=1, alpha#=1, size#=1, rx#=1.0, ry#=1.0, rz#=1.0 )
	Local isLower
	Local spaceCount

	If x = 9999 Then
		spaceCount = 0
		For i = 1 To Len( text )
			thisChar$ = Mid( text, i, 1 )
			If Asc( thisChar ) >=97 And Asc( thisChar ) =< 122
				spaceCount = spaceCount + ( spacer - 1 )
			Else 
				spaceCount = spaceCount + spacer
			EndIf
		Next
		x = ( spaceCount / 2 ) * -1
	EndIf
	
	For i = 1 To Len( text )
		char.Text3D = New Text3D
		char\char = Mid( text, i, 1 )
		If Asc( char\char ) >=97 And Asc( char\char ) <= 122
			isLower = True
		Else
			isLower = False
		EndIf
		For letter.Letter3D = Each Letter3D
			If char\char = letter\char
				char\entity = CopyEntity( letter\entity )
				Exit
			EndIf	
		Next
		If char\entity <> 0
			If texture <> 0 
				EntityTexture char\entity, texture
			EndIf	
			char\pos = New Vertex
			char\dirVec = New Vector
			char\rotVec = New Vector
			char\rotFac = New Vector
			char\rotVec\x = rx
			char\rotVec\y = ry
			char\rotVec\z = rz
			char\pos\x = x
			char\pos\y = y
			char\pos\z = z
			char\group = g_groupNumber
			char\part = g_partNumber
			char\alpha = alpha
			EntityAlpha char\entity, alpha
			EntityShininess char\entity, shine
			ScaleEntity char\entity, size, size, size
			PositionEntity char\entity, x, y, z
			RotateEntity char\entity, rx, ry, rz
			ShowEntity char\entity
			g_partNumber = g_partNumber + 1
		EndIf	
		If isLower Then
			x = x + spacer
		Else
			x = x + (spacer - 1)
		EndIf
		If x > 150 
			y = y - 5
		EndIf	
		If x > 150 
			x = -150
		EndIf	
	Next
	g_groupNumber = g_groupNumber + 1
	ResetPartNumber()
End Function

Function FreeUnusedText()
	For char.Text3D = Each Text3D
		If char\entity <> 0
			FreeEntity char\entity
		EndIf	
		Delete char\pos
		Delete char\dirVec
		Delete char\rotVec
		Delete char\rotFac
		Delete char
	Next
	ResetPartNumber()
	ResetGroupNumber()
End Function

Function ExplodeText3D( group, part, timer# )
	SeedRnd Millisecs()
	For char.Text3D = Each Text3D
		If char\group = group Or group = 0
			char\isAnimating = True
			char\animTimer = timer
			char\animType = TEXT_ANIM_EXPLODE
			char\animStartTime = Millisecs()
			char\animFrames = 120
			char\dirVec\x = Rnd(-2,2)
			char\dirVec\y = Rnd(-2,2)
			char\dirVec\z = Rnd(-2,2)
			char\rotFac\x = Rnd(-10,10)
			char\rotFac\y = Rnd(-10,10)
			char\rotFac\z = Rnd(-10,10)
		End If
	Next
End Function

Function FadeInText3D( group, part, timer# )
	For char.Text3D = Each Text3D
		If( char\group = group And char\part = part ) Or (char\group = group And part = 0) Or (group = 0 And part = 0)
			char\isAnimating = True
			char\alpha = 0
			char\animTimer = timer
			char\animType = TEXT_ANIM_FADE_IN
			char\animStartTime = Millisecs()
			char\animFrames = 60
		End If
	Next
End Function

Function FadeOutText3D( group, part, timer# )
	For char.Text3D = Each Text3D
		If( char\group = group And char\part = part ) Or (char\group = group And part = 0) Or (group = 0 And part = 0)
			char\isAnimating = True
			char\alpha = 1
			char\animTimer = timer
			char\animType = TEXT_ANIM_FADE_OUT
			char\animStartTime = Millisecs()
			char\animFrames = 60
		End If
	Next
End Function

Function UpdateText3D()
	For char.Text3D = Each Text3D
		If char\isAnimating And char\entity <> 0
			Select char\animType
				Case TEXT_ANIM_FADE_IN
					If char\animStartTime + char\animTimer < Millisecs()
						char\animStartTime = char\animStartTime + char\animTimer
						char\alpha = char\alpha + .05
						If char\alpha > 1.00
							EntityAlpha char\entity, 1
							char\isAnimating = False
							char\animFrames = 0
						Else
							EntityAlpha char\entity, char\alpha
						EndIf	
					EndIf

				Case TEXT_ANIM_FADE_OUT
					If char\animStartTime + char\animTimer < Millisecs()
						char\animStartTime = char\animStartTime + char\animTimer
						char\alpha = char\alpha - .05
						If char\alpha < 0
							EntityAlpha char\entity, 0.00000
							char\isAnimating = False
							char\animFrames = 0
						Else
							EntityAlpha char\entity, char\alpha
						EndIf	
					EndIf


				Case TEXT_ANIM_EXPLODE
					If char\animStartTime + char\animTimer < Millisecs()
						char\animFrames = char\animFrames - 1
						If char\animFrames <= 0 
							char\isAnimating = False
						Else	
							char\animStartTime = char\animStartTime + char\animTimer
							TranslateEntity char\entity, char\dirVec\x, char\dirVec\y, char\dirVec\z
							char\pos\x = char\pos\x + char\dirVec\x
							char\pos\y = char\pos\y + char\dirVec\y
							char\pos\z = char\pos\z + char\dirVec\z
							char\rotVec\x = char\rotVec\x + char\rotFac\x
							char\rotVec\y = char\rotVec\y + char\rotFac\y
							char\rotVec\z = char\rotVec\z + char\rotFac\z
							RotateEntity char\entity, char\rotVec\x, char\rotVec\y, char\rotVec\z
						EndIf	
					EndIf

			End Select
		EndIf		
	Next
End Function

Function ResetGroupNumber()
	g_groupNumber = 1
End Function

Function ResetPartNumber()
	g_partNumber = 1
End Function

Function StarWarsText3D( group, zStart, yUp, scrollSpeed#, scrollTime# )
	;AnimateText3D( group,0,0,0,zStart,35,0,0,0.5,0,0 )
	;AnimateText3D( group,0,0,yUp,scrollSpeed,0,0,0,scrollTime,0,1 )
End Function

;---
;
; JDP - Refactor this code, it sucks.
;
;---
;Function AnimateText3D( group, part, x#, y#, z#, dx#, dy#, dz#, timer#, stagger, alpha# )
;	Select stagger
;		Case 0
;			FIRSTX=999999
;			LASTX=999999
;			YPLACE=999999
;			ZPLACE=999999
;			THISNEWLINE=0
;			For char.Text3D = Each Text3D
;				If char\group = group Then 
;					If FIRSTX=999999 Then
;						FIRSTX = char\pos\x
;						YPLACE = char\pos\y
;						ZPLACE = char\pos\z
;					EndIf
;					If char\pos\y <> YPLACE And THISNEWLINE = 0 Then
;						LASTX = UPDX
;						THISNEWLINE = 1
;					EndIf
;					UPDX = char\pos\x
;				EndIf
;			Next
;			If UPDX = FIRSTX Then LASTX = UPDX
;			If LASTX = 999999 Then LASTX = UPDX
;			DHTEXTMIDDLE = ( ( FIRSTX + LASTX ) / 2 )
;			letterPivot = CreatePivot()
;			PositionEntity letterPivot, DHTEXTMIDDLE, YPLACE, ZPLACE
;			For char.Text3D = Each Text3D
;				If char\group = group Then
;					EntityParent char\entity, letterPivot
;					EntityAlpha char\entity, alpha
;				EndIf
;			Next
;			timeFactor# = ( timer * 1000 ) / 25
;			MOVETHISXINC# = MOVEXPOS# / timeFactor
;			MOVETHISYINC# = MOVEYPOS# / timeFactor
;			MOVETHISZINC# = MOVEZPOS# / timeFactor
;			SPININCX# = dx / timeFactor
;			SPININCY# = dy / timeFactor
;			SPININCZ# = dz / timeFactor
;			DHTIME = Millisecs()
;			DHTIME = DHTIME + 25
;			SPINYX# = SPININCX#
;			SPINYY# = SPININCY#
;			SPINYZ# = SPININCZ#
;			For i = 1 To timeFactor
;				TranslateEntity letterPivot, MOVETHISXINC#, MOVETHISYINC#, MOVETHISZINC#
;				RotateEntity letterPivot, SPINYX#, SPINYY#, SPINYZ#
;				SPINYX# = SPINYX# + SPININCX#
;				SPINYY# = SPINYY# + SPININCY#
;				SPINYZ# = SPINYZ# + SPININCZ#
;				While True
;					If DHTIME < Millisecs() Then Exit
;				Wend
;				DHTIME = DHTIME + 25
;				UpdateWorld
;				RenderWorld
;				Flip
;			Next
;			For char.Text3D = Each Text3D
;				If char\group = group Then
;					EntityParent char\entity, 0
;				EndIf
;			Next
;			FreeEntity letterPivot
;
;		Case 1
;			For char.Text3D = Each Text3D
;				If char\group = group Then 
;					timeFactor = (timer * 1000) / 25
;					MOVETHISXINC# = MOVEXPOS# / timeFactor
;					MOVETHISYINC# = MOVEYPOS# / timeFactor
;					MOVETHISZINC# = MOVEZPOS# / timeFactor
;					SPININCX# = dx / timeFactor
;					SPININCY# = dy / timeFactor
;					SPININCZ# = dz / timeFactor
;					DHTIME = Millisecs()
;					DHTIME = DHTIME + 25
;					SPINYX# = SPININCX#
;					SPINYY# = SPININCY#
;					SPINYZ# = SPININCZ#
;					EntityAlpha char\entity, alpha
;					For i = 1 To timeFactor
;						TranslateEntity char\entity, MOVETHISXINC#, MOVETHISYINC#, MOVETHISZINC#
;						RotateEntity char\entity, SPINYX#, SPINYY#, SPINYZ#
;						SPINYX# = SPINYX# + SPININCX#
;						SPINYY# = SPINYY# + SPININCY#
;						SPINYZ# = SPINYZ# + SPININCZ#
;						While True
;							If DHTIME < Millisecs() Then Exit
;						Wend
;						DHTIME = DHTIME + 25
;						UpdateWorld
;						RenderWorld
;						Flip
;					Next
;				EndIf
;			Next
;
;		Case 2
;			revCounter = 0
;			For char.Text3D = Each Text3D
;				revCounter = revCounter + 1
;			Next
;			char.Text3D = Last Text3D
;			While revCounter > 0 
;				If char\group = group Then 
;					timeFactor = (timer * 1000) / 25
;					MOVETHISZINC# = MOVEZPOS# / timeFactor
;					SPININCX# = dx / timeFactor
;					SPININCY# = dy / timeFactor
;					SPININCZ# = dz / timeFactor
;					DHTIME = Millisecs()
;					DHTIME = DHTIME + 25
;					SPINYX# = SPININCX#
;					SPINYY# = SPININCY#
;					SPINYZ# = SPININCZ#
;					EntityAlpha char\entity, alpha#
;					For i = 1 To timeFactor
;						TranslateEntity char\entity, MOVETHISXINC#, MOVETHISYINC#, MOVETHISZINC#
;						RotateEntity char\entity, SPINYX#, SPINYY#, SPINYZ#
;						SPINYX# = SPINYX# + SPININCX#
;						SPINYY# = SPINYY# + SPININCY#
;						SPINYZ# = SPINYZ# + SPININCZ#
;						While True
;							If DHTIME < Millisecs() Then Exit
;						Wend
;						DHTIME = DHTIME + 25
;						UpdateWorld
;						RenderWorld
;						Flip
;					Next
;				EndIf
;				revCounter = revCounter - 1
;				char.Text3D = Before char
;			Wend
;			
;		Default 
;
;	End Select 
;End Function

