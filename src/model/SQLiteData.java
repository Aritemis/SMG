/**
 *	@author Ariana Fairbanks
 */
package model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import adapter.Controller;

public class SQLiteData
{
	private Controller base;
	private static Connection con;
	private static boolean hasData;

	public SQLiteData(Controller base)
	{
		this.base = base;
		hasData = false;
		getConnection();
		initial();
	}
	
	public ResultSet query(String SQLCommand)
	{
		ResultSet res = null;
		try
		{
			if (con == null)
			{	getConnection();	}
			Statement statement = con.createStatement();
			res = statement.executeQuery(SQLCommand);
		}
		catch (SQLException e){e.printStackTrace();}
		return res;
	}
	
	public ResultSet compareLogin(String userName, String pass)
	{
		ResultSet res = null;
		try
		{
			if (con == null)
			{	getConnection();	}
			String query = "SELECT ID FROM USER WHERE userName = ? AND pass = ?";
			PreparedStatement preparedStatement = con.prepareStatement(query);
			preparedStatement.setString(1, userName);
			preparedStatement.setString(2, pass);
			res = preparedStatement.executeQuery();
		}
		catch (SQLException e){}
		return res;
	}
	
	public boolean changeLogin(int ID, String pass, String newPass)
	{
		boolean match = false;
		if(base.getPerms() < 3)
		{
			try
			{
				ResultSet res = null;
				if (con == null)
				{	getConnection();	}
				String query = "SELECT pass FROM USER WHERE ID = ?";
				PreparedStatement preparedStatement = con.prepareStatement(query);
				preparedStatement.setInt(1, ID);
				res = preparedStatement.executeQuery();
				String result = res.getString("pass");
				if (pass.equals(result))
				{
					match = true;
					query = "UPDATE USER SET pass = ? WHERE ID = ?";
					preparedStatement = con.prepareStatement(query);
					preparedStatement.setString(1, newPass);
					preparedStatement.setInt(2, ID);
					preparedStatement.executeUpdate();
				}
			}
			catch (SQLException e){e.printStackTrace();}
		}
		return match;
	}
	
	public boolean resetPassword(String userName)
	{
		boolean result = true;
		if(base.getPerms() < 2)
		{
			try
			{
				if (con == null)
				{	getConnection();	}
				ResultSet res = null;
				String query = "SELECT ID FROM USER WHERE userName = ?";
				PreparedStatement preparedStatement = con.prepareStatement(query);
				preparedStatement.setString(1, userName);
				res = preparedStatement.executeQuery();
				String id = "" + res.getInt("ID");
				query = "UPDATE USER SET pass = ? WHERE userName = ?";
				preparedStatement = con.prepareStatement(query);
				preparedStatement.setString(1, id);
				preparedStatement.setString(2, userName);
				preparedStatement.executeUpdate();
			}
			catch (SQLException e)
			{
				e.printStackTrace(); 
				result = false;
			}
		}
		return result;
	}
	
	public void loginSuccess(String userName)
	{
		try
		{
			if (con == null)
			{	getConnection();	}
			String query = "UPDATE USER SET failedAttempts = ? WHERE userName = ?";
			PreparedStatement preparedStatement = con.prepareStatement(query);
			preparedStatement.setInt(1, 0);
			preparedStatement.setString(2, userName);
			preparedStatement.executeUpdate();
			query = "UPDATE USER SET isLocked = ? WHERE userName = ?";
			preparedStatement = con.prepareStatement(query);
			preparedStatement.setBoolean(1, false);
			preparedStatement.setString(2, userName);
			preparedStatement.executeUpdate();
		}
		catch (SQLException e){ e.printStackTrace(); }
	}
	
