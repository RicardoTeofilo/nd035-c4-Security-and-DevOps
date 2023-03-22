package com.example.demo.integration;

import com.example.demo.SareetaApplication;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;

@Transactional
@SpringBootTest(classes = SareetaApplication.class)
public class SareetaApplicationFunctionalTest {



}
