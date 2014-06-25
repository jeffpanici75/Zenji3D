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

Const ILLUSION_UNKNOWN = 0
Const ILLUSION_SPEED# = .45
Const ILLUSION_OFFSET_Y# = 1.75
Const ILLUSION_TYPE = 1

Const ILLUSION_SPARK_NOP = 0
Const ILLUSION_SPARK_RUMBLE = 1
Const ILLUSION_SPARK_FIRE = 2

Const ILLUSION_THINK_WANDER = 1
Const ILLUSION_THINK_SEEK = 2

Type Illusion
	Field id
	Field fire.Fire
	Field bx
	Field by
	Field think
	Field thinkUnits
    Field thinkType
	Field sparkDir
	Field sparkState
	Field sparkStateTimer.Timer
	Field sparkEndPivot
	Field sparkSegments
	Field sparkTriggerSeconds
	Field bolt.LightningBolt
	Field moving
	Field units#
	Field halfMove
	Field halfMoveRev
	Field halfMoveReset
	Field visited[MAX_PUZZLE_PIECES]
End Type

Global g_zapSound.Sound[2]
Global g_warnPlayer
Global g_boardRefreshed 
Global g_illusionCount

Function InitializeIllusions()
	StartFireEffectsSubsystem
	g_zapSound[0] = SoundLoad( RarExtractFile( "res\sounds\zap-charge.mp3" ) )
	g_zapSound[1] = SoundLoad( RarExtractFile( "res\sounds\zap-discharge.mp3" ) )
    g_zapSound[0]\userFlag = SOUND_TYPE_FX
    g_zapSound[1]\userFlag = SOUND_TYPE_FX
	g_illusionCount = 0
End Function

Function ShutdownIllusions()
	EndFireEffectsSubsystem()
	SoundFree g_zapSound[0]
	SoundFree g_zapSound[1]
End Function

Function AddIllusion( spark = False, thinkType = ILLUSION_THINK_WANDER )
	illusion.Illusion = New Illusion
	Repeat 
		illusion\bx = Rand( 1, g_boardWidth )
		illusion\by = Rand( 1, g_boardHeight )
		idx = (illusion\by - 1) * g_currentPuzzle\width + (illusion\bx - 1)
	Until g_currentPuzzle\pieces[idx]\piece <> PUZZLE_PIECE_N	
	temp.Vertex = GetPlatformVertex( illusion\bx, illusion\by )
	temp\y = temp\y + ILLUSION_OFFSET_Y
	illusion\fire = AddFire( temp\x, temp\y, temp\z )
	illusion\fire\posVec = temp
	illusion\moving = False
	illusion\think = Rand( MOVE_RIGHT, MOVE_DOWN )
    illusion\thinkType = thinkType
	illusion\thinkUnits = 0
	illusion\sparkState = ILLUSION_SPARK_NOP
    If spark
	    illusion\sparkStateTimer = StartTimer( 1400 )
	    illusion\sparkTriggerSeconds = Rand( 3, 15 ) * 1000
    EndIf    
	EntityParent illusion\fire\piv, GetBoardPivot()
	EntityType illusion\fire\piv, ILLUSION_TYPE
	EntityRadius illusion\fire\piv, 3
	illusion\id = g_illusionCount
	g_illusionCount = g_illusionCount + 1
End Function

Function RemoveIllusions()
	ResetLightning()
	EraseFires()
	Delete Each Illusion
	g_illusionCount = 0
End Function

Function CheckIllusionCollisions()
	For illusion.Illusion = Each Illusion
		If CountCollisions( illusion\fire\piv ) > 0
			Return True
		EndIf
	Next
End Function

Function ResetIllusionCollisions()
	For illusion.Illusion = Each Illusion
		ResetEntity illusion\fire\piv
	Next
End Function

