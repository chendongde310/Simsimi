package com.chendong.ai.simsimi;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
/**
 *
 * 作者：chendongde310
 * Github:www.github.com/chendongde310
 * 日期：2016/12/13 - 16:31
 * 注释：小黄鸡，官网上抓的接口，且用且珍惜
 * 更新内容：
 */
public class MainActivity extends AppCompatActivity {

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://www.simsimi.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    SimsimiService service = retrofit.create(SimsimiService.class);
    private android.widget.EditText sendText;
    private android.widget.TextView gettext;
    private android.widget.TextView button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.button = (TextView) findViewById(R.id.button);
        this.gettext = (TextView) findViewById(R.id.gettext);
        this.sendText = (EditText) findViewById(R.id.sendText);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sendText.getText()!=null){
                    gettext(sendText.getText().toString());
                }
            }
        });
    }

    private void gettext(String text) {
        service.getReqText(text,Math.random()).enqueue(new Callback<Request>() {
            @Override
            public void onResponse(Call<Request> call, Response<Request> response) {
                gettext.setText(response.body().getRespSentence());
                sendText.setText("");
            }

            @Override
            public void onFailure(Call<Request> call, Throwable t) {
                t.printStackTrace();
                gettext.setText("获取失败");
            }
        });
    }


}
