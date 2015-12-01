package morse.morseapp.message;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
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

    public abstract char getChar(String s);

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
    protected byte getLetterSpacing() { return -6; }

    /** the spacing between characters. In morse, this value is -1 (one unit flash off) */
    protected byte getInnerCharacterSpacing() { return -3; }

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
    public static final Map<Character, String> INTERNATIONAL_MORSE_MAP;

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
        morseMap.put(' ',".-.-");
        morseMap.put('#',"----");
        morseMap.put('.',"...-.");
        morseMap.put('!',"---.");
        morseMap.put('?',"..--");
        morseMap.put(',',"....-");
        INTERNATIONAL_MORSE_MAP = Collections.unmodifiableMap(morseMap);
    }


    // inverse map of the international morse alphabet's inner characters
    public static final Map<String, Character> INTERNATIONAL_MORSE_MAP_INVERSE;
    static{
        HashMap<String, Character> morseMap = new HashMap<>();

        Iterator it = Alphabet.INTERNATIONAL_MORSE_MAP.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            morseMap.put((String)pair.getValue(), (Character)pair.getKey());
        }
        INTERNATIONAL_MORSE_MAP_INVERSE = morseMap;
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
            if (INTERNATIONAL_MORSE_MAP.containsKey(c))
                return INTERNATIONAL_MORSE_MAP.get(c);
            else
                return "";
        }

        @Override
        public char getChar(String s) {
            if (INTERNATIONAL_MORSE_MAP_INVERSE.containsKey(s))
                return INTERNATIONAL_MORSE_MAP_INVERSE.get(s);
            else
                return '*';
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
            return -2;
        }

        @Override
        protected byte getLetterSpacing() {
            return -6;
        }
    };



    // custom mapping of characters as bits
    private static final Map<Character, String> BIT_MORSE;
    private static final Map<String, Character> BIT_MORSE_INVERSE;

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

    static {
        HashMap<String, Character> morseMap = new HashMap<>();

        Iterator it = Alphabet.BIT_MORSE.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            morseMap.put((String)pair.getValue(), (Character)pair.getKey());
        }
        BIT_MORSE_INVERSE = morseMap;
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

        public char getChar(String s) {
            if (BIT_MORSE_INVERSE.containsKey(s)) {
                return BIT_MORSE_INVERSE.get(s);
            }
            else {
                return '*';
            }
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
