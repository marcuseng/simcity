package role.marcusRestaurant;

import mainCity.PersonAgent;
import mainCity.contactList.ContactList;
import mainCity.gui.trace.AlertLog;
import mainCity.gui.trace.AlertTag;
import mainCity.interfaces.WorkerRole;
import mainCity.restaurants.marcusRestaurant.MarcusTable;
import mainCity.restaurants.marcusRestaurant.sharedData.*;
import mainCity.restaurants.marcusRestaurant.interfaces.*;

import java.util.*;

import role.Role;
import role.market.MarketGreeterRole;

/**
 * Restaurant Cook Agent
 */

public class MarcusCookRole extends Role implements Cook, WorkerRole {
	private CookGuiInterface cookGui;
	private String name;
	private List<MarketGreeterRole> markets;
	private int tracker, selector;
	private List<Order> orders;
	private Map<String, Food> foods;
	private RevolvingStand stand;
	private boolean onDuty;

	Timer timer = new Timer();
	public enum CookStatus {normal, checkingStand, lowFood, checkingFulfillment};
	private CookStatus status;
	private String order;
	private int grill;
	private boolean needGui;

	public MarcusCookRole(PersonAgent p, String n) {
		super(p);
		this.name = n;
		orders = Collections.synchronizedList(new ArrayList<Order>());
		markets = Collections.synchronizedList(new ArrayList<MarketGreeterRole>());
		status = CookStatus.normal;
		onDuty = true;
		
		foods = Collections.synchronizedMap(new HashMap<String, Food>());
		
		synchronized(foods) {
			foods.put("Swiss", new Food("Swiss", 7500, 4, 7, 20));//Name, CookTime, Quantity, Threshold, Capacity
			foods.put("American", new Food("American", 5500, 3, 4, 20));
			foods.put("Cheddar", new Food("Cheddar", 3500, 3, 5, 20));
			foods.put("Provolone", new Food("Provolone", 6000, 4, 5, 20));
		}
		
		tracker = 0;
		selector = 0;
		grill = 0;
		
		needGui = false;
		
		if(ContactList.getInstance().getMarket() != null && ContactList.getInstance().getMarket2() != null) {
			markets.add(ContactList.getInstance().getMarket().getGreeter());
			markets.add(ContactList.getInstance().getMarket2().getGreeter());
		}
	}

	public void setGui(CookGuiInterface g) {
		cookGui = g;
		cookGui.setPresent(true);
	}
	
	public void setStand(RevolvingStand s) {
		this.stand = s;
	}
	
	public List<Order> getOrders() {
		return orders;
	}

	// Messages
	public void msgHereIsAnOrder(Waiter w, String choice, MarcusTable t) {
		output("msgHereIsAnOrder: received an order of "+ choice + " for table " + t.getTableNumber());
		orders.add(new Order(w, choice, t.getTableNumber()));
		cookGui.DoGoToCounter();
		stateChanged();
	}
	
	public void msgHereIsYourOrder(Map<String, Integer> inventory) {
		output("Received my order from the market!");

		for(Map.Entry<String, Integer> entry : inventory.entrySet()) {
			foods.get(entry.getKey()).addQuantity(entry.getValue());
		}
		
		status = CookStatus.checkingFulfillment;
		stateChanged();
	}
	
	public void msgGoOffDuty(double amount) {
		addToCash(amount);
		onDuty = false;
		stateChanged();
	}
	
	public void msgCheckStand() {
		if(status == CookStatus.normal) {
			status = CookStatus.checkingStand;
			stateChanged();
		}
	}
	
	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	public boolean pickAndExecuteAnAction() {
		if(needGui) {
			ContactList.getInstance().setMarcusCook(this);
			cookGui.guiAppear();
			needGui = false;
		}
		synchronized(markets) {
			if(status == CookStatus.lowFood) {
				if(tracker < markets.size()) {
					orderFromMarket();
				}
				else {
					tracker = 0;
					status = CookStatus.normal;
				}
				return true;
			}
		}
		
		if(status == CookStatus.checkingFulfillment) {
			checkFulfillment();
			return true;
		}
		
		synchronized(orders) {
			if(!orders.isEmpty()) {
				for(Order o : orders) {
					if(o.status == OrderStatus.cooked) {
						callWaiter(o);
						return true;
					}
					else if(o.status == OrderStatus.pending) {
						o.status = OrderStatus.cooking;
						cookOrder(o);
						return true;
					}
				}
			}
		}
		
		if(status == CookStatus.checkingStand) {
			checkStand();
			return true;
		}
		
		if(!onDuty) {
			cookGui.DoLeaveRestaurant();
			super.setInactive();
			onDuty = true;
			needGui = true;
		}

		return false;
	}

