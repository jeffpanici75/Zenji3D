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

Const ZEN_CHARSET$ = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!?.,;:-@"

Type ZenKoan
    Field text$
    Field author$
End Type

Global g_zenKoans.ZenKoan[100]
Global g_numKoans
Global g_scrollSprite.Sprite
Global g_zen16b.BitmapFont
Global g_scrollImage
Global g_koanTimer.Timer
Global g_gameFlag
Global g_newGameFlag

.koans
Data "-There is no end.\nThere is no beginning.\nThere is only the infinite\npassion of life.-\n","Fredrico Fellini"
Data "-Everything is perfect,\nbut there is a lot of\nroom for improvement.-\n", "Shunryu Suzuki"
Data "-It is glorious the things\nan empty self sees!\nI walk the stream to its\nvery source, sit and watch\nthe clouds rise...If by chance\nI meet an old woodsman,\nwe talk and laugh,\nno rush to get home.-\n", "Wang wei"
Data "-To enjoy the world without\njudgement is what a realized\nlife is like.-\n", "Charlotte Joko Beck"
Data "-I am free when\nI am within myself.-\n", "George Wilhelm Friedrich Hegel"
Data "-A superior vessel\ntakes a long time\nto complete.-\n", "Zen Proverb"
Data "-I went and I returned.\nIt was nothing special.\nRozan famous for its misty\nmountains; Sekko for\nits water.-\n", "Chinese Saying"
Data "-Ordinary men hate solitude.\nBut the Master makes\nuse of it, embracing his\naloneness, realizing he is one\nwith the whole universe-.\n", "Lao tsu"
Data "-If the wrong person preaches\na right teaching, even a right\nteaching becomes wrong.\nIf the right person\nexpounds a wrong teaching,\neven a wrong teaching\nbecomes right.-\n", "Muso Kokushi"
Data "-The birds have vanished\ninto the sky, and now\nthe last cloud drains away.\nWe sit together, the mountain\nand me, until only\nthe mountain remains.-\n", "Li Po"
Data "-It is not the answer\nthat enlightens,\nbut the question.-\n", "Eugene Ionesco"
Data "-Zen is the vehicle\nof reality.-\n", "Jim Harrison"
Data "-The wind blows hard\namong the pines\nToward the beginning\nOf an edless past.\nListen: you have\nheard everything.-\n", "Shinkichi Takahashi"
Data "-Catch the vigorous horse\nof your mind.-\n", "Zen saying"
Data "-The tighter you squeeze,\nthe less you have.-\n", "Zen saying"
Data "-Learning Zen is a phenomenon\nof gold and dung.\nBefore you understand it,\nit is like gold;\nafter you understand it,\nit is like dung.-\n", "Zen saying"
Data "-No thought, no relfection,\nno analysis,\nno cultivation,\nno intention;\nlet it settle itself.-\n", "Tilopa"
Data "-When you pass through,\nno one can pin you down,\nno one can call you back.-\n", "Ying An"
Data "-The only Zen you find on\nthe tops of mountains is the\nZen you bring up there.-\n", "Robert M. Pirsig"
Data "-Man stands in his own shadow\nand wonders why it is dark.-\n", "Zen proverb"
Data "-Sit quietly, doing nothing\nspring comes, and\nthe grass grows by itself.-\n", "Zen proverb"
Data "-More important than learning\nhow to recall things\nis finding ways to\nforget things that are\ncluttering the mind.-\n", "Eric Butterworth"
Data "-The mind of the past is\nungraspable; The mind of the\nfuture is ungraspable;\nThe mind of the present\nis ungraspable.\n", "Diamond Sutra"
Data "-Out beyond ideas of\nwrong doing and right\ndoing there is a field.\nI'll meet you there.-\n", "Jelaluddin Rumi"
Data "-Nothing is more real\nthan nothing.-\n", "Samuel Beckett"
Data "-The meaning of life\nis to see.-\n", "Hui neng"
Data "-Old pond, frog jumps in;\nplop.-\n", "Basho"
Data "-Zen is like looking for\nspectacles that are\nsitting on your nose.-\n", "Zen saying"
Data "-When you can do nothing\nwhat can you do?-\n", "Zen koan"
Data "-I am what I am\nand that is all\nthat I am.-\n", "Popeye"
Data "-In an utter emptiness\nanything can take place.-\n", "John Cage"
Data "-If you gaze for long into the\nabyss, the abyss also\ngazes into you.-\n","Friedrich Nietzsche"
Data "-Every moment is nothing\nwithout end.-\n", "Octavio Paz"
Data "-Each day should be passed\nas though it were our last.-\n", "Pubilius Syrus"
Data "-We dance around in a ring\nand suppose, but the Secret\nsits in the middle and knows.-\n", "Robert Frost"
Data "-The obstacle is the path.-\n", "Zen proverb"
Data "-He who knows how to\nshave the razor, will know\nhow to erase the eraser.-\n", "Henri Michaux"
Data "-There is no spoon.-\n", "Neo"
Data "-Stop trying to hit me\nand hit me.-\n", "Morpheous"
Data "###"

