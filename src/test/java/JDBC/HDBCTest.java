package JDBC;

import beans.Region;
import cucumber.api.java.it.Ma;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import javax.xml.ws.Response;
import java.sql.*;
import java.util.*;

public class HDBCTest {
	String url = "jdbc:oracle:thin:@ec2-54-197-2-98.compute-1.amazonaws.com:1521:xe";
	String user = "hr";
	String pass = "hr";
	@Test(enabled = false)
	public void oracleSQL() throws SQLException {
		Connection connection = DriverManager.getConnection(url,user,pass);//import from java.SQL
		Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);//making it scrollable
		ResultSet resultSet = statement.executeQuery("SELECT * FROM countries");
		//Result set starts before the first row
//
//		while (resultSet.next())//we use .next() to go to the next method
//		System.out.println(resultSet.getString(1)+ " - "+resultSet.getString("country_name")+" - "+resultSet.getInt("region_id"));
		
//		resultSet.next();
//		resultSet.next();
//		resultSet.next();
//		resultSet.first();
//		System.out.println(resultSet.getRow());
//		System.out.println(resultSet.getString(2));
		
		
		//find out how many records in resultSet
		resultSet.last();
		System.out.println("Total number of rows "+resultSet.getRow());
		resultSet.absolute(2);
		System.out.println(resultSet.getRow());
		
		
		//how to move first row and loop everhing again after you are at the last one
		
		resultSet.beforeFirst();//you gota go to before first since the next will take me to the next one
		while (resultSet.next()){
			System.out.println(resultSet.getString(2));
			
		}
		
		System.out.println("********");
		//or
		resultSet.first();
		do{
			System.out.println(resultSet.getString(2));
		}while (resultSet.next());
		
		resultSet.close();
		statement.close();
		connection.close();
	}
	
	@Test
	public void metaDetaTest() throws SQLException {
		Connection connection = DriverManager.getConnection(url,user,pass);
		Statement statement = connection.createStatement();
		ResultSet resultSet = statement.executeQuery("SELECT * FROM employees");
		
		DatabaseMetaData databaseMetaData = connection.getMetaData();//this will give information on the SQL it self
		System.out.println("user :" +databaseMetaData.getUserName());
		System.out.println("database "+ databaseMetaData.getDatabaseProductName());
		System.out.println("Database Product version "+databaseMetaData.getDatabaseProductVersion());
		
		//to get information on the data it self like column names and fun stuff
		//use ResultSetMetaDeta
		
		ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
		System.out.println(resultSetMetaData.getTableName(1));
		System.out.println(resultSetMetaData.getColumnName(2));
		
		//how many columns
		System.out.println(resultSetMetaData.getColumnCount());
		
		//first column name
		System.out.println(resultSetMetaData.getColumnName(1));
		
		//get table name
		System.out.println("table name: " +resultSetMetaData.getTableName(1));
		
		//print all column names using loop
		
		int  count = resultSetMetaData.getColumnCount();
		for (int i = 1 ; i<count;i++ ){
			System.out.println(resultSetMetaData.getColumnName(i));
		}
		
		resultSet.close();
		statement.close();
		connection.close();
		
	}
	
	@Test
	public void DButil() throws Exception{
		Connection connection = DriverManager.getConnection(url,user,pass);
		Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
		ResultSet resultSet = statement.executeQuery("SELECT first_name , last_name , salary , job_id from employees where rownum <= 5");
		
		//dour main structure it will keep whole query result
		List<Map<String , Object>> queryData = new ArrayList<>();
		//we will add the first row data to this map
		Map <String,Object> row1 =new HashMap<>();
		resultSet.next();//going to first row
		//key is coumn name , value is value of the column
		row1.put("first_name", "Steven");
		row1.put("last_name", "King");
		row1.put("Salary", 24000);
		row1.put("Job_id", "AD_PRES");
		
		System.out.println(row1);
		queryData.add(row1);//pushing to the list
		System.out.println(queryData.get(0).get("first_name"));
		
		resultSet.next();
		Map<String,Object> row2 = new HashMap<>();
		row2.put("first_name", resultSet.getObject("first_name"));
		row2.put("last_name", resultSet.getObject("last_name"));
		row2.put("salary", resultSet.getObject("salary"));
		row2.put("job_id", resultSet.getObject("job_id"));
		queryData.add(row2);
		
		System.out.println("First row "+queryData.get(0));
		System.out.println("Second row "+queryData.get(1));

	}
	
	//Dynamic code to put everything in list<map>>
	//i need
	//Dynamic Collection
	//Number of columns --> resultsetMetaData.gerColumnCount()
	//number of rows -->while(resultset.next()
	//columns name --> resiltset.getObject(column index)
	
	@Test
	public void DBUtilDynamic() throws SQLException {
		Connection connection = DriverManager.getConnection(url,user,pass);
		Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
		ResultSet resultSet = statement.executeQuery("SELECT * from countries");
		//database metadata (create object)
		DatabaseMetaData dbSetMetaData = connection.getMetaData();
		//resultset metaData
		ResultSetMetaData rsSetMetaData = resultSet.getMetaData();
		//---------Dynamic List for every Query---------\\
		
		//main list
		List<Map<String , Object>> queryData = new ArrayList<>();
		
		//number of columns
		int colCount = rsSetMetaData.getColumnCount();
		
		while (resultSet.next()){
			//creating map to adding each row inside
			Map<String , Object> row = new HashMap<>();
			
			for(int i = 1 ; i<=colCount;i++) {
				row.put(rsSetMetaData.getColumnName(i),resultSet.getObject(i));
			}
			queryData.add(row);
			
		}
		
		for (Map results : queryData){
			System.out.println(results);
			System.out.println(results.get(rsSetMetaData.getColumnName(1)));
			System.out.println(results.get(rsSetMetaData.getColumnName(2)));
			System.out.println(results.get(rsSetMetaData.getColumnName(3)));
		}
		resultSet.close();
		statement.close();
		connection.close();
		
	}
	
}
