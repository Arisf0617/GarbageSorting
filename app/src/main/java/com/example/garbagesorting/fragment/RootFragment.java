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
 * ???????????? + ?????? + poi???????????? by??????haoyu 2021/5/7 17???00 add
 * ???bug???poi?????????????????????????????????(????????? 2021/5/7 20???49)
 * ??????????????????????????????
 * ???????????????????????????????????? 2021/5/14 22???00
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

    // ??????View Poi??????
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

    // ??????
    private int mLoadIndex = 0;

    //????????????
//    private Button bikeBtn = null;
//    private Button walkBtn = null;

    private ImageView bikeIV = null;
    private ImageView walkIV = null;

    /*???????????????Marker????????????????????????????????????*/
    private Marker mStartMarker;
    private Marker mEndMarker;

    private LatLng startPt;
    private LatLng endPt;

    double latitude;//????????????
    double longitude;//????????????

    private BikeNaviLaunchParam bikeParam;
    private WalkNaviLaunchParam walkParam;

    private BitmapDescriptor bdStart = BitmapDescriptorFactory
            .fromResource(R.drawable.icon_start);
    private BitmapDescriptor bdEnd = BitmapDescriptorFactory
            .fromResource(R.drawable.icon_end);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //????????????
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

        /*??????????????????*/
        bikeIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bikeParam == null){
                    ToastUtil.showMsg(getActivity(),"???????????????????????????????????????!");
                }else{
                    startBikeNavi();
                    //System.out.println("????????????");
                    ToastUtil.showMsg(getActivity(),"????????????????????????...");
                }
            }
        });

        /*????????????????????????*/
        walkIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(walkParam == null){
                    ToastUtil.showMsg(getActivity(),"???????????????????????????????????????!");
                }else{
                    walkParam.extraNaviMode(0);
                    startWalkNavi();
                    ToastUtil.showMsg(getActivity(),"????????????????????????...");
                }
            }
        });

        return root;
    }

    /**
     * ?????????View
     */
    private void initView() {
        //mMapView = findViewById(R.id.mapview);
        //mBaiduMap = mMapView.getMap();
        // ??????????????????
        mBaiduMap.setMyLocationEnabled(true);
        MyLocationConfiguration myLocationConfiguration =
                new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL, true, null);
        // ??????????????????????????????
        mBaiduMap.setMyLocationConfiguration(myLocationConfiguration);
        // ???????????????????????????
        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        // ??????????????????????????????????????????
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_UI);
        initPoiView();
    }

    /**
     * ?????????Poi?????????
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
        // ?????????????????????
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
                .pageNum(mLoadIndex) // ????????????
                .cityLimit(true)
                .scope(1));
    }

    @Override
    public void onGetPoiResult(PoiResult poiResult) {
        if (poiResult == null || poiResult.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
            mLoadIndex = 0;
            Toast.makeText(getActivity(), "???????????????", Toast.LENGTH_LONG).show();
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
            Toast.makeText(getActivity(), "???????????????", Toast.LENGTH_LONG).show();
            return;
        }

        List<SuggestionResult.SuggestionInfo> suggesInfos = suggestionResult.getAllSuggestions();
        if (null == suggesInfos) {
            return;
        }

        // ???????????????
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


        //System.out.println("123456789"+poiInfo.getLocation());//??????poi????????????
        endPt = poiInfo.getLocation();

        /*?????????????????????????????????*/
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

        /* ??????????????????Marker */
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
     * ??????????????????poi
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

        // ?????????????????? latLng ??????
        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLng(latLng);
        mBaiduMap.setMapStatus(mapStatusUpdate);

        // ???????????????
        HideKeyboardUtils.hideKeyBoard(getActivity());

        // ???????????????
        clearData();

        // ???????????????
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

        // ?????????????????? latLng ??????
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

        // ???????????????
        HideKeyboardUtils.hideKeyBoard(getActivity());

        // ???????????????
        showPoiMarker(poiInfo, i);
    }


    private void showPoiMarker(PoiInfo poiInfo, int i) {
        if (null == poiInfo) {
            return;
        }

        MarkerOptions markerOptions = new MarkerOptions()
                .position(poiInfo.getLocation())
                .icon(mBitmapDescWaterDrop);

        // ?????????poi????????????
        if (0 == i) {
            InfoWindow infoWindow = getPoiInfoWindow(poiInfo);
            markerOptions.scaleX(1.5f).scaleY(1.5f).infoWindow(infoWindow);
        }

        Marker marker = (Marker) mBaiduMap.addOverlay(markerOptions);
        if (null != marker) {
            mMarkerPoiInfo.put(marker, poiInfo);

            if (0 == i) {//???????????????marker?????????????????????
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
     * ???????????????
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
     * ????????????suggestion??????
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
     * ????????????poi??????
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
     * ????????????
     */
    private void hideInfoLayout() {
        if (null == mLayoutDetailInfo) {
            return;
        }

        mLayoutDetailInfo.setVisibility(View.GONE);
    }

    /**
     * ????????????????????????????????????
     */
    private void setBounds(List<LatLng> latLngs) {
        if (null == latLngs || latLngs.size() <= 0) {
            return;
        }

        int horizontalPadding = 80;
        int verticalPaddingBottom = 400;

        // ????????????????????????
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        // ????????????????????????????????????????????????
        builder.include(latLngs);

        // ??????????????????????????????MapView???padding????????????????????????
        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLngBounds(builder.build(),
                horizontalPadding,
                verticalPaddingBottom,
                horizontalPadding,
                verticalPaddingBottom);
        // ????????????
        mBaiduMap.setMapStatus(mapStatusUpdate);
        // ?????????????????????????????????????????????????????????????????????????????????logo?????????????????????
        mBaiduMap.setViewPadding(0,
                0,
                0,
                verticalPaddingBottom);
    }


    /**
     * ????????????sug?????????????????????mEditPoi?????????????????????sug???????????????key
     *
     * @param suggestInfo
     */
    private void setPoiTextWithLocateSuggestInfo(SuggestionResult.SuggestionInfo suggestInfo) {
        if (null == suggestInfo) {
            return;
        }

        mEditTextPoi.removeTextChangedListener(mMyTextWatcher); // ???????????????TextWatcher???????????????sug??????
        mEditTextPoi.setText(suggestInfo.getKey());
        mEditTextPoi.setSelection(suggestInfo.getKey().length()); // ?????????????????????
        mEditTextPoi.addTextChangedListener(mMyTextWatcher);
    }


    /**
     * ????????????
     */
    private void startLocation() {
        // ???????????????
        mLocationClient = new LocationClient(getContext());
        mLocationClient.registerLocationListener(mListener);
        LocationClientOption locationClientOption = new LocationClientOption();
        // ????????????????????????????????????????????? LocationMode.Hight_Accuracy???????????????
        locationClientOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        // ???????????????????????????????????????????????????GCJ02
        locationClientOption.setCoorType("bd09ll");
        // ???????????????0?????????????????????????????????????????????????????????0
        // ???????????????0????????????1000ms???????????????
        locationClientOption.setScanSpan(1000);
        //???????????????????????????gps?????????false
        locationClientOption.setOpenGps(true);
        // ?????????????????????????????????????????????????????????????????????false
        // ?????????????????????????????????????????????????????????????????????true
        locationClientOption.setIsNeedAddress(true);
        // ???????????????false?????????????????????POI??????????????????BDLocation
        locationClientOption.setIsNeedLocationPoiList(true);
        // ??????????????????
        mLocationClient.setLocOption(locationClientOption);
        // ????????????
        mLocationClient.start();
    }


    /**
     * ????????????
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
                        if (granted) {//????????????
                            //????????????????????????
                            startLocation();// ???????????????
                        } else {//????????????
                            //ToastUtil.showMsg(getActivity(),"???????????????");
                            //Toast.makeText(MainActivity.this,"???????????????",Toast.LENGTH_SHORT).show();
                        }
                    });
        }else {
            startLocation();// ???????????????
        }
    }

    /**
     * ???????????????????????????
     */
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        double x = sensorEvent.values[SensorManager.DATA_X];
        if (Math.abs(x - lastX) > 1.0) {
            mCurrentDirection = (float) x;
            myLocationData = new MyLocationData.Builder()
                    .accuracy(mCurrentAccracy)
                    // ?????????????????????????????????????????????????????????0-360
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
        // ???????????????????????????
        mSensorManager.unregisterListener(this);
        // ?????????????????????
        mLocationClient.stop();
        // ??????????????????
        mBaiduMap.setMyLocationEnabled(false);
        // ???activity??????onDestroy???????????????mMapView.onDestroy()
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
         * ????????????????????????
         *
         * @param location ????????????
         */
        @Override
        public void onReceiveLocation(BDLocation location) {
            if(mEditTextCity.getText().toString().isEmpty()){
                mEditTextCity.setText(location.getCity());
            }
            // MapView ???????????????????????????????????????
            if (location == null || mMapView == null) {
                return;
            }
            mCurrentLat = location.getLatitude();
            mCurrentLon = location.getLongitude();
            mCurrentAccracy = location.getRadius();

            latitude = location.getLatitude();    //??????????????????
            longitude = location.getLongitude();    //??????????????????
            System.out.println("????????????latitude"+latitude+"????????????longitude"+longitude);

            if(startPt == null){
                startPt = new LatLng(latitude, longitude);
            }

            myLocationData = new MyLocationData.Builder()
                    .accuracy(mCurrentAccracy)// ????????????????????????????????????????????????
                    .direction(mCurrentDirection)// ?????????????????????????????????????????????????????????0-360
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
            // ??????????????????
            String cityStr = mEditTextCity.getText().toString();
            // ?????????????????????
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
     * ??????????????????
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
     * ????????????????????????
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
         * ??????????????????
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
         * ????????????????????????
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
//     * ????????????????????????Marker
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