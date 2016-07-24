package com.tomokey.helloandroid.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * メッセージデータクラス
 */
@Getter
@Setter
@AllArgsConstructor
public class Message {
    public Integer id;
    public String name;
    public String message;
}
