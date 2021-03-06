package mainCity.restaurants.marcusRestaurant.gui;

import javax.imageio.ImageIO;
import javax.swing.*;

import mainCity.contactList.ContactList;
import mainCity.gui.CityCard;
import mainCity.gui.CityGui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

public class MarcusAnimationPanel extends CityCard implements ActionListener {
	private MarcusRestaurantPanel restaurant = new MarcusRestaurantPanel(this);
    private final int WINDOWX = 500;
    private final int WINDOWY = 410;
    private static final int x = 100;
    private static final int y = 260;
    private static final int w = 50;
    private static final int h = 50;
    private static final int tableCount = 4;
   
    private Image bufferImage;
    private Dimension bufferSize;

    private BufferedImage resttableImg = null;
    
    private List<Gui> guis = new ArrayList<Gui>();
    private List<Gui> personGuis = new ArrayList<Gui>();

    public MarcusAnimationPanel(CityGui gui) {
    	super(gui);
    	ContactList.getInstance().setMarcusRestaurant(restaurant);
    	setSize(WINDOWX, WINDOWY);
        setVisible(true);
        StringBuilder path = new StringBuilder("imgs/");
        try {
			resttableImg = ImageIO.read(new File(path.toString() + "resttable.png"));
		} catch (IOException e) {
			
			e.printStackTrace();
		}
        bufferSize = this.getSize();
        setLayout(null);
        
    	Timer timer = new Timer(10, this );
    	timer.start();
    }

	public void actionPerformed(ActionEvent e) {
		repaint();  //Will have paintComponent called
	}

    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;
        Graphics2D kitchen = (Graphics2D)g;
        
        //Clear the screen by painting a rectangle the size of the frame
        g2.setColor(getBackground());
	    g2.fillRect(0, 0, WINDOWX, WINDOWY );
	
	    for(int i = 0; i < tableCount; i++) {
	     	g2.setColor(Color.ORANGE);
	        //g2.fillRect(x+100*i, y, w, h);
	        g.drawImage(resttableImg,x+100*i, y,null);
	    }
	        
	    kitchen.setColor(Color.GRAY);
	    kitchen.fillRect(160, 0, 95, 45);
        kitchen.setColor(Color.GRAY);
	    kitchen.fillRect(255, 15, 20, 30);
        
	    for(int i = 0; i < tableCount; i++) {
	    	kitchen.setColor(Color.WHITE);
	        kitchen.fillRect(170 + 20*i, 1, 15, 15);
	    }

        for(Gui gui : personGuis) {
            if (gui.isPresent()) {
                gui.updatePosition();
            }
        }
        
	    for(Gui gui : guis) {
	        if (gui.isPresent()) {
	            gui.draw(g2);
	        }
	    }
	    
	    for(Gui gui : personGuis) {
            if (gui.isPresent()) {
	            gui.draw(g2);
            }
        }
    }

    public void backgroundUpdate() {
    	for(Gui gui : personGuis) {
            if (gui.isPresent()) {
                gui.updatePosition();
            }
        }
    }
    
    public void addGui(CustomerGui gui) {
        personGuis.add(gui);
    }

    public void addGui(WaiterGui gui) {
        personGuis.add(gui);
    }
    
    public void addGui(CookGui gui) {
    	personGuis.add(gui);
    }
    
    @Override
    public void clearPeople() {
    	personGuis.clear();
    }
}
