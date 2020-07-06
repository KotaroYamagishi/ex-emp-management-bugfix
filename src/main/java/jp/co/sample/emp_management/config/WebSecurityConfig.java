package jp.co.sample.emp_management.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import jp.co.sample.emp_management.domain.Administrator;
import jp.co.sample.emp_management.service.AdministratorService;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig  extends WebSecurityConfigurerAdapter{
    
   

    


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                //指定したパターンごとに制限をかける
                .antMatchers("/js/**", "/css/**", "/img/**","/**").permitAll();//制限なし
        //アクセスの許可
        http
                .formLogin()
                .loginPage("/")
                .permitAll();

        http.authorizeRequests()
                .antMatchers("/").permitAll()
                .antMatchers("/toInsert").permitAll()
                .antMatchers("/insert").permitAll()
                .antMatchers("/insert-result").permitAll()
                .antMatchers("/login").permitAll()
                .antMatchers("/employee").permitAll()
                .antMatchers("/showList").permitAll()
                .antMatchers("/showDetail").permitAll()
                .antMatchers("/update").permitAll()
                .antMatchers("/insert").permitAll()
                .antMatchers("/toInsert").permitAll()
                .anyRequest().authenticated();




        
    }

   

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

   
}