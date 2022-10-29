package org.funix.asm3.activity;

import android.Manifest;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.funix.asm3.R;
import org.funix.asm3.fragment.DetailFragment;
import org.funix.asm3.fragment.MenuFragment;
import org.funix.asm3.model.Animal;
import org.funix.asm3.receiver.PhoneStateEmoijReceiver;

import java.util.ArrayList;
import java.util.List;

/**
 * activity chứa các fragment
 */
public class MainActivity extends AppCompatActivity {

    public static final String SHARED_FILE = "file_savef";
    private static final String KEY_ACTIVE = "ACTIVE";
    public static int phoneState = -1;
    private final String KEY_ANIMAL_LIST = "animalList";
    private final String KEY_POSITION = "position";
    PhoneStateEmoijReceiver receiver;

    private List<Animal> animalList;
    private int position = -1;
    private String active;

    private DetailFragment detailFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        // đặng kí nhận thông tin khi có cuộc gọi
        receiver = new PhoneStateEmoijReceiver();
        IntentFilter filter = new IntentFilter("android.intent.action.PHONE_STATE");
        registerReceiver(receiver, filter);

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    /**
     * khởi tạo và hiển thị MenuFragment
     */
    private void initViews() {

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.ln_main, new MenuFragment(), null)
                .commit();

        checkPermissions();
    }

    /**
     * kiểm tra và yêu cầu cấp quyền
     */
    private void checkPermissions() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {

                int PERMISSION_REQUEST_CODE = 101;
                requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_CALL_LOG},
                        PERMISSION_REQUEST_CODE);
            }
        }
    }

    /**
     * xem kết quả trả về nếu chỉ cần 1 quyền chưa được cấp sẽ thông báo và thoát chương trình
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 101) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED ||
                    grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, R.string.please_allow_this_permission, Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    /**
     * chuyển sang DetailFragment và gán lại vị trí đã click trong list để sử dụng lại khi đổi
     * hướng màn hình
     *
     * @param listAnimals list các động vật cấp cho adapter của viewPager
     * @param position    vị trí trang viewPager cần hiển thị animal
     */
    public void showDetail(List<Animal> listAnimals, int position) {

        this.position = position;

        // khởi tạo DetailFragment và truyền các đối số cần thiết lấy từ tham số hàm này
        detailFragment = new DetailFragment();
        detailFragment.setData(listAnimals, position);

        // thay fragment cũ bằng detailFragment và thêm phiên vào backStack
        getSupportFragmentManager().beginTransaction().replace(R.id.ln_main, detailFragment, null)
                .addToBackStack(null).commit();

    }

    /**
     * bắt sự kiện khi bắt đầu đổi hướng màn hình
     *
     * @param outState truyền dữ liệu cho mà hình sau khi đã thay đổi
     */
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {

        /* nếu detailFragment != null && position != -1 tức là đang xem màn hình chi tiết set lại
         vị trí position bằng cách gọi hàm lấy vị trí trang đang xem getPosition() của đối tượng
         detailFragment đã khởi tạo trước đó để xem chi tiết ở hàm showDetail()
            do khi thoát khỏi màn hình chi tiết detailFragment ko null và set lại position = -1 */
        if (detailFragment != null && position != -1) {
            position = detailFragment.getPosition();
        }

        /* truyền list animal và truyền vị trí hiển thị trong list nếu nó đang ở màn hình detail
        sẽ được gán lại thì vị trí sẽ khác -1 */
        outState.putParcelableArrayList(KEY_ANIMAL_LIST, (ArrayList<Animal>) animalList);
        outState.putInt(KEY_POSITION, position);

        outState.putString(KEY_ACTIVE, active);

        super.onSaveInstanceState(outState);

    }

    /**
     * bắt sự kiện khi hướng màn hình đã thay đổi xong
     *
     * @param savedInstanceState nhận dữ liệu từ mà hình trước khi thay đổi
     */
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // nhận lại các list animal và vị trí hiển thị trong list
        animalList = savedInstanceState.getParcelableArrayList(KEY_ANIMAL_LIST);
        position = savedInstanceState.getInt(KEY_POSITION);

        active = savedInstanceState.getString(KEY_ACTIVE);

        /* nếu vị trí khác -1 tức là nó đang ở màn hình chi tiết và list chắc chắn ko null
         vì đã hiển thị list lên màn hình và đã click vào 1 animal để sang màn hình chi tiết
         gọi hàm hiển thị fragment detail và truyền list với vị trí vào */
        if (position != -1) {
            showDetail(animalList, position);
        }
    }

    /**
     * hủy đăng kí receiver
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    public void setAnimalList(List<Animal> animalList) {
        this.animalList = animalList;
    }

    /**
     * trả list này lấy từ onRestoreInstanceState cho menuFragment sau khi xoay màn hình để nó tự hiển thị lại danh sách
     */
    public List<Animal> getAnimalList() {
        return animalList;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

}