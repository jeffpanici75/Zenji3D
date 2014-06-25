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

Global g_mouseSprite.Sprite
Global g_isPointerActive

Function InitializePointerSprite( reload = False )
    Local temp = LoadTextureNoFilters( RarExtractFile( "res\bitmaps\pointer.png" ) )
    If reload Then Delete g_mouseSprite
    g_mouseSprite = SpriteCreate( 320, 240, 32, 32, temp )
    SetSpriteHandle g_mouseSprite, -1, -1
    SetSpriteOrder g_mouseSprite, -100
    HidePointerSprite
End Function

Function SetPointerPosition( x, y )
    If Not g_isPointerActive Return
    If x <> g_mouseSprite\pos\x Or y <> g_mouseSprite\pos\y
        g_mouseSprite\pos\x = x
        g_mouseSprite\pos\y = y
        PositionSprite g_mouseSprite
    EndIf    
End Function

Function HidePointerSprite()
    HideSprite g_mouseSprite
    g_isPointerActive = False
End Function

Function ShowPointerSprite()
    ShowSprite g_mouseSprite
    g_isPointerActive = True
End Function
