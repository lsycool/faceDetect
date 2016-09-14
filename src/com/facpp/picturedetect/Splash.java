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
		// 窗口设置为全屏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// 设置窗口为半透明
		getWindow().setFormat(PixelFormat.TRANSLUCENT);
	    setContentView(R.layout.welcome);
//	    ImageView splash=(ImageView) findViewById(R.id.imageView_splash);
//	    Animation animation =AnimationUtils.loadAnimation(this, R.layout.splash);
//	    animation.setFillAfter(true);  
//	    splash.setAnimation(animation);
	    new Handler().postDelayed(new Runnable(){   
	    	// 为了减少代码使用匿名Handler创建一个延时的调用
	    	            public void run() {   
	    	                Intent i = new Intent(Splash.this, HelloApp.class);    
	    	                //通过Intent打开最终真正的主界面Main这个Activity
	    	                Splash.this.startActivity(i);    //启动Main界面
	    	                Splash.this.finish();    //关闭自己这个开场屏
	    	            }   
	    	        }, 5000); 
	}
}
