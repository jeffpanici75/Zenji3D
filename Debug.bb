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

Global g_debugFile = 0

Function OpenDebugFile()
	g_debugFile = WriteFile( "debug.log" )
	WriteLine g_debugFile, "Debug log open on " + CurrentDate() + " @ " + CurrentTime() + " -------------------------------------"
	WriteLine g_debugFile, ""
End Function

Function CloseDebugFile()
	WriteLine g_debugFile, ""
	WriteLine g_debugFile, "Debug log closed on " + CurrentDate() + " @ " + CurrentTime() + " -----------------------------------"
	CloseFile g_debugFile
End Function

Function DebugWrite( s$ )
	If g_debugFile <> 0 
		WriteLine g_debugFile, s$
	EndIf	
End Function
