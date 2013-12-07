package mainCity.market1.test.mock;


import java.util.Map;

import mainCity.market1.*;
import mainCity.market1.interfaces.*;

public class MockCashier extends Mock implements MarketCashier {
	public DeliveryMan1 deliveryMan;
    public Employee employee;
    public Greeter greeter;

    public MockCashier(String name) {
            super(name);

    }
    
    public void setGreeter(Greeter g){
    }
    public void deductCash(double sub){
    }
    
	public void msgComputeBill(Map<String, Integer> inventory, Customer c, Employee e){
		log.add(new LoggedEvent("Received msgComputeBill from " + e.getName() + " for " + c.getName()));
	}
	public void msgComputeBill(Map<String, Integer> inventory, String name, Employee e){
		
	}
	@Override
	public void msgHereIsPayment(double amount, Customer cust){
		log.add(new LoggedEvent("Received msgHereIsPayment from " + cust.getName() + " for $" + amount));
	}
	@Override
	public void msgPleaseRecalculateBill(Customer cust){
		log.add(new LoggedEvent("Received msgPleaseRecalculateBill from " + cust.getName()));
	}
	@Override
	public void msgChangeVerified(Customer cust){
		log.add(new LoggedEvent("Received msgChangeVerified from " + cust.getName()));
	}
	public void msgHereIsMoneyIOwe(Customer cust, double amount){
		
	}
}