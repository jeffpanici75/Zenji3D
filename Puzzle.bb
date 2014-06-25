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

Const MAX_PUZZLE_PIECES = 500

Const PUZZLE_PIECE_S = 0
Const PUZZLE_PIECE_T = 1
Const PUZZLE_PIECE_L = 2
Const PUZZLE_PIECE_N = 3
Const PUZZLE_PIECE_B = 4

Const PUZZLE_SHAPE_RECTANGLE = 0
Const PUZZLE_SHAPE_CIRCLE = 1
Const PUZZLE_SHAPE_TRIANGLE = 2
Const PUZZLE_SHAPE_PENTAGON = 3
Const PUZZLE_SHAPE_HEXAGON = 4
Const PUZZLE_SHAPE_OCTAGON = 5
Const PUZZLE_SHAPE_DODECAGON = 6
Const PUZZLE_SHAPE_STAR_5 = 7
Const PUZZLE_SHAPE_STAR_4 = 8

Const LEVEL_EASY = 1
Const LEVEL_MEDIUM = 2
Const LEVEL_HARD = 3

Type Puzzle
	Field pieces.PuzzlePiece[MAX_PUZZLE_PIECES]
	Field nbrIllusions
    Field illusionsSpark
    Field seconds
    Field shape
	Field width
	Field height
    Field elbows
    Field straights
    Field seed
	Field px
	Field py
	Field sx
	Field sy
End Type

Type PuzzlePiece
	Field piece
	Field rotation
	Field rotDir
	Field rotUnits#
	Field isRotating
	Field validMoves[4]
	Field entity
	Field visited
	Field lastMove
	Field bonus
End Type

Global g_puzzles.Puzzle[100]
Global g_puzzleCount
Global g_totalPieces

Dim g_pipeRotations( 3, 4, 5 )
Dim g_validMotion( 3, 4, 5 )

;          Right  Left   Up     Down
.s_rotations
Data    0, False, False, True,  True
Data  -90, True,  True,  False, False
Data -180, False, False, True,  True
Data   90, True,  True,  False, False

.t_rotations
Data    0, True,  False, True,  True
Data  -90, True,  True,  False, True
Data -180, False, True,  True,  True
Data   90, True,  True,  True,  False

.l_rotations
Data    0, True,  False, False, True
Data  -90, False, True,  False, True
Data -180, False, True,  True,  False
Data   90, True,  False, True,  False