Function UpdateIllusions()
	For illusion.Illusion = Each Illusion
		If Not illusion\moving
			If illusion\sparkState <> ILLUSION_SPARK_NOP
				If g_warnPlayer
					ShowWarningCallout()
					g_warnPlayer = False
				EndIf
				Select illusion\sparkState
					Case ILLUSION_SPARK_RUMBLE
						AddParticle( illusion\fire\piv )
						If CheckTimer( illusion\sparkStateTimer )
							SoundPlay g_zapSound[1]
							illusion\sparkState = ILLUSION_SPARK_FIRE
							g_warnPlayer = SetLightningEndPivot( illusion )
							illusion\bolt = CreateLightningBolt( illusion\fire\piv, illusion\sparkEndPivot, illusion\sparkSegments, 1.5 )
							ResetTimer illusion\sparkStateTimer
						EndIf	
					Case ILLUSION_SPARK_FIRE
						If CheckTimer( illusion\sparkStateTimer )
							illusion\sparkState = ILLUSION_SPARK_NOP
							FreeLightningBolt( illusion\bolt )
							FreeEntity illusion\sparkEndPivot
							illusion\sparkEndPivot = 0
						Else
							g_warnPlayer = SetLightningEndPivot( illusion )
							UpdateLightning( illusion\bolt )
						EndIf	
				End Select
			Else	
				ThinkForIllusion( illusion )
			EndIf	
		Else
			If IsMoveValid( illusion\bx, illusion\by, illusion\think ) Or illusion\halfMove = True
				illusion\units = illusion\units - ILLUSION_SPEED
				If illusion\units < 0
					If illusion\halfMove = False
						UpdateIllusionPosition( illusion )
					EndIf		
					If illusion\halfMoveReset
						illusion\halfMoveReset = False
						illusion\halfMove = False
					EndIf
                    illusion\moving = False
                    illusion\units = 0
                    If illusion\halfMove = False
                        illusion\fire\posVec = GetPlatformVertex( illusion\bx, illusion\by )
                        illusion\fire\posVec\y = illusion\fire\posVec\y + ILLUSION_OFFSET_Y
                    EndIf
				Else	
					Select illusion\think
						Case MOVE_UP
							illusion\fire\dirVec\z = -.15
							illusion\fire\posVec\z = illusion\fire\posVec\z + ILLUSION_SPEED
						Case MOVE_DOWN
							illusion\fire\dirVec\z = .15
							illusion\fire\posVec\z = illusion\fire\posVec\z - ILLUSION_SPEED
						Case MOVE_RIGHT
							illusion\fire\dirVec\x = -.15
							illusion\fire\posVec\x = illusion\fire\posVec\x + ILLUSION_SPEED
						Case MOVE_LEFT
							illusion\fire\dirVec\x = .15
							illusion\fire\posVec\x = illusion\fire\posVec\x - ILLUSION_SPEED
					End Select
				EndIf
			Else
				illusion\units = PLATFORM_DISTANCE - illusion\units
				illusion\think = GetOppositeDirection( illusion\think )
				illusion\thinkUnits = 1
				illusion\halfMove = True
				illusion\halfMoveReset = True
				illusion\halfMoveRev = ILLUSION_UNKNOWN
			EndIf
			PositionEntity illusion\fire\piv, illusion\fire\posVec\x, illusion\fire\posVec\y, illusion\fire\posVec\z
		EndIf	
	Next	
	UpdateFires()
End Function

Function ThinkForIllusion( illusion.Illusion, thinkOverride = 0 )
	If thinkOverride > 0
		illusion\think = thinkOverride
	Else
		If CheckTimer( illusion\sparkStateTimer, illusion\sparkTriggerSeconds )
			If illusion\halfMove = False
				For i = MOVE_RIGHT To MOVE_DOWN
					If IsMoveValid( illusion\bx, illusion\by, i )
						SoundPlay g_zapSound[0]
						illusion\sparkDir = i
						g_warnPlayer = SetLightningEndPivot( illusion )
						illusion\sparkState = ILLUSION_SPARK_RUMBLE
						ResetTimer illusion\sparkStateTimer
						Return
					EndIf
				Next
			EndIf
			Return
		Else
			Local units = 0
			Local newDir = 0
            If illusion\thinkUnits > 0 And IsMoveValid( illusion\bx, illusion\by, illusion\think )
                illusion\thinkUnits = illusion\thinkUnits - 1
            Else 
                If illusion\halfMove
                    illusion\think = GetOppositeDirection( illusion\think )
                    illusion\thinkUnits = 1
                Else
                    Select illusion\thinkType
                        Case ILLUSION_THINK_WANDER
                            If illusion\think = MOVE_RIGHT Or illusion\think = MOVE_LEFT
                                illusion\think = Rand( MOVE_UP, MOVE_DOWN )
                            ElseIf illusion\think = MOVE_UP Or illusion\think = MOVE_DOWN
                                illusion\think = Rand( MOVE_RIGHT, MOVE_LEFT )
                            EndIf	
                            If Not IsMoveValid( illusion\bx, illusion\by, illusion\think )
                                illusion\think = GetOppositeDirection( illusion\think )
                            EndIf
                        Case ILLUSION_THINK_SEEK
                            diffx = illusion\bx - g_player\bx
                            diffy = illusion\by - g_player\by
                            If illusion\think = MOVE_UP Or illusion\think = MOVE_DOWN
                                If diffx > 0
                                    illusion\think = MOVE_LEFT
                                Else
                                    illusion\think = MOVE_RIGHT
                                EndIf
                            ElseIf illusion\think = MOVE_RIGHT Or illusion\think = MOVE_LEFT   
                                If diffy < 0
                                    illusion\think = MOVE_UP
                                Else
                                    illusion\think = MOVE_DOWN
                                EndIf
                            EndIf
                    End Select
                    illusion\thinkUnits = CalculateDirectionLength( illusion\bx, illusion\by, illusion\think ) - 1
                EndIf        
            EndIf        
		EndIf
	EndIf
	If IsMoveValid( illusion\bx, illusion\by, illusion\think ) And illusion\halfMove = False
		illusion\units = PLATFORM_DISTANCE
		illusion\moving = True
	Else
		If illusion\halfMove
			If illusion\think = illusion\halfMoveRev
				illusion\units = PLATFORM_DISTANCE / 2.00
				illusion\moving = True
				illusion\halfMove = True
				illusion\halfMoveReset = True
				illusion\halfMoveRev = ILLUSION_UNKNOWN
			EndIf
		Else
			If IsDirectionValid( illusion\bx, illusion\by, illusion\think )
				illusion\units = PLATFORM_DISTANCE / 2.00
				illusion\moving = True
				illusion\halfMove = True
				illusion\halfMoveReset = False
				illusion\halfMoveRev = GetOppositeDirection( illusion\think )
			EndIf	
		EndIf	
	EndIf
