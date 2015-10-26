package morse.morseapp.message;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Class to help map letters (such as 'a', 'b', 'ö', '?' etc) to units (bytes) of flash on/off.
 * This is done by first mapping each character to 'inner characters' and then mapping those to bytes.
 * For instance, in the morse code alphabet, inner characters are '-' and '.' (dash and dot), and their byte representations are 3 and 1.
 */
public abstract class Alphabet {
    private Alphabet() {}

    /** Convert a word character (eg. 'a') to its inner character (dots and dashes) representation. In morse, an input 'a' should return ".-" */
    abstract String get(char c);

    /**
     * convert an inner character (eg. a dot or dash) to a number of units. In morse dot returns 1 and dash returns 3
     * A positive number will signify flash on, a negative number is flash off.
     * */
    abstract byte getInner(char innerChar);

    /** the spacing between characters in a word. In morse, this value is -3 (three units flash off) */
    protected byte getLetterSpacing() { return 0; }

    /** the spacing between characters .-.-.. and so forth. In morse, this value is -1 (on unit flash off) */
    protected byte getInnerCharacterSpacing() { return 0; }

    final boolean hasLetterSpacing() {
        return getLetterSpacing() != 0;
    }

    final boolean hasInnerCharacterSpacing() {
        return getInnerCharacterSpacing() != 0;
    }



    /**
     * ------------------------------------------------------------
     * ---------------- DEFAULT ALPHABETS BELOW -------------------
     * ------------------------------------------------------------
     */

    // map of the international morse alphabet's inner characters
    private static final Map<Character, String> internationalMorseMap;

    static {
        HashMap<Character, String> morseMap = new HashMap<>();
        morseMap.put('a',".-");
        morseMap.put('b',"-...");
        morseMap.put('c',"-.-.");
        morseMap.put('d',"-..");
        morseMap.put('e',".");
        morseMap.put('f',"..-.");
        morseMap.put('g',"--.");
        morseMap.put('h',"....");
        morseMap.put('i',"..");
        morseMap.put('j',".---");
        morseMap.put('k',"-.-");
        morseMap.put('l',".-..");
        morseMap.put('m',"--");
        morseMap.put('n',"-.");
        morseMap.put('o',"---");
        morseMap.put('p',".--.");
        morseMap.put('q',"--.-");
        morseMap.put('r',".-.");
        morseMap.put('s',"...");
        morseMap.put('t',"-");
        morseMap.put('u',"..-");
        morseMap.put('v',"...-");
        morseMap.put('w',".--");
        morseMap.put('x',"-..-");
        morseMap.put('y',"-.--");
        morseMap.put('z',"--..");
        internationalMorseMap = Collections.unmodifiableMap(morseMap);
    }

    /**
     * Special note:
     * In international morse, a space is considered to be "7 units flash off"
     * Here, we implement this by having space as a letter whose inner character returns -1 (one unit flash off)
     * Since letter spacing is -3, we'll get -7 from adding (-3) + (-1) + (-3).
     */
    public static final Alphabet INTERNATIONAL_MORSE = new Alphabet() {
        @Override
        String get(char c) {
            switch (c) {
                case ' ': return " ";
                default: {
                    if (internationalMorseMap.containsKey(c))
                        return internationalMorseMap.get(c);
                    else
                        return "";
                }
            }
        }

        @Override
        byte getInner(char c) {
            switch (c) {
                case ' ': return -1;
                case '.': return 1;
                case '-': return 3;
                default: return 0;
            }
        }

        @Override
        protected byte getInnerCharacterSpacing() {
            return -1;
        }

        @Override
        protected byte getLetterSpacing() {
            return -3;
        }
    };
}