;          Seq Level        Seed  shape                    w   h  px py sx sy elbows straight secs # fire  spark
.puzzles
#ifdef DEMO_BUILD
    ; Zen Student Level
    Data     1,LEVEL_EASY  ,   1, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    30,    0, False
    Data     2,LEVEL_EASY  ,   2, PUZZLE_SHAPE_RECTANGLE,  4,  5,  2, 2, 2, 2,     2,      1,    30,    1, False
    Data     3,LEVEL_EASY  ,   3, PUZZLE_SHAPE_RECTANGLE,  6,  5,  3, 3, 3, 3,     2,      2,    50,    1, False
    Data     4,LEVEL_EASY  ,   4, PUZZLE_SHAPE_RECTANGLE,  6,  7,  3, 3, 3, 3,     3,      2,    50,    1, False
    Data     5,LEVEL_EASY  ,   5, PUZZLE_SHAPE_RECTANGLE,  8,  5,  4, 3, 4, 3,     3,      2,    55,    1, True
    Data     6,LEVEL_EASY  ,   6, PUZZLE_SHAPE_RECTANGLE,  8,  7,  4, 3, 4, 3,     5,      4,    55,    1, True
    Data     7,LEVEL_EASY  ,   7, PUZZLE_SHAPE_RECTANGLE, 10,  5,  5, 2, 5, 2,     5,      4,    60,    1, True
    Data     8,LEVEL_EASY  ,   8, PUZZLE_SHAPE_RECTANGLE, 10,  7,  5, 4, 5, 4,     6,      5,    75,    1, True
    Data     9,LEVEL_EASY  ,   9, PUZZLE_SHAPE_RECTANGLE, 10,  9,  5, 4, 5, 4,     8,      6,    85,    2, True
    Data    10,LEVEL_EASY  ,  10, PUZZLE_SHAPE_RECTANGLE, 12,  9,  6, 4, 6, 4,    10,      8,    95,    2, True

    ; Zen Apprentice Level
    Data     1,LEVEL_MEDIUM,  11, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     3,      2,    30,    0, False
    Data     2,LEVEL_MEDIUM,  12, PUZZLE_SHAPE_RECTANGLE,  4,  5,  2, 2, 2, 2,     4,      3,    30,    1, False
    Data     3,LEVEL_MEDIUM,  13, PUZZLE_SHAPE_RECTANGLE,  6,  5,  3, 3, 3, 3,     4,      4,    50,    1, False
    Data     4,LEVEL_MEDIUM,  14, PUZZLE_SHAPE_RECTANGLE,  6,  7,  3, 3, 3, 3,     4,      4,    50,    1, False
    Data     5,LEVEL_MEDIUM,  17, PUZZLE_SHAPE_RECTANGLE,  8,  5,  4, 3, 4, 3,     8,      5,    55,    1, False
    Data     6,LEVEL_MEDIUM,  16, PUZZLE_SHAPE_RECTANGLE,  8,  7,  4, 3, 4, 3,     8,      5,    55,    2, True
    Data     7,LEVEL_MEDIUM,  18, PUZZLE_SHAPE_RECTANGLE, 10,  5,  5, 2, 5, 2,     8,      6,    60,    2, True
    Data     8,LEVEL_MEDIUM,  19, PUZZLE_SHAPE_RECTANGLE, 10,  5,  5, 4, 5, 4,     8,      6,    75,    2, True
    Data     9,LEVEL_MEDIUM,  20, PUZZLE_SHAPE_RECTANGLE, 12,  9,  6, 4, 6, 4,    10,      9,    85,    3, True
    Data    10,LEVEL_MEDIUM,  21, PUZZLE_SHAPE_RECTANGLE, 12,  9,  6, 4, 6, 4,    12,      9,    95,    3, True

    ; Zen Master Level
    Data     1,LEVEL_HARD  ,  21, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     3,      3,    30,    0, False
    Data     2,LEVEL_HARD  ,  22, PUZZLE_SHAPE_RECTANGLE,  4,  5,  2, 2, 2, 2,     4,      3,    30,    1, False
    Data     3,LEVEL_HARD  ,  23, PUZZLE_SHAPE_RECTANGLE,  6,  5,  3, 3, 3, 3,     4,      4,    50,    1, False
    Data     4,LEVEL_HARD  ,  24, PUZZLE_SHAPE_RECTANGLE,  6,  7,  3, 3, 3, 3,     4,      5,    50,    2, True
    Data     5,LEVEL_HARD  ,  26, PUZZLE_SHAPE_RECTANGLE,  8,  5,  4, 3, 4, 3,     8,      6,    55,    2, True
    Data     6,LEVEL_HARD  ,  26, PUZZLE_SHAPE_RECTANGLE,  8,  7,  4, 3, 4, 3,     8,      7,    55,    2, True
    Data     7,LEVEL_HARD  ,  27, PUZZLE_SHAPE_RECTANGLE, 10,  5,  5, 2, 5, 2,     8,      8,    60,    2, True
    Data     8,LEVEL_HARD  ,  28, PUZZLE_SHAPE_RECTANGLE, 10,  7,  5, 4, 5, 4,    10,      9,    75,    3, True
    Data     9,LEVEL_HARD  ,  29, PUZZLE_SHAPE_RECTANGLE, 12,  9,  6, 4, 6, 4,    12,     10,    85,    3, True
    Data    10,LEVEL_HARD  ,  30, PUZZLE_SHAPE_RECTANGLE, 12,  9,  6, 4, 6, 4,    14,     11,    95,    3, True
