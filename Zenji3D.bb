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

Include "Keys.bb"
Include "Debug.bb"
Include "UnRar.bb"
Include "Maths.bb"
Include "Input.bb"
Include "Timer.bb"
Include "Water.bb"
Include "Bonus.bb"
Include "Logos.bb"
Include "Mouse.bb"
Include "String.bb"
Include "Camera.bb"
Include "Puzzle.bb"
Include "Player.bb"
Include "Sounds.bb"
Include "Sprites.bb"
Include "Fonts2D.bb"
Include "ZenKoans.bb"
Include "MainMenu.bb"
Include "Settings.bb"
Include "Graphics.bb"
Include "GameOver.bb"
Include "GameBoard.bb"
Include "GameAward.bb"
Include "GameStats.bb"
Include "TheSource.bb"
Include "Illusions.bb"
Include "Explosions.bb"
Include "HighScores.bb"
Include "FireEffects.bb"
Include "CommandLine.bb"
Include "SettingsDialog.bb"
Include "DifficultyMenu.bb"
Include "AttractSequence.bb"
Include "LightningEffect.bb"
Include "DiskAccessDialog.bb"

Const FPS = 30
Const ATTRACT_MODE = 1
Const GAME_MODE = 2
Const GAME_AWARD_MODE = 3
Const GAME_OVER_MODE = 4
Const GAME_PLAYER_DIE_MODE = 5
Const GAME_MAIN_MENU_MODE = 6
Const GAME_DIFFICULTY_MENU_MODE = 7
Const GAME_ZEN_KOAN_MODE = 8
Const CONFIG_SETTINGS_MODE = 9

#ifdef DEMO_BUILD
    Global g_nagImage
#endif    
Global g_introSound.Sound
Global g_pauseSound.Sound
Global g_clickSound.Sound
Global g_finalFight8r.BitmapFont
Global g_finalFight16r.BitmapFont
Global g_finalFight16b.BitmapFont
Global g_themeChannel
Global g_winChannel
Global g_gameState
Global g_snapshotNumber = 1
Global g_gamePaused = False
Global g_mirrorFrameCount = 0
Global g_mirrorFrameLimit = 15
Global g_fpsShow
Global g_fpsCount
Global g_fpsStable = 60
Global g_fpsTimer.Timer = StartTimer( 1000 )
Global g_framePeriod = 1000 / FPS
Global g_frameTime = 0
Global g_frameTimer = CreateTimer( 60 )
Global g_endGameFlag = False
Global g_difficultyLevel = LEVEL_EASY
Global g_realTimeMirrorSetting.Setting
Global g_useDirectInputSetting.Setting
Global g_antiAliasSetting.Setting

AppTitle "Zenji 3D"
ParseCommandLine
If GetOptionValue( "-debug" ) = "True"
    OpenDebugFile
EndIf   
g_fpsShow = (GetOptionValue( "-fps" ) = "True")

LoadSettings
g_antiAliasSetting = GetSetting( "AntiAlias" )
g_useDirectInputSetting = GetSetting( "UseDirectInput" )
g_realTimeMirrorSetting = GetSetting( "RealTimeMirror" )

RarInitialize
RarSetPassword "FaTboY"
RarSetArchiveFile "Zenji3D.pak"
RarCatalogArchive
EnumerateGraphicModes
SetGraphicsMode

SetBuffer BackBuffer()
Color 255, 255, 255
Cls
HidePointer

;--------------------------
; Run company logos 
;--------------------------
g_introSound = SoundLoad( RarExtractFile( "res\sounds\zenji-intro.wav" ) )
g_introSound\userFlag = SOUND_TYPE_MUSIC
DisplayCompanyLogos

;--------------------------
; Load all of the resources
;--------------------------
InitializeZenji3D
UpdateSystemSettings

