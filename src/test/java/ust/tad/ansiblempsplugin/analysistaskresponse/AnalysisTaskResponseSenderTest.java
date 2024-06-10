package ust.tad.ansiblempsplugin.analysistaskresponse;

import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ust.tad.ansiblempsplugin.analysistask.AnalysisTaskResponseSender;

@SpringBootTest
public class AnalysisTaskResponseSenderTest {

    @Autowired
    AnalysisTaskResponseSender analysisTaskResponseSender;

    @Test
    public void sendAnalysisTaskResponse() throws JsonProcessingException {
        analysisTaskResponseSender.sendSuccessResponse(UUID.randomUUID());
    }
    
}
