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

Const LIGHTNING_TYPE = 3

Global g_range# = 0
Global g_division# = 0
Global g_turnX# = 15

Type LightningBolt
	Field sparks.Lightning[500]
	Field parts
	Field deviation#
	Field fromEntity
	Field toEntity
	Field deviationEntity
	Field targetEntity
	Field dummyEntity
End Type

Type Lightning
	Field sparkEntity
End Type

Function CreateLightningBolt.LightningBolt( fromEntity, toEntity, parts, deviation#, parent = 0 )
	bolt.LightningBolt = New LightningBolt
	bolt\fromEntity = fromEntity
	bolt\toEntity = toEntity
	bolt\parts = parts - 1
	bolt\deviation = deviation
	bolt\deviationEntity = CreatePivot( parent )
	bolt\targetEntity = CreatePivot( parent )
	bolt\dummyEntity = CreatePivot( parent )
	For i = 0 To bolt\parts
		bolt\sparks[i] = CreateSpark( parent )
	Next
	UpdateLightning( bolt )
	Return bolt
End Function

Function FreeLightningBolt( bolt.LightningBolt )
	For i = 0 To bolt\parts
		FreeEntity bolt\sparks[i]\sparkEntity
		Delete bolt\sparks[i]
	Next
	FreeEntity bolt\deviationEntity
	FreeEntity bolt\targetEntity
	FreeEntity bolt\dummyEntity
	Delete bolt
End Function

Function ResetLightning()
	For bolt.LightningBolt = Each LightningBolt
		FreeLightningBolt( bolt )
	Next
End Function

Function CheckLightningCollisions()
	For spark.Lightning = Each Lightning
		If CountCollisions( spark\sparkEntity ) > 0
			Return True
		EndIf
	Next
End Function

Function ResetLightningCollisions()
	For spark.Lightning = Each Lightning
		ResetEntity spark\sparkEntity
	Next
End Function

Function UpdateLightning( bolt.LightningBolt )
	Local de = bolt\deviationEntity
	Local fe = bolt\fromEntity
	Local te = bolt\toEntity
	Local tge = bolt\targetEntity
	Local dummy = bolt\dummyEntity
	PositionEntity dummy, EntityX#( fe, True ), EntityY#( fe, True ), EntityZ#( fe, True )
	PointEntity dummy, te
	g_range = EntityDistance#( fe, te )
	g_division = g_range / bolt\parts
	PositionEntity de, EntityX#( fe, True ), EntityY#( fe, True ), EntityZ#( fe, True )
	For i = 0 To bolt\parts
		sparkEntity = bolt\sparks[i]\sparkEntity
		PositionEntity tge, EntityX#( dummy, True ), EntityY#( dummy, True ), EntityZ#( dummy, True )
		MoveEntity tge, 0, Rnd( bolt\deviation )-Rnd( bolt\deviation ), Rnd( bolt\deviation )-Rnd( bolt\deviation )
		PointEntity de, tge
		PositionEntity sparkEntity, EntityX#( de, True ), EntityY#( de, True ), EntityZ#( de, True )
		RotateEntity sparkEntity, EntityPitch#( de, True ), EntityYaw#( de, True ), EntityRoll#( de, True )
		ScaleEntity sparkEntity, EntityDistance#( de, tge ), .4, 1.3
		PositionEntity de, EntityX#( tge, True ), EntityY#( tge, True ), EntityZ#( tge, True )
		MoveEntity dummy, 0, 0, g_division
		TurnEntity sparkEntity, g_turnX, 90, 0
		g_turnX = g_turnX + 30
		If g_turnX > 90
			g_turnX = 15
		EndIf
	Next
End Function

Function CreateSpark.Lightning( parent )
	spark.Lightning = New Lightning
	spark\sparkEntity = CreateSparkMesh( parent )
    EntityColor spark\sparkEntity, 255, Rnd( 200, 255 ), Rnd( 64, 255 )
	EntityType spark\sparkEntity, LIGHTNING_TYPE
    TurnEntity spark\sparkEntity, 0, 0, 0
	EntityRadius spark\sparkEntity, 1.5
	ScaleEntity spark\sparkEntity, 1, 1, 1
	Return spark
End Function

Function CreateSparkMesh( parent )
    mesh = CreateMesh( parent )
    surface = CreateSurface( mesh ) 
    AddVertex  surface, 0, 1, 0, 1, 0
    AddVertex  surface, 1, 1, 0, 0, 0
    AddVertex  surface, 0,-1, 0, 1, 1
    AddVertex  surface, 1,-1, 0, 0, 1
    AddTriangle surface, 0, 1, 2
    AddTriangle surface, 3, 2, 1
    AddTriangle surface, 2, 1, 0
    AddTriangle surface, 1, 2, 3
	TurnEntity mesh, 0, 0, 0
	EntityFX mesh, 1
	Return mesh
End Function
