package com.github.GandhiTC.java.RestApiExample.tests;



import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.testng.Assert;
import org.testng.SkipException;
import io.restassured.RestAssured;
import io.restassured.filter.Filter;
import io.restassured.http.ContentType;
import io.restassured.internal.RestAssuredResponseOptionsImpl;
import io.restassured.parsing.Parser;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSender;



/*
 * NOTE 1:
 * 			This class is used to test the dummy API example at:
 * 			http://dummy.restapiexample.com
 */

/*
 * NOTE 2:
 * 			There are 2 methods of checking whether a test should be skipped or not.
 *
 * 			The first method is to use TestNG's dependsOnMethods parameter.
 * 			When you assign this parameter to a TestNG test, it checks to see
 * 			whether or not the listed test methods completed successfully, if not,
 * 			TestNG will skip testing this test.
 *
 * 			The second method is a hard-coded check to see if this java class
 * 			has a value set for the empId variable.
 *
 * 			The value is set during the addition/creation of a new employee,
 * 			if a value is not present, then there is no need to continue
 * 			testing any subsequent test methods which rely on it.
 *
 * 			The check is done by calling the checkForTestSkip() at the beginning
 * 			of each test method (except the test method that sets the value).
 *
 * 			You can even mix the two methods by creating a test method which
 * 			calls on checkForTestSkip(), then add that test method to the
 * 			dependsOnMethods parameter list for any other test methods.
 */

/*
 * NOTE 3:
 * 			I opted to group test methods into separate java class files
 * 			only for the sake of simplicity.
 *
 * 			There are plenty of options available to us,
 * 			including (but not limited to) using TestNG groups,
 * 			TestNG include/exclude, custom annotations,
 * 			altering annotation values at runtime, etc etc.
 */

/*
 * NOTE 4:
 * 			TestNG's verbose default level for this project is set to 0.
 * 			If your tests run into errors and you want to see the
 * 			stacktraces in console, set the test-level verbose to 2
 * 			in the "alter" method within the "TestNGConsoleFormatter"
 * 			listener.
 */



public class Base
{
	static	String		empName			= "John Doe";
			String		empSalary		= "80000";
			String		empAge			= "28";
			String		newName			= "John Doe Jr";
			String		newSalary		= "90000";
			String		newAge			= "31";
	static	String		empId			= null;
			Response	empListResponse	= null;
			int			newSize			= 0;
			boolean		inEmployeeList	= false;


	@SuppressWarnings("rawtypes")
	static final Filter FORCE_JSON_RESPONSE_BODY = (reqSpec, respSpec, ctx) ->
	{
		Response response = ctx.next(reqSpec, respSpec);
		((RestAssuredResponseOptionsImpl)response).setContentType("application/json");
		return response;
	};

	@SuppressWarnings("rawtypes")
	static final Filter FORCE_UTF8_JSON_RESPONSE_BODY = (reqSpec, respSpec, ctx) ->
	{
		Response response = ctx.next(reqSpec, respSpec);
		((RestAssuredResponseOptionsImpl)response).setContentType("application/json;charset=utf-8");
		return response;
	};


	//	BeforeSuite
	void setMaxListSize(int outputWindowListMaxSize)
	{
		newSize = Math.max(0, outputWindowListMaxSize);

		if((newSize > 1) && ((newSize % 2) != 0))
		{
			newSize -= 1;
		}

		RestAssured.baseURI	= "http://dummy.restapiexample.com/api/v1";

		//	to filter all requests
		Filter[] filterArray = new Filter[] {FORCE_UTF8_JSON_RESPONSE_BODY};
		RestAssured.filters(FORCE_JSON_RESPONSE_BODY, filterArray);

		//	to selectively filter requests,
		//	add the following line after given(), before when()
//		filters(FORCE_JSON_RESPONSE_BODY)
	}


