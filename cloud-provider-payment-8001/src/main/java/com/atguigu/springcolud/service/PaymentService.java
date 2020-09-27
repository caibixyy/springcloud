package com.atguigu.springcolud.service;


import com.atguigu.springcloud.entities.Payment;

public interface PaymentService {

    int create(Payment payment);

    Payment getPaymentById(Long id);
}
