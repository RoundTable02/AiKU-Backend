package aiku_main.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class InquiryDto {
    @NotBlank
    private String title;

    @NotBlank
    private String content;

    private MultipartFile file;

    @Email
    private String email;
}
