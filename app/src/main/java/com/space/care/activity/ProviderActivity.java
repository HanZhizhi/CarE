package com.space.care.activity;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.ToolbarWidgetWrapper;
import android.util.Log;
import android.view.View;
import android.view.Window;

import com.space.care.R;

public class ProviderActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private int provider_id=-1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_provider);

        Intent intent=getIntent();
        if(intent!=null)
        {
            Bundle bundle=intent.getExtras();
            provider_id=bundle.getInt("provider_id");
        }

        Log.i("provider_id","**"+provider_id);

        initViews();
    }

    private void initViews()
    {
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("服务提供商");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
