package com.chendong.ai.simsimi.bean;

import java.util.Date;

/**
 * 作者：陈东  —  www.renwey.com
 * 日期：2016/12/13 - 17:54
 * 注释：
 */
public class MessageBean {

    public static final int WHO_SIM = 1;
    public static final int WHO_ME = 0;
    private String message;
    private int who;
    private Date time;

    public MessageBean(String message, int who, Date time) {
        this.message = message;
        this.who = who;
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getWho() {
        return who;
    }

    public void setWho(int who) {
        this.who = who;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
