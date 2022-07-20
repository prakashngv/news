package com.kisszo.news.netty.mysql;

public enum ActiveMQConfig {
	URL("localhost"),
	Port("61616"),
	Queue("report"),
	ReminderQueue("Reminder"),
	BillingPayloadQueue("billingpayload"),
	ConsumptionPayloadQueue("consumptionpayload"),
	Username("admin"),
	Password("admin");
	
	String value;
	
	private ActiveMQConfig(String value){
		this.value = value;
	}
	
	public String getValue(){
		return this.value;
	}
	
	
}
