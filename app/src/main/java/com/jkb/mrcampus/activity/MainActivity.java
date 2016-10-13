package com.jkb.mrcampus.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jkb.core.Injection;
import com.jkb.core.contract.menu.MenuContract;
import com.jkb.core.control.userstate.LoginContext;
import com.jkb.core.control.userstate.UserState;
import com.jkb.core.presenter.function.index.HomePagePresenter;
import com.jkb.core.presenter.function.setting.FunctionSettingPresenter;
import com.jkb.core.presenter.menu.MenuPresenter;
import com.jkb.core.presenter.menu.RightMenuPresenter;
import com.jkb.core.presenter.menu.SwitchFunctionPresenter;
import com.jkb.mrcampus.R;
import com.jkb.mrcampus.base.BaseSlideMenuActivity;
import com.jkb.mrcampus.fragment.function.index.HomePageFragment;
import com.jkb.mrcampus.fragment.function.setting.SettingFragment;
import com.jkb.mrcampus.fragment.function.special.SubjectFragment;
import com.jkb.mrcampus.fragment.function.tools.ToolsFragment;
import com.jkb.mrcampus.fragment.menu.RightMenuFragment;
import com.jkb.mrcampus.fragment.menu.SwitchFunctionFragment;
import com.jkb.mrcampus.helper.ActivityUtils;
import com.jkb.mrcampus.service.LocationService;
import com.jkb.mrcampus.utils.ClassUtils;

/**
 * 核心的Activity类，负责显示主要功能模块
 * Created by JustKiddingBaby on 2016/7/23.
 */
public class MainActivity extends BaseSlideMenuActivity implements MenuContract.View {

    /**
     * 展示的当前页面
     */
    private MenuPresenter.SHOW_VIEW showView = MenuPresenter.SHOW_VIEW.HOMEPAGE;
    private static final String SAVE_SHOW_VIEW_POSITION = "save_show_view_position";
    //本页的Presenter层
    private MenuPresenter mPresenter;
    //菜单
    private SlidingMenu slidingMenu;

    //左滑菜单
    private SwitchFunctionFragment functionFragment;//左滑菜单
    private SwitchFunctionPresenter switchFunctionPresenter;

    //右滑菜单
    private RightMenuFragment rightMenuFragment;//右滑菜单View视图层
    private RightMenuPresenter rightMenuPresenter;//右滑菜单的P层

    //首页
    private HomePageFragment homePageFragment;
    private HomePagePresenter homePagePresenter;

    //设置
    private SettingFragment settingFragment;
    private FunctionSettingPresenter functionSettingPresenter;

    //小工具
    private ToolsFragment toolsFragment;

    //专题
    private SubjectFragment subjectFragment;

