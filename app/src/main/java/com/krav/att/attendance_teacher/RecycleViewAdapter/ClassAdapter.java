package com.krav.att.attendance_teacher.RecycleViewAdapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.krav.att.attendance_teacher.Parcelable.People;
import com.krav.att.attendance_teacher.R;

import java.util.List;

/**
 * Created by 01547598 on 8/4/2017.
 */

public class ClassAdapter extends RecyclerView.Adapter<ClassAdapter.PeopleViewHolder> {

    private List<People> peopletList;

    public ClassAdapter(List<People> peopletList) {
        this.peopletList = peopletList;
    }

    @Override
    public int getItemCount() {
        return peopletList != null ? peopletList.size() : 0;
    }

    @Override
    public void onBindViewHolder(PeopleViewHolder peopleViewHolder, int i) {
        int j = i*2;
        People ci = peopletList.get(j);
        peopleViewHolder.vC1Name.setText(ci.getName());
        j = j+1;
        if (peopletList.size() < j) {
            ci = peopletList.get(j);
            peopleViewHolder.vC2Name.setText(ci.getName());
        } else {
            peopleViewHolder.vCard_view2.setVisibility(View.GONE);
        }
    }

    @Override
    public PeopleViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.class_adapter_list_layout, viewGroup, false);

        return new PeopleViewHolder(itemView);
    }

    public static class PeopleViewHolder extends RecyclerView.ViewHolder {
        protected CardView vCard_view1;
        protected TextView vC1Name;
        protected CardView vCard_view2;
        protected TextView vC2Name;


        public PeopleViewHolder(View v) {
            super(v);
            vCard_view1 = (CardView) v.findViewById(R.id.card_view1);
            vC1Name = (TextView) v.findViewById(R.id.c1_name);
            vCard_view2 = (CardView) v.findViewById(R.id.card_view2);
            vC2Name = (TextView) v.findViewById(R.id.c2_name);
        }
    }
}