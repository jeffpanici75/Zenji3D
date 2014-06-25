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

Const KEY_ESCAPE = 1
Const KEY_1 = 2
Const KEY_2 = 3
Const KEY_3 = 4
Const KEY_4 = 5
Const KEY_5 = 6
Const KEY_6 = 7
Const KEY_7 = 8
Const KEY_8 = 9
Const KEY_9 = 10
Const KEY_0 = 11
Const KEY_MINUS = 12
Const KEY_EQUALS = 13
Const KEY_BACKSPACE = 14
Const KEY_TAB = 15
Const KEY_Q = 16
Const KEY_W = 17
Const KEY_E = 18
Const KEY_R = 19
Const KEY_T = 20
Const KEY_Y = 21
Const KEY_U = 22
Const KEY_I = 23
Const KEY_O = 24
Const KEY_P = 25
Const KEY_LEFT_BRACKET = 26
Const KEY_RIGHT_BRACKET = 27
Const KEY_RETURN = 28
Const KEY_LEFT_CTRL = 29
Const KEY_A = 30
Const KEY_S = 31
Const KEY_D = 32
Const KEY_F = 33
Const KEY_G = 34
Const KEY_H = 35
Const KEY_J = 36
Const KEY_K = 37
Const KEY_L = 38
Const KEY_SEMI_COLON = 39
Const KEY_APOSTROPHE = 40
Const KEY_ACCENT_GRAVE = 41
Const KEY_LEFT_SHIFT = 42
Const KEY_BACK_SLASH = 43
Const KEY_Z = 44
Const KEY_X = 45
Const KEY_C = 46
Const KEY_V = 47
Const KEY_B = 48
Const KEY_N = 49
Const KEY_M = 50
Const KEY_COMMA = 51
Const KEY_PERIOD = 52
Const KEY_FORWARD_SLASH = 53
Const KEY_RIGHT_SHIFT = 54
Const KEY_MULTIPLY = 55
Const KEY_LEFT_ALT = 56
Const KEY_SPACE = 57
Const KEY_CAPITAL = 58
Const KEY_F1 = 59
Const KEY_F2 = 60
Const KEY_F3 = 61
Const KEY_F4 = 62
Const KEY_F5 = 63
Const KEY_F6 = 64
Const KEY_F7 = 65
Const KEY_F8 = 66
Const KEY_F9 = 67
Const KEY_F10 = 68
Const KEY_NUM_LOCK = 69
Const KEY_SCROLL_LOCK = 70
Const KEY_NUM_PAD_7 = 71
Const KEY_NUM_PAD_8 = 72
Const KEY_NUM_PAD_9 = 73
Const KEY_NUM_PAD_MINUS = 74
Const KEY_NUM_PAD_4 = 75
Const KEY_NUM_PAD_5 = 76
Const KEY_NUM_PAD_6 = 77
Const KEY_NUM_PAD_ADD = 78
Const KEY_NUM_PAD_1 = 79
Const KEY_NUM_PAD_2 = 80
Const KEY_NUM_PAD_3 = 81
Const KEY_NUM_PAD_0 = 82
Const KEY_NUM_PAD_PERIOD = 83
Const KEY_F11 = 87
Const KEY_F12 = 88
Const KEY_NUM_PAD_ENTER = 156
Const KEY_RIGHT_CTRL = 157
Const KEY_NUM_PAD_DIVIDE = 181
Const KEY_SYS_REQ = 183
Const KEY_RIGHT_ALT = 184
Const KEY_PAUSE = 197
Const KEY_HOME = 199
Const KEY_CURSOR_UP = 200
Const KEY_PAGE_UP = 201
Const KEY_PAGE_DOWN = 209
Const KEY_CURSOR_LEFT = 203
Const KEY_CURSOR_RIGHT = 205
Const KEY_END = 207
Const KEY_CURSOR_DOWN = 208
Const KEY_INSERT = 210
Const KEY_DELETE = 211
Const KEY_LEFT_WINDOWS = 219
Const KEY_RIGHT_WINDOWS = 220

