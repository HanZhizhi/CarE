package com.space.care.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.FrameLayout;

import com.space.care.R;
import com.space.care.mainFrag.OrderFrag;
import com.space.care.mainFrag.ServeFrag;
import com.space.care.mainFrag.UserFrag;

public class MainActivity extends AppCompatActivity implements Toolbar.OnMenuItemClickListener{
    private FrameLayout mainFrame;
    private ServeFrag fragServe;
    private OrderFrag fragOrder;
    private UserFrag fragUser;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    Log.i("clicked","serve");
                    showServices();
                    return true;
                case R.id.navigation_order:
                    Log.i("clicked","order");
                    showOrders();
                    return true;
                case R.id.navigation_user:
                    Log.i("clicked","user");
                    showUser();
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        initViews();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.CALL_PHONE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.READ_PHONE_STATE},
                    9);//自定义的code
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 9: {
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        //System.out.println("Permissions --> " + "Permission Granted: " + permissions[i]);
                    } else if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        //System.out.println("Permissions --> " + "Permission Denied: " + permissions[i]);
                    }
                }
            }
            break;
            default: {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }

    private void initViews()
    {
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        mainFrame= (FrameLayout) findViewById(R.id.main_content_frame);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        navigation.setSelectedItemId(0);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        toolbar.setTitle("哈哈哈");

        setSupportActionBar(toolbar);

        toolbar.setOnMenuItemClickListener(this);

        showServices();
    }

    private void hideAllFrag(FragmentTransaction ftransaction)
    {
        if (fragServe!=null)
        {
            ftransaction.hide(fragServe);
        }
        if (fragOrder!=null)
        {
            ftransaction.hide(fragOrder);
        }
        if (fragUser!=null)
        {
            ftransaction.hide(fragUser);
        }
    }

    private void showServices()
    {
        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
        if (fragServe==null)
        {
            fragServe=new ServeFrag();
            transaction.add(R.id.main_content_frame,fragServe);
        }
        hideAllFrag(transaction);
        transaction.show(fragServe);
        transaction.commit();
    }

    private void showOrders()
    {
        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
        if (fragOrder==null)
        {
            fragOrder=new OrderFrag();
            transaction.add(R.id.main_content_frame,fragOrder);
        }
        hideAllFrag(transaction);
        transaction.show(fragOrder);
        transaction.commit();
    }

    private void showUser()
    {
        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
        if (fragUser==null)
        {
            fragUser=new UserFrag();
            transaction.add(R.id.main_content_frame,fragUser);
        }
        hideAllFrag(transaction);
        transaction.show(fragUser);
        transaction.commit();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.action_lbs:
                Log.i("clickedlbs","lsb");
                Intent lbsIntent=new Intent(MainActivity.this,LbsActivity.class);
                startActivity(lbsIntent);
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_toolbar, menu);
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
