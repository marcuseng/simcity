package mainCity.restaurants.jeffersonrestaurant.gui;

import javax.swing.*;

import mainCity.restaurants.jeffersonrestaurant.JeffersonCashierRole;
import mainCity.restaurants.jeffersonrestaurant.JeffersonCookRole;
import mainCity.restaurants.jeffersonrestaurant.JeffersonCustomerRole;
import mainCity.restaurants.jeffersonrestaurant.JeffersonHostRole;
import mainCity.restaurants.jeffersonrestaurant.JeffersonMarketRole;
import mainCity.restaurants.jeffersonrestaurant.JeffersonWaiterRole;

import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

/**
 * Panel in frame that contains all the restaurant information,
 * including host, cook, waiters, and customers.
 */
public class RestaurantPanel extends JPanel {

    //Host, cook, waiters and customers
  // private WaiterAgent waiter = new WaiterAgent("Sarah");
    private JeffersonHostRole host =new JeffersonHostRole ("Sal");
  // private WaiterGui waiterGui = new WaiterGui(waiter);
    private JeffersonCookRole cook = new JeffersonCookRole("Jim");
    private JeffersonCashierRole cashier = new JeffersonCashierRole("Dave");
    
    private JeffersonMarketRole m1 = new JeffersonMarketRole();
    private JeffersonMarketRole m2 = new JeffersonMarketRole();
    private JeffersonMarketRole m3 = new JeffersonMarketRole();
    private JeffersonMarketRole m4 = new JeffersonMarketRole();
    
    
    
    
  

    private Vector<JeffersonCustomerRole> customers = new Vector<JeffersonCustomerRole>();
    private Vector<JeffersonWaiterRole> waiters = new Vector<JeffersonWaiterRole>();

    private JPanel restLabel = new JPanel();
    private ListPanel customerPanel = new ListPanel(this, "Customers");
    private ListPanel waiterPanel = new ListPanel(this,"Waiters");
    
    private JPanel group = new JPanel();

    private RestaurantGui gui; //reference to main gui
    
    public void addWaiterToList(JeffersonWaiterRole w){
    	host.waiters.add(w);
    	
    }
    



    public RestaurantPanel(RestaurantGui gui) {
        this.gui = gui;
       // waiter.setGui(waiterGui);
        //waiter.setCook(cook);
       // waiter.setHost(host);
        //gui.animationPanel.addGui(waiterGui);
       // waiter.startThread();
        cook.startThread();
        m1.startThread();
        m2.startThread();
        m3.startThread();
        m4.startThread();
        
        
        m1.setCook(cook);
        m2.setCook(cook);
        m3.setCook(cook);
        m4.setCook(cook);
        
        m1.setCashier(cashier);
        m2.setCashier(cashier);
        m3.setCashier(cashier);
        m4.setCashier(cashier);
        
        cook.addMarket(m1);
        cook.addMarket(m2);
        cook.addMarket(m3);
        cook.addMarket(m4);
        
        CookGui cg = new CookGui(cook,gui);
        cook.setGui(cg);
        gui.animationPanel.addGui(cg);
        
        host.startThread();
        cashier.startThread();
        //host.waiters.add(waiter);
       // waiters.add(waiter);

        setLayout(new GridLayout(1, 2, 20, 20));
        group.setLayout(new GridLayout(1, 2, 10, 10));
        
        group.add(customerPanel);
        group.add(waiterPanel);

        initRestLabel();
        add(restLabel);
        add(group);
    }

    /**
     * Sets up the restaurant label that includes the menu,
     * and host and cook information
     */
    private void initRestLabel() {
        JLabel label = new JLabel();
        restLabel.setLayout(new BoxLayout((Container)restLabel, BoxLayout.Y_AXIS));
        restLabel.setLayout(new BorderLayout());
      label.setText(
              "<html><h3><u>Tonight's Staff</u></h3><table><tr><td>host:</td><td>" + "Sal" + "</td></tr></table><h3><u> Menu</u></h3><table><tr><td>Steak</td><td>$15.99</td></tr><tr><td>Chicken</td><td>$10.99</td></tr><tr><td>Salad</td><td>$5.99</td></tr><tr><td>Pizza</td><td>$8.99</td></tr></table><br></html>");

        restLabel.setBorder(BorderFactory.createRaisedBevelBorder());
        restLabel.add(label, BorderLayout.CENTER);
        restLabel.add(new JLabel("               "), BorderLayout.EAST);
        restLabel.add(new JLabel("               "), BorderLayout.WEST);
       
    }

