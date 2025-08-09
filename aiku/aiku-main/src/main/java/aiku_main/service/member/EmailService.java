package aiku_main.service.member;

import aiku_main.dto.InquiryDto;
import aiku_main.exception.MemberNotFoundException;
import aiku_main.gmail.GmailAPIProvider;
import aiku_main.repository.member.MemberRepository;
import common.domain.member.Member;
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
    private final MemberRepository memberRepository;

    public Long submitContactRequest(Long accessMemberId, InquiryDto inquiryDto) {
        Member member = memberRepository.findById(accessMemberId)
                .orElseThrow(() -> new MemberNotFoundException());

        String title = "<QA> " + inquiryDto.getTitle();
        String sender = "Sender : " + inquiryDto.getEmail() + "\n"
                + member.getId() + "&&" + member.getOauthId() + " p" + member.getPoint() + "\n";
        String content = sender + inquiryDto.getContent();
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

        return member.getId();
    }

}