;--------------------------
; Main-loop, FSM based
;--------------------------
ResetTimer g_fpsTimer
ResetFrameTime
Repeat
	Repeat
		frameElapsed = MilliSecs () - g_frameTime
	Until frameElapsed
	frameTicks = frameElapsed / g_framePeriod
	frameTween# = Float( frameElapsed Mod g_framePeriod ) / Float( g_framePeriod )
	For frameLimit = 1 To frameTicks
		If frameLimit = frameTicks Then CaptureWorld
		g_frameTime = g_frameTime + g_framePeriod
		UpdateGame()
		UpdateWorld
	Next
	If g_gameState = GAME_MODE
		CheckGameCollisions()
        If g_realTimeMirrorSetting\value = "True"
		    g_mirrorFrameCount = g_mirrorFrameCount + 1
		    If g_mirrorFrameCount > g_mirrorFrameLimit
		    	UpdateMirrorTexture
		    	g_mirrorFrameCount = 0
		    EndIf
        EndIf    
	EndIf
    If KeyHit( KEY_ESCAPE )
        If g_gameState = ATTRACT_MODE 
            g_endGameFlag = True
        ElseIf g_gameState >= GAME_MODE And g_gameState <= GAME_PLAYER_DIE_MODE
            FlushKeys
            FlushJoy
            EnableDiskAccessIcon
            CameraOverWater
            SoundStopAll
            RemoveIllusions
            FreeLevelResources
            FreePlayerResources
            SetAttractMode
            DisableDiskAccessIcon
        ElseIf g_gameState = GAME_MAIN_MENU_MODE
            g_gameEndFlag = True
        ElseIf g_gameState = GAME_DIFFICULTY_MENU_MODE
        ElseIf g_gameState = CONFIG_SETTINGS_MODE
            EnableDiskAccessIcon
            FreeSettingsDialogResources
            SetAttractMode
            DisableDiskAccessIcon
        EndIf
    ElseIf KeyHit( KEY_F12 )
        SaveBuffer BackBuffer(), "screenshots\zenji" + g_snapshotNumber + ".bmp" 
        g_snapshotNumber = g_snapshotNumber + 1
    EndIf
    If g_gameState = GAME_MODE
        If KeyHit( g_inputMap( INPUT_PAUSE_BUTTON, KEYBOARD ) ) Or JoyMappedInput( g_inputMap( INPUT_PAUSE_BUTTON, JOYPAD ) )
            ToggleGamePaused
        EndIf    
    EndIf
    If g_endGameFlag Then Exit
	RenderWorld frameTween
    g_fpsCount = g_fpsCount + 1
	If CheckTimer( g_fpsTimer )
		g_fpsStable = g_fpsCount
        g_fpsCount = 0
        If g_realTimeMirror Then g_mirrorFrameLimit = (g_fpsStable / 4) - 1
	EndIf	
	If g_fpsShow 
        DrawBitmapFont g_finalFight16r, 5, 5, "T:" + TrisRendered() + " F:" + g_fpsStable + " VM:" +(TotalVidMem()-AvailVidMem())
    EndIf
	Flip False
    WaitTimer g_frameTimer
Forever    

Function ResetFrameTime()
    g_frameTime = MilliSecs() - g_framePeriod
End Function

Function IncrementFrameTime()
    g_frameTime = g_frameTime + g_framePeriod
End Function

;------------------------
; Shutdown and cleanup
;------------------------
ShutdownAttractSequence
ShutdownIllusions
SoundFreeAll
FreeSprite g_mouseSprite
#ifdef DEMO_BUILD
    ShowNagScreen
#endif    
RarRemoveFiles
RarShutdown
If GetOptionValue( "-debug" ) = "True"
	CloseDebugFile()
EndIf	
End

;------------------------
; FSM Implementation
;------------------------
Function InAttractMode()
    Return (g_gameState = ATTRACT_MODE)
End Function

Function SetDifficultyLevel( level )
    g_difficultyLevel = level
End Function

Function SetAttractMode( subState = ATTRACT_SPLASH_STATE )
	g_gameState = ATTRACT_MODE
    InitializeMainMenu False
	LoadAttractResources
    SetVolumeLevels
    ResetFrameTime
	StartAttractSequence( subState )
End Function

Function SetConfigSettingsMode()
    g_gameState = CONFIG_SETTINGS_MODE
    StartSettingsDialog
End Function

Function SetMainMenuMode()
    g_gameState = GAME_MAIN_MENU_MODE
    InitializeMainMenu True
    StartMainMenu
End Function

Function SetDifficultyMenuMode()
    g_gameState = GAME_DIFFICULTY_MENU_MODE
    InitializeDifficultyMenu
End Function

Function SetGameMode( flag, newGame = False )
	g_gameState = GAME_MODE
	If newGame 
		g_currentLevel = 1
		InitializePlayer
		warp$ = GetOptionValue( "-level" )
		If warp <> ""
			g_currentLevel = Int( warp )
		EndIf
	EndIf
    If flag
        LoadPuzzles g_difficultyLevel
        PrepareCurrentLevel
        SetVolumeLevels
    EndIf
