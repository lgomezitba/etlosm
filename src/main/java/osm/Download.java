package osm;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Download {

	// myID    lat         lon
	// 206114  39.757371	-121.843286
	ArrayList<Coordinates> myData= new ArrayList<>();
	
	HashMap<Coordinates, String> reverse= new HashMap<>();
	
	
	public void init() {
		
		InputStream is = Download.class.getResourceAsStream("/coordinates.txt");
	    Scanner scanner = new Scanner(is, "UTF-8");
	    scanner.useDelimiter(",");
	    if (is == null) {
	       System.out.println("File not found!");
	       return;
	    }
	 
	    while (scanner.hasNextLine()) {
	        String linea= scanner.nextLine();
	        String[] arreglo = linea.split("\\|");
	        Coordinates c = new Coordinates(arreglo);
	        myData.add(c);
  	
    		InputStream sr = Download.class.getResourceAsStream( "/" + c.minid + ".xml");
    		String text = new BufferedReader(new InputStreamReader(sr))
	    		                    .lines().collect(Collectors.joining("\n"));
    		reverse.put(c, text);
    		
  	    }
	    
	   
    
	}
    
    public void download() {
    	
    	for (Coordinates c : myData) {
    		 System.out.println(c.minid);
         	  System.out.format("https://nominatim.openstreetmap.org/reverse?format=xml&lat=%s&lon=%s&accept-language=en",
         			  c.lat, c.lon);
         	  System.out.println();
         	  
         	  // grabarlo como xml
		}
    }	
    
    
    public void downloadSkip(int i) {
    	
    	for (Coordinates c : myData) {
    		if (i> 0) {
          	  i--;
          	  continue;  
            }
    		System.out.println(c.minid);
         	System.out.format("https://nominatim.openstreetmap.org/reverse?format=xml&lat=%s&lon=%s&accept-language=en",
         			  c.lat, c.lon);
         	System.out.println();
         	
         	 // grabarlo como xml
		}
    }	

          
          
    public static void main(String[] args) {
    	  Download x = new Download();
    	  x.init();
    //	  x.downloadSkip(102);
    	  x.download();
    }
	
}
