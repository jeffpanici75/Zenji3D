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

Const PLATFORM_DISTANCE# = 13.35

Const BOARD_OFFSET_Y# = 750
Const BOARD_OFFSET_Z# = 0

Const LEVEL_MUSIC_NORMAL = 0
Const LEVEL_MUSIC_FAST = 1

Const PIPE_OFFSET_Y# = 2.25
Const PIPE_ROTATE_SPEED# = 10
Const PIPE_ROTATE_MS = 150

Const MOVE_RIGHT = 1
Const MOVE_LEFT = 2
Const MOVE_UP = 3
Const MOVE_DOWN = 4
Const ROTATE_RIGHT = 5
Const ROTATE_LEFT = 6

Const AWARD_INIT = 1
Const AWARD_SCORE_TIMER = 2
Const AWARD_SCORE_PIPES = 3
Const AWARD_PAUSE = 4
Const AWARD_FINI = 5

Const MIRROR_BOUNCE_LIMIT = 60

Global g_levelMusic.Sound[2]
Global g_currentLevel
Global g_currentPuzzle.Puzzle
Global g_currentLevelMusic
Global g_gameSeconds
Global g_boardWidth
Global g_boardHeight
Global g_boardPivot
Global g_boardOffset.Rectangle
Global g_moonLight
Global g_mirrorPlane
Global g_mirrorCamera
Global g_mirrorImage
Global g_mirrorTexture
Global g_mirrorDirY# = .0255
Global g_mirrorBounceCount = 0
Global g_mirrorRect.Rectangle
Global g_mirrorCenter.Point
Global g_awardState = AWARD_INIT
Global g_platform
Global g_platformTexture
Global g_platforms[2]
Global g_pipes[3]
Global g_pipeIdx
Global g_gameTimer.Timer
Global g_addBonusTimer.Timer
Global g_pipeRotateTimer.Timer
Global g_boardShapes

Function InitializeGame( reload = False )
    If reload = False
        InitializePuzzleRules
        LoadHighScores
        LoadBonusResources
        LoadGameAwardResources
        g_levelMusic[0] = SoundLoad( RarExtractFile( "res\sounds\level-music.mp3" ), True )
        g_levelMusic[1] = SoundLoad( RarExtractFile( "res\sounds\level-music-fast.mp3" ), True )
        g_levelMusic[0]\userFlag = SOUND_TYPE_MUSIC
        g_levelMusic[1]\userFlag = SOUND_TYPE_MUSIC
    EndIf    
	LoadPlayerResources reload
	LoadGameOverResources reload
	LoadGameStatsResources reload

    g_boardShapes = LoadAnimImage( RarExtractFile( "res\bitmaps\shapes.png" ), 32, 32, 0, 9 )
    g_platformTexture = LoadTexture( RarExtractFile( "res\models\Platf768.bmp" ) )
	g_platforms[0] = LoadMesh( RarExtractFile( "res\models\blue-platform.3ds" ) )
	g_platforms[1] = LoadMesh( RarExtractFile( "res\models\yellow-platform.3ds" ) )
	For i=0 To 1
		HideEntity g_platforms[i]
        ScaleMesh g_platforms[i], 3.25, 3.25, 3.25
	Next	
	g_pipes[0] = LoadAnimMesh( RarExtractFile( "res\models\pipe-s.3ds" ) )
    g_pipes[1] = LoadAnimMesh( RarExtractFile( "res\models\pipe-t.3ds" ) )
    g_pipes[2] = LoadAnimMesh( RarExtractFile( "res\models\pipe-l.3ds" ) )
	For i=0 To 2
		HideEntity g_pipes[i]
		ScaleEntity g_pipes[i], 1.5, 1.5, 1.5
		glass = GetChild( g_pipes[i], 2 )
		EntityAlpha glass, .40
		EntityColor glass, 255, 255, 255
	Next

	g_mirrorImage = CreateImage( 256, 256 )
    ClearTextureFilters
	g_mirrorTexture = CreateTexture( 256, 256, 1 + 2 + 256 )
    TextureFilter "", 1 + 8
	ScaleTexture g_mirrorTexture, -1, -1
    g_mirrorPlane = CreateQuad( 0 )
	EntityAlpha g_mirrorPlane, .9
	EntityShininess g_mirrorPlane, .6
	PositionEntity g_mirrorPlane, 0, -10, BOARD_OFFSET_Z + 40
	RotateEntity g_mirrorPlane, 90, 0, 0
	TurnEntity g_mirrorPlane, 0, 180, 0
	EntityTexture g_mirrorPlane, g_mirrorTexture
	ScaleEntity g_mirrorPlane, 300, 300, 1

	g_mirrorCamera = CreateCamera()
	CameraClsColor g_mirrorCamera, 0, 0, 0
	PositionEntity g_mirrorCamera, -7.5, BOARD_OFFSET_Y - 80, BOARD_OFFSET_Z
	RotateEntity g_mirrorCamera, -90, 0, 0
	CameraProjMode g_mirrorCamera, 0
	CameraViewport g_mirrorCamera, 0, 0, 256, 256

	g_moonLight = CreateLight( 2 )
	PositionEntity g_moonLight, 0, 100, -50
	AmbientLight 64, 64, 64
