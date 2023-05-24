package com.husc.services;

import org.springframework.stereotype.Service;


@Service
public class CurrencyService {
	private static final double EXCHANGE_RATE = 0.000043;
	
    public double convertVndToUsd(double price) {
        return  Math.round(price * EXCHANGE_RATE * 100.0) / 100.0;
    }
}
