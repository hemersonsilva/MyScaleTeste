package com.android.myscale;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

public class MyScaleActivity extends Activity {

	private EditText txtValue;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		final RulerView rulerViewMm = (RulerView) findViewById(R.id.rulerTeste);
		txtValue = (EditText) findViewById(R.id.edittext);
		rulerViewMm.setStartingPoint((float) 0);
		rulerViewMm.setUpdateListener(new onViewUpdateListener() {

			@Override
			public void onViewUpdate(float result) {
				float value = (float) Math.round(result);
				txtValue.setText(value + "");
			}
		});
	}
}
