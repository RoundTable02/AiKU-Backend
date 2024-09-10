package common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BaseResultDto {
    private Long id;
    public static BaseResultDto get(Long id){
        return new BaseResultDto(id);
    }
}
