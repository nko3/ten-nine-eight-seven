package su.jit.nko3.ten_nine_eight_seven;

import java.net.InetSocketAddress;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.widget.TextView;

public class Main extends Activity {

	private Recorder _recorder;
	public static Handler statusHandler;
	public static Handler socketHandler;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		socketHandler = new Handler() {
			@Override
			public void handleMessage(Message message) {
				_recorder.socketAvailable((InetSocketAddress) message.obj);
			}
		};
		statusHandler = new Handler() {
			@Override
			public void handleMessage(Message message) {
				String text = (String) message.obj;
				TextView textView = (TextView) findViewById(R.id.statusView);
				textView.setText(text);
			}
		};

		FragmentManager fragmentManager = getFragmentManager();

		if (fragmentManager.findFragmentByTag("connectionFragment") == null) {
			FragmentTransaction fragmentTransaction = fragmentManager
					.beginTransaction();
			ConnectionFragment fragment = new ConnectionFragment();
			fragmentTransaction.add(fragment, "connectionFragment");
			fragmentTransaction.commit();
		}
		_recorder = new Recorder(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onResume() {
		_recorder.enable();
		super.onResume();

	}

	@Override
	public void onPause() {
		_recorder.disable();
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
