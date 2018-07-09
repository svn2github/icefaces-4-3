/*
 * Copyright 2004-2015 ICEsoft Technologies Canada Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS
 * IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package org.icefaces.demo.emporium.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

import org.icefaces.ace.model.colorEntry.ColorEntryLayout;
import org.icefaces.ace.model.colorEntry.SwatchEntry;

@ManagedBean(name=ListData.BEAN_NAME)
@ApplicationScoped
public class ListData implements Serializable {
	private static final long serialVersionUID = -9018968427877614267L;

	public static final String BEAN_NAME = "listData";
	
	public static final String DEFAULT_SELECT_LABEL = "-- Select --";
	public static final String DEFAULT_TAB_ORIENTATION = "top";
	public static final String DEFAULT_LOCATION = "North America";
	public static final SwatchEntry DEFAULT_FOREGROUND_COLOR = new SwatchEntry("Black", 0,0,0);
	public static final SwatchEntry DEFAULT_BACKGROUND_COLOR = new SwatchEntry("Steel Blue", 0.6902, 0.7686, 0.8706);

	public static final String[] TAB_ORIENTATIONS = new String[] {
		DEFAULT_TAB_ORIENTATION, "bottom"
	};

    public static List<String> SWATCHPARTS = new ArrayList<String>(Arrays.asList("header", "preview", "hex", "swatches", "footer"));
	public static final List<SwatchEntry> COLORS = generateColors();
    public static final List<ColorEntryLayout> SWATCHLAYOUT = generateLayout();

	public static final String[] LOCATIONS = new String[] {
		"Africa", "Asia", "Australia", "Europe", DEFAULT_LOCATION, "South America"
	};
	public static final String[] FIRST_NAMES = new String[] {
		"James", "John", "Robert", "Michael", "William", "David", "Richard", "Joseph",
		"Charles", "Thomas", "Christopher", "Daniel", "Matthew", "Donald",
		"Anthony", "Paul", "Mark", "George", "Steven", "Kenneth", "Andrew",
		"Edward", "Joshua", "Brian", "Kevin", "Ronald", "Timothy", "Jason",
		"Jeffrey", "Gary", "Mary", "Patricia", "Jennifer", "Elizabeth",
		"Linda", "Barbara", "Susan", "Margaret", "Jessica", "Dorothy",
		"Sarah", "Karen", "Nancy", "Betty", "Lisa", "Sandra", "Helen",
		"Ashley", "Donna", "Kimberly", "Carol", "Michelle", "Emily",
		"Amanda", "Melissa", "Deborah", "Laura", "Stephanie", "Rebecca", "Sharon"
	};
	public static final String[] LAST_NAMES = new String[] {
		"Smith", "Johnson", "Williams", "Brown", "Jones", "Miller", "Davis",
		"Murphy", "Gallo", "Wilson", "Anderson", "Taylor", "Thomas",
		"Moore", "Martin", "Jackson", "Thompson", "Lee", "Harris", "Clark"
	};
	public static final String[] ITEMS = new String[] {
	    "Air Freshener", "Apple", "Bag", "Balloon", "Bananas", "Bed", "Beef", "Bicycle",
		"Blanket", "Blouse", "Book", "Bookmark", "Boom Box",
		"Bottle", "Bottle Cap", "Bow", "Bowl", "Box", "Bracelet", "Bread",
		"Brocolli", "Buckle", "Button", "Camera", "Candle",
		"Candy Wrapper", "Canvas", "Car", "Carrots", "Cat", "Cell Phone",
		"Chair", "Chalk", "Chapter Book", "Charger", "Checkbook",
		"Chocolate", "Cinder Block", "Clamp", "Clay Pot", "Clock",
		"Clothes", "Coasters", "Computer", "Conditioner", "Controller",
		"Cookie Jar", "Cork", "Couch", "Cup", "Desk",
		"Doll", "Door", "Drawer", "Drill Press",
		"Eye Liner", "Face Wash", "Fake Flowers", "Flag", "Floor",
		"Flowers", "Food", "Fork", "Fridge", "Glass", "Glasses",
		"Glow Stick", "Greeting Card", "Grid Paper", "Hair Brush",
		"Hair Tie", "Hanger", "Headphones", "Helmet", "House",
		"Ice Cube Tray", "Keyboard", "Key Chain", "Keys", "Knife", "Lace",
		"Lamp", "Lamp Shade", "Leg Warmers", "Lip Gloss", "Magnet", "Milk",
		"Mirror", "Model Car", "Money", "Monitor", "Mop", "Mouse Pad",
		"Nail Clippers", "Nail File", "Needle",
		"Newspaper", "Outlet", "Packing Peanuts", "Paint Brush", "Pants",
		"Paper", "Pen", "Pencil", "Perfume", "Phone", "Photo Album",
		"Piano", "Picture Frame", "Pillow", "Plastic Fork",
		"Plate", "Playing Card", "Pool Stick", "Puddle", "Purse", "Radio",
		"Remote", "Ring", "Rubber Band", "Rubber Duck", "Rug",
		"Rusty Nail", "Sailboat", "Sandal", "Sand Paper", "Scotch Tape",
		"Screw", "Seat Belt", "Shampoo", "Sharpie", "Shawl", "Shirt",
		"Shoe Lace", "Shoes", "Shovel", "Sidewalk", "Sketch Pad",
		"Slipper", "Soap", "Socks", "Soda Can", "Sofa", "Speakers",
		"Sponge", "Spoon", "Spring", "Sticky Note", "Stockings",
		"Stop Sign", "Sun Glasses", "Table", "Television",
		"Thermometer", "Thermostat", "Thread", "Tire Swing", "Toe Ring",
		"Toilet", "Tomato", "Toothbrush", "Toothpaste", "Tooth Picks",
		"Towel", "Tree", "Truck", "USB Drive",
		"Vase", "Wagon", "Wallet",
		"Washing Machine", "Watch", "Water Bottle", "White Out", "Window", "Zipper"
	};

    private static List<ColorEntryLayout> generateLayout() {
        List<ColorEntryLayout> layout= new ArrayList<ColorEntryLayout>() ;
        layout.add(new ColorEntryLayout("preview", 0, 0, 1, 1));
        layout.add(new ColorEntryLayout("hex", 1,0,1,1));
        layout.add(new ColorEntryLayout("swatches", 2,0, 1, 1));
        return layout;
    }
	
	private static List<SwatchEntry> generateColors() {
		List<SwatchEntry> toReturn = new ArrayList<SwatchEntry>();
		toReturn.add(new SwatchEntry("White", 1,1,1));
		toReturn.add(DEFAULT_FOREGROUND_COLOR);
		toReturn.add(new SwatchEntry("Red", 1, 0, 0));
		toReturn.add(new SwatchEntry("Green", 0, 1, 0));
		toReturn.add(new SwatchEntry("Blue", 0, 0, 1));
		toReturn.add(new SwatchEntry("Yellow", 1, 1, 0));
		toReturn.add(new SwatchEntry("Purple", 0.50196, 0, 0.50196));
		toReturn.add(new SwatchEntry("Brown", 0.647, 0.165, 0.165));
		toReturn.add(new SwatchEntry("Cyan", 0, 0.529, 0.529));
		toReturn.add(new SwatchEntry("Orange", 1, 0.549, 0));
		toReturn.add(new SwatchEntry("Salmon", 0.914, 0.588, 0.478));
		toReturn.add(new SwatchEntry("Sky Blue", 0, 0.749, 1));
		toReturn.add(new SwatchEntry("Forestgreen", 0.133, 0.545, 0.133));
		toReturn.add(new SwatchEntry("Gold", 1, 0.843, 0));
		toReturn.add(DEFAULT_BACKGROUND_COLOR);
		toReturn.add(new SwatchEntry("MediumVioletRed", 0.859, 0.439, 0.577));
		toReturn.add(new SwatchEntry("Seashell", 1, 0.961, 0.933));
		return toReturn;
	}
	
	public String getDefaultSelectLabel() {
		return DEFAULT_SELECT_LABEL;
	}
	
	public String[] getTabOrientations() {
		return TAB_ORIENTATIONS;
	}
	
	public List<SwatchEntry> getColors() {
		return COLORS;
	}

    public List<ColorEntryLayout> getSwatchLayout(){ return SWATCHLAYOUT;}
    public List<String> getSwatchParts(){return SWATCHPARTS;}
	
	public String[] getLocations() {
		return LOCATIONS;
	}
	
	public String[] getFirstNames() {
		return FIRST_NAMES;
	}
	
	public String[] getLastNames() {
		return LAST_NAMES;
	}
	
	public String[] getItems() {
		return ITEMS;
	}
}
