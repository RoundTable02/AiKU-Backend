package alarm;

import alarm.filter.MdcFilter;
import alarm.filter.RequestLogInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    private final RequestLogInterceptor requestLogInterceptor;
    private final MdcFilter mdcFilter;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestLogInterceptor)
                .addPathPatterns("/**");
    }

    @Bean
    public FilterRegistrationBean<MdcFilter> mdcFilter() {
        FilterRegistrationBean<MdcFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(mdcFilter);
        registrationBean.addUrlPatterns("/*");
        return registrationBean;
    }
}
