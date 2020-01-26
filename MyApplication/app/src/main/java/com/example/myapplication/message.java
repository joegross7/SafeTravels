package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.AWSStartupHandler;
import com.amazonaws.mobile.client.AWSStartupResult;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.CreateTopicRequest;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.amazonaws.services.sns.model.SubscribeRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import android.os.Bundle;
import android.util.Log;


public class message extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message2);
        String contactNumber = "+19045027748";
/*
        AmazonSNSClient(AWSCredentials awsCredentials);

        final CreateTopicRequest createTopicRequest = new CreateTopicRequest("MyTopic");
        final CreateTopicResponse createTopicResponse = snsClient.createTopic(createTopicRequest);

        final SubscribeRequest subscribeRequest = new SubscribeRequest(topicArn, "SMS",
                "+19045027748");
        snsClient.subscribe(subscribeRequest);

        final String msg = "If you receive this message, publishing a message to an Amazon SNS topic works.";
        final PublishRequest publishRequest = new PublishRequest(topicArn, msg);
        final PublishResult publishResponse = snsClient.publish(publishRequest);

    }
    private BasicAWSCredentials awsCreds = new BasicAWSCredentials("AKIAYO4MKXNF6QPMVBV3", "MiuoNvqvtGE9/xpnzQhIQGbjejGiWxD9xW3ECfYJ");
    private String topicArn = "arn:aws:sns:us-east-1:913171289914:SafeTravels";

    }
*/
    }
}
