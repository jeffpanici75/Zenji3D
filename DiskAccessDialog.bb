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

Global g_showDiskAccessIcon
Global g_diskAccessTexture
Global g_diskSprite.Sprite
Global g_currentIconFrame

Function InitializeDiskAccessIcon()
    g_currentIconFrame = 0
    g_diskAccessTexture = LoadAnimTextureNoFilters( RarExtractFile( "res\bitmaps\disk-access.png" ), 1, 128, 128, 0, 8 )
    g_diskSprite = SpriteCreate( (GraphicsWidth() - 128) / 2, (GraphicsHeight() - 128) / 2, 128, 128, SPRITE_NO_TEXTURE )
    SetSpriteHandle g_diskSprite, -1, -1
    SetSpriteOrder g_diskSprite, -75
    SetCurrentFrame
    DisableDiskAccessIcon
End Function

Function EnableDiskAccessIcon()
    g_showDiskAccessIcon = True
    ShowSprite g_diskSprite
End Function

Function DisableDiskAccessIcon()
    g_showDiskAccessIcon = False
    HideSprite g_diskSprite
End Function

Function UpdateDiskAccessIcon()
    If g_showDiskAccessIcon
        SetCurrentFrame
        g_currentIconFrame = g_currentIconFrame + 1
        If g_currentIconFrame > 7
            g_currentIconFrame = 0
        EndIf
        UpdateWorld
        RenderWorld
        Flip False
    EndIf    
End Function

Function SetCurrentFrame()
    EntityTexture g_diskSprite\entity, g_diskAccessTexture, g_currentIconFrame
End Function
