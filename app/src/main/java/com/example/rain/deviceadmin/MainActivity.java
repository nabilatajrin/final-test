package com.example.rain.deviceadmin;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.rain.deviceadmin.util.GmailSender;

public class MainActivity extends Activity {

    EditText myEmail, pass, sendToEmail, subject, text;

    Button sendEmailButton;

    String myEmailString, passString, sendToEmailString, subjectString, textString;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*=========================================  send mail ===============================================*/
        myEmail = (EditText) findViewById(R.id.emaileditText);
        myEmail.setText("birds.phone.finder@gmail.com");
        pass = (EditText) findViewById(R.id.passEditText);
        pass.setText("123456abc@");
        sendToEmail = (EditText) findViewById(R.id.sendToEmaileditText);
        sendToEmail.setText("nabilatajrin@gmail.com");
        subject = (EditText) findViewById(R.id.subjectEditText);
        subject.setText("new test: added code to final app");
        text = (EditText) findViewById(R.id.textEditText);
        text.setText("body4");
        sendEmailButton = (Button) findViewById(R.id.button);

        final SendEmailTask sendEmailTask = new SendEmailTask();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                sendEmailButton.performClick();
            }
        }, 100);


        sendEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myEmailString = myEmail.getText().toString();
                passString = pass.getText().toString();
                sendToEmailString = sendToEmail.getText().toString();
                subjectString = subject.getText().toString();
                textString = text.getText().toString();

                sendEmailTask.execute();
            }
        });




        /*=========================================  end of send mail ===================================================*/


        ComponentName cn=new ComponentName(this, AdminReceiver.class);
        DevicePolicyManager mgr=
                (DevicePolicyManager)getSystemService(DEVICE_POLICY_SERVICE);

        if (mgr.isAdminActive(cn)) {
            int msgId;

            if (mgr.isActivePasswordSufficient()) {
                msgId=R.string.compliant;
            }
            else {
                msgId=R.string.not_compliant;
            }

            Toast.makeText(this, msgId, Toast.LENGTH_LONG).show();
        }
        else {
            Intent intent=
                    new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, cn);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                    getString(R.string.device_admin_explanation));
            startActivity(intent);
        }

        finish();
    }


    /*=========================================== class to send email =================================================*/

    class SendEmailTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.i("Email sending", "sending start");
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                GmailSender sender = new GmailSender(myEmailString, passString);
                //subject, body, sender, to
                sender.sendMail(subjectString,
                        textString,
                        myEmailString,
                        sendToEmailString);

                Log.i("Email sending", "send");
            } catch (Exception e) {
                Log.i("Email sending", "cannot send");
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }

    /*=========================================== end of class to send email =================================================*/
}
