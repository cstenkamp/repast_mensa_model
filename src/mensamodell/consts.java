package mensamodell;

import java.util.ArrayList;
import java.util.List;


public class consts {

	//settings
	public final static int SIZE_X = 100;
	public final static int SIZE_Y = 60;
	public final static boolean MAY_LEAVE_WITHOUT_FOOD = false;
	
	

	//Theken
	public final static int AKTIONSTHEKE = 0;
	public final static int FLEISCHTHEKE = 1;
	public final static int POMMES = 2;
	public final static int VEGGIETHEKE = 3;
	public final static int EINTOPF = 4;
	public final static int SCHNELLERTELLER = 5;
	public final static int SALATBAR = 6;
	public final static int VEGANTHEKE = 7;

	//Foodpreferences
	public final static int MEAT = 0;
	public final static int VEGGIE = 1;
	public final static int VEGANER = 2;
	public final static int NOPREFERENCE = -1;
	
	//Essen
	public final static int ESSEN_MEAT = 0;
	public final static int ESSEN_VEGGIE = 1;
	public final static int ESSEN_VEGAN = 2;
	public final static int ESSEN_SALAD = 3;
	public final static int ESSEN_POMMES = 4;
	//these are sorted by priority: a person of type meat will like the lowest one most. A person of type vegan will like the lowest one >= itself (=2) most. 
	
	// Listen mit Essenswahl
	public static List<Integer> vegetarian = new ArrayList<>() ;
	public static List<Integer> vegan = new ArrayList<>();
	public static List<Integer> meatlover = new ArrayList<>();
	public static List<Integer> noPref = new ArrayList<>();
	
	//walking styles
	public final static int CHAOTIC = 0;
	public final static int GOALORIENTED = 1;
	public final static int PATHORIENTED = 2;
	
	//for the mensagrid
	public final static int GRID_KASSE = 1;
	public final static int GRID_STUDENT = 2;
	public final static int GRID_THEKE = 3;
	public final static int GRID_AUSGABE = 4;
	

	//wait times
	public static final int waitKasse = 4;
	public static final int waitAktion = 2;
	public static final int waitMeat = 2;
	public static final int waitSalad = 6;
	
}
