package jp.techacademy.kanta.nakayama.taskapp;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class CategoryActivity extends AppCompatActivity {
    private Realm categoryRealm;
    private RealmResults<Category> mCategoryRealmResults;
    private RealmChangeListener categoryRealmListener = new RealmChangeListener() {
        @Override
        public void onChange() {
            //reloadCategoryListView();
        }
    };

    private ListView mListView;
    private CategoryAdapter mCategoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_input);
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
        
// Realmの設定 このままではいけないがどう修正すべきかわからない
        // Realmの設定 このままではいけないがどう修正すべきかわからない
        // Realmの設定 このままではいけないがどう修正すべきかわからない
        categoryRealm = Realm.getDefaultInstance();
        // Realmの設定 このままではいけないがどう修正すべきかわからない
        // Realmの設定 このままではいけないがどう修正すべきかわからない
        // Realmの設定 このままではいけないがどう修正すべきかわからない

        mCategoryRealmResults = categoryRealm.where(Category.class).findAll();
//        mCategoryRealmResults.sort("id", Sort.DESCENDING);
        categoryRealm.addChangeListener(categoryRealmListener);

        //ListViewの設定
        mCategoryAdapter=new CategoryAdapter(CategoryActivity.this);
        mListView=(ListView)findViewById(R.id.listView1);

        reloadListView();
    }

    private void reloadListView(){
        ArrayList<Category> CategoryArrayList=new ArrayList<>();

        mCategoryAdapter.setCategoryArrayList(CategoryArrayList);
        mListView.setAdapter(mCategoryAdapter);
        mCategoryAdapter.notifyDataSetChanged();
    }
}
