package com.shoestore.shoestoreWeb.entity;

import java.time.LocalDate;

public class Payment {
    long paymentID;
    LocalDate paymentDate;
    String paymentMethod;
    double amount;
}