End Function

Function SetPlayerDiesMode()
	g_gameState = GAME_PLAYER_DIE_MODE
	StartPlayerDeathSequence
    SetVolumeLevels
End Function

Function SetGameAwardMode()
	g_gameState = GAME_AWARD_MODE
	StartAwardSequence
    SetVolumeLevels
End Function

Function SetGameOverMode()
	g_gameState = GAME_OVER_MODE
	StartGameOverSequence
    SetVolumeLevels
End Function

Function SetZenKoanMode( flag, newGame = False )
    showKoans.Setting = GetSetting( "ShowZenKoans" )
    If showKoans\value = "True"
        g_gameState = GAME_ZEN_KOAN_MODE
        StartZenKoan flag, newGame
    Else
        SetGameMode flag, newGame
    EndIf    
End Function

Function ToggleGamePaused()
    Local musicVolume.Setting = GetSetting( "MusicVolume" )
    Local soundVolume.Setting = GetSetting( "SoundVolume" )
    Local mVol# = Float( Int( musicVolume\value ) ) / 100
    Local sVol# = Float( Int( soundVolume\value ) ) / 100
	g_gamePaused = Not g_gamePaused
	If g_gamePaused
		CameraOverWater()
		FadeOutStatistics()
		SoundFadeOut g_levelMusic[g_currentLevelMusic]
		ShowPauseSprite()
		SoundPlay g_pauseSound
	Else
		ResetCamera()
		FadeInStatistics()
		SoundFadeIn g_levelMusic[g_currentLevelMusic], mVol
		HidePauseSprite()
		SoundPlay g_pauseSound
	EndIf
End Function

Function UpdateGame()
	UpdateSounds()
    SetPointerPosition MouseX(), MouseY()
	Select g_gameState
		Case ATTRACT_MODE
			UpdateWater()
			UpdateAttractSequence()
			UpdateSprites()

		Case GAME_MODE
			UpdateWater()
			UpdateMirrorPlane()
			UpdateSprites()
			If Not g_gamePaused
                UpdateCamera()
				HandlePlayerInput()
				UpdateIllusions()
				UpdatePlayer()
				UpdatePipes()
				UpdateBonuses()
				UpdateTheSource()
				UpdateGameTimer()
			EndIf	
            
		Case GAME_AWARD_MODE
			UpdateWater()
			UpdateMirrorPlane()
			UpdateAwardSequence()
			UpdateCamera()
			
		Case GAME_PLAYER_DIE_MODE	
			UpdateWater()
			UpdateMirrorPlane()
			UpdatePlayerDeathSequence()
			UpdateCamera()
			
		Case GAME_OVER_MODE
			UpdateWater()
			UpdateMirrorPlane()
			UpdateGameOverSequence()

        Case CONFIG_SETTINGS_MODE
            UpdateWater
            UpdateSettingsDialog

        Case GAME_MAIN_MENU_MODE
            UpdateMainMenu

        Case GAME_DIFFICULTY_MENU_MODE
            UpdateWater
            UpdateDifficultyMenu

        Case GAME_ZEN_KOAN_MODE
            UpdateWater
            UpdateSprites
            UpdateZenKoan
		Default	
	End Select		
End Function

