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

Const WRAPPER_DLL$ = "beepak.dll"
Const ERAR_END_ARCHIVE = 10
Const ERAR_NO_MEMORY = 11
Const ERAR_BAD_DATA = 12
Const ERAR_BAD_ARCHIVE = 13
Const ERAR_UNKNOWN_FORMAT = 14
Const ERAR_EOPEN = 15
Const ERAR_ECREATE = 16
Const ERAR_ECLOSE = 17
Const ERAR_EREAD = 18
Const ERAR_EWRITE = 19
Const ERAR_SMALL_BUF = 20
Const ERAR_UNKNOWN = 21

Type RarFile
	Field path$
	Field extracted
	Field isDirectory
End Type

Global g_inBank
Global g_outBank
Global g_archiveFile$
Global g_currentFile$
Global g_isDirectory
Global g_textureExtension[8]

.texture_extensions
Data ".png", ".jpg", ".bmp", ".PNG", ".JPG", ".BMP"

Function RarInitialize()
	g_inBank = CreateBank( 1024 )
	g_outBank = CreateBank( 1024 )
    Restore texture_extensions
    For i = 0 To 7
        Read extension$
        g_textureExtension[i] = RarExtensionToInt( extension )
    Next
End Function

Function RarShutdown()
	FreeBank g_inBank
	FreeBank g_outBank
End Function

Function RarSetArchiveFile( filename$ )
	g_archiveFile = filename$
End Function

Function RarSetPassword( password$ )
	PokeString g_inBank, password
	Return CallDLL( WRAPPER_DLL, "RAR_SetPassword", g_inBank, g_outBank )
End Function

Function RarCatalogArchive()
	Local result
	RarOpenArchive
	Repeat
		result = RarReadNextHeader()
		If result = 0
			file.RarFile = New RarFile
			file\path = g_currentFile
			file\isDirectory = g_isDirectory
			file\extracted = False
		EndIf	
	Until result <> 0
	RarCloseArchive
End Function

Function RarOpenArchive()
	Local result
	Local message$
	PokeString g_inBank, g_archiveFile
	result = CallDLL( WRAPPER_DLL, "RAR_OpenArchive", g_inBank, g_outBank )
	message = PeekString( g_outBank )
	If result <> 0
		RuntimeError message
	EndIf
	Return result
End Function

Function RarCloseArchive()
	Local result
	result = CallDLL( WRAPPER_DLL, "RAR_CloseArchive" )
	Return result
End Function

Function RarExtractFile$( filename$ )
	Local result
	Local message$
	file.RarFile = RarFindFile( filename )
	If file <> Null
		If file\extracted Then Return filename
		RarOpenArchive
		Repeat
			result = RarReadNextHeader()
			If g_currentFile = filename
				PokeString g_inBank, filename
				If CallDLL( WRAPPER_DLL, "RAR_ExtractCurrentHeader", g_inBank, g_outBank ) <> 0
					message = PeekString( g_outBank )
					RuntimeError message
				Else
                    Local extension$ = Upper( Mid( filename, RightFirstPeriod( filename ) ) )
					file\extracted = True
					RarMarkDirectoryChain file
					RarCloseArchive
                    If extension = ".3DS" Or extension = ".MD2" Or extension = ".X" Or extension = ".B3D"
                        RarExplodeTextureFiles filename
                    EndIf
					Return filename
				EndIf	
			EndIf
		Until result <> 0
		RarCloseArchive
	EndIf	
	RuntimeError "File " + filename + " not found in archive " + g_archiveFile
End Function

Function RarReadNextHeader$()
	Local result
	Local message$
	result = CallDLL( WRAPPER_DLL, "RAR_ReadNextHeader", g_inBank, g_outBank )
	g_currentFile = PeekString( g_outBank )
	If Left( g_currentFile, 3 ) = "(D)"
		g_currentFile = Mid( g_currentFile, 4 )
		g_isDirectory = True
	Else
		g_isDirectory = False
	EndIf
	Return result
End Function

Function RarRemoveFiles()
	Local file.RarFile = Last RarFile
	While file <> Null
		If file\extracted
			If file\isDirectory
				DeleteDir file\path
			Else
				DeleteFile file\path
			EndIf
			file\extracted = False
		EndIf
		file = Before file
	Wend
End Function

Function RarMarkDirectoryChain( file.RarFile )
	Local offset
	offset = RightFirstSlash( file\path )
	If offset = 0 Then Return
	offset = offset - 1
	file = RarFindFile( Left( file\path, offset ) )
	file\extracted = True
	RarMarkDirectoryChain file
	Return
End Function

Function RarFindFile.RarFile( path$ )
	For file.RarFile = Each RarFile
		If file\path = path
			Return file
		EndIf	
	Next
	Return Null
End Function

Function RarExplodeTextureFiles( filename$ )
    Local fileHandle
    Local fileSize
    Local bank
    Local extensions[8]
    Local files$[250]
    Local extractCount = 0

    fileHandle = ReadFile( filename$ )
    If fileHandle = 0
        Return
    EndIf
    fileSize = FileSize( filename$ )
    bank = CreateBank( fileSize + 4 )
    ReadBytes bank, fileHandle, 0, fileSize
    CloseFile fileHandle
    For i = 0 To fileSize
        extension = PeekInt( bank, i )
        For j = 0 To 7
            If extension = g_textureExtension[j]
                Local offset = i + 3
                Local byte = 0
                Local textureFilename$ = ""
                Local foundTexture = False
                Repeat
                    If offset <= 0 Then Exit
                    byte = PeekByte( bank, offset )
                    If byte = 0 Or byte = 34 Or byte < 31 Or byte > 129
                        Exit
                    EndIf
                    textureFilename = textureFilename + Chr( byte )
                    offset = offset - 1
                Forever
                If Len( textureFilename ) > 0
                    textureFilename = ReverseString( textureFilename )
                    textureFilename = Mid( filename, 1, RightFirstSlash( filename ) ) + textureFilename
                    For k = 0 To extractCount
                        If files[k] = textureFilename
                            foundTexture = True
                            Exit
                        EndIf
                    Next    
                    If foundTexture = False
                        If extractCount < 250
                            files[extractCount] = textureFilename
                            extractCount = extractCount + 1
                        Else
                            RuntimeError "UnRar: Can only auto extract 250 textures per model."
                        EndIf
                    EndIf    
                    Exit
                EndIf        
            EndIf
        Next
    Next
    If extractCount > 0
        For i = 0 To extractCount - 1
            RarExtractFile( files[i] )
        Next
    EndIf    
    FreeBank bank
End Function

Function RarExtensionToInt( extension$ )
    Local bank
    Local value = 0
    bank = CreateBank( 4 )
    PokeByte bank, 0, Asc( Mid( extension, 1, 1 ) )
    PokeByte bank, 1, Asc( Mid( extension, 2, 1 ) )
    PokeByte bank, 2, Asc( Mid( extension, 3, 1 ) )
    PokeByte bank, 3, Asc( Mid( extension, 4, 1 ) )
    value = PeekInt( bank, 0 )
    FreeBank bank
    Return value
End Function