#else
    ; Zen Student Level
    Data     1,LEVEL_EASY  ,   1, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data     2,LEVEL_EASY  ,   2, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data     3,LEVEL_EASY  ,   3, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data     4,LEVEL_EASY  ,   4, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data     5,LEVEL_EASY  ,   6, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data     6,LEVEL_EASY  ,   6, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data     7,LEVEL_EASY  ,   7, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data     8,LEVEL_EASY  ,   8, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data     9,LEVEL_EASY  ,   9, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    10,LEVEL_EASY  ,  10, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    11,LEVEL_EASY  ,  11, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    12,LEVEL_EASY  ,  12, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    13,LEVEL_EASY  ,  13, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    14,LEVEL_EASY  ,  14, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    15,LEVEL_EASY  ,  15, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    16,LEVEL_EASY  ,  16, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    17,LEVEL_EASY  ,  17, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    18,LEVEL_EASY  ,  18, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    19,LEVEL_EASY  ,  19, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    20,LEVEL_EASY  ,  20, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    21,LEVEL_EASY  ,  21, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    22,LEVEL_EASY  ,  22, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    23,LEVEL_EASY  ,  23, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    24,LEVEL_EASY  ,  24, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    25,LEVEL_EASY  ,  25, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    26,LEVEL_EASY  ,  26, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    27,LEVEL_EASY  ,  27, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    28,LEVEL_EASY  ,  28, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    29,LEVEL_EASY  ,  29, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    30,LEVEL_EASY  ,  30, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    31,LEVEL_EASY  ,  31, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    32,LEVEL_EASY  ,  32, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    33,LEVEL_EASY  ,  33, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    34,LEVEL_EASY  ,  34, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    35,LEVEL_EASY  ,  35, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    36,LEVEL_EASY  ,  36, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    37,LEVEL_EASY  ,  37, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    38,LEVEL_EASY  ,  38, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    39,LEVEL_EASY  ,  39, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    40,LEVEL_EASY  ,  40, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    41,LEVEL_EASY  ,  41, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    42,LEVEL_EASY  ,  42, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    43,LEVEL_EASY  ,  43, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    44,LEVEL_EASY  ,  44, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    45,LEVEL_EASY  ,  45, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    46,LEVEL_EASY  ,  46, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    47,LEVEL_EASY  ,  47, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    48,LEVEL_EASY  ,  48, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    49,LEVEL_EASY  ,  49, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    50,LEVEL_EASY  ,  50, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    51,LEVEL_EASY  ,  51, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    52,LEVEL_EASY  ,  52, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    53,LEVEL_EASY  ,  53, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    54,LEVEL_EASY  ,  54, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    55,LEVEL_EASY  ,  55, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    56,LEVEL_EASY  ,  56, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    57,LEVEL_EASY  ,  57, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    58,LEVEL_EASY  ,  58, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    59,LEVEL_EASY  ,  59, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    60,LEVEL_EASY  ,  60, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    61,LEVEL_EASY  ,  61, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    62,LEVEL_EASY  ,  62, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    63,LEVEL_EASY  ,  63, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    64,LEVEL_EASY  ,  64, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    65,LEVEL_EASY  ,  65, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    66,LEVEL_EASY  ,  66, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    67,LEVEL_EASY  ,  67, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    68,LEVEL_EASY  ,  68, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    69,LEVEL_EASY  ,  69, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    70,LEVEL_EASY  ,  70, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    71,LEVEL_EASY  ,  71, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    72,LEVEL_EASY  ,  72, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    73,LEVEL_EASY  ,  73, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    74,LEVEL_EASY  ,  74, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    75,LEVEL_EASY  ,  75, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    76,LEVEL_EASY  ,  76, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    77,LEVEL_EASY  ,  77, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    78,LEVEL_EASY  ,  78, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    79,LEVEL_EASY  ,  79, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    80,LEVEL_EASY  ,  80, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    81,LEVEL_EASY  ,  81, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    82,LEVEL_EASY  ,  82, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    83,LEVEL_EASY  ,  83, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    84,LEVEL_EASY  ,  84, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    85,LEVEL_EASY  ,  85, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    86,LEVEL_EASY  ,  86, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    87,LEVEL_EASY  ,  87, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    88,LEVEL_EASY  ,  88, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    89,LEVEL_EASY  ,  89, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    90,LEVEL_EASY  ,  90, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    91,LEVEL_EASY  ,  91, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    92,LEVEL_EASY  ,  92, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    93,LEVEL_EASY  ,  93, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    94,LEVEL_EASY  ,  94, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    95,LEVEL_EASY  ,  95, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    96,LEVEL_EASY  ,  96, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    97,LEVEL_EASY  ,  97, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    98,LEVEL_EASY  ,  98, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    99,LEVEL_EASY  ,  99, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data   100,LEVEL_EASY  , 100, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False

    ; Zen Apprentice Level
    Data     1,LEVEL_MEDIUM,   1, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data     2,LEVEL_MEDIUM,   2, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data     3,LEVEL_MEDIUM,   3, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data     4,LEVEL_MEDIUM,   4, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data     5,LEVEL_MEDIUM,   6, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data     6,LEVEL_MEDIUM,   6, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data     7,LEVEL_MEDIUM,   7, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data     8,LEVEL_MEDIUM,   8, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data     9,LEVEL_MEDIUM,   9, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    10,LEVEL_MEDIUM,  10, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    11,LEVEL_MEDIUM,  11, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    12,LEVEL_MEDIUM,  12, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    13,LEVEL_MEDIUM,  13, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    14,LEVEL_MEDIUM,  14, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    15,LEVEL_MEDIUM,  15, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    16,LEVEL_MEDIUM,  16, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    17,LEVEL_MEDIUM,  17, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    18,LEVEL_MEDIUM,  18, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    19,LEVEL_MEDIUM,  19, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    20,LEVEL_MEDIUM,  20, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    21,LEVEL_MEDIUM,  21, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    22,LEVEL_MEDIUM,  22, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    23,LEVEL_MEDIUM,  23, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    24,LEVEL_MEDIUM,  24, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    25,LEVEL_MEDIUM,  25, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    26,LEVEL_MEDIUM,  26, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    27,LEVEL_MEDIUM,  27, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    28,LEVEL_MEDIUM,  28, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    29,LEVEL_MEDIUM,  29, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    30,LEVEL_MEDIUM,  30, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    31,LEVEL_MEDIUM,  31, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    32,LEVEL_MEDIUM,  32, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    33,LEVEL_MEDIUM,  33, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    34,LEVEL_MEDIUM,  34, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    35,LEVEL_MEDIUM,  35, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    36,LEVEL_MEDIUM,  36, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    37,LEVEL_MEDIUM,  37, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    38,LEVEL_MEDIUM,  38, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    39,LEVEL_MEDIUM,  39, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    40,LEVEL_MEDIUM,  40, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    41,LEVEL_MEDIUM,  41, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    42,LEVEL_MEDIUM,  42, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    43,LEVEL_MEDIUM,  43, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    44,LEVEL_MEDIUM,  44, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    45,LEVEL_MEDIUM,  45, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    46,LEVEL_MEDIUM,  46, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    47,LEVEL_MEDIUM,  47, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    48,LEVEL_MEDIUM,  48, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    49,LEVEL_MEDIUM,  49, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    50,LEVEL_MEDIUM,  50, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    51,LEVEL_MEDIUM,  51, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    52,LEVEL_MEDIUM,  52, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    53,LEVEL_MEDIUM,  53, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    54,LEVEL_MEDIUM,  54, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    55,LEVEL_MEDIUM,  55, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    56,LEVEL_MEDIUM,  56, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    57,LEVEL_MEDIUM,  57, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    58,LEVEL_MEDIUM,  58, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    59,LEVEL_MEDIUM,  59, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    60,LEVEL_MEDIUM,  60, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    61,LEVEL_MEDIUM,  61, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    62,LEVEL_MEDIUM,  62, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    63,LEVEL_MEDIUM,  63, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    64,LEVEL_MEDIUM,  64, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    65,LEVEL_MEDIUM,  65, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    66,LEVEL_MEDIUM,  66, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    67,LEVEL_MEDIUM,  67, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    68,LEVEL_MEDIUM,  68, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    69,LEVEL_MEDIUM,  69, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    70,LEVEL_MEDIUM,  70, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    71,LEVEL_MEDIUM,  71, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    72,LEVEL_MEDIUM,  72, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    73,LEVEL_MEDIUM,  73, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    74,LEVEL_MEDIUM,  74, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    75,LEVEL_MEDIUM,  75, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    76,LEVEL_MEDIUM,  76, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    77,LEVEL_MEDIUM,  77, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    78,LEVEL_MEDIUM,  78, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    79,LEVEL_MEDIUM,  79, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    80,LEVEL_MEDIUM,  80, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    81,LEVEL_MEDIUM,  81, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    82,LEVEL_MEDIUM,  82, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    83,LEVEL_MEDIUM,  83, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    84,LEVEL_MEDIUM,  84, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    85,LEVEL_MEDIUM,  85, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    86,LEVEL_MEDIUM,  86, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    87,LEVEL_MEDIUM,  87, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    88,LEVEL_MEDIUM,  88, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    89,LEVEL_MEDIUM,  89, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    90,LEVEL_MEDIUM,  90, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    91,LEVEL_MEDIUM,  91, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    92,LEVEL_MEDIUM,  92, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    93,LEVEL_MEDIUM,  93, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    94,LEVEL_MEDIUM,  94, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    95,LEVEL_MEDIUM,  95, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    96,LEVEL_MEDIUM,  96, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    97,LEVEL_MEDIUM,  97, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    98,LEVEL_MEDIUM,  98, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    99,LEVEL_MEDIUM,  99, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data   100,LEVEL_MEDIUM, 100, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False

    ; Zen Master Level
    Data     1,LEVEL_HARD  ,   1, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data     2,LEVEL_HARD  ,   2, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data     3,LEVEL_HARD  ,   3, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data     4,LEVEL_HARD  ,   4, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data     5,LEVEL_HARD  ,   6, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data     6,LEVEL_HARD  ,   6, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data     7,LEVEL_HARD  ,   7, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data     8,LEVEL_HARD  ,   8, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data     9,LEVEL_HARD  ,   9, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    10,LEVEL_HARD  ,  10, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    11,LEVEL_HARD  ,  11, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    12,LEVEL_HARD  ,  12, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    13,LEVEL_HARD  ,  13, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    14,LEVEL_HARD  ,  14, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    15,LEVEL_HARD  ,  15, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    16,LEVEL_HARD  ,  16, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    17,LEVEL_HARD  ,  17, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    18,LEVEL_HARD  ,  18, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    19,LEVEL_HARD  ,  19, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    20,LEVEL_HARD  ,  20, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    21,LEVEL_HARD  ,  21, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    22,LEVEL_HARD  ,  22, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    23,LEVEL_HARD  ,  23, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    24,LEVEL_HARD  ,  24, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    25,LEVEL_HARD  ,  25, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    26,LEVEL_HARD  ,  26, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    27,LEVEL_HARD  ,  27, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    28,LEVEL_HARD  ,  28, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    29,LEVEL_HARD  ,  29, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    30,LEVEL_HARD  ,  30, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    31,LEVEL_HARD  ,  31, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    32,LEVEL_HARD  ,  32, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    33,LEVEL_HARD  ,  33, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    34,LEVEL_HARD  ,  34, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    35,LEVEL_HARD  ,  35, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    36,LEVEL_HARD  ,  36, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    37,LEVEL_HARD  ,  37, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    38,LEVEL_HARD  ,  38, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    39,LEVEL_HARD  ,  39, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    40,LEVEL_HARD  ,  40, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    41,LEVEL_HARD  ,  41, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    42,LEVEL_HARD  ,  42, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    43,LEVEL_HARD  ,  43, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    44,LEVEL_HARD  ,  44, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    45,LEVEL_HARD  ,  45, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    46,LEVEL_HARD  ,  46, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    47,LEVEL_HARD  ,  47, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    48,LEVEL_HARD  ,  48, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    49,LEVEL_HARD  ,  49, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    50,LEVEL_HARD  ,  50, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    51,LEVEL_HARD  ,  51, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    52,LEVEL_HARD  ,  52, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    53,LEVEL_HARD  ,  53, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    54,LEVEL_HARD  ,  54, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    55,LEVEL_HARD  ,  55, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    56,LEVEL_HARD  ,  56, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    57,LEVEL_HARD  ,  57, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    58,LEVEL_HARD  ,  58, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    59,LEVEL_HARD  ,  59, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    60,LEVEL_HARD  ,  60, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    61,LEVEL_HARD  ,  61, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    62,LEVEL_HARD  ,  62, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    63,LEVEL_HARD  ,  63, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    64,LEVEL_HARD  ,  64, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    65,LEVEL_HARD  ,  65, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    66,LEVEL_HARD  ,  66, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    67,LEVEL_HARD  ,  67, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    68,LEVEL_HARD  ,  68, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    69,LEVEL_HARD  ,  69, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    70,LEVEL_HARD  ,  70, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    71,LEVEL_HARD  ,  71, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    72,LEVEL_HARD  ,  72, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    73,LEVEL_HARD  ,  73, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    74,LEVEL_HARD  ,  74, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    75,LEVEL_HARD  ,  75, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    76,LEVEL_HARD  ,  76, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    77,LEVEL_HARD  ,  77, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    78,LEVEL_HARD  ,  78, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    79,LEVEL_HARD  ,  79, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    80,LEVEL_HARD  ,  80, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    81,LEVEL_HARD  ,  81, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    82,LEVEL_HARD  ,  82, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    83,LEVEL_HARD  ,  83, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    84,LEVEL_HARD  ,  84, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    85,LEVEL_HARD  ,  85, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    86,LEVEL_HARD  ,  86, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    87,LEVEL_HARD  ,  87, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    88,LEVEL_HARD  ,  88, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    89,LEVEL_HARD  ,  89, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    90,LEVEL_HARD  ,  90, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    91,LEVEL_HARD  ,  91, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    92,LEVEL_HARD  ,  92, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    93,LEVEL_HARD  ,  93, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    94,LEVEL_HARD  ,  94, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    95,LEVEL_HARD  ,  95, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    96,LEVEL_HARD  ,  96, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    97,LEVEL_HARD  ,  97, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    98,LEVEL_HARD  ,  98, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data    99,LEVEL_HARD  ,  99, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
    Data   100,LEVEL_HARD  , 100, PUZZLE_SHAPE_RECTANGLE,  4,  3,  2, 2, 2, 2,     2,      1,    25,    0, False
