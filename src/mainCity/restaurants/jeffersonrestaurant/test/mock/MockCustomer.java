package mainCity.restaurants.jeffersonrestaurant.test.mock;


import role.jeffersonRestaurant.JeffersonHostRole;
import role.jeffersonRestaurant.JeffersonWaiterRole;
import mainCity.restaurants.jeffersonrestaurant.Menu;
import mainCity.restaurants.jeffersonrestaurant.interfaces.Cashier;
import mainCity.restaurants.jeffersonrestaurant.interfaces.Customer;
import mainCity.restaurants.jeffersonrestaurant.interfaces.Waiter;
import mainCity.restaurants.jeffersonrestaurant.test.mock.EventLog;


public class MockCustomer extends Mock implements Customer {

	/**
	 * Reference to the Cashier under test that can be set by the unit test.
	 */
	public Cashier cashier;
	
	public JeffersonHostRole host;
	
	public EventLog log;

	public MockCustomer(String name) {
		super(name);

	}
	
	public void msgAnimationFinishedGoToSeat() {
		log.add(new LoggedEvent("Finished going to seat."));
		
	}

	
	public void msgWhatWouldYouLike() {
		log.add(new LoggedEvent("Asked what I would like."));
		
	}

	
	public void msgHereIsYourFood() {
		log.add(new LoggedEvent("Recieved message Here Is Your Food."));
		
	}

	
	public void msgAnimationFinishedLeaveRestaurant() {
		log.add(new LoggedEvent("Finished leaving restaurant."));
		
	}

	
	public void msgHereIsYourCheck() {
		log.add(new LoggedEvent("Recieved message Here is Your Check"));
		
	}

	
	public void msgNotAvailable() {
		log.add(new LoggedEvent("Recieved message that no table is available"));
		
	}

	
	public void msgRestaurantFullLeave() {
		log.add(new LoggedEvent("Told to leave restaurant beacause full"));
		
	}


	
	public void msgSitAtTable(int t, Menu m, JeffersonWaiterRole waiterAgent) {
		log.add(new LoggedEvent("Received msgSitAtTable"));
	}

	@Override
	public void msgSitAtTable(int t, Menu m, Waiter waiterAgent) {
		log.add(new LoggedEvent("Received msgSitAtTable"));
		
	}



	
	
	
}
