package jp.techacademy.kanta.nakayama.taskapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;

public class MainActivity extends AppCompatActivity {
    public final static String EXTRA_TASK="jp.techacademy.kanta.nakayama.taskapp.TASK";

    private Realm mRealm;
    private RealmResults<Task> mTaskRealmResults;
    private RealmChangeListener mRealmListener=new RealmChangeListener(){
        @Override
        public void onChange() {
            reloadListView();
        }
    };

    private ListView mListView;
    private TaskAdapter mTaskAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //「絞込み」ボタンを押したときの動作
        Button searchButton=(Button)findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reloadSearchListView();
            }
        });

        //「戻る」ボタンを押したときの動作
        Button returnButton=(Button)findViewById(R.id.return_button);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reloadListView();
            }
        });

        //「+」ボタンを押したときの動作（タスクの追加）
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,InputActivity.class);
                startActivity(intent);
            }
        });

        //Realmの設定
        mRealm=Realm.getDefaultInstance();
        mTaskRealmResults=mRealm.where(Task.class).findAll();
        mTaskRealmResults.sort("date", Sort.DESCENDING);
        mRealm.addChangeListener(mRealmListener);

        //ListViewの設定
        mTaskAdapter=new TaskAdapter(MainActivity.this);
        mListView=(ListView)findViewById(R.id.listView1);

        //ListViewをタップしたときの処理
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //入力・編集する画面に推移させる。
                Task task=(Task)parent.getAdapter().getItem(position);

                Intent intent=new Intent(MainActivity.this,InputActivity.class);
                intent.putExtra(EXTRA_TASK,task);
                startActivity(intent);
            }
        });

        //ListViewを長押ししたときの処理
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //タスクの削除処理
                final Task task=(Task)parent.getAdapter().getItem(position);

                //ダイアログを表示する
                AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);

                builder.setTitle("削除");
                builder.setMessage(task.getTitle()+"を削除しますか？");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        RealmResults<Task> results=mRealm.where(Task.class).equalTo("id",task.getId()).findAll();
                        mRealm.beginTransaction();
                        results.clear();
                        mRealm.commitTransaction();

                        Intent resultIntent=new Intent(getApplicationContext(),TaskAlarmReceiver.class);
                        PendingIntent resultPendingIntent=PendingIntent.getBroadcast(
                                MainActivity.this,
                                task.getId(),
                                resultIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );
                        AlarmManager alarmManager=(AlarmManager)getSystemService(ALARM_SERVICE);
                        alarmManager.cancel(resultPendingIntent);

                        reloadListView();
                    }
                });
                builder.setNegativeButton("CANCEL",null);

                AlertDialog dialog=builder.create();
                dialog.show();

                return true;
            }
        });

        reloadListView();
    }

    private void reloadListView(){
        ArrayList<Task> taskArrayList=new ArrayList<>();

        for(int i=0;i<mTaskRealmResults.size();i++){
            Task task=new Task();

            task.setId(mTaskRealmResults.get(i).getId());
            task.setTitle(mTaskRealmResults.get(i).getTitle());
            task.setContents(mTaskRealmResults.get(i).getContents());
            task.setCategory(mTaskRealmResults.get(i).getCategory());
            task.setDate(mTaskRealmResults.get(i).getDate());

            taskArrayList.add(task);
        }
        mTaskAdapter.setTaskArrayList(taskArrayList);
        mListView.setAdapter(mTaskAdapter);
        mTaskAdapter.notifyDataSetChanged();
    }

    private void reloadSearchListView(){
        ArrayList<Task> searchTaskArrayList=new ArrayList<>();
        EditText searchCategoryEditText=(EditText)findViewById(R.id.search_text);
        String searchCategory=searchCategoryEditText.getText().toString();
        String compareCategory;

        for(int i=0;i<mTaskRealmResults.size();i++){
            compareCategory=mTaskRealmResults.get(i).getCategory().categoryName;
            if(compareCategory.equals(searchCategory)) {
                Task task = new Task();

                task.setId(mTaskRealmResults.get(i).getId());
                task.setTitle(mTaskRealmResults.get(i).getTitle());
                task.setContents(mTaskRealmResults.get(i).getContents());
                task.setCategory(mTaskRealmResults.get(i).getCategory());
                task.setDate(mTaskRealmResults.get(i).getDate());

                searchTaskArrayList.add(task);
            }
        }
        mTaskAdapter.setTaskArrayList(searchTaskArrayList);
        mListView.setAdapter(mTaskAdapter);
        mTaskAdapter.notifyDataSetChanged();
    }

    protected void onDestroy(){
        super.onDestroy();
        mRealm.close();
    }
}