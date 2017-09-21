package md.jack.web.controller;

import md.jack.model.api.Message;
import md.jack.model.api.MessageRequest;
import md.jack.model.api.MessageResponse;
import md.jack.model.db.Consumer;
import md.jack.service.ConsumerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/api/message")
public class CloudMessagingController
{
    @Autowired
    private ConsumerService consumerService;

    @PostMapping(value = "/register", produces = APPLICATION_JSON_VALUE)
    public @ResponseBody MessageResponse register(@RequestBody final MessageRequest messageRequest)
    {
        Consumer byToken = consumerService.getByToken(messageRequest.getRegistrationToken());
        if (byToken == null)
        {
            byToken = new Consumer();
            byToken.setRegistrationToken(messageRequest.getRegistrationToken());

            consumerService.add(byToken);

            return new MessageResponse(new Message("Consumer registered successfully"));
        }
        else
        {
            return new MessageResponse(new Message("Registration token already used"));
        }
    }
}
