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

Const SPRITE_NO_TEXTURE = -1
Const SPRITE_NOP = 0
Const SPRITE_FADE_IN = 1
Const SPRITE_FADE_OUT = 2
Const SPRITE_BLINK = 3

Const SPRITE_NO_PARENT = -1

Type Sprite
	Field pos.Vertex
	Field rot.Vector
	Field width#
	Field height#
	Field entity
	Field texture
	Field alpha#
	Field animMode
	Field timer.Timer
End Type

Global g_spriteParent
Global g_halfScreenWidth
Global g_halfScreenHeight

Function InitializeSpriteSystem()
	g_spriteParent = g_camera
	g_halfScreenWidth = GraphicsWidth() / 2
	g_halfScreenHeight = GraphicsHeight() / 2
End Function

Function SpriteCreate.Sprite( x#, y#, width#, height#, texture = 0, parent = 0, flags = 5 )
	sprite.Sprite = SpriteCreateSimple( x, y, width, height, parent )
    InitializeSpriteTexture sprite, texture, flags
	Return sprite
End Function

Function SpriteCreateSimple.Sprite( x#, y#, width#, height#, parent = 0 )
    sprite.Sprite = New Sprite
    sprite\pos = New Vertex
    sprite\rot = New Vector
    sprite\pos\x = x
    sprite\pos\y = y
    sprite\pos\z = 1.001
    sprite\width = width
    sprite\height = height
    sprite\alpha = 1
    sprite\animMode = SPRITE_NOP
    If parent = SPRITE_NO_PARENT
        sprite\entity = CreateQuad()
    Else
        If parent = 0
            sprite\entity = CreateQuad( g_spriteParent )
            EntityOrder sprite\entity, -50
        Else
            sprite\entity = CreateQuad( parent )
        EndIf
    EndIf   
    EntityFX sprite\entity, 1 + 16
    EntityAlpha sprite\entity, 1
    ResizeSprite sprite, sprite\width, sprite\height
    PositionSprite sprite
    Return sprite
End Function

Function InitializeSpriteTexture( sprite.Sprite, texture = 0, flags = 5 )
    If texture = 0
        ClearTextureFilters
        sprite\texture = CreateTexture( NearestPowerOf2( sprite\width ), NearestPowerOf2( sprite\height ), flags )
        ClearTexture( sprite\texture )
        EntityTexture sprite\entity, sprite\texture
        TextureFilter "", 1 + 8
    ElseIf texture <> SPRITE_NO_TEXTURE
        sprite\texture = texture
        EntityTexture sprite\entity, sprite\texture
    EndIf
End Function

Function FreeSprite( sprite.Sprite )
	If sprite = Null Then Return
    FreeTexture sprite\texture
	FreeEntity sprite\entity
	Delete sprite\pos
	Delete sprite\rot
    If sprite\timer <> Null
        Delete sprite\timer
    EndIf    
	Delete sprite
End Function

Function SetSpriteParent( sprite.Sprite, parent )
	EntityParent sprite\entity, parent
End Function

Function SetSpriteOrder( sprite.Sprite, order )
	EntityOrder sprite\entity, order
End Function

Function ClearSprite( sprite.Sprite, rgb = 0 )
	ClearTexture sprite\texture, rgb
End Function

Function PositionSprite( sprite.Sprite )
	Local tx#, ty#
	tx =  (sprite\pos\x - g_halfScreenWidth) / g_halfScreenWidth
	ty = -(sprite\pos\y - g_halfScreenHeight) / g_halfScreenWidth
	PositionEntity sprite\entity, tx, ty, sprite\pos\z
End Function

Function PositionSpriteW( sprite.Sprite )
	PositionEntity sprite\entity, sprite\pos\x, sprite\pos\y, sprite\pos\z
End Function

Function ResizeSprite( sprite.Sprite, w# = 0, h# = 0 )
	If w = 0 And h = 0
		w = sprite\width
		h = sprite\height
	EndIf
	ScaleEntity sprite\entity, w/Float(GraphicsWidth()), h/Float(GraphicsWidth()), 1
End Function

Function ResizeSpriteW( sprite.Sprite, x#, y#, z# )
	ScaleEntity sprite\entity, x, y, z
End Function

Function SetSpriteHandle( sprite.Sprite, handlex#, handley# )
	s = GetSurface( sprite\entity, 1 )
	VertexCoords s,0,VertexX(s,0)-handlex,VertexY(s,0)+handley,VertexZ(s,0)
	VertexCoords s,1,VertexX(s,1)-handlex,VertexY(s,1)+handley,VertexZ(s,1)
	VertexCoords s,2,VertexX(s,2)-handlex,VertexY(s,2)+handley,VertexZ(s,2)
	VertexCoords s,3,VertexX(s,3)-handlex,VertexY(s,3)+handley,VertexZ(s,3)
End Function

Function SetSpriteFadeIn( sprite.Sprite )
	sprite\alpha = 0
	sprite\animMode = SPRITE_FADE_IN
	EntityAlpha sprite\entity, sprite\alpha
End Function

Function SetSpriteFadeOut( sprite.Sprite )
	sprite\alpha = 1
	sprite\animMode = SPRITE_FADE_OUT
	EntityAlpha sprite\entity, sprite\alpha
End Function

Function SetSpriteBlink( sprite.Sprite, timer = 1000 )
	sprite\animMode = SPRITE_BLINK
	sprite\timer = StartTimer( timer )
End Function

Function SetSpriteNop( sprite.Sprite )
	sprite\animMode = SPRITE_NOP
	If sprite\timer <> Null
		Delete sprite\timer
	EndIf
End Function

Function HideSprite( sprite.Sprite )
	HideEntity sprite\entity
End Function

Function ShowSprite( sprite.Sprite )
	ShowEntity sprite\entity
End Function

Function UpdateSprites()
	For sprite.Sprite = Each Sprite
		Select sprite\animMode
			Case SPRITE_NOP
				
			Case SPRITE_FADE_IN
				sprite\alpha = sprite\alpha + .05
				EntityAlpha sprite\entity, sprite\alpha
				If sprite\alpha > 1.00
					sprite\animMode = SPRITE_NOP
				EndIf

			Case SPRITE_FADE_OUT
				sprite\alpha = sprite\alpha - .05
				EntityAlpha sprite\entity, sprite\alpha
				If sprite\alpha < 0
					sprite\animMode = SPRITE_NOP
				EndIf

			Case SPRITE_BLINK
				If CheckTimer( sprite\timer )
					sprite\alpha = Not sprite\alpha
					EntityAlpha sprite\entity, sprite\alpha
				EndIf	

		End Select
	Next
End Function
