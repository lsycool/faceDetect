package com.facpp.picturedetect;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class HelloApp extends Activity {
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hello_app);
        Button button = (Button)this.findViewById(R.id.button1);//����ѵ��
        button.setOnClickListener(new OnClickListener() {
			
 			public void onClick(View arg0) {
 				//������ѵ������
 				Intent HelloIntent = new Intent(HelloApp.this,FaceTrain.class);
 				startActivity(HelloIntent);
 			}
 		});
        
        Button button1 = (Button)this.findViewById(R.id.button2);//����ʶ����֤
        button1.setOnClickListener(new OnClickListener() {
			
 			public void onClick(View arg0) {
 				//������ѵ������
 				Intent HelloIntent = new Intent(HelloApp.this,FaceDetec.class);
 				startActivity(HelloIntent);
 			}
 		});
        
        Button button2 = (Button)this.findViewById(R.id.button3);//�������
        button2.setOnClickListener(new OnClickListener() {
			
 			public void onClick(View arg0) {
 				//������ѵ������
 				Intent HelloIntent = new Intent(HelloApp.this,FaceCount.class);
 				startActivity(HelloIntent);
 			}
 		});
        
	}
}