#endif
Data    -1

Function InitializePuzzleRules()
    Restore s_rotations
    For p = 0 To 2
        For r = 0 To 3
            For d = 0 To 4
                Read g_pipeRotations( p, r, d )
            Next
        Next
    Next    
    For i = 0 To 2
        For r = 0 To 3
            count = 0
            For dir = 1 To 4
                If g_pipeRotations( i, r, dir )
                    g_validMotion( i, r, count + 1 ) = dir
                    count = count + 1
                EndIf
            Next
            g_validMotion( i, r, 0 ) = count
        Next    
    Next    
End Function

Function LoadPuzzles( level = LEVEL_EASY )
    Local seed, shape, width, height
    Local px, py
    Local sx, sy
    Local elbows, straights
    Local seconds
    Local nbrIllusions, illusionsSpark
    Delete Each Puzzle
    g_puzzleCount = 0
    Restore puzzles
    Repeat
        Read seq
        If seq = -1 Then Exit
        Read playLevel
        Read seed, shape
        Read width, height
        Read px, py
        Read sx, sy
        Read elbows, straights
        Read seconds
        Read nbrIllusions, illusionsSpark
        If level = playLevel
            puzzle.Puzzle = New Puzzle
            puzzle\seed = seed
            puzzle\shape = shape
            puzzle\width = width
            puzzle\height = height
            puzzle\px = px
            puzzle\py = py
            puzzle\sx = sx
            puzzle\sy = sy
            puzzle\elbows = elbows
            puzzle\straights = straights
            puzzle\seconds = seconds
            puzzle\nbrIllusions = nbrIllusions
            puzzle\illusionsSpark = illusionsSpark
            g_puzzles[g_puzzleCount] = puzzle
            g_puzzleCount = g_puzzleCount + 1
        EndIf        
    Forever
