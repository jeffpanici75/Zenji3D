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

Global g_flameSprite = 0;
Global g_particleSprite = 0;

Type Flame
	Field ent
	Field ang#
	Field size#
	Field alph#
	Field dis#
	Field dirVec.Vector
End Type

Type Fire
	Field piv
	Field posVec.Vertex
	Field dirVec.Vector
End Type

Type Particle
	Field ent
	Field alpha#
	Field pop
	Field dirVec.Vector
End Type

Function StartFireEffectsSubsystem()
	g_flameSprite = LoadSprite( RarExtractFile( "res\bitmaps\smoke.bmp" ) )
	g_particleSprite = LoadSprite( RarExtractFile( "res\bitmaps\spark.bmp" ) )
	HideEntity g_flameSprite
	HideEntity g_particleSprite
End Function

Function EndFireEffectsSubsystem()
	EraseFires()
	FreeEntity g_particleSprite
	FreeEntity g_flameSprite
End Function

Function AddFlame( parent, size#=1, dis#=.016, dx#=0, dy#=0.3, dz#=0 )
	a.flame = New Flame
	a\ent = CopyEntity( g_flameSprite, parent )
	a\alph = 1
	a\size = size
	a\dis = dis
	a\ang = Rnd(360)
	ScaleSprite a\ent, a\size, a\size
	EntityColor a\ent, Rnd(150,255), Rnd(0,100), 0
	a\dirVec = New Vector
	a\dirVec\x = dx
	a\dirVec\y = dy
	a\dirVec\z = dz
End Function

Function UpdateFlames()
	For a.Flame = Each Flame
		If a\alph > 0.01
			a\alph = a\alph - a\dis
			EntityAlpha a\ent, a\alph
			RotateSprite a\ent, a\ang
			a\ang = a\ang + .2
			MoveEntity a\ent, a\dirVec\x, a\dirVec\y, a\dirVec\z
		Else
			FreeEntity a\ent
			Delete a
		End If
	Next
End Function

Function UpdateFires()
	For a.Fire = Each Fire
		AddFlame( a\piv, Rnd(1,4), .070, a\dirVec\x, a\dirVec\y, a\dirVec\z )
	Next
	UpdateFlames()
	UpdateParticles()
	UpdateParticles()
End Function

Function EraseFires()
	For a.fire = Each Fire
		If a\piv<>0
			FreeEntity a\piv
		EndIf			
	Next
	Delete Each Fire
	Delete Each Flame
	Delete Each Particle
End Function

Function AddFire.Fire( x#, y#, z#, dx#=0, dy#=.23, dz#=0 )
	a.Fire = New Fire
	a\piv = CreatePivot()
	PositionEntity a\piv, x, y, z
	a\posVec = New Vertex
	a\dirVec = New Vector
	a\posVec\x = x
	a\posVec\y = y
	a\posVec\z = z
	a\dirVec\x = dx
	a\dirVec\y = dy
	a\dirVec\z = dz
	Return a
End Function

Function AddParticle( parent, r=255, g=255, b=255 )
	a.Particle = New Particle
	a\ent = CopyEntity( g_particleSprite, parent )
	a\dirVec = New Vector
	a\dirVec\x = Rnd(-.1,.1)
	a\dirVec\y = Rnd(0.1,.7)
	a\dirVec\z = Rnd(-.1,.1)
	ScaleSprite a\ent, Rnd(.4,.7), Rnd(.4,.7)
	a\alpha = 1
	a\pop = False
	EntityColor a\ent, r, g, b
End Function

Function UpdateParticles()
	For a.Particle = Each Particle
		MoveEntity a\ent, a\dirVec\x, a\dirVec\y, a\dirVec\z
		If EntityY( a\ent ) < .3
			a\dirVec\y = -a\dirVec\y
			a\dirVec\y = a\dirVec\y * .62
			a\pop = True
		End If
		a\dirVec\y = a\dirVec\y - .02
		If a\pop 
			a\alpha = a\alpha - .02
			EntityAlpha a\ent, a\alpha
			If a\alpha < 0.05
				FreeEntity a\ent
				Delete a
			End If
		End If
	Next
End Function
