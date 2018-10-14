/***
 Copyright (c) 2013 CommonsWare, LLC
 Licensed under the Apache License, Version 2.0 (the "License"); you may not
 use this file except in compliance with the License. You may obtain a copy
 of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
 by applicable law or agreed to in writing, software distributed under the
 License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 OF ANY KIND, either express or implied. See the License for the specific
 language governing permissions and limitations under the License.

 Covered in detail in the book _The Busy Coder's Guide to Android Development_
 https://commonsware.com/Android
 */

package com.example.rain.lostphonefinder;

import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.rain.lostphonefinder.util.GmailSender;

import static android.content.ContentValues.TAG;

public class AdminReceiver extends DeviceAdminReceiver {

    final SendEmailTask sendEmailTask = new SendEmailTask();


    @Override
    public void onEnabled(Context ctxt, Intent intent) {
        /*========== 1 ==========*/
        ComponentName cn=new ComponentName(ctxt, AdminReceiver.class);
        DevicePolicyManager mgr=
                (DevicePolicyManager)ctxt.getSystemService(Context.DEVICE_POLICY_SERVICE);

        /*========== 1 ==========*/

        mgr.setPasswordQuality(cn,
                DevicePolicyManager.PASSWORD_QUALITY_NUMERIC);

        onPasswordChanged(ctxt, intent);
    }

    @Override
    public void onPasswordChanged(Context ctxt, Intent intent) {
        DevicePolicyManager mgr=
                (DevicePolicyManager)ctxt.getSystemService(Context.DEVICE_POLICY_SERVICE);
        int msgId;

        if (mgr.isActivePasswordSufficient()) {
            msgId=R.string.compliant;
        }
        else {
            msgId=R.string.not_compliant;
        }

//        Toast.makeText(ctxt, msgId, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPasswordFailed(Context ctxt, Intent intent) {
        //here is the code//



        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                sendEmailTask.execute();
                Log.d(TAG, "onPasswordFailed: sen email task delimeter ########################################");
            }
        }, 100);

        sendEmailTask.execute();

        ctxt.startActivity(new Intent(ctxt, MainActivity.class));

        /*Toast.makeText(ctxt, R.string.password_failed, Toast.LENGTH_LONG)
                .show();*/
        Log.d(TAG, "onPasswordFailed: FAILED ########################################");
    }

    @Override
    public void onPasswordSucceeded(Context ctxt, Intent intent) {
        /*Toast.makeText(ctxt, R.string.password_success, Toast.LENGTH_LONG)
                .show();*/

        Log.d(TAG, "onPasswordSuccess: SUCCESS ########################################");
    }
}

/*=========================================== class to send email ==============================================*/

class SendEmailTask extends AsyncTask<Void, Void, Void> {

    EditText myEmail, pass, sendToEmail, subject, text;

    Button sendEmailButton;

    String myEmailString = "my.lost.phone.finder@gmail.com", passString = "123456abc@", sendToEmailString = "nabilatajrin@gmail.com", subjectString = "success msg from lock screen", textString = "body5";

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