End Function

Function PrepareCurrentLevel()
    Local thisPiece.PuzzlePiece = Null
	ClearCollisions
	g_boardPivot = CreatePivot()
	g_boardOffset = New Rectangle
	g_currentPuzzle = GetPuzzle( g_currentLevel - 1 )
	InitializeGameBoard( g_currentPuzzle\width, g_currentPuzzle\height )
	For y = 1 To g_currentPuzzle\height
		For x = 1 To g_currentPuzzle\width
            thisPiece = GetPuzzlePiece( x, y )
			If thisPiece\piece <> PUZZLE_PIECE_N
				thisPiece\entity = CopyEntity( g_pipes[thisPiece\piece], g_boardPivot )
				PositionPuzzlePiece( thisPiece, x, y )
			EndIf	
		Next
	Next	
	SeedRnd Millisecs()
	SetPlayer( g_currentPuzzle\px, g_currentPuzzle\py )
    i = 0  
    Repeat
        If i < g_currentPuzzle\nbrIllusions 
            If i = ( g_currentPuzzle\nbrIllusions - 1 ) And g_currentPuzzle\nbrIllusions > 1
                AddIllusion( g_currentPuzzle\illusionsSpark, ILLUSION_THINK_SEEK )
            Else    
                AddIllusion( g_currentPuzzle\illusionsSpark )
            EndIf
        Else    
            Exit
        EndIf
        i = i + 1
    Forever    
	SetTheSource( g_currentPuzzle\sx, g_currentPuzzle\sy )
	PositionEntity g_boardPivot, 0, BOARD_OFFSET_Y, BOARD_OFFSET_Z
	ClearTexture( g_mirrorTexture )
	UpdateMirrorTexture()
    g_addBonusTimer = StartTimer( 4000 - (g_currentLevel * 50) )
	CheckLevelAddBonuses()
	CheckForPuzzleCompletion()
	ResetCamera()
	UpdateFaces()
	UpdateScore()
	UpdateSolved()
	ShowStatistics()
	g_currentLevelMusic = LEVEL_MUSIC_NORMAL
	If Not InAttractMode()
		SoundStopAll
		SoundPlay g_levelMusic[g_currentLevelMusic], True
	EndIf
	ResetGameTimer()
End Function

Function InitializeGameBoard( width, height )
	Local x = 0
	Local z = 0
    Local boardSurface
    Local renderPlatform
    Local workImage = CreateImage( 32, 32 )
    Local workBuffer
    CopyRect 0, 0, 32, 32, 0, 0, ImageBuffer( g_boardShapes, g_currentPuzzle\shape ), ImageBuffer( workImage )
    ResizeImage workImage, width, height
    workBuffer = ImageBuffer( workImage )

	g_boardWidth = width
	g_boardHeight = height
	g_boardOffset\x = -((width / 2) * PLATFORM_DISTANCE) 
	g_boardOffset\y = -((height / 2) * PLATFORM_DISTANCE)
	g_boardOffset\width = ((width / 2) * PLATFORM_DISTANCE) - g_boardOffset\x
	g_boardOffset\height = ((height / 2) * PLATFORM_DISTANCE) - g_boardOffset\y
	z = g_boardOffset\y
	x = g_boardOffset\x
    
    g_platform = CreateEmptyMesh( g_boardPivot )
    EntityTexture g_platform, g_platformTexture
    PositionEntity g_platform, 0, 0, 0

    renderPlatform = False
    LockBuffer workBuffer
	For j = 1 To g_boardHeight
		For i = 1 To g_boardWidth
            If( ReadPixelFast( i - 1, j - 1, workBuffer ) And $FFFFFF ) <> 0
                AddMeshToSurface( g_platforms[renderPlatform], GetSurface( g_platform, 1 ), x, 0, z )
            Else
                Local piece.PuzzlePiece = GetPuzzlePiece( i, j )
                piece\piece = PUZZLE_PIECE_N
            EndIf    
			x = x + PLATFORM_DISTANCE
            renderPlatform = Not renderPlatform
		Next
        renderPlatform = Not renderPlatform
		x = g_boardOffset\x
		z = z + PLATFORM_DISTANCE
	Next
    UnlockBuffer workBuffer
    FreeImage workImage
