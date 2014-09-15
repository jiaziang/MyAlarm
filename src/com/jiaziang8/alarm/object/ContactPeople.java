package com.jiaziang8.alarm.object;

public class ContactPeople {
	private String name ;
	private String pinYinName;
	private String number;
	  public ContactPeople(String name) {  
	        super();  
	        this.name = name;  
	    }  
	  
	    public ContactPeople(String name, String pinYinName) {  
	        super();  
	        this.name = name;  
	        this.pinYinName = pinYinName;  
	    }  
	    
	    public ContactPeople(String name,String pinYinName,String number){
	    	super();
	    	this.name = name;
	    	this.pinYinName = pinYinName;
	    	this.number = number;
	    }
	    
	  
	    public String getName() {  
	        return name;  
	    }  
	  
	    public void setName(String name) {  
	        this.name = name;  
	    }  
	  
	    public String getPinYinName() {  
	        return pinYinName;  
	    }  
	  
	    public void setPinYinName(String pinYinName) {  
	        this.pinYinName = pinYinName;  
	    }  
	    
	    public void setNumber(String number){
	    	this.number = number;
	    }
	    
	    public String getNumber(){
	    	return number;
	    }
}
