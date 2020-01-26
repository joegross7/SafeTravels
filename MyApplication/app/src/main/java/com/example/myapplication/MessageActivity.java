package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.amazonaws.mobile.client.AWSStartupHandler;
import com.amazonaws.mobile.client.AWSStartupResult;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.CreateTopicRequest;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.amazonaws.services.sns.model.SubscribeRequest;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobileconnectors.lex.interactionkit.Response;
import com.amazonaws.mobileconnectors.lex.interactionkit.config.InteractionConfig;
import com.amazonaws.mobileconnectors.lex.interactionkit.ui.InteractiveVoiceView;

import java.util.HashMap;
import java.util.Map;

public class MessageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);


/*

        final CreateTopicRequest createTopicRequest = new CreateTopicRequest("MyTopic");
        final CreateTopicResponse createTopicResponse = snsClient.createTopic(createTopicRequest);

        final String topicArn = "arn:aws:sns:us-east-1:913171289914:SafeTravelsarn:aws:sns:us-east-1:913171289914:SafeTravels";
        String contactNumber = "+19045027748";
        final SubscribeRequest subscribeRequest = new SubscribeRequest(topicArn, "SMS", contactNumber);
        snsClient.subscribe(subscribeRequest);

*/
        final Button mapActivityButton = (Button)findViewById(R.id.message_button);
        mapActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BasicAWSCredentials awsCreds = new BasicAWSCredentials("AKIA5JHKAC45NZNEFFGV", "OR8EOGojz5k/vaIUqio3Qlx8YlauxgLitmwMxRvH");
                final AmazonSNSClient snsClient = new AmazonSNSClient(awsCreds);
                final String message = "granty loves you";
                final String phoneNumber = "+19045027748";
                final Map<String, MessageAttributeValue> smsAttributes = new HashMap<String, MessageAttributeValue>();
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try  {
                            sendSMSMessage(snsClient, message, phoneNumber, smsAttributes);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();
                // Code here executes on main thread after user presses button
                mapActivityButton.setEnabled(false);
            }
        });

        Button mappActivityButton = (Button)findViewById(R.id.continue_button);
        mappActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startIntent = new Intent(getApplicationContext(), MapsActivity.class);
                //how to pass information
                startActivity(startIntent);
            }
        });



    }

    public static void sendSMSMessage(AmazonSNSClient snsClient, String message, String phoneNumber, Map<String, MessageAttributeValue> smsAttributes) {
        PublishResult result = snsClient.publish(new PublishRequest()
                .withMessage(message)
                .withPhoneNumber(phoneNumber)
                .withMessageAttributes(smsAttributes));
        System.out.println(result);
    }
}
