package com.taxeo.sbus.srxp.model;

import java.net.URL;
import java.util.Date;
import java.util.List;

public class Expense {
	
	private int id;
	
	private int reportId;
	
	private Account account;
	
	private Currency currency;
	
	private Date expenseDate;
	
	private PaymentMethod paymentMethod;
	
	private String pictureName;
	
	private byte[] pictureContent;
	
	private URL pictureUrl;
	
	private List<Amount> amounts;

}
