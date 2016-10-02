package jp.techacademy.kanta.nakayama.taskapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;

public class CategoryActivity extends AppCompatActivity {
    public final static String EXTRA_CATEGORY="jp.techacademy.kanta.nakayama.taskapp.CATEGORY";

    private Realm categoryRealm;
    private RealmResults<Category> mCategoryRealmResults;
    private RealmChangeListener categoryRealmListener = new RealmChangeListener() {
        @Override
        public void onChange() {
            reloadListView();
        }
    };

    private ListView mListView;
    private CategoryAdapter mCategoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        //fab.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View view) {
        //        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
        //                .setAction("Action", null).show();
        //    }
        //});

        //Realmの設定
        categoryRealm = Realm.getDefaultInstance();
        mCategoryRealmResults = categoryRealm.where(Category.class).findAll();
        mCategoryRealmResults.sort("id", Sort.DESCENDING);
        categoryRealm.addChangeListener(categoryRealmListener);

        //ListViewの設定
        mCategoryAdapter=new CategoryAdapter(CategoryActivity.this);
        mListView=(ListView)findViewById(R.id.listView1);

        //ListViewをタップしたときの処理
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //入力・編集する画面に推移する（InputActivityに移動）
            }
        });

        //ListViewを長押ししたときの処理
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //Categoryを削除する。
                return false;
            }
        });

        if(mCategoryRealmResults.size()==0){
            //アプリ起動時にカテゴリの数が0だった場合、表示テスト用のサンプルを表示する。
            addCategoryForTest();
        }

        reloadListView();
    }

    private void reloadListView(){
        ArrayList<Category> CategoryArrayList=new ArrayList<>();

        for(int i=0;i<mCategoryRealmResults.size();i++){
            Category category=new Category();

            category.setId(mCategoryRealmResults.get(i).getId());
            category.setCategoryName(mCategoryRealmResults.get(i).getCategoryName());

            CategoryArrayList.add(category);
        }
        mCategoryAdapter.setCategoryArrayList(CategoryArrayList);
        mListView.setAdapter(mCategoryAdapter);
        mCategoryAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        categoryRealm.close();
    }

    private void addCategoryForTest(){
        Category category=new Category();
        category.setId(0);
        category.setCategoryName("Nakayama");

        categoryRealm.beginTransaction();
        categoryRealm.copyToRealmOrUpdate(category);
        categoryRealm.commitTransaction();
    }

}
