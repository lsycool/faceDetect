package com.facpp.picturedetect;

import java.io.IOException;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.faceplusplus.api.FaceDetecter;
import com.faceplusplus.api.FaceDetecter.Face;
import com.facpp.picturedetect.R;

public class FaceCount extends Activity implements Callback, PreviewCallback {
	private SurfaceView camerasurface = null;
	private FaceMask mask = null;
	//private TextView textView = (TextView)findViewById(R.id.textView_count1);
	Camera camera = null;
	HandlerThread handleThread = null;
	Handler detectHandler = null;
	Runnable detectRunnalbe = null;
	private int width = 320;
	private int height = 240;
	private int facenum = 0;
	private int camerafrontorback = 1;
	FaceDetecter facedetecter = null;
	MediaPlayer mp = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.face_count);
		camerasurface = (SurfaceView) findViewById(R.id.camera_preview);
		//        TextView textView = (TextView)findViewById(R.id.textView_count1);
		mask = (FaceMask) findViewById(R.id.mask);
		//        LayoutParams para = new LayoutParams(480, 800);
		mp = MediaPlayer.create(getBaseContext(), R.raw.alertmusic);

		LayoutParams para = new LayoutParams(getScreenWH().widthPixels, getScreenWH().heightPixels);

		handleThread = new HandlerThread("dt");
		handleThread.start();
		detectHandler = new Handler(handleThread.getLooper());
		para.addRule(RelativeLayout.CENTER_IN_PARENT);
		camerasurface.setLayoutParams(para);
		mask.setLayoutParams(para);
		camerasurface.getHolder().addCallback(this);
		camerasurface.setKeepScreenOn(true);
		facedetecter = new FaceDetecter();
		if (!facedetecter.init(this, "71933deeb624e333e476215396ff981c")) {
			//textView.setText("初始化失败。。");
		}
		facedetecter.setTrackingMode(true);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		mp.stop();
		return false;
	}