.scanKeyMap
Data "KEY_ESCAPE", 1
Data "KEY_1", 2
Data "KEY_2", 3
Data "KEY_3", 4
Data "KEY_4", 5
Data "KEY_5", 6
Data "KEY_6", 7
Data "KEY_7", 8
Data "KEY_8", 9
Data "KEY_9", 10
Data "KEY_0", 11
Data "KEY_MINUS", 12
Data "KEY_EQUALS", 13
Data "KEY_BACKSPACE", 14
Data "KEY_TAB", 15
Data "KEY_Q", 16
Data "KEY_W", 17
Data "KEY_E", 18
Data "KEY_R", 19
Data "KEY_T", 20
Data "KEY_Y", 21
Data "KEY_U", 22
Data "KEY_I", 23
Data "KEY_O", 24
Data "KEY_P", 25
Data "KEY_LEFT_BRACKET", 26
Data "KEY_RIGHT_BRACKET", 27
Data "KEY_RETURN", 28
Data "KEY_LEFT_CTRL", 29
Data "KEY_A", 30
Data "KEY_S", 31
Data "KEY_D", 32
Data "KEY_F", 33
Data "KEY_G", 34
Data "KEY_H", 35
Data "KEY_J", 36
Data "KEY_K", 37
Data "KEY_L", 38
Data "KEY_SEMI_COLON", 39
Data "KEY_APOSTROPHE", 40
Data "KEY_ACCENT_GRAVE", 41
Data "KEY_LEFT_SHIFT", 42
Data "KEY_BACK_SLASH", 43
Data "KEY_Z", 44
Data "KEY_X", 45
Data "KEY_C", 46
Data "KEY_V", 47
Data "KEY_B", 48
Data "KEY_N", 49
Data "KEY_M", 50
Data "KEY_COMMA", 51
Data "KEY_PERIOD", 52
Data "KEY_FORWARD_SLASH", 53
Data "KEY_RIGHT_SHIFT", 54
Data "KEY_MULTIPLY", 55
Data "KEY_LEFT_ALT", 56
Data "KEY_SPACE", 57
Data "KEY_CAPITAL", 58
Data "KEY_F1", 59
Data "KEY_F2", 60
Data "KEY_F3", 61
Data "KEY_F4", 62
Data "KEY_F5", 63
Data "KEY_F6", 64
Data "KEY_F7", 65
Data "KEY_F8", 66
Data "KEY_F9", 67
Data "KEY_F10", 68
Data "KEY_NUM_LOCK", 69
Data "KEY_SCROLL_LOCK", 70
Data "KEY_NUM_PAD_7", 71
Data "KEY_NUM_PAD_8", 72
Data "KEY_NUM_PAD_9", 73
Data "KEY_NUM_PAD_MINUS", 74
Data "KEY_NUM_PAD_4", 75
Data "KEY_NUM_PAD_5", 76
Data "KEY_NUM_PAD_6", 77
Data "KEY_NUM_PAD_ADD", 78
Data "KEY_NUM_PAD_1", 79
Data "KEY_NUM_PAD_2", 80
Data "KEY_NUM_PAD_3", 81
Data "KEY_NUM_PAD_0", 82
Data "KEY_NUM_PAD_PERIOD", 83
Data "KEY_F11", 87
Data "KEY_F12", 88
Data "KEY_NUM_PAD_ENTER", 156
Data "KEY_RIGHT_CTRL", 157
Data "KEY_NUM_PAD_DIVIDE", 181
Data "KEY_SYS_REQ", 183
Data "KEY_RIGHT_ALT", 184
Data "KEY_PAUSE", 197
Data "KEY_HOME", 199
Data "KEY_CURSOR_UP", 200
Data "KEY_PAGE_UP", 201
Data "KEY_PAGE_DOWN", 209
Data "KEY_CURSOR_LEFT", 203
Data "KEY_CURSOR_RIGHT", 205
Data "KEY_END", 207
Data "KEY_CURSOR_DOWN", 208
Data "KEY_INSERT", 210
Data "KEY_DELETE", 211
Data "KEY_LEFT_WINDOWS", 219
Data "KEY_RIGHT_WINDOWS", 220
Data "###"
