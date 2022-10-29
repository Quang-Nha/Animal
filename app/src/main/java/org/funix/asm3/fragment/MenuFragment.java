package org.funix.asm3.fragment;

import static org.funix.asm3.activity.MainActivity.SHARED_FILE;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TableRow;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import org.funix.asm3.R;
import org.funix.asm3.activity.MainActivity;
import org.funix.asm3.adapter.AnimalAdapter;
import org.funix.asm3.interfaces.RvItemClicked;
import org.funix.asm3.model.Animal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * hiển thị danh sách các animal
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class MenuFragment extends Fragment {

    private Context mContext;
    private RecyclerView rvAnimal;
    private List<Animal> listAnimals;
    private DrawerLayout mDrawer;

    TableRow tbrSea;
    TableRow tbrMammal;
    TableRow tbrBird;

    String active;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_menu, container, false);

        initViews(v);

        return v;
    }

    /**
     * khởi tạo các view
     *
     * @param v view gốc của fragment
     */
    private void initViews(View v) {

        mDrawer = v.findViewById(R.id.drawer);

        rvAnimal = v.findViewById(R.id.rv_animals);
        ImageView ivBack = v.findViewById(R.id.iv_back);

        // tạo animation mờ cho RecyclerView rvAnimal
        Animation alpha = AnimationUtils.loadAnimation(mContext, R.anim.alpha);
        rvAnimal.startAnimation(alpha);

        // không hiển thị nút back
        ivBack.setVisibility(View.GONE);

        // nếu đang là màn hình dọc mDrawer != null thì mới xử lý sự kiện
        if (mDrawer != null) {
            //Xử lý mở menu trái khi click iv_menu
            v.findViewById(R.id.iv_menu).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDrawer.openDrawer(GravityCompat.START);
                }
            });
        }

        tbrSea = v.findViewById(R.id.tbr_sea);
        tbrMammal = v.findViewById(R.id.tbr_mammal);
        tbrBird = v.findViewById(R.id.tbr_bird);

        //Hiển thị list ảnh động vật biển khi click vào tbr_sea
        tbrSea.setOnClickListener(v1 -> {

            // nếu đang là màn hình dọc mDrawer != null thì cả mDrawer chạy animation abc_fade_in
            if (mDrawer != null) {
                v.startAnimation(AnimationUtils.loadAnimation(mContext, androidx.appcompat.R.anim.abc_fade_in));
            }

            // ImageView trong tbrSea chạy blink_infinity
            tbrSea.findViewById(R.id.iv_sea).startAnimation(AnimationUtils.loadAnimation(mContext
                    , R.anim.blink_infinity));

            // lưu tên ImageView chạy animation và biến nhớ
            active = "seas";

            // xoá animation của 2 item còn lại
            tbrMammal.findViewById(R.id.iv_mammal).clearAnimation();
            tbrBird.findViewById(R.id.iv_bird).clearAnimation();

            showAnimals("seas");

        });

        //Hiển thị list ảnh động vật có vú
        tbrMammal.setOnClickListener(v1 -> {

            // tương tự tbrSea
            if (mDrawer != null) {
                v.startAnimation(AnimationUtils.loadAnimation(mContext, androidx.appcompat.R.anim.abc_fade_in));
            }

            tbrMammal.findViewById(R.id.iv_mammal).startAnimation(AnimationUtils.loadAnimation(mContext
                    , R.anim.blink_infinity));

            active = "mammals";

            tbrSea.findViewById(R.id.iv_sea).clearAnimation();
            tbrBird.findViewById(R.id.iv_bird).clearAnimation();

            showAnimals("mammals");
        });


        //Hiển thị list ảnh chim muông
        tbrBird.setOnClickListener(v1 -> {

            // tương tự tbrSea
            if (mDrawer != null) {
                v.startAnimation(AnimationUtils.loadAnimation(mContext, androidx.appcompat.R.anim.abc_fade_in));
            }

            tbrBird.findViewById(R.id.iv_bird).startAnimation(AnimationUtils.loadAnimation(mContext
                    , R.anim.blink_infinity));

            active = "birds";

            tbrSea.findViewById(R.id.iv_sea).clearAnimation();
            tbrMammal.findViewById(R.id.iv_mammal).clearAnimation();

            showAnimals("birds");
        });
    }

    /**
     * cài đặt dữ liệu cho rvAnimal và hiển thị các animals
     *
     * @param animalType tên chủng loại animals
     */
    private void showAnimals(String animalType) {

        listAnimals = new ArrayList<>();

        try {

            AssetManager am = mContext.getAssets();

            // lấy danh sách tên các ảnh icon của loại animal tương ứng
            String[] listPhotoIC = am.list("ic_animal/" + animalType);

            // lấy danh sách tên các ảnh nền của loại animal tương ứng
            String[] listPhotoBg = am.list("bg_animal/" + animalType);

            Bitmap photoIC;
            Bitmap photoBG;
            String name;
            StringBuilder content;

            // duyệt qua từng tên các ảnh icon animal
            for (String photo : listPhotoIC) {

                // lấy đường dẫn đến ảnh icon
                String ic_animalPath = "ic_animal/" + animalType + "/" + photo;

                // set bitmap cho ảnh icon
                photoIC = BitmapFactory.decodeStream(am.open(ic_animalPath));

                // lấy tên animal sau khi bỏ các phần ko liên quan của tên ảnh
                name = photo;
                name = name.replace("ic_", "");
                name = name.substring(0, name.indexOf("."));

                // lấy đường dẫn thông tin mô tả chi tiết của animal theo /des_ name + .txt là tên file
                String descriptionPath = "description/" + animalType + "/des_" + name + ".txt";

                content = new StringBuilder();
                String line;

                // tạo luồng đọc file mô tả theo path trên
                InputStream inputStream = am.open(descriptionPath);
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                // đọc nội dung và thêm vào content
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }

                //Lấy thông tin động vật yêu thích đã lưu trữ trong file_savef của SharedPreference
                boolean isLove = mContext.getSharedPreferences(SHARED_FILE, Context.MODE_PRIVATE)
                        .getBoolean(ic_animalPath, false);

                // chỉnh tên cho viết hoa chữ cái đầu và bỏ "_"
                name = name.substring(0, 1).toUpperCase(Locale.ROOT) + name.substring(1)
                        .replace("_", " ");

                //Khởi tạo động vật với ảnh nền chưa tạo
                Animal animal = new Animal(photoIC, null, ic_animalPath, name.trim(), content.toString(), isLove);

                //Cho vào danh sách
                listAnimals.add(animal);

            }

            // duyệt qua từng tên các ảnh nền animal
            for (String photo : listPhotoBg) {

                // set bitmap cho ảnh nền
                photoBG = BitmapFactory.decodeStream(am.open("bg_animal/" + animalType + "/" + photo.replace("ic_", "bg_")));

                // lấy tên animal theo tên ảnh nền
                String nameBG = photo.replace("bg_", "").replace("_", " ");
                nameBG = nameBG.substring(0, nameBG.indexOf(".")).trim();

                // duyệt mảng tìm animal có tên giống với tên vừa nhận và set ảnh nền ở trên cho nó
                for (Animal animal : listAnimals) {
                    if (animal.getName().equalsIgnoreCase(nameBG)) {
                        animal.setPhotoBG(photoBG);
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        // gọi hàm hiển thị danh sách animal từ list vừa tạo
        setRvAnimal();
    }

    /**
     * set adapter cho RecyclerView để hiển thị
     */
    public void setRvAnimal() {

        // khi đã xác định được list animal cần hiển thị thì gán lại cho list trong MainActivity
        // để sử dụng hiển thị lại khi thay đổi hướng màn hình
        ((MainActivity) requireContext()).setAnimalList(listAnimals);

        // khởi tạo adapter cho rvAnimal truyền list và interface RvItemClicked bằng lớp ẩn danh
        AnimalAdapter animalAdapter = new AnimalAdapter(listAnimals, new RvItemClicked() {
            @Override
            public void onRvItemClicked(int position) {

                 /* gọi hàm showDetail từ MainActivity/mContext để chuyển sang fragment detail
                  truyền list của frag này và position
                 từ đối số của hàm sẽ được truyền vào từ lớp sử dụng hàm này là animalAdapter */
                ((MainActivity) mContext).showDetail(listAnimals, position);
            }
        });

        //Hiển thị danh sách ảnh lên RecyclerView
        rvAnimal.setAdapter(animalAdapter);
        rvAnimal.setHasFixedSize(true);

        // nếu đang là màn hình dọc mDrawer != null thì mới đóng menu trái
        if (mDrawer != null) {
            mDrawer.closeDrawers();
        }
    }

    /**
     * tránh trang rỗng khi back lại fragment này từ detail fragment hoặc thay đổi hướng màn hình
     * sẽ load lại list animal theo list animals đã lưu từ MainActivity
     */
    @Override
    public void onResume() {
        super.onResume();

        // lấy lại list animal từ MainActivity và hiển thị lại trên màn hình
        listAnimals = ((MainActivity) requireContext()).getAnimalList();

        if (listAnimals != null) {
            setRvAnimal();
        }

        // lấy lại biến nhớ active từ MainActivity
        active = ((MainActivity) requireContext()).getActive();

        // cài lại ImageView đã chạy animation trước đó theo biến nhớ
        if (active != null) {
            switch (active) {
                case "seas":
                    tbrSea.findViewById(R.id.iv_sea).startAnimation(AnimationUtils
                            .loadAnimation(mContext, R.anim.blink_infinity));

                    break;
                case "mammals":
                    tbrMammal.findViewById(R.id.iv_mammal).startAnimation(AnimationUtils
                            .loadAnimation(mContext, R.anim.blink_infinity));

                    break;
                case "birds":
                    tbrBird.findViewById(R.id.iv_bird).startAnimation(AnimationUtils
                            .loadAnimation(mContext, R.anim.blink_infinity));

                    break;
            }
        }
    }

    @Override
    public void onPause() {

        // gửi biến nhớ ImageView nào chạy animation cho MainActivity
        ((MainActivity) requireContext()).setActive(active);
        super.onPause();
    }

    /**
     * lấy biến môi trường, activity chứa fragment này
     *
     * @param context activity chứa
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

}