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
	// �洢��Ƭ����
	private byte[] array = null;

	public static HttpRequests httpRequests = new HttpRequests("71933deeb624e333e476215396ff981c", "x0-sZ0eVnqWg2gU2rARhv8uYnbSfNx4E", true, false);

	private TextView textView;
	private EditText editView;

	private boolean flag = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// ����ȥ������
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// ��������Ϊȫ��
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// ���ô���Ϊ��͸��
		getWindow().setFormat(PixelFormat.TRANSLUCENT);
		setContentView(R.layout.face_train);	

		textView = (TextView)this.findViewById(R.id.textView_trainlog);
		editView = (EditText)this.findViewById(R.id.editText1);
		sufaceView = (ImageView) this.findViewById(R.id.imageView_faceshow);

		Button button = (Button) this.findViewById(R.id.button_cameraopen);// ��ȡӰ��
		button.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				// ����ϵͳ����APP
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(intent, 1);
				flag = true;
			}
		});

		Button button1 = (Button) this.findViewById(R.id.button_trainface);// ����ѵ��
		button1.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {

				if(flag!= true && array == null){
					FaceTrain.this.runOnUiThread(new Runnable() {

						public void run() {
							textView.setText("���Ȼ�ȡӰ�����ݡ�");
						}
					});
					return;
				}

				//				final ArrayList<String> personinfoList = new ArrayList<String>();
				FaceTrain.this.runOnUiThread(new Runnable() {

					public void run() {
						textView.setText("����ѵ������");
					}
				});
				
				new Thread(new Runnable() {
					public void run() {
						try {
							httpRequests.trainSearch(new PostParameters().setFacesetName("faceset_lsy0"));
							FaceTrain.this.runOnUiThread(new Runnable() {

								public void run() {
									textView.setText("ѵ���ɹ�����");
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
					textView.setText("���ڼ�⡣��");
				}
			});

			Bundle bundle = data.getExtras();  
			Bitmap bitmap = (Bitmap) bundle.get("data");// ��ȡ������ص����ݣ���ת��ΪBitmapͼƬ��ʽ  	

			ByteArrayOutputStream stream = new ByteArrayOutputStream();//���Բ����ڴ滺���������ݣ�ת�����ֽ�����
			float scale = Math.min(1, Math.min(600f / bitmap.getWidth(), 600f / bitmap.getHeight()));//������Ƭ��ģ
			Matrix matrix = new Matrix();
			matrix.postScale(scale, scale);
			final Bitmap imgSmall = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
			imgSmall.compress(Bitmap.CompressFormat.JPEG, 100, stream);//��bitmapд��stream����
			
//			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);//��bitmapд��stream����
			array = stream.toByteArray();//��streamת����byte����
			sufaceView.setImageBitmap(bitmap);// ��ͼƬ��ʾ��ImageView��  

			FaceppDetect faceppDetect = new FaceppDetect();
			faceppDetect.setDetectCallback(new DetectCallback() {

				public void detectResult(JSONObject rst){

					try{
						int count = rst.getJSONArray("face").length();
						if(count == 0){								
							throw new FaceppParseException("û�м�⵽����.");
						}
						//find out all faces
						httpRequests.facesetAddFace(new PostParameters().setFacesetName("faceset_lsy0").setFaceId(rst.getJSONArray("face").getJSONObject(0).getString("face_id")));
						final ArrayList<String> personinfoList = new ArrayList<String>();		
						String temString = editView.getText().toString().trim();;
						personinfoList.add(temString);
						temString = rst.getJSONArray("face").getJSONObject(0).getJSONObject("attribute").getJSONObject("gender").getString("value")+"";
						personinfoList.add(temString);
						temString = rst.getJSONArray("face").getJSONObject(0).getJSONObject("attribute").getJSONObject("age").getInt("value")+"";
						temString = temString +"��"+ rst.getJSONArray("face").getJSONObject(0).getJSONObject("attribute").getJSONObject("age").getInt("range")+"";
						personinfoList.add(temString);
						temString = rst.getJSONArray("face").getJSONObject(0).getJSONObject("attribute").getJSONObject("race").getString("value");
						personinfoList.add(temString);
						double smile = rst.getJSONArray("face").getJSONObject(0).getJSONObject("attribute").getJSONObject("smiling").getDouble("value");
						if (smile > 9.3) {
							temString = "ɵЦ";
						}else if(smile > 6){
							temString = "΢Ц";
						}else if (smile >3) {
							temString = "ƽ��";
						}else {
							temString = "����";
						}
						personinfoList.add(temString);
						temString = rst.getJSONArray("face").getJSONObject(0).getJSONObject("position").getJSONObject("center").getDouble("x")+"";
						personinfoList.add(temString);
						temString = rst.getJSONArray("face").getJSONObject(0).getJSONObject("position").getJSONObject("center").getDouble("y")+"";
						personinfoList.add(temString);
						//						
						FaceTrain.this.runOnUiThread(new Runnable() {

							public void run() {
								textView.setText("���ɹ� .");
							}
						});

						Intent HelloIntent = new Intent(FaceTrain.this,Facetrain_result.class);
						Bundle mBundle=new Bundle();
						mBundle.putString("name",personinfoList.get(0));//ѹ������
						mBundle.putString("gender",personinfoList.get(1));//ѹ������
						mBundle.putString("age",personinfoList.get(2));//ѹ������
						mBundle.putString("race",personinfoList.get(3));//ѹ������
						mBundle.putString("smile",personinfoList.get(4));//ѹ������
						mBundle.putString("x",personinfoList.get(5));//ѹ������
						mBundle.putString("y",personinfoList.get(6));//ѹ������						
						
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
	   
	public class FaceppDetect {//�Ի�ȡ��ͼƬ���м��
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
						JSONObject result = httpRequests.detectionDetect(new PostParameters().setImg(data).setMode("oneface").setTag(personTag));//��ʼʶ��
						//finished , then call the callback function
						if (callback != null) {
							callback.detectResult(result);//��ִ�н������
						}
					} catch (FaceppParseException e) {
						e.printStackTrace();
						FaceTrain.this.runOnUiThread(new Runnable() {
							public void run() {
								//								Toast.makeText(getApplicationContext(), "�޷���������", Toast.LENGTH_SHORT).show();
								textView.setText("�޷������Ʒ�����,�����������á�");
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
