package jp.techacademy.kanta.nakayama.taskapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by nhk2204 on 2016/10/02.
 */
public class CategoryAdapter extends BaseAdapter{
    private LayoutInflater mLayoutInflater;
    private ArrayList<Category> mCategoryArrayList;

    //　　public void CategoryAdapter(Context context){
    //  　　mLayoutInflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    //　　}
    //　　　↓
    public CategoryAdapter(Context context){
        mLayoutInflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setCategoryArrayList(ArrayList<Category> CategoryArrayList){
        mCategoryArrayList=CategoryArrayList;
    }

    @Override
    public int getCount() {
        return mCategoryArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return mCategoryArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mCategoryArrayList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView=mLayoutInflater.inflate(android.R.layout.simple_list_item_2,null);
        }

        TextView textView1=(TextView)convertView.findViewById(android.R.id.text1);
        textView1.setText(mCategoryArrayList.get(position).getCategoryName());

        return convertView;
    }
}
