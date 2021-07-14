package com.example.garbagesorting.fragment;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.bikenavi.BikeNavigateHelper;
import com.baidu.mapapi.bikenavi.adapter.IBEngineInitListener;
import com.baidu.mapapi.bikenavi.adapter.IBRoutePlanListener;
import com.baidu.mapapi.bikenavi.model.BikeRoutePlanError;
import com.baidu.mapapi.bikenavi.params.BikeNaviLaunchParam;
import com.baidu.mapapi.bikenavi.params.BikeRouteNodeInfo;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.baidu.mapapi.walknavi.WalkNavigateHelper;
import com.baidu.mapapi.walknavi.adapter.IWEngineInitListener;
import com.baidu.mapapi.walknavi.adapter.IWRoutePlanListener;
import com.baidu.mapapi.walknavi.model.WalkRoutePlanError;
import com.baidu.mapapi.walknavi.params.WalkNaviLaunchParam;
import com.baidu.mapapi.walknavi.params.WalkRouteNodeInfo;
import com.example.garbagesorting.BaiduMap.BNaviGuideActivity;
import com.example.garbagesorting.BaiduMap.PoiItemAdapter;
import com.example.garbagesorting.BaiduMap.WNaviGuideActivity;
import com.example.garbagesorting.R;
import com.example.garbagesorting.utils.HideKeyboardUtils;
import com.example.garbagesorting.utils.ToastUtil;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 地图展示 + 定位 + poi地点检索 by——haoyu 2021/5/7 17：00 add
 * 有bug，poi地点不能点击（未解决）(已解决 2021/5/7 20：49)
 * 接下来做导航（暂定）
 * 完成了步行导航和骑行导航 2021/5/14 22：00
 *
 * */
