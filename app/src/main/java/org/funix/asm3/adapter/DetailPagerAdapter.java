package org.funix.asm3.adapter;

import static org.funix.asm3.activity.MainActivity.SHARED_FILE;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.tv.TvView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import org.funix.asm3.R;
import org.funix.asm3.activity.MainActivity;
import org.funix.asm3.model.Animal;

import java.util.List;

public class DetailPagerAdapter extends PagerAdapter {
    List<Animal> animalList;

    public DetailPagerAdapter(List<Animal> animals) {
    this.animalList = animals;
    }

    /**
     * ánh xạ 1 item view từ thư mục layout vào trong chương trình
     * Sau đó đổ dữ liệu ứng với data tương ứng vào
     *
     * @param container view chứa các item view
     * @param position  vị trí trang tương ứng với phần tử trong list
     * @return item view cần lấy
     */
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        //Ánh xạ item view vào trong môi trường code
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.item_detail, container, false);

        SharedPreferences sharedPreferences = container.getContext().getSharedPreferences(SHARED_FILE, Context.MODE_PRIVATE);

        // lấy animal trong list với position tương ứng
        Animal animal = animalList.get(position);

        //Ánh xạ các view ra từ itemView dựa vào id
        ImageView ivBg = view.findViewById(R.id.iv_bg);
        ImageView ivLike = view.findViewById(R.id.iv_detail_favorite);
        ImageView ivCall = view.findViewById(R.id.iv_call);
        TextView tvDetailName = view.findViewById(R.id.tv_detail_name);
        TextView tvDetail = view.findViewById(R.id.tv_detail);
        TextView tvPhone = view.findViewById(R.id.tv_phone);

        // set các view của itemView dựa vào animal
        ivBg.setImageBitmap(animal.getPhotoBG());
        tvDetail.setText(animal.getContent());
        tvDetailName.setText(animal.getName());

        // set phone cho tvPhone lấy từ bộ nhớ với key là path của animal + "_phone"
        tvPhone.setText(sharedPreferences.getString(animal.getPath() + "_phone", ""));

        // set icon tương ứng với trạng thái thích
        if (animal.isFav()) {
            ivLike.setImageResource(R.drawable.ic_favorite);
        } else {
            ivLike.setImageResource(R.drawable.ic_not_favorite);
        }

        // xử lý sự kiện click vào hình trạng thái like
        ivLike.setOnClickListener(view1 -> {
            changeLike(ivLike, animal, sharedPreferences);
        });

        // bắt sự kiện nhấn nút điện thoại ivCall
        ivCall.setOnClickListener(v -> {
            setPhoneNumber(tvPhone, sharedPreferences, animal);
        });

        // thêm layout item vào container để hiển thị
        container.addView(view);
        return view;
    }

    /**
     * hiển thị dialog cài đặt số điện thoại
     *
     * @param animal  động vật đang xem
     * @param tvPhone cần set text từ kết quả nhập vào dialog
     */
    private void setPhoneNumber(TextView tvPhone, SharedPreferences sharedPreferences
            , Animal animal) {

        AlertDialog.Builder builder = new AlertDialog.Builder(tvPhone.getContext());

        // cài đặt dialog, lấy layout liên kết, cho phép hủy khi click ra ngoài và hiển thị
        AlertDialog alertDialog = builder.setView(R.layout.dialog_input_phone)
                .setCancelable(true)
                .show();

        // lấy các view từ layout liên kết
        ImageView icAnimal = alertDialog.findViewById(R.id.iv_ic);
        EditText etPhone = alertDialog.findViewById(R.id.et_phone);
        Button btnSave = alertDialog.findViewById(R.id.btn_save);
        Button btnDelete = alertDialog.findViewById(R.id.btn_delete);

        // set ảnh là icon của animal đang xem
        icAnimal.setImageBitmap(animal.getPhoto());

        // lấy text của tvPhone gán cho etPhone
        etPhone.setText(tvPhone.getText());

        // khi ấn nút lưu set text cho tvPhone là sđt etPhone đã nhập đồng thời thoát dialog và ẩn bàn phím
        // cập nhật giá trị vào bộ nhớ
        btnSave.setOnClickListener(v -> {
            // lấy sdt đã nhập ở etPhone
            String phone = etPhone.getText().toString().trim();

            if (!phone.isEmpty()) {
                // xem sdt đã tồn tại chưa bằng cách dùng nó làm key lấy dữ liệu trong bộ nhớ
                String path = sharedPreferences.getString(phone, null);

                // nếu nó đã tồn tại thì thông báo nhập lại và thoát
                if (path != null) {
                    Toast.makeText(tvPhone.getContext(), "Phone number already exists, please enter another number!"
                            , Toast.LENGTH_LONG).show();
                    return;
                }

                // set phone đã nhập cho tvPhone
                tvPhone.setText(phone);
                hideSoftKeyboard(tvPhone.getContext());

                // put key là path của animal + "_phone", value là phone đã nhập
                // put key là phone đã nhập, value là path của animal
                sharedPreferences.edit().putString(animal.getPath() + "_phone", phone).apply();
                sharedPreferences.edit().putString(phone, animal.getPath()).apply();

            }
            alertDialog.dismiss();

        });

        // khi ấn nút xóa clear tvPhone đồng thời thoát dialog và ẩn bàn phím
        // cập nhật giá trị vào bộ nhớ
        btnDelete.setOnClickListener(v -> {
            // nếu animal có số điện thoại
            if (!tvPhone.getText().toString().trim().isEmpty()) {
                // lấy phone chuẩn bị xóa ghi ở tvPhone vào biến trung gian
                String phone = tvPhone.getText().toString();

                // xóa phone hiển thị trên tvPhone
                tvPhone.setText("");
                hideSoftKeyboard(tvPhone.getContext());

                // xóa key là path của animal + "_phone" và
                // xóa key là sđt vừa hủy khỏi bộ nhớ
                sharedPreferences.edit().remove(animal.getPath() + "_phone").apply();
                sharedPreferences.edit().remove(phone).apply();
            }

            // đóng dialog
            alertDialog.dismiss();

        });
    }

    /**
     * ẩn bàn phím
     *
     * @param context để gọi các hàm
     */
    private void hideSoftKeyboard(Context context) {

        try {
            InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(
                    Activity.INPUT_METHOD_SERVICE);

            inputMethodManager.hideSoftInputFromWindow(((MainActivity) context).getCurrentFocus()
                    .getWindowToken(), 0);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**
     * nếu đang thích thì chuyển sang không thích và ngược lại,
     * set ảnh và thuộc tính like của animal lại tương ứng
     * sửa lại dữ liệu trong sharedPreferences với key là path ảnh icon của animal
     * @param ivLike
     * @param animal
     * @param sharedPreferences
     */
    private void changeLike(ImageView ivLike, Animal animal, SharedPreferences sharedPreferences) {
        if (animal.isFav()) {
            ivLike.setImageResource(R.drawable.ic_not_favorite);
            animal.setIsFav(false);
            sharedPreferences.edit().remove(animal.getPath()).apply();
        } else {
            ivLike.setImageResource(R.drawable.ic_favorite);
            animal.setIsFav(true);
            sharedPreferences.edit().putBoolean(animal.getPath(), true).apply();
        }
    }


    /**
     * khai báo số lượng item view (page) sẽ được sinh ra
     */
    @Override
    public int getCount() {
        if (animalList != null) {
            return animalList.size();
        }
        return 0;
    }

    /**
     * khi vuốt sang trái-phải để hiển thị page mới
     * Nếu vuốt chưa được 1 nửa và nhả tay ra, Page cũ sẽ được giữ lại hiển thị trên màn hình
     */
    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    /**
     * Khi 1 Page không còn được hiển thị trên màn hình, nó sẽ bị container destroy
     */
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
