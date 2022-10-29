package org.funix.asm3.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.funix.asm3.R;
import org.funix.asm3.interfaces.RvItemClicked;
import org.funix.asm3.model.Animal;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * adapter của {@link RecyclerView}
 */
public class AnimalAdapter extends RecyclerView.Adapter<AnimalAdapter.ViewHolder> {
    List<Animal> animalList;
    RvItemClicked rvItemClicked;

    public AnimalAdapter(List<Animal> listAnimals, RvItemClicked rvItemClicked) {
        this.animalList = listAnimals;
        this.rvItemClicked = rvItemClicked;
    }

    /**
     * ViewHolder extends RecyclerView.ViewHolder chứa layout tự tạo
     */
    protected static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivIcAnimal;
        private final ImageView ivLike;
        private final TextView tvNameAnimal;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIcAnimal = itemView.findViewById(R.id.iv_ic_animal);
            ivLike = itemView.findViewById(R.id.iv_favorite);
            tvNameAnimal = itemView.findViewById(R.id.tv_name);
        }
    }


    /**
     * khởi tạo AnimalViewHolder, gán cho layout item_animal
     *
     * @return AnimalViewHolder
     */
    @NonNull
    @Override
    public AnimalAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_animal, parent, false);
        return new ViewHolder(v);
    }

    /**
     * liên kết các view của AnimalViewHolder với Animal trong list ở position tương ứng
     *
     * @param holder   AnimalViewHolder
     * @param position vị trí Animal trong list
     */
    @Override
    public void onBindViewHolder(@NonNull AnimalAdapter.ViewHolder holder, int position) {
        Animal animal = animalList.get(position);

        holder.tvNameAnimal.setText(animal.getName());
        holder.ivIcAnimal.setImageBitmap(animal.getPhoto());

        // ẩn, hiện icon favorite theo thuộc tính isFav của animal
        if (animal.isFav()) {
            holder.ivLike.setVisibility(View.VISIBLE);
        } else {
            holder.ivLike.setVisibility(View.GONE);
        }

        /* khi click vào holder gọi onRvItemClicked của interface rvItemClicked đã được truyền bằng
        lớp ẩn danh và truyền vào position đang xét */
        holder.itemView.setOnClickListener(view -> {

            // set animation cho view được click
            Animation blink = AnimationUtils.loadAnimation(view.getContext(), R.anim.blink);
            view.startAnimation(blink);

            // tạo luồng trễ chuyển sang màn hình fragment detail
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    rvItemClicked.onRvItemClicked(position);
                }
            };

            Timer timer = new Timer();

                /* chạy timerTask sau 0.2s để xem rõ animation của view được click
                 rồi mới chạy luồng chuyển màn hình */
            timer.schedule(timerTask, 200L);

        });
    }

    @Override
    public int getItemCount() {
        if (animalList != null) {
            return animalList.size();
        }
        return 0;
    }
}
