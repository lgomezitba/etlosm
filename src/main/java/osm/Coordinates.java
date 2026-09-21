package osm;

import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;



public class Coordinates {
// select distinct  latitude, longitude, min(myid) as id
	String lat;
	String lon;
	String minid;
	Coordinates(String[] values){
		lat= values[0];
		lon= values[1];
		minid= values[2];
	}
	
	Coordinates(String theLat, String theLon){
		lat= theLat;
		lon= theLon;
	}
	
	@Override
	public String toString() {
		return String.format("lat %s, lon %s, miid %s", lat, lon, minid);
	}
	

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Coordinates) {
			Coordinates auxi = (Coordinates) obj;
			return lat.equals(auxi.lat) && lon.equals(auxi.lon);
		}
		return false;
	}

	@Override
	public int hashCode() {
		
		return lat.hashCode() + lon.hashCode();
	}
}
