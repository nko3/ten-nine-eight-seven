package su.jit.nko3.ten_nine_eight_seven;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.SurfaceView;
import android.widget.ImageButton;

public class Main extends Activity {

    private Connection _connection;
	private LocationService _locationService;
	private Recorder _recorder;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        _connection = new Connection();
        _locationService = new LocationService(this, _connection);
        
        SurfaceView surface = (SurfaceView) findViewById(R.id.recordView);
        ImageButton button = (ImageButton) findViewById(R.id.recordButton);
        _recorder = new Recorder(surface, button);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    @Override
	public void onResume() {
    	_locationService.start();
    	_recorder.enable();
    	_connection.start(_recorder);
		super.onResume();
		
	}
	
	@Override
	public void onPause() {
		_locationService.stop();
		_recorder.disable();
		super.onPause();
	}
	
	@Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
