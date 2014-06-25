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

Type CommandLineOption
	Field name$
	Field value$
End Type

Function ParseCommandLine()
	Local params$ = CommandLine$()
	For i = 1 To Words( params )
		If Word( params, i ) = "-fps"
			command.CommandLineOption = New CommandLineOption
			command\name = "-fps"
			command\value = "True"
		ElseIf Word( params, i ) = "-debug"
			command.CommandLineOption = New CommandLineOption
			command\name = "-debug"
			command\value = "True"
		ElseIf Word( params, i ) = "-window"
			command.CommandLineOption = New CommandLineOption
			command\name = "-window"
			command\value = "True"
		ElseIf Word( params, i ) = "-resw" Or Word( params, i ) = "-resh" Or Word( params, i ) = "-resd"
			command.CommandLineOption = New CommandLineOption
			command\name = Word( params, i )
			i = i + 1
			command\value = Word( params, i )
		ElseIf Word( params, i ) = "-level"
			command.CommandLineOption = New CommandLineOption
			command\name = Word( params, i )
			i = i + 1
			command\value = Word( params, i )
		EndIf
	Next	
End Function

Function GetOptionValue$( name$ )
	For option.CommandLineOption = Each CommandLineOption
		If option\name = name
			Return option\value
		EndIf	
	Next
	Return ""
End Function
