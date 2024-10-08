package aiku_main.service;

import aiku_main.dto.InquiryDto;
import aiku_main.gmail.GmailAPIProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import javax.mail.MessagingException;
import java.io.IOException;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class EmailService {

    private final GmailAPIProvider gmailAPIProvider;

    public void submitContactRequest(InquiryDto inquiryDto) {
        String title = "<QA> " + inquiryDto.getTitle();
        String content = "Sender : " + inquiryDto.getEmail() + "\n" + inquiryDto.getContent();
        try {
            gmailAPIProvider.sendMessage(
                    title,
                    content,
                    inquiryDto.getFile()
            );

        } catch (MessagingException | IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Not able to process request.");
        }

    }

}
