package com.lambdaschool.sampleemps;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class SampleempsApplication
{
    private static Environment env;
    private static boolean stop = false;
    private static void checkEnvironmentVariable(String envvar) {
        if (System.getenv(envvar) == null) {
            stop = true;
        }
    }

    public static void main(String[] args)
    {
        checkEnvironmentVariable("OAUTHCLIENTID");
        checkEnvironmentVariable("OAUTHCLIENTSECRET");
        if (!stop) {
            SpringApplication.run(SampleempsApplication.class, args);
        } else {
            System.out.println("Environment Variables NOT SET: OAUTHCLIENTID and/or OAUTHCLIENTSECRET");
        }
    }

}
