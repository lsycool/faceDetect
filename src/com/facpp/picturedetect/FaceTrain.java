package com.facpp.picturedetect;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import org.json.JSONObject;
import com.facepp.error.FaceppParseException;
import com.facepp.http.HttpRequests;
import com.facepp.http.PostParameters;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PixelFormat;


public class FaceTrain extends Activity {

	//private SurfaceView sufaceView;
	private ImageView sufaceView;
	// 存储照片数据
	private byte[] array = null;

	public static HttpRequests httpRequests = new HttpRequests("71933deeb624e333e476215396ff981c", "x0-sZ0eVnqWg2gU2rARhv8uYnbSfNx4E", true, false);

	private TextView textView;
	private EditText editView;

	private boolean flag = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 窗口去掉标题
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 窗口设置为全屏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// 设置窗口为半透明
		getWindow().setFormat(PixelFormat.TRANSLUCENT);
		setContentView(R.layout.face_train);	

		textView = (TextView)this.findViewById(R.id.textView_trainlog);
		editView = (EditText)this.findViewById(R.id.editText1);
		sufaceView = (ImageView) this.findViewById(R.id.imageView_faceshow);

		Button button = (Button) this.findViewById(R.id.button_cameraopen);// 获取影像
		button.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				// 调用系统照相APP
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(intent, 1);
				flag = true;
			}
		});

		Button button1 = (Button) this.findViewById(R.id.button_trainface);// 进行训练
		button1.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {

				if(flag!= true && array == null){
					FaceTrain.this.runOnUiThread(new Runnable() {

						public void run() {
							textView.setText("请先获取影像数据。");
						}
					});
					return;
				}

				//				final ArrayList<String> personinfoList = new ArrayList<String>();
				FaceTrain.this.runOnUiThread(new Runnable() {

					public void run() {
						textView.setText("正在训练。。");
					}
				});
				
				new Thread(new Runnable() {
					public void run() {
						try {
							httpRequests.trainSearch(new PostParameters().setFacesetName("faceset_lsy0"));
							FaceTrain.this.runOnUiThread(new Runnable() {

								public void run() {
									textView.setText("训练成功。。");
								}
							});
						} catch (final FaceppParseException e) {
							// TODO Auto-generated catch block
							FaceTrain.this.runOnUiThread(new Runnable() {

								public void run() {
									textView.setText(e.getMessage().toString());
								}
							});
						}
					}	
				}).start();
			}
		});
	}

	@Override  
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{  
		// TODO Auto-generated method stub  
		super.onActivityResult(requestCode, resultCode, data);  
		if (resultCode == Activity.RESULT_OK) 
		{  
			FaceTrain.this.runOnUiThread(new Runnable() {

				public void run() {
					textView.setText("正在检测。。");
				}
			});

			Bundle bundle = data.getExtras();  
			Bitmap bitmap = (Bitmap) bundle.get("data");// 获取相机返回的数据，并转换为Bitmap图片格式  	

			ByteArrayOutputStream stream = new ByteArrayOutputStream();//可以捕获内存缓冲区的数据，转换成字节数组
			float scale = Math.min(1, Math.min(600f / bitmap.getWidth(), 600f / bitmap.getHeight()));//控制照片规模
			Matrix matrix = new Matrix();
			matrix.postScale(scale, scale);
			final Bitmap imgSmall = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
			imgSmall.compress(Bitmap.CompressFormat.JPEG, 100, stream);//把bitmap写入stream流中
			
//			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);//把bitmap写入stream流中
			array = stream.toByteArray();//把stream转化成byte数组
			sufaceView.setImageBitmap(bitmap);// 将图片显示在ImageView里  

			FaceppDetect faceppDetect = new FaceppDetect();
			faceppDetect.setDetectCallback(new DetectCallback() {

				public void detectResult(JSONObject rst){

					try{
						int count = rst.getJSONArray("face").length();
						if(count == 0){								
							throw new FaceppParseException("没有检测到人脸.");
						}
						//find out all faces
						httpRequests.facesetAddFace(new PostParameters().setFacesetName("faceset_lsy0").setFaceId(rst.getJSONArray("face").getJSONObject(0).getString("face_id")));
						final ArrayList<String> personinfoList = new ArrayList<String>();		
						String temString = editView.getText().toString().trim();;
						personinfoList.add(temString);
						temString = rst.getJSONArray("face").getJSONObject(0).getJSONObject("attribute").getJSONObject("gender").getString("value")+"";
						personinfoList.add(temString);
						temString = rst.getJSONArray("face").getJSONObject(0).getJSONObject("attribute").getJSONObject("age").getInt("value")+"";
						temString = temString +"±"+ rst.getJSONArray("face").getJSONObject(0).getJSONObject("attribute").getJSONObject("age").getInt("range")+"";
						personinfoList.add(temString);
						temString = rst.getJSONArray("face").getJSONObject(0).getJSONObject("attribute").getJSONObject("race").getString("value");
						personinfoList.add(temString);
						double smile = rst.getJSONArray("face").getJSONObject(0).getJSONObject("attribute").getJSONObject("smiling").getDouble("value");
						if (smile > 9.3) {
							temString = "傻笑";
						}else if(smile > 6){
							temString = "微笑";
						}else if (smile >3) {
							temString = "平静";
						}else {
							temString = "生气";
						}
						personinfoList.add(temString);
						temString = rst.getJSONArray("face").getJSONObject(0).getJSONObject("position").getJSONObject("center").getDouble("x")+"";
						personinfoList.add(temString);
						temString = rst.getJSONArray("face").getJSONObject(0).getJSONObject("position").getJSONObject("center").getDouble("y")+"";
						personinfoList.add(temString);
						//						
						FaceTrain.this.runOnUiThread(new Runnable() {

							public void run() {
								textView.setText("检测成功 .");
							}
						});

						Intent HelloIntent = new Intent(FaceTrain.this,Facetrain_result.class);
						Bundle mBundle=new Bundle();
						mBundle.putString("name",personinfoList.get(0));//压入数据
						mBundle.putString("gender",personinfoList.get(1));//压入数据
						mBundle.putString("age",personinfoList.get(2));//压入数据
						mBundle.putString("race",personinfoList.get(3));//压入数据
						mBundle.putString("smile",personinfoList.get(4));//压入数据
						mBundle.putString("x",personinfoList.get(5));//压入数据
						mBundle.putString("y",personinfoList.get(6));//压入数据						
						
						HelloIntent.putExtras(mBundle);
						startActivity(HelloIntent);
						//imgSmall.recycle();
					}
					catch (final Exception  e) {
						// TODO Auto-generated catch block
						FaceTrain.this.runOnUiThread(new Runnable() {

							public void run() {
								textView.setText(e.getMessage().toString());
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
						String personTag = editView.getText().toString().trim();
						//detect
						JSONObject result = httpRequests.detectionDetect(new PostParameters().setImg(data).setMode("oneface").setTag(personTag));//开始识别
						//finished , then call the callback function
						if (callback != null) {
							callback.detectResult(result);//把执行结果传入
						}
					} catch (FaceppParseException e) {
						e.printStackTrace();
						FaceTrain.this.runOnUiThread(new Runnable() {
							public void run() {
								//								Toast.makeText(getApplicationContext(), "无法连接网络", Toast.LENGTH_SHORT).show();
								textView.setText("无法连接云服务器,请检查网络设置。");
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
