package morse.morseapp.message;

import android.os.Handler;

import java.util.Iterator;
import java.util.List;

import morse.morseapp.utilities.Settings;

/**
 * Created by ekreutzman on 26/10/15.
 */
public class Sender {
    private static Alphabet alphabet;
    private static Torch torch;
    private static int unitLength = 1000 / 5;

    private static boolean sending = false;
    private static Handler handler = new Handler();
    private static Iterator<Byte> iterator;

    public static void setAlphabet(Alphabet alphabet) {
        Sender.alphabet = alphabet;
    }

    public static void setTorch(Torch torch) {
        Sender.torch = torch;
    }

    private static final Runnable flasher = new Runnable() {
        @Override
        public void run() {
            if (iterator.hasNext()) {
                byte b = iterator.next();

                if (b > 0 && !torch.isOn()) {
                    torch.turnOn();
                } else if (b < 0 && torch.isOn()) {
                    torch.turnOff();
                }

                int interval = unitLength * Math.abs(b);
                handler.postDelayed(this, interval);
            } else {
                if (torch.isOn())
                    torch.turnOff();

                sending = false;
            }
        }
    };

    public static void send(String message) {
        if (sending) return;

        if (alphabet == null)
            alphabet = Alphabet.INTERNATIONAL_MORSE;

        iterator = Encoder.encode(message, alphabet).iterator();
        unitLength = Settings.getCharsPerSecond();
        sending = true;

        handler.postDelayed(flasher, 0);
    }

    public static void cancel() {
        handler.removeCallbacks(flasher);

        if (torch.isOn())
            torch.turnOff();

        sending = false;
    }
}