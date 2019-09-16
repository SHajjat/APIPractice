package beans;

import java.lang.reflect.Array;
import java.util.*;

public class RegionRespose {
	private int region_id;
	private String region_name;
	private ArrayList<Map<String,String>> links;// this is the links under the region_name
	/*links": [
                {
                    "rel": "self",
                    "href": "http://54.227.140.148:1000/ords/hr/countries/AR"
                }
            ]
            
            links.get(0).get("rel");
	 */
	
	public int getRegion_id() {
		return region_id;
	}
	
	public void setRegion_id(int region_id) {
		this.region_id = region_id;
	}
	
	public String getRegion_name() {
		return region_name;
	}
	
	public void setRegion_name(String region_name) {
		this.region_name = region_name;
	}
	
	public ArrayList<Map<String, String>> getLinks() {
		return links;
	}
	
	public void setLinks(ArrayList<Map<String, String>> links) {
		this.links = links;
	}
}
