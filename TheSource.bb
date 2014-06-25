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

Type Source
	Field pos.Vertex
	Field bx
	Field by
	Field entity
End Type

Global g_energySprite
Global g_theSource.Source
Global g_spriteScale#
Global g_scaleDelta#

Function InitializeTheSource()
	g_energySprite = LoadSprite( RarExtractFile( "res\bitmaps\energy-green.jpg" ) )
	HideEntity g_energySprite
	g_spriteScale = 8
	g_scaleDelta = -.13
End Function

Function SetTheSource( x, y )
	g_spriteScale = 8
	g_scaleDelta = -.13
	g_theSource = New Source
	g_theSource\bx = x
	g_theSource\by = y
	g_theSource\entity = CopyEntity( g_energySprite )
	g_theSource\pos = GetPlatformVertex( g_theSource\bx, g_theSource\by )
	g_theSource\pos\y = g_theSource\pos\y + 3
	EntityParent g_theSource\entity, GetBoardPivot()
	EntityAlpha g_theSource\entity, .8
	ScaleSprite g_theSource\entity, g_spriteScale, g_spriteScale
	PositionEntity g_theSource\entity, g_theSource\pos\x, g_theSource\pos\y, g_theSource\pos\z
End Function

Function UpdateTheSource()
	g_spriteScale = g_spriteScale + g_scaleDelta
	If g_spriteScale < 2 Or g_spriteScale > 8
		g_scaleDelta = -g_scaleDelta
	EndIf
	ScaleSprite g_theSource\entity, g_spriteScale, g_spriteScale
End Function

Function FreeTheSource()
	FreeEntity g_theSource\entity
	Delete g_theSource
End Function
