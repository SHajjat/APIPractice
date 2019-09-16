package API;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.hamcrest.MatcherAssert;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.lang.reflect.Array;
import java.util.*;

import static io.restassured.RestAssured.*;
import static org.codehaus.groovy.runtime.InvokerHelper.asList;
import static org.testng.Assert.assertEquals;

public class APIDay2 {
	String url = "http://54.227.140.148:1000/ords/hr";
	
	/*
	Given Accept type is Json
	When I send a get request to url/employees
	Then status code is 200
	And response content should be json
	and 100 employees data should be in json response body
	
	*/
	@Test
	public void f() {
		given().accept(ContentType.JSON).and().param("limit", 100).when().get(url + "/employees")
				.then().contentType(ContentType.JSON)
				.and().statusCode(200).body("items.employee_id", Matchers.hasSize(100));
		
	}
	
	/*
	 Given Accept type is Json
   When I send a get request to url/employees
   Then status code is 200
   And response content should be json
   and first_name must be "John"
   and email must be "JCHEN"
   */
	@Test
	public void testWithPath() {
		given().accept(ContentType.JSON).and().pathParam("id", 110)//path parameter
				.when().get(url + "/employees/{id}").then()
				.statusCode(200).and().contentType(ContentType.JSON).and().body("first_name", Matchers.equalTo("John")
				, "email", Matchers.equalTo("JCHEN"));
	}
	
	/*
	 Given Accept type is Json
   When I send a get request to url/employees
   Then status code is 200
   And response content should be json
   and first_name must be "John"
   and email must be "JCHEN"
   */
	
	@Test
	public void testWithJsonPath() {
		Response response = given().accept(ContentType.JSON).pathParam("id", 110)
				.when().get(url + "/employees/{id}");
		
		
		//get json body and assign to JsonPath format
		//the whole reason of this JsonPath is to be able to reach the strings
		JsonPath json = response.jsonPath();
		System.out.println(json.getString("first_name"));
		System.out.println(json.getString("job_id"));
		
		String actualFirst_name = json.getString("first_name");
		String expectedFirst_name = "John";
		assertEquals(actualFirst_name, expectedFirst_name);
	}
	
	
	/*
	 Given Accept type is Json
   When I send a get request to url/employees
   Then status code is 200
   and resoinse content should be json
   all data should be returned
   */
	
	@Test
	public void testJsonPathWithList() {
		Map<String, Integer> paramMap = new HashMap<>();
		paramMap.put("limit", 100);
		Response response = given().accept(ContentType.JSON).and()
				.params(paramMap).and().get(url + "/employees/");
		
		JsonPath json = response.jsonPath();
		//verify status code
		assertEquals(response.statusCode(), 200);
		//get all employee ids and assign to list
		List<String> employee_ids = json.getList("items.employee_id");
		assertEquals(employee_ids.size(), 100);
		//System.out.println(employee_ids);
		
		//get all emails assign to a list and print them
		
		List<String> emails = json.getList("items.email");
		List<String> names = json.getList("items.last_name");
		String emp120email = json.getString("items[20].email");//this will give me the 20th in the array
		System.out.println(emp120email);
		System.out.println(emails.toString());
		//String [] emailsArr =(String[])(json.getList("items.email").toArray()); you cant cast down to String from object
		//System.out.println(Arrays.toString(emailsArr));
		/*
	get all emp id that are greater than 150
	 */
		
		List<Integer> empIds = json.getList("items.findAll{it.employee_id > 150}.employee_id");
		System.out.println(empIds);
		
		//get all employees last_name , who salary greater than 7000
		
		List<String> salaries = json.getList("items.findAll{it.salary > 7000}.last_name");
		System.out.println(salaries);
		
		List<String> first_name = json.getList("items.findAll{it.salary<10000}.first_name");
		System.out.println(first_name);
	}
	
	/*
	 Given Accept type is Json
   When I send a get request to url/employees
   Then status code is 200
   and resoinse content should be json
   all data should be returned
	 */
	@Test
	public void testWithJsonToMap() {
		Response response = given().accept(ContentType.JSON).and().when().get(url + "/employees/140");
		
		//we canvert Json result to hashmap data structure
		Map<String, Object> jsonMap = response.as(HashMap.class);
		System.out.println(jsonMap.get("first_name"));
		System.out.println(jsonMap.get("salary"));
		System.out.println(jsonMap);
		String actualFirstName = jsonMap.get("first_name").toString();
		assertEquals(actualFirstName, "Joshua");
		
	}
	
