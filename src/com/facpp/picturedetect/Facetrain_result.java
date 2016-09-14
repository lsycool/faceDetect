package com.facpp.picturedetect;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class Facetrain_result extends Activity {
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.facetrain_result);
        
        Bundle bundle=getIntent().getExtras();
        String data=bundle.getString("name");//读出数据
        TextView tm = (TextView)this.findViewById(R.id.textView8);
        tm.setText(data);
        data=bundle.getString("gender");//读出数据
        if (data.equals("Male")) {
        	data = "男";
		}
        else {
			data = "女";
		}
        tm = (TextView)this.findViewById(R.id.textView9);
        tm.setText(data);
        data=bundle.getString("age");//读出数据
        tm = (TextView)this.findViewById(R.id.textView10);
        tm.setText(data);
        data=bundle.getString("race");//读出数据
        if (data.equals("Asian")) {
			data = "黄种人";
		}else if(data.equals("Black")){
			data = "非洲人种";
		}else if (data.equals("White")) {
			data = "白种人";
		}
        tm = (TextView)this.findViewById(R.id.textView11);
        tm.setText(data);
        data=bundle.getString("smile");//读出数据
        tm = (TextView)this.findViewById(R.id.textView12);
        tm.setText(data);
        data=bundle.getString("x");//读出数据
        tm = (TextView)this.findViewById(R.id.textView13);
        tm.setText(data);
        data=bundle.getString("y");//读出数据
        tm = (TextView)this.findViewById(R.id.textView14);
        tm.setText(data);
	}
}
