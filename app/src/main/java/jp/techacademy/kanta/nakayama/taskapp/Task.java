package jp.techacademy.kanta.nakayama.taskapp;

import java.io.Serializable;
import java.util.Date;

import io.realm.RealmObject;

/**
 * Created by nhk2204 on 2016/09/22.
 */
public class Task extends RealmObject implements Serializable{
    private String title;   //タイトル
    private String contents;    //内容
    private Date date;  //日時

    //idをプライマリキーとして設定
    private int id;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title){
        this.title=title;
    }

    public String getContents(){
        return contents;
    }

    public void setContents(String contents){
        this.contents=contents;
    }

    public Date getDate(){
        return date;
    }

    public void setDate(Date date){
        this.date=date;
    }

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id=id;
    }
}
