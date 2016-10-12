package com.reconova.facecloud.mqtt;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.reconova.facecloud.MainActivity;
import com.reconova.facecloud.R;
import com.reconova.facecloud.mqtt.internal.Connections;
import com.reconova.facecloud.mqtt.model.ConnectionModel;
import com.reconova.facecloud.util.ToastUtil;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;
import java.util.Random;

/**
 * Created by Administrator on 2016/10/12.
 */

public class ServiceMqttClient extends Service {

    private ChangeListener changeListener = new ChangeListener();
    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    static Random random = new Random();
    static int length = 8;
    private ConnectionModel formModel;

    private MqttAndroidClient mqttAndroidClient;


    final String serverUri = "tcp://192.168.1.198:1883";
    final String subscriptionTopic = "MarsYx";
    final String clientId = "ExampleAndroidClient";

    private NotificationManager mNotifMan;
    private Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        mNotifMan = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        context = getApplicationContext();
        mqttInterface();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //  mqttClientConnection();

        return super.onStartCommand(intent, flags, startId);
    }


    private void addToHistory(String mainText) {
        System.out.println("LOG: " + mainText);

//        ToastUtil.showToast(this, mainText);

//		mAdapter.add(mainText);
//		Snackbar.make(findViewById(android.R.id.content), mainText, Snackbar.LENGTH_LONG)
//				.setAction("Action", null).show();

    }

    public PendingIntent getDefalutIntent(int flags) {
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, new Intent(), flags);
        return pendingIntent;
    }

    public void subscribeToTopic() {
        try {
            mqttAndroidClient.subscribe(subscriptionTopic, 0, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    addToHistory("Subscribed!");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    addToHistory("Failed to subscribe");
                }
            });

            // THIS DOES NOT WORK!
            mqttAndroidClient.subscribe(subscriptionTopic, 0, new IMqttMessageListener() {
                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    // message Arrived!
//                    System.out.println("Message: " + topic + " : " + new String(message.getPayload()));
                    NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);

                    mBuilder.setContentTitle("")//设置通知栏标题
                            .setContentText(new String(message.getPayload())) //设置通知栏显示内容
                            .setContentIntent(getDefalutIntent(Notification.FLAG_INSISTENT)) //设置通知栏点击意图
                            //  .setNumber(number) //设置通知集合的数量
                            .setTicker("您有一条新的通知") //通知首次出现在通知栏，带上升动画效果的
                            .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                            .setPriority(Notification.PRIORITY_DEFAULT) //设置该通知优先级
                            .setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
//                            .setOngoing(false)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
                            .setDefaults(Notification.DEFAULT_VIBRATE)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合
                            //Notification.DEFAULT_ALL  Notification.DEFAULT_SOUND 添加声音 // requires VIBRATE permission
                            .setSmallIcon(R.drawable.facecloud_ico);//设置通知小ICON
                    Notification notification = mBuilder.build();
//                    notification.flags = Notification.FLAG_ONGOING_EVENT  ;
                    notification.flags |= Notification.FLAG_AUTO_CANCEL;//点击清除
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.addCategory(Intent.CATEGORY_LAUNCHER);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                    PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
                    mBuilder.setContentIntent(pendingIntent);
                    mNotificationManager.notify(0, mBuilder.build());

                }
            });

        } catch (MqttException ex) {
            System.err.println("Exception whilst subscribing");
            ex.printStackTrace();
        }
    }

    private void mqttInterface() {
//        android.os.Debug.waitForDebugger();
        mqttAndroidClient = new MqttAndroidClient(getApplicationContext(), serverUri, clientId);
        mqttAndroidClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {

                if (reconnect) {
                    addToHistory("Reconnected to : " + serverURI);
                    // Because Clean Session is true, we need to re-subscribe
                    subscribeToTopic();
                } else {
                    addToHistory("Connected to: " + serverURI);
                }
            }

            @Override
            public void connectionLost(Throwable cause) {
                addToHistory("The Connection was lost.");
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                addToHistory("Incoming message: " + new String(message.getPayload()));
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });

        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(false);


        try {
            //addToHistory("Connecting to " + serverUri);
            mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                    disconnectedBufferOptions.setBufferEnabled(true);
                    disconnectedBufferOptions.setBufferSize(100);
                    disconnectedBufferOptions.setPersistBuffer(false);
                    disconnectedBufferOptions.setDeleteOldestMessages(false);
                    mqttAndroidClient.setBufferOpts(disconnectedBufferOptions);
                    subscribeToTopic();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    addToHistory("Failed to connect to: " + serverUri);
                }
            });


        } catch (MqttException ex) {
            ex.printStackTrace();
        }
    }

    private void mqttClientConnection() {
//        Map<String, Connection> connections =  Connections.getInstance(this.getActivity())
//                .getConnections();
//        String connectionKey = this.getArguments().getString(ActivityConstants.CONNECTION_KEY);
//        Connection connection = connections.get(connectionKey);

        formModel = new ConnectionModel();

        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(AB.charAt(random.nextInt(AB.length())));
        }
        String clientHandle = sb.toString() + '-' + formModel.getServerHostName() + '-' + formModel.getClientId();
        formModel.setClientHandle(clientHandle);
        persistAndConnect(formModel);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class ChangeListener implements PropertyChangeListener {

        /**
         * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
         */
        @Override
        public void propertyChange(PropertyChangeEvent event) {

            if (!event.getPropertyName().equals(ActivityConstants.ConnectionStatusProperty)) {
                return;
            }
//            mainActivity.runOnUiThread(new Runnable() {
//
//                @Override
//                public void run() {
//                    mainActivity.drawerFragment.notifyDataSetChanged();
//                }
//
//            });

        }

    }

    public void persistAndConnect(ConnectionModel model) {
//        Log.i(TAG, "Persisting new connection:" + model.getClientHandle());
        Connection connection = Connection.createConnection(model.getClientHandle(), model.getClientId(), model.getServerHostName(), model.getServerPort(), this, model.isTlsConnection());
        connection.registerChangeListener(changeListener);
        connection.changeConnectionStatus(Connection.ConnectionStatus.CONNECTING);


        String[] actionArgs = new String[1];
        actionArgs[0] = model.getClientId();
        final ActionListener callback = new ActionListener(this,
                ActionListener.Action.CONNECT, connection, actionArgs);
        connection.getClient().setCallback(new MqttCallbackHandler(this, model.getClientHandle()));


        connection.getClient().setTraceCallback(new MqttTraceCallback());

        MqttConnectOptions connOpts = optionsFromModel(model);

        connection.addConnectionOptions(connOpts);
        Connections.getInstance(this).addConnection(connection);
//        connectionMap.add(model.getClientHandle());
//        drawerFragment.addConnection(connection);

//        try {
//            connection.getClient().connect(connOpts, null, callback);
//            Fragment fragment  = new ConnectionFragment();
//            Bundle bundle = new Bundle();
//            bundle.putString(ActivityConstants.CONNECTION_KEY, connection.handle());
//            bundle.putBoolean(ActivityConstants.CONNECTED, true);
//            fragment.setArguments(bundle);
//            String title = connection.getId();
//            displayFragment(fragment, title);
//
//        }
//        catch (MqttException e) {
//            Log.e(this.getClass().getCanonicalName(),
//                    "MqttException Occured", e);
//        }

    }

    public void updateAndConnect(ConnectionModel model) {
        Map<String, Connection> connections = Connections.getInstance(this)
                .getConnections();

//        Log.i(TAG, "Updating connection: " + connections.keySet().toString());
        try {
            Connection connection = connections.get(model.getClientHandle());
            // First disconnect the current instance of this connection
            if (connection.isConnected()) {
                connection.changeConnectionStatus(Connection.ConnectionStatus.DISCONNECTING);
                connection.getClient().disconnect();
            }
            // Update the connection.
            connection.updateConnection(model.getClientId(), model.getServerHostName(), model.getServerPort(), model.isTlsConnection());
            connection.changeConnectionStatus(Connection.ConnectionStatus.CONNECTING);

            String[] actionArgs = new String[1];
            actionArgs[0] = model.getClientId();
            final ActionListener callback = new ActionListener(this,
                    ActionListener.Action.CONNECT, connection, actionArgs);
            connection.getClient().setCallback(new MqttCallbackHandler(this, model.getClientHandle()));

            connection.getClient().setTraceCallback(new MqttTraceCallback());
            MqttConnectOptions connOpts = optionsFromModel(model);
            connection.addConnectionOptions(connOpts);
            Connections.getInstance(this).updateConnection(connection);
//            drawerFragment.updateConnection(connection);

            connection.getClient().connect(connOpts, null, callback);
//            Fragment fragment  = new ConnectionFragment();
//            Bundle bundle = new Bundle();
//            bundle.putString(ActivityConstants.CONNECTION_KEY, connection.handle());
//            fragment.setArguments(bundle);
//            String title = connection.getId();
//            displayFragment(fragment, title);


        } catch (MqttException ex) {

        }
    }


    private MqttConnectOptions optionsFromModel(ConnectionModel model) {

        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(model.isCleanSession());
        connOpts.setConnectionTimeout(model.getTimeout());
        connOpts.setKeepAliveInterval(model.getKeepAlive());
        if (!model.getUsername().equals(ActivityConstants.empty)) {
            connOpts.setUserName(model.getUsername());
        }

        if (!model.getPassword().equals(ActivityConstants.empty)) {
            connOpts.setPassword(model.getPassword().toCharArray());
        }
        if (!model.getLwtTopic().equals(ActivityConstants.empty) && !model.getLwtMessage().equals(ActivityConstants.empty)) {
            connOpts.setWill(model.getLwtTopic(), model.getLwtMessage().getBytes(), model.getLwtQos(), model.isLwtRetain());
        }
        //   if(tlsConnection){
        //       // TODO Add Keys to conOpts here
        //       //connOpts.setSocketFactory();
        //   }
        return connOpts;
    }

}
