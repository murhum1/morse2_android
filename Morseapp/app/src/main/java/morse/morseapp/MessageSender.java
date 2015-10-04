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

    private Mode morseMode;
    private int FPS = 10;
    private boolean flash_on = false;
    private boolean sending_message = false;

    static Map<String, String> code = new HashMap<String, String>();

    public static enum Mode {
        DOT_IS_SHORT_FLASH, DOT_IS_OFF
    }



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
    public void run() {
        params = MainActivity.cam.getParameters();
        try {
            Log.i(MainActivity.APP_TAG, "Current Unit length: " + Integer.toString((1000 / FPS) * 1));
            for (char c : msg.toCharArray()) {

                if(morseMode == Mode.DOT_IS_SHORT_FLASH) {
                    if (c == '.') {
                        MainActivity.turnCameraOn();
                        Thread.sleep((1000 / FPS) * 1);
                        MainActivity.turnCameraOff();

                    } else if (c == '-') {
                        MainActivity.turnCameraOn();
                        Thread.sleep((1000 / FPS) * 3);
                        MainActivity.turnCameraOff();
                    }

                    // wait between characters so we can distinguish different chars
                    Thread.sleep((1000 / FPS) * 2);

                } else if (morseMode == Mode.DOT_IS_OFF){
                    if (c == '.') {
                        if(flash_on)
                            MainActivity.turnCameraOff();
                        Thread.sleep(((1000 / FPS) * 1));
                        flash_on = false;

                    } else if (c == '-') {
                        if(!flash_on)
                            MainActivity.turnCameraOn();
                        Thread.sleep((1000 / FPS) * 1);
                        flash_on = true;
                    }
                }
            }
            MainActivity.turnCameraOff();
            sending_message = false;
        } catch (Exception e) {
            Log.e(MainActivity.APP_TAG, "Error: " + e.toString());
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

        this.start();
    }

    public static String convertToMorse(String message) {
        String morseMsg = "";
        for(char c : message.toCharArray()) {
            String morseChar = Character.toString(c);
            if(morseChar != null)
                morseMsg += code.get(morseChar);
        }

        Log.i(MainActivity.APP_TAG, "Morse String: " + morseMsg);
        return morseMsg;
    }

    public boolean isSendingMessage() {
        return sending_message;
    }

}
