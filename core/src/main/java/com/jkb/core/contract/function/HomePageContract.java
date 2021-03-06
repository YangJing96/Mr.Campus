package com.jkb.core.contract.function;

import com.jkb.core.base.BasePresenter;
import com.jkb.core.base.BaseView;

/**
 * HomePageFragment的页面协议类
 * Created by JustKiddingBaby on 2016/7/25.
 */
public interface HomePageContract {

    interface View extends BaseView<Presenter> {

        /**
         * 显示左滑菜单视图
         */
        void showLeftMenu();

        /**
         * 显示右滑菜单视图
         */
        void showRightMenu();

        /**
         * 设置已经登录的右滑菜单的视图
         */
        void setLoginRightMenuView();

        /**
         * 设置未登录的右滑菜单的视图
         */
        void setLogoutRightMenuView();

        /**
         * 显示热门的视图
         */
        void showHot();

        /**
         * 显示动态
         */
        void showMatters();

        /**
         * 设置左边菜单的通知状态
         */
        void setLeftMenuNotifyStatus(boolean leftMenuNotifyStatus);

        /**
         * 设置右边菜单的通知状态
         */
        void setRightMenuNotifyStatus(boolean rightMenuNotifyStatus);
    }

    interface Presenter extends BasePresenter {

        /**
         * 判断是否登录的身份来显示视图
         */
        void judgeStatusToShowView();
    }
}