Function InitializeZenKoans( reload = False )
    Local count = 0
    If reload
        Delete g_scrollSprite
        Delete g_zen16b
    EndIf
    g_zen16b = LoadBitmapFont( RarExtractFile( "res\bitmaps\zen-font16.png" ), ZEN_CHARSET, 0 )
    g_scrollImage = LoadImage( RarExtractFile( "res\bitmaps\rice-paper.png" ) )
    g_scrollSprite = SpriteCreate( (GraphicsWidth() - 512) / 2, (GraphicsHeight() - 256) / 2, 512, 256, tempTex )
    SetSpriteHandle g_scrollSprite, -1, -1
    HideSprite g_scrollSprite
    If Not reload
        Restore koans
        Repeat
            Read text$
            If text = "###" Then Exit
            Read author$
            koan.ZenKoan = New ZenKoan
            koan\text = text
            koan\author = author
            g_zenKoans[count] = koan
            count = count + 1
        Forever
        g_numKoans = count
    EndIf    
End Function

Function StartZenKoan( flag, newGame ) 
    ShowZenKoan
    g_koanTimer = StartTimer( 3500, 5000 )
    CameraOverWater
    ShowSprite g_scrollSprite
    SetSpriteFadeIn g_scrollSprite
    g_gameFlag = flag
    g_newGameFlag = newGame
End Function

Function UpdateZenKoan()
    If TimerExpired( g_koanTimer )
        Delete g_koanTimer
        SetGameMode g_gameFlag, g_newGameFlag
    Else
        If CheckTimer( g_koanTimer )
            SetSpriteFadeOut g_scrollSprite
        EndIf
    EndIf
End Function

Function ShowZenKoan()
    Local width = ImageWidth( g_scrollImage )
    Local height = ImageHeight( g_scrollImage )
    Local srcBuffer = ImageBuffer( g_scrollImage )
    Local destBuffer = TextureBuffer( g_scrollSprite\texture )
    Local offsety = 31
    Local line$ = ""
    CopyTextureRect 0, 0, width, height, 0, 0, srcBuffer, destBuffer, $000000
    koan.ZenKoan = g_zenKoans[Rand( 0, g_numKoans - 1)]
    For i = 1 To Len( koan\text )
        char$ = Mid( koan\text, i, 1 )
        If char$ = "\"
            If Mid( koan\text, i + 1, 1 ) = "n"
                DrawTextureFont g_zen16b, 15, offsety, line, g_scrollSprite\texture
                offsety = offsety + 17
                line = ""
            EndIf
            i = i + 1
        EndIf
        line = line + char
    Next
    DrawTextureFont g_zen16b, (500 - (Len( koan\author ) * 16)), 214, koan\author, g_scrollSprite\texture
End Function
