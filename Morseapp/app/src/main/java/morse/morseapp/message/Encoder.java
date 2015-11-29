package morse.morseapp.message;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class that converts a message such as "hello world" to a list of bytes.
 * The byte list will contain 'units' of flash on/off, where a negative number -x means "x units flash off".
 *
 * The encode message also needs an alphabet (eg. INTERNATIONAL_MORSE) to map letters to bytes.
 * For morse, the message "a" whose morse code is ".-" would return a list { 1, -1, 3 } (flash on 1 unit, off 1 unit, on 3 units)
 *
 * Naturally, this class isn't concerned with the millisecond length of each unit.
 * */
public class Encoder {
    public static ArrayList<Byte> encode(String message, Alphabet alphabet) {
        char[] chars = message.toCharArray();

        ArrayList<Byte> bytes = new ArrayList<>();

        for (char letter : chars) {
            if (alphabet.hasLetterSpacing()) {
                bytes.add(alphabet.getLetterSpacing());
            }

            char[] inner = alphabet.get(letter).toCharArray();

            byte pb = 0;
            for (char c : inner) {
                if (pb != 0 && alphabet.hasInnerCharacterSpacing()) {
                    bytes.add(alphabet.getInnerCharacterSpacing());
                }

                byte b = alphabet.getInner(c);
                pb = b;
                bytes.add(b);
            }
        }

        return bytes;
    }

    /**
     * Decodes a message received by some sort of receiver.
     * @param bits: the response as a sequence of bits (false is one unit of light off, true is one unit light on)
     * @param alphabet: the alphabet the message was encoded in
     * @returns the decoded string
     */
    public static String decode(List<Boolean> bits, Alphabet alphabet) {
        // decoding done separately for different alphabets to improve efficiency
        return alphabet.decodeMessage(bits);
    }
}
