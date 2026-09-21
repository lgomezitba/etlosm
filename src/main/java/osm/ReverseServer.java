package osm;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ReverseServer {

    // Datos de ejemplo (lat,lon -> XML)
    private static final Map<String, String> DATA = new HashMap<>();

  

    public static void main(String[] args) throws Exception {

    	Download d = new Download();
    	d.init();
    	
  		InputStream sr = ReverseServer.class.getResourceAsStream("/latmissing.xml");
 		String textLat = new BufferedReader(new InputStreamReader(sr))
     		                    .lines().collect(Collectors.joining("\n"));
  		sr = ReverseServer.class.getResourceAsStream("/lonmissing.xml");
 		String textLon = new BufferedReader(new InputStreamReader(sr))
     		                    .lines().collect(Collectors.joining("\n"));
 		
 		sr = ReverseServer.class.getResourceAsStream("/latbadnumber.xml");
 		String textBadNumberLat = new BufferedReader(new InputStreamReader(sr))
     		                    .lines().collect(Collectors.joining("\n"));
 		
 		sr = ReverseServer.class.getResourceAsStream("/lonbadnumber.xml");
 		String textBadNumberLon = new BufferedReader(new InputStreamReader(sr))
     		                    .lines().collect(Collectors.joining("\n"));
 		
 		int port = 9999;   //8080;
 
 		String envPort = System.getenv("PORT");

 		// si asignan automaticamente el cloud
 		if (envPort != null) {
 		    port = Integer.parseInt(envPort);
 		}
 		else
	 		if (args.length == 1) {
	 			try {
	 				int p= Integer.valueOf(args[0]);
	 				port= p;
	 			}
	 			catch (Exception e) {
	 				
	 			}
 			
 		}
    	
        HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", port), 0);
      
        server.createContext("/reverse", (HttpExchange exchange) -> {
            String query = exchange.getRequestURI().getQuery();

            Map<String, String> params = parseQuery(query);

            String lat = params.get("lat");
            String lon = params.get("lon");
            
            if (lat == null) {
            	String response= textLat;
            	exchange.getResponseHeaders().add("Content-Type", "application/xml");
                exchange.sendResponseHeaders(200, response.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
            else 
            	 if (lon == null) {
                 	String response= textLon;
                 	exchange.getResponseHeaders().add("Content-Type", "application/xml");
                    exchange.sendResponseHeaders(200, response.getBytes().length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                 }
            	 else {
            		 if (lat.contains(",")) {
                     	String response= textBadNumberLat;
                     	exchange.getResponseHeaders().add("Content-Type", "application/xml");
                        exchange.sendResponseHeaders(200, response.getBytes().length);
                        OutputStream os = exchange.getResponseBody();
                        os.write(response.getBytes());
                        os.close();
      
            		 }
            		 else
            			 if (lon.contains(",")) {
                         	String response= textBadNumberLon;
                         	exchange.getResponseHeaders().add("Content-Type", "application/xml");
                            exchange.sendResponseHeaders(200, response.getBytes().length);
                            OutputStream os = exchange.getResponseBody();
                            os.write(response.getBytes());
                            os.close();
          
                		 }
            			 else {	 
		            		 Coordinates c= new Coordinates(lat, lon);
		            		 String response= d.reverse.get(c);
		            		 if (response == null) {
		            			 
		            			 // intentar cercano
		            			 c= findClosest(d, lat, lon);
		            			 if (c != null)
		            				 response= d.reverse.get(c);
		            			 else { // no way
			            			 String s= "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>" + "\n";
			            		 
			            			 String s1= String.format("<reversegeocode querystring=\"lat=%s&amp;lon=%s&amp;format=xml\">", lat, lon);
			            			 s= s + s1 +"\n" + "<error>Unable to geocode</error>" + "\n" + "</reversegeocode>";
			            			 
			            			 response= s; 
		            			 }
		            		}
		            		 
		            		 exchange.getResponseHeaders().add("Content-Type", "application/xml");
		                     exchange.sendResponseHeaders(200, response.getBytes().length);
		                     OutputStream os = exchange.getResponseBody();
		                     os.write(response.getBytes());
		                     os.close();
		            	 }
            }
           

           
        });

        server.setExecutor(null); // default
        server.start();

        System.out.println("Servidor escuchando en http://localhost:" + port);
    }

    private static Map<String, String> parseQuery(String query) {
        Map<String, String> map = new HashMap<>();
        if (query == null) return map;

        for (String param : query.split("&")) {
            String[] pair = param.split("=");
            if (pair.length == 2) {
                map.put(pair[0], pair[1]);
            }
        }
        return map;
    }
    
    
    private static Coordinates findClosest(Download d, String lat, String lon) {
        double bestDist = Double.MAX_VALUE;
        Coordinates OK= null;
        for (Coordinates p : d.myData) {
            double dist = distance(Double.valueOf(lat), Double.valueOf(lon), Double.valueOf(p.lat), Double.valueOf(p.lon));

            if (dist < bestDist) {
            	OK= p;
                bestDist = dist;
            }
        }
        
        if (bestDist <= 0.20) // 0.09) // en gral. 0.08 0.002 0.004 0.005
        	return OK;
        else
        	return null;
    }
    
    
    private static double distance(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371; // radio tierra km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon/2) * Math.sin(dLon/2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        return R * c;
    }
    
    
}