	private Response switchedResponse(Object bodyObject, String empIdString)
	{
		StackTraceElement[]		stacktrace		= Thread.currentThread().getStackTrace();
		StackTraceElement		element			= stacktrace[2];
		String					methodName		= element.getMethodName();

		RequestSender			genericSender	= bodyObject == null ? given().expect().defaultParser(Parser.JSON).when() : given().body(bodyObject).expect().defaultParser(Parser.JSON).when();
		Response 				thisResponse	= null;
		Response				processRequest	= null;

		//	Use switch when there are more than 5 cases, otherwise use if/else if.
		switch(methodName)
		{
			case "createEmployee":
				processRequest	= genericSender.post("/create");
				break;

			case "getSingleEmployee":
				processRequest = genericSender.get("/employee/" + empIdString);
				break;

			case "updateEmployee":
				processRequest = genericSender.put("/update/" + empIdString);
				break;

			case "deleteEmployee":
				processRequest = genericSender.delete("/delete/" + empIdString);
				break;

			case "getAllEmployees":
				processRequest = genericSender.get("/employees");
				break;

			case "deleteAllEmployees":
				processRequest = genericSender.delete("/delete/" + empIdString);
				break;

			default:
				processRequest = genericSender.get("/employees");
				break;
		}

								thisResponse	= processRequest
													.then()
														.assertThat()
															.contentType(ContentType.JSON).and()
															.statusCode(200).and()
															.time(lessThanOrEqualTo(3000L)).and()
															.header("Pragma", "no-cache")
														.extract()
															.response();

		return thisResponse;
	}


	//	Test 1
	public void createEmployee(boolean negativeTesting)
	{
		//	example of getting given().body() value from a HashMap<String, Object>
		HashMap<String, Object>		map				= new HashMap<>();
									map.put("name", empName);
									map.put("salary", empSalary);
									map.put("age", empAge);

		Response					response 		=	switchedResponse(map, null);
		String						responseString 	=	response.body().asString();


		if(responseString.startsWith("<"))
		{
			printFailure("Server returned invalid html/javascript/css.", "Server returned invalid html response.");
		}
		else if(responseString.contains("\"status\":\"failed\"") || responseString.contains("\"message\":\"Error!"))
		{
			System.out.println(response.prettyPrint());

			if(responseString.contains("Duplicate entry") && responseString.contains("employee_name_unique"))
			{
				System.err.println("\tEmployee creation failed as expected!\r\n\tEmployee names must be unique.\r\n\t"
														+ empName
														+ " is already employed here.");
			}
			else
			{
				printFailure("", "Server returned an error response.");
			}
		}
		else
		{
			JsonPath jp = new JsonPath(responseString);
			HashMap<String, Object>	data  = jp.get("data");

			System.out.println(jp.prettify() + "\r\n");

			if(jp.get("status").toString().equalsIgnoreCase("success"))
			{
				if(data == null)
				{
					printFailure("", "Server returned invalid response.");
				}

				if(negativeTesting == true)
				{
					String tmpName = data.get("name").toString();

					if(tmpName.equalsIgnoreCase(empName))
					{
						printFailure("", "Server allowed a duplicate name!");
					}
				}
			}
			else
			{
				printFailure("", "Server returned an error.");
			}

			empId = data.get("id").toString();

			System.out.println("\tEmployee successfully created!");
		}
	}


	//	Test 2
	public void getSingleEmployee()
	{
		if(empId == null)
		{
			checkForTestSkip(true);
		}

		Response 	response 		=	switchedResponse(null, empId);
		String		responseString 	=	response.body().asString();

		if(responseString.startsWith("<"))
		{
			printFailure("Server returned invalid html/javascript/css.", "Server returned invalid html response.");
		}
		else if(responseString.contains("\"status\":\"failed\"") || responseString.contains("\"message\":\"Error!"))
		{
			System.out.println(response.prettyPrint() + "\r\n");
			printFailure("", "Server returned an error response.");
		}
		else
		{
			JsonPath 				jp 		= new JsonPath(responseString);
			HashMap<String, Object>	data  	= jp.get("data");

			System.out.println(jp.prettify() + "\r\n");

			if(jp.get("status").toString().equalsIgnoreCase("success") && (data == null))
			{
				printFailure("", "Server returned invalid response.");
			}

			System.out.println("\tEmployee "    + data.get("employee_name").toString()
			                   + " with id " + data.get("id").toString()
			                   + " \r\n\twas successfully retrieved!");
		}
	}


