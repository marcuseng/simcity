package housing;

//import housing.houseAgent.Appliance;
//import housing.houseAgent.kitchenState;
//import housing.houseAgent.type;

import housing.Interfaces.Occupant;
import housing.Interfaces.landLord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



/**
 * Base class for simple agents
 */
public class personHome 
{
	Occupant occupant;
	landLord owner;
	String cookingMeal;
	Map<String, Integer> FoodSupply = new HashMap<String, Integer>();
	public List <Appliance> Appliances = new ArrayList<Appliance>();
	public List <Appliance> AAppliances = new ArrayList<Appliance>();

	List<String> needFood = new ArrayList<String>();
	public personHome(Occupant occ)
	{
		super();
		//homeType = home;
		occupant = occ;
		/*if(home == type.apartment)
		{
			owner = lndlrd;

		}*/
		FoodSupply.put("pasta" , 5);
		FoodSupply.put("fish", 1);
		FoodSupply.put("chickenSoup", 5);
		
		
	Appliances.add(new Appliance("stove" , true, false));
	Appliances.add(new Appliance("fridge" , true, false));
	Appliances.add(new Appliance("sink" , true, false));
	Appliances.add(new Appliance("TV", true, false));
	
	AAppliances.add(new Appliance("stove" , true, true));
	AAppliances.add(new Appliance("fridge" , true, true));
	AAppliances.add(new Appliance("sink" , true, true));
	AAppliances.add(new Appliance("TV", false, true));

		
	}
	
	
	
	
	public void applianceBroke()
	{
		System.out.println("house set appliance to broken");
		String appln = "sink";
		switch((int) (Math.random() * 4)) {
		case 0:
			appln = "TV";		
			break;
		case 1:
			appln = "sink";
			break;
		case 2:
			appln = "fridge";
			break;
		case 3:
			appln = "stove";
			break;
		}
		for(Appliance a : Appliances )
		{
			if(a.appliance.equals(appln))
			a.setWorking(false);
		}
		
	}
	
	
	
//ACTIONS
	
	
	
	
	public void checkSupplies(String meal)
	{
		if(FoodSupply.get(meal) == 0)
		{
			
			needFood.add(meal);
			occupant.msgNeedFood(needFood);
			System.out.println("NEEDS TO GO TO MARKET TO BY FOOD");
			
		}
		else
		{
			occupant.msgCookFood(meal);
			System.out.println("HAS FOOD, WILL COOK A MEAL");

			if(FoodSupply.containsKey(meal))
			{
				int amt = FoodSupply.get(meal);
				FoodSupply.put(meal, amt-1);
			}
		}
		
	}
	
	public void CheckAppliances(boolean renter)
	{
		if(!renter)
		{
		for (Appliance appl : Appliances)
		{
			if(appl.isWorking() == false)
			{
				occupant.msgNeedsMaintenance(appl.appliance);
				
			}
		}
		}
		
		if(renter)
		{
		for (Appliance appl : AAppliances)
		{
			if(appl.isWorking() == false)
			{
				occupant.msgNeedsMaintenance(appl.appliance);
				
			}
		}
		}
		
		
	}
	
	public void GroceryListDone()
	{
		if(needFood.size() != 0)
		{
			for(String f : needFood)
			{
				FoodSupply.put(f, 1);
				System.out.println("replenish food supply in kitchen");
				needFood.remove(f);
			}				
			System.out.println("no other food needed");

		}
	}
	
	public void setOccupant(Occupant oc)
	{
		this.occupant = oc;
	}
	
	
	public List<Appliance> getAList()
	{
		return Appliances;
	}
	
	public List<Appliance> getAAList()
	{
		return AAppliances;
	}
	

//Inner class for the different appliances in the kitchen
	
	
	public class Appliance
	{
		//DATA
		
			String appliance;
			private boolean working;
			boolean apt;
			int yPos;
			int xPos;
			
			
			public Appliance (String nm, boolean wrk, boolean apartment)
			{
				appliance = nm; 
				setWorking(wrk);
				apt = apartment;
			
			if(!apartment)
			{	
				if(nm.equals("stove"))
				{
					xPos = 200;
					yPos = 25;
					
				}
				if(nm.equals("fridge"))
				{
					xPos = 300;
					yPos = 25;
				}
				if(nm.equals("sink"))
				{
					xPos = 250;
					yPos = 25;
				}
				if(nm.equals("TV"))
				{
					xPos = 70;
					yPos = 80;
				}
			}
			
			if(apartment)
			{
				if(nm.equals("stove"))
				{
					xPos = 350;
					yPos = 15;
					
				}
				if(nm.equals("fridge"))
				{
					xPos = 450;
					yPos = 15;
				}
				if(nm.equals("sink"))
				{
					xPos = 400;
					yPos = 15;
				}
				if(nm.equals("TV"))
				{
					xPos = 65;
					yPos = 10;
				}
				
			}
				
			}
			public String getName()
			{
				return appliance;
			}
			
			public int getXPos()
			{
				return xPos;
			}
			public int getYPos()
			{
				return yPos;
			}
			public boolean isWorking() {
				return working;
			}
			public void setWorking(boolean working) {
				this.working = working;
			}
			
			
	}


}