	public void loginFailure(String userName)
	{
		try
		{
			ResultSet res = null;
			if (con == null)
			{	getConnection();	}
			String query = "SELECT failedAttempts FROM USER WHERE userName = ?";
			PreparedStatement preparedStatement = con.prepareStatement(query);
			preparedStatement.setString(1, userName);
			res = preparedStatement.executeQuery();
			int result = res.getInt("failedAttempts");
			result += 1;
			query = "UPDATE USER SET failedAttempts = ? WHERE userName = ?";
			preparedStatement = con.prepareStatement(query);
			preparedStatement.setInt(1, result);
			preparedStatement.setString(2, userName);
			preparedStatement.executeUpdate();
			if (result > 5)
			{
				query = "UPDATE USER SET isLocked = ? WHERE userName = ?";
				preparedStatement = con.prepareStatement(query);
				preparedStatement.setBoolean(1, true);
				preparedStatement.setString(2, userName);
				preparedStatement.executeUpdate();
			}
		}
		catch (SQLException e){e.printStackTrace();}
	}
	
	public boolean isLocked(String userName)
	{
		boolean result = true;
		ResultSet res = null;
		PreparedStatement preparedStatement;
		if (con == null)
		{	getConnection();	}
		try 
		{
			String query = "SELECT isLocked FROM USER WHERE userName = ?";
			preparedStatement = con.prepareStatement(query);
			preparedStatement.setString(1, userName);
			res = preparedStatement.executeQuery();
			result = res.getBoolean("isLocked");
		}		
		catch (SQLException e){e.printStackTrace();}
		return result;
	}
	
	public ResultSet getUserInfo(int id)
	{
		ResultSet res = null;
		PreparedStatement preparedStatement;
		if (con == null)
		{	getConnection();	}
		try 
		{
			String query = "SELECT firstName, lastName, permission, classID FROM USER WHERE ID = ?";
			preparedStatement = con.prepareStatement(query);
			preparedStatement.setInt(1, id);
			res = preparedStatement.executeQuery();
		}		
		catch (SQLException e){e.printStackTrace();}
		return res;
	}
	
	public ResultSet getCustomEquationInfo(String classID)
	{
		ResultSet res = null;
		PreparedStatement preparedStatement;
		if (con == null)
		{	getConnection();	}
		try 
		{
			String query = "SELECT * FROM CUSTOM_EQUATIONS WHERE classID = ?";
			preparedStatement = con.prepareStatement(query);
			preparedStatement.setString(1, classID);
			res = preparedStatement.executeQuery();
		}		
		catch (SQLException e){e.printStackTrace();}
		return res;
	}
	
	public void updateCustomEquations(String classID, String equationString, int frequency, int numberOfEquations)
	{
		try
		{
			if (con == null)
			{	getConnection();	}
			String query = "UPDATE CUSTOM_EQUATIONS SET questionList = ?, frequency = ?, numberOfEquations = ? WHERE classID = ?";
			PreparedStatement preparedStatement = con.prepareStatement(query);
			preparedStatement.setString(1, equationString);
			preparedStatement.setInt(2, frequency);
			preparedStatement.setInt(3, numberOfEquations);
			preparedStatement.setString(4, classID);
			preparedStatement.executeUpdate();
		}
		catch (SQLException e)
		{
			e.printStackTrace(); 
		}
	}
	
	public int importUsers(File csvFile)
	{
		//CSV file should have user ID, first name, last name, class ID, permission level, new line.
		//Username default - first letter of first name, first letter of last name, last 3 digits of user ID 
		//Password default - user ID
		
		//Username default subject to change.
		//Duplicate users are ignored.
        BufferedReader br = null;
        String line = "";
        String delimiter = ",";
        int currentLine = 0;
        if(base.getPerms() < 2)
        {
            try 
            {
                br = new BufferedReader(new FileReader(csvFile));
                while ((line = br.readLine()) != null) 
                {
                	currentLine++;
                	//TODO catch invalid input
                    String[] values = line.split(delimiter);
                    String idString = values[0].trim();
                    String firstName = values[1].trim();
                    String lastName = values[2].trim();
                    String classID = values[3].trim();
                    String userName = firstName.substring(0, 1) 
                    				+ lastName.substring(0, 1) 
                    				+ idString.substring(idString.length() - 4);
                    System.out.println(userName);
                    int id = Integer.parseInt(idString);
                    if(userExists(id))
                    {
                    	System.out.println("User with ID " + idString + " already exists.");
                    }
                    else
                    {
                    	addUser(id, userName, idString, firstName, lastName, classID, Integer.parseInt(values[4].trim()));
                    }
                }
                currentLine = -1;
            } 
            catch (IOException e) 
            {
                e.printStackTrace();
            } 
            finally 
            {
                if (br != null) 
                {
                    try 
                    {	br.close();	} 
                    catch (IOException e) 
                    {	 e.printStackTrace();	}
                }
            }
        }
        return currentLine;
    }