	@Test
	public void convertingJsonToListOfMaps() {
		Response response = given().accept(ContentType.JSON).and().when().get(url + "/departments/");
		JsonPath jsonPath = response.jsonPath();
		
		//we are getting json response and assgin to list of maps
		List<Map> result = jsonPath.getList("items", Map.class);
		System.out.println(result.get(0).get("department_name"));
		
		assertEquals(result.get(4).get("department_id"), 50);
	}
	
	
	@Test
	public void doItYourSelf() {
		
		Response response = given().accept(ContentType.JSON).and().when().get(url + "/employees");
		JsonPath jsonPath = response.jsonPath();
		
		List<Map> employees = jsonPath.getList("items", Map.class);
		System.out.print(employees.get(0).get("first_name") + " ");
		System.out.print(employees.get(0).get("last_name") + " ");
		System.out.println(((String) (employees.get(0).get("email"))).toLowerCase() + "@gmail.com");
		
	}
	
	/*
	Given Content type is Json
	and limit is 10
	when i send the get request to url/regions
	the status code must be 200
	then I should see following data
	1.Europe
	2.Americas
	3.Asia
	4.Middle East and Africa
	 */
	@Test
	public void confirmReaionsTest() {
		Map<String, Integer> params = new HashMap<>();
		params.put("limit", 10);
		List<String> expected = new ArrayList<>(Arrays.asList("Europe", "Americas", "Asia", "Middle East and Africa"));
		
		//first Way to do it
		
		given().accept(ContentType.JSON).and().param("limit", 10)
				.when().get(url + "/regions").then().contentType(ContentType.JSON)
				.and().body("items.region_name", Matchers.equalTo(expected));
		
		//second way to do it
		Response response = given().accept(ContentType.JSON).params(params).when().get(url + "/regions");
		JsonPath jsonPath = response.jsonPath();
		
		assertEquals(jsonPath.getString("items[0].region_name"), expected.get(0));
		assertEquals(jsonPath.getString("items[1].region_name"), expected.get(1));
		assertEquals(jsonPath.getString("items[2].region_name"), expected.get(2));
		assertEquals(jsonPath.getString("items[3].region_name"), expected.get(3));
		
		
		List<Map> regions = jsonPath.getList("items", Map.class);
		System.out.println(regions);
		
		for ( Map map : regions ) {
			Assert.assertTrue(expected.remove(map.get("region_name")));
			
		}
		
	}
	
	@Test
	public void convertJsonToListofMap() {
		Response response = given().accept(ContentType.JSON)
				.when().get(url + "/departments/");
		
		JsonPath jsonPath = response.jsonPath();
		
		//we are getting json response and putting it in map
		List<Map> departments = jsonPath.getList("items", Map.class);
		System.out.println(departments.get(departments.size() - 1).get("department_name"));

//		for (Map map : departments){
//			System.out.println(map);
//		}
		
		String actualDepartmentName = (String) departments.get(4).get("department_name");
		String expectedDepartmentName = "Shipping";
		assertEquals(actualDepartmentName, expectedDepartmentName);
		System.out.println(departments.get(10));
		
		//verify department_id
		Object actualDepartmentID = departments.get(4).get("department_id");
		Object expectedDepartmentID = 50;
		assertEquals(actualDepartmentID, expectedDepartmentID);
		
		
	}
	
	@Test
	public void regionsTest() {
		/*
		Given Content type is Json
		When I send the Get request to url/regions
		the status code must be 200
		Then I should see following data
		1. "Europe" , "Americas" , "Asia" , "Middle East and Africa"
		 */
		List<String> regions = new ArrayList<>(Arrays.asList("Europe", "Americas", "Asia", "Middle East and Africa"));
		when().get(url + "/regions/").then().statusCode(200).body("items.region_name", Matchers.equalTo(regions));
		
		
		Response response1 = when().get(url + "/regions/");
		JsonPath jsonPath1 = response1.jsonPath();
		
		List<Map> regionsMap = jsonPath1.getList("items", Map.class);
		int i = 0;
		for ( Map region : regionsMap ) {
			assertEquals(region.get("region_name"),regions.get(i++));
			System.out.println(region);
			
			
		}
		
		
		Response response = when().get(url + "/regions/");
		response.then().statusCode(200).and().body("items[0].region_name",Matchers.equalTo(regions.get(0)));
		response.then().statusCode(200).and().body("items[1].region_name",Matchers.equalTo(regions.get(1)));
		response.then().statusCode(200).and().body("items[2].region_name",Matchers.equalTo(regions.get(2)));
		response.then().statusCode(200).and().body("items[3].region_name",Matchers.equalTo(regions.get(3)));
	}
}
