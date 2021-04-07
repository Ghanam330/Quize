package com.example.quaiz;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BookMarkAdapter extends RecyclerView.Adapter<BookMarkAdapter.Viewholder> {
    private final List<QuestionModel> list;

    public BookMarkAdapter(List<QuestionModel> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bookmark_item, parent, false);

        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
        holder.setDate(list.get(position).getQuestion(), list.get(position).getCorrectANS(), position);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class Viewholder extends RecyclerView.ViewHolder {
        private final TextView quaestion;
        private final TextView answer;
        private final ImageButton deleteBtn;

        public Viewholder(@NonNull View itemView) {
            super(itemView);

            quaestion = itemView.findViewById(R.id.quastion);
            answer = itemView.findViewById(R.id.answer);
            deleteBtn = itemView.findViewById(R.id.delete_btn);
        }


        private void setDate(String quastion, String answer, final int position) {
            this.quaestion.setText(quastion);
            this.answer.setText(answer);
            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    list.remove(position);
                    notifyItemRemoved(position);
                }
            });
        }

    }
}