	//	Test 3
	public void updateEmployee()
	{
		if(empId == null)
		{
			checkForTestSkip(true);
		}


		//	example of getting given().body() value from a hard-coded string
		/*
		String		bodyString		=	"{\r\n" +
												"  \"name\": \"" + newName + "\",\r\n" +
												"  \"salary\": \"" + newSalary + "\",\r\n" +
												"  \"age\": \"" + newAge + "\"\r\n" +
										"}";
		Response	response 		=	switchedResponse(bodyString);
		*/


		//	example of getting given().body() value from a .json file
		Response	response 		=	switchedResponse(generateStringFromResource("./src/test/resources/newinfo.json"), empId);
		String		responseString 	=	response.body().asString();


		if(responseString.startsWith("<"))
		{
			printFailure("Server returned invalid html/javascript/css.", "Server returned invalid html response.");
		}
		else if(responseString.contains("\"status\":\"failed\"") || responseString.contains("\"message\":\"Error!"))
		{
			System.out.println(response.prettyPrint() + "\r\n");
			printFailure("", "Server returned an error response.");
		}
		else
		{
			JsonPath 				jp 		= new JsonPath(responseString);
			HashMap<String, Object>	data  	= jp.get("data");

			System.out.println(jp.prettify());

			if(jp.get("status").toString().equalsIgnoreCase("success") && (data == null))
			{
				printFailure("", "Server returned invalid response.");
			}

			if(!data.get("employee_name").toString().equalsIgnoreCase(newName) 		||
			   !data.get("employee_salary").toString().equalsIgnoreCase(newSalary) 	||
			   !data.get("employee_age").toString().equalsIgnoreCase(newAge))
			{
				printFailure("Failed to update employee with id " + empId, responseString);
			}
			else
			{
				empName		= newName;
				empSalary	= newSalary;
				empAge		= newAge;

				System.out.println("\tEmployee " + data.get("employee_name").toString() + " successfully updated!");
			}
		}
	}


	//	Test 4
	public void deleteEmployee()
	{
		if(empId == null)
		{
			checkForTestSkip(true);
		}

		Response	response 		=	switchedResponse(null, empId);
		String		responseString 	=	response.body().asString();


		if(responseString.startsWith("<"))
		{
			printFailure("Server returned invalid html/javascript/css.", "Server returned invalid html response.");
		}
		else if(responseString.contains("\"status\":\"failed\"") || responseString.contains("\"message\":\"Error!"))
		{
			System.out.println(response.prettyPrint() + "\r\n");
			printFailure("", "Server returned an error response.");
		}
		else
		{
			JsonPath jp = new JsonPath(responseString);

			System.out.println(jp.prettify() + "\r\n");
			System.out.println("\tEmployee with id " + empId + " successfully deleted!");
		}
	}


	//	Test 5
	public void getAllEmployees()
	{
		StackTraceElement[]	stacktrace		= Thread.currentThread().getStackTrace();
		StackTraceElement	element			= stacktrace[2];
		String				methodName		= element.getMethodName();
		boolean				isSelfCalled	= (methodName.equalsIgnoreCase("checkForTestSkip") || methodName.equalsIgnoreCase("deleteAllEmployees"))
												&& element.getClassName().equalsIgnoreCase(Base.class.getName());

		Response			response 		=	switchedResponse(null, null);

		if(isSelfCalled)
		{
			empListResponse = response;
			return;
		}

		String 				responseString  = response.body().asString();


		if(responseString.startsWith("<"))
		{
			printFailure("Server returned invalid html/javascript/css.", "Server returned invalid html response.");
		}
		else if(responseString.contains("\"status\":\"failed\"") || responseString.contains("\"message\":\"Error!"))
		{
			System.out.println(response.prettyPrint() + "\r\n");
			printFailure("", "Server returned an error response.");
		}
		else
		{
			JsonPath							jp				= new JsonPath(responseString);		//	response.jsonPath();
			List<HashMap<String, Object>>		data			= jp.getList("data");				//	starts at root
			int									sizeOfList		= data.size();
			int									count			= 0;

			//	If we were to make changes to the list named data, create an Iterator for data, instead of using a for loop
			for(HashMap<String, Object> singleObject : data)
			{
				if(singleObject.get("employee_name").equals(empName) || singleObject.get("employee_name").equals(newName))
				{
					inEmployeeList = true;
					break;
				}
			}

			if((newSize > 0) && (sizeOfList > newSize))
			{
				// iterating over a list using the for/foreach constructs
				// implicitly creates an iterator which is necessarily inaccessible
				Iterator<HashMap<String, Object>>	iter		= data.iterator();
				int									x			= 0;
				int									halfSize	= newSize == 1 ? 1 : newSize / 2;

				while(iter.hasNext())
				{
					@SuppressWarnings("unused")
					HashMap<String, Object>			it			= iter.next();

					if((x > (halfSize - 1)) && (x < (sizeOfList - halfSize)))
					{
						iter.remove();
					}
					else
					{
						count++;
					}

					x++;
				}

				System.out.println(jp.prettify() + "\r\n");
				System.out.println("\tAll employees successfully retrieved!");
				System.out.println("\t(Showing " + count + " of " + sizeOfList + " in output)");
			}
			else
			{
				System.out.println(jp.prettify() + "\r\n");
				System.out.println("\tAll employees successfully retrieved!");
			}
		}
	}


