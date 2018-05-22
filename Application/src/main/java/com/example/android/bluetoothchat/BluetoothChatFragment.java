/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.bluetoothchat;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;

import android.widget.Button;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import java.text.SimpleDateFormat;
import  java.util.Date;
import android.widget.VideoView;

//import com.example.android.common.logger.Log;

import org.json.JSONObject;
import org.json.JSONException;

/**
 * This fragment controls Bluetooth to communicate with other devices.
 */
public class BluetoothChatFragment extends Fragment {

    private static final String TAG = "BluetoothChatFragment";

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;

    public String tempValue = "";

    public static int presureSound;
    public static int speedSound;

    private static int device_number;
    long mNow;
    Date mDate;
    long eNow;
    Date eDate;

    SimpleDateFormat mFormat = new SimpleDateFormat("hh:mm:ss");


    public static int SensorFlag = 0;

    final static int lnit=0;
    final static  int Run =1;
    final static int Pause=2;

    public static int cur_Status = lnit;
  //  int myCount =1;
 //   long myBaseTime;
//    long myPauseTime;

    public static int myCount =1;
    long myBaseTime;
    long myPauseTime;

    public static SoundPool sp = new SoundPool(1, AudioManager.STREAM_MUSIC,0);//최대 음악파일 개수, 스트림 타입, 음질-기본값:0

    int soundID;
    int soundID2;
    int soundID3;
    int soundID4;
    int soundID5;

    public static String cpr_during_time;

    public static int pre_count_result;
    public static String pre_countt;
    public static String send_start_time;
    public static String send_end_time;
    public static TextView Receive_Data_x;
    public static TextView Receive_Data_z;
    public static TextView Receive_Data_y;
    TextView TV_CPR_START_TIME;
    VideoView videoView;
    TextView TV_STOPWATCH;
    TextView TV_CPR_END_TIME;
    /**
     * Name of the connected device
     */
    private String mConnectedDeviceName = null;

    /**
     * Array adapter for the conversation thread
     */
    private ArrayAdapter<String> mConversationArrayAdapter;

    /**
     * String buffer for outgoing messages
     */
    private StringBuffer mOutStringBuffer;

    /**
     * Local Bluetooth adapter
     */
    private BluetoothAdapter mBluetoothAdapter = null;

    /**
     * Member object for the chat services
     */
    private BluetoothChatService mChatService = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            FragmentActivity activity = getActivity();
            Toast.makeText(activity, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            activity.finish();
        }


