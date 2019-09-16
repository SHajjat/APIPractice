package API;

import beans.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.omg.CORBA.Object;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.*;

import static io.restassured.RestAssured.given;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;


public class APIDay3 {
	String url = "http://54.227.140.148:1000/ords/hr";
	
	
	/*
	Given accept type is JSON
	and Content-Type is JSON
	When I send to POST request to URL/regions
	with following JSON BODY
	{
	"region_id":11,
	"region_name":"myregion"
	}
	then status code must be 201
	and response body should match request body
	 */
	@Test
	public void regionPOSTv1(){//this way not recommended
		String requestBody ="{\"region_id\": 15,\"region_name\": \"Irbid\"}";
		
		Response response = given().accept(ContentType.JSON)//im telling API i want to see jason response (BODY)
				.and().contentType(ContentType.JSON)//im telling API i want to send JSON response
				.and().body(requestBody)//sending request body
				.when().post(url+"/regions/");
		
		System.out.println(response.statusCode());
		System.out.println(response.statusLine());
		System.out.println(response.prettyPrint());
		
		//verify status code for post request
		assertEquals(response.statusCode(),201);
		
		JsonPath jsonPath = response.jsonPath();
		//verify region name
		assertEquals(jsonPath.getString("region_name"),"Irbid");
		assertEquals(jsonPath.getInt("region_id"),15);
		
	}
	
	
	@Test
	public void regionPOSTv2(){
		//map will be converted to JSON
		Map requestMap = new HashMap<>();
		requestMap.put("region_id",16);
		requestMap.put("region_name","Amman");
		
		Response response = given().accept(ContentType.JSON)
				.and().contentType(ContentType.JSON).
				and().body(requestMap)
				.when().post(url+"/regions/");
		
		
		JsonPath jsonPath = response.jsonPath();
		
		assertEquals(jsonPath.getInt("region_id"),16);
		assertEquals(jsonPath.getString("region_name"),"Amman");
		
		
		
		
		
	}
	
	@Test
	public void getAmman(){
		Response response = given().accept(ContentType.JSON)
				.when().get(url+"/regions/");
		assertEquals(response.statusCode(),200);
		
		
		JsonPath jsonPath = response.jsonPath();
		
		List<Map> regions = jsonPath.getList("items",Map.class);
		for(Map region:regions){
			if((Integer)region.get("region_id") == 15){
				assertEquals(region.get("region_name"),"Amman");
			}
		}
	}
	
	//******** POJO ---> PLAIN OLD JOB OBJECT ******** ANOTHER NAME IS BEANS
	
	
	
	@Test
	public void regionPOSTv3(){
		Region region = new Region();
		region.setRegion_id(111);
		region.setRegion_name("zarka");
		
		Response response = given().accept(ContentType.JSON)
				.and().contentType(ContentType.JSON)
				.and().body(region)
				.and().when().post(url+"/regions/");
		
		JsonPath jsonPath = response.jsonPath();
		
		assertEquals(jsonPath.getString("region_name"),"zarka");
		assertEquals(jsonPath.getInt("region_id"),111);
		assertEquals(response.statusCode(),201);
		
		
	}
	
	
	@Test
	public void regionPOSTv4(){
		Region region = new Region();
		region.setRegion_id(112);
		region.setRegion_name("wadi rum");
		
		//map will be converted to JSON
		Response response = given().accept(ContentType.JSON)
				.and().contentType(ContentType.JSON)
				.and().body(region)
				.and().when().post(url+"/regions/");
		
		//verify status code for post request
		assertEquals(response.statusCode(),201);
		
		//create our POJO object from response body
		RegionRespose regionRespose = response.as(RegionRespose.class);
		assertEquals(regionRespose.getRegion_id(),region.getRegion_id()); // or 112 so this is dynamic
		assertEquals(regionRespose.getRegion_name(),region.getRegion_name());
		
	}
	
	
	@Test
	public void regionPOSTv5(){
		Region region = new Region();
		region.setRegion_id(113);
		region.setRegion_name("wadi rum");
		
		//POJO will be converted to JSON
		Response response = given().accept(ContentType.JSON)
				.and().contentType(ContentType.JSON)
				.and().body(region)
				.and().when().post(url+"/regions/");
		//verify status code for post request
		assertEquals(response.statusCode(),201);
		
		//create our POJO object for response body
		
		RegionRespose regionRespose = response.as(RegionRespose.class);
		assertEquals(regionRespose.getRegion_id(),113);
		assertEquals(regionRespose.getRegion_name(),region.getRegion_name());
		
	}
	
	
	/*
	Given Accept type is Json
	And Content-Type is Json
	When I sent to POST request to url/countries/
	with following JSON body
	{
	"country_id":"BB"
	"country_name":"SamirLand",
	"region_id":2
	}
	then status code ,ust be 201
	and response body should match request body
	 */
	
	@Test
	public void countryPOSTTest(){
		Country country = new Country();
		country.setCountry_id("CC");
		country.setCountry_name("Narnia");
		country.setRegion_id(2);
		
		Response response = given().accept(ContentType.JSON).and().contentType(ContentType.JSON)
				.when().body(country).post(url+"/countries/");
		
		assertEquals(response.statusCode(),201);
		
		CountryResponse countryResponse = response.as(CountryResponse.class);
		assertEquals(countryResponse.getCountry_id(),country.getCountry_id());
		assertEquals(countryResponse.getCountry_name(),country.getCountry_name());
		assertEquals(countryResponse.getRegion_id(),country.getRegion_id());
		
		
	}
	
	
	@Test
	public void countryPUTTest(){
		Country country = new Country();
		country.setCountry_id("CC");
		country.setCountry_name("Jordan");
		country.setRegion_id(3);
		
		Response response = given().accept(ContentType.JSON).and().contentType(ContentType.JSON)
				.when().body(country).put(url+"/countries/"+country.getCountry_id());
		
		assertEquals(response.statusCode(),200);
		
		CountryResponse countryResponse = response.as(CountryResponse.class);
		assertEquals(countryResponse.getCountry_id(),country.getCountry_id());
		assertEquals(countryResponse.getCountry_name(),country.getCountry_name());
		assertEquals(countryResponse.getRegion_id(),country.getRegion_id());
		
		
	}
	
	
	
	@Test
	public void countryDeleteTest(){
		Country country = new Country();
		country.setCountry_id("CC");
		country.setCountry_name("Narnia");
		country.setRegion_id(2);
		
		Response response = given().accept(ContentType.JSON)
				.when().delete(url+"/countries/"+country.getCountry_id());
		
		assertEquals(response.statusCode(),200);
		//look at this brahhh
		JsonPath jsonPath = response.jsonPath();
		assertEquals(jsonPath.getInt("rowsDeleted"),1);
		/*
		Body for delete
		{
    "rowsDeleted": 1
       }
		 */
		
	}
	
	//Gson and Jackson
	
	@Test
	public void jacksonExample() throws JsonProcessingException {
		Country country = new Country();
		country.setCountry_id("DD");
		country.setCountry_name("DDDD");
		country.setRegion_id(3);
		
		ObjectMapper mapper = new ObjectMapper();//coming from com.fasterxml.jackson.databind.ObjectMapper;
		String jsonRequest = mapper.writeValueAsString(country);// if rest assure didnt have it for us , we have to do this
		//this does serilization for us, but its automatic with rest
		//you have Jackson and Gson
		System.out.println(jsonRequest);
		
		
	}
	
	
	
}