	private void addUser(int id, String userName, String pass, String firstName, String lastName, String classID, int permissions)
	{
		if (con == null)
		{	getConnection();	}
		try 
		{
			PreparedStatement preparedStatement;
			preparedStatement = con.prepareStatement("INSERT INTO USER VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?);");
			preparedStatement.setInt(1, id);
			preparedStatement.setString(2, userName);
			preparedStatement.setString(3, pass);
			preparedStatement.setString(4, firstName);
			preparedStatement.setString(5, lastName);
			preparedStatement.setString(6, classID);
			preparedStatement.setInt(7, permissions);
			preparedStatement.setInt(8, 0);
			preparedStatement.setBoolean(9, false);
			preparedStatement.execute();
		} 
		catch (SQLException e) {e.printStackTrace();}
	}

	private void addCustomEquations(String classID, String questionList, int numberOfEquations, int frequency)
	{
		if (con == null)
		{	getConnection();	}
		try 
		{
			PreparedStatement preparedStatement;
			preparedStatement = con.prepareStatement("INSERT INTO CUSTOM_EQUATIONS VALUES( ?, ?, ?, ?);");		
			preparedStatement.setString(1, classID);
			preparedStatement.setString(2, questionList);
			preparedStatement.setInt(3, numberOfEquations);
			preparedStatement.setInt(4, frequency);
			preparedStatement.execute();
		} 
		catch (SQLException e) {e.printStackTrace();}
	}
	
	// maybe make this public? not sure yet
	private void addStudentScoreRecord(int studentID, int recordID)
	{
		if (con == null)
		{	getConnection();	}
		try 
		{
			PreparedStatement preparedStatement;
			preparedStatement = con.prepareStatement("INSERT INTO STUDENT_SCORE_RECORDS VALUES(?, ?);");
			preparedStatement.setInt(1, studentID);
			preparedStatement.setInt(2, recordID);
			preparedStatement.executeUpdate();
		}
		catch (SQLException e) {e.printStackTrace();}
	}
	
	// Method to retrieve the records for a student. Called from TeacherMenu/ViewRecords
	public ResultSet selectStudentRecord(int studentID)
	{
		ResultSet studentRecords = null;
		PreparedStatement preparedStatement;
		if (con == null)
		{	getConnection();	}
		try 
		{
			String query = "SELECT * from STUDENT_SCORE_RECORDS WHERE studentID=?";
			preparedStatement = con.prepareStatement(query);
			preparedStatement.setInt(1, studentID);
			studentRecords = preparedStatement.executeQuery();
			return studentRecords;	
		}
		catch (SQLException e) { e.printStackTrace(); }		
		return studentRecords;
	}
	
	private boolean userExists(int ID)
	{
		boolean result = false;
		ResultSet res = null;
		try
		{
			if (con == null)
			{	getConnection();	}
			String query = "SELECT ID FROM USER WHERE ID = ?";
			PreparedStatement preparedStatement = con.prepareStatement(query);
			preparedStatement.setInt(1, ID);
			res = preparedStatement.executeQuery();
			result = res.next();
		}
		catch (SQLException e){}
		return result;
	}

