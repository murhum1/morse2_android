package morse.morseapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by juho on 2.10.15.
 */
public class MessageSender extends Thread {

    MainActivity mainActivity;
    Torch torch;


    // The message in encoded format
    private String morseMessage;
    static Map<String, String> code = new HashMap<String, String>();

    private Mode morseMode;
    private int FPS = 10;
    private boolean flash_on = false;

    private boolean sending_message = false;

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
        try {
            int turnOnDelay = torch.getTurnOnDelay();
            int turnOffDelay = torch.getTurnOffDelay();

            if(turnOffDelay > (1000/FPS) || (turnOnDelay > (1000/FPS)))
                throw new Exception("Torch turn on/off delay can't be longer than unit length. Timing would be off!");

            Log.i(MainActivity.APP_TAG, "Current Unit length: " + Integer.toString((1000 / FPS) * 1));

            for (char c : morseMessage.toCharArray()) {

                if (morseMode == Mode.DOT_IS_SHORT_FLASH) {

                    if (c == '.') {
                        torch.turnOn();
                        Thread.sleep((1000 / FPS) * 1 - turnOffDelay);
                        torch.turnOff();

                    } else if (c == '-') {
                        torch.turnOn();
                        Thread.sleep((1000 / FPS) * 3 - turnOffDelay);
                        torch.turnOff();
                    }

                    // wait between characters so we can distinguish different chars
                    Thread.sleep((1000 / FPS) * 2 - turnOnDelay);

                } else if (morseMode == Mode.DOT_IS_OFF) {
                    if (c == '.') {
                        if (flash_on)
                            torch.turnOff();
                        Thread.sleep(((1000 / FPS) * 1));
                        flash_on = false;

                    } else if (c == '-') {
                        if (!flash_on)
                            torch.turnOn();
                        Thread.sleep((1000 / FPS) * 1);
                        flash_on = true;
                    }
                }
            }
            torch.turnOff();
            sending_message = false;
        } catch (Exception e) {
            Log.e(MainActivity.APP_TAG, "Sending message failed: " + e.toString());
            sending_message = false;
            sendError(e);
        }
    }


    public void sendMessage(String message, int FPS, Mode morseMode, Torch torch, MainActivity mainActivity) {
        if (isSendingMessage())
            return;

        sending_message = true;

        this.morseMode = morseMode;
        this.FPS = FPS;
        this.morseMessage = convertToMorse(message);
        this.torch = torch;
        this.mainActivity = mainActivity;

        this.start();
    }

    /*
           Converts a string to morse. The language is defined in MessageSender.code
    */
    public static String convertToMorse(String message) {
        String morseMsg = "";
        for (char c : message.toCharArray()) {
            String morseChar = Character.toString(c);
            if (morseChar != null)
                morseMsg += code.get(morseChar);
        }

        Log.i(MainActivity.APP_TAG, "Morse String: " + morseMsg);
        return morseMsg;
    }

    public boolean isSendingMessage() {
        return sending_message;
    }

    public void sendError(Exception e) {
        final Exception fe = e;
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mainActivity.displayError(fe);
            }
        });
    }

}
