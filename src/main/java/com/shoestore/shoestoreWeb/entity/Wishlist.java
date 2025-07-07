package com.shoestore.shoestoreWeb.entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

public class Wishlist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long wishlist_id;
}
