package morse.morseapp;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;

import morse.morseapp.MessageSender.Mode;

public class MainActivity extends Activity {

    static Camera cam;
    private Camera.Parameters params;
    MessageSender msgSender = new MessageSender();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void sendMessage(View view) {

        String message = ((EditText) findViewById(R.id.messageField)).getText().toString();
        int fps = Integer.parseInt(((EditText) findViewById(R.id.fpsField)).getText().toString());

        Mode morseMode = ((RadioButton) findViewById(R.id.flashOffRadio)).isChecked() ? Mode.DOT_IS_OFF : Mode.DOT_IS_SHORT_FLASH;

        msgSender.sendMessage(message, fps, morseMode);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Initialize camera
        cam = Camera.open();
        params = cam.getParameters();
        cam.startPreview();
    }

    @Override
    protected void onPause() {
        super.onDestroy();
        cam.stopPreview();
        cam.release();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
