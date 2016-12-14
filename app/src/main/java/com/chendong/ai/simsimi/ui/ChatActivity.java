package com.chendong.ai.simsimi.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
    private List<MessageBean> list = new ArrayList<>();
    private Context context;
    private boolean forFlag = false;//自娱自乐开关
    private boolean ftFlag = false;//低准确度开关

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
        adapter = new SwipBaseAdapter();
        listview.setAdapter(adapter);
        setListener();
    }

    private void setListener() {
        send.setOnClickListener(this);
        listview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideSoftInput();
                return false;
            }
        });

    }


    @Override
    public void onClick(View v) {
        if (sendText != null) {
            getText(sendText.getText().toString());
        }
    }

    /**
     * 隐藏软键盘
     */
    private void hideSoftInput() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(sendText.getWindowToken(), 0);
    }


    private void getText(final String text) {
        sendText.setText("");
        final MessageBean myMessage = new MessageBean(text, MessageBean.WHO_ME, new Date());
        addMessage(myMessage);

        MyService.getInstance().getService().getReqText(text, getFT()).enqueue(new Callback<Request>() {
            @Override
            public void onResponse(Call<Request> call, final Response<Request> response) {
                if (response.body() != null && 200 == response.body().getStatus()) {
                    addMessage(new MessageBean(response.body().getRespSentence(), MessageBean.WHO_SIM, new Date()));

                    if (forFlag) {
                        //开始自娱自乐。。
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                getText(response.body().getRespSentence());
                            }
                        }, 2000);
                    }
                } else {
                    addMessage(new MessageBean("你在BBb什么？？请说人话~", MessageBean.WHO_SIM, new Date()));
                }

            }

            @Override
            public void onFailure(Call<Request> call, Throwable t) {
                sendText.setText(text);
                t.printStackTrace();
                myMessage.setSucceed(false);
                addMessage(new MessageBean("你发的消息失败啦！", MessageBean.WHO_SIM, new Date()));
            }
        });
    }

    private void addMessage(MessageBean messageBean) {
        list.add(messageBean);
        adapter.notifyDataSetChanged();
        listview.setSelection(list.size());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_menu, menu);
        return true;
    }

    /**
     * 准确度
     * @return
     */
    private double getFT(){
        if(ftFlag){
            return 0.01;
        }else {
            return 1;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.empty) {  //清空面板
            list.clear();
            adapter.notifyDataSetChanged();
        } else if (item.getItemId() == R.id.intelligence) {  //智障模式
            ftFlag = !ftFlag;
            Toast.makeText(context, "黄暴模式："+(ftFlag?"开":"关"), Toast.LENGTH_SHORT).show();
        } else if (item.getItemId() == R.id.auto) {  //自动对话
            forFlag = !forFlag;
            Toast.makeText(context, "自娱自乐："+(forFlag?"开":"关"), Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    class SwipBaseAdapter extends BaseAdapter {

        String[] item = {"删除此条", "再次发送", "复制内容"
        };


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
            final MessageBean messageBean = getItem(position);
            View view = null;
            TextView textView = new TextView(context);
            if (messageBean.getWho() == MessageBean.WHO_ME) {
                view = View.inflate(context, R.layout.view_chat_me, null);
                textView = (TextView) view.findViewById(R.id.text_me);
                ImageView imageView = (ImageView) view.findViewById(R.id.err_img);
                textView.setText(messageBean.getMessage());
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        list.remove(messageBean);
                        getText(messageBean.getMessage());
                    }
                });

                if (!messageBean.isSucceed()) {
                    imageView.setVisibility(View.VISIBLE);
                } else {
                    imageView.setVisibility(View.GONE);
                }

            } else if (messageBean.getWho() == MessageBean.WHO_SIM) {
                view = View.inflate(context, R.layout.view_chat_sim, null);
                textView = (TextView) view.findViewById(R.id.text_sim);
                textView.setText(messageBean.getMessage());
            }
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    new AlertDialog.Builder(context)
                            .setItems(item, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case 0:
                                            list.remove(messageBean);
                                            notifyDataSetChanged();
                                            break;
                                        case 1:
                                            getText(messageBean.getMessage());
                                            break;
                                        case 2:
                                            sendText.setText(messageBean.getMessage());
                                           // Toast.makeText(context, messageBean.getMessage(), Toast.LENGTH_SHORT).show();
                                            break;
                                        default:
                                            break;
                                    }
                                }
                            }).show();
                }
            });

            return view;
        }
    }

}
