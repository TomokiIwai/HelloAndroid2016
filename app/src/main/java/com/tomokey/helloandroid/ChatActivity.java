package com.tomokey.helloandroid;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.annimon.stream.Stream;
import com.tomokey.helloandroid.dto.Message;
import com.tomokey.helloandroid.dto.MessageSentResult;
import com.tomokey.helloandroid.service.HelloAndroidService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import lombok.Setter;

/**
 * チャット画面アクティビティクラス
 */
public class ChatActivity extends AppCompatActivity {
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // View
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @BindView(R.id.listview)
    ListView listview;
    @BindView(R.id.input_text)
    EditText inputText;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // インスタンス変数
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // 名前
    private String mName;

    // メッセージリストを定期的に更新するタスク
    private MessageUpdater mMsgUpdateTask = new MessageUpdater();

    // メッセージデータとListViewを接続するアダプタ
    private SimpleAdapter mAdapter;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Androidライフサイクルメソッド
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * ユーザーインターフェース生成時に呼び出されます。
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // SharedPreferencesに保存してある名前を取得
        mName = getSharedPreferences("Setting", MODE_PRIVATE).getString("name", "名無し");

        setTitle(mName);

        // 画面上の部品を探す
        ButterKnife.bind(this);

        mAdapter = new SimpleAdapter(this);
        listview.setAdapter(mAdapter);
    }

    /**
     * ユーザーインターフェース表示開始時に呼び出されます。
     */
    @Override
    protected void onStart() {
        super.onStart();

        EventBus.getDefault().register(this);
    }

    /**
     * ユーザーインタラクション開始時に呼び出されます。
     */
    @Override
    protected void onResume() {
        super.onResume();

        // メッセージの更新タスクを開始
        mMsgUpdateTask.start();
    }

    /**
     * ユーザーインタラクション終了時に呼び出されます。
     */
    @Override
    protected void onPause() {
        super.onPause();

        // メッセージの更新タスクを終了
        mMsgUpdateTask.stop();
    }

    /**
     * ユーザーインターフェース表示終了時に呼び出されます。
     */
    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);

        super.onStop();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // UIイベントハンドラー
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 送信ボタンが押下された際に呼び出されます。
     */
    @OnClick(R.id.send_button)
    public void onClickSendButton() {
        // 入力テキストが空の場合
        if (TextUtils.isEmpty(inputText.getText())) {
            // Toastで「メッセージを入力してください」と表示して終了
            Toast.makeText(this, R.string.plz_input_msg, Toast.LENGTH_SHORT).show();
            return;
        }

        // メッセージ送信APIを呼び出す
        new HelloAndroidService().sendMessage(mName, inputText.getText().toString());

        // 入力テキストをクリアしておく
        inputText.setText("");
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // EventBusハンドラー
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * メッセージのロードが完了した際に呼び出されます。
     *
     * @param messageList メッセージリスト
     */
    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageLoaded(List<Message> messageList) {
        mAdapter.addAll(messageList);

        // 既読最終メッセージIDを更新
        Stream.of(messageList).reduce((a, b) -> b).map(Message::getId).ifPresent(lastId -> mMsgUpdateTask.setMLastId(lastId));
    }

    /**
     * メッセージ送信が完了した際に呼び出されます。
     *
     * @param result メッセージ送信結果
     */
    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageSentCompleted(MessageSentResult result) {
        Toast.makeText(this, R.string.notify_sent_msg, Toast.LENGTH_SHORT).show();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Inner Class
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * リストアダプタークラス
     */
    private static class SimpleAdapter extends ArrayAdapter<Message> {
        private LayoutInflater mInflater;

        /**
         * コンストラクタ
         */
        public SimpleAdapter(Context context) {
            super(context, R.layout.message_row);

            mInflater = LayoutInflater.from(context);
        }

        /**
         * ListViewの行に表示するViewを生成します。
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // 行に該当するデータを取得
            Message item = getItem(position);

            // 必要に応じてViewを生成
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.message_row, parent, false);
            }

            // 名前とメッセージのViewを探す
            TextView name = ButterKnife.findById(convertView, R.id.name);
            TextView message = ButterKnife.findById(convertView, R.id.message);

            // 名前とメッセージを設定
            name.setText(item.getName());
            message.setText(item.getMessage());

            return convertView;
        }
    }

    /**
     * メッセージ更新タスク
     */
    private static class MessageUpdater implements Runnable {
        // 再実行までの間隔(ミリ秒)
        private static final int RELOAD_DELAY = 5000;

        // 最終既読メッセージID
        @Setter
        private int mLastId;

        // ハンドラー
        private Handler mHandler = new Handler();

        /**
         * タスクを開始します。
         */
        public void start() {
            run();
        }

        /**
         * タスクを終了します。
         */
        public void stop() {
            mHandler.removeCallbacksAndMessages(null);
        }

        /**
         * 主処理を行います。
         */
        @Override
        public void run() {
            // メッセージ一覧取得APIを呼び出す
            new HelloAndroidService().listMessage(mLastId);

            // このタスクをRELOAD_DELAY後に再度実行するように予約
            mHandler.postDelayed(this, RELOAD_DELAY);
        }
    }
}
