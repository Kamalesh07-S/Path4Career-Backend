package com.careerpath.admin;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
public class HashGenTest {
    @Test
    public void gen() {
        System.out.println("MYHASH:::" + new BCryptPasswordEncoder().encode("admin123"));
    }
}
