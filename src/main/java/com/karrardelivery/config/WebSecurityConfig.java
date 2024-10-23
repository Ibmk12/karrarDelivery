package com.karrardelivery.config;//package com.bss.CDRProcessor.config;
//
//import com.bss.cdrprocessor.filter.RequestIdFilter;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.annotation.Order;
//
//@Configuration
//@EnableWebSecurity
//@Order(1)
//public class WebSecurityConfig {
//
//    private final RequestIdFilter requestIdFilter;
//
//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
//        return authConfig.getAuthenticationManager();
//    }
//
//    public WebSecurityConfig(RequestIdFilter requestIdFilter) {
//        this.requestIdFilter = requestIdFilter;
//    }
//
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http.cors().and().csrf().disable()
//                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
//                .authorizeRequests().anyRequest().permitAll();
//        http.addFilterBefore(requestIdFilter, UsernamePasswordAuthenticationFilter.class);
//        return http.build();
//    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//}