public class RootFragment extends Fragment
        implements SensorEventListener,
        OnGetPoiSearchResultListener, OnGetSuggestionResultListener,
        BaiduMap.OnMapClickListener, BaiduMap.OnMarkerClickListener{

    private static final String TAG = RootFragment.class.getSimpleName();
    public MapView mMapView = null;
    private BaiduMap mBaiduMap = null;
    private LocationClient mLocationClient;
    private boolean isFirstLoc = true;
    private Double lastX = 0.0;
    private float mCurrentDirection = 0;
    private double mCurrentLat = 0.0;
    private double mCurrentLon = 0.0;
    private MyLocationData myLocationData;
    private float mCurrentAccracy;
    private SensorManager mSensorManager;

    // 地图View Poi实例
    private EditText mEditTextCity = null;
    private EditText mEditTextPoi = null;
    private PoiSearch mPoiSearch = null;
    private SuggestionSearch mSuggestionSearch = null;
    private RecyclerView mRecyclerView = null;
    private TextView mPoiTitle = null;
    private TextView mPoiAddress = null;
    private LinearLayout mLayoutDetailInfo = null;
    private PoiItemAdapter mPoiItemAdaper = null;
    private BitmapDescriptor mBitmapDescWaterDrop =
            BitmapDescriptorFactory.fromResource(R.drawable.water_drop);
    private Button mBtnSearch = null;
    private HashMap<Marker, PoiInfo> mMarkerPoiInfo = new HashMap<>();
    private Marker mPreSelectMarker = null;
    private MyTextWatcher mMyTextWatcher = new MyTextWatcher();

    // 分页
    private int mLoadIndex = 0;

    //导航相关
//    private Button bikeBtn = null;
//    private Button walkBtn = null;

    private ImageView bikeIV = null;
    private ImageView walkIV = null;

    /*导航起终点Marker，可拖动改变起终点的坐标*/
    private Marker mStartMarker;
    private Marker mEndMarker;

    private LatLng startPt;
    private LatLng endPt;

    double latitude;//纬度信息
    double longitude;//经度信息

    private BikeNaviLaunchParam bikeParam;
    private WalkNaviLaunchParam walkParam;

    private BitmapDescriptor bdStart = BitmapDescriptorFactory
            .fromResource(R.drawable.icon_start);
    private BitmapDescriptor bdEnd = BitmapDescriptorFactory
            .fromResource(R.drawable.icon_end);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //显示布局
        View root=inflater.inflate(R.layout.fragment_root, container, false);
        mMapView = root.findViewById(R.id.mapview);
        mBaiduMap = mMapView.getMap();
        mEditTextCity = root.findViewById(R.id.city);
        mEditTextPoi = root.findViewById(R.id.poi);
        mBtnSearch = root.findViewById(R.id.btn_search);
        mRecyclerView = root.findViewById(R.id.poiList);
        mLayoutDetailInfo = root.findViewById(R.id.poiInfo);

//        bikeBtn = root.findViewById(R.id.btn_bikenavi);
//        walkBtn = root.findViewById(R.id.btn_walknavi_normal);

        bikeIV = root.findViewById(R.id.iv_bikenavi);
        walkIV = root.findViewById(R.id.iv_walknavi_normal);

        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(this);

        mSuggestionSearch = SuggestionSearch.newInstance();
        mSuggestionSearch.setOnGetSuggestionResultListener(this);

        mBaiduMap.setOnMapClickListener(this);
        mBaiduMap.setOnMarkerClickListener(this);

        checkVersion();
        initView();
        startLocation();

        /*骑行导航入口*/
        bikeIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bikeParam == null){
                    ToastUtil.showMsg(getActivity(),"请先通过检索功能设置目的地!");
                }else{
                    startBikeNavi();
                    //System.out.println("骑行导航");
                    ToastUtil.showMsg(getActivity(),"即将开启骑行导航...");
                }
            }
        });

        /*普通步行导航入口*/
        walkIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(walkParam == null){
                    ToastUtil.showMsg(getActivity(),"请先通过检索功能设置目的地!");
                }else{
                    walkParam.extraNaviMode(0);
                    startWalkNavi();
                    ToastUtil.showMsg(getActivity(),"即将开启步行导航...");
                }
            }
        });

        return root;
    }

    /**
     * 初始化View
     */
    private void initView() {
        //mMapView = findViewById(R.id.mapview);
        //mBaiduMap = mMapView.getMap();
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        MyLocationConfiguration myLocationConfiguration =
                new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL, true, null);
        // 设置定位图层配置信息
        mBaiduMap.setMyLocationConfiguration(myLocationConfiguration);
        // 获取传感器管理服务
        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        // 为系统的方向传感器注册监听器
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_UI);
        initPoiView();
    }

    /**
     * 初始化Poi兴趣点
     */
    private void initPoiView(){

        if (null == mEditTextCity || null == mEditTextPoi || null == mBtnSearch) {
            return;
        }

        mEditTextPoi.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                return false;
            }
        });

        mEditTextPoi.addTextChangedListener(mMyTextWatcher);

        mBtnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchPoiInCity();
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        if (null == mRecyclerView) {
            return;
        }

        mRecyclerView.setLayoutManager(linearLayoutManager);
        mPoiItemAdaper = new PoiItemAdapter();
        mPoiItemAdaper.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SuggestionResult.SuggestionInfo suggestInfo =
                        mPoiItemAdaper.getItemSuggestInfo(position);
                locateSuggestPoi(suggestInfo);

                setPoiTextWithLocateSuggestInfo(suggestInfo);
            }
        });

        mRecyclerView.setAdapter(mPoiItemAdaper);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                HideKeyboardUtils.hideKeyBoard(getActivity());
            }
        });

        if (null == mLayoutDetailInfo) {
            return;
        }

        mPoiTitle = mLayoutDetailInfo.findViewById(R.id.poiTitle);
        mPoiAddress = mLayoutDetailInfo.findViewById(R.id.poiAddress);

    }

    private void searchPoiInCity() {
        String cityStr = mEditTextCity.getText().toString();
        // 获取检索关键字
        String keyWordStr = mEditTextPoi.getText().toString();
        if (TextUtils.isEmpty(cityStr) || TextUtils.isEmpty(keyWordStr)) {
            return;
        }

        if (View.VISIBLE == mRecyclerView.getVisibility()) {
            mRecyclerView.setVisibility(View.INVISIBLE);
        }

        mPoiSearch.searchInCity((new PoiCitySearchOption())
                .city(cityStr)
                .keyword(keyWordStr)
                .pageNum(mLoadIndex) // 分页编号
                .cityLimit(true)
                .scope(1));
    }

    @Override
    public void onGetPoiResult(PoiResult poiResult) {
        if (poiResult == null || poiResult.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
            mLoadIndex = 0;
            Toast.makeText(getActivity(), "未找到结果", Toast.LENGTH_LONG).show();
            return;
        }

        List<PoiInfo> poiInfos = poiResult.getAllPoi();
        if (null == poiInfos) {
            return;
        }

        mRecyclerView.setVisibility(View.GONE);

        setPoiResult(poiInfos);
    }

    /**
     * @param poiDetailResult
     * @deprecated
     */
    @Override
    public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {
    }


    @Override
    public void onGetPoiDetailResult(PoiDetailSearchResult poiDetailSearchResult) {

    }

    @Override
    public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

    }

    @Override
    public void onGetSuggestionResult(SuggestionResult suggestionResult) {
        if (suggestionResult == null
                || suggestionResult.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
            mLoadIndex = 0;
            Toast.makeText(getActivity(), "未找到结果", Toast.LENGTH_LONG).show();
            return;
        }

        List<SuggestionResult.SuggestionInfo> suggesInfos = suggestionResult.getAllSuggestions();
        if (null == suggesInfos) {
            return;
        }

        // 隐藏之前的
        hideInfoLayout();

        mRecyclerView.setVisibility(View.VISIBLE);

        if (null == mPoiItemAdaper) {
            mPoiItemAdaper = new PoiItemAdapter(suggesInfos);
        } else {
            mPoiItemAdaper.updateData(suggesInfos);
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        HideKeyboardUtils.hideKeyBoard(getActivity());
    }

    @Override
    public void onMapPoiClick(MapPoi mapPoi) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (null == marker || null == mMarkerPoiInfo || mMarkerPoiInfo.size() <= 0) {
            return false;
        }

        Iterator itr = mMarkerPoiInfo.entrySet().iterator();
        Marker tmpMarker;
        PoiInfo poiInfo = null;
        Map.Entry<Marker, PoiInfo> markerPoiInfoEntry;
        while (itr.hasNext()) {
            markerPoiInfoEntry = (Map.Entry<Marker, PoiInfo>) itr.next();
            tmpMarker = markerPoiInfoEntry.getKey();
            if (null == tmpMarker) {
                continue;
            }

            if (tmpMarker.getId() == marker.getId()) {
                poiInfo = markerPoiInfoEntry.getValue();
                break;
            }
        }

        if (null == poiInfo) {
            return false;
        }
        InfoWindow infoWindow = getPoiInfoWindow(poiInfo);

        mBaiduMap.showInfoWindow(infoWindow);


        //System.out.println("123456789"+poiInfo.getLocation());//获取poi点经纬度
        endPt = poiInfo.getLocation();

        /*构造导航起终点参数对象*/
        BikeRouteNodeInfo bikeStartNode = new BikeRouteNodeInfo();
        bikeStartNode.setLocation(startPt);
        BikeRouteNodeInfo bikeEndNode = new BikeRouteNodeInfo();
        bikeEndNode.setLocation(endPt);
        bikeParam = new BikeNaviLaunchParam().startNodeInfo(bikeStartNode).endNodeInfo(bikeEndNode);

        WalkRouteNodeInfo walkStartNode = new WalkRouteNodeInfo();
        walkStartNode.setLocation(startPt);
        WalkRouteNodeInfo walkEndNode = new WalkRouteNodeInfo();
        walkEndNode.setLocation(endPt);
        walkParam = new WalkNaviLaunchParam().startNodeInfo(walkStartNode).endNodeInfo(walkEndNode);

        /* 初始化起终点Marker */
        //initOverlay();

        showPoiInfoLayout(poiInfo);

        if (null != mPreSelectMarker) {
            mPreSelectMarker.setScale(1.0f);
        }

        marker.setScale(1.5f);
        mPreSelectMarker = marker;

        return true;
    }


    /**
     * 在地图上定位poi
     *
     * @param suggestInfo
     */
    private void locateSuggestPoi(SuggestionResult.SuggestionInfo suggestInfo) {
        if (null == suggestInfo) {
            return;
        }

        if (null == mRecyclerView || null == mMapView) {
            return;
        }

        mRecyclerView.setVisibility(View.INVISIBLE);

        LatLng latLng = suggestInfo.getPt();

        // 将地图平移到 latLng 位置
        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLng(latLng);
        mBaiduMap.setMapStatus(mapStatusUpdate);

        // 隐藏输入法
        HideKeyboardUtils.hideKeyBoard(getActivity());

        // 清除之前的
        clearData();

        // 显示当前的
        if (showSuggestMarker(latLng) ) {
            showPoiInfoLayout(suggestInfo);
        } else {
            setPoiTextWithLocateSuggestInfo(suggestInfo);
            searchPoiInCity();
        }
    }


    private void setPoiResult(List<PoiInfo> poiInfos) {
        if (null == poiInfos || poiInfos.size() <= 0) {
            return;
        }

        clearData();

        // 将地图平移到 latLng 位置
        LatLng latLng = poiInfos.get(0).getLocation();
        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLng(latLng);
        mBaiduMap.setMapStatus(mapStatusUpdate);

        Iterator itr = poiInfos.iterator();
        List<LatLng> latLngs = new ArrayList<>();
        PoiInfo poiInfo = null;
        int i = 0;
        while (itr.hasNext()) {
            poiInfo = (PoiInfo) itr.next();
            if (null == poiInfo) {
                continue;
            }

            locatePoiInfo(poiInfo, i);
            latLngs.add(poiInfo.getLocation());
            if (0 == i) {
                showPoiInfoLayout(poiInfo);
            }

            i++;
        }

        setBounds(latLngs);
    }


    private void clearData() {
        mBaiduMap.clear();
        mMarkerPoiInfo.clear();
        mPreSelectMarker = null;
    }

    private void locatePoiInfo(PoiInfo poiInfo, int i) {
        if (null == poiInfo) {
            return;
        }

        // 隐藏输入法
        HideKeyboardUtils.hideKeyBoard(getActivity());

        // 显示当前的
        showPoiMarker(poiInfo, i);
    }


    private void showPoiMarker(PoiInfo poiInfo, int i) {
        if (null == poiInfo) {
            return;
        }

        MarkerOptions markerOptions = new MarkerOptions()
                .position(poiInfo.getLocation())
                .icon(mBitmapDescWaterDrop);

        // 第一个poi放大显示
        if (0 == i) {
            InfoWindow infoWindow = getPoiInfoWindow(poiInfo);
            markerOptions.scaleX(1.5f).scaleY(1.5f).infoWindow(infoWindow);
        }

        Marker marker = (Marker) mBaiduMap.addOverlay(markerOptions);
        if (null != marker) {
            mMarkerPoiInfo.put(marker, poiInfo);

            if (0 == i) {//若只有一个marker点则获取这个点
                mPreSelectMarker = marker;
            }
        }
    }

    private InfoWindow getPoiInfoWindow(PoiInfo poiInfo) {
        TextView textView = new TextView(getContext());
        textView.setText(poiInfo.getName());
        textView.setPadding(10, 5, 10, 5);
        textView.setBackground(this.getResources().getDrawable(R.drawable.bg_info));
        InfoWindow infoWindow = new InfoWindow(textView, poiInfo.getLocation(), -150);
        return infoWindow;
    }

    /**
     * 显示定位点
     *
     * @param latLng
     */
    private boolean showSuggestMarker(LatLng latLng) {
        if (null == latLng) {
            return false;
        }

        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .icon(mBitmapDescWaterDrop)
                .scaleX(1.5f)
                .scaleY(1.5f);
        mBaiduMap.addOverlay(markerOptions);

        return true;
    }

    /**
     * 显示底部suggestion详情
     *
     * @param suggestInfo
     */
    private void showPoiInfoLayout(SuggestionResult.SuggestionInfo suggestInfo) {

        if (null == mLayoutDetailInfo || null == suggestInfo) {
            return;
        }

        if (null == mPoiTitle) {
            return;
        }

        if (null == mPoiAddress) {
            return;
        }

        mLayoutDetailInfo.setVisibility(View.VISIBLE);

        mPoiTitle.setText(suggestInfo.getKey());

        String address = suggestInfo.getAddress();
        if (TextUtils.isEmpty(address)) {
            mPoiAddress.setVisibility(View.GONE);
        } else {
            mPoiAddress.setText(suggestInfo.getAddress());
            mPoiAddress.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 显示底部poi详情
     *
     * @param poiInfo
     */
    private void showPoiInfoLayout(PoiInfo poiInfo) {

        if (null == mLayoutDetailInfo || null == poiInfo) {
            return;
        }

        if (null == mPoiTitle) {
            return;
        }

        if (null == mPoiAddress) {
            return;
        }

        mLayoutDetailInfo.setVisibility(View.VISIBLE);

        mPoiTitle.setText(poiInfo.getName());

        String address = poiInfo.getAddress();
        if (TextUtils.isEmpty(address)) {
            mPoiAddress.setVisibility(View.GONE);
        } else {
            mPoiAddress.setText(poiInfo.getAddress());
            mPoiAddress.setVisibility(View.VISIBLE);
        }
    }



    /**
     * 隐藏详情
     */
    private void hideInfoLayout() {
        if (null == mLayoutDetailInfo) {
            return;
        }

        mLayoutDetailInfo.setVisibility(View.GONE);
    }

    /**
     * 最佳视野内显示所有点标记
     */
    private void setBounds(List<LatLng> latLngs) {
        if (null == latLngs || latLngs.size() <= 0) {
            return;
        }

        int horizontalPadding = 80;
        int verticalPaddingBottom = 400;

        // 构造地理范围对象
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        // 让该地理范围包含一组地理位置坐标
        builder.include(latLngs);

        // 设置显示在指定相对于MapView的padding中的地图地理范围
        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLngBounds(builder.build(),
                horizontalPadding,
                verticalPaddingBottom,
                horizontalPadding,
                verticalPaddingBottom);
        // 更新地图
        mBaiduMap.setMapStatus(mapStatusUpdate);
        // 设置地图上控件与地图边界的距离，包含比例尺、缩放控件、logo、指南针的位置
        mBaiduMap.setViewPadding(0,
                0,
                0,
                verticalPaddingBottom);
    }


    /**
     * 选中某条sug检索结果时，将mEditPoi的文字设置为该sug检索结果的key
     *
     * @param suggestInfo
     */
    private void setPoiTextWithLocateSuggestInfo(SuggestionResult.SuggestionInfo suggestInfo) {
        if (null == suggestInfo) {
            return;
        }

        mEditTextPoi.removeTextChangedListener(mMyTextWatcher); // 暂时移除调TextWatcher，防止触发sug检索
        mEditTextPoi.setText(suggestInfo.getKey());
        mEditTextPoi.setSelection(suggestInfo.getKey().length()); // 将光标移到末尾
        mEditTextPoi.addTextChangedListener(mMyTextWatcher);
    }


    /**
     * 启动定位
     */
    private void startLocation() {
        // 定位初始化
        mLocationClient = new LocationClient(getContext());
        mLocationClient.registerLocationListener(mListener);
        LocationClientOption locationClientOption = new LocationClientOption();
        // 可选，设置定位模式，默认高精度 LocationMode.Hight_Accuracy：高精度；
        locationClientOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        // 可选，设置返回经纬度坐标类型，默认GCJ02
        locationClientOption.setCoorType("bd09ll");
        // 如果设置为0，则代表单次定位，即仅定位一次，默认为0
        // 如果设置非0，需设置1000ms以上才有效
        locationClientOption.setScanSpan(1000);
        //可选，设置是否使用gps，默认false
        locationClientOption.setOpenGps(true);
        // 可选，是否需要地址信息，默认为不需要，即参数为false
        // 如果开发者需要获得当前点的地址信息，此处必须为true
        locationClientOption.setIsNeedAddress(true);
        // 可选，默认false，设置是否需要POI结果，可以在BDLocation
        locationClientOption.setIsNeedLocationPoiList(true);
        // 设置定位参数
        mLocationClient.setLocOption(locationClientOption);
        // 开启定位
        mLocationClient.start();
    }


    /**
     * 检查版本
     */
    @SuppressLint("CheckResult")
    private void checkVersion() {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            RxPermissions rxPermissions = new RxPermissions(getActivity());
            rxPermissions.request(Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .subscribe(granted -> {
                        if (granted) {//申请成功
                            //发起连续定位请求
                            startLocation();// 定位初始化
                        } else {//申请失败
                            //ToastUtil.showMsg(getActivity(),"权限未开启");
                            //Toast.makeText(MainActivity.this,"权限未开启",Toast.LENGTH_SHORT).show();
                        }
                    });
        }else {
            startLocation();// 定位初始化
        }
    }

    /**
     * 传感器方向信息回调
     */
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        double x = sensorEvent.values[SensorManager.DATA_X];
        if (Math.abs(x - lastX) > 1.0) {
            mCurrentDirection = (float) x;
            myLocationData = new MyLocationData.Builder()
                    .accuracy(mCurrentAccracy)
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(mCurrentDirection)
                    .latitude(mCurrentLat)
                    .longitude(mCurrentLon).build();
            mBaiduMap.setMyLocationData(myLocationData);
        }
        lastX = x;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onResume() {
        super.onResume();
        if (null != mMapView) {
            mMapView.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (null != mMapView) {
            mMapView.onPause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 取消注册传感器监听
        mSensorManager.unregisterListener(this);
        // 退出时销毁定位
        mLocationClient.stop();
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        // 在activity执行onDestroy时必须调用mMapView.onDestroy()
        if (null != mMapView) {
            mMapView.onDestroy();
        }
        if (mPoiSearch != null) {
            mPoiSearch.destroy();
        }
        if (null != mSuggestionSearch) {
            mSuggestionSearch.destroy();
        }
        if (null != mBitmapDescWaterDrop) {
            mBitmapDescWaterDrop.recycle();
        }


    }


    private BDAbstractLocationListener mListener = new BDAbstractLocationListener() {

        /**
         * 定位请求回调函数
         *
         * @param location 定位结果
         */
        @Override
        public void onReceiveLocation(BDLocation location) {
            if(mEditTextCity.getText().toString().isEmpty()){
                mEditTextCity.setText(location.getCity());
            }
            // MapView 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }
            mCurrentLat = location.getLatitude();
            mCurrentLon = location.getLongitude();
            mCurrentAccracy = location.getRadius();

            latitude = location.getLatitude();    //获取纬度信息
            longitude = location.getLongitude();    //获取经度信息
            System.out.println("纬度信息latitude"+latitude+"经度信息longitude"+longitude);

            if(startPt == null){
                startPt = new LatLng(latitude, longitude);
            }

            myLocationData = new MyLocationData.Builder()
                    .accuracy(mCurrentAccracy)// 设置定位数据的精度信息，单位：米
                    .direction(mCurrentDirection)// 此处设置开发者获取到的方向信息，顺时针0-360
                    .latitude(mCurrentLat)
                    .longitude(mCurrentLon)
                    .build();
            mBaiduMap.setMyLocationData(myLocationData);
            if (location.getLocType() == BDLocation.TypeGpsLocation
                    || location.getLocType() == BDLocation.TypeNetWorkLocation
                    || location.getLocType() == BDLocation.TypeOffLineLocation) {
                if (isFirstLoc) {
                    isFirstLoc = false;
                    LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
                    MapStatus.Builder builder = new MapStatus.Builder();
                    builder.target(ll).zoom(18.0f);
                    mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
                }
            }
        }
    };


    class MyTextWatcher implements TextWatcher {


        /**
         * @param s
         * @param start
         * @param count
         * @param after
         */
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        /**
         * @param s
         * @param start
         * @param before
         * @param count
         */
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.length() <= 0 && View.VISIBLE == mRecyclerView.getVisibility()) {
                mRecyclerView.setVisibility(View.INVISIBLE);
            }
        }

        /**
         * @param s
         */
        @Override
        public void afterTextChanged(Editable s) {
            // 获取检索城市
            String cityStr = mEditTextCity.getText().toString();
            // 获取检索关键字
            String keyWordStr = mEditTextPoi.getText().toString();
            if (TextUtils.isEmpty(cityStr) || TextUtils.isEmpty(keyWordStr)) {
                return;
            }

            if (View.VISIBLE == mRecyclerView.getVisibility()) {
                mRecyclerView.setVisibility(View.INVISIBLE);
            }

            mSuggestionSearch.requestSuggestion(new SuggestionSearchOption()
                    .city(cityStr)
                    .keyword(keyWordStr)
                    .citylimit(true));
        }
    }

    /**
     * 开始骑行导航
     */
    private void startBikeNavi() {
        Log.d(TAG, "startBikeNavi");
        try {
            BikeNavigateHelper.getInstance().initNaviEngine(getActivity(),new IBEngineInitListener() {
                @Override
                public void engineInitSuccess() {
                    Log.d(TAG, "BikeNavi engineInitSuccess");
                    routePlanWithBikeParam();
                }

                @Override
                public void engineInitFail() {
                    Log.d(TAG, "BikeNavi engineInitFail");
                    BikeNavigateHelper.getInstance().unInitNaviEngine();
                }
            });
        } catch (Exception e) {
            Log.d(TAG, "startBikeNavi Exception");
            e.printStackTrace();
        }
    }

    /**
     * 发起骑行导航算路
     */
    private void routePlanWithBikeParam() {
        BikeNavigateHelper.getInstance().routePlanWithRouteNode(bikeParam, new IBRoutePlanListener() {
            @Override
            public void onRoutePlanStart() {
                Log.d(TAG, "BikeNavi onRoutePlanStart");
            }

            @Override
            public void onRoutePlanSuccess() {
                Log.d(TAG, "BikeNavi onRoutePlanSuccess");
                Intent intent = new Intent();
                intent.setClass(getActivity(), BNaviGuideActivity.class);
                startActivity(intent);
            }

            @Override
            public void onRoutePlanFail(BikeRoutePlanError error) {
                Log.d(TAG, "BikeNavi onRoutePlanFail");
            }

        });
    }


        /**
         * 开始步行导航
         */
        private void startWalkNavi() {
            Log.d(TAG, "startWalkNavi");
            try {
                WalkNavigateHelper.getInstance().initNaviEngine(getActivity(), new IWEngineInitListener() {
                    @Override
                    public void engineInitSuccess() {
                        Log.d(TAG, "WalkNavi engineInitSuccess");
                        routePlanWithWalkParam();
                    }

                    @Override
                    public void engineInitFail() {
                        Log.d(TAG, "WalkNavi engineInitFail");
                        WalkNavigateHelper.getInstance().unInitNaviEngine();
                    }
                });
            } catch (Exception e) {
                Log.d(TAG, "startBikeNavi Exception");
                e.printStackTrace();
            }
        }


        /**
         * 发起步行导航算路
         */
        private void routePlanWithWalkParam() {
            WalkNavigateHelper.getInstance().routePlanWithRouteNode(walkParam, new IWRoutePlanListener() {
                @Override
                public void onRoutePlanStart() {
                    Log.d(TAG, "WalkNavi onRoutePlanStart");
                }

                @Override
                public void onRoutePlanSuccess() {

                    Log.d(TAG, "onRoutePlanSuccess");

                    Intent intent = new Intent();
                    intent.setClass(getActivity(), WNaviGuideActivity.class);
                    startActivity(intent);

                }

                @Override
                public void onRoutePlanFail(WalkRoutePlanError error) {
                    Log.d(TAG, "WalkNavi onRoutePlanFail");
                }

            });
        }


//    /**
//     * 初始化导航起终点Marker
//     */
//    public void initOverlay() {
//
//        MarkerOptions ooA = new MarkerOptions().position(startPt).icon(bdStart)
//                .zIndex(9).draggable(true);
//
//        mStartMarker = (Marker) (mBaiduMap.addOverlay(ooA));
//        mStartMarker.setDraggable(true);
//        startPt = new LatLng(latitude, longitude);
//        endPt = new LatLng(latitude, longitude);
//        mStartMarker.setPosition(startPt);
//        //mStartMarker.setPosition();
//
//        MarkerOptions ooB = new MarkerOptions().position(endPt).icon(bdEnd)
//                .zIndex(5);
//        mEndMarker = (Marker) (mBaiduMap.addOverlay(ooB));
//        mEndMarker.setDraggable(true);
//        mEndMarker.setPosition(endPt);
//
//        mBaiduMap.setOnMarkerDragListener(new BaiduMap.OnMarkerDragListener() {
//            public void onMarkerDrag(Marker marker) {
//            }
//
//            public void onMarkerDragEnd(Marker marker) {
//                if(marker == mStartMarker){
//                    startPt = marker.getPosition();
//                }else if(marker == mEndMarker){
//                    endPt = marker.getPosition();
//                }
//
//                BikeRouteNodeInfo bikeStartNode = new BikeRouteNodeInfo();
//                bikeStartNode.setLocation(startPt);
//                BikeRouteNodeInfo bikeEndNode = new BikeRouteNodeInfo();
//                bikeEndNode.setLocation(endPt);
//                bikeParam = new BikeNaviLaunchParam().startNodeInfo(bikeStartNode).endNodeInfo(bikeEndNode);
//
//                WalkRouteNodeInfo walkStartNode = new WalkRouteNodeInfo();
//                walkStartNode.setLocation(startPt);
//                WalkRouteNodeInfo walkEndNode = new WalkRouteNodeInfo();
//                walkEndNode.setLocation(endPt);
//                walkParam = new WalkNaviLaunchParam().startNodeInfo(walkStartNode).endNodeInfo(walkEndNode);
//
//            }
//
//            public void onMarkerDragStart(Marker marker) {
//            }
//        });
//    }

}