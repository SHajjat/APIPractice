package API;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.testng.Assert;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.*;

public class APIDay1 {
	
	String url = "http://54.227.140.148:1000/ords/hr";
	/*
	When I send a Get request to URL/employees
	then response status code should be 200
	 */
	@Test
	public void simpleStatusCode(){
		when().get("http://54.227.140.148:1000/ords/hr/employees") // postman handles the http:// but here you have to put it in the IP address
				.then().statusCode(200);
		
	}
	
	/*
	When I send a Get request to URL/employees
	then I should see the json response
	 */
	@Test
	public void printResponse(){
		when().get("http://54.227.140.148:1000/ords/hr/contries").body().prettyPrint();//before body you cant use than()
		//when().get("http://54.227.140.148:1000/ords/hr/employees").body().prettyPrint()
	}
	
	/*
	When I send a Get request to URL/employees with Json Header
	then I should see the json response
	
	 */
	@Test
	public void getWithHeaders(){
		given().accept(ContentType.JSON).when().get("http://54.227.140.148:1000/ords/hr/employees").then().statusCode(200);
	}
	
	@Test
	public void getWithHeadersXML(){
		given().accept(ContentType.XML).when().get("http://54.227.140.148:1000/ords/hr/employees").then().statusCode(200);
	}
	/*
	when i send a Get request to get employee 1234
	then respose status should be 404
	 */
	@Test
	public void negativeGet(){
		
		when().get("http://54.227.140.148:1000/ords/hr/employees/1234")
				.then().statusCode(404);
	}
	
	/*
	when i send a Get Request to URL/employees/1234
	then status code is 404
	And Response Body error message should include "Not Found"
	 */
	
	@Test
	public void negativeGetWithBody(){
		Response response = when().get("http://54.227.140.148:1000/ords/hr/employees/1234");
		
		//status code Check
		Assert.assertEquals(response.statusCode(),404);
		Assert.assertTrue(response.asString().contains("Not Found"));//we have to use as string it doesnt support toString
		response.prettyPrint();
		
		//works the same , asString should be used no toString()
		Assert.assertTrue(when().get("http://54.227.140.148:1000/ords/hr/employees/1234").asString().contains("Not Found"));
	}
	/*
	When I send a Get request to url/employees/110
	and accept type is Json
	Then status code is 200
	And response contend should be Json

	 */
	@Test
	public void verifyContentType() {
		given().accept(ContentType.JSON)//accept(will get us content json)
				.when().get("http://54.227.140.148:1000/ords/hr/employees/110")
				.then().statusCode(200).and().contentType(ContentType.JSON);//contentType(contentType) is validating and dude you can use And
		/*//This will fail
		given().accept(ContentType.JSON)//accept(will get us content json)
				.when().get("http://54.227.140.148:1000/ords/hr/employees/110")
				.then().statusCode(200).and().contentType(ContentType.XML);
				
		 *///This will fail
	}
	
	/*
	When I send a Get Resquest to URL/Employees/110
	Then status code is 200
	and repsonse content should be Json
	and first name should be "John
	 */
	@Test
	public void verifyFirstName(){
		given().accept(ContentType.JSON)
				.when().get(url+"/employees/110").then().statusCode(200).and().contentType(ContentType.JSON)
				.and().body("first_name", Matchers.equalTo("John"));
	}
	
	/*
	Given Accept type is Json
	When I send a Get request to url/employees/150
	Then status code is 200
	and response content should be Json
	and last name should be "Tucker"
	And job_id should be "SA_REP"
	 */
	@Test
	public void verifyJobID(){
		given().accept(ContentType.JSON).when().get(url+"/employees/150")
				.then().statusCode(200)
				.and().contentType(ContentType.JSON)
				.and().body("last_name",Matchers.equalTo("Tucker"))
				.and().body("job_id",Matchers.equalTo("SA_REP"));
	}
	
	/*
	Given Accept type is Json
	When I send a Get request to url/employees/150
	Then status code is 200
	and response content should be Json
	and 4 regions should be returned
	 */
	@Test
	public void verifyRegionCount(){
		given().accept(ContentType.JSON)
				.when().get(url+"/regions")
				.then().assertThat().statusCode(200)                      //assetThat() is for readability its called synthetic sugar
				.and().assertThat().contentType(ContentType.JSON)
				.and().assertThat().body("items.region_id",Matchers.hasSize(4))//every regoin ID is inside array, so items will show how many region_id
	            .and().assertThat().body("items.region_name",Matchers.hasItem("Europe"))//items.region_name  are [Europe, Americas, Asia, Middle East and Africa]
		        .and().assertThat().body("items.region_name",Matchers.hasItems("Americas", "Asia"));//hasItems is for multiple inserts
		
	}
	
	@Test
	public void confirmName(){
		given().contentType(ContentType.JSON).get(url+"/employees/150")
				.then().statusCode(200)
				.and().body("first_name",Matchers.equalTo("Peter"))
				.and().body("last_name",Matchers.equalTo("Tucker"));
	}
	
	
	@Test
	public void confirmRegion(){
		when().get(url+"/regions/1").then().body("region_name",Matchers.equalTo("Europe"));
	}
	
}
