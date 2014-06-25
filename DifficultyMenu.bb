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

Const MENU_ZEN_STUDENT = 0
Const MENU_ZEN_APPRENTICE = 1
Const MENU_ZEN_MASTER = 2
Const MENU_BACK = 3

Global g_diffMenuSprite.Sprite
Global g_diffBackImage
Global g_diffMenuItem[4]
Global g_diffLeftEdge
Global g_diffTopEdge
Global g_diffPerformAction
Global g_currentDiffItem

Function InitializeDifficultyMenu()
    g_diffLeftEdge = (GraphicsWidth() - 256 ) / 2
    g_diffTopEdge = (GraphicsHeight() - 256 ) / 2
    g_diffMenuSprite = SpriteCreate( g_diffLeftEdge, g_diffTopEdge, 256, 256, 0, 0, 1 )
    SetSpriteHandle g_diffMenuSprite, -1, -1
    UpdateDiskAccessIcon
    g_diffBackImage   = LoadImage( RarExtractFile( "res\bitmaps\pick-difficulty.png" ) ): UpdateDiskAccessIcon
    g_diffMenuItem[0] = LoadImage( RarExtractFile( "res\bitmaps\difficulty-student.png" ) ): UpdateDiskAccessIcon
    g_diffMenuItem[1] = LoadImage( RarExtractFile( "res\bitmaps\difficulty-apprentice.png" ) ): UpdateDiskAccessIcon
    g_diffMenuItem[2] = LoadImage( RarExtractFile( "res\bitmaps\difficulty-master.png" ) ): UpdateDiskAccessIcon
    g_diffMenuItem[3] = LoadImage( RarExtractFile( "res\bitmaps\difficulty-back.png" ) ): UpdateDiskAccessIcon
    g_currentDiffItem = 0
    g_diffPerformAction = -1
    ShowPointerSprite
    UpdateDiffMenuTexture
End Function

Function FreeDifficultyMenuResources()
    FreeSprite g_diffMenuSprite
    FreeImage g_diffBackImage
    For i=0 To 3
        FreeImage g_diffMenuItem[i]
    Next
    HidePointerSprite
End Function

Function UpdateDifficultyMenu()
    Local mx = MouseX()
    Local my = MouseY()
    Local offsety = g_diffTopEdge + 71
    If MouseHit( 1 )
        For i = 0 To 2
            width = ImageWidth( g_diffMenuItem[i] )
            height = ImageHeight( g_diffMenuItem[i] )
            If RectsOverlap( g_diffLeftEdge + 20, offsety, width - 20, height - 8, mx, my, 2, 2 )
                g_currentDiffItem = i
                g_diffPerformAction = i
                UpdateDiffMenuTexture
                Return
            EndIf
            offsety = offsety + height
        Next
        width = ImageWidth( g_diffMenuItem[3] )
        height = ImageHeight( g_diffMenuItem[3] )
        If RectsOverlap( g_diffLeftEdge + 75, g_diffTopEdge + 211, width, height, mx, my, 2, 2 )
            g_currentDiffItem = 3
            g_diffPerformAction = i
            UpdateDiffMenuTexture
            Return
        EndIf
    EndIf    
    If KeyHit( g_inputMap( INPUT_MOVE_UP, KEYBOARD ) ) Or JoyHitUp()
        If g_currentDiffItem > 0
            g_currentDiffItem = g_currentDiffItem - 1
            UpdateDiffMenuTexture
            SoundPlay g_clickSound
        EndIf
    ElseIf KeyHit( g_inputMap( INPUT_MOVE_DOWN, KEYBOARD ) ) Or JoyHitDown()
        If g_currentDiffItem < 3
            g_currentDiffItem = g_currentDiffItem + 1
            UpdateDiffMenuTexture
            SoundPlay g_clickSound
        EndIf
    ElseIf KeyHit( g_inputMap( INPUT_START_BUTTON, KEYBOARD ) ) Or JoyMappedInput( g_inputMap( INPUT_START_BUTTON, JOYPAD ) )
        DiffPerformSelectedItem
    EndIf
    If g_diffPerformAction <> -1
        DiffPerformSelectedItem
    EndIf    
End Function

Function UpdateDiffMenuTexture()
    Local destBuffer = TextureBuffer( g_diffMenuSprite\texture )
    Local x = 0
    Local y = 71
    Local width, height, srcBuffer
    width = ImageWidth( g_diffBackImage )
    height = ImageHeight( g_diffBackImage )
    srcBuffer = ImageBuffer( g_diffBackImage )
    CopyRect 0, 0, width, height, 0, 0, srcBuffer, destBuffer
    If g_currentDiffItem = 0
        x = 0
        y = 71
    ElseIf g_currentDiffItem > 0 And g_currentDiffItem < 3
        For i = 0 To g_currentDiffItem - 1
            y = y + ImageHeight( g_diffMenuItem[i] )
        Next
    Else
        x = 75
        y = 211
    EndIf
    width = ImageWidth( g_diffMenuItem[g_currentDiffItem] )
    height = ImageHeight( g_diffMenuItem[g_currentDiffItem] )
    srcBuffer = ImageBuffer( g_diffMenuItem[g_currentDiffItem] )
    CopyRect 0, 0, width, height, x, y, srcBuffer, destBuffer
End Function

Function DiffPerformSelectedItem()
    If g_currentDiffItem >= MENU_ZEN_STUDENT And g_currentDiffItem <= MENU_ZEN_MASTER
        FreeDifficultyMenuResources
        SetDifficultyLevel g_currentDiffItem + 1
        SetZenKoanMode True, True
    ElseIf g_currentDiffItem = MENU_BACK
        EnableDiskAccessIcon
        FreeDifficultyMenuResources
        SetAttractMode
        DisableDiskAccessIcon
    EndIf    
    SoundPlay g_clickSound
End Function

