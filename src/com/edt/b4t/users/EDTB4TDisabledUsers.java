package com.edt.b4t.users;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

import com.edt.b4t.util.TimeDate;

public class EDTB4TDisabledUsers implements Comparable<EDTB4TDisabledUsers>
{
    private String id = "",
                    name = "",
                    type = "";
    
    private Date signedIn = null;

    public String getId()
    {
        return this.id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getName()
    {
        return this.name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getType()
    {
        return this.type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getLastSignedIn()
    {
        SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy");
        return format.format(this.signedIn);
        
//        return this.lastSignedIn;
    }

    public void setLastSignedIn(String lastSignedIn)
    {
        this.signedIn = TimeDate.convertStringToDate(lastSignedIn);
        
//        System.out.println(this.lastSignedIn + " vs " + this.signedIn);
    }

    public Date getSignedIn()
    {
        return this.signedIn;
    }

    @Override
    public int compareTo(EDTB4TDisabledUsers obj)
    {
        int compareDates = obj.getSignedIn().hashCode();

        //ascending order
        return this.signedIn.hashCode() - compareDates;

        //descending order
        //return compareQuantity - this.signedIn;

    }

    public static Comparator<EDTB4TDisabledUsers> UserComparator = new Comparator<EDTB4TDisabledUsers>()
    {
        @Override
        public int compare(EDTB4TDisabledUsers user1, EDTB4TDisabledUsers user2)
        {
            Date emp1 = user1.getSignedIn(),
                 emp2 = user2.getSignedIn();

          //ascending order
          return emp1.compareTo(emp2);

          //descending order
//          return emp2.compareTo(emp1);
        }
    };
}