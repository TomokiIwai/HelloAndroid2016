package com.tomokey.helloandroid.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * メッセージ送信結果データクラス
 */
@Getter
@Setter
@AllArgsConstructor
public class MessageSentResult {
    public String result;
    public String reason;
}
