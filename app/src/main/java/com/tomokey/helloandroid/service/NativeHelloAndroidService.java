package com.tomokey.helloandroid.service;

import android.util.Log;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.io.CharStreams;
import com.tomokey.helloandroid.R;
import com.tomokey.helloandroid.app.MyApp;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * （Androidデフォルト実装版）HelloAndroidサービスクラス
 */
public class NativeHelloAndroidService {
    /**
     * メッセージ一覧を取得します。
     *
     * @return メッセージ一覧
     */
    public String listMessage() {
        // スレッドを作成
        ListMessageThread thread = new ListMessageThread();
        thread.setPriority(Thread.MAX_PRIORITY);

        // スレッドの処理を開始
        thread.start();

        // スレッドの処理終了を待機
        Thread.yield();

        // スレッドの実行結果を返却
        return thread.result;
    }

    /**
     * メッセージ一覧を取得するスレッドクラス
     */
    private static class ListMessageThread extends Thread {
        // APIサーバーのURL
        private static final String BASE_URL = MyApp.getInstance().getString(R.string.api_host);

        // 文字列をスラッシュで結合するツール
        private static final Joiner SLASH = Joiner.on('/');

        // メッセージ一覧情報を格納する変数
        public String result;

        @Override
        public void run() {
            try {
                // 接続先のURLを構築
                String urlStr = SLASH.join(BASE_URL, "list.php");

                // URLオブジェクトを生成
                URL url = new URL(urlStr);

                // コネクションを確立
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();

                // 通信を行い、結果を取得
                InputStream stream = conn.getInputStream();

                // ストリームの内容を読み込むためのReaderオブジェクトを生成
                InputStreamReader reader = new InputStreamReader(stream, Charsets.UTF_8);

                // ストリームの内容を文字列として取得
                result = CharStreams.toString(reader);
            } catch (Exception e) {
                Log.e("AndroidTest", "Error", e);
            }
        }
    }
}
