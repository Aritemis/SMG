/**
 *	@author Ariana Fairbanks
 */
package adapter;
import java.sql.*;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import model.SQLiteData;
import view.SMGFrame;

public class SMGController
{
	private SQLiteData database;
	private SMGFrame frame;
	private int state; //login screen, root menu, teacher menu, student menu, settings, password change, 
	private int lastState;
	private int ID;
	private String firstName;
	private String lastName;
	private String classID;
	private int permissions; //root, subroot, teacher, student

	public void start()
	{
		database = new SQLiteData(this);
		frame = new SMGFrame(this);
		logout();
	}
	
	public void checkLogin(String userName, String pass)
	{
		JPanel errorPanel = new JPanel();
		ResultSet res = database.compareLogin(userName, pass);
		try
		{
			if(database.isLocked(userName))
			{
				JOptionPane.showMessageDialog(errorPanel, "This account has been locked due to too many failed login attempts.", "Error", JOptionPane.ERROR_MESSAGE);
			}
			else if(res.next())
			{
				System.out.println("Done.");
				ID = res.getInt("ID");
				permissions = database.permission(ID);
				firstName = database.firstName(ID);
				lastName = database.lastName(ID);
				classID = database.classID(ID);
				returnToMenu();
				System.out.println(ID);
				frame.updateState();
				database.loginSuccess(userName);
			}
			else
			{
				JOptionPane.showMessageDialog(errorPanel, "Incorrect username or password.", "Error", JOptionPane.ERROR_MESSAGE);
				if(!(userName.equals("root")))
				{
					database.loginFailure(userName);
				}
			}
		}
		catch (SQLException e){	}
	}
	
	public void changePassword(String pass, String newPass)
	{
		boolean result = database.changeLogin(ID, pass, newPass);
		if(result)
		{
			JPanel errorPanel = new JPanel();
			JOptionPane.showMessageDialog(errorPanel, "Password changed.", "Done", JOptionPane.INFORMATION_MESSAGE);
			changeState(lastState);
		}
		else
		{
			JPanel errorPanel = new JPanel();
			JOptionPane.showMessageDialog(errorPanel, "Incorrect password.", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public void returnToMenu()
	{
		if(permissions < 2)
		{
			changeState(1);
			//root menu
		}
		else if(permissions == 2)
		{
			changeState(2);
			//teacher menu
		}
		else
		{
			changeState(3);
			//student menu
		}
	}
	
	public void logout()
	{
		state = 0;
		lastState = 0;
		ID = 0;
		firstName = "";
		lastName = "";
		classID = "";
		permissions = 3;
		frame.updateState();
	}
	
	public void changeState(int nextState)
	{
		lastState = state;
		state = nextState;
		frame.updateState();
	}
	
	public void unlockAccount(String userName)
	{
		JPanel errorPanel = new JPanel();
		if(permissions < 2)
		{
			database.loginSuccess(userName);
		}
		if(database.isLocked(userName))
		{
			JOptionPane.showMessageDialog(errorPanel, "Failed to unlock account.", "Error", JOptionPane.ERROR_MESSAGE);
		}
		else
		{
			JOptionPane.showMessageDialog(errorPanel, "Account successfully unlocked.", "", JOptionPane.INFORMATION_MESSAGE);
		}
	}
	
	public void resetPassword(String userName)
	{
		JPanel errorPanel = new JPanel();
		boolean change = false;
		if(permissions < 2)
		{
			change = database.resetPassword(userName);
		}
		if(!change)
		{
			JOptionPane.showMessageDialog(errorPanel, "Password not reset.", "", JOptionPane.ERROR_MESSAGE);
		}
		else
		{
			JOptionPane.showMessageDialog(errorPanel, "Password successfully reset.", "", JOptionPane.INFORMATION_MESSAGE);
		}
	}
	
	public String getName()
	{	return firstName;	}
	
	public String getLastName()
	{	return lastName; }
	
	public String getFullName()
	{	return firstName + " " + lastName;	}
	
	public String getClassID()
	{	return classID;	}
	
	public int getState()
	{	return state;	}
	
	public int getPerms()
	{	return permissions;	}

}
