package jp.techacademy.kanta.nakayama.taskapp;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by nhk2204 on 2016/10/02.
 */
public class Category extends RealmObject implements Serializable{

    //idをプライマリキーとして設定
    @PrimaryKey
    int id;

    //カテゴリ
    String categoryName;

    public void setCategoryName(String categoryName){
        this.categoryName=categoryName;
    }
    public String getCategoryName(){
        return categoryName;
    }

    public void setId(int id){
        this.id=id;
    }
    public int getId(){
        return id;
    }
}
