package aiku_main.controller;

import aiku_main.dto.InquiryDto;
import aiku_main.service.EmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RequestMapping("/mail")
@RequiredArgsConstructor
@RestController
public class EmailSendController {
    private final EmailService emailService;

    @PostMapping(path = "/request")
    public void submitContactRequest(@ModelAttribute @Valid InquiryDto inquiryDto) {
        emailService.submitContactRequest(inquiryDto);
    }

}
