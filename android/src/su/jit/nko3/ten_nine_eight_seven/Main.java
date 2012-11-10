package su.jit.nko3.ten_nine_eight_seven;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class Main extends Activity {

    private Client client;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        client = new Client(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    @Override
	public void onResume() {
		super.onResume();
		client.onResume();
	}
	
	@Override
	public void onPause() {
		client.onPause();
		super.onPause();
	}
	
	@Override
    protected void onDestroy() {
        client.onDestroy();
        super.onDestroy();
    }
}