End Function

Function UpdateIllusionPosition( illusion.Illusion )
	Select illusion\think
		Case MOVE_UP
			illusion\by = illusion\by + 1
			If illusion\by > g_boardHeight
				illusion\by = g_boardHeight
			EndIf	
		Case MOVE_DOWN
			illusion\by = illusion\by - 1
			If illusion\by < 1
				illusion\by = 1
			EndIf
		Case MOVE_RIGHT
			illusion\bx = illusion\bx + 1
			If illusion\bx > g_boardWidth
				illusion\bx = g_boardWidth
			EndIf	
		Case MOVE_LEFT
			illusion\bx = illusion\bx - 1
			If illusion\bx < 1
				illusion\bx = 1
			EndIf	
	End Select
End Function

Function IllusionAtLocation( x, y )
	For illusion.Illusion = Each Illusion
		If illusion\bx = x And illusion\by = y
			Return True
		EndIf
	Next
	Return False
End Function

Function CalculateDirectionLength( x, y, dir )
	Local altx = 0
	Local alty = 0
	Local endx = x
	Local endy = y
	Local dist = 0
	Select dir
		Case MOVE_UP
			While IsMoveValid( endx, endy, MOVE_UP )
				endy = endy + 1
				If( IsMoveValid( endx, endy, MOVE_RIGHT ) Or IsMoveValid( endx, endy, MOVE_LEFT ) ) And alty = 0
					alty = endy
				EndIf
			Wend
			If( Rand( 1, 10 ) Mod 2 ) = 0 Then endy = alty
			dist = Abs( endy - y )
		Case MOVE_DOWN
			While IsMoveValid( endx, endy, MOVE_DOWN )
				endy = endy - 1
				If( IsMoveValid( endx, endy, MOVE_RIGHT ) Or IsMoveValid( endx, endy, MOVE_LEFT ) ) And alty = 0
					alty = endy
				EndIf
			Wend
			If( Rand( 1, 10 ) Mod 2 ) = 0 Then endy = alty
			dist = Abs( endy - y )
		Case MOVE_LEFT
			While IsMoveValid( endx, endy, MOVE_LEFT )
				endx = endx - 1
				If( IsMoveValid( endx, endy, MOVE_UP ) Or IsMoveValid( endx, endy, MOVE_DOWN ) ) And altx = 0
					altx = endx
				EndIf
			Wend
			If( Rand( 1, 10 ) Mod 2 ) = 0 Then endx = altx
			dist = Abs( endx - x )
		Case MOVE_RIGHT
			While IsMoveValid( endx, endy, MOVE_RIGHT )
				endx = endx + 1
				If( IsMoveValid( endx, endy, MOVE_UP ) Or IsMoveValid( endx, endy, MOVE_DOWN ) ) And altx = 0
					altx = 0
				EndIf
			Wend
			If( Rand( 1, 10 ) Mod 2 ) = 0 Then endx = altx
			dist = Abs( endx - x )
	End Select
	Return dist
End Function

Function SetLightningEndPivot( illusion.Illusion )
	Local endx = illusion\bx
	Local endy = illusion\by
	Select illusion\sparkDir
		Case MOVE_UP
			While IsMoveValid( endx, endy, MOVE_UP ): endy = endy + 1: Wend
			illusion\sparkSegments = Abs(endy - illusion\by) * 10
		Case MOVE_DOWN
			While IsMoveValid( endx, endy, MOVE_DOWN ): endy = endy - 1: Wend
			illusion\sparkSegments = Abs(endy - illusion\by) * 10
		Case MOVE_LEFT
			While IsMoveValid( endx, endy, MOVE_LEFT ): endx = endx - 1: Wend
			illusion\sparkSegments = Abs(endx - illusion\bx) * 10
		Case MOVE_RIGHT
			While IsMoveValid( endx, endy, MOVE_RIGHT ): endx = endx + 1: Wend
			illusion\sparkSegments = Abs(endx - illusion\bx) * 10
	End Select
	If illusion\sparkEndPivot = 0
		illusion\sparkEndPivot = CreatePivot( GetBoardPivot() )
	EndIf	
	pos.Vertex = GetPlatformVertex( endx, endy )
	pos\y = pos\y + ILLUSION_OFFSET_Y
	PositionEntity illusion\sparkEndPivot, pos\x, pos\y, pos\z
	Return ((g_player\bx >= illusion\bx And g_player\bx <= endx) And (g_player\by >= illusion\by And g_player\by <= endy))
End Function
