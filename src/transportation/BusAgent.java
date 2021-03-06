package transportation;

import java.util.*;
import java.util.concurrent.Semaphore;

import transportation.gui.BusGui;
import agent.Agent;
import mainCity.PersonAgent;
import mainCity.PersonAgent.CityLocation;
import mainCity.contactList.ContactList;
import mainCity.test.LoggedEvent;
import mainCity.test.EventLog;

public class BusAgent extends Agent{

		/** Data **/
			
			BusGui gui; 
			Timer stopTimer = new Timer(); 
		
			public List<PersonAgent> Passengers = Collections.synchronizedList(new ArrayList<PersonAgent>()); 
			int capacity = 50; 
			CityLocation currentLocation; 
			CityLocation destination = CityLocation.restaurant_marcus;
			
			public enum BusState
			{none, ArrivedAtBusStop, ReadyToGo}; 	
			public BusState currentState = BusState.ReadyToGo; 
			
			int DestinationX, DestinationY; 
			private Semaphore atDestination = new Semaphore(0, true); 
			
			public EventLog log = new EventLog(); 
			
			public BusAgent() { 
			}
			
		/** Messages  **/ 
			public void msgIWantToGetOnBus(PersonAgent p){ 
				log.add(new LoggedEvent(p.getName() + " wants to get on bus"));
				System.out.println(p.getName() + " getting on bus. My destination is " + p.getDestination());
				Passengers.add(p);
				
			}
			
			public void msgImGettingOffBus(PersonAgent p){ 
				log.add(new LoggedEvent(p.getName() + " wants to get off bus"));
				System.out.println(p.getName() + " getting off bus."); 
				Passengers.remove(p);
			}
			
			public void msgAtBusStop(CityLocation cl){
				log.add(new LoggedEvent("At bus stop near " + cl));
				currentLocation = cl;
				currentState = BusState.ArrivedAtBusStop; 
				stateChanged();
			}

		/** Scheduler **/ 
			
			public boolean pickAndExecuteAnAction() {
				if(currentState == BusState.ArrivedAtBusStop){ 
					DropOffAndLoadPassengers(); 
					return true;
				}
				
				
				return false;
			}
			
		/** Actions **/  
			public void DropOffAndLoadPassengers() { 
				//People who want to get on the bus are already added to the list, so mostly just gui stuff, timer for persons to have time to get on bus, etc.
				gui.atBusStop = true;
				currentState = BusState.ReadyToGo;
				
				//System.out.println("But currently at " + currentLocation);
				for(int i=0; i<ContactList.stops.size(); i++){ 
					if(ContactList.stops.get(i).stopLocation == currentLocation) { 
						if(ContactList.stops.get(i).waitingPeople.size() != 0) {
							ContactList.stops.get(i).BusHasArrived(this, capacity);
							System.out.println("At: " + ContactList.stops.get(i).stopLocation);
						} 
					}
				}
				
				//Tell passengers that destination has been reached. 
				if(Passengers.size() != 0){
					synchronized(Passengers){
						for(int j=0; j<Passengers.size(); j++){ 
							if(Passengers.get(j).getDestination() == currentLocation) {
								Passengers.get(j).msgArrivedAtDestination();
							}
						}
					}
					//remove passengers who left the bus from the passenger list. 
				}

				gui.atBusStop = false;
				stateChanged();
	
			}
			
		public void setGui(BusGui bg){ 
			gui = bg;
		}
							
}


