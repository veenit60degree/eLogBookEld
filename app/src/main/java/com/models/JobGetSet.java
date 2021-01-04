package com.models;

public class JobGetSet {

	String Name, Address, StateCode, CountryCode, LoadId, LoadNumber, jobId, IsCustomeBrokerCleared, IsRead ;
	
	
	public JobGetSet(String Name,String Address,String StateCode,String CountryCode,
			String LoadId,String LoadNumber,String IsRead, String jobId, String IsCustomeBrokerCleared){
		this.Name = Name;
		this.Address = Address;
		this.StateCode = StateCode;
		this.CountryCode = CountryCode;
		this.LoadId = LoadId;
		this.LoadNumber = LoadNumber;
		this.IsRead = IsRead;
		this.jobId = jobId;
		this.IsCustomeBrokerCleared = IsCustomeBrokerCleared;
		
	}


	public String getName() {
		return Name;
	}


	public void setName(String name) {
		Name = name;
	}


	public String getAddress() {
		return Address;
	}


	public void setAddress(String address) {
		Address = address;
	}


	public String getStateCode() {
		return StateCode;
	}


	public void setStateCode(String stateCode) {
		StateCode = stateCode;
	}


	public String getCountryCode() {
		return CountryCode;
	}


	public void setCountryCode(String countryCode) {
		CountryCode = countryCode;
	}


	public String getLoadId() {
		return LoadId;
	}


	public void setLoadId(String loadId) {
		LoadId = loadId;
	}


	public String getLoadNumber() {
		return LoadNumber;
	}


	public void setLoadNumber(String loadNumber) {
		LoadNumber = loadNumber;
	}


	public String getIsRead() {
		return IsRead;
	}


	public void setIsRead(String isRead) {
		IsRead = isRead;
	}


	public String getJobId() {
		return jobId;
	}

	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

	public String getIsCustomeBrokerCleared() {
		return IsCustomeBrokerCleared;
	}

	public void setIsCustomeBrokerCleared(String isCustomeBrokerCleared) {
		IsCustomeBrokerCleared = isCustomeBrokerCleared;
	}

}