End Function

Function FreeLevelResources()
	Local totalPieces = g_boardWidth * g_boardHeight - 1
	DisownPlayerSprites()
	RemoveIllusions()
	FreeTheSource()
	FreeBonuses()
	HideStatistics()
	FreeEntity g_boardPivot: g_boardPivot = 0
	For i = 0 To totalPieces
		Delete g_currentPuzzle\pieces[i]
	Next
	g_player\entity[0] = 0
	g_Player\entity[1] = 0
	Delete g_gameTimer
	Delete g_mirrorRect
	Delete g_boardOffset
	Delete g_mirrorCenter
    Delete g_addBonusTimer
	Delete g_pipeRotateTimer
End Function

Function PositionPuzzlePiece( piece.PuzzlePiece, x, y )
	rotVal = g_pipeRotations( piece\piece, piece\rotation, 0 )
	pos.Vertex = GetPlatformVertex( x, y )
	RotateEntity piece\entity, 0, rotVal, 0
	PositionEntity piece\entity, pos\x, pos\y + PIPE_OFFSET_Y, pos\z
End Function

Function UpdateMirrorTexture()
    Local mcx, mcy, mw, mh
    CameraProjMode g_camera, 0
    CameraProjMode g_mirrorCamera, 1
    RenderWorld
    If g_mirrorRect = Null
        g_mirrorRect = New Rectangle
        g_mirrorCenter = New Point
        CalculateMirrorRect
    EndIf
    mcx = Int( g_mirrorCenter\x )
    mcy = Int( g_mirrorCenter\y )
    mw = Int( g_mirrorRect\width )
    mh = Int( g_mirrorRect\height )
    ClearTextureRect g_mirrorTexture, mcx, mcy, mw, mh
    If g_realTimeMirrorSetting\value = "False"
        CopyTextureRectS g_mirrorRect\x, g_mirrorRect\y, mw, mh, mcx, mcy, BackBuffer(), TextureBuffer( g_mirrorTexture )
    Else
        GrabImage g_mirrorImage, Int(g_mirrorRect\x), Int(g_mirrorRect\y)
        SetBuffer TextureBuffer( g_mirrorTexture )
        DrawImageRect g_mirrorImage, mcx, mcy, 0, 0, mw, mh
        SetBuffer BackBuffer()
    EndIf    
    CameraProjMode g_mirrorCamera, 0
    CameraProjMode g_camera, 1
End Function

Function CalculateMirrorRect()
    Local platformY# = EntityY( g_platform, True )
	CameraProject( g_mirrorCamera, g_boardOffset\x, platformY, g_boardOffset\y )
	g_mirrorRect\x = ProjectedX() - 16
	g_mirrorRect\y = ProjectedY() - 16
	CameraProject( g_mirrorCamera, g_boardOffset\width + g_boardOffset\x, platformY, g_boardOffset\height + g_boardOffset\y )
	g_mirrorRect\width = (ProjectedX() - g_mirrorRect\x)
	g_mirrorRect\height = (ProjectedY() - g_mirrorRect\y) + 16
	g_mirrorCenter\x = (256 - g_mirrorRect\width) / 2
	g_mirrorCenter\y = (256 - g_mirrorRect\height) / 2
End Function

Function UpdateMirrorPlane()
	g_mirrorBounceCount = g_mirrorBounceCount + 1
	If g_mirrorBounceCount > MIRROR_BOUNCE_LIMIT
		g_mirrorBounceCount = 0
		g_mirrorDirY = -g_mirrorDirY
	EndIf	
	MoveEntity g_mirrorPlane, 0, g_mirrorDirY, 0
End Function

Function UpdatePipes()
	For i = 0 To ((g_boardWidth * g_boardHeight) - 1)
		If g_currentPuzzle\pieces[i]\isRotating
			g_currentPuzzle\pieces[i]\rotUnits = g_currentPuzzle\pieces[i]\rotUnits - PIPE_ROTATE_SPEED
			If g_currentPuzzle\pieces[i]\rotUnits <= 0
				g_currentPuzzle\pieces[i]\isRotating = False
				CheckForPuzzleCompletion()
			EndIf	
			Select g_currentPuzzle\pieces[i]\rotDir
				Case MOVE_RIGHT
					TurnEntity g_currentPuzzle\pieces[i]\entity, 0, -PIPE_ROTATE_SPEED, 0
				Case MOVE_LEFT
					TurnEntity g_currentPuzzle\pieces[i]\entity, 0, PIPE_ROTATE_SPEED, 0
			End Select
		EndIf
	Next
