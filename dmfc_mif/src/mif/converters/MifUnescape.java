/*
 * Created on 2005-feb-28
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 * 
 * Very slow...
 */
package mif.converters;

/**
 * 
 * ... beskrivning ...
 * 
 * @author  Joel Håkansson, TPB
 * @version 2005-feb-28
 * @since 1.0
 */
public abstract class MifUnescape {
	public static final int WINDOWS_STANDARD = 1;
	public static final int WINDOWS_SYMBOL = 2;
	public static final int WINDOWS_DINGBATS = 3;
	public static final int WINDOWS_FILENAME = 4;
	
	public static final int UNIX_STANDARD = 11;
	public static final int UNIX_SYMBOL = 12;
	public static final int UNIX_DINGBATS = 13;
	
	public static final int MACINTOSH_STANDARD = 21;
	public static final int MACINTOSH_SYMBOL = 22;
	public static final int MACINTOSH_DINGBATS = 23;

	/**
	 * 
	 */
	public final static String[][] COMMON = new String[][] {
			// Mif Escape chars (Mif Reference p. 7)
			{"\\", "\\"},
			{"t", "\u0009"},
			{">", ">"},
			{"q", "'"},
			{"Q", "`"}
	};
	
	public final static String[][] WINDOWS_STANDARD_MAP = new String[][] {			
			// Mif Hex repr (FrameMaker Character Sets)
			// '*' markerar oexakta ersättningar, vissa har medvetet översatts för att undvika
			// problem med talsyntesen (olika typer av citat-tecken
			// markerar förenklingar
			{"x04 ", ""}, //*
			{"x05 ", ""}, //*
			{"x15 ", ""}, //*
			{"x08 ", "\u0009"},
			{"x09 ", "\n"}, // * forced return
			{"x0a ", "\n"},
			{"x10 ", " "}, // * numeric space
			{"x11 ", "\u00a0"},
			{"x12 ", " "}, // * thin space
			{"x13 ", " "}, // * en space
			{"x14 ", " "}, // * em space
			{"x27 ", "'"},
			{"x60 ", "`"},
			{"xda ", "/"},
			{"xde ", "fi"}, // * fi-ligatur
			{"xdf ", "fl"}, // * fl-ligatur
			{"xf5 ", ""}, //*
			{"xf9 ", ""}, //*
			{"xfa ", ""}, //*
			{"xfe ", ""}, //*
			{"xfd ", ""}, //*
			{"x20 ", " "},
			{"x21 ", "!"},
			{"x22 ", "\""},
			{"x23 ", "#"},
			{"x24 ", "$"},
			{"x25 ", "%"},
			{"x26 ", "&"},
			{"x28 ", "("},
			{"x29 ", ")"},
			{"x2a ", "*"},
			{"x2b ", "+"},
			{"x2c ", ","},
			{"x2d ", "-"},			
			{"x2e ", "."},
			{"x2f ", "/"},
			{"x30 ", "0"},
			{"x31 ", "1"},
			{"x32 ", "2"},
			{"x33 ", "3"},
			{"x34 ", "4"},
			{"x35 ", "5"},
			{"x36 ", "6"},
			{"x37 ", "7"},
			{"x38 ", "8"},
			{"x39 ", "9"},
			{"x3a ", ":"},
			{"x3b ", ";"},
			{"x3c ", "<"},
			{"x3d ", "="},
			{"x3e ", ">"},
			{"x3f ", "?"},
			{"x40 ", "@"},
			{"x41 ", "A"},
			{"x42 ", "B"},
			{"x43 ", "C"},
			{"x44 ", "D"},
			{"x45 ", "E"},
			{"x46 ", "F"},
			{"x47 ", "G"},
			{"x48 ", "H"},
			{"x49 ", "I"},
			{"x4a ", "J"},
			{"x4b ", "K"},
			{"x4c ", "L"},
			{"x4d ", "M"},
			{"x4e ", "N"},
			{"x4f ", "O"},
			{"x50 ", "P"},
			{"x51 ", "Q"},
			{"x52 ", "R"},
			{"x53 ", "S"},
			{"x54 ", "T"},
			{"x55 ", "U"},
			{"x56 ", "V"},
			{"x57 ", "W"},
			{"x58 ", "X"},
			{"x59 ", "Y"},
			{"x5a ", "Z"},
			{"x5b ", "["},
			{"x5c ", ""},
			{"x5d ", "]"},
			{"x5e ", "^"},
			{"x5f ", "_"},
			{"x61 ", "a"},
			{"x62 ", "b"},
			{"x63 ", "c"},
			{"x64 ", "d"},
			{"x65 ", "e"},
			{"x66 ", "f"},
			{"x67 ", "g"},
			{"x68 ", "h"},
			{"x69 ", "i"},
			{"x6a ", "j"},
			{"x6b ", "k"},
			{"x6c ", "l"},
			{"x6d ", "m"},
			{"x6e ", "n"},
			{"x6f ", "o"},
			{"x70 ", "p"},
			{"x71 ", "q"},
			{"x72 ", "r"},
			{"x73 ", "s"},
			{"x74 ", "t"},
			{"x75 ", "u"},
			{"x76 ", "v"},
			{"x77 ", "w"},
			{"x78 ", "x"},
			{"x79 ", "y"},
			{"x7a ", "z"},
			{"x7b ", "{"},
			{"x7c ", "|"},
			{"x7d ", "}"},
			{"x7e ", "~"},
			{"xe2 ", "'"}, // * quotesinglebase
			{"xc4 ", "f"}, // florin
			{"xe3 ", "\""}, // * quotedoublebase
			{"xc9 ", "..."}, // * ellipsis
			{"xa0 ", ""}, //*
			{"xe0 ", ""}, //*
			{"xf6 ", "^"},
			{"xe4 ", ""}, //*
			{"xb3 ", ""}, //*
			{"xdc ", "'"}, // * guilsinglleft <
			{"xce ", "OE"}, // OE
			{"xd4 ", "'"}, // * quoteleft
			{"xd5 ", "'"}, // * quoteright
			{"xd2 ", "\""}, // * quotedblleft
			{"xd3 ", "\""}, // * quotedblright
			{"xa5 ", "\u2022"}, 
			{"xd0 ", "-"}, // * endash
			{"xd1 ", "-"}, // * emdash
			{"xf7 ", "~"},
			{"xaa ", ""}, //*
			{"xf0 ", ""}, //*
			{"xdd ", "'"}, // * guilsinglright >
			{"xcf ", "oe"}, // oe
			{"xd9 ", "\u0178"},
			{"xc1 ", "\u00a1"},
			{"xa2 ", "\u00a2"},
			{"xa3 ", "\u00a3"},
			{"xdb ", "\u00a4"},
			{"xb4 ", "\u00a5"},
			{"xad ", "\u00a6"},
			{"xa4 ", "\u00a7"},
			{"xac ", "\u00a8"},
			{"xa9 ", "\u00a9"},
			{"xbb ", "\u00aa"},
			{"xc7 ", "\""}, // guillemetleft (much less than <<) \u00ab
			{"xc2 ", "\u00ac"},
//			{"x2d ", "-"}, den här är med två gånger i källmaterialet, men en gång räcker bra här...
			{"xa8 ", "\u00ae"},
			{"xf8 ", "\u00af"},
			{"xfb ", "\u00b0"},
			{"xb1 ", "\u00b1"},
			{"xb7 ", "\u00b2"},
			{"xb8 ", "\u00b3"},
			{"xab ", "\u00b4"},
			{"xb5 ", "\u00b5"},
			{"xa6 ", "\u00b6"},
			{"xe1 ", "\u00b7"},
			{"xfc ", "\u00b8"},
			{"xb6 ", "\u00b9"},
			{"xbc ", "\u00ba"},
			{"xc8 ", "\""}, // guillemetright (much more than >>) \u00bb
			{"xb9 ", "\u00bc"},
			{"xba ", "\u00bd"},
			{"xbd ", "\u00be"},
			{"xc0 ", "\u00bf"},
			{"xcb ", "\u00c0"},
			{"xe7 ", "\u00c1"},
			{"xe5 ", "\u00c2"},
			{"xcc ", "\u00c3"},
			{"x80 ", "\u00c4"},
			{"x81 ", "\u00c5"},
			{"xae ", "\u00c6"},
			{"x82 ", "\u00c7"},
			{"xe9 ", "\u00c8"},
			{"x83 ", "\u00c9"},
			{"xe6 ", "\u00ca"},
			{"xe8 ", "\u00cb"},
			{"xed ", "\u00cc"},
			{"xea ", "\u00cd"},
			{"xeb ", "\u00ce"},
			{"xec ", "\u00cf"},
			{"xc3 ", "\u00d0"},
			{"x84 ", "\u00d1"},
			{"xf1 ", "\u00d2"},
			{"xee ", "\u00d3"},
			{"xef ", "\u00d4"},
			{"xcd ", "\u00d5"},
			{"x85 ", "\u00d6"},
			{"xb0 ", "\u00d7"},
			{"xaf ", "\u00d8"},
			{"xf4 ", "\u00d9"},
			{"xf2 ", "\u00da"},
			{"xf3 ", "\u00db"},
			{"x86 ", "\u00dc"},
			{"xc5 ", "\u00dd"},
			{"xd7 ", "\u00de"},
			{"xa7 ", "\u00df"},
			{"x88 ", "\u00e0"},
			{"x87 ", "\u00e1"},
			{"x89 ", "\u00e2"},
			{"x8b ", "\u00e3"},
			{"x8a ", "\u00e4"},
			{"x8c ", "\u00e5"},
			{"xbe ", "\u00e6"},
			{"x8d ", "\u00e7"},
			{"x8f ", "\u00e8"},
			{"x8e ", "\u00e9"},
			{"x90 ", "\u00ea"},
			{"x91 ", "\u00eb"},
			{"x92 ", "\u00ec"},
			{"x93 ", "\u00ed"},
			{"x94 ", "\u00ee"},
			{"x95 ", "\u00ef"},
			{"xb2 ", "\u00f0"},
			{"x96 ", "\u00f1"},
			{"x98 ", "\u00f2"},
			{"x97 ", "\u00f3"},
			{"x99 ", "\u00f4"},
			{"x9b ", "\u00f5"},
			{"x9a ", "\u00f6"},
			{"xd6 ", "\u00f7"},
			{"xbf ", "\u00f8"},
			{"x9d ", "\u00f9"},
			{"x9c ", "\u00fa"},
			{"x9e ", "\u00fb"},
			{"x9f ", "\u00fc"},
			{"xc6 ", "\u00fd"},
			{"xca ", "\u00fe"},
			{"xd8 ", "\u00ff"}
			};
	
