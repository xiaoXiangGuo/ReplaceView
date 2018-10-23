package org.zcx.replaceview;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.zcx.replaceviewlibrary.ReplaceView;

public class MainActivity extends AppCompatActivity {

    private ReplaceView replaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView textView = new TextView(this);
        textView.setText("我是谁，我在哪");
        textView.setTextColor(0xf00);
        textView.setTextSize(100);
        setContentView(R.layout.activity_main);
        replaceView = new ReplaceView.Build(this)
                .addReplaceView(textView, 1)
                .create();
    }

    @Override
    protected void onResume() {
        super.onResume();
        replaceView.showReplaceView(1);
    }
}
