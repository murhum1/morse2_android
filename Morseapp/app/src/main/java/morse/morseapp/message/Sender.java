package morse.morseapp.message;

import android.os.Handler;

import java.util.Iterator;
import java.util.List;

import morse.morseapp.utilities.Settings;
/**
 * Send morse messages by using the camera flash.
 */
public class Sender {
    private static Alphabet alphabet;
    private static Torch torch;
    private static int unitLength = 1000 / 5;

    private static boolean sending = false;
    private static boolean vibrateOnDone = true;
    private static Handler handler = new Handler();
    private static Iterator<Byte> iterator;

    public static void setAlphabet(Alphabet alphabet) {
        Sender.alphabet = alphabet;
    }

    public static void setTorch(Torch torch) {
        Sender.torch = torch;
    }

    public static void setVibrateOnDone(boolean vibrateOnDone) {
        Sender.vibrateOnDone = vibrateOnDone;
    }

    private static final Runnable flasher = new Runnable() {
        @Override
        public void run() {
            if (iterator.hasNext()) {
                byte b = iterator.next();

                if (b > 0) {
                    torch.turnOn();
                } else if (b < 0) {
                    torch.turnOff();
                }

                int interval = unitLength * Math.abs(b);
                handler.postDelayed(this, interval);
            } else {
                torch.turnOff();
                sending = false;

                if (vibrateOnDone)
                    Settings.vibrate(50);
            }
        }
    };

    public static void send(final String message) {
        if (sending) return;

        iterator = Encoder.encode(message, alphabet).iterator();
        unitLength = 1000 / Settings.getFrequency();
        sending = true;

        handler.postDelayed(flasher, 0);
    }

    public static void cancel() {
        if (null != handler)
            handler.removeCallbacks(flasher);

        if (null != torch)
            torch.turnOff();

        sending = false;
    }
}
