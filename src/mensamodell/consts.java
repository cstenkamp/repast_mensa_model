package mensamodell;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class consts {

	public final static int SIZE_X = 100;
	public final static int SIZE_Y = 60;

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
	public final static int VEGGIE = 0;
	public final static int VEGANER = 1;
	public final static int MEAT = 2;
	public final static int NOPREFERENCE = 3;
	
	//Essen
	public final static int ESSEN_VEGGIE = 0;
	public final static int ESSEN_VEGAN = 1;
	public final static int ESSEN_MEAT = 2;
	public final static int ESSEN_SALAD = 3;
	public final static int ESSEN_POMMES = 4;
	
	// Listen mit Essenswahl
	public final static List<Integer> vegetarian = new ArrayList<>();
	vegetarian.add(ESSEN_VEGGIE);
	vegetarian.add(ESSEN_VEGAN);
	vegetarian.add(ESSEN_SALAD);
	vegetarian.add(ESSEN_POMMES);
	public final static List<Integer> vegan = new ArrayList<>();
	vegan.add(ESSEN_VEGAN);
	vegan.add(ESSEN_SALAD);
	vegan.add(ESSEN_POMMES);
	public final static List<Integer> meatlover = new ArrayList<>();
	meatlover.add(ESSEN_VEGGIE);
	meatlover.add(ESSEN_VEGAN);
	meatlover.add(ESSEN_SALAD);
	meatlover.add(ESSEN_POMMES);
	public final static List<Integer> noPref = new ArrayList<>();
	noPref.add(ESSEN_VEGGIE);
	noPref.add(ESSEN_VEGAN);
	noPref.add(ESSEN_SALAD);
	noPref.add(ESSEN_POMMES);
	
	//walking styles
	public final static int CHAOTIC = 0;
	public final static int GOALORIENTED = 1;
	public final static int PATHORIENTED = 2;
	
	//for the mensagrid
	public final static int GRID_THEKE = 1;
	public final static int GRID_KASSE = 2;
	

	
}
