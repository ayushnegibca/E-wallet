package org.gfg.NotificationService.consumer;

import org.gfg.CommonConstants;
import org.gfg.NotificationService.util.EmailTemplate;
import org.gfg.NotificationService.worker.EmailWorker;
import org.gfg.model.DocumentType;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class EmailMessageConsumer {

    @Autowired
    EmailWorker emailWorker;

    @KafkaListener(topics = CommonConstants.USER_CREATED_TOPIC,groupId = "user-group")
    public void consumerNewlyCreatedUser(String data){
        System.out.println(data);
        JSONObject jsonObject = new JSONObject(data);

        String name = jsonObject.optString(CommonConstants.USER_NAME);
        String email = jsonObject.optString(CommonConstants.USER_EMAIL);
        String documentNo = jsonObject.optString(CommonConstants.USER_DOCUMENT_VALUE);

       DocumentType documentType = jsonObject.optEnum(DocumentType.class,CommonConstants.USER_DOCUMENT_TYPE);

       emailWorker.sendNotification(email,name,documentNo,documentType.toString());
    }
}
