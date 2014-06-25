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

Const MENU_START_GAME = 0
Const MENU_SETTINGS = 1
Const MENU_EXIT = 2

Global g_splashSprite.Sprite
Global g_menuScaleX#
Global g_menuScaleY#
Global g_menuBackImage
Global g_restoreImage
Global g_menuItem[3]
Global g_currentMenuItem
Global g_isMainMenuActivated

Function InitializeMainMenu( all = False )
    If g_splashSprite = Null
        Local splashImage = LoadImage( RarExtractFile( "res\bitmaps\splash.png" ) ): UpdateDiskAccessIcon
        g_splashSprite = SpriteCreateSimple( 0, 0, 1024, 768 )
        HideSprite g_splashSprite
        InitializeSpriteTexture g_splashSprite, 0, 1
        UpdateDiskAccessIcon
        SetSpriteHandle g_splashSprite, -1, -1
        ResizeSprite g_splashSprite, GraphicsWidth(), GraphicsWidth()
        UpdateDiskAccessIcon
        CopyRect 0, 0, 1024, 768, 0, 0, ImageBuffer( splashImage ), TextureBuffer( g_splashSprite\texture )
        FreeImage splashImage
        UpdateDiskAccessIcon
    EndIf
    If all
        g_restoreImage = CreateImage( 351, 177 )
        SetBuffer TextureBuffer( g_splashSprite\texture )
        GrabImage g_restoreImage, 16, 549
        SetBuffer BackBuffer()
        g_menuBackImage = LoadImage( RarExtractFile( "res\bitmaps\main-menu.png" ) ): UpdateDiskAccessIcon
        g_menuItem[0] = LoadImage( RarExtractFile( "res\bitmaps\start-game.png" ) ): UpdateDiskAccessIcon
        g_menuItem[1] = LoadImage( RarExtractFile( "res\bitmaps\settings.png" ) ): UpdateDiskAccessIcon
        g_menuItem[2] = LoadImage( RarExtractFile( "res\bitmaps\exit.png" ) ): UpdateDiskAccessIcon
        g_currentMenuItem = 0
        UpdateSplashTexture
    Else
        g_restoreImage = 0
    EndIf 
    g_isMainMenuActivated = -1
    g_menuScaleX = Float( GraphicsWidth() ) / Float( 1024 )
    g_menuScaleY = Float( GraphicsHeight() ) / Float( 768 )
    ShowPointerSprite
End Function

Function FreeMainMenuResources( all = False )
    If all
        FreeSprite g_splashSprite
        If g_restoreImage <> 0
            FreeImage g_restoreImage
        EndIf
    Else    
        If g_restoreImage <> 0
            CopyRect 0, 0, 351, 177, 16, 549, ImageBuffer( g_restoreImage ), TextureBuffer( g_splashSprite\texture )
            UpdateDiskAccessIcon
            FreeImage g_restoreImage
            g_restoreImage = 0
            UpdateDiskAccessIcon
        EndIf
    EndIf    
    FreeImage g_menuBackImage
    For i=0 To 2
        FreeImage g_menuItem[i]
    Next
    HidePointerSprite
    UpdateDiskAccessIcon
End Function

Function StartMainMenu()
    ShowSprite g_splashSprite
End Function

Function UpdateMainMenu()
    If MouseHit( 1 )
        topEdge = Int( Float( 549 ) * g_menuScaleY )
        For i = 0 To 2
            leftEdge = Int( Float( 16 ) * g_menuScaleX )
            width = Int( Float( ImageWidth( g_menuItem[i] ) * g_menuScaleX ) )
            height = Int( Float( ImageHeight( g_menuItem[i] ) * g_menuScaleY ) )
            If RectsOverlap( leftEdge, topEdge, width, height, MouseX(), MouseY(), 2, 2 )
                g_currentMenuItem = i
                g_isMainMenuActivated = i
                UpdateSplashTexture
                Return
            EndIf    
            topEdge = topEdge + Int( Float( ImageHeight( g_menuItem[i] ) ) * g_menuScaleY )
        Next
    EndIf
    If KeyHit( g_inputMap( INPUT_MOVE_UP, KEYBOARD ) ) Or JoyHitUp()
        If g_currentMenuItem > 0
            g_currentMenuItem = g_currentMenuItem - 1
            UpdateSplashTexture
            SoundPlay g_clickSound
        EndIf
    ElseIf KeyHit( g_inputMap( INPUT_MOVE_DOWN, KEYBOARD ) ) Or JoyHitDown()
        If g_currentMenuItem < 2
            g_currentMenuItem = g_currentMenuItem + 1
            UpdateSplashTexture
            SoundPlay g_clickSound
        EndIf
    ElseIf KeyHit( g_inputMap( INPUT_START_BUTTON, KEYBOARD ) ) Or JoyMappedInput( g_inputMap( INPUT_START_BUTTON, JOYPAD ) )
        PerformMainMenuItem
    EndIf
    If g_isMainMenuActivated <> -1
        g_isMainMenuActivated = -1
        PerformMainMenuItem
    EndIf
End Function

Function PerformMainMenuItem()
    Select g_currentMenuItem
        Case MENU_START_GAME
            FreeMainMenuResources True
            SetDifficultyMenuMode
        Case MENU_SETTINGS
            EnableDiskAccessIcon
            FreeMainMenuResources True
            SetConfigSettingsMode
            DisableDiskAccessIcon
        Case MENU_EXIT
            g_endGameFlag = True
    End Select
    SoundPlay g_clickSound
End Function

Function UpdateSplashTexture()
    Local destBuffer = TextureBuffer( g_splashSprite\texture )
    Local y = 549
    CopyRect 0, 0, ImageWidth( g_menuBackImage ), ImageHeight( g_menuBackImage ), 16, 549, ImageBuffer( g_menuBackImage ), destBuffer
    If g_currentMenuItem > 0
        For i = 0 To g_currentMenuItem - 1
            y = y + ImageHeight( g_menuItem[i] )
        Next
    EndIf
    CopyRect 0, 0, ImageWidth( g_menuItem[g_currentMenuItem] ), ImageHeight( g_menuItem[g_currentMenuItem] ) - 1, 16, y, ImageBuffer( g_menuItem[g_currentMenuItem] ), destBuffer
End Function

Function ShowSplashScreen()
    ShowSprite g_splashSprite
End Function

Function HideSplashScreen()
    HideSprite g_splashSprite
End Function