	//	Test 6
	public void deleteAllEmployees()
	{
		getAllEmployees();

		if(empListResponse != null)
		{
			int								x				= 0;
			boolean							broken			= false;
			JsonPath						jp				= empListResponse.jsonPath();
			List<HashMap<String, Object>>	data			= jp.getList("data");
			String							responseString	= "";
			Response						response;

			// If we were to make changes to the list (data) itself,
			// create an iterator for data, instead of using a for loop
			for(HashMap<String, Object> singleObject : data)
			{
				if(singleObject.get("employee_name").equals(empName) || singleObject.get("employee_name").equals(newName))
				{
					String tmpId			= singleObject.get("id").toString();

					if(tmpId.equalsIgnoreCase(empId))
					{
						response 			=	switchedResponse(null, empId);
						responseString 		=	response.body().prettyPrint();

						x++;


						if(responseString.startsWith("<"))
						{
							broken = true;
							printFailure("Server returned invalid html/javascript/css.", "Server returned invalid html response.");
							break;
						}
						else if(responseString.contains("\"status\":\"failed\"") || responseString.contains("\"message\":\"Error!"))
						{
							System.out.println(response.prettyPrint() + "\r\n");
							broken = true;
							printFailure("", "Server returned an error response.");
							break;
						}

						break;
					}
				}
			}

			if((x == 0) && !broken)
			{
				System.out.println("\tNo employee(s) needed to be deleted.");
			}
			else
			{
				System.out.println(responseString);
			}
		}
	}


	private static String generateStringFromResource(String path)
	{
		try
		{
			return new String(Files.readAllBytes(Paths.get(path)));
		}
		catch(IOException e)
		{
			e.printStackTrace();
			return "";
		}
	}


	void printFailure(String failString, String responseString)
	{
		System.err.println("\t" + responseString);

		String	failTxt	= failString.isEmpty() ? "Error in body of response" : failString;
		String	failMsg	= "\r\n\r\n############################## " + failTxt + " ##############################\r\n";

		Assert.fail(failMsg);
	}


	private void checkForTestSkip(boolean lookUpEmpId)
	{
		if(lookUpEmpId)
		{
			getAllEmployees();

			JsonPath						jp		= empListResponse.jsonPath();
			List<HashMap<String, Object>>	data	= jp.getList("data");

			for(HashMap<String, Object> singleObject : data)
			{
				if(singleObject.get("employee_name").toString().equals(empName) || singleObject.get("employee_name").toString().equals(newName))
				{
					empId = singleObject.get("id").toString();
				}
			}
		}

		if(empId == null)
		{
//			String	skipTxt	= lookUpEmpId ? "Employee does not exist!"
//													: "A new employee must be created before running this test.";

//			String	skipMsg	= "\r\n\r\n############################## "
//													+ skipTxt
//													+ " ##############################\r\n";

//			System.err.println("Skipping this test due to employee ID not being set.");
//			throw new SkipException(skipMsg);

			SkipException se = new SkipException("Skipping this test due to employee ID not being set.");
			se.setStackTrace(new StackTraceElement[0]);
			throw se;
		}
	}
}