package morse.morseapp.message;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Class to help map letters (such as 'a', 'b', 'ö', '?' etc) to units (bytes) of flash on/off.
 * This is done by first mapping each character to 'inner characters' and then mapping those to bytes.
 * For instance, in the morse code alphabet, inner characters are '-' and '.' (dash and dot), and their byte representations are 3 and 1, respectively.
 */
public abstract class Alphabet {
    private Alphabet() {}

    public abstract String name();

    /** Convert a word character (eg. 'a') to its inner character (dots and dashes) representation. In morse, an input 'a' should return ".-" */
    abstract String get(char c);

    /**
     * convert an inner character (eg. a dot or dash) to a number of units. In morse dot returns 1 and dash returns 3
     * A positive number will signify flash on, a negative number is flash off.
     * */
    abstract byte getInner(char innerChar);

    /**
     * Decode a given message
     */
    abstract String decodeMessage(List<Boolean> bits);

    /** the spacing between characters in a word. In morse, this value is -3 (three units flash off) */
    protected byte getLetterSpacing() { return 0; }

    /** the spacing between characters .-.-.. and so forth. In morse, this value is -1 (one unit flash off) */
    protected byte getInnerCharacterSpacing() { return 0; }

    final boolean hasLetterSpacing() {
        return getLetterSpacing() != 0;
    }

    final boolean isLetterSpacing(byte test) {
        return test == getLetterSpacing();
    }

    final boolean isInnerCharacterSpacing(byte test) {
        return test == getInnerCharacterSpacing();
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
    private static final Map<Character, String> INTERNATIONAL_MORSE_MAP;

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
        INTERNATIONAL_MORSE_MAP = Collections.unmodifiableMap(morseMap);
    }

    /**
     * Special note:
     * In international morse, a space is considered to be "7 units flash off"
     * Here, we implement this by having space as a letter whose inner character returns -1 (one unit flash off)
     * Since letter spacing is -3, we'll get -7 from adding (-3) + (-1) + (-3).
     */
    public static final Alphabet INTERNATIONAL_MORSE = new Alphabet() {
        @Override
        public String name() {
            return "International Morse";
        }

        @Override
        String get(char c) {
            switch (c) {
                case ' ': return " ";
                default: {
                    if (INTERNATIONAL_MORSE_MAP.containsKey(c))
                        return INTERNATIONAL_MORSE_MAP.get(c);
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
        String decodeMessage(List<Boolean> bits) {
            int len = bits.size();

            /**
             * ------------------------------------------------
             * -------------- WORK IN PROGRESS ----------------
             * ------------------------------------------------
             */

            /*for (int i = 0; i < len; i++) {

                // variable to store our dots and dashes
                String s = "";

                int negatives = 0;
                Boolean b;

                int j = i;
                while (bits.get(j) && j++ < len) {

                }

            }*/

            return "";
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



    // custom mapping of characters as bits
    private static final Map<Character, String> BIT_MORSE;

    static {
        HashMap<Character, String> morseMap = new HashMap<>();
        morseMap.put('a',".....");
        morseMap.put('b',"....-");
        morseMap.put('c',"...-.");
        morseMap.put('d',"...--");
        morseMap.put('e',"..-..");
        morseMap.put('f',"..-.-");
        morseMap.put('g',"..--.");
        morseMap.put('h',"..---");
        morseMap.put('i',".-...");
        morseMap.put('j',".-..-");
        morseMap.put('k',".-.-.");
        morseMap.put('l',".-.--");
        morseMap.put('m',".--..");
        morseMap.put('n',".--.-");
        morseMap.put('o',".---.");
        morseMap.put('p',".----");
        morseMap.put('q',"-....");
        morseMap.put('r',"-...-");
        morseMap.put('s',"-..-.");
        morseMap.put('t',"-..--");
        morseMap.put('u',"-.-..");
        morseMap.put('v',"-.-.-");
        morseMap.put('w',"-.--.");
        morseMap.put('x',"-.---");
        morseMap.put('y',"--...");
        morseMap.put('z',"--..-");
        morseMap.put(' ',"--.-.");
        BIT_MORSE = Collections.unmodifiableMap(morseMap);
    }

    /**
     * A simple alphabet where letters are encoded as a sequence of bits.
     */
    public static final Alphabet BIT_ENCODING_ALPHABET = new Alphabet() {
        @Override
        public String name() {
            return "Bit Encoding Alphabet";
        }

        final int bitLength = BIT_MORSE.get('a').length();
        final Set<Character> keys = BIT_MORSE.keySet();

        @Override
        String get(char c) {
            if (BIT_MORSE.containsKey(c))
                return BIT_MORSE.get(c);
            else
                return "";
        }

        @Override
        byte getInner(char innerChar) {
            switch (innerChar) {
                case '.':
                default:
                    return -1;
                case '-':
                    return 1;
            }
        }

        private Character getByString(String test) {
            if (test.length() != bitLength)
                return null;

            for (Character c : keys) {
                if (BIT_MORSE.get(c).equals(test))
                    return c;
            }

            return null;
        }

        /** bits are zero and one (. and -) */
        private int getIntFromBits(String bits, char one) {
            int num = 0;
            char[] chars = bits.toCharArray();
            for (int i = 0; i < chars.length; i++) {
                char c = chars[i];

                if (c == one) {
                    num = num | (1 << (chars.length - (i + 1)));
                }
            }

            return num;
        }

        @Override
        String decodeMessage(List<Boolean> bits) {
            int len = bits.size();
            StringBuilder message = new StringBuilder("");

            for (int i = 0; i <= len - bitLength; i += bitLength) {
                StringBuilder s = new StringBuilder(bitLength);
                for (int j = 0; j < bitLength; j++) {
                    if (bits.get(i + j)) {
                        s.append('-');
                    } else {
                        s.append('.');
                    }
                }

                Character next = getByString(s.toString());

                if (null != next) {
                    message.append(next);
                }
            }

            return message.toString();
        }
    };

    public static final Alphabet[] ALL = {INTERNATIONAL_MORSE, BIT_ENCODING_ALPHABET};
}