	private static String doIt(String input, String[][] map, int mapLen) {
		String[] tmp = input.split("\\\\");
		String output = tmp[0];
		for (int i=1; i<tmp.length; i++) {
			if (tmp[i].length()<mapLen) {
				// We don't have a match, restore...
				output += "\\" + tmp[i];
			} else {
				String str = tmp[i].substring(0,mapLen);
				int j=0;
				while (j < map.length && !str.equals(map[j][0])) {
					j++;
				}
				if (j < map.length) {
					// We have a match, change...
					output += map[j][1] + tmp[i].substring(mapLen);
				} else {
					// We don't have a match, restore...
					output += "\\" + tmp[i];
				}
			}
		}
		return output;
	}
	
	/**
	 * Unescapes the <b>input</b> string using the provided <b>mappingMode</b>.
	 * @param input
	 * @param mappingMode
	 * @return the unescaped string
	 */
	public static String unescape(String input, int mappingMode)
	{ 
		String output = input;
		switch (mappingMode) {
			case WINDOWS_STANDARD:
				output = doIt(output, WINDOWS_STANDARD_MAP, 4);
				break;
			case WINDOWS_SYMBOL:
				break;
			case WINDOWS_DINGBATS:
				break;
			case UNIX_STANDARD:
				break;
			case MACINTOSH_STANDARD:
				break;
		}
		output = doIt(output, COMMON, 1);
		return output;
	}
	
}