         soundID = sp.load(getActivity(), R.raw.resendcompleted,1);
         soundID2 = sp.load(getActivity(), R.raw.gocpr,1);
         soundID3 = sp.load(getActivity(), R.raw.check,1);
         soundID4 = sp.load(getActivity(), R.raw.pressurestrong,1);
         soundID5 = sp.load(getActivity(), R.raw.speedfast,1);


    }

    Handler myTimer = new Handler(){
        public void handleMessage(Message msg){
            TV_STOPWATCH.setText(getTimeOut());
            cpr_during_time = TV_STOPWATCH.getText().toString();

            myTimer.sendEmptyMessage(0);
        }
    };

    public String getTimeOut(){
        long now = SystemClock.elapsedRealtime();
        long outTime = now-myBaseTime;
        String easy_outTime = String.format("%02d:%02d:%02d",outTime/1000/60,(outTime/1000)%60,(outTime%1000)/10);
        return easy_outTime;
    }

    private String getCPRStarttime()
    {
        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);
        return mFormat.format(mDate);
    }


    @Override
    public void onStart() {
        super.onStart();

            Thread myThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {

                    if (SensorFlag == 1) {

                        String data_xx;
                        String data_zz;


                        data_xx = String.valueOf((int) data_x);
                        data_zz = String.valueOf((float) data_z);
                        pre_countt = String.valueOf((int) pre_count_result);

                        try {
                            PHPRequest request = new PHPRequest("http://192.168.43.168/htdocs/data_insert.php");
                            String result = request.PHPtest(myCount, null, null, "20180401", send_start_time, "00:00:00", data_xx, pre_countt, data_zz, FirstActivity.str1);
                            Toast.makeText(getActivity(), FirstActivity.str1, Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    else if (SensorFlag == 2)
                        {
                            String data_xx;
                            String data_zz;


                            data_xx = String.valueOf((int) data_x);
                            data_zz = String.valueOf((float) data_z);
                            pre_countt = String.valueOf((int) pre_count_result);

                            try {
                                PHPRequest request = new PHPRequest("http://192.168.43.168/htdocs/data_insert.php");
                                String result = request.PHPtest(myCount, null, null, "20180401", send_start_time, send_end_time, data_xx, pre_countt, data_zz, FirstActivity.str);
                                // Log.d("test321", result);
                                Thread.sleep(10);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            SensorFlag = 0;
                        }

                }

            }

        });
        myThread.start();


        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session
        } else if (mChatService == null) {
            setupChat();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mChatService != null) {
            mChatService.stop();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
                // Start the Bluetooth chat services
                mChatService.start();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_bluetooth_chat, container, false);
        Button BTN_send = (Button)view.findViewById((R.id.BTN_RESEND));



        final Button BTN_CPR_START_TIME = (Button)view.findViewById(R.id.BTN_CPR_START_TIME);
        final Button BTN_CPR_STOP_TIME = (Button)view.findViewById(R.id.BTN_CPR_STOP_TIME);
        final Button BTN_HISTORY = (Button)view.findViewById(R.id.BTN_HISTORY);


        BTN_HISTORY.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getActivity(), ChartActivity.class); // 다음 넘어갈 클래스 지정
                startActivity(intent); // 다음 화면으로 넘어간다
            }
        });

        BTN_CPR_STOP_TIME.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                switch (cur_Status) {
                    case Pause:

                        myTimer.removeMessages(0);
                        cur_Status = lnit;


                        TV_STOPWATCH.setText("00:00:00");

                        Receive_Data_x.setText("0");
                        Receive_Data_y.setText("0");
                        Receive_Data_z.setText("0");
                        TV_CPR_START_TIME.setText("00:00:00");
                        TV_CPR_END_TIME.setText("00:00:00");

                        Receive_Data_x.setBackgroundColor(Color.argb(0, 0, 0, 0));
                        Receive_Data_y.setBackgroundColor(Color.argb(0, 0, 0, 0));
                        Receive_Data_z.setBackgroundColor(Color.argb(0, 0, 0, 0));
                        SensorFlag = 0;
                        pre_count_result = 0;
                        // }

                        break;
                }

            }

        });

        BTN_CPR_START_TIME.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.BTN_CPR_START_TIME: //시작버튼을 클릭했을때 현재 상태값에 따라 다른 동작을 할수있게끔 구현.
                        switch (cur_Status) {
                            case lnit:
                                sp.play(soundID2,1,1,0,0,1);
                                myBaseTime = SystemClock.elapsedRealtime();
                                System.out.println(myBaseTime);
                                TV_CPR_START_TIME.setText(getCPRStarttime());
                                send_start_time = TV_CPR_START_TIME.getText().toString();
                               myTimer.sendEmptyMessage(0);
                                BTN_CPR_START_TIME.setText("멈춤"); //버튼의 문자"시작"을 "멈춤"으로 변경
                              cur_Status = Run; //현재상태를 런상태로 변경
                                //videoView.seekTo(1);
                                videoView.start();
                                SensorFlag = 1;
                                break;
                            case Run:
                                sp.play(soundID3,1,1,0,0,1);
                                myTimer.removeMessages(0); //핸들러 메세지 제거
                                myPauseTime = SystemClock.elapsedRealtime();
                                TV_CPR_END_TIME.setText(getCPRStarttime());
                                send_end_time = TV_CPR_END_TIME.getText().toString();
                                BTN_CPR_START_TIME.setText("시작");
                                cur_Status = Pause;
                                videoView.stopPlayback();
                                SensorFlag = 2;
                                break;
                        }
                        break;
                }
            }
        });

        BTN_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirstActivity.sendSMS("01026766250","신고 위치 : " + FirstActivity.str);

                //sendSMS("01026766250","위도 : " + FirstActivity.longitude+ "\n경도 : "  + FirstActivity.latitude);
                sp.play(soundID,1,1,0,0,1);

            }
        });

        //원래 fragment를 되돌리기 위해서는 아래 주석을 사용 해야한다.
        return view;//inflater.inflate(R.layout.fragment_bluetooth_chat, container, false);
    }





    ////////////////////////////////////////////////line

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        Receive_Data_x = (TextView)view.findViewById(R.id.ReceiveDatax);
        Receive_Data_y = (TextView)view.findViewById(R.id.ReceiveDatay);
        Receive_Data_z = (TextView)view.findViewById(R.id.ReceiveDataz);

        TV_CPR_START_TIME = (TextView)view.findViewById(R.id.TV_CPR_START_TIME);
        TV_CPR_END_TIME = (TextView)view.findViewById(R.id.TV_CPR_END_TIME);
        TV_STOPWATCH =(TextView)view.findViewById(R.id.TV_STOPWATCH);

        videoView = (VideoView)view.findViewById(R.id.videoView1);
        MediaController mediaController = new MediaController(getActivity());
            mediaController.setAnchorView(videoView);
            Uri video = Uri.parse("android.resource://" + getActivity().getPackageName() + "/" + R.raw.videoplayback);


            videoView.setMediaController(mediaController);
            videoView.setVideoURI(video);
            videoView.requestFocus();



    }

    /**
     * Set up the UI and background operations for chat.
     */
    private void setupChat() {
       // Log.d(TAG, "setupChat()");


        // Initialize the array adapter for the conversation thread
        mConversationArrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.message);

        // Initialize the send button with a listener that for click events

        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = new BluetoothChatService(getActivity(), mHandler);

        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer("");
    }

    /**
     * Makes this device discoverable for 300 seconds (5 minutes).
     */
    private void ensureDiscoverable() {
        if (mBluetoothAdapter.getScanMode() !=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }

    /**
     * Sends a message.
     *
     * @param message A string of text to send.
     */
    private void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            Toast.makeText(getActivity(), R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            mChatService.write(send);

            // Reset out string buffer to zero and clear the edit text field
            mOutStringBuffer.setLength(0);
            //   mOutEditText.setText(mOutStringBuffer);
        }
    }

    /**
     * The action listener for the EditText widget, to listen for the return key
     */

    private TextView.OnEditorActionListener mWriteListener
            = new TextView.OnEditorActionListener() {
        public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
            // If the action is a key-up event on the return key, send the message
            if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_UP) {
                String message = view.getText().toString();
                sendMessage(message);
            }
            return true;
        }
    };

    /**
     * Updates the status on the action bar.
     *
     * @param resId a string resource ID
     */
    private void setStatus(int resId) {
        FragmentActivity activity = getActivity();
        if (null == activity) {
            return;
        }
        final ActionBar actionBar = activity.getActionBar();
        if (null == actionBar) {
            return;
        }
        actionBar.setSubtitle(resId);
    }

    /**
     * Updates the status on the action bar.
     *
     * @param subTitle status
     */
    private void setStatus(CharSequence subTitle) {
        FragmentActivity activity = getActivity();
        if (null == activity) {
            return;
        }
        final ActionBar actionBar = activity.getActionBar();
        if (null == actionBar) {
            return;
        }
        actionBar.setSubtitle(subTitle);
    }

    public static int data_x;
    public static int data_y;
    public static float data_z;



    JSONObject JsonData;
    /**
     * The Handler that gets information back from the BluetoothChatService
     */
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            FragmentActivity activity = getActivity();
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
                            setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
                            mConversationArrayAdapter.clear();
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
                            setStatus(R.string.title_connecting);
                            break;
                        case BluetoothChatService.STATE_LISTEN:
                        case BluetoothChatService.STATE_NONE:
                            setStatus(R.string.title_not_connected);
                            break;
                    }
                    break;
                case Constants.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    mConversationArrayAdapter.add("Me:  " + writeMessage);
                    break;

                //여기서 부터 블루투스 값을 읽기 시작한다.
                case Constants.MESSAGE_READ:

                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);


                    if (readMessage.contains("}")) {
                        tempValue += readMessage;
                        mConversationArrayAdapter.add(mConnectedDeviceName + ":  " + tempValue);
                        myCount++;

                        try {
                            // Toast.makeText(getContext(), "!!!!!!!", Toast.LENGTH_SHORT).show();


                            JsonData = new JSONObject(tempValue);

                            data_x = Integer.parseInt(JsonData.getString("x"));
                            data_y = Integer.parseInt(JsonData.getString("y"));
                            data_z = Float.parseFloat(JsonData.getString("z"));


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //    Log.d("test1212 : ", tempValue);

                        tempValue = "";
                    } else {
                        tempValue += readMessage;
                    }

                    if (SensorFlag == 1) {
                        pres_Count(data_x, data_z);

                        Receive_Data_x.setText(String.valueOf((int) data_x) + "  KG");
                        Receive_Data_y.setText(String.valueOf((int) pre_count_result) + "  회");
                        double data_z1 = data_z * 0.4;
                        int data_z11 = (int)data_z1;
                        Receive_Data_z.setText(String.valueOf((int) data_z11) + "  CM");

                        changeDataxColor(data_x);
                        changeDatayColor(pre_count_result);
                        changeDatazColor(data_z);



                        String speedSound1 = String.valueOf((int)speedSound);
                        Log.d("speedSound : ", speedSound1);
                        String presuSound1 = String.valueOf((int)presureSound);

                        Log.d("presuSound1: ", presuSound1);
/*
                        if(speedSound == 200)
                        {
                            sp.play(soundID5,100,100,1,0,1);
                            speedSound = 0;
                        }
*/
                        if(presureSound >= 200)
                        {
                            sp.play(soundID4,1,1,0,0,1);
                            presureSound = 0;
                        }

                    }


                   break;
                case Constants.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
                    if (null != activity) {
                        Toast.makeText(activity, "Connected to "
                                + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    }
                    break;
                case Constants.MESSAGE_TOAST:
                    if (null != activity) {
                        Toast.makeText(activity, msg.getData().getString(Constants.TOAST),
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

    public static int aaaa = 0;
    public void pres_Count(float data_x, float data_z)
    {


        if(data_x >= 45 && data_z >= 4.00)
        {

            aaaa++;
            if (aaaa == 2)
            {
                pre_count_result++;
                aaaa = 0;
            }
        }
   }
    public static void changeDatayColor(int aa)
    {
        if(aa<1)
        {
            Receive_Data_y.setBackgroundResource(R.drawable.sensorred);
        }
        else
        {
            Receive_Data_y.setBackgroundResource(R.drawable.sensorgreen);
        }
    }

    public static void changeDatazColor(float aa)
    {
        if(aa <= 4) {
            Receive_Data_z.setBackgroundResource(R.drawable.sensorred);
            speedSound++;
        }
        else
        {
            Receive_Data_z.setBackgroundResource(R.drawable.sensorgreen);
        }
    }
    public static void changeDataxColor(int aa)
    {
        if(aa < 45)
        {
            Receive_Data_x.setBackgroundResource(R.drawable.sensorred);
            presureSound++;
        }
        else
        {
            Receive_Data_x.setBackgroundResource(R.drawable.sensorgreen);
        }
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE_SECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, true);
                }
                break;
            case REQUEST_CONNECT_DEVICE_INSECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, false);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    setupChat();
                } else {
                    // User did not enable Bluetooth or an error occurred
             //       Log.d(TAG, "BT not enabled");
                    Toast.makeText(getActivity(), R.string.bt_not_enabled_leaving,
                            Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                }
        }
    }

    /**
     * Establish connection with other device
     *
     * @param data   An {@link Intent} with {@link DeviceListActivity#EXTRA_DEVICE_ADDRESS} extra.
     * @param secure Socket Security type - Secure (true) , Insecure (false)
     */
    private void connectDevice(Intent data, boolean secure) {
        // Get the device MAC address
        //String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        //bluetooth 자동연결
        String address = "00:21:13:01:93:37";

        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        mChatService.connect(device, secure);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.bluetooth_chat, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.secure_connect_scan: {
                // Launch the DeviceListActivity to see devices and do scan
                Intent serverIntent = new Intent(getActivity(), DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
                return true;
            }
            case R.id.insecure_connect_scan: {
                // Launch the DeviceListActivity to see devices and do scan
                Intent serverIntent = new Intent(getActivity(), DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_INSECURE);
                return true;
            }
            case R.id.discoverable: {
                // Ensure this device is discoverable by others
                ensureDiscoverable();
                return true;
            }
        }
        return false;
    }
}
