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

Const SOUND_NOP = 0
Const SOUND_FADE_IN = 1
Const SOUND_FADE_OUT = 2
Const SOUND_PAN_LEFT = 3
Const SOUND_PAN_RIGHT = 4
Const SOUND_PAUSED = 5
Const SOUND_PLAYING = 6

Const SOUND_TYPE_MUSIC = 1
Const SOUND_TYPE_FX = 2

Type Sound
	Field file$
    Field userFlag
	Field buffer
	Field channel
	Field volume#
    Field volumeTarget#
	Field pan#
	Field freq
	Field isMusic
	Field action
	Field status
End Type

Function SoundLoad.Sound( file$, loop = False )
	snd.Sound = New Sound
	snd\file = file
	snd\buffer = LoadSound( file )
	If loop
		LoopSound snd\buffer
	EndIf
	snd\channel = 0
	snd\volume = 1.0
	snd\pan = 0
	snd\isMusic = False
	Return snd
End Function

Function MusicLoad.Sound( file$ )
	snd.Sound = New Sound
	snd\file = file
	snd\buffer = 0
	snd\channel = 0
	snd\volume = 1.0
	snd\pan = 0
	snd\isMusic = True
	Return snd
End Function

Function SoundFree( snd.Sound )
	If snd = Null Then Return
	If Not snd\isMusic
		FreeSound snd\buffer
	EndIf
	Delete snd
End Function

Function SoundVolumeAll( volume#, userFlag )
    For snd.Sound = Each Sound
        If snd\userFlag = userFlag
            snd\volume = volume
            If snd\isMusic
                ChannelVolume snd\channel, snd\volume
            Else
                SoundVolume snd\buffer, snd\volume
            EndIf    
        EndIf
    Next
End Function

Function SoundFreeAll()
	For snd.Sound = Each Sound
		SoundFree snd
	Next
End Function

Function SoundStopAll()
	For snd.Sound = Each Sound
		SoundStop snd
	Next
End Function

Function SoundResume( snd.Sound )
	ResumeChannel snd\channel
End Function

Function SoundPause( snd.Sound )
	PauseChannel snd\channel
End Function

Function SoundStop( snd.Sound )
	StopChannel snd\channel
	snd\channel = 0
End Function

Function SoundPlay( snd.Sound, reset = False )
	If snd\isMusic
		If SoundPlaying( snd )
			SoundStop snd
		EndIf
		snd\channel = PlayMusic( snd\file )
	Else
		If Not SoundPlaying( snd )
			snd\channel = PlaySound( snd\buffer )
		Else
			If reset
				SoundStop snd
				snd\channel = PlaySound( snd\buffer )
			Else	
				SoundResume snd
			EndIf	
		EndIf	
	EndIf	
End Function

Function SoundUpdate( snd.Sound )
    If snd\isMusic Or SoundPlaying( snd )
	    ChannelVolume snd\channel, snd\volume
	    ChannelPan snd\channel, snd\pan
    Else
        SoundVolume snd\buffer, snd\volume
        SoundPan snd\buffer, snd\pan
    EndIf
End Function

Function SoundPlaying( snd.Sound )
	Return ChannelPlaying( snd\channel )
End Function

Function SoundFadeIn( snd.Sound, target# = 0.00 )
	snd\action = SOUND_FADE_IN
	snd\volumeTarget = target
End Function

Function SoundFadeOut( snd.Sound, target# = 1.00 )
	snd\action = SOUND_FADE_OUT
	snd\volumeTarget = target
End Function

Function SoundPanLeft( snd.Sound )
	snd\action = SOUND_PAN_LEFT
End Function

Function SoundPanRight( snd.Sound )
	snd\action = SOUND_PAN_RIGHT
End Function

Function SoundFrequency( snd.Sound, freq )
	snd\freq = freq
	ChannelPitch snd\channel, snd\freq
End Function

Function UpdateSounds()
	For snd.Sound = Each Sound
		If snd\isMusic
			If Not SoundPlaying( snd )
				
			EndIf
		EndIf
		Select snd\action
			Case SOUND_NOP
			Case SOUND_FADE_IN
				snd\volume = snd\volume + .05
				If snd\volume >= snd\volumeTarget 
					snd\action = SOUND_NOP
				EndIf
			Case SOUND_FADE_OUT
				snd\volume = snd\volume - .05
				If snd\volume < volumeTarget
					snd\action = SOUND_NOP
				EndIf
			Case SOUND_PAN_LEFT
				snd\pan = snd\pan - .05
				If snd\pan < -1.0
					snd\action = SOUND_NOP
				EndIf
			Case SOUND_PAN_RIGHT
				snd\pan = snd\pan + .05
				If snd\pan > 1.0
					snd\action = SOUND_NOP
				EndIf
		End Select
		If snd\action <> SOUND_NOP
			SoundUpdate snd
		EndIf
	Next
End Function
