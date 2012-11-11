package su.jit.nko3.ten_nine_eight_seven;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import android.graphics.Color;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;

public class Recorder {
	
	public static final int CAMERA = 0;

	public void enable() {
		Log.e("recorder", "enable");
		if (camera == null) {
			camera = Camera.open(0);
			try {
				camera.setPreviewDisplay(surface.getHolder());
				camera.startPreview();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void socketAvailable(InetSocketAddress obj) {
		Log.e("recorder", "socket available" + obj.toString());
		this.socketAddress = obj;
		showButton();
	}

	public void disable() {
		Log.e("recorder", "disable");
		stop();
		camera.stopPreview();
		camera.release();
		camera = null;
		if (socket != null) {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			socket = null;
		}
		hideButton();
	}

	protected boolean recording = false;
	protected ImageButton button;
	protected Camera camera;
	protected MediaRecorder recorder;
	protected Socket socket;
	protected InetSocketAddress socketAddress;
	protected SurfaceView surface;
	protected Main context;

	Recorder(Main context) {

		this.context = context;

		SurfaceView surface = (SurfaceView) context
				.findViewById(R.id.recordView);
		ImageButton button = (ImageButton) context
				.findViewById(R.id.recordButton);
		initializeButton(button);
		initializeSurface(surface);

	}

	private void initializeButton(ImageButton button) {
		this.button = button;
		hideButton();
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				toggleRecording();
			}
		});
	}

	private void initializeSurface(SurfaceView surface) {
		this.camera = Camera.open(CAMERA);
		this.surface = surface;

		setCameraDisplayOrientation(CAMERA, this.camera);

		surface.getHolder().addCallback(new SurfaceHolder.Callback() {
			@Override
			public void surfaceCreated(SurfaceHolder holder) {
			} // wait until surfaceChanged()

			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
			}

			@Override
			public void surfaceChanged(SurfaceHolder holder, int format,
					int width, int height) {
				try {
					camera.setPreviewDisplay(holder);
					camera.startPreview();

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}

	protected void hideButton() {
		button.setVisibility(View.INVISIBLE);
	}

	protected void showButton() {
		button.setVisibility(View.VISIBLE);
	}

	protected void start() {
		Log.e("recorder", "start");
		camera.unlock();
		recorder = new MediaRecorder();
		recorder.setCamera(camera);
		AsyncTask<Void, Void, Void> startRecording = new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
				recorder.setOutputFormat(8);
				recorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
				try {
					socket = new Socket();
					socket.connect(socketAddress);
					recorder.setOutputFile(ParcelFileDescriptor.fromSocket(
							socket).getFileDescriptor());
					recorder.prepare();
					recorder.start();
					recording = true;
				} catch (Exception e) {
					recorder.reset();
					try {
						camera.reconnect();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					e.printStackTrace();
				}
				return null;
			}
		};
		startRecording.execute();
		button.setColorFilter(Color.argb(150, 255, 0, 0));
	}

	protected void stop() {
		Log.e("recorder", "stop");
		if (recorder != null) {
			recorder.reset();
			recorder.release();
			recorder = null;
		}
		try {
			camera.reconnect();
		} catch (IOException e) {
			e.printStackTrace();
		}
		recording = false;
		button.setColorFilter(Color.argb(0, 255, 0, 0));
	}

	protected void toggleRecording() {
		if (recording) {
			stop();
		} else {
			start();
		}
	}

	public void setCameraDisplayOrientation(int cameraId,
			android.hardware.Camera camera) {
		android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
		android.hardware.Camera.getCameraInfo(cameraId, info);

		int rotation = context.getWindowManager().getDefaultDisplay()
				.getRotation();
		int degrees = 0;
		switch (rotation) {
		case Surface.ROTATION_0:
			degrees = 0;
			break;
		case Surface.ROTATION_90:
			degrees = 90;
			break;
		case Surface.ROTATION_180:
			degrees = 180;
			break;
		case Surface.ROTATION_270:
			degrees = 270;
			break;
		}

		int result;
		if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
			result = (info.orientation + degrees) % 360;
			result = (360 - result) % 360; // compensate the mirror
		} else { // back-facing
			result = (info.orientation - degrees + 360) % 360;
		}
		camera.setDisplayOrientation(result);
	}
}
