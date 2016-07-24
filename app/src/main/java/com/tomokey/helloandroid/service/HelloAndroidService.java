package com.tomokey.helloandroid.service;

import android.util.Log;

import com.tomokey.helloandroid.dto.MessageSentResult;
import com.tomokey.helloandroid.dto.Message;

import org.greenrobot.eventbus.EventBus;

import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * HelloAndroidサービスクラス
 */
public class HelloAndroidService extends AbstractService {
    private Call<List<Message>> listMessageCall;
    private Call<MessageSentResult> sendMessageCall;

    /**
     * メッセージの一覧を取得します。
     *
     * @param lastId 既読最終メッセージID
     */
    public void listMessage(Integer lastId) {
        listMessageCall = createService(Api.class).list(lastId);
        listMessageCall.enqueue(new Callback<List<Message>>() {
            /**
             * 通信成功時に呼び出されます。
             */
            public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
                EventBus.getDefault().post(response.body());
            }

            /**
             * 通信失敗時に呼び出されます。
             */
            public void onFailure(Call<List<Message>> call, Throwable t) {
                EventBus.getDefault().post(Collections.<Message>emptyList());
            }
        });
    }

    /**
     * メッセージを送信します。
     *
     * @param name    名前
     * @param message メッセージ
     */
    public void sendMessage(String name, String message) {
        sendMessageCall = createService(Api.class).send(name, message);
        sendMessageCall.enqueue(new Callback<MessageSentResult>() {
            /**
             * 通信成功時に呼び出されます。
             */
            public void onResponse(Call<MessageSentResult> call, Response<MessageSentResult> response) {
                EventBus.getDefault().post(response.body());
            }

            /**
             * 通信失敗時に呼び出されます。
             */
            public void onFailure(Call<MessageSentResult> call, Throwable t) {
                Log.e(HelloAndroidService.class.getSimpleName(), "Failed to send message.", t);
            }
        });
    }

    /**
     * APIインターフェース定義
     */
    public interface Api {
        /**
         * メッセージ一覧を取得します。
         *
         * @param lastId 既読最終メッセージID
         * @return {@link Call}
         */
        @FormUrlEncoded
        @POST("/list.php")
        Call<List<Message>> list(@Field("lastId") Integer lastId);

        /**
         * メッセージを送信します。
         *
         * @param name    名前
         * @param message メッセージ
         * @return {@link Call}
         */
        @FormUrlEncoded
        @POST("/send.php")
        Call<MessageSentResult> send(@Field("name") String name, @Field("message") String message);
    }
}
