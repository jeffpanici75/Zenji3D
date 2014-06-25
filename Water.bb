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

Const TEXTURE_FRAME_DELAY = 4
Const WATER_TEXTURE_FRAMES = 22

Global g_waterMesh
Global g_waterSurface
Global g_waterTexture
Global g_waterMeshVertexCount
Global g_textureFrameDelay
Global g_textureFrame
Global g_waterSpeed
Global g_waveHeight

Dim g_vertices.Vertex( 0 )

Function InitializeWaterSystem()
	g_waterMesh = LoadMesh( RarExtractFile( "res\models\20x20quad.3ds" ) )
	g_waterTexture = LoadAnimTexture( RarExtractFile( "res\bitmaps\wateranim.jpg" ), 256, 124, 124, 0, 25 )
	RotateMesh g_waterMesh, 90, 0, 0
	RotateEntity g_waterMesh, -90, 0, 0
	ScaleEntity g_waterMesh, 10, 10, 10
	EntityTexture g_waterMesh, g_waterTexture, 0, 1
	ScaleTexture g_waterTexture, .100, .100
	EntityAlpha g_waterMesh, 0.4
	EntityShininess g_waterMesh, 0.6

	g_waterSurface = GetSurface( g_waterMesh, 1 )
	g_waterMeshVertexCount = CountVertices( g_waterSurface )
	Dim g_vertices.Vertex( g_waterMeshVertexCount )
	For i = 0 To g_waterMeshVertexCount - 1
		g_vertices(i) = New Vertex
		g_vertices(i)\x# = VertexX#( g_waterSurface, i )
		g_vertices(i)\y# = VertexY#( g_waterSurface, i )
		g_vertices(i)\z# = VertexZ#( g_waterSurface, i )
	Next
	g_waterSpeed = 4
	g_waveHeight = 1.125
End Function

	
; Create a wave effect by moving all the vertices in the mesh up and down using Sin
; Try editing:
; Freq#=MilliSecs()/10 
;                   ^ The Bigger the divide, the slower the water moves
; Vertex(a)\z#=Sin(freq+Vertex(a)\x#*300+Vertex(a)\y#*400)*1.125
;                                                         ^ The Bigger the Multiply The Higher 
;                                                           the waves will be, lower = smaller
Function UpdateWater()
	g_textureFrameDelay = g_textureFrameDelay + 1
	If g_textureFrameDelay = TEXTURE_FRAME_DELAY
		EntityTexture g_waterMesh, g_waterTexture, g_textureFrame, 1
		g_textureFrame = g_textureFrame + 1
		If g_textureFrame = WATER_TEXTURE_FRAMES
			g_textureFrame = 0
		EndIf	
		g_textureFrameDelay = 0
	EndIf

	For i = 0 To g_waterMeshVertexCount - 1 
		freq# = MilliSecs() / g_waterSpeed
		g_vertices(i)\z# = Sin( freq + g_vertices(i)\x# * 300 + g_vertices(i)\y# * 400) * g_waveHeight
		VertexCoords g_waterSurface, i, g_vertices(i)\x#, g_vertices(i)\y#, g_vertices(i)\z#
	Next
	UpdateNormals g_waterMesh
End Function
