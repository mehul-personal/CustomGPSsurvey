package com.analytics.customgpssurvey.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.analytics.customgpssurvey.R;
import com.analytics.customgpssurvey.model.GetDataModel;

import java.util.ArrayList;

/**
 * Created by techiestown on 16/2/17.
 */

public class FormDataListAdapter extends RecyclerView.Adapter<FormDataListAdapter.MyViewHolder> {
    private static final String TAG = "FormDataListAdapter";
    ArrayList<ArrayList<GetDataModel>> ProjectFormList;
    Typeface LibreBoldFont, RobotRegularFont;
    Context mContext;
    private View mSelectedView;
    private int mSelectedPosition;
    private RecyclerView mRecyclerView;

    public FormDataListAdapter(Context mContext, RecyclerView recyclerView, ArrayList<ArrayList<GetDataModel>> ProjectFormList) {
        this.ProjectFormList = ProjectFormList;
        mRecyclerView = recyclerView;
        this.mContext = mContext;
        setHasStableIds(true);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_manage_form_data, parent, false);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!v.isSelected()) {
                    // We are selecting the view clicked
                    if (mSelectedView != null) {
                        // deselect the previously selected view
                        mSelectedView.setSelected(false);
                    }
                    mSelectedPosition = mRecyclerView.getChildAdapterPosition(v);
                    mSelectedView = v;
                } else {
                    // We are deselecting the view clicked
                    mSelectedPosition = -1;
                    mSelectedView = null;
                }

                // toggle the item clicked
                v.setSelected(!v.isSelected());
            }
        });
        LibreBoldFont = Typeface.createFromAsset(parent.getContext().getAssets(), "LibreBaskerville-Bold.otf");
        RobotRegularFont = Typeface.createFromAsset(parent.getContext().getAssets(), "Roboto-Regular.ttf");

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        if (position == mSelectedPosition) {
            holder.itemView.setSelected(true);

            // keep track of the currently selected view when recycled
            mSelectedView = holder.itemView;
        } else {
            holder.itemView.setSelected(false);
        }

        holder.formName.setText("");
        holder.formName.setTypeface(RobotRegularFont);
        SpannableString mainString = new SpannableString("");
       // holder.image1.setVisibility(View.GONE);
        holder.image2.setVisibility(View.GONE);
        holder.image3.setVisibility(View.GONE);
        holder.image4.setVisibility(View.GONE);
        int m=0;
        for (int j = 0; j < ProjectFormList.get(position).size(); j++) {
            if (ProjectFormList.get(position).get(j).getKey().contains("IMAGE")) {
                if (ProjectFormList.get(position).get(j).getValue().length() > 100) {
//                        byte[] imageByte = CommonMethods.decodeToByteArray(ProjectFormList.get(position).get(j).getValue());
//                        Bitmap bitmap = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);
                    //  Log.e("image String",":>"+ProjectFormList.get(position).get(j).getValue());
                    byte[] imageByte = Base64.decode(ProjectFormList.get(position).get(j).getValue(), Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);
                    if (m == 0) {
                        holder.image1.setVisibility(View.VISIBLE);
                        holder.image1.setImageBitmap(bitmap);
                        m++;
                    } else if (m == 1) {
                        holder.image2.setVisibility(View.VISIBLE);
                        holder.image2.setImageBitmap(bitmap);
                        m++;
                    } else if (m == 2) {
                        holder.image3.setVisibility(View.VISIBLE);
                        holder.image3.setImageBitmap(bitmap);
                        m++;
                    } else if (m == 3) {
                        holder.image4.setVisibility(View.VISIBLE);
                        holder.image4.setImageBitmap(bitmap);
                        m++;
                    }

                }
                continue;
            } else {
                SpannableString flag = new SpannableString(ProjectFormList.get(position).get(j).getKey() + " : " + ProjectFormList.get(position).get(j).getValue() + "\n");
                int sposition = ProjectFormList.get(position).get(j).getKey().length() + 3;
                flag.setSpan(new ForegroundColorSpan(Color.parseColor("#ffc107")), 0, sposition, 0);
                flag.setSpan(new ForegroundColorSpan(Color.WHITE), sposition, flag.length(), 0);
                //TextUtils.concat(mainString,flag);
                holder.formName.setText(TextUtils.concat(holder.formName.getText(), flag));
                holder.status_icon.setImageResource(ProjectFormList.get(position).get(j).getStatus().equalsIgnoreCase("DRAFT") ? R.drawable.ic_upload : R.drawable.ic_uploaded);
            }
        }
    }

    @Override
    public int getItemCount() {
        return ProjectFormList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView formName;
        public ImageView image1, image2, image3, image4, status_icon;
        public LinearLayout itemView;

        public MyViewHolder(View view) {
            super(view);
            view.setClickable(true);
            formName = (TextView) view.findViewById(R.id.txvFormName);
            image1 = (ImageView) view.findViewById(R.id.image1);
            image2 = (ImageView) view.findViewById(R.id.image2);
            image3 = (ImageView) view.findViewById(R.id.image3);
            image4 = (ImageView) view.findViewById(R.id.image4);
            status_icon = (ImageView) view.findViewById(R.id.status_icon);
            itemView = (LinearLayout) view.findViewById(R.id.itemView);
        }
    }


}