	//Actions
	private void cookOrder(Order o) {
		synchronized(foods) {
			Food f = foods.get(o.choice);
		
			if(f.amount == 0) {
				output("Looks like we're all out of " + o.choice + ", Telling waiter " + o.waiter);
				o.waiter.msgOutOfFood(o.table, o.choice);
				order = o.choice;
				status = CookStatus.lowFood;
				orders.remove(o);
				return;
			}
			
			f.amount--;
			
			if(f.amount < f.threshold) {
				output("I'm running low on " + o.choice);
				order = o.choice;
				status = CookStatus.lowFood;
			}
			
			output("Cooking order: Grilled cheese with " + o.choice + " with " + f.amount + " left");
			o.grill = (++grill) % 4;
			cookGui.DoGoToGrill(o.grill);
			timer.schedule(new CookTimer(o), foods.get(o.choice).cookTime);
		}
	}
	
	private void callWaiter(Order o) {
		output("Calling back waiter for table " + o.table);
		o.waiter.msgOrderIsReady(o.table, o.choice);
		orders.remove(o);
	}
	
	private void checkFulfillment() {
		synchronized(foods) {
			Food f = foods.get(order);
			if(f.amount < f.threshold) {
				status = CookStatus.lowFood;
				selector = (selector+1) % markets.size();
				tracker++;
	
				stateChanged();
				return;
			}
			
			status = CookStatus.normal;
			tracker = 0;
		}
	}
	
	private void orderFromMarket() {
		//output("Sending an order to Market #" + selector);
		
		synchronized(foods) {
			Map<String, Integer> inventoryOrder = new HashMap<String, Integer>();
			for (Map.Entry<String, Food> entry : foods.entrySet()) {
				Food item = entry.getValue();
				if(item.amount < item.threshold) {
					inventoryOrder.put(item.name, item.capacity-item.amount);
				}
			}
			
			if(markets.get(selector) != null) {	
				markets.get(selector).msgINeedInventory("marcusRestaurant", inventoryOrder);
			}
			
			status = CookStatus.normal;
		}
	}
	
	private void checkStand() {
		status = CookStatus.normal;		
		cookGui.DoGoToCounter();
		
		if(stand.isEmpty()) {
			return;
		}
		
		output("There's orders on the stand. Processing...");
		while(!stand.isEmpty()) {
			OrderTicket temp = stand.remove();
			orders.add(new Order(temp.getWaiter(), temp.getChoice(), temp.getTable().getTableNumber()));
			stateChanged();
		}
	}
	
	public void depleteInventory(){
		for (Map.Entry<String, Food> entry : foods.entrySet()){
			entry.getValue().amount = 0;
		}

		status = CookStatus.lowFood;
	}
	
	public String toString() {
		return "Cook " + name;
	}
	
	public enum OrderStatus {pending, cooking, cooked};
	public class Order {
		Waiter waiter;
		String choice;
		int table;
		public OrderStatus status;
		int grill;

		Order(Waiter w, String c, int t) {
			waiter = w;
			choice = c;
			table = t;
			status = OrderStatus.pending;
		}
	}
	
	class Food {
		String name;
		int cookTime;
		int amount;
		final int threshold;
		final int capacity;
		
		Food(String n, int time, int a, int thresh, int cap) {
			this.name = n;
			this.cookTime = time;
			this.amount = a;
			this.threshold = thresh;
			this.capacity = cap;
		}
		
		void addQuantity(int q) {
			amount += q;
		}
	}
	
	class CookTimer extends TimerTask {
		Order timedOrder;
		
		public CookTimer(Order o) {
			timedOrder = o;
		}
		
		public void run() {
			System.out.println(timedOrder.choice + " is ready!");
			timedOrder.status = OrderStatus.cooked;
			cookGui.DoClearGrill(timedOrder.grill);
			cookGui.DoGoToCounter();
			stateChanged();
		}
	}
	
	private void output(String input) {
		AlertLog.getInstance().logMessage(AlertTag.MARCUS_RESTAURANT, this.getName(), input);
		AlertLog.getInstance().logMessage(AlertTag.MARCUS_COOK, this.getName(), input);
	}
}
