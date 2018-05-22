package com.example.android.bluetoothchat;

import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.media.AudioManager;
import android.media.SoundPool;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class FirstActivity extends AppCompatActivity {

    TextView tv;
    public static double longitude;
    public static double latitude;
    public static String str = "";
    public static String str1 = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        tv = (TextView) findViewById(R.id.textView2);
        LinearLayout first_layout = (LinearLayout)findViewById(R.id.first_layout);
       // first_layout.setBackgroundColor(Color.WHITE);

        final LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        ImageButton BTN1 = (ImageButton)findViewById(R.id.BTN_start);

        //////////////////////////////////////////////////////////////SOUNDPOOL 관련
        final SoundPool sp = new SoundPool(1, AudioManager.STREAM_MUSIC,0);//최대 음악파일 개수, 스트림 타입, 음질-기본값:0

        final int soundID = sp.load(this, R.raw.sendcompleted,1);

        BTN1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                //http://bitsoul.tistory.com/131

                // GPS 제공자의 정보가 바뀌면 콜백하도록 리스너 등록하기~!!!
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,100,1,mLocationListener);
                lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,100,1,mLocationListener);


                sp.play(soundID,1,1,0,0,1);

                //message 사용
                //sendSMS("01026766250","위도 : " + FirstActivity.longitude+ "\n경도 : "  + FirstActivity.latitude);
                getLocation(FirstActivity.latitude,FirstActivity.longitude);
                String ss = String.valueOf((float)latitude);
                String ss1 = String.valueOf((float)longitude);

                str1 = ss +" , " + ss1;

                sendSMS("01026766250","신고 위치 : " + str);


                Intent intent = new Intent(getApplicationContext(), MainActivity.class); // 다음 넘어갈 클래스 지정
                startActivity(intent); // 다음 화면으로 넘어간다
            }
        });
    }

    //좌표값을 지오코딩을 활용해 주소로 나타내기
    public void getLocation(double lat, double lng)
    {

        Geocoder gocoder = new Geocoder(this, Locale.KOREA);


        List<Address> address;
        try{
           // List<Address> address = gocoder.getFromLocation(FirstActivity.latitude,)
            if(gocoder != null)
            {
                address = gocoder.getFromLocation(lat, lng,1);
                if(address != null && address.size()>0)
                {
                    str = address.get(0).getAddressLine(0).toString();
                }
            }
        }catch (IOException e)
        {
            Log.e("FirstActivity","주소를 찾지 못하였습니다.");
            e.printStackTrace();
        }

    }
    //message 보내는 메소드
    public static void sendSMS(String phoneNumber, String message)
    {
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, null, null);
    }
    public LocationListener mLocationListener = new LocationListener()
    {
        public void onLocationChanged(Location location) {
            //여기서 위치값이 갱신되면 이벤트가 발생한다.
            //값은 Location 형태로 리턴되며 좌표 출력 방법은 다음과 같다.

            Log.d("test", "onLocationChanged, location:" + location);
            FirstActivity.longitude = location.getLongitude(); //경도
            FirstActivity.latitude = location.getLatitude();   //위도
            double altitude = location.getAltitude();   //고도
            float accuracy = location.getAccuracy();    //정확도
            String provider = location.getProvider();   //위치제공자
            //Gps 위치제공자에 의한 위치변화. 오차범위가 좁다.
            //Network 위치제공자에 의한 위치변화
            //Network 위치는 Gps에 비해 정확도가 많이 떨어진다.
            //Toast.makeText(this, "위도  : " + longitude + "\n경도 :" + latitude, Toast.LENGTH_LONG).show();
         //   tv.setText("위치정보 : " + provider + "\n위도 : " + longitude + "\n경도 : " + latitude + "\n고도 : " + altitude + "\n정확도 : "  + accuracy);

            //getLocation(FirstActivity.longitude,FirstActivity.latitude);


        }
        public void onProviderDisabled(String provider) {
            // Disabled시
            Log.d("test", "onProviderDisabled, provider:" + provider);
        }

        public void onProviderEnabled(String provider) {
            // Enabled시
            Log.d("test", "onProviderEnabled, provider:" + provider);
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
            // 변경시
            Log.d("test", "onStatusChanged, provider:" + provider + ", status:" + status + " ,Bundle:" + extras);
        }
    };
} // end of class