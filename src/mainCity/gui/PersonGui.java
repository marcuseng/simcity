package mainCity.gui;

import mainCity.PersonAgent;
import mainCity.PersonAgent.CityLocation;
import mainCity.contactList.ContactList;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.imageio.ImageIO;

public class PersonGui implements Gui{
	CityGui gui;
	private PersonAgent agent = null;
	private int xPos, yPos;
	private int xDestination, yDestination;
	private static final int w = 20;
	private static final int h = 20;
	private boolean isPresent = false;
	private boolean isVisible = true;
	private boolean traveling = false;
	private BufferedImage personImg = null;
	private ArrayList<Coordinate> corners = new ArrayList<Coordinate>();
	private LinkedList<Coordinate> path = new LinkedList<Coordinate>();
	
	public PersonGui(PersonAgent p, CityGui g) {
		agent = p;
		this.gui = g;
		xDestination = xPos = 0;
		yDestination = yPos = 0;
		StringBuilder path = new StringBuilder("imgs/");
		try {
			personImg = ImageIO.read(new File(path.toString() + "person.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		corners.add(new Coordinate(105, 125));
		corners.add(new Coordinate(105, 330));
		corners.add(new Coordinate(175, 125));
		corners.add(new Coordinate(175, 330));
		corners.add(new Coordinate(345, 125));
		corners.add(new Coordinate(345, 330));
		corners.add(new Coordinate(415, 125));
		corners.add(new Coordinate(415, 330));
		corners.add(new Coordinate(585, 125));
		corners.add(new Coordinate(585, 330));
		corners.add(new Coordinate(655, 125));
		corners.add(new Coordinate(655, 330));
	}

	public void updatePosition() {
		if(xPos == xDestination && yPos == yDestination && traveling) {
			xDestination = path.peek().x;
			yDestination = path.poll().y;
		}
		
		if (xPos < xDestination)
			xPos++;
		else if (xPos > xDestination)
			xPos--;

		if (yPos < yDestination)
			yPos++;
		else if (yPos > yDestination)
			yPos--;
		
		if (path.isEmpty() && traveling) {
			traveling = false;
			agent.msgAtDestination();
		}
	}
	
	public void draw(Graphics2D g) {
		if(isVisible) {
			g.setColor(Color.ORANGE);
			g.drawImage(personImg, xPos,yPos, null);
			//g.fillRect(xPos, yPos, w, h);
		}
	}

	public boolean isPresent() {
		return true;
	}

	public void setPresent(boolean p) {
		isPresent = p;
	}

	public void DoGoToLocation(PersonAgent.CityLocation destination) {
		switch(destination) {
			case restaurant_marcus:
				calculatePath(105, 180);
				xDestination = path.peek().x;
				yDestination = path.poll().y;
				break;
			case restaurant_ellen:
				calculatePath(105, 280);
				xDestination = path.peek().x;
				yDestination = path.poll().y;
				break;
			case restaurant_ena:
				calculatePath(347, 180);
				xDestination = path.peek().x;
				yDestination = path.poll().y;
				break;
			case restaurant_jefferson:
				calculatePath(347, 280);
				xDestination = path.peek().x;
				yDestination = path.poll().y;
				break;
			case restaurant_david: 
				calculatePath(585, 230);
				xDestination = path.peek().x;
				yDestination = path.poll().y;
				break;
			case market:
				calculatePath(415, 215);
				xDestination = path.peek().x;
				yDestination = path.poll().y;
				break;
			case bank:
				calculatePath(175, 230);
				xDestination = path.peek().x;
				yDestination = path.poll().y;
				break;
			case home:
				calculatePath(100, 500);
				xDestination = path.peek().x;
				yDestination = path.poll().y;
				break;
			default:
				calculatePath(0, 0);
				xDestination = path.peek().x;
				yDestination = path.poll().y;
				break;
		}		
	}
	
	public void DoGoToStop() {
		System.out.println("Gui is told to go to nearest bus stop");

		//Looking for stop that is the minimum distance.
		PersonAgent.CityLocation destination = findNearestStop();
		
		System.out.println("Walking toward " + destination);
		
		switch(destination) {
		case restaurant_marcus:
			xDestination = 130;
			yDestination = 180;
			break;
		case restaurant_ellen:
			xDestination = 130;
			yDestination = 280;
			break;
		case restaurant_david:
			xDestination = 635;
			yDestination = 230;
			break;
		case restaurant_ena:
			xDestination = 260;
			yDestination = 80;
			break;
		case restaurant_jefferson:
			xDestination = 220;
			yDestination = 380;
			break;
		case market:
			xDestination = 455;
			yDestination = 80;
			break;
		case bank:
			xDestination = 130;
			yDestination = 230;
			break;
		case home:
			xDestination = 320;
			yDestination = 80;
			break;
		default:
			xDestination = 0;
			yDestination = 0;
			break;
	}
		traveling = true;
	}
	
	public void DoGoInside() {
		isVisible = false;
	}
	
	public void DoGoOutside() {
		isVisible = true;
	}
	
	public int getX() {
		return xDestination;
	}
	
	public int getY() {
		return yDestination;
	}
	
	public CityLocation findNearestStop(){ 
		
		//starts off with first bus stop
		//measures absolute value of difference in x and y between person's current location and bus stop's location
		//sets destination to the stop
		int distance = Math.abs(xPos - ContactList.stops.get(0).xLocation) + (Math.abs(yPos - ContactList.stops.get(0).yLocation));
		PersonAgent.CityLocation destination = ContactList.stops.get(0).stopLocation;
		
		//goes through list of bus stops to find nearest stop
		for(int i=1; i < ContactList.stops.size(); i++) { 
			int tempdistance = Math.abs(xPos - ContactList.stops.get(i).xLocation) 
								+ (Math.abs(yPos - ContactList.stops.get(i).yLocation)); 
			if(tempdistance < distance){ 
				destination = ContactList.stops.get(i).stopLocation;
			}
		}
		
		return destination;
	}
	
	private void calculatePath(int destX, int destY) {
		Coordinate current = new Coordinate(xPos, yPos);
		Coordinate destination = new Coordinate(destX, destY);
		TreeMap<Integer, Coordinate> nodes = new TreeMap<Integer, Coordinate>();
		
		while(current.x != destination.x || current.y != destination.y) {
			for(int i = 0; i < corners.size(); ++i) {
				nodes.put(getDistance(current, corners.get(i)), corners.get(i));
			}
			
			nodes.put(getDistance(current, destination), destination);
			
			Coordinate node1 = (Coordinate) nodes.pollFirstEntry().getValue();
			Coordinate node2 = (Coordinate) nodes.pollFirstEntry().getValue();
			Coordinate node3 = (Coordinate) nodes.pollFirstEntry().getValue();
			
			if(node1 == destination || node2 == destination || node3 == destination) {
				path.add(destination);
				traveling = true;
				nodes.clear();
				return;
			}
			
			Coordinate pathNext = node1;
			if(getDistance(pathNext, destination) > getDistance(node2, destination)) {
				pathNext = node2;
			}
			if(getDistance(pathNext, destination) > getDistance(node3, destination)) {
				pathNext = node3;
			}
			
			current = pathNext;
			path.add(pathNext);			
			nodes.clear();
		}
		
		for(int i = 0; i < path.size(); i++) {
			System.out.println(path.get(i));
		}
		
		traveling = true;
	}
	
	private int getDistance(Coordinate a, Coordinate b) {
		return ((int) Math.sqrt(Math.pow((Math.abs(a.x - b.x)), 2) + Math.pow((Math.abs(a.y - b.y)), 2)));
	}
	
	public class Coordinate {
		int x;
		int y;
		
		Coordinate(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}
}