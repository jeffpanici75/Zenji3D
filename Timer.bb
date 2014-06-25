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

Type Timer
	Field startTick
	Field lastTick
	Field tickDuration
	Field lifeTime
End Type

Function StartTimer.Timer( duration, lifetime = 0 )
	timer.Timer = New Timer
	timer\tickDuration = duration
	timer\lifeTime = lifetime
	ResetTimer( timer )
	Return timer
End Function

Function ResetTimer( timer.Timer )
	timer\startTick = Millisecs()
	timer\lastTick = Millisecs()
End Function

Function CheckTimer( timer.Timer, durationOverride=0 )
	If timer = Null Return False
	ms = Millisecs()
	If durationOverride > 0 
		checkDuration = durationOverride
	Else
		checkDuration = timer\tickDuration
	EndIf
	If( ms - timer\lastTick ) > checkDuration
		timer\lastTick = ms + 2
		Return True
	Else
		Return False
	EndIf
End Function

Function TimerExpired( timer.Timer )
	If timer = Null Return False
	If timer\lifeTime > 0 And (Millisecs() - timer\startTick > timer\lifeTime) 
		Return True
	Else
		Return False
	EndIf
End Function
