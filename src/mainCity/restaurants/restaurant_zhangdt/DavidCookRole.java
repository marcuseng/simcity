package mainCity.restaurants.restaurant_zhangdt;

import agent.Agent;
import mainCity.contactList.ContactList;
import mainCity.restaurants.restaurant_zhangdt.DavidMarketRole;
import mainCity.restaurants.restaurant_zhangdt.DavidCustomerRole.AgentEvent;
import mainCity.restaurants.restaurant_zhangdt.DavidCustomerRole.AgentState;
import mainCity.restaurants.restaurant_zhangdt.DavidWaiterRole.myCustomer;
import mainCity.restaurants.restaurant_zhangdt.gui.CookGui;
import mainCity.restaurants.restaurant_zhangdt.gui.WaiterGui;
import mainCity.restaurants.restaurant_zhangdt.gui.RestaurantGui;
import mainCity.restaurants.restaurant_zhangdt.interfaces.Cook;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.Semaphore;

public class DavidCookRole extends Agent implements Cook{
	
/*   Data   */ 
	
	private String name; 
	Timer cookTimer = new Timer(); 
	
	enum CookStatus
	{none, Opening, sendingOrder, Checked, massOrderReady, recievedOrder, NoFood, ordering} 
	CookStatus cstate = CookStatus.Opening; 
	
	enum OrderStatus 
	{none, Opening, pending, handledOrder, massOrderReady, cooking, cooked, done, NoFood }; 
	
	enum FoodStatus 
	{none, empty, requested, delivered, marketEmpty} 
	
	class Food {
		String Choice; 
		int quantity;
		int cookingTime; 
		int amountToOrder; 
		int low = 3; 
		FoodStatus fstate = FoodStatus.none; 
		
		public Food(String c, int q, int ct){
			Choice = c; 
			quantity = q; 
			cookingTime = ct; 
		}
	}
	
	//List<Food> Inventory = new ArrayList<Food>(); 
	Map<String, Food> Inventory = new TreeMap<String, Food>(); 
	Map<String, Integer> foodAvailableAtMarket = new TreeMap<String, Integer>(); 
	
	ContactList contactList;
	List<DavidMarketRole> marketAgent = new ArrayList<DavidMarketRole>();
	
	List<String> menu = new ArrayList<String>();
	
	String MarketOrder;
	int MarketChoice = 0;
	
	boolean cooking = false;
	
	class Order { 
		
		DavidWaiterRole Waiter; 
		String Choice; 
		int tableNum; 
		OrderStatus os; 
		
		public Order(DavidWaiterRole w, String choice2, int table) {
			Waiter = w; 
			Choice = choice2; 
			tableNum = table; 
		}
		
	}
	
	
	List<Order> pendingOrders = new ArrayList<Order>(); 
	
	DavidCashierRole cashier; 
	
	private CookGui gui = null;
	
	private Semaphore atGrill = new Semaphore(0,true);
	
	//Constructor
	public DavidCookRole(String name) { 
		super(); 
		this.name = name; 
		
		menu.add("steak");
		menu.add("chicken");
		menu.add("salad");
		menu.add("pizza");
		
		Inventory.put("steak", new Food("steak", 5000, 5));	//type, cookingTime, amount
        Inventory.put("pizza", new Food("pizza", 2500, 5));
        Inventory.put("pasta", new Food("pasta", 1000, 5));
        Inventory.put("soup", new Food("soup", 2000, 5));
        
        foodAvailableAtMarket.put("steak", 0);
        foodAvailableAtMarket.put("pizza", 0);
        foodAvailableAtMarket.put("pasta", 0);
        foodAvailableAtMarket.put("soup", 0);
	}
	
/*   Messages   */ 
	public void msgHereIsAnOrder (DavidWaiterRole w, String choice, int table) {
		print("msgHereIsAnOrder Called");
		//gui.DoMoveToFridge();
		Order newOrder = new Order(w, choice, table);
		newOrder.os = OrderStatus.pending; 
		pendingOrders.add(newOrder);
		stateChanged();
	}
	
	public void msgDoneCooking(Order o) {
		print(o.Choice + "done cooking..."); 
		o.os = OrderStatus.cooked; 
		stateChanged();
	}
	
	public void msgHereIsYourOrder (Map<String, Integer> inventoryFulfilled) {
		print("msgHereIsYourOrder Called"); 
		for (Map.Entry<String, Integer> entry : inventoryFulfilled.entrySet()){
			print("Had " + Inventory.get(entry.getKey()).quantity + " " + Inventory.get(entry.getKey()).Choice + "(s).");
			Inventory.get(entry.getKey()).quantity += entry.getValue();
			print("Now have " + Inventory.get(entry.getKey()).quantity + " " + Inventory.get(entry.getKey()).Choice + "(s).");
			Food f = Inventory.get(entry.getKey());
			f.fstate = FoodStatus.delivered;
		}
		stateChanged(); 
	}
	
	public void msgMarketOutOfOrder() { 
		print("msgMarketOutOfOrder"); 
		if(MarketChoice == 0) {
			MarketChoice = 1;
		}
		else if(MarketChoice == 1) {
			MarketChoice = 2;
		}
		else if(MarketChoice == 2) {
			print("Markets all unavailable"); 
			cstate = CookStatus.none;
			stateChanged();
		}
		else {
			cstate = CookStatus.sendingOrder;
			stateChanged();
		}
		
	}
	