Function InitializeZenji3D()
	Local emptyBar = LoadImage( RarExtractFile( "res\bitmaps\loading-empty.png" ) )
	Local fullBar = LoadImage( RarExtractFile( "res\bitmaps\loading-full.png" ) )
	DrawProgressBar( emptyBar, fullBar, 5, True )
    #ifdef DEMO_BUILD
	    g_nagImage = LoadImage( RarExtractFile( "res\bitmaps\nag.png" ) ) 
        DrawProgressBar( emptyBar, fullBar, 10, True )
        ResizeImage g_nagImage, GraphicsWidth(), GraphicsHeight()
        DrawProgressBar( emptyBar, fullBar, 15, True )
    #endif
 	InitializeFonts: DrawProgressBar( emptyBar, fullBar, 19, True )
    g_clickSound = SoundLoad( RarExtractFile( "res\sounds\click.wav" ) )
    g_clickSound\userFlag = SOUND_TYPE_FX
	g_pauseSound = SoundLoad( RarExtractFile( "res\sounds\pause.wav" ) )
    g_pauseSound\userFlag = SOUND_TYPE_FX
 	DrawProgressBar( emptyBar, fullBar, 20, True )
    InitializeInputSystem
	InitializeCamera: DrawProgressBar( emptyBar, fullBar, 30, True )
	InitializeSpriteSystem: DrawProgressBar( emptyBar, fullBar, 35, True )
    InitializePointerSprite
    InitializeZenKoans: DrawProgressBar( emptyBar, fullBar, 40, True )
	InitializeScoreBoard True: DrawProgressBar( emptyBar, fullBar, 42, True )
	InitializeAttractSequence: DrawProgressBar( emptyBar, fullBar, 45, True )
	InitializeGame: DrawProgressBar( emptyBar, fullBar, 80, True )
	InitializeWaterSystem: DrawProgressBar( emptyBar, fullBar, 85, True )
	InitializeTheSource: DrawProgressBar( emptyBar, fullBar, 90, True )
	InitializeIllusions: DrawProgressBar( emptyBar, fullBar, 92, True )
	SetAttractMode
    DrawProgressBar( emptyBar, fullBar, 95, True )
	RarRemoveFiles 
    DrawProgressBar( emptyBar, fullBar, 98, True )
    InitializeDiskAccessIcon
    DrawProgressBar( emptyBar, fullBar, 102, True )
	FreeImage emptyBar
	FreeImage fullBar
End Function

Function InitializeFonts()
    Delete g_finalFight8r
    Delete g_finalFight16r
    Delete g_finalFight16b
    g_finalFight8r = LoadBitmapFont( RarExtractFile( "res\bitmaps\ff-font8r.png" ), FINAL_FIGHT_CHARSET, 0 )
    g_finalFight16r = LoadBitmapFont( RarExtractFile( "res\bitmaps\ff-font16r.png" ), FINAL_FIGHT_CHARSET, 0 )
    g_finalFight16b = LoadBitmapFont( RarExtractFile( "res\bitmaps\ff-font16b.png" ), FINAL_FIGHT_CHARSET, 0 )
End Function

Function UpdateSystemSettings() 
    If IsVideoResetRequired()
        SetGraphicsMode
        SetBuffer BackBuffer(): Cls
        InitializeCamera True
        InitializeSpriteSystem
        InitializePointerSprite True
        InitializeDiskAccessIcon
        EnableDiskAccessIcon
        InitializeZenKoans True: UpdateDiskAccessIcon
        InitializeFonts: UpdateDiskAccessIcon
        InitializeScoreBoard: UpdateDiskAccessIcon
        InitializeGame: UpdateDiskAccessIcon
        InitializeWaterSystem: UpdateDiskAccessIcon
        InitializeTheSource: UpdateDiskAccessIcon
        InitializeIllusions: UpdateDiskAccessIcon
        #ifdef DEMO_BUILD
            g_nagImage = LoadImage( RarExtractFile( "res\bitmaps\nag.png" ) ) 
            ResizeImage g_nagImage, GraphicsWidth(), GraphicsHeight()
        #endif
        ResetPerformed
    EndIf
    AntiAlias (g_antiAliasSetting\value = "True")
    EnableDirectInput (g_useDirectInputSetting\value = "True")
End Function

Function SetVolumeLevels()
    Local musicVolume.Setting = GetSetting( "MusicVolume" )
    Local soundVolume.Setting = GetSetting( "SoundVolume" )
    SoundVolumeAll Float( Int( musicVolume\value ) ) / 100, SOUND_TYPE_MUSIC
    SoundVolumeAll Float( Int( soundVolume\value ) ) / 100, SOUND_TYPE_FX
End Function

#ifdef DEMO_BUILD
Function ShowNagScreen()
	Local y 
    Select GraphicsHeight()
        Case 480: y = 164
        Case 600: y = 200
        Case 768: y = 350
    End Select
	FlushKeys
	DrawBlock g_nagImage, 0, 0
	CentreBitmapFont g_finalFight16r, y, "THANKS FOR PLAYING!!!" : y = y + 32
	CentreBitmapFont g_finalFight16b, y, "PRESS ESCAPE TO EXIT"
	Flip False
	Repeat
        Delay 1
    Until KeyHit( KEY_ESCAPE )
End Function
#endif
