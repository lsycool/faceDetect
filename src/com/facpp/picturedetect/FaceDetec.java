package com.facpp.picturedetect;

import java.io.ByteArrayOutputStream;


import org.json.JSONObject;

import com.facepp.error.FaceppParseException;
import com.facepp.http.HttpRequests;
import com.facepp.http.PostParameters;



import android.app.Activity;
import android.content.Intent;

import android.content.res.Configuration;
import android.graphics.Bitmap;


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class FaceDetec extends Activity {

	private byte[] array;
	
	private int count;

	private Bitmap bitmap;
	
	private String nameall;
	
	private ImageView sufaceView;
	
	private TextView textView;
		
	public static HttpRequests httpRequests = new HttpRequests("71933deeb624e333e476215396ff981c", "x0-sZ0eVnqWg2gU2rARhv8uYnbSfNx4E", true, false);


	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);     
		// 窗口去掉标题
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 窗口设置为全屏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// 设置窗口为半透明
		getWindow().setFormat(PixelFormat.TRANSLUCENT);
		setContentView(R.layout.face_detec);
		sufaceView = (ImageView) this.findViewById(R.id.imageView_facedc);
		textView = (TextView)this.findViewById(R.id.textView1);

		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);   
		startActivityForResult(intent, 1);  
	}
	@Override  
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{  
		// TODO Auto-generated method stub  
		super.onActivityResult(requestCode, resultCode, data);  
		if (resultCode == Activity.RESULT_OK) 
		{  

			FaceDetec.this.runOnUiThread(new Runnable() {
				
				public void run() {
					//show the image
					sufaceView.setImageBitmap(bitmap);
					textView.setText("正在识别。。");
				}
			});
			Bundle bundle = data.getExtras();  
			bitmap = (Bitmap) bundle.get("data");// 获取相机返回的数据，并转换为Bitmap图片格式  

			ByteArrayOutputStream stream = new ByteArrayOutputStream();//可以捕获内存缓冲区的数据，转换成字节数组
			float scale = Math.min(1, Math.min(600f / bitmap.getWidth(), 600f / bitmap.getHeight()));//控制照片规模
			Matrix matrix = new Matrix();
			matrix.postScale(scale, scale);
			final Bitmap imgSmall = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
			imgSmall.compress(Bitmap.CompressFormat.JPEG, 100, stream);//把bitmap写入stream流中
			
//			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
			array = stream.toByteArray();//把stream转化成byte数组	
			sufaceView.setImageBitmap(bitmap);

			FaceppDetect faceppDetect = new FaceppDetect();
			faceppDetect.setDetectCallback(new DetectCallback() {
				
				public void detectResult(JSONObject rst){
					
					nameall = "";
					Paint paint = new Paint();
					paint.setColor(Color.RED);
					paint.setStrokeWidth(Math.max(bitmap.getWidth(), bitmap.getHeight()) / 100f);//设置线宽				
					
					Bitmap bitmap1 = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
					Canvas canvas = new Canvas(bitmap1);
					canvas.drawBitmap(bitmap, new Matrix(), null);
					
					try{
						count = rst.getJSONArray("face").length();
						if(count == 0){								
							throw new FaceppParseException("没有检测到人脸.");
						}
						
						for (int i = 0; i < count; i++) {
							float x, y, w, h;
							//get the center point（获取的是百分比即相对100的值）
							x = (float)rst.getJSONArray("face").getJSONObject(i)
									.getJSONObject("position").getJSONObject("center").getDouble("x");
							y = (float)rst.getJSONArray("face").getJSONObject(i)
									.getJSONObject("position").getJSONObject("center").getDouble("y");

							//get face size
							w = (float)rst.getJSONArray("face").getJSONObject(i)
									.getJSONObject("position").getDouble("width");
							h = (float)rst.getJSONArray("face").getJSONObject(i)
									.getJSONObject("position").getDouble("height");

							x = x / 100 * bitmap.getWidth();
							w = w / 100 * bitmap.getWidth() * 0.7f;//*0.7是为了画外包矩形
							y = y / 100 * bitmap.getHeight();
							h = h / 100 * bitmap.getHeight() * 0.7f;
							
							Paint paint1 = new Paint();
							paint1.setColor(Color.RED);
							paint1.setStrokeWidth(Math.max(w/0.7f, h/0.7f) / 100f);//设置线宽

							//draw the box to mark it out（画脸的外包矩形）
							canvas.drawLine(x - w, y - h, x - w, y + h, paint);//画左竖边
							canvas.drawLine(x - w, y - h, x + w, y - h, paint);
							canvas.drawLine(x + w, y + h, x - w, y + h, paint);
							canvas.drawLine(x + w, y + h, x + w, y - h, paint);
							
							//httpRequests.trainSearch(new PostParameters().setFacesetName("faceset_lsy0"));
							JSONObject result1 = httpRequests.recognitionSearch(new PostParameters().setKeyFaceId(rst.getJSONArray("face").getJSONObject(i).getString("face_id")).setFacesetName("faceset_lsy0").setCount(1));	
							String resultname = result1.getJSONArray("candidate").getJSONObject(0).getString("tag");
							if (resultname.equals("")) {
								resultname = "未命名";
							}
							nameall = nameall + resultname + "  ";
							double similarity = result1.getJSONArray("candidate").getJSONObject(0).getDouble("similarity");
							canvas.drawText(resultname, x-w/2,y, paint1);
							if(similarity < 60){								
								throw new FaceppParseException("没有检测到人脸.");
							}
						}
						bitmap = bitmap1;
						FaceDetec.this.runOnUiThread(new Runnable() {
							
							public void run() {
								//show the image
								sufaceView.setImageBitmap(bitmap);
								//Toast.makeText(getApplicationContext(), resultname, Toast.LENGTH_SHORT).show();
								textView.setText("识别完毕共:"+ count + "张脸(" + nameall + ")");
							}
						});
						//imgSmall.recycle();
						
					}catch (final Exception  e) {
						// TODO Auto-generated catch block
						FaceDetec.this.runOnUiThread(new Runnable() {
							
							public void run() {
								textView.setText(e.getMessage());
							}
						});
					}
				}
			});
			faceppDetect.detect(array);	
		}
	}   

	@Override
	public void onConfigurationChanged(Configuration newConfig) {

		super.onConfigurationChanged(newConfig);
	}
	
	public class FaceppDetect {//对获取的图片进行检测
		DetectCallback callback = null;

		public void setDetectCallback(DetectCallback detectCallback) { 
			callback = detectCallback;
		}

		public void detect(final byte[] data ) {

			new Thread(new Runnable() {

				public void run() {

					try {
						//detect
						JSONObject result = FaceTrain.httpRequests.detectionDetect(new PostParameters().setImg(data));//开始识别
						//finished , then call the callback function
						if (callback != null) {
							callback.detectResult(result);//把执行结果传入
						}
					} catch (FaceppParseException e) {
						e.printStackTrace();
						FaceDetec.this.runOnUiThread(new Runnable() {
							public void run() {
								Toast.makeText(getApplicationContext(), "无法连接网络", Toast.LENGTH_SHORT).show();
							}
						});
					}

				}
			}).start();
		}
	}

	interface DetectCallback {
		void detectResult(JSONObject rst);
	}
}
