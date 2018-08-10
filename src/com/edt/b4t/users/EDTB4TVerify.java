package com.edt.b4t.users;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.edt.b4t.B4TTimesheets;
import com.edt.b4t.B4TUsers;
import com.edt.b4t.util.AppProperties;
import com.edt.b4t.util.Str;
import com.edt.b4t.util.TimeDate;

public class EDTB4TVerify
{
	public static String body = "";
	
	public EDTB4TVerify()
	{
		super();
		
		AppProperties.setFname("props/UserVerification");
		AppProperties.init();
	}
	
	public static void main(String[] args) 
			throws IOException
	{
		EDTB4TVerify verifiy = new EDTB4TVerify();
		
	    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS"),
	    				 tsFormat = new SimpleDateFormat("yyyy-MM-dd");
	    
        System.out.println("Start: " + format.format(new Date()));

		B4TUsers users = new B4TUsers("");
		
		System.out.println(users.getIds().size() + " dreamers");
		
		Date weeksAgo = TimeDate.todayMinus(14);
		
		String params = Str.convertToURL("?$filter=createdDate ge '"
		                                 + tsFormat.format(weeksAgo)
		                                 + "'&$orderby=entryDate desc");
//		                                 + "'&$orderby=createdDate desc");
		
		B4TTimesheets timesheets = new B4TTimesheets(params);
		
		String demoFile = "verified-report.html";
		
		File file = new File(demoFile);
		
		if (file.exists())
			file.delete();
		
		FileWriter fname = new FileWriter(demoFile);
		
		PrintWriter buffer = new PrintWriter(fname);

		verifiy.report(buffer, users, timesheets);
		
		buffer.println(EDTB4TVerify.body);
		buffer.close();
		
		com.edt.email.Outlook365Email email = new com.edt.email.Outlook365Email("props/UserVerification");
		email.send(EDTB4TVerify.body);
		
        System.out.println("Done: " + format.format(new Date()));
	}

	public static void println(String str)
	{
		EDTB4TVerify.body += str;
	}
	
	private void reportHeader(PrintWriter buffer)
	{
		EDTB4TVerify.println("<html>");
		EDTB4TVerify.println(Str.CR + "<head>");
		EDTB4TVerify.println(Str.CR + "<meta charset=\"UTF-8\">");
		EDTB4TVerify.println(Str.CR + "<link rel=\"icon\" href=\"https://edtssl.eagledream-hosting.com/wp-content/uploads/2015/02/edt_fav_2.png\"/>");
		EDTB4TVerify.println(Str.CR + "<title>EDT - Bill4Time Users</title>");
		EDTB4TVerify.println(Str.CR + "<script src=\"https://www.w3schools.com/lib/w3.js\"></script>");

		EDTB4TVerify.println(Str.CR + "</head>");
		EDTB4TVerify.println(Str.CR + "<body>");
		EDTB4TVerify.println(Str.CR + "<center>");
		
		EDTB4TVerify.println(Str.CR + "<img src=" + System.getProperty("image.header") + ">");
		
		EDTB4TVerify.println(Str.CR + "<table border=\"1\" removed=#FFFFFF cellspacing=\"1\" cellpadding=\"10\">");
		EDTB4TVerify.println(Str.CR + "<thead>");
		EDTB4TVerify.println(Str.CR + "<tr>");
		
//		id,fname,lname,userType,status,position,department
		EDTB4TVerify.println(Str.CR + "<th bgcolor=\"midnightblue\"><font color=\"white\">Id</font></th>");
		EDTB4TVerify.println(Str.CR + "<th bgcolor=\"midnightblue\"><font color=\"white\">User</font></th>");
		EDTB4TVerify.println(Str.CR + "<th bgcolor=\"midnightblue\"><font color=\"white\">Type</font></th>");
		
		EDTB4TVerify.println(Str.CR + "<th bgcolor=\"midnightblue\"><font color=\"white\">Last Signed In</font></th>");
//		EDTB4TVerify.println(Str.CR + "<th bgcolor=\"midnightblue\"><font color=\"white\">Created</font></th>");

		EDTB4TVerify.println(Str.CR + "</tr>");
		EDTB4TVerify.println(Str.CR + "</thead>");
	}
	