	private void getConnection()
	{
		try
		{
			Class.forName("org.sqlite.JDBC");
			//database path
			//if there is no database there a new one will be created
			String databaseFilePath = "jdbc:sqlite:C:/ProgramData/MPDKWID";
			con = DriverManager.getConnection(databaseFilePath);
		}
		catch (SQLException | ClassNotFoundException e)
		{
			//TODO maybe make this work for Mac as well???
			try			
			{
				Class.forName("org.sqlite.JDBC");
				File homedir = new File(System.getProperty("user.home"));
				String databaseFilePath = "jdbc:sqlite:" + homedir + "/MPDKWID";
				con = DriverManager.getConnection(databaseFilePath);
				// https://docs.oracle.com/javase/8/docs/api/java/nio/file/Files.html#setPosixFilePermissions-java.nio.file.Path-java.util.Set-
			}
			catch (SQLException | ClassNotFoundException e2)
			{
				e2.printStackTrace();
				System.out.println("linux fix didn't work");
			}
		}
		initial();
	}

	private void initial() 
	{
		if (!hasData)
		{
			hasData = true;
			Statement state;
			try
			{
				state = con.createStatement();
				
				// drop table if exists
				//Commented out for demo
				//state.execute("DROP TABLE IF EXISTS USER;");
				
				ResultSet res = state.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='USER'");
				if (!res.next())
				{
					System.out.println("Building the User table.");
					state = con.createStatement();
					state.executeUpdate("CREATE TABLE USER(ID INTEGER," + "userName VARCHAR(15)," + "pass VARCHAR(15),"
							+ "firstName VARCHAR(30)," + "lastName VARCHAR(30)," + "classID VARCHAR(5),"
							+ "permission INTEGER," + "failedAttempts INTEGER," + "isLocked BOOLEAN,"
							+ "PRIMARY KEY (ID));");
					
					addUser(000000, "root", "root", "Root", "User", "00", 0);
					addUser(111111, "deft", "deft", "Default", "Teacher", "1A", 2);
					addUser(222222, "defs", "defs", "Default", "Student", "1A", 3);
				}
				
				//Drop table			
				state.execute("DROP TABLE IF EXISTS CUSTOM_EQUATIONS;");
				
				ResultSet customEq = state.executeQuery("SELECT name FROM sqlite_master WHERE type='table' " +
						"AND name='CUSTOM_EQUATIONS'");
				if (!customEq.next())
				{
					System.out.println("Building the custom equations table.");
					state = con.createStatement();
					state.executeUpdate("CREATE TABLE CUSTOM_EQUATIONS(classID VARCHAR(5),"
							+ "questionList VARCHAR(600)," + "numberOfEquations INTEGER," + "frequency INTEGER," 
							+ "FOREIGN KEY (classID) REFERENCES USER(classID),"
							+ "PRIMARY KEY (classID));");
					
					addCustomEquations("1A", "5+10:7-2", 2, 5);
				}
				
				// drop table if exists
				//TODO Once we are done testing we want to get rid of this logic so it doesn't reset every time you open the application.
				state.execute("DROP TABLE IF EXISTS STUDENT_SCORE_RECORDS;");
				ResultSet studentScoreRecords = state.executeQuery("SELECT name FROM sqlite_master WHERE type='table' " +
						"AND name='STUDENT_SCORE_RECORDS'");
				
				if (!studentScoreRecords.next())
				{
					System.out.println("Building Student Score Records table.");
					state = con.createStatement();
					state.executeUpdate("CREATE TABLE STUDENT_SCORE_RECORDS(studentID INTEGER," + "recordID INTEGER," + 
							"PRIMARY KEY (studentID, recordID)," + "FOREIGN KEY (studentID) REFERENCES USER(ID));");
					
					addStudentScoreRecord(222222, 000000);
					addStudentScoreRecord(222222, 000001);
					addStudentScoreRecord(222222, 000002);
				}
			}
			catch (SQLException e){ e.printStackTrace(); }
		}
	}

}