End Function

Function RotatePipeRight()
    Local thisPiece.PuzzlePiece = Null
	If CheckTimer( g_pipeRotateTimer ) And g_player\moving = False And g_player\halfMove = False
        thisPiece = GetPuzzlePiece( g_player\bx, g_player\by )
		If thisPiece\piece <> PUZZLE_PIECE_N And thisPiece\isRotating = False
            thisPiece\visited = False
            SetPipeColor thisPiece\entity, False
			thisPiece\isRotating = True
			thisPiece\rotUnits = 90
			thisPiece\rotDir = MOVE_RIGHT
			thisPiece\rotation = thisPiece\rotation + 1
			If thisPiece\rotation > 3
				thisPiece\rotation = 0
			EndIf
		EndIf
	EndIf
End Function

Function RotatePipeLeft()
    Local thisPiece.PuzzlePiece = Null
	If CheckTimer( g_pipeRotateTimer ) And g_player\moving = False And g_player\halfMove = False
        thisPiece = GetPuzzlePiece( g_player\bx, g_player\by )
		If thisPiece\piece <> PUZZLE_PIECE_N And thisPiece\isRotating = False
            thisPiece\visited = False
            SetPipeColor thisPiece\entity, False
			thisPiece\isRotating = True
			thisPiece\rotUnits = 90
			thisPiece\rotDir = MOVE_LEFT
			thisPiece\rotation = thisPiece\rotation - 1
			If thisPiece\rotation < 0 
				thisPiece\rotation = 3
			EndIf
		EndIf
	EndIf
End Function

Function GetPlatformVertex.Vertex( x=0, y=0 )
	vertex.Vertex = New Vertex
	vertex\x = g_boardOffset\x + ( (x - 1) * PLATFORM_DISTANCE )
	vertex\y = 3
	vertex\z = (g_boardOffset\y + ( (y - 1) * PLATFORM_DISTANCE ))
	Return vertex
End Function

Function GetBoardPivot()
	Return g_boardPivot
End Function

Function GetOppositeDirection( dir )
	opposite = 0
	If dir = MOVE_LEFT
		opposite = MOVE_RIGHT
	ElseIf dir = MOVE_RIGHT
		opposite = MOVE_LEFT
	ElseIf dir = MOVE_UP
		opposite = MOVE_DOWN
	ElseIf dir = MOVE_DOWN
		opposite = MOVE_UP
	EndIf
	Return opposite
End Function

Function GetDirectionName$( dir )
	If dir = MOVE_LEFT
		Return "MOVE_LEFT"
	ElseIf dir = MOVE_RIGHT
		Return "MOVE_RIGHT"
	ElseIf dir = MOVE_UP
		Return "MOVE_UP"
	ElseIf dir = MOVE_DOWN
		Return "MOVE_DOWN"
	EndIf
	Return "UNKNOWN DIRECTION"
End Function

Function ResetGameTimer()
	g_gameTimer = StartTimer( 1000 )
	g_pipeRotateTimer = StartTimer( PIPE_ROTATE_MS )
	g_gameSeconds = g_currentPuzzle\seconds
End Function

Function UpdateGameTimer()
	If CheckTimer( g_gameTimer )
		g_gameSeconds = g_gameSeconds - 1
		If g_gameSeconds <= 0 
			SetPlayerDiesMode()
		ElseIf g_gameSeconds = 10
            If Not InAttractMode()
			    SoundPause g_levelMusic[g_currentLevelMusic]
			    g_currentLevelMusic = LEVEL_MUSIC_FAST
			    SoundPlay g_levelMusic[g_currentLevelMusic]
            EndIf    
		EndIf
	EndIf
    If CheckTimer( g_addBonusTimer )
        CheckLevelAddBonuses
    EndIf
	UpdateTimer()
End Function

Function CheckGameCollisions()
	If CheckPlayerCollisions() Or CheckIllusionCollisions() Or CheckLightningCollisions() Then
		ResetEntity g_player\entity[0]
		ResetEntity g_player\entity[1]
		ResetIllusionCollisions()
		ResetLightningCollisions()
		If TimerExpired( g_playerUpTimer )
			SetPlayerDiesMode()
		EndIf	
	EndIf
End Function

Function EnableCollisions()
	Collisions PLAYER_TYPE, ILLUSION_TYPE, 1, 1
	Collisions PLAYER_TYPE, LIGHTNING_TYPE, 1, 1
	Collisions ILLUSION_TYPE, PLAYER_TYPE, 1, 1
	Collisions LIGHTNING_TYPE, PLAYER_TYPE, 1, 1
End Function