End Function

Function IsPieceRotating( x, y )
    Local thisPiece.PuzzlePiece = GetPuzzlePiece( x, y )
	Return thisPiece\isRotating
End Function

Function IsDirectionValid( x, y, dir )
    Local thisPiece.PuzzlePiece = GetPuzzlePiece( x, y )
	Return (g_pipeRotations( thisPiece\piece, thisPiece\rotation, dir ) And (Not thisPiece\isRotating))
End Function

Function IsMoveValid( x, y, dir, rot = -1 )
    Local thisPiece.PuzzlePiece = GetPuzzlePiece( x, y )
    Local oppositeDir = GetOppositeDirection( dir )
    If rot = -1 Then rot = thisPiece\rotation
	isValid = (g_pipeRotations( thisPiece\piece, rot, dir ) And (Not thisPiece\isRotating))
	If isValid 
		isValid = False
		Select dir
			Case MOVE_UP:    y = y + 1
			Case MOVE_DOWN:  y = y - 1
			Case MOVE_RIGHT: x = x + 1
			Case MOVE_LEFT:  x = x - 1
		End Select
        If( ( x >= 1 And x <= g_currentPuzzle\width ) And ( y >= 1 And y <= g_currentPuzzle\height ) )
            thisPiece = GetPuzzlePiece( x, y )
		    isValid = (g_pipeRotations( thisPiece\piece, thisPiece\rotation, oppositeDir ) And (Not thisPiece\isRotating))
        EndIf    
	EndIf	
    Return isValid