//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//
//		if (keyCode == KeyEvent.KEYCODE_CAMERA || keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
//			//切换前后摄像头
//			int cameraCount = 0;
//			CameraInfo cameraInfo = new CameraInfo();
//			cameraCount = Camera.getNumberOfCameras();//得到摄像头的个数
//
//			for(int i = 0; i < cameraCount; i++   ) {
//
//				Camera.getCameraInfo(i, cameraInfo);//得到每一个摄像头的信息
//				if(camerafrontorback == 1) {
//					//现在是后置，变更为前置
//					if(cameraInfo.facing  == Camera.CameraInfo.CAMERA_FACING_FRONT) {//代表摄像头的方位，CAMERA_FACING_FRONT前置      CAMERA_FACING_BACK后置  
//						camera.stopPreview();//停掉原来摄像头的预览
//						camera.release();//释放资源
//						camera = null;//取消原来摄像头
//						//camera = Camera.open(i);//打开当前选中的摄像头
//						//camera.startPreview();//开始预览
//						camerafrontorback = 0;
//						camera.setPreviewCallback(this);
//						break;
//					}
//				} else {
//					//现在是前置， 变更为后置
//					if(cameraInfo.facing  == Camera.CameraInfo.CAMERA_FACING_BACK) {//代表摄像头的方位，CAMERA_FACING_FRONT前置      CAMERA_FACING_BACK后置  
//						camera.stopPreview();//停掉原来摄像头的预览
//						camera.release();//释放资源
//						camera = null;//取消原来摄像头
//						camera = Camera.open(i);//打开当前选中的摄像头
////						camera.startPreview();//开始预览
//						camerafrontorback = 1;
//						camera.setPreviewCallback(this);
//						break;
//					}
//				}
//
//			}
//		}
//		return super.onKeyDown(keyCode, event);
//	}

	@Override
	protected void onResume() {
		super.onResume();
		camera = Camera.open(camerafrontorback);
		Camera.Parameters para = camera.getParameters();
		Size previewSize = findBestPreviewSize(para,camera);
		width = previewSize.width;
		height = previewSize.height;
		para.setPreviewSize(previewSize.width,previewSize.height);		
		// para.setPreviewSize(width, height);
		camera.setParameters(para);
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (camera != null) {
			camera.setPreviewCallback(null);
			camera.stopPreview();
			camera.release();
			finish();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		facedetecter.release(this);
		handleThread.quit();
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
	}


	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

		try {
			camera.setPreviewDisplay(holder);
		} catch (IOException e) {
			e.printStackTrace();
		}
		MyOrientationDetector cameraOrientation = new MyOrientationDetector(getBaseContext());
		if (camera != null) {
			int orientation = cameraOrientation.getOrientation();
			Camera.Parameters para = camera.getParameters();
			para.setRotation(90);
			para.set("rotation", 90);
			camera.setDisplayOrientation(90);
			if ((orientation >= 45) && (orientation < 135)) {
				para.setRotation(180);
				para.set("rotation", 180);
				camera.setDisplayOrientation(180);
			}
			if ((orientation >= 135) && (orientation < 225)) {
				para.setRotation(270);
				para.set("rotation", 270);
				camera.setDisplayOrientation(270);
			}
			if ((orientation >= 225) && (orientation < 315)) {
				para.setRotation(0);
				para.set("rotation", 0);
				camera.setDisplayOrientation(0);
			}
			camera.setParameters(para);
		}
		//camera.setDisplayOrientation(90);
		camera.startPreview();
		camera.setPreviewCallback(this);

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {

	}


	@Override
	public void onPreviewFrame(final byte[] data, Camera camera) {
		camera.setPreviewCallback(null);
		detectHandler.post(new Runnable() {

			@Override
			public void run() {
				//byte[] ori = new byte[width * height];
				//              Bitmap mBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
				//    			ByteArrayOutputStream stream = new ByteArrayOutputStream();
				//    			float scale = Math.min(1, Math.min(600f / mBitmap.getWidth(), 600f / mBitmap.getHeight()));//控制照片规模
				//    			Matrix matrix = new Matrix();
				//    			matrix.postScale(scale, scale);
				//    			Bitmap imgSmall = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), matrix, false);
				//    			imgSmall.compress(Bitmap.CompressFormat.JPEG, 100, stream);
				//    			byte[] ori1 = stream.toByteArray();//数据压缩
				//    			byte[] ori = new byte[Integer.parseInt(new java.text.DecimalFormat("0").format(width * height * (0.25)))];

				int newWidth = (width+3)/4;
				int newHeight = (height+3)/4;
				byte[] ori = new byte[newWidth * newHeight];

				int is = 0;
				for (int x = width - 1; x >= 0; x=x-4) {

					for (int y = height - 1; y >= 0; y=y-4) {

						ori[is] = data[y * width + x];
						is++;
					}
				}

				int curfacenum = 0;
				final Face[] faceinfo = facedetecter.findFaces( ori, newHeight, newWidth);
				if (faceinfo != null) {
					curfacenum = faceinfo.length;					
				}else {
					curfacenum = 0;
				}

				if (facenum != curfacenum) {
					//                	mp = MediaPlayer.create(getBaseContext(), R.raw.alertmusic);
					mp.start();
					facenum = curfacenum;
				}
				else {
					mp.pause();
				}

				FaceCount.this.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						mask.setFaceInfo(faceinfo);//画外包矩形框                     
						TextView textView = (TextView)findViewById(R.id.textView_count1);
						textView.setText("现场有" + facenum + "人");
					}
				});
				FaceCount.this.camera.setPreviewCallback(FaceCount.this);
			}
		});
	}


	private Size findBestPreviewSize(Camera.Parameters parameters, Camera mCamera) {

		final Pattern COMMA_PATTERN = Pattern.compile(",");
		String previewSizeValueString = null;
		int diff = Integer.MAX_VALUE;
		previewSizeValueString = parameters.get("preview-size-values");

		if (previewSizeValueString == null) {
			previewSizeValueString = parameters.get("preview-size-value");
		}

		if(previewSizeValueString == null) {  // 有些手机例如m9获取不到支持的预览大小   就直接返回屏幕大小
			return  mCamera.new Size(getScreenWH().widthPixels,getScreenWH().heightPixels);
		}

		int bestX = 0;
		int bestY = 0;

		for(String prewsizeString : COMMA_PATTERN.split(previewSizeValueString))
		{
			prewsizeString = prewsizeString.trim();

			int dimPosition = prewsizeString.indexOf('x');
			if(dimPosition == -1){
				continue;
			}

			int newX = 0;
			int newY = 0;

			try{
				newX = Integer.parseInt(prewsizeString.substring(0, dimPosition));
				newY = Integer.parseInt(prewsizeString.substring(dimPosition+1));
			}catch(NumberFormatException e){
				continue;
			}

			Point screenResolution = new Point (getScreenWH().widthPixels,getScreenWH().heightPixels);

			int newDiff = Math.abs(newX - screenResolution.x)+Math.abs(newY- screenResolution.y);

			if(newDiff == diff)
			{
				bestX = newX;
				bestY = newY;
				break;
			} else if(newDiff < diff){
				if((3 * newX) == (4 * newY)) {
					bestX = newX;
					bestY = newY;
					diff = newDiff;
				}
			}
		}
		if (bestX > 0 && bestY > 0) {
			return mCamera.new Size(bestX, bestY);
		}
		return null;
	}


	protected DisplayMetrics getScreenWH() {
		DisplayMetrics dMetrics = new DisplayMetrics();
		dMetrics = this.getResources().getDisplayMetrics();
		return dMetrics;
	}  
	//方向变化监听器，监听传感器方向的改变

	public class MyOrientationDetector extends OrientationEventListener {
		int Orientation;

		public MyOrientationDetector(Context context) {
			super(context);
		}

		@Override
		public void onOrientationChanged(int orientation) {

			this.Orientation = orientation;		
		}

		public int getOrientation() {
			return Orientation;
		}
	}
}
