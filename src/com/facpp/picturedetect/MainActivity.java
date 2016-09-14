package com.facpp.picturedetect;

import java.io.ByteArrayOutputStream;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.provider.MediaStore.Images.ImageColumns;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facepp.error.FaceppParseException;
import com.facepp.http.HttpRequests;
import com.facepp.http.PostParameters;

/**
 * A simple demo, get a picture form your phone<br />
 * Use the facepp api to detect<br />
 * Find all face on the picture, and mark them out.
 * @author moon5ckq
 */
public class MainActivity extends Activity {

	final private static String TAG = "MainActivity";
	final private int PICTURE_CHOOSE = 1;
	
	private ImageView imageView = null;
	private Bitmap img = null;
	private Button buttonDetect = null;
	private TextView textView = null;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        Button button = (Button)this.findViewById(R.id.button1);//打开图像
        button.setOnClickListener(new OnClickListener() {
			
			public void onClick(View arg0) {
				//get a picture form your phone
				Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
		        photoPickerIntent.setType("image/*");//强制指定intent数据类型
		        startActivityForResult(photoPickerIntent, PICTURE_CHOOSE);//会调用后面的onActivityResult函数，这个函数实现了对img变量的赋值
			}
		});
        
        textView = (TextView)this.findViewById(R.id.textView1);//显示状态
        
        buttonDetect = (Button)this.findViewById(R.id.button2);//人脸检测
        buttonDetect.setVisibility(View.INVISIBLE);
        buttonDetect.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				
				textView.setText("Waiting ...");
				
				FaceppDetect faceppDetect = new FaceppDetect();
				faceppDetect.setDetectCallback(new DetectCallback() {
					
					public void detectResult(JSONObject rst) {
						//Log.v(TAG, rst.toString());
						
						//use the red paint
						Paint paint = new Paint();
						paint.setColor(Color.RED);
						paint.setStrokeWidth(Math.max(img.getWidth(), img.getHeight()) / 100f);//设置线宽

						//create a new canvas
						Bitmap bitmap = Bitmap.createBitmap(img.getWidth(), img.getHeight(), img.getConfig());
						Canvas canvas = new Canvas(bitmap);
						canvas.drawBitmap(img, new Matrix(), null);
						
						
						try {
							//find out all faces
							final int count = rst.getJSONArray("face").length();//获取人脸数
							for (int i = 0; i < count; ++i) {
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
								
								//change percent value to the real size
								x = x / 100 * img.getWidth();
								w = w / 100 * img.getWidth() * 0.7f;//*0.7是为了画外包矩形
								y = y / 100 * img.getHeight();
								h = h / 100 * img.getHeight() * 0.7f;

								//draw the box to mark it out（画脸的外包矩形）
								canvas.drawLine(x - w, y - h, x - w, y + h, paint);//画左竖边
								canvas.drawLine(x - w, y - h, x + w, y - h, paint);
								canvas.drawLine(x + w, y + h, x - w, y + h, paint);
								canvas.drawLine(x + w, y + h, x + w, y - h, paint);
							}
							
							//save new image
							img = bitmap;

							MainActivity.this.runOnUiThread(new Runnable() {
								
								public void run() {
									//show the image
									imageView.setImageBitmap(img);
									textView.setText("Finished, "+ count + " faces.");
								}
							});
							
						} catch (JSONException e) {
							e.printStackTrace();
							MainActivity.this.runOnUiThread(new Runnable() {
								public void run() {
									textView.setText("Error.");
								}
							});
						}
						
					}
				});
				faceppDetect.detect(img);
			}
		});
        
        imageView = (ImageView)this.findViewById(R.id.imageView1);
        imageView.setImageBitmap(img);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {//startActivityForResult()函数触发，主要功能为img变量赋值
    	super.onActivityResult(requestCode, resultCode, intent);
    	
    	//the image picker callback
    	if (requestCode == PICTURE_CHOOSE) {
    		if (intent != null) {
    			//The Android api ~~~ 
    			//Log.d(TAG, "idButSelPic Photopicker: " + intent.getDataString());
    			Cursor cursor = getContentResolver().query(intent.getData(), null, null, null, null);//数据库游标，指向intent中的内容
    			cursor.moveToFirst();
    			int idx = cursor.getColumnIndex(ImageColumns.DATA);//（根据列名字获得列索引）获取影像数据索引
    			String fileSrc = cursor.getString(idx); //根据列索引获取列的值（这儿是获取图片的路径）
    			//Log.d(TAG, "Picture:" + fileSrc);
    			
    			//just read size
    			Options options = new Options();
    			options.inJustDecodeBounds = true;//无解码方式（读原始数据）
    			img = BitmapFactory.decodeFile(fileSrc, options);//根据图片路径构造Bitmap类对象

    			//scale size to read
    			options.inSampleSize = Math.max(1, (int)Math.ceil(Math.max((double)options.outWidth / 1024f, (double)options.outHeight / 1024f)));//图像如果大于1024*1024则对图像进行降采样（向上取整）
    			options.inJustDecodeBounds = false;//按照重新编码的方式读取图像
    			img = BitmapFactory.decodeFile(fileSrc, options);
    			textView.setText("Clik Detect. ==>");
    			
    			
    			imageView.setImageBitmap(img);
    			buttonDetect.setVisibility(View.VISIBLE);
    		}
    		else {
    			Log.d(TAG, "idButSelPic Photopicker canceled");
    		}
    	}
    }

    public class FaceppDetect {//对获取的图片进行检测
    	DetectCallback callback = null;
    	
    	public void setDetectCallback(DetectCallback detectCallback) { 
    		callback = detectCallback;
    	}

    	public void detect(final Bitmap image) {
    		
    		new Thread(new Runnable() {
				
				public void run() {
					HttpRequests httpRequests = new HttpRequests("71933deeb624e333e476215396ff981c", "x0-sZ0eVnqWg2gU2rARhv8uYnbSfNx4E", true, false);
		    		//Log.v(TAG, "image size : " + img.getWidth() + " " + img.getHeight());
		    		
		    		ByteArrayOutputStream stream = new ByteArrayOutputStream();//可以捕获内存缓冲区的数据，转换成字节数组
		    		float scale = Math.min(1, Math.min(600f / img.getWidth(), 600f / img.getHeight()));//控制照片规模
		    		Matrix matrix = new Matrix();
		    		matrix.postScale(scale, scale);

		    		Bitmap imgSmall = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, false);
		    		//Log.v(TAG, "imgSmall size : " + imgSmall.getWidth() + " " + imgSmall.getHeight());
		    		
		    		imgSmall.compress(Bitmap.CompressFormat.JPEG, 100, stream);//把bitmap写入stream流中
		    		byte[] array = stream.toByteArray();//把stream转化成byte数组
		    		
		    		try {
		    			//detect
						JSONObject result = httpRequests.detectionDetect(new PostParameters().setImg(array));//开始识别
						//finished , then call the callback function
						if (callback != null) {
							callback.detectResult(result);//把执行结果传入
						}
					} catch (FaceppParseException e) {
						e.printStackTrace();
						MainActivity.this.runOnUiThread(new Runnable() {
							public void run() {
								textView.setText("Network error.");
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


