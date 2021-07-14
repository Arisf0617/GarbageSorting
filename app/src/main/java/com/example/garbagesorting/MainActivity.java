package com.example.garbagesorting;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.viewpager.widget.ViewPager;

import com.example.garbagesorting.adapter.ViewPagerAdapter;
import com.example.garbagesorting.adapter.findAdapter;
import com.example.garbagesorting.dao.FindDao;
import com.example.garbagesorting.fragment.FindFragment;
import com.example.garbagesorting.fragment.HomeFragment;
import com.example.garbagesorting.fragment.PersonFragment;
import com.example.garbagesorting.fragment.RootFragment;
import com.example.garbagesorting.model.find;
import com.example.garbagesorting.view.BottomNavigationViewHelper;
import com.example.garbagesorting.view.CustomViewPager;
import com.example.garbagesorting.view.SplashView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by bruce on 2016/11/1.
 * HomeActivity 主界面
 */

public class MainActivity extends MyBaseFragmentActivity{
    private CustomViewPager viewPager;
    private MenuItem menuItem;
    private BottomNavigationView bottomNavigationView;
    private final int LOGIN=4;
    SharedPreferences sp=null;
    private List<find> list=new ArrayList<>();
    private findAdapter adapter;
    private FindDao findDao=new FindDao();
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.light_blue_theme);
        setContentView(R.layout.activity_main);
        SplashView.simpleShowSplashView(this);
        SplashView.showSplashView(this, 6, R.drawable.shouye, new SplashView.OnSplashViewActionListener() {
            @Override
            public void onSplashImageClick(String actionUrl) {
                Log.d("SplashView", "img clicked. actionUrl: " + actionUrl);
                Toast.makeText(MainActivity.this, "img clicked.", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onSplashViewDismiss(boolean initiativeDismiss) {
                Log.d("SplashView", "dismissed, initiativeDismiss: " + initiativeDismiss);
            }
        });


        //initUI();
        //initData();
        sp=getSharedPreferences("User",MODE_PRIVATE);
        findViewById(R.id.popup_menu_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu=new PopupMenu(MainActivity.this,view);//1.实例化PopupMenu
                getMenuInflater().inflate(R.menu.menu,popupMenu.getMenu());//2.加载Menu资源

                //3.为弹出菜单设置点击监听
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Intent intent=new Intent();
                        switch (item.getItemId()){
                            case R.id.popup_help:
                                //Toast.makeText(MainActivity.this,"添加",Toast.LENGTH_SHORT).show();
                                return true;
                            case R.id.popup_swift:
                                // Toast.makeText(MainActivity.this,"删除",Toast.LENGTH_SHORT).show();
                                return true;
                            case R.id.popup_exit:
                                SharedPreferences.Editor editor=sp.edit();
                                editor.clear();
                                editor.commit();
                                //sp=null;
                                //Toast.makeText(MainActivity.this,"更多",Toast.LENGTH_SHORT).show();
                                intent.setClass(MainActivity.this,LoginActivity.class);
                                intent.putExtra("null","");
                                startActivityForResult(intent,LOGIN);
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popupMenu.show();//4.显示弹出菜单
            }
        });
        viewPager =  findViewById(R.id.viewpager);
        viewPager.setPagingEnabled(false);
        viewPager.setOffscreenPageLimit(4);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        //默认 >3 的选中效果会影响ViewPager的滑动切换时的效果，故利用反射去掉
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.item_home:
                                viewPager.setCurrentItem(0);
                                break;
                            case R.id.item_lib:
                                viewPager.setCurrentItem(1);
                                break;
                            case R.id.item_find:
                                viewPager.setCurrentItem(2);
                                break;
                            case R.id.item_person:
                                viewPager.setCurrentItem(3);
                                break;
                        }
                        return false;
                    }
                });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (menuItem != null) {
                    menuItem.setChecked(false);
                } else {
                    bottomNavigationView.getMenu().getItem(0).setChecked(false);
                }
                menuItem = bottomNavigationView.getMenu().getItem(position);
                menuItem.setChecked(true);
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        setupViewPager(viewPager);

    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new HomeFragment());
        adapter.addFragment(new FindFragment());
        adapter.addFragment(new RootFragment());
        adapter.addFragment(new PersonFragment());
        viewPager.setAdapter(adapter);
    }
}