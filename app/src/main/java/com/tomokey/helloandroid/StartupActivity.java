package com.tomokey.helloandroid;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import com.tomokey.helloandroid.service.ChatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class StartupActivity extends AppCompatActivity {
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // View
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @BindView(R.id.name)
    EditText mName;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // インスタンス変数
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private SharedPreferences mPrefs;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Androidライフサイクルメソッド
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * ユーザーインターフェース生成時に呼び出されます。
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);

        // 画面上の部品を探す
        ButterKnife.bind(this);

        mPrefs = getSharedPreferences("Setting", MODE_PRIVATE);

        // 保存済みの名前を表示
        mName.setText(mPrefs.getString("name", ""));
        mName.setSelection(mName.getText().length());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // UIイベントハンドラー
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 「はじめる」ボタンが押下された際に呼び出されます。
     */
    @SuppressWarnings("unused")
    @OnClick(R.id.start_button)
    public void onClickStartButton() {
        // 入力テキストが空の場合
        if (TextUtils.isEmpty(mName.getText())) {
            // Toastで「名前を入力してください」と表示して終了
            Toast.makeText(this, R.string.plz_input_name, Toast.LENGTH_SHORT).show();
            return;
        }

        // SHaredPreferencesへ保存
        mPrefs.edit().putString("name", mName.getText().toString()).apply();

        // チャット画面へ遷移する
        Intent intent = new Intent(this, ChatActivity.class);
        startActivity(intent);

        // この画面は閉じておく
        finish();
    }
}