End Function

Function CheckForPuzzleCompletion()
	If IsPuzzleSolved()
		If IsCameraOverhead()
			ToggleCameraView()
			UpdateCamera()
		EndIf
		CameraZoomOut()
		CameraRoam( g_theSource\pos\x, g_theSource\pos\z )
		SetGameAwardMode()
	EndIf
End Function

Function IsPuzzleSolved()
	ResetVisitedPieces
	VisitPuzzlePiece( g_currentPuzzle\sx, g_currentPuzzle\sy )
	Return CheckVisitedCount()
End Function

Function VisitPuzzlePiece( x, y )
    Local thisPiece.PuzzlePiece = GetPuzzlePiece( x, y )
    Local dir = 0
	If thisPiece\visited Then Return
    If thisPiece\bonus = False
	    SetPipeColor thisPiece\entity, True
    EndIf    
    thisPiece\visited = True
	For i = 1 To g_validMotion( thisPiece\piece, thisPiece\rotation, 0 )
        dir = g_validMotion( thisPiece\piece, thisPiece\rotation, i )
		If IsMoveValid( x, y, dir )
			Select dir
				Case MOVE_RIGHT: VisitPuzzlePiece( x + 1, y )
				Case MOVE_LEFT:  VisitPuzzlePiece( x - 1, y )
				Case MOVE_UP:    VisitPuzzlePiece( x, y + 1 )
				Case MOVE_DOWN:  VisitPuzzlePiece( x, y - 1 )
			End Select
		EndIf
	Next	
