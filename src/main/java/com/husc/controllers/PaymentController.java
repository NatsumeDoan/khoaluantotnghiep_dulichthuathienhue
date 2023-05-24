package com.husc.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.husc.config.PaypalPaymentIntent;
import com.husc.config.PaypalPaymentMethod;
import com.husc.models.Booking;
import com.husc.repository.BookingRepository;
import com.husc.security.services.PaypalService;
import com.husc.utils.Utils;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;

import jakarta.servlet.http.HttpServletRequest;
@Controller
@RequestMapping("/paypal")
public class PaymentController {
	public static final String URL_PAYPAL_SUCCESS = "pay/success";
	public static final String URL_PAYPAL_CANCEL = "pay/cancel";
	private Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private PaypalService paypalService;
	
	@Autowired
	BookingRepository bookingRepository;
	
	@PostMapping("/pay")
	public String pay(HttpServletRequest request,@RequestParam("price") double price,@RequestParam("bookingid") long bookingid,@RequestParam("description") String description, @RequestParam("quantity") Integer quantity,@RequestParam("email") String email ){
		String cancelUrl = Utils.getBaseURL(request) + "/paypal/" + URL_PAYPAL_CANCEL;
		String successUrl = Utils.getBaseURL(request) + "/paypal/" + URL_PAYPAL_SUCCESS;
		try {
			Payment payment = paypalService.createPayment(
					bookingid,
					price,
					quantity,
					email,
					"USD",
					PaypalPaymentMethod.paypal,
					PaypalPaymentIntent.sale,
					description,
					cancelUrl,
					successUrl);
			for(Links links : payment.getLinks()){
				if(links.getRel().equals("approval_url")){
					return "redirect:" + links.getHref();
				}
			}
		} catch (PayPalRESTException e) {
			log.error(e.getMessage());
		}
		return "redirect:/booking/payment/" + bookingid;
	}
	@GetMapping(URL_PAYPAL_CANCEL)
	public String cancelPay(@RequestParam("bookingid") long id){
		return "redirect:/booking/payment/" + id;
	}
	@GetMapping(URL_PAYPAL_SUCCESS)
	public String successPay(@RequestParam("paymentId") String paymentId, @RequestParam("PayerID") String payerId,@RequestParam("bookingid") long id){
		try {
			Payment payment = paypalService.executePayment(paymentId, payerId);
			if(payment.getState().equals("approved")){
				Booking booking = bookingRepository.findById(id).get();
				booking.setIsPaying(true);
				bookingRepository.save(booking);
				return  "redirect:/booking/payment/detail/" + id;
			}
		} catch (PayPalRESTException e) {
			log.error(e.getMessage());
		}
		return "redirect:/booking/payment/" + id;
	}
}