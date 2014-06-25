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

Type GraphicMode
	Field width
	Field height
	Field depth
	Field is3D
End Type

Global g_mask = $FF00FF
Global g_currentGraphicMode.GraphicMode = Null

.validModes
Data "1", "1", 800, 600, 32
Data "1", "0", 800, 600, 16
Data "0", "1", 640, 480, 32
Data "0", "0", 640, 480, 16
Data "###"

Function SetGraphicsMode()
	Local width
	Local height
	Local depth
    Local videoMode.Setting
    Local colorDepth.Setting

	window = ( GetOptionValue( "-window" ) = "True" )
    If window 
        window = 2
    EndIf

	width = Int( GetOptionValue( "-resw" ) )
	height = Int( GetOptionValue( "-resh" ) )
	depth = Int( GetOptionValue( "-resd" ) )
    If width <> 0 And height <> 0 And depth <> 0
        g_currentGraphicMode = GetGraphicMode( width, height, depth, True )
	    If g_currentGraphicMode <> Null
	    	Graphics3D width, height, depth, window
	    	If depth = 16 Then g_mask = $F800F8
	    	Return
        EndIf
    EndIf

    videoMode = GetSetting( "VideoMode" )
    colorDepth = GetSetting( "ColorDepth" )
    If videoMode\value = "Auto"
        Restore validModes
        Repeat
            Read modeKey$
            If modeKey = "###" Then Exit
            Read depthKey$, width, height, depth
            g_currentGraphicMode = GetGraphicMode( width, height, depth, True )
            If g_currentGraphicMode <> Null
                Graphics3D width, height, depth, window
                videoMode\value = modeKey
                colorDepth\value = depthKey
                Return
            EndIf
        Forever    
        RuntimeError "Zenji 3D requires one of the following modes: 800x600x32/16 or 640x480x32/16. Please consider upgrading your video hardware."
    Else
        depth = 32 
        If colorDepth\value = "0" 
            depth = 16
            g_mask = $F800F8
        ElseIf colorDepth\value = "1"
            depth = 32
            g_mask = $FF00FF
        Else
            RuntimeError "An invalid color depth was given.  Valid values are '0' and '1'. Delete the config.dat file and re-run Zenji 3D."
        EndIf
        If videoMode\value = "0"
            width = 640
            height = 480
        ElseIf videoMode\value = "1"
            width = 800
            height = 600
        ElseIf videoMode\value = "2"
            width = 1024
            height = 768
        Else
            RuntimeError "An invalid video mode was given. Valid values are '0','1', and '2'. Delete the config.dat file and re-run Zenji 3D."
        EndIf
        g_currentGraphicMode = GetGraphicMode( width, height, depth, True )
        Graphics3D width, height, depth, window
    EndIf
End Function

Function EnumerateGraphicModes()
	For i = 1 To CountGfxModes()
		mode.GraphicMode = New GraphicMode
		mode\width = GfxModeWidth( i )
		mode\height = GfxModeHeight( i )
		mode\depth = GfxModeDepth( i )
		mode\is3D = GfxMode3D( i )
	Next
End Function

Function GetGraphicMode.GraphicMode( width, height, depth, td )
	For mode.GraphicMode = Each GraphicMode
		If mode\width = width And mode\height = height And mode\depth = depth And mode\is3D = td
			Return mode
		EndIf
	Next
	Return Null
End Function

Function GetMaskColor()
	Return g_mask
End Function

Function ClearTexture( texture, rgb = 0 )
	buffer = TextureBuffer( texture )
	LockBuffer buffer
	For y = 0 To (TextureHeight( texture ) - 1)
		For x = 0 To (TextureWidth( texture ) - 1)
			WritePixelFast x, y, rgb, buffer
		Next
	Next
	UnlockBuffer buffer		
End Function

Function ClearTextureRect( texture, x, y, w, h, rgb = 0 )
	buffer = TextureBuffer( texture )
	LockBuffer buffer
	For i = y To y + (h - 1)
		For j = x To x + (w - 1)
			WritePixelFast j, i, rgb, buffer
		Next
	Next	
	UnlockBuffer buffer
End Function

Function CopyTextureRect( x, y, w, h, dx, dy, fromBuffer, toBuffer, transparent = 0 )
	Local sdx = dx
	LockBuffer fromBuffer
	LockBuffer toBuffer
	For i = y To y + (h - 1)
		For j = x To x + (w - 1)
			rgb = ReadPixelFast( j, i, fromBuffer ) And $FFFFFF 
			If rgb = transparent 
				alpha = $0
			Else 
				alpha = $FF000000
			EndIf
			WritePixelFast dx, dy, rgb Or alpha, toBuffer 
			dx = dx + 1
		Next
		dx = sdx
		dy = dy + 1
	Next	
	UnlockBuffer toBuffer
	UnlockBuffer fromBuffer
End Function

Function CopyTextureRectS( x, y, w, h, dx, dy, fromBuffer, toBuffer, transparent = 0 )
	Local sdx = dx
	LockBuffer fromBuffer
	LockBuffer toBuffer
	For i = y To y + (h - 1)
		For j = x To x + (w - 1)
			rgb = ReadPixelFast( j, i, fromBuffer ) And $FFFFFF 
			If rgb <> transparent 
				WritePixelFast dx, dy, rgb Or $FF000000, toBuffer 
			EndIf	
			dx = dx + 1
		Next
		dx = sdx
		dy = dy + 1
	Next	
	UnlockBuffer toBuffer
	UnlockBuffer fromBuffer
End Function