End Function

Function IsPuzzleSolvable()
	WalkPuzzle( g_currentPuzzle\sx, g_currentPuzzle\sy )
	Return CheckVisitedCount( False )
End Function

Function WalkPuzzle( x, y )
    Local thisPiece.PuzzlePiece = GetPuzzlePiece( x, y )
    Local dir = 0, rot = 0 
	If thisPiece\visited Or thisPiece\piece = PUZZLE_PIECE_N Then Return
    thisPiece\visited = True
    rot = thisPiece\rotation
    Repeat
		For i = 1 To g_validMotion( thisPiece\piece, rot, 0 )
			dir = g_validMotion( thisPiece\piece, rot, i )
			If IsMoveValid( x, y, dir, rot )
				Select dir
					Case MOVE_RIGHT: WalkPuzzle( x + 1, y )
					Case MOVE_LEFT:  WalkPuzzle( x - 1, y )
					Case MOVE_UP:    WalkPuzzle( x, y + 1 )
					Case MOVE_DOWN:  WalkPuzzle( x, y - 1 )
				End Select
			EndIf
		Next
		rot = rot + 1
		If rot > 3
			rot = 0
		EndIf
		If rot = thisPiece\rotation Then Exit
	Forever	
End Function

Function CheckVisitedCount( resetColor = True )
	Local solveCount = 0
	For i = 0 To g_totalPieces - 1
		If g_currentPuzzle\pieces[i]\visited And g_currentPuzzle\pieces[i]\bonus = False
			solveCount = solveCount + 1
		ElseIf g_currentPuzzle\pieces[i]\piece = PUZZLE_PIECE_N
			solveCount = solveCount + 1
		Else
			If resetColor 
            	SetPipeColor g_currentPuzzle\pieces[i]\entity, False
            EndIf	
		EndIf	
	Next
    Return (solveCount = g_totalPieces)
End Function

Function ResetVisitedPieces()
	For i = 0 To g_totalPieces - 1
		g_currentPuzzle\pieces[i]\visited = False
	Next
End Function

Function SetPipeColor( entity, connected = False )
	Local glass = 0
	If entity <> 0
		glass = GetChild( entity, 2 )
		If connected
			EntityColor glass, 0, 255, 0
		Else
			EntityColor glass, 255, 255, 255
		EndIf
	EndIf	
End Function

Function GetPuzzlePiece.PuzzlePiece( x, y )
    Return g_currentPuzzle\pieces[(y - 1) * g_currentPuzzle\width + (x - 1)]
End Function

Function GetPuzzle.Puzzle( level )
    If InAttractMode()
        puzzle.Puzzle = AttractModePuzzle()
        g_totalPieces = puzzle\width * puzzle\height
        Return puzzle
    Else
        GenerateRandomPuzzle( g_puzzles[level] )
		g_totalPieces = g_puzzles[level]\width * g_puzzles[level]\height
        Return g_puzzles[level]
    EndIf    
End Function

