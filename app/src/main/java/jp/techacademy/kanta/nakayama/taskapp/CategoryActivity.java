package jp.techacademy.kanta.nakayama.taskapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
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

    private Category mCategory;

    private Button mButton;
    private EditText mCategoryEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent=getIntent();
        mCategory=(Category)intent.getSerializableExtra(CategoryActivity.EXTRA_CATEGORY);

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
                Category category=(Category)parent.getAdapter().getItem(position);

                Intent intent=new Intent(CategoryActivity.this,InputActivity.class);
                intent.putExtra(EXTRA_CATEGORY,category);
                startActivity(intent);
            }
        });

        //ListViewを長押ししたときの処理
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //Categoryを削除する。

                final Category category=(Category)parent.getAdapter().getItem(position);

                //ダイアログを表示させる。
                AlertDialog.Builder builder=new AlertDialog.Builder(CategoryActivity.this);

                builder.setTitle("削除");
                builder.setMessage(category.getCategoryName()+"を削除しますか？");

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        RealmResults<Category> results=categoryRealm.where(Category.class).equalTo("id",category.getId()).findAll();

                        categoryRealm.beginTransaction();
                        results.clear();
                        categoryRealm.commitTransaction();

                        reloadListView();
                    }
                });
                builder.setNegativeButton("CANCEL",null);

                AlertDialog dialog=builder.create();
                dialog.show();

                return true;
            }
        });

        //作成ボタンを押したときの処理
        mButton=(Button)findViewById(R.id.category_button);
        mCategoryEdit=(EditText)findViewById(R.id.category_edit);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //新しいカテゴリを作成する。
                if(mCategoryEdit.getText().toString().equals("")){
                    AlertDialog.Builder builder=new AlertDialog.Builder(CategoryActivity.this);
                    builder.setTitle("ERROR");
                    builder.setMessage("カテゴリを入力してください");

                    AlertDialog dialog=builder.create();
                    dialog.show();
                }else{
                    addCategory();
                    //finish();
                }
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

    private void addCategory(){
        Realm realm=Realm.getDefaultInstance();
        if(mCategory==null){
            mCategory=new Category();
            RealmResults<Category>categoryRealmResults=realm.where(Category.class).findAll();

            int identifier;
            if(categoryRealmResults.max("id")!=null){
                identifier=categoryRealmResults.max("id").intValue()+1;
            }else{
                identifier=0;
            }
            mCategory.setId(identifier);
        }

        String categoryName=mCategoryEdit.getText().toString();
        mCategory.setCategoryName(categoryName);

        realm.beginTransaction();;
        realm.copyToRealmOrUpdate(mCategory);
        realm.commitTransaction();

        realm.close();
    }

}
