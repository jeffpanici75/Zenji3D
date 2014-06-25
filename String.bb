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

Global g_delimitChar$ = " "

Function ZeroPadLeft$( value, length )
	Local s$ = Str(value)
	Return String$( "0", length - Len(s) ) + s
End Function

Function SetDelimitChar( char$ )
	g_delimitChar = char
End Function

Function Words( string$ )
	Local offset = 0
	Local count = 0
	Local work$ = string
	If Len( work ) > 0
		work = work + g_delimitChar
	EndIf	
	Repeat
		offset = Instr( work, g_delimitChar, offset + 1 )
		If offset <> 0 Then count = count + 1
	Until offset = 0
	Return count
End Function

Function Word$( string$, n )
	Local offset = 0
	Local lastOffset = 1
	Local count = 1
	Local work$ = string
	If Len( work ) > 0
		work = work + g_delimitChar
	EndIf
	Repeat
		offset = Instr( work, g_delimitChar, offset + 1 )
		If offset <> 0
			If count = n
				Return Mid( work, lastOffset, offset - lastOffset )
			Else
				count = count + 1
			EndIf	
		EndIf
		lastOffset = (offset + 1)
	Until offset = 0
	Return ""
End Function

Function PokeString( bank, s$ )
	For i=1 To Len( s$ )
		PokeByte bank, i - 1, Asc(Mid$(s$,i,1))
	Next
	PokeByte bank, i - 1, 0
End Function

Function PeekString$( bank )
	Local byte
	Local offset = 0
	Local result$ = ""
	Repeat
		byte = PeekByte( bank, offset )
		If byte <> 0
			result = result + Chr$( byte )
			offset = offset + 1
		Else
			Exit
		EndIf
	Forever	
	Return result
End Function

Function RightFirstSlash( s$ )
	Local offset = Len( s$ )
	While Mid( s$, offset, 1 ) <> "/" And Mid( s$, offset, 1 ) <> "\"
		offset = offset - 1
		If offset = 0 Then Exit
	Wend
	Return offset
End Function

Function RightFirstPeriod( s$ )
    Local offset = Len( s$ )
    While Mid( s$, offset, 1 ) <> "."
        offset = offset - 1
        If offset = 0 Then Exit
    Wend
    Return offset
End Function

Function ReverseString$( s$ )
    Local offset = Len( s$ )
    Local rev$ = ""
    While offset > 0
        rev = rev + Mid( s$, offset, 1 )
        offset = offset - 1
    Wend
    Return rev$
End Function
