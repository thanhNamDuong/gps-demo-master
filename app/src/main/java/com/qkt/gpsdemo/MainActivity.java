package com.qkt.gpsdemo;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    /*
     * Cái này giống như mã lệnh để phân biệt các yêu cầu.
     * M không cần quan tâm lắm, dùng giống là ok rồi
     */
    private final int REQUEST_PERMISSION_CODE = 2;
    /*
     * Thời gian cập nhật lại vị trí (mili giây)
     */
    private final int DELAY = 5000;
    /*
     * Dùng để hiển thị tọa độ GPS
     */
    private TextView mTvGps;
    /*
     * Dùng để quán lý tọa độ
     */
    LocationManager locationManager;
    /*
     * Biến này để biết đang start hay stop
     */
    /*
     * Biến này để nghe các sự kiện
     */
    LocationListener locationListener;
    private boolean isStart = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*
         * Tìm cái textView có id là tv_gps trong activity_main.xml để gán cho nó.
         */
        mTvGps = (TextView) findViewById(R.id.tv_gps);

        /*
         * Tìm button start.
         */
        Button start = (Button) findViewById(R.id.btn_start);
        /*
         * Khi click start thì sẽ gọi hàm startGetGps
         */
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGetGps();
            }
        });

        /*
         * Giống cái nut start
         */
        Button stop = (Button) findViewById(R.id.btn_stop);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stop();
            }
        });

        /*
         * Nếu đã có quyền thì khởi tạo các biến để lấy vị trí.
         * Còn chưa có thì yêu cầu cấp quyền.
         */
        if (!checkPermission()) {
            requestPermission();
        } else {
            /*
             * Khai bao các biến để lấy location
             */
            // Acquire a reference to the system Location Manager
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    /*
                     * Hàm này được gọi khi vị trí thay đổi
                     */
                    displayLocation(location);
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            };
        }
    }

    /**
     * Hàm này được gọi nếu lệnh requestPermission() được gọi và người dùng đã cho phép.
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
         /*
             * Khai bao các biến để lấy location
             */
        // Acquire a reference to the system Location Manager
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
    }

    /**
     * Hàm này dùng để kiểm tra xem điện thoại có cho phép dùng GPs hay không?
     *
     * @return
     */
    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            int per = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
            if (per != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * Hàm này sẽ yêu cầu cấp quyền Gps cho ứng dụng.
     * Nó được gọi khi hàm checkPermission trả về false
     */
    private void requestPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_PERMISSION_CODE);
    }

    /**
     * Hàm này dùng để hiển thị vị trí
     *
     * @param location
     */
    private void displayLocation(Location location) {
        if (location != null) {
            String loc = "(" + location.getAltitude() + "," + location.getLatitude() + ")";
            Log.i("Update", "new location " + loc);
            mTvGps.setText(mTvGps.getText().toString() + "\n" + loc);
        }
    }

    /**
     * Sau khi khi báo, gọi hàm này sẽ bắt đầu lấy vị trí.
     * Hàm này được gọi khi nhấn nut start
     */
    private void startGetGps() {
        /*
         * Kiểm tra xem có đang chạy hay k?
         * Đang chạy thì không gọi nữa.
         */
        if (isStart) {
            return;
        }

        String gpsProvider = LocationManager.NETWORK_PROVIDER;
        isStart = true;
        if(checkPermission()) {
            /*
             * Bắt đầu nghe sự thay đổi tọa  độ
             */
            locationManager.requestLocationUpdates(gpsProvider, 0, 0, locationListener);
        }
    }

    /**
     * Hafm nafy được gọi khi nhán Stop
     */
    private void stop() {
        isStart = false;
        if(locationListener != null && locationManager != null){
            locationManager.removeUpdates(locationListener);
        }
    }

}
