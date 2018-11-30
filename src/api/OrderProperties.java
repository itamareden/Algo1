package api;

public final class OrderProperties {
	
    public enum Status {
         SENT  ("SENT"),
         EXECUTED   ("EXECUTED"),
         CANCELLED   ("CANCELLED");

         private final String e_name;

         Status(String name)
             { this.e_name = name; }

         public String getName() { return e_name; }
    }

    public enum Direction {
    	LONG,
    	SHORT
    }

}
