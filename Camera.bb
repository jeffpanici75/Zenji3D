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

Const CAMERA_ZOOM_IN_MAX# = 55
Const CAMERA_ZOOM_OUT_MAX# = 65
Const CAMERA_ZOOM_IN = 1
Const CAMERA_ZOOM_OUT = 2
Const CAMERA_FOLLOW_PLAYER = 3
Const CAMERA_LAG_PLAYER_Z# = 40
Const CAMERA_ZOOM_SPEED# = .90

Global g_cameraVertex.Vertex
Global g_camera
Global g_spotLight
Global g_zoomCount#
Global g_cameraState
Global g_cameraOverhead
Global g_cameraRoam
Global g_cameraRoamDeltaX#
Global g_cameraRoamDeltaZ#
Global g_cameraRoamUnitsX#
Global g_cameraRoamUnitsZ#
Global g_cameraAlpha#
Global g_alphaPlane

Function InitializeCamera( reload = False )
    If reload
        Delete g_cameraVertex
    EndIf
	g_cameraVertex = New Vertex
	g_camera = CreateCamera()
	CameraRange g_camera,0.1,3000
	CameraFogMode g_camera,1
	CameraFogColor g_camera,10,10,50
	CameraClsColor g_camera,10,10,50
	CameraFogRange g_camera,900,2000
	g_spotLight = CreateLight( 2, g_camera )
	LightRange g_spotLight, 125
	g_alphaPlane = CreateMesh( g_camera )
	camsurf = CreateSurface( g_alphaPlane )
	AddVertex( camsurf, -10, -10, 0 )
	AddVertex( camsurf, -10, 10, 0 )
	AddVertex( camsurf, 10, 10, 0 )
	AddVertex( camsurf, 10, -10, 0 )
	AddTriangle( camsurf, 0, 1, 2 )
	AddTriangle( camsurf, 0, 2,  3)
	MoveEntity g_alphaPlane, 0, 0, 1.00001
	EntityColor g_alphaPlane, 0, 0, 0
	EntityOrder g_alphaPlane, -100
	SetCameraAlpha( 0 )
End Function

Function UpdateCamera()
	Select g_cameraState
		Case CAMERA_ZOOM_IN
			If g_zoomCount > 0
				g_zoomCount = g_zoomCount - CAMERA_ZOOM_SPEED
				g_cameraVertex\y = g_cameraVertex\y - CAMERA_ZOOM_SPEED
				PositionCamera()
			Else
				g_cameraState = CAMERA_FOLLOW_PLAYER
			EndIf
						
		Case CAMERA_ZOOM_OUT	
			If g_zoomCount < CAMERA_ZOOM_OUT_MAX
				g_zoomCount = g_zoomCount + CAMERA_ZOOM_SPEED
				g_cameraVertex\y = g_cameraVertex\y + CAMERA_ZOOM_SPEED
			EndIf
			
		Case CAMERA_FOLLOW_PLAYER
			PositionCamera()

	End Select
	If g_cameraRoam
		If g_cameraRoamUnitsX > 0
			g_cameraVertex\x = g_cameraVertex\x + g_cameraRoamDeltaX
			g_cameraRoamUnitsX = g_cameraRoamUnitsX - CAMERA_ZOOM_SPEED
		EndIf	
		If g_cameraRoamUnitsZ > 0
			g_cameraVertex\z = g_cameraVertex\z + g_cameraRoamDeltaZ
			g_cameraRoamUnitsZ = g_cameraRoamUnitsZ - CAMERA_ZOOM_SPEED
		EndIf
		If g_cameraRoamUnitsX <= 0 And g_cameraRoamUnitsZ <= 0
			g_cameraRoam = False
		EndIf
	EndIf	
	PositionEntity g_camera, g_cameraVertex\x, g_cameraVertex\y, g_cameraVertex\z
End Function

Function PositionCamera()
	g_cameraVertex\x = g_player\pos\x
	If g_cameraOverhead = False
		g_cameraVertex\z = g_player\pos\z - CAMERA_LAG_PLAYER_Z
	Else
		g_cameraVertex\z = g_player\pos\z
	EndIf	
End Function