	public void msgMassOrderReady() {
		print ("msgMassOrderReady recieved"); 
		cstate = CookStatus.massOrderReady;
		stateChanged();
	}
	
	public void msgFinishedMovingToGrill() { 
		if(atGrill.availablePermits() < 1){
			print ("msgFinishedMoving recieved"); 
			atGrill.release(); 
			stateChanged();
		}
	}
	

	
/*   Scheduler   */ 
	
	protected boolean pickAndExecuteAnAction() { 
		try{	
			if(cstate == CookStatus.Opening){
				CheckInventory();
				return true;
			}
			
			for(Order currentOrder : pendingOrders) {
				if(currentOrder.os == OrderStatus.pending && cooking == false){ 
					cookOrder(currentOrder);
					return true;
				}
				if(currentOrder.os == OrderStatus.cooked){ 
					print("Order is ready");
					OrderIsReady(currentOrder); 
					return true;
				}
			}

			return false; 
		}
		catch(ConcurrentModificationException r){ 
			return false;
		}
	}
	
/*   Actions   */
	
	public void CheckInventory(){
Map<String, Integer>lowInventory = new TreeMap<String, Integer>();
		
		for (String c : menu){
			if (Inventory.get(c).quantity <= Inventory.get(c).low){
				print("Adding " + Inventory.get(c).Choice + " to market order");
				Inventory.get(c).amountToOrder = 10;
				lowInventory.put(c, Inventory.get(c).amountToOrder);
				//OrderFromMarket(inventory.get(c), inventory.get(c).amountToOrder, inventory.get(c).nextMarket);
			}
		}
		if(!lowInventory.isEmpty()){
			OrderFromMarket(lowInventory);
		}
	}
	
	public void cookOrder(final Order o) { 
		print("cookOrder called by " + o.Waiter.getName());
		cooking = true;
		Map<String, Integer> inventoryNeeded = new TreeMap<String, Integer>(); 
		Food tempFood = Inventory.get(o.Choice);
		
		//quantity of food is low. Ordering 10 more
		if(tempFood.quantity <= tempFood.low) {
			inventoryNeeded.put(tempFood.Choice, 10); 
			OrderFromMarket(inventoryNeeded);
			
		}
		
		if(tempFood.quantity == 0) {
			print("Currently out of " + o.Choice);
			o.Waiter.msgOutOfFood(o.Choice);
			pendingOrders.remove(o);
			cooking = false;
			stateChanged();
		}
		
			o.os = OrderStatus.cooking; 
			tempFood.quantity --;
			
			for(int i=0; i<Inventory.size(); i++) {
				if(tempFood.Choice == Inventory.get(i).Choice){
					Inventory.get(i).quantity = tempFood.quantity; 
					print(Inventory.get(i).Choice + " quantity: " + Inventory.get(i).quantity);
				}
			}
			
			cookTimer.schedule(new TimerTask() {
				public void run() {
					print("Done Cooking");
					msgDoneCooking(o); 
				    //gui.DoMoveToPlating();
					cooking = false;
					stateChanged();
				}
			},
			tempFood.cookingTime);
			
	}
	
	public void OrderFromMarket(Map<String, Integer> inventory){ 
		contactList.getInstance().marketGreeter.msgINeedInventory("DavidRestaurant", this, cashier, inventory); 
	}

	
	public void OrderIsReady(Order o){ 
		for(int i=0; i<o.Waiter.customerList.size(); i++) {
			if(o.Waiter.customerList.get(i).t.getTable() == o.tableNum) {
				o.Waiter.msgOrderIsReady(o.Waiter.customerList.get(i), o);
			}
		}
		pendingOrders.remove(o);
		o.os = OrderStatus.done; 
		stateChanged();
	}
	
// utilities 
	
	public void setGui(CookGui RG) {
		gui = RG;
	}
	
	public void addMarket(DavidMarketRole m) {
		marketAgent.add(m);
	}
	
	public void addCashier(DavidCashierRole c) {
		cashier = c;
	}
	
	public void emptyInventory() {
		for(int i=0; i<Inventory.size(); i++) {
			Inventory.get(i).quantity = 0;
			print("Setting the quantity of " + Inventory.get(i).Choice + " to " + Inventory.get(i).quantity);
		}
	}
	
	public void emptySteak() {
		Inventory.get(0).quantity = 0;
		print("Setting the quantity of " + Inventory.get(0).Choice + " to " + Inventory.get(0).quantity);
	}
	
	public void emptyChicken() {
		Inventory.get(1).quantity = 0;
		print("Setting the quantity of " + Inventory.get(1).Choice + " to " + Inventory.get(1).quantity);
	}
	
	public void emptySalad() {
		Inventory.get(2).quantity = 0;
		print("Setting the quantity of " + Inventory.get(2).Choice + " to " + Inventory.get(2).quantity);
	}
	
	public void emptyPizza() {
		Inventory.get(3).quantity = 0;
		print("Setting the quantity of " + Inventory.get(3).Choice + " to " + Inventory.get(3).quantity);
	}
	
	public void ManualOrder() { 
		cstate = CookStatus.NoFood; 
		stateChanged();
	}

}