    /**
     * When a customer or waiter is clicked, this function calls
     * updatedInfoPanel() from the main gui so that person's information
     * will be shown
     *
     * @param type indicates whether the person is a customer or waiter
     * @param name name of person
     */
    public void showInfo(String type, String name) {

        if (type.equals("Customers")) {

            for (int i = 0; i < customers.size(); i++) {
                JeffersonCustomerRole temp = customers.get(i);
                if (temp.getName() == name)
                    gui.updateInfoPanel(temp);
            }
        }
        if (type.equals("Waiters")) {

            for (int i = 0; i < waiters.size(); i++) {
                JeffersonWaiterRole temp = waiters.get(i);
                if (temp.getName() == name)
                    gui.updateInfoPanel(temp);
            }
        }
        
    }

    /**
     * Adds a customer or waiter to the appropriate list
     *
     * @param type indicates whether the person is a customer or waiter (later)
     * @param name name of person
     */
    /*
    public void addPerson(String type, String name) {

    	if (type.equals("Customers")) {
    		CustomerAgent c = new CustomerAgent(name);	
    		CustomerGui g = new CustomerGui(c, gui);

    		gui.animationPanel.addGui(g);// dw
    		c.setWaiter(waiter);
    		
    		c.setHost(host);
    		
    		c.setGui(g);
    		customers.add(c);
    		c.startThread();
    	}
    }
    */
    public void addPerson(String type, String name, boolean hungry) {
    	
    	
    	if (type.equals("Customers")) {
    		JeffersonCustomerRole c = new JeffersonCustomerRole(name);
    		c.setHost(host);
    		//host.waitingCustomers.add(c);
    		//for now only setting 1 waiter, not addlist
    		//host.setWaiter(waiter);
    		CustomerGui g = new CustomerGui(c, gui);
    		if (hungry){g.setHungry();}

    		gui.animationPanel.addGui(g);// dw
    	//	c.setWaiter(waiter);
    		c.setGui(g);
    		
    		customers.add(c);
    		c.startThread();
    		if(name.equalsIgnoreCase("broke")){
    			c.setMoney(0);	
    		}
    		
    		else if(name.equalsIgnoreCase("poor")){
    			c.setMoney(5.99);
    			
    		}
    		else if(name.equalsIgnoreCase("cheapskate")){
    			c.setMoney(-1);
    		}
    		else{
    			c.setMoney(100);
    		}
    		
    		
    		
    	}
    	if (type.equals("Waiters")) {
    		JeffersonWaiterRole w =  new JeffersonWaiterRole(name);
        	WaiterGui waiterGui = new WaiterGui(w);
        	w.setCook(cook);
            w.setHost(host);
            w.setCashier(cashier);
            gui.animationPanel.addGui(waiterGui);
    		w.setGui(waiterGui);
    		waiters.add(w);
    		waiterGui.setOrigin(waiters.size()*25 + 50, 170);
    		host.waiters.add(w);
    		
    		System.out.println("waiter added");
            w.startThread();
            //host.msgWaitersUpdate();
            //host.msgWaiterAdded();
    	}
    	
    }
    /*
    public void addWaiter(String waitername){
    	WaiterAgent w =  new WaiterAgent(waitername);
    	WaiterGui waiterGui = new WaiterGui(w);
    	
        w.setGui(waiterGui);
        w.setCook(cook);
        w.setHost(host);
        host.waiters.add(w);
        gui.animationPanel.addGui(waiterGui);
        waiters.add(w);
        System.out.println("waiter added");
        w.startThread();
    	
    }
   */ 
}