    //服务
//    private LocationService locationService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRootView(R.layout.aty_main);
        init(savedInstanceState);
        mPresenter.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        mPresenter.start();
    }

    @Override
    protected void initListener() {
        LoginContext.getInstance().setLoginStatusChangedListener(loginStatusChangedListener);
    }


    @Override
    protected void initData(Bundle savedInstanceState) {
        //恢复数据
        if (savedInstanceState != null) {
            showView = (MenuPresenter.SHOW_VIEW) savedInstanceState.
                    getSerializable(SAVE_SHOW_VIEW_POSITION);
        }
        fm = getSupportFragmentManager();
        initPresenter();
        initSlideMenu(savedInstanceState);

        startLocationService();//开启服务

        //第一次进入时调用显示首页视图
        if (!savedInstanceStateValued) {
            showIndex();
        } else {
            restorePresenters();
        }
    }

    /**
     * 开启Service
     */
    private void startLocationService() {
        System.out.println("提示信息:我在绑定service");
        if (!ActivityUtils.isServiceWorked(LocationService.class.getName(), getApplicationContext())) {
            Intent intent = new Intent(context, LocationService.class);
            startService(intent);
        }
    }


    /**
     * 恢复添加过的Presenter
     */
    @Override
    public void restorePresenter(String fragmentTAG) {
        if (ClassUtils.getClassName(HomePageFragment.class).equals(fragmentTAG)) {
            homePageFragment = (HomePageFragment) fm.findFragmentByTag(fragmentTAG);
            homePagePresenter = new HomePagePresenter(homePageFragment);
        } else if (ClassUtils.getClassName(SettingFragment.class).equals(fragmentTAG)) {
            settingFragment = (SettingFragment) fm.findFragmentByTag(fragmentTAG);
            functionSettingPresenter = new FunctionSettingPresenter(settingFragment,
                    Injection.provideFunctionSettingDataRepertory(getApplicationContext()));
        } else if (ClassUtils.getClassName(ToolsFragment.class).equals(fragmentTAG)) {
            toolsFragment = (ToolsFragment) fm.findFragmentByTag(fragmentTAG);
        } else if (ClassUtils.getClassName(SubjectFragment.class).equals(fragmentTAG)) {
            subjectFragment = (SubjectFragment) fm.findFragmentByTag(fragmentTAG);
        }
    }

    /**
     * 初始化Presenter层数据
     */
    private void initPresenter() {
        mPresenter = new MenuPresenter(this, Injection.provideLoginResponsitory(getApplicationContext()));
        mPresenter.setCurrentView(showView);
    }


    @Override
    protected void initView() {
        slideMenuSetting();//设置SlideMenu
    }

    /**
     * 初始化侧滑菜单
     */
    private void initSlideMenu(Bundle savedInstanceState) {
        //设置Fragments
        if (savedInstanceState == null) {
            initMenuFragments();
        } else {
            getMenuFragmentsFromFm();
        }
        //初始化侧滑菜单的Presenter层
        if (switchFunctionPresenter == null) {
            switchFunctionPresenter = new SwitchFunctionPresenter(functionFragment);
        }
        if (rightMenuPresenter == null) {
            rightMenuPresenter = new RightMenuPresenter(rightMenuFragment,
                    Injection.provideRightMenuDataRepertory(getApplicationContext()));
        }
    }

    /**
     * 设置SlideMenu
     */
    private void slideMenuSetting() {
        slidingMenu = getSlidingMenu();
        slidingMenu.setMode(SlidingMenu.LEFT_RIGHT);
        //设置触摸屏幕的模式
        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
        slidingMenu.setShadowWidthRes(R.dimen.shadow_width);
        slidingMenu.setShadowDrawable(R.drawable.shadow);
        // 设置滑动菜单视图的宽度
        slidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        // 设置渐入渐出效果的值
        slidingMenu.setFadeDegree(0.35f);
        // menu.setBehindScrollScale(1.0f);
        slidingMenu.setSecondaryShadowDrawable(R.drawable.shadow);

        //设置左右布局
        setBehindContentView(R.layout.menu_frame_left);
        slidingMenu.setSecondaryMenu(R.layout.menu_frame_right);
    }

    /**
     * 从FragmentManager中得到Fragment
     */
    private void getMenuFragmentsFromFm() {
        functionFragment = (SwitchFunctionFragment) fm.
                findFragmentByTag(SwitchFunctionFragment.class.getName());
        rightMenuFragment = (RightMenuFragment) fm.
                findFragmentByTag(RightMenuFragment.class.getName());
    }

    /**
     * 初始化菜单Fragment
     */
    private void initMenuFragments() {
        //初始化左滑菜单
        if (functionFragment == null) {
            functionFragment = SwitchFunctionFragment.newInstance();
            ActivityUtils.addFragmentToActivity(fm, functionFragment, R.id.id_menu_left);
        }
        if (rightMenuFragment == null) {
            //初始化右滑菜单
            rightMenuFragment = RightMenuFragment.newInstance();
            ActivityUtils.addFragmentToActivity(fm, rightMenuFragment, R.id.id_menu_right);
        }
    }

    @Override
    public void showLeftMenu() {
        Log.d(TAG, "showLeftMenu");
        slidingMenu.showMenu();
    }

    @Override
    public void showRightMenu() {
        Log.d(TAG, "showRightMenu");
        if (LoginContext.getInstance().isLogined()) {
            slidingMenu.showSecondaryMenu();
        } else {
            startLoginActivity();
        }
    }

    @Override
    public void hideMenu() {
        Log.d(TAG, "hideMenu");
        toggle();
    }

    @Override
    public void setMenuOnCloseListener(SlidingMenu.OnCloseListener onClosedListener) {
        slidingMenu.setOnCloseListener(onClosedListener);
    }

    @Override
    public void setMenuOnClosedListener(SlidingMenu.OnClosedListener onClosedListener) {
        slidingMenu.setOnClosedListener(onClosedListener);
    }

    @Override
    public void setMenuOpenListener(SlidingMenu.OnOpenListener openListener) {
        slidingMenu.setOnOpenListener(openListener);
    }

    @Override
    public void setMenuOpenedListener(SlidingMenu.OnOpenedListener openedListener) {
        slidingMenu.setOnOpenedListener(openedListener);
    }

    @Override
    public void showIndex() {
        Log.d(TAG, "showIndex");
        initFragmentStep1(HomePageFragment.class);
        showView = MenuPresenter.SHOW_VIEW.HOMEPAGE;
        ActivityUtils.showFragment(fm, homePageFragment);
    }

    @Override
    public void startMap() {
        Log.d(TAG, "startMap");
        Intent intent = new Intent(this, MapActivity.class);
        startActivityWithPushLeftAnim(intent);
    }

    @Override
    public void showSpecialModel() {
        Log.d(TAG, "showSpecialModel");
        initFragmentStep1(SubjectFragment.class);
        showView = MenuPresenter.SHOW_VIEW.FEATURE;
        ActivityUtils.showFragment(fm, subjectFragment);
    }

    @Override
    public void showTools() {
        Log.d(TAG, "showTools");
        initFragmentStep1(ToolsFragment.class);
        showView = MenuPresenter.SHOW_VIEW.TOOLS;
        ActivityUtils.showFragment(fm, toolsFragment);
    }

    @Override
    public void startMessage() {
        Log.d(TAG, "startMessage");
        startMessageCenterActivity();
    }

    @Override
    public void showSetting() {
        Log.d(TAG, "showSetting");
        initFragmentStep1(SettingFragment.class);
        showView = MenuPresenter.SHOW_VIEW.SETTING;
        ActivityUtils.showFragment(fm, settingFragment);
    }

    @Override
    public void startChooseSchools() {
        Log.d(TAG, "startChooseSchools");
//        LoginContext.getInstance().setUserState(new LogoutState());
        showSelectSchoolView();
    }


    @Override
    public void startLoginActivity() {
        Log.d(TAG, "startLoginActivity");
        Intent intent = new Intent(this, EnteringActivity.class);
        startActivityWithPushLeftAnim(intent);
    }

    @Override
    public void hideAllView() {
        ActivityUtils.hideFragments(fm,
                SwitchFunctionFragment.class.getName(), RightMenuFragment.class.getName());
    }

    /**
     * 初始化Fragment步骤2
     */
    @Override
    public void initFragmentStep2(Class<?> fragmentClass) {
        String fragmentTAG = fragmentClass.getName();
        if (ClassUtils.getClassName(HomePageFragment.class).equals(fragmentTAG)) {
            initIndexFragment();
        } else if (ClassUtils.getClassName(SettingFragment.class).equals(fragmentTAG)) {
            initSettingFragment();
        } else if (ClassUtils.getClassName(ToolsFragment.class).equals(fragmentTAG)) {
            initToolsFragment();
        } else if (ClassUtils.getClassName(SubjectFragment.class).equals(fragmentTAG)) {
            initSubjectFragment();
        }
    }


    /**
     * 初始化首页的页面数据
     */
    private void initIndexFragment() {
        if (homePageFragment == null) {
            homePageFragment = HomePageFragment.newInstance();
            ActivityUtils.addFragmentToActivity(fm, homePageFragment, R.id.fm_content);
        }
        if (homePagePresenter == null) {
            homePagePresenter = new HomePagePresenter(homePageFragment);
        }
    }

    /**
     * 初始化设置页面的数据
     */
    private void initSettingFragment() {
        if (settingFragment == null) {
            settingFragment = SettingFragment.newInstance();
            ActivityUtils.addFragmentToActivity(fm, settingFragment, R.id.fm_content);
        }
        if (functionSettingPresenter == null) {
            functionSettingPresenter = new FunctionSettingPresenter(settingFragment,
                    Injection.provideFunctionSettingDataRepertory(getApplicationContext()));
        }
    }

    /**
     * 初始化专题
     */
    private void initSubjectFragment() {
        if (subjectFragment == null) {
            subjectFragment = SubjectFragment.newInstance();
            ActivityUtils.addFragmentToActivity(fm, subjectFragment, R.id.fm_content);
        }
    }

    /**
     * 初始化小工具
     */
    private void initToolsFragment() {
        if (toolsFragment == null) {
            toolsFragment = ToolsFragment.newInstance();
            ActivityUtils.addFragmentToActivity(fm, toolsFragment, R.id.fm_content);
        }
    }

    @Override
    public void setPresenter(MenuContract.Presenter presenter) {
        mPresenter = (MenuPresenter) presenter;
    }

    @Override
    public void showLoading(String value) {
        super.showLoading(value);
    }

    @Override
    public void dismissLoading() {
        super.dismissLoading();
    }

    @Override
    public void showReqResult(String value) {
        showShortToast(value);
    }

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(SAVE_SHOW_VIEW_POSITION, showView);
    }

    @Override
    public void onBackPressed() {
        if (slidingMenu.isMenuShowing() || slidingMenu.isSecondaryMenuShowing()) {
            onBackPressed();
        } else {
            exitBy2Click();
        }
    }

    /**
     * 是否登录的状态变化的监听器
     */
    private UserState.LoginStatusChangedListener loginStatusChangedListener =
            new UserState.LoginStatusChangedListener() {
                @Override
                public void onLogin() {
                    slidingMenu.setMode(SlidingMenu.LEFT_RIGHT);//设置为两侧滑动
                }

                @Override
                public void onLogout() {
                    slidingMenu.setMode(SlidingMenu.LEFT);//设置为只有左侧滑动
                }
            };
}
