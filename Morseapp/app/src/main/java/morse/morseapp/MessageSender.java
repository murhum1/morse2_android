package morse.morseapp;

import android.hardware.Camera;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by juho on 2.10.15.
 */
public class MessageSender extends Thread {

    private String msg;
    private Camera.Parameters params;

    private int FPS = 10;

    private boolean short_is_none = true;
    private boolean flash_on = false;

    private boolean sending_message = false;

    static Map<String, String> code = new HashMap<String, String>();

    public static enum Mode {
        DOT_IS_SHORT_FLASH, DOT_IS_OFF
    }

    Mode morseMode;

    public MessageSender() {
        code.put("a", "-....-");
        code.put("b", "-.....");
        code.put("c", ".-....");
        code.put("d", "--....");
        code.put("e", "..-...");
        code.put("f", "-.-...");
        code.put("g", ".--...");
        code.put("h", "---...");
        code.put("i", "...-..");
        code.put("j", "-..-..");
        code.put("k", "--.-..");
        code.put("k", "-.--..");
        code.put("l", "----..");
        code.put("m", "....-.");
        code.put("n", "-...-.");
        code.put("o", "--..-.");
        code.put("p", "---.-.");
        code.put("q", "-.---.");
        code.put("r", "--.--.");
        code.put("s", "-----.");
        code.put("t", "-.-.-.");
        code.put("u", ".----.");
        code.put("v", "-.-.-.");
        code.put("x", ".....-");
        code.put("z", "....--");
        code.put("w", "...---");
        code.put(" ", "..---");
    }
    @Override
    public void run () {
        params = MainActivity.cam.getParameters();
        try {
            Log.i("MORSE", "Current Unit length: " + Integer.toString((1000 / FPS) * 1));
            for (char c : msg.toCharArray()) {

                if(morseMode == Mode.DOT_IS_SHORT_FLASH) {
                    if (c == '.') {
                        MainActivity.turnOn();
                        Thread.sleep((1000 / FPS) * 1);
                        MainActivity.turnOff();

                    } else if (c == '-') {
                        MainActivity.turnOn();
                        Thread.sleep((1000 / FPS) * 3);
                        MainActivity.turnOff();
                    }

                    // wait between characters so we can distinguish different chars
                    Thread.sleep((1000 / FPS) * 2);

                } else {
                    if (c == '.') {
                        if(flash_on)
                            MainActivity.turnOff();
                        Thread.sleep(((1000 / FPS) * 1));
                        flash_on = false;

                    } else if (c == '-') {
                        if(!flash_on)
                            MainActivity.turnOn();
                        Thread.sleep((1000 / FPS) * 1);
                        flash_on = true;
                    }
                }
            }
            MainActivity.turnOff();
            sending_message = false;
        } catch (Exception e) {
            Log.e("MORSE", "Error: " + e.toString());
            sending_message = false;
        }
    }


    public void sendMessage(String message, int FPS, Mode morseMode) {
        if(sending_message)
            return;

        sending_message = true;

        this.morseMode = morseMode;
        this.FPS = FPS;
        this.msg = convertToMorse(message);

        this.run();
    }

    public static String convertToMorse(String message) {
        String morseMsg = "";
        for(char c : message.toCharArray()) {
            String morseChar = Character.toString(c);
            if(morseChar != null)
                morseMsg += code.get(morseChar);
        }

        Log.i("MORSE", "Morse String: " + morseMsg);
        return morseMsg;
    }

}
