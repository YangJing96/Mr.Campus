package com.jkb.core.control.messageState;

import android.content.Context;

import com.jkb.core.control.messageState.dataSource.MessagesDataCallback;
import com.jkb.core.control.messageState.dataSource.MessagesDbLocalDataSource;
import com.jkb.core.control.userstate.LoginContext;
import com.jkb.model.info.UserInfoSingleton;
import com.jkb.model.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import jkb.mrcampus.db.entity.Messages;

/**
 * 消息的被观察者对象
 * Created by JustKiddingBaby on 2016/10/24.
 */

public class MessageObservable extends Observable implements MessageObservableAction {

    //data
    private Context mContext;
    private MessagesDbLocalDataSource dataSource;
    private int user_id;
    //状态
    private MessageObservableAction messageUserState;
    private MessageLoginObservable loginState;
    private MessageLogoutObservable logoutState;

    private MessageObservable() {
        dataSource = MessagesDbLocalDataSource.newInstance(mContext);
        loginState = new MessageLoginObservable(dataSource, user_id);
        logoutState = new MessageLogoutObservable(dataSource);

        setMessageUserState(false);//默认为未登录下的状态
    }

    private static MessageObservable INSTANCE = null;

    /**
     * 初始化方法
     */
    public static void init(Context applicationContext) {
        newInstance().setContext(applicationContext);
    }

    public static MessageObservable newInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MessageObservable();
        }
        return INSTANCE;
    }

    /**
     * 设置上下文对象
     */
    public void setContext(Context context) {
        this.mContext = context;
    }

    /**
     * 设置用户id
     */
    public void setUser_id(int user_id) {
        this.user_id = user_id;
        loginState.setUser_id(user_id);
        setChanged();
        notifyObservers();
    }

    /**
     * 设置状态
     */
    public void setMessageUserState(boolean isLogin) {
        if (!isLogin) {
            messageUserState = logoutState;
            setUser_id(0);
        } else {
            messageUserState = loginState;
        }
        setChanged();
        notifyObservers();
    }


    @Override
    public synchronized void addObserver(Observer o) {
        //当有新的观察者加入的时候，通知全体成员更新数据
        //目的在于给推送通知给新加入的成员
        LogUtils.d(MessageObservable.class, "我接收到了新的订阅成员,我将会发送全体消息");
        super.addObserver(o);
        setChanged();
        notifyObservers();
    }

    ////////////////Action分割线////////////////////////////////////////////////////////////////

    @Override
    public List<Messages> getAllDynamicMessage() {
        return messageUserState.getAllDynamicMessage();
    }

    @Override
    public List<Messages> getAllUnReadDynamicMessage() {
        return messageUserState.getAllUnReadDynamicMessage();
    }

    @Override
    public int getAllUnReadMessageCount() {
        return messageUserState.getAllUnReadMessageCount();
    }

    @Override
    public int getAllDynamicMessageCount() {
        return messageUserState.getAllDynamicMessageCount();
    }

    @Override
    public int getAllUnReadDynamicMessageCount() {
        return messageUserState.getAllUnReadDynamicMessageCount();
    }

    @Override
    public void readMessage(Messages messages) {
        messageUserState.readMessage(messages);
        //读取消息
        setChanged();
        notifyObservers();
    }

    @Override
    public void readMessage(int messageId) {
        //得到消息体
        messageUserState.readMessage(messageId);
    }

    @Override
    public void saveMessage(Messages messages) {
        messageUserState.saveMessage(messages);
        //设置数据更新
        setChanged();
        notifyObservers();
    }
}