Function AttractModePuzzle.Puzzle()
    puzzle.Puzzle = New Puzzle
    puzzle\nbrIllusions = 0
    puzzle\seconds = 25
    puzzle\width = 4
    puzzle\height = 3
    puzzle\px = 2
    puzzle\py = 2
    puzzle\sx = 2
    puzzle\sy = 2

    puzzle\pieces[0] = New PuzzlePiece
    puzzle\pieces[0]\piece = PUZZLE_PIECE_N
    puzzle\pieces[0]\rotation = 0

    puzzle\pieces[1] = New PuzzlePiece
    puzzle\pieces[1]\piece = PUZZLE_PIECE_T
    puzzle\pieces[1]\rotation = 0

    puzzle\pieces[2] = New PuzzlePiece
    puzzle\pieces[2]\piece = PUZZLE_PIECE_L
    puzzle\pieces[2]\rotation = 0

    puzzle\pieces[3] = New PuzzlePiece
    puzzle\pieces[3]\piece = PUZZLE_PIECE_T
    puzzle\pieces[3]\rotation = 2

    puzzle\pieces[4] = New PuzzlePiece
    puzzle\pieces[4]\piece = PUZZLE_PIECE_T
    puzzle\pieces[4]\rotation = 2

    puzzle\pieces[5] = New PuzzlePiece
    puzzle\pieces[5]\piece = PUZZLE_PIECE_T
    puzzle\pieces[5]\rotation = 1

    puzzle\pieces[6] = New PuzzlePiece
    puzzle\pieces[6]\piece = PUZZLE_PIECE_S
    puzzle\pieces[6]\rotation = 1

    puzzle\pieces[7] = New PuzzlePiece
    puzzle\pieces[7]\piece = PUZZLE_PIECE_T
    puzzle\pieces[7]\rotation = 2

    puzzle\pieces[8] = New PuzzlePiece
    puzzle\pieces[8]\piece = PUZZLE_PIECE_T
    puzzle\pieces[8]\rotation = 0

    puzzle\pieces[9] = New PuzzlePiece
    puzzle\pieces[9]\piece = PUZZLE_PIECE_T
    puzzle\pieces[9]\rotation = 3

    puzzle\pieces[10] = New PuzzlePiece
    puzzle\pieces[10]\piece = PUZZLE_PIECE_L
    puzzle\pieces[10]\rotation = 3

    puzzle\pieces[11] = New PuzzlePiece
    puzzle\pieces[11]\piece = PUZZLE_PIECE_T
    puzzle\pieces[11]\rotation = 1
    Return puzzle
End Function

Function GenerateRandomPuzzle( puzzle.Puzzle )
	Local idx = 0
	Local lastRot = -1
    Local sWait = (puzzle\width * puzzle\height) / puzzle\straights
    Local lWait = (puzzle\width * puzzle\height) / puzzle\elbows
    Local sCount = 0
    Local lCount = 0
    Local sUsed = False
    Local lused = False

	SeedRnd puzzle\seed
	For y = 1 To puzzle\height
		For x = 1 To puzzle\width
			puzzle\pieces[idx] = New PuzzlePiece
			While True
				piece = Rand( 0, 2 )
				If piece = PUZZLE_PIECE_S
					If sCount < puzzle\straights
						If sUsed = False
							sCount = sCount + 1
							sUsed = True
							Exit
						EndIf	
					EndIf
				ElseIf piece = PUZZLE_PIECE_L
					If lCount < puzzle\elbows
						If lUsed = False
							lCount = lCount + 1
							lUsed = True
							Exit
						EndIf	
					EndIf
				Else
                    sWait = sWait - 1
                    lWait = lWait - 1
                    If sWait <= 0
                        sWait = (puzzle\width * puzzle\height) / puzzle\straights
                        sUsed = False
                    EndIf    
                    If lWait <= 0
                        lWait = (puzzle\width * puzzle\height) / puzzle\elbows
                        lUsed = False
                    EndIf    
                    Exit
				EndIf
			Wend
			If y = 1
				Select piece
					Case PUZZLE_PIECE_S: rot = 0
					Case PUZZLE_PIECE_L: rot = Rand( 2, 3 )
					Case PUZZLE_PIECE_T
						Repeat
							rot = Rand( 0, 3 )
						Until rot <> 1	
				End Select
			ElseIf y = puzzle\height
				Select piece
					Case PUZZLE_PIECE_S: rot = 0
					Case PUZZLE_PIECE_L: rot = Rand( 0, 1 )
					Case PUZZLE_PIECE_T: rot = Rand( 0, 2 )
				End Select
			ElseIf x = 1
				Select piece
					Case PUZZLE_PIECE_S: rot = 1
					Case PUZZLE_PIECE_L
						Repeat
							rot = Rand( 0, 3 )
						Until rot <> 1 And rot <> 2	
					Case PUZZLE_PIECE_T
						Repeat
							rot = Rand( 0, 3 )
						Until rot <> 2
				End Select
			ElseIf x = puzzle\width
				Select piece
					Case PUZZLE_PIECE_S: rot = 1
					Case PUZZLE_PIECE_L: rot = Rand( 0, 1 )
					Case PUZZLE_PIECE_T
						Repeat
							rot = Rand( 0, 3 )
						Until rot <> 0
				End Select
			Else
				Repeat
					rot = Rand( 0, 3 )
					If rot <> lastRot
						checkPiece.PuzzlePiece = puzzle\pieces[(y - 2) * puzzle\width + (x - 1)]
						If rot <> checkPiece\rotation Then Goto picked
					EndIf	
				Forever
.picked				
				lastRot = rot
			EndIf
			puzzle\pieces[idx]\piece = piece
			puzzle\pieces[idx]\rotation = rot
			idx = idx + 1
		Next
	Next	
End Function
