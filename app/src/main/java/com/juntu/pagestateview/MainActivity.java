package com.juntu.pagestateview;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.juntu.pagestate.PageStateView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private PageStateView pageState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView customText = new TextView(this);
        customText.setText("自定义布局");


        pageState = new PageStateView.Builder(this)
                //这个地方传的就是你要换掉的控件   fragment/activity 直接传this
                .init(findViewById(R.id.content))
                .setCustomView(customText)
//                .setErrorView(View.inflate(this,R.layout.layout_error,null))
                .setRetryListener(new PageStateView.Builder.OnRetryClickListener() {
                    @Override
                    public void onRetry() {
                        Toast.makeText(MainActivity.this, "请重试！！", Toast.LENGTH_SHORT).show();
                    }
                })
                .build();
    }

    public void loading(View view) {
        pageState.loading();
    }

    public void noNet(View view) {
        pageState.noNet();
    }

    public void onError(View view) {
        pageState.error();
    }

    public void complete(View view) {
        pageState.content();
    }

    public void noData(View view) {
        pageState.noData();
    }

    public void custom(View view) {
        pageState.custom();
    }
}
