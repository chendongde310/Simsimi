package com.chendong.ai.simsimi.bean;

/**
 * 作者：陈东  —  www.renwey.com
 * 日期：2016/12/13 - 15:43
 * 注释：
 */
public class Request {

    private int status;

    private String respSentence;

    public void setStatus(int status){
        this.status = status;
    }
    public int getStatus(){
        return this.status;
    }
    public void setRespSentence(String respSentence){
        this.respSentence = respSentence;
    }
    public String getRespSentence(){
        return this.respSentence;
    }


    @Override
    public String toString() {
        return "Request{" +
                "status=" + status +
                ", respSentence='" + respSentence + '\'' +
                '}';
    }
}
