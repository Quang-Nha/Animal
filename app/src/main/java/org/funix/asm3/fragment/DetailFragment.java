package org.funix.asm3.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import org.funix.asm3.R;
import org.funix.asm3.activity.MainActivity;
import org.funix.asm3.adapter.DetailPagerAdapter;
import org.funix.asm3.model.Animal;

import java.util.List;

/**
 * hiển thị chi tiết thông tin 1 animal
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class DetailFragment extends Fragment {

    private List<Animal> animals;
    private int position;

    private ViewPager viewPager;

    public void setData(List<Animal> animals, int position) {
        this.animals = animals;
        this.position = position;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_detail, container, false);
        initViews(v);

        return v;
    }

    /**
     * khởi tạo các view
     *
     * @param v layout gốc
     */
    private void initViews(View v) {
        viewPager = v.findViewById(R.id.vp_detail);

        // bắt sự kiện nhấn nút back xóa fragment khỏi BackStack để trở lại fragment trước
        v.findViewById(R.id.iv_back).setOnClickListener(view -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        // ẩn nút menu của layout action_bar
        v.findViewById(R.id.iv_menu).setVisibility(View.GONE);

        // lấy adapter và truyền list animals rồi gán cho ViewPager
        DetailPagerAdapter adapter = new DetailPagerAdapter(animals);

        viewPager.setAdapter(adapter);

        // set vị trí trang mà ViewPager cần hiển thị
        viewPager.setCurrentItem(position);

    }

    /**
     * lấy vị trí trang đang xem
     */
    public int getPosition() {
        return viewPager.getCurrentItem();
    }

    /**
     * khi thoát khỏi fragment thì set vị trí hiển thị trong list về -1
     * để khi xoay màn hình không hiển thị lại animal này
     */
    @Override
    public void onDetach() {
        ((MainActivity)requireContext()).setPosition(-1);
        super.onDetach();
    }
}