Function CameraRoam( dx, dz )
	g_cameraRoam = True
	g_cameraRoamUnitsX = Abs( g_player\pos\x - dx )
	g_cameraRoamUnitsZ = Abs( g_player\pos\z - dz )
	If g_theSource\pos\x < g_player\pos\x
		g_cameraRoamDeltaX = -CAMERA_ZOOM_SPEED
	Else
		g_cameraRoamDeltaX = CAMERA_ZOOM_SPEED
	EndIf
	If g_theSource\pos\z < g_player\pos\z
		g_cameraRoamDeltaZ = -CAMERA_ZOOM_SPEED
	Else
		g_cameraRoamDeltaZ = CAMERA_ZOOM_SPEED
	EndIf
End Function

Function IsCameraOverhead()
	Return g_cameraOverhead
End Function

Function ToggleCameraView()
	If g_cameraState = CAMERA_FOLLOW_PLAYER
		g_cameraOverhead = Not g_cameraOverhead
		If g_cameraOverhead
			g_cameraVertex\y = g_cameraVertex\y + 50
			CameraOverHead()
		Else
			CameraBehind()
			g_cameraVertex\y = g_cameraVertex\y - 50
		EndIf
	EndIf	
End Function

Function CameraOverWater()
	g_cameraVertex\x = 0
	g_cameraVertex\y = 500
	g_cameraVertex\z = -1500
	PositionEntity g_camera, g_cameraVertex\x, g_cameraVertex\y, g_cameraVertex\z
	RotateEntity g_camera, 60, 0, 0
End Function

Function ResetCamera( zoom = True )
	g_cameraOverhead = False
	If zoom
		g_cameraVertex\y = BOARD_OFFSET_Y + 100
		CameraBehind()
		CameraZoomIn()
	Else
		g_cameraVertex\y = BOARD_OFFSET_Y + 20
		CameraBehind()
	EndIf
End Function

Function CameraBehind()
	g_cameraVertex\x = g_theSource\pos\x
	g_cameraVertex\z = g_theSource\pos\z - CAMERA_LAG_PLAYER_Z
	PositionEntity g_camera, g_cameraVertex\x, g_cameraVertex\y, g_cameraVertex\z
	PointEntity g_camera, g_theSource\entity
End Function

Function CameraOverHead()
	g_cameraVertex\x = g_player\pos\x
	g_cameraVertex\z = g_player\pos\z
	PositionEntity g_camera, g_cameraVertex\x, g_cameraVertex\y, g_cameraVertex\z
	PointEntity g_camera, g_player\entity[0]
End Function

Function CameraZoomIn()
	g_cameraState = CAMERA_ZOOM_IN
	g_zoomCount = CAMERA_ZOOM_IN_MAX
End Function

Function CameraZoomOut()
	g_cameraState = CAMERA_ZOOM_OUT
	g_zoomCount = 0
End Function

Function CameraFollowPlayer()
	g_cameraState = CAMERA_FOLLOW_PLAYER
End Function

Function SetCameraAlpha( alpha# )
	g_cameraAlpha = alpha
	EntityAlpha g_alphaPlane, g_cameraAlpha
	If g_cameraAlpha <= 0
		HideEntity g_alphaPlane
	Else
		ShowEntity g_alphaPlane
	EndIf	
End Function

Function MoveCameraUp()
	g_cameraVertex\y = g_cameraVertex\y + 10
	PositionEntity g_camera, g_cameraVertex\x, g_cameraVertex\y, g_cameraVertex\z
	PointEntity g_camera, g_player\entity[0]
End Function

Function MoveCameraDown()
	g_cameraVertex\y = g_cameraVertex\y - 10
	PositionEntity g_camera, g_cameraVertex\x, g_cameraVertex\y, g_cameraVertex\z
	PointEntity g_camera, g_player\entity[0]
End Function

Function LabelEntity( camera, entity, label$ )
	If EntityInView( entity, camera )
		CameraProject camera, EntityX( entity ), EntityY( entity ), EntityZ( entity )
		x = ProjectedX() - 1
		y = ProjectedY() - 1
		DrawBitmapFont g_finalFight16r, x, y + 20, label
	EndIf
End Function
