package com.example.linkedin;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
public class SkillsAdapter extends RecyclerView.Adapter<SkillsAdapter.SkillsViewHolder>  {

    private List<String> skillsList;

    public SkillsAdapter(List<String> skillsList) {
        this.skillsList = skillsList;
    }

    @NonNull
    @Override
    public SkillsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_skill, parent, false);
        return new SkillsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SkillsViewHolder holder, int position) {
        String skill = skillsList.get(position);
        holder.bind(skill);
    }

    @Override
    public int getItemCount() {
        return skillsList.size();
    }

    public void setSkills(List<String> skillsList) {
        this.skillsList = skillsList;
        notifyDataSetChanged();
    }

    public static class SkillsViewHolder extends RecyclerView.ViewHolder {

        private final TextView skillTextView;

        public SkillsViewHolder(@NonNull View itemView) {
            super(itemView);
            skillTextView = itemView.findViewById(R.id.skillTextView);
        }

        public void bind(String skill) {
            skillTextView.setText(skill);
        }
    }
}
