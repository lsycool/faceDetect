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
        String data=bundle.getString("name");//��������
        TextView tm = (TextView)this.findViewById(R.id.textView8);
        tm.setText(data);
        data=bundle.getString("gender");//��������
        if (data.equals("Male")) {
        	data = "��";
		}
        else {
			data = "Ů";
		}
        tm = (TextView)this.findViewById(R.id.textView9);
        tm.setText(data);
        data=bundle.getString("age");//��������
        tm = (TextView)this.findViewById(R.id.textView10);
        tm.setText(data);
        data=bundle.getString("race");//��������
        if (data.equals("Asian")) {
			data = "������";
		}else if(data.equals("Black")){
			data = "��������";
		}else if (data.equals("White")) {
			data = "������";
		}
        tm = (TextView)this.findViewById(R.id.textView11);
        tm.setText(data);
        data=bundle.getString("smile");//��������
        tm = (TextView)this.findViewById(R.id.textView12);
        tm.setText(data);
        data=bundle.getString("x");//��������
        tm = (TextView)this.findViewById(R.id.textView13);
        tm.setText(data);
        data=bundle.getString("y");//��������
        tm = (TextView)this.findViewById(R.id.textView14);
        tm.setText(data);
	}
}
