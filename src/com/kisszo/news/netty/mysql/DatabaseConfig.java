package com.kisszo.news.netty.mysql;

public enum DatabaseConfig {
	Url("192.168.1.40"),
	Url2("192.168.1.40"),
	memCache("127.0.0.1"),
	Port("2480"),
	Dbname("mio"),
	UserName("root"),
	Password("gms123"),
	SQS("false"),  //true or false
	//StripeKey("sk_live_Rx3aB7AqlBxpx3FAAMrUqrJK"), // live
	//StripeEndPointSecret("whsec_um0nIsJYVtSnidRAQZiUdxrVz0McSLOB"), // live
 	StripeKey("sk_test_sViHqDS9So6dI1w03elSYEXt"), // test
 	StripeEndPointSecret("whsec_ntGmPYDjAutXiXJfR0w87bTcyY3XqdXm"), // test
	
	//QBClientId("L0w5gPmfOAgJ8a1PY9hoXMtKL8uzDNcAAGTE6u8KLq0PtJgGKt"), // live
	//QBClientSecret("hpqSbPLupDpVJ2NnzsWTqcCzyKyc5iaMgetTi53T"), // live
 	//QBRedirectURI("https://www.waffor.com/login/getQuickBooksAuthu"); //live
 	QBClientId("L0WTe4MpDure7CkfkNHmgAJOn9zi98BSmdblekOxNTTV7OiBYO"), // test
 	QBClientSecret("LELKhdlfCnvrkMjpivS46Y4yNfCnTfv5tMWkEHxk"), // test
 	QBRedirectURI("http://localhost/quick/login/getQuickBooksAuthu"); //test
	
 	 
	String value;
	
	private DatabaseConfig(String value){
		this.value = value;
	}
	
	public String getValue(){
		return this.value;
	}
}
