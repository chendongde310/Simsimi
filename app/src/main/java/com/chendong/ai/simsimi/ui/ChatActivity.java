package com.chendong.ai.simsimi.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
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
import com.chendong.ai.simsimi.utils.BitmapUtil;
import com.orhanobut.hawk.Hawk;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String SAVE_KEY = "ChatRecord";
    private android.widget.TextView send;
    private android.widget.EditText sendText;
    private android.widget.RelativeLayout sendrl;
    private SimsimiService service;
    private SwipBaseAdapter adapter;
    private android.widget.ListView listview;
    private List<MessageBean> list = new ArrayList<>();
    private Context context;
    private boolean forFlag = false;//自娱自乐开关
    private boolean ftFlag = false;//黄暴开关
    private static  final String shareUrl = BitmapUtil.getSDPath()+"/simsimi/cache";
    private static  final String shareName = "share.png";
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
        getChatRecord();
        adapter = new SwipBaseAdapter();
        listview.setAdapter(adapter);
        listview.setSelection(adapter.getCount() - 1);
        setListener();
    }

    /**
     * 读取消息记录
     */
    private void getChatRecord() {
        list.addAll(Hawk.get(SAVE_KEY,new ArrayList<MessageBean>()));
    }


    /**
     * 储存消息记录
     */
    private void saveChatRecord() {
        Hawk.put(SAVE_KEY, list);
    }

    private void dataSetChanged() {
        adapter.notifyDataSetChanged();
        //储存消息记录
        saveChatRecord();
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
        if (sendText.getText() != null&&sendText.getText().length()>0) {
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
                    addMessage(new MessageBean("???", MessageBean.WHO_SIM, new Date()));
                }

            }

            @Override
            public void onFailure(Call<Request> call, Throwable t) {
                sendText.setText(text);
                t.printStackTrace();
                myMessage.setSucceed(false);
                addMessage(new MessageBean("你发的消息失败啦！点击小红色点重新发送", MessageBean.WHO_SIM, new Date()));
            }
        });
    }

    private void addMessage(MessageBean messageBean) {
        list.add(messageBean);
        dataSetChanged();
        listview.setSelection(adapter.getCount() - 1);

    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_menu, menu);
        return true;
    }

    /**
     * 黄暴程度  数值越小越不过滤 bad word
     *
     * @return
     */
    private double getFT() {
        if (ftFlag) {
            return 0.01;
        } else {
            return 1;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.empty) {  //清空面板
            list.clear();
            dataSetChanged();
        } else if (item.getItemId() == R.id.intelligence) {  //智障模式
            ftFlag = !ftFlag;
            Toast.makeText(context, "黄暴模式：" + (ftFlag ? "开" : "关"), Toast.LENGTH_SHORT).show();
        } else if (item.getItemId() == R.id.auto) {  //自动对话
            forFlag = !forFlag;
            Toast.makeText(context, "自娱自乐：" + (forFlag ? "开" : "关"), Toast.LENGTH_SHORT).show();
        }else if (item.getItemId() == R.id.share) {  //截图分享

            shareImg();





        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 分享图片
     *
     */
    private void shareImg() {
        Bitmap bitmap = BitmapUtil.shotWindowToBitmap(this);
        AlertDialog  alertDialog =   new  AlertDialog.Builder(this).setView(R.layout.view_share_bitmap).create();
        alertDialog .show();
        Window window = alertDialog.getWindow();
        window.setContentView(R.layout.view_share_bitmap);
        ImageView ShareImage = (ImageView) window.findViewById(R.id.share_img);
        ShareImage.setImageBitmap(bitmap);
        ImageView shareImg  = (ImageView) window.findViewById(R.id.share_bitmap );
        ImageView downloadImg = (ImageView) window.findViewById(R.id.download_bitmap);
        final Bitmap savebitmap = bitmap;
        shareImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    BitmapUtil.saveToSD(savebitmap,shareUrl,shareName);
                    shareMsg("发现一个有神经病的聊天机器人",null,shareUrl+shareName);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

        downloadImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String fileUrl = BitmapUtil.getSDPath()+"/simsimi/";
                    String fileName = "sim"+System.currentTimeMillis()+".png";
                    BitmapUtil.saveToSD(savebitmap,fileUrl,"sim"+fileName);
                    Toast.makeText(context, "保存成功,文件路径为："+fileUrl+fileName, Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });




    }

    /**
     * 分享功能
     *

     * @param msgTitle
     *            消息标题
     * @param msgText
     *            消息内容
     * @param imgPath
     *            图片路径，不分享图片则传null
     */
    public void shareMsg(String msgTitle, String msgText,
                         String imgPath) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        if (imgPath == null || imgPath.equals("")) {
            intent.setType("text/plain"); // 纯文本
        } else {
            File f = new File(imgPath);
            if (f != null && f.exists() && f.isFile()) {
                Logger.d("设置分享文件为图片类型");
                intent.setType("image/jpg");
                Uri u = Uri.fromFile(f);
                intent.putExtra(Intent.EXTRA_STREAM, u);
            }
        }
        intent.putExtra(Intent.EXTRA_SUBJECT, msgTitle);
        intent.putExtra(Intent.EXTRA_TEXT, msgText);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(Intent.createChooser(intent, getTitle()));
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
