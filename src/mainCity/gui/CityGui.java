package mainCity.gui;

import javax.swing.*;

import mainCity.gui.AnimationPanel;
//import mainCity.restaurants.restaurant_zhangdt.gui.RestaurantGui;
import mainCity.market.gui.*;
import mainCity.restaurants.EllenRestaurant.gui.*;
import mainCity.contactList.*;

import java.awt.*;
import java.awt.event.*;

public class CityGui extends JFrame{
	
	private AnimationPanel animationPanel = new AnimationPanel(); 
	public ContactList contactList = new ContactList();
	
	private CityPanel cityPanel = new CityPanel(this); 
	//private MarketGui marketGui = new MarketGui();
	
	public CityGui() { 
		
		int WINDOWX = 1500; 
		int WINDOWY = 525; 
		
		setBounds(50, 50, WINDOWX, WINDOWY+150);
		setLayout(new BorderLayout());
		
		Dimension animationDim = new Dimension((int) (WINDOWX * .7), (int) (WINDOWY * .70));
        getAnimationPanel().setPreferredSize(animationDim);
        getAnimationPanel().setMinimumSize(animationDim);
        getAnimationPanel().setMaximumSize(animationDim);
        getAnimationPanel().setBorder(BorderFactory.createEtchedBorder());
        add(getAnimationPanel(), BorderLayout.CENTER);

        Dimension restDim = new Dimension((int) (WINDOWX* .3), (int) (WINDOWY * .70));
        cityPanel.setPreferredSize(restDim);
        cityPanel.setMinimumSize(restDim);
        cityPanel.setMaximumSize(restDim);
        add(cityPanel, BorderLayout.WEST);
        
        
        
        
	}
	
	public static void main(String[] args) {
        CityGui gui = new CityGui();
        gui.setTitle("csci201 Restaurant");
        gui.setVisible(true);
        gui.setResizable(false);
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        MarketGui marketGui = new MarketGui();
        marketGui.setVisible(true);
        //marketGui.setContactList(contactList);
        
        RestaurantGui ellenRestGui = new RestaurantGui();
        ellenRestGui.setVisible(true);
        
        //ellenRestGui.getRestPanel().setContactList(contactList);
                
        marketGui.getMarketPanel().getGreeter().addCook(ellenRestGui.getRestPanel().getCook());
        //contactList.addCook(cook);
        ellenRestGui.getRestPanel().getCook().setMarketGreeter(marketGui.getMarketPanel().getGreeter());
    }
	
	public AnimationPanel getAnimationPanel() {
		return animationPanel;
	}
}
