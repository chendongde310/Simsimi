package com.chendong.ai.simsimi.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chendong.ai.simsimi.MyService;
import com.chendong.ai.simsimi.R;
import com.chendong.ai.simsimi.api.SimsimiService;
import com.chendong.ai.simsimi.bean.MessageBean;
import com.chendong.ai.simsimi.bean.Request;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {

    private android.widget.TextView send;
    private android.widget.EditText sendText;
    private android.widget.RelativeLayout sendrl;
    private SimsimiService service;
    private SwipBaseAdapter adapter;
    private android.widget.ListView listview;
    private List<MessageBean> list =new ArrayList<>();
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        context = this;
        this.listview = (ListView) findViewById(R.id.listview);
        this.sendrl = (RelativeLayout) findViewById(R.id.send_rl);
        this.sendText = (EditText) findViewById(R.id.sendText);
        this.send = (TextView) findViewById(R.id.send);
        service = MyService.getInstance().getService();
        send.setOnClickListener(this);
        adapter = new SwipBaseAdapter();
        listview.setAdapter(adapter);
    }


    @Override
    public void onClick(View v) {
        if (sendText != null) {
            getText(sendText.getText().toString());
        }


    }


    private void getText(final String text) {
        sendText.setText("");
        addMessage(new MessageBean(text, MessageBean.WHO_ME, new Date()));
        MyService.getInstance().getService().getReqText(text, Math.random()).enqueue(new Callback<Request>() {
            @Override
            public void onResponse(Call<Request> call, Response<Request> response) {
                if (response.body() != null && 200 == response.body().getStatus()) {

                    addMessage(new MessageBean(response.body().getRespSentence(), MessageBean.WHO_SIM, new Date()));
                } else {
                    addMessage(new MessageBean("你在BB什么呀？~老子完全听不懂哟~", MessageBean.WHO_SIM, new Date()));
                }
            }

            @Override
            public void onFailure(Call<Request> call, Throwable t) {
                sendText.setText(text);
                t.printStackTrace();
                addMessage(new MessageBean("获取失败了哦~绝对不是老子的问题啦！", MessageBean.WHO_SIM, new Date()));
            }
        });
    }

    private void addMessage(MessageBean messageBean) {
        list.add(messageBean);
        adapter.notifyDataSetChanged();
        listview.setSelection(list.size());
    }


    class SwipBaseAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public MessageBean getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            MessageBean messageBean = getItem(position);
            View view = null;
            if (messageBean.getWho() == MessageBean.WHO_ME) {
                view = View.inflate(context, R.layout.view_chat_me, null);
                TextView textView = (TextView) view.findViewById(R.id.text_me);
                textView.setText(messageBean.getMessage());
            } else if (messageBean.getWho() == MessageBean.WHO_SIM) {
                view = View.inflate(context, R.layout.view_chat_sim, null);
                TextView textView = (TextView) view.findViewById(R.id.text_sim);
                textView.setText(messageBean.getMessage());
            }

            return view;
        }
    }


}
