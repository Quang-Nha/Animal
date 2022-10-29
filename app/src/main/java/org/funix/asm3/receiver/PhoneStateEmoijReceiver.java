package org.funix.asm3.receiver;

import static org.funix.asm3.activity.MainActivity.SHARED_FILE;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.ImageView;
import android.widget.Toast;

import org.funix.asm3.activity.MainActivity;

import java.io.IOException;

public class PhoneStateEmoijReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        // lắng nghe khi trạng thái cuộc gọi thay đổi
        telephonyManager.listen(new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String phoneNumber) {
                super.onCallStateChanged(state, phoneNumber);

                /* ở android 10 trở lên custom toast chỉ hoạt động khi đang ở ứng dụng của mình
                 nếu chuyển sang ứng dụng gọi điện khi chưa kịp quay lại ứng dụng cũ
                 thì dù đã đăng kí lắng nghe sự kiện phone và gọi lệnh hiển thị toast
                 nó cũng sẽ không hiển thị

                 cần để delay 1s để mà hình kịp thời quay lại ứng dụng của nó
                 cụ thể là trường hợp ngắt cuộc gọi sẽ quay lại ứng dụng ban đầu của nó
                 nên bắt sự kiện lắng nghe ngắt cuộc gọi thì sẽ gọi hàm hiển thị toast sau 1s */
                if (state == TelephonyManager.CALL_STATE_IDLE) {

                    Handler handler = new Handler();
                    // delay sau 1s thì cho phép toast hiển thị tiếp
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showEmoij(context, state, phoneNumber);
                        }
                    }, 1000);

                }
                // trường hợp còn lại là các trạng thái khác của phone gọi hàm hiển thị luôn
                else {
                    showEmoij(context, state, phoneNumber);
                }

            }
        }, PhoneStateListener.LISTEN_CALL_STATE);

    }

    /**
     * hiển thị toast với trạng thái cuộc gọi tương ứng và hiển thị hình ảnh nếu có
     */
    private void showEmoij(Context context, int state, String phoneNumber) {

        // tạo ImageView
        ImageView ivEmoij = new ImageView(context);
        ivEmoij.setMinimumWidth(600);
        ivEmoij.setMinimumHeight(600);

        AssetManager manager = context.getAssets();

        // nếu là trạng thái hủy cuộc gọi thì set ảnh tương ứng cho ImageView
        if (state == TelephonyManager.CALL_STATE_IDLE) {

            Bitmap icon = null;
            try {
                icon = BitmapFactory.decodeStream(manager.open("ic_offhook.png"));
                ivEmoij.setImageBitmap(icon);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // nếu là trạng thái bắt đầu đổ chuông
        else if (state == TelephonyManager.CALL_STATE_RINGING) {

            SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_FILE, Context.MODE_PRIVATE);

            // do android 12 thêm dấu + vào sđt nên cần loại bỏ trước
            if (phoneNumber.contains("+")) {
                phoneNumber = phoneNumber.replace("+", "");
            }

            // lấy đường dẫn của ảnh với key là phone, nếu ko có key này thì trả về null
            String iconPath = sharedPreferences.getString(phoneNumber, null);

            // nếu path ko null thì lấy ảnh từ path và set cho ImageView
            if (iconPath != null) {
                try {
                    Bitmap icon = BitmapFactory.decodeStream(manager.open(iconPath));
                    ivEmoij.setImageBitmap(icon);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            // path null thì cho ImageView null
            else {
                ivEmoij = null;
            }

        }
        // không nằm trong 2 trường hợp trên thì cũng cho ảnh null
        else {
            ivEmoij = null;
        }

        // chỉ hiện thị toast khi ImageView ko null
        if (ivEmoij != null) {

            // do đối tượng bắt sự kiện này bị gọi liên tục nhiều lần khi 1 sự kiện xảy ra nên hàm bị gọi nhiều lần
            // chỉ khi trạng thái của cuộc gọi thay đổi khác với trạng thái của lần gọi trước thì mới gọi
            // thực hiện show toast
            // vd: nếu bị gọi nhiều lần khi sự kiện CALL_STATE_RINGING thì đối tượng cũng bị gọi nhiều lần
            // và code ở khu vực này cũng bị gọi nhiều lần, nhưng trạng thái cuộc gọi vẫn giống trạng thái lần trước
            // lưu trong MainActivity.phoneState thì sẽ ko gọi đoạn code show toast
            // nếu trạng thái khác thì mới show toast
            if (MainActivity.phoneState != state) {
                // hiển thị toast với ImageView trên
                Toast toast = new Toast(context.getApplicationContext());
                toast.setView(ivEmoij);
                toast.setDuration(Toast.LENGTH_LONG);
                toast.show();

                // lưu lại trạng thái cuộc gọi này để so sánh với lần tiếp theo
                MainActivity.phoneState = state;
            }

        }

    }
}