	private void report(PrintWriter buffer, B4TUsers users, B4TTimesheets timesheets)
	{
	    List <EDTB4TDisabledUsers> emps = new ArrayList <EDTB4TDisabledUsers>();
	    
		this.reportHeader(buffer);
		
		for (int index = 0; index < users.getIds().size() - 1; index++)
		{
			if ((!"Active".equals(users.getStatuses().get(index))) || ("2".equals(users.getIds().get(index))))
				continue;
			
			if (!this.validate(users.getIds().get(index), timesheets))
			{
			    boolean isCoOp = ("Limited User".equals(users.getUserTypes().get(index)));
			    
			    EDTB4TDisabledUsers emp = new EDTB4TDisabledUsers();
			    
			    emp.setId(users.getIds().get(index));
			    emp.setName(users.getFnames().get(index) + " " + users.getLnames().get(index));
			    emp.setType((isCoOp) ? "CoOp" : "FTE");
			    emp.setLastSignedIn(this.getEntryDate(users.getIds().get(index)));
			    emps.add(emp);
			    
//			    System.out.println(emp.getLastSignedIn() + " vs " + emp.getSignedIn());
			}
		}
		
		EDTB4TDisabledUsers[] emp = new EDTB4TDisabledUsers[emps.size()];
        
        for (int index = 0; index < emps.size(); index++)
            emp[index] = emps.get(index);

        Arrays.sort(emp, EDTB4TDisabledUsers.UserComparator);
//        this.print(emp);

		for (int index = 0; index < emps.size(); index++)
        {
            boolean isCoOp = ("CoOp".equals(emp[index].getType()));
        
            EDTB4TVerify.println(Str.CR + "<tr>");
            
            String typeColumn = (isCoOp) ? Str.CR + "<font color=\"red\"><center>CoOp</center></font>"
                                         : Str.CR + "<font color=\"blue\"><center>FTE</center></font>";

            EDTB4TVerify.println(Str.CR + "<td><center>" + emp[index].getId() + "</center></td>");
            EDTB4TVerify.println(Str.CR + "<td><center>" + emp[index].getName() + "</center></td>");
            EDTB4TVerify.println(Str.CR + "<td><center>" + typeColumn + "</center></td>");
            EDTB4TVerify.println(Str.CR + "<td><center>" + emp[index].getLastSignedIn() + "</center></td>");
            EDTB4TVerify.println(Str.CR + "</tr>");
        }
		
        EDTB4TVerify.println(Str.CR + "<tr>");
        EDTB4TVerify.println(Str.CR + "<td colspan=\"4\"><center>" + emps.size() + " Users Costing EDT $" + (emps.size() * 5) + "</center></td>");
        EDTB4TVerify.println(Str.CR + "</tr>");

		EDTB4TVerify.println(Str.CR + "</table>");
		EDTB4TVerify.println(Str.CR + "</center>");
		EDTB4TVerify.println(Str.CR + "</body></html>");
	}
	
//	private void print(EDTB4TDisabledUsers[] emp)
//	{
//       int i = 0;
//        
//        for (EDTB4TDisabledUsers temp: emp)
//        {
//            System.out.println(Str.CR + "emps " + ++i + ": "
//                                + temp.getId()
//                                + " "
//                                + temp.getName()
//                                + " / "
//                                + temp.getLastSignedIn()
//                                );
//        }
//        
//        System.out.println(Str.CR + "");
//	}
	
	private String getEntryDate(String userId)
	{
		String str = "NA";
		
		B4TTimesheets sheets = new B4TTimesheets(Str.convertToURL("?$filter=userId eq " 
		                                        + userId
				                                + "&$orderby=entryDate desc"));
		
		if (sheets.getCreatedDates().size() > 0)
		    str = sheets.getCreatedDates().get(0);
		
		return str;
	}

	private boolean validate(String user, B4TTimesheets timesheets)
	{
		boolean validated = false;
		
		for (int index = 0; ((!validated) && (index < timesheets.getUserIds().size())); index++)
			validated = user.equals(timesheets.getUserIds().get(index));
			
		return validated;
	}
}
