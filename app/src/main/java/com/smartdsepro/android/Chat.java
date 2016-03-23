package com.smartdsepro.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Chat extends AppCompatActivity {

    ArrayList<ChatMsg> chatmsgs;
    Pubnub pubnub;
    ListView chatMsglist;
    ArrayAdapter chatAdapter;
    Button sendButton;
    EditText chatMsgBox;
    String name;
    ButtonController buttonController;

    @Override
    protected void onResume() {
        super.onResume();
        name = LoginHelper.getUserName(this);
        if (name.equals(Constants.LOGIN_NAME_NOT_SET))
        {
            Intent i = new Intent(Chat.this, Login_logout.class);
            startActivity(i);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        buttonController = new ButtonController(this);

        chatmsgs = new ArrayList<ChatMsg>();
        pubnub = new Pubnub("pub-c-6bf6a729-b60a-4296-94d9-0859668b63fc", "sub-c-6fe7e48c-e62d-11e5-a4f2-0619f8945a4f");


        chatMsglist = (ListView) findViewById(R.id.chatList);


        chatAdapter = new chatmsgadapter();
        chatMsglist.setAdapter(chatAdapter);



        showHistory();
        sendButton = (Button) findViewById(R.id.chatBtn);
        chatMsgBox = (EditText) findViewById(R.id.chatmsgBox);

        try {
            subscribe();

        } catch (Exception e) {

        }
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage(chatMsgBox.getText().toString());
                chatMsgBox.setText("");


            }
        });





    }
    private String getDateTime()
    {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        //get current date time with Date()
        Date date = new Date();
        return dateFormat.format(date).toString();



    }

    private void sendMessage(String msg) {
        try {
            JSONObject jsonObject = new JSONObject();

            jsonObject.accumulate("user", name);
            jsonObject.accumulate("msg", msg);
            jsonObject.accumulate("senttime", getDateTime());


            pubnub.publish(Constants.CHANNEL_NAME, jsonObject, new Callback() {
                @Override
                public void successCallback(String channel, Object message) {
                    super.successCallback(channel, message);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showHistory() {
        pubnub.history(Constants.CHANNEL_NAME, 100, new Callback() {

            @Override
            public void successCallback(String channel, Object message) {
                super.successCallback(channel, message);
                try {
                    JSONArray fullArray = (JSONArray) message;
                    JSONArray msgArray = fullArray.getJSONArray(0);
                    int totalMsg = msgArray.length();
                    for(int i = 0; i<totalMsg; i++)
                    {
                        JSONObject currMsg = msgArray.getJSONObject(i);
                        chatmsgs.add(new ChatMsg(currMsg.getString("user"), currMsg.getString("msg"), currMsg.getString("senttime")));

                    }
                    Chat.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            chatAdapter.notifyDataSetChanged();
                        }
                    });
                } catch (Exception e) {

                }


                System.out.println("Hist - " + message.toString());

                //Log.i(Constants.DEBUG_TAG, message.toString());
            }

            @Override
            public void successCallback(String channel, Object message, String timetoken) {
                super.successCallback(channel, message, timetoken);
            }
        });
    }


    private void subscribe() throws Exception{
        pubnub.subscribe(Constants.CHANNEL_NAME, new Callback() {
            @Override
            public void successCallback(String channel, final Object message, final String timetoken) {
                super.successCallback(channel, message, timetoken);
                Timestamp timestamp = new Timestamp(Long.parseLong(timetoken));
                Date date = new Date(timestamp.getTime());

                JSONObject newMsg = (JSONObject) message;
                try {

                    chatmsgs.add(new ChatMsg(newMsg.getString("user"), newMsg.getString("msg"), newMsg.getString("senttime")));

                } catch (Exception e) {
                    e.printStackTrace();
                }

                Chat.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        chatAdapter.notifyDataSetChanged();
                        // Toast.makeText(Chat.this, message.toString() + "  " + timetoken, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    public class chatmsgadapter extends ArrayAdapter<ChatMsg> {
        public chatmsgadapter() {
            super(getApplicationContext(), R.layout.chatlistlike,
                    chatmsgs);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View workingView = convertView;
            if (workingView == null) {
                workingView = getLayoutInflater().inflate(
                        R.layout.chatlistlike, parent, false);

            }

            ChatMsg current = getItem(position);
            TextView tv_user = (TextView) workingView
                    .findViewById(R.id.chat_username);
            TextView tv_msg = (TextView) workingView
                    .findViewById(R.id.chat_msg);
            TextView tv_time = (TextView) workingView
                    .findViewById(R.id.chat_time);

            tv_user.setText(current.getUserName());
            tv_msg.setText(current.getMsg());
            tv_time.setText(current.getSentTime());

            return workingView;
        }

    }

}
