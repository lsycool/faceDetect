package com.facpp.picturedetect;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;


public class Splash extends Activity {
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// ��������Ϊȫ��
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// ���ô���Ϊ��͸��
		getWindow().setFormat(PixelFormat.TRANSLUCENT);
	    setContentView(R.layout.welcome);
//	    ImageView splash=(ImageView) findViewById(R.id.imageView_splash);
//	    Animation animation =AnimationUtils.loadAnimation(this, R.layout.splash);
//	    animation.setFillAfter(true);  
//	    splash.setAnimation(animation);
	    new Handler().postDelayed(new Runnable(){   
	    	// Ϊ�˼��ٴ���ʹ������Handler����һ����ʱ�ĵ���
	    	            public void run() {   
	    	                Intent i = new Intent(Splash.this, HelloApp.class);    
	    	                //ͨ��Intent������������������Main���Activity
	    	                Splash.this.startActivity(i);    //����Main����
	    	                Splash.this.finish();    //�ر��Լ����������
	    	            }   
	    	        }, 5000); 
	}
}