Function CopyTextureRectB( x, y, w, h, dx, dy, fromBuffer, toBuffer )
    Local sdx = dx
    LockBuffer fromBuffer
    LockBuffer toBuffer
    For i = y To y + (h - 1)
        For j = x To x + (w - 1)
            WritePixelFast dx, dy, ReadPixelFast( j, i, fromBuffer ), toBuffer 
            dx = dx + 1
        Next
        dx = sdx
        dy = dy + 1
    Next    
    UnlockBuffer toBuffer
    UnlockBuffer fromBuffer
End Function

Function FadeBlock( x, y, x1, y1, frombuffer, tobuffer, fadeR#, fadeG#=0, fadeB#=0 )
	LockBuffer frombuffer
	LockBuffer tobuffer
	If fadeB=0 Then fadeB=fadeR
	If fadeG=0 Then fadeG=fadeR
	For s1 = x To x + x1
		For s2 = y To y + y1
			rgb = ReadPixelFast(s1,s2,frombuffer) And $ffffff
			r = (rgb Shr 16) And 255
			g = (rgb Shr 8) And 255
			b = rgb And 255
			r = r * fadeR
			b = b * fadeB
			g = g * fadeG
			If r > 255 Then r = 255
			If g > 255 Then g = 255
			If b > 255 Then b = 255
			rgb = (r Shl 16) + (g Shl 8) + b
			WritePixelFast s1, s2, rgb, tobuffer
		Next
	Next
	UnlockBuffer frombuffer
	UnlockBuffer tobuffer
End Function

Function DrawProgressBar( backImage, frontImage, amount, flip = False )
    Local width = ImageWidth( backImage )
    Local height = ImageHeight( backImage )
    Local x = (GraphicsWidth() - width) / 2
    Local y = ((GraphicsHeight() - height) / 2)
    DrawBlockRect backImage, x, y, 0, 0, width, height
    DrawBlockRect frontImage, x, y, 0, 0, amount * 1.7, height
    If flip 
        Flip False
        Delay 0
    EndIf
End Function

Function LoadTextureNoFilters( filename$, flags = 5 )
    Local hndl
    ClearTextureFilters
    hndl = LoadTexture( filename, flags )
    TextureFilter "", 1 + 8
    Return hndl
End Function

Function LoadAnimTextureNoFilters( filename$, flags = 5, width, height, start, count )
    Local hndl
    ClearTextureFilters
    hndl = LoadAnimTexture( filename, flags, width, height, start, count )
    TextureFilter "", 1 + 8
    Return hndl
End Function

Function CreateQuad( parent = 0, lx = -1, mx = 1 )
	quad = CreateMesh( parent )
	s = CreateSurface( quad )
	AddVertex s,  lx,  mx, 0, 0, 0
	AddVertex s, -lx,  mx, 0, 1, 0
	AddVertex s,  lx, -mx, 0, 0, 1
	AddVertex s, -lx, -mx, 0, 1, 1
	AddTriangle s, 0, 1, 2 
	AddTriangle s, 3, 2, 1
	EntityFX quad, 1 + 16
	Return quad
End Function

Function CreateEmptyMesh( parent = 0 )
    mesh = CreateMesh( parent )
    surface = CreateSurface( mesh )
    Return mesh
End Function

Function AddMeshToSurface(SrcMesh, DestSurface, Offset_X#=0, Offset_Y#=0, Offset_Z#=0)
    If CountSurfaces( SrcMesh ) > 0
        SrcSurface = GetSurface(SrcMesh, 1)
        DestVerts = CountVertices(DestSurface)
        SrcVerts = CountVertices(SrcSurface)
        For VertLoop = 0 To SrcVerts-1
            Vx#  = VertexX#(SrcSurface, VertLoop)
            Vy#  = VertexY#(SrcSurface, VertLoop)
            Vz#  = VertexZ#(SrcSurface, VertLoop)
            Vu#  = VertexU#(SrcSurface, VertLoop)
            Vv#  = VertexV#(SrcSurface, VertLoop)       
            Vw#  = VertexW#(SrcSurface, VertLoop)
            Vnx# = VertexNX#(SrcSurface, VertLoop)
            Vny# = VertexNY#(SrcSurface, VertLoop)
            Vnz# = VertexNZ#(SrcSurface, VertLoop)                      
            Vr   = VertexRed(SrcSurface, VertLoop)
            Vg   = VertexGreen(SrcSurface, VertLoop)
            Vb   = VertexBlue(SrcSurface, VertLoop)
            AddVertex(DestSurface, Vx#+Offset_X#, Vy#+Offset_Y#, Vz#+Offset_Z#, Vu#, Vv#, Vw#)
            VertexNormal(DestSurface, VertLoop+DestVerts, Vnx#, Vny#, Vnz#)
            VertexColor(DestSurface, VertLoop+DestVerts, Vr, Vg, Vb) 
        Next
        SrcTris  = CountTriangles(SrcSurface)
        For TriLoop = 0 To SrcTris-1
            V0 = TriangleVertex(SrcSurface, TriLoop, 0)
            V1 = TriangleVertex(SrcSurface, TriLoop, 1)
            V2 = TriangleVertex(SrcSurface, TriLoop, 2)
            AddTriangle(DestSurface, V0+DestVerts, V1+DestVerts, V2+DestVerts)
        Next
    EndIf
End Function

Function HideEntityAndChildren( entity )
	Local i
	HideEntity entity
	For i = 1 To CountChildren( entity )
		HideEntityAndChildren( GetChild( entity, i ) )
	Next
End Function

Function ShowEntityAndChildren( entity )
	Local i
	ShowEntity entity
	For i = 1 To CountChildren( entity )
		ShowEntityAndChildren( GetChild( entity, i ) )
	Next
End Function

