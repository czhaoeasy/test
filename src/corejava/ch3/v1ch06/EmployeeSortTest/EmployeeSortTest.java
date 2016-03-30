import java.util.*;

/**
 * This program demonstrates the use of the Comparable interface.
 * @version 1.30 2004-02-27
 * @author Cay Horstmann
 */
public class EmployeeSortTest
{
   public static void main(String[] args)
   {
      Employee[] staff = new Employee[3];

      staff[0] = new Employee("Harry Hacker", 35000,0);
      staff[1] = new Employee("Carl Cracker", 75000,0);
      staff[2] = new Employee("Tony Tester", 38000,0);

      Arrays.sort(staff);

      // print out information about all Employee objects
     
      for(Employee e : staff)
      {
    	  e.calculateBonus(0.042);
      }
      
      for (Employee e : staff)
          System.out.println("name=" + e.getName() + ",salary=" + e.getSalary() + ",bonus=" + e.getBonus());
      
      if (staff[2].compareTo(staff[1]) == 1)
      	  System.out.println(staff[2].getName() + "'s salary is higer than " +staff[1].getName() + "'s. ");
      ;
   }
}

class Employee implements Comparable<Employee>
{
   public Employee(String n, double s, double b)
   {
      name = n;
      salary = s;
      bonus = b;
      
   }

   public String getName()
   {
      return name;
   }

   public double getSalary()
   {
      return salary;
   }
   
   public double getBonus()
   {
      return bonus;
   }


   public void raiseSalary(double byPercent)
   {
      double raise = salary * byPercent / 100;
      salary += raise;
   }
   
  public void calculateBonus(double byPercent)
  {
	   double bonus = salary * 12 * byPercent;
	   this.bonus += bonus;
	   
   }
   /**
    * Compares employees by salary
    * @param other another Employee object
    * @return a negative value if this employee has a lower salary than
    * otherObject, 0 if the salaries are the same, a positive value otherwise
    */
   public int compareTo(Employee other)
   {
      if (salary < other.salary) return -1;
      if (salary > other.salary) return 1;
      return 0;
   }
   
  // public int compareTo(Employee other)
  // {
	//   if(bonus < other.bonus) return -1;
	//   if(bonus > other.bonus) return 1;
	//   return 0;
 //  }

   private String name;
   private double salary;
   private double bonus;
}
