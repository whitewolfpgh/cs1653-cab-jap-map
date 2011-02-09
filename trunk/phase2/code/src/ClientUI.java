import java.util.*;
import java.lang.*;
import java.io.*;

public class ClientUI
{
	public static Scanner s;
	public static String selection;
	public static void main(String [] args)
	{
		s = new Scanner(System.in);
		printMainMenu();
	}
	
	public static void printMainMenu()
	{
		System.out.println("===== Welcome to [Project Title] v0.1 (built by Chris Brack, Matt Parmelee and Josh Pogoersckey) =====");
		System.out.println("");
		System.out.println("What do you want to do next?");
		System.out.println(" [01] option one\t\t\t[07] option seven");
		System.out.println(" [02] option two\t\t\t[08] option eight");
		System.out.println(" [03] option three\t\t\t[09] option nine");
		System.out.println(" [04] option four\t\t\t[10] option ten");
		System.out.println(" [05] option five\t\t\t[11] option eleven");
		System.out.println(" [06] option six\t\t\t[12] logout");
		System.out.println("(Give zzz to any prompt in any task to return back to this menu)");
		System.out.println("");
		System.out.print("Your selection => ");
		selection = s.next();
		if (selection.compareTo("01") == 0)
		{
			optionOne();
		}
		else if (selection.compareTo("02") == 0)
		{
			optionTwo();
		}
		else if (selection.compareTo("03") == 0)
		{
			optionThree();
		}
		else if (selection.compareTo("04") == 0)
		{
			optionFour();
		}
		else if (selection.compareTo("05") == 0)
		{
			optionFive();
		}
		else if (selection.compareTo("06") == 0)
		{
			optionSix();
		}
		else if (selection.compareTo("07") == 0)
		{
			optionSeven();
		}
		else if (selection.compareTo("08") == 0)
		{
			optionEight();
		}
		else if (selection.compareTo("09") == 0)
		{
			optionNine();
		}
		else if (selection.compareTo("10") == 0)
		{
			optionTen();
		}
		else if (selection.compareTo("11") == 0)
		{
			optionEleven();
		}
		else if (selection.compareTo("12") == 0)
		{
			System.exit(0);
		}
		else
		{
			System.out.println("Invalid selection.");
			printMainMenu();
		}
	}
	
	public static void optionOne()
	{
		System.out.println("This is the submenu for option one:");
		System.out.println("[01] Sub-option one");
		System.out.println("[02] Sub-option two");
		System.out.println("[03] Sub-option three");
		System.out.println("");
		System.out.print("Your selection => ");
		selection = s.next();
		if (selection.compareTo("zzz") == 0)
		{
			printMainMenu();
		}
		else if (selection.compareTo("01") == 0)
		{
			System.out.println("You have selected sub-option one");
			System.out.println("");
			System.out.print("Your selection => ");
			selection = s.next();
			if (selection.compareTo("zzz") == 0)
			{
				printMainMenu();
			}
			else
			{
				System.out.println("Invalid selection.");
				optionOne();
			}
		}
		else if (selection.compareTo("02") == 0)
		{
			System.out.println("You have selected sub-option two");
			System.out.println("");
			System.out.print("Your selection => ");
			selection = s.next();
			if (selection.compareTo("zzz") == 0)
			{
				printMainMenu();
			}
			else
			{
				System.out.println("Invalid selection.");
				optionOne();
			}
		}
		else if (selection.compareTo("03") == 0)
		{
			System.out.println("You have selected sub-option three");
			System.out.println("");
			System.out.print("Your selection => ");
			selection = s.next();
			if (selection.compareTo("zzz") == 0)
			{
				printMainMenu();
			}
			else
			{
				System.out.println("Invalid selection.");
				optionOne();
			}
		}
		else
		{
			System.out.println("Invalid selection");
			optionOne();
		}
	}
	
	public static void optionTwo()
	{
		System.out.println("This is the submenu for option two:");
		System.out.println("[01] Sub-option one");
		System.out.println("[02] Sub-option two");
		System.out.println("[03] Sub-option three");
		System.out.println("");
		System.out.print("Your selection => ");
		selection = s.next();
		if (selection.compareTo("zzz") == 0)
		{
			printMainMenu();
		}
		else if (selection.compareTo("01") == 0)
		{
			System.out.println("You have selected sub-option one");
			System.out.println("");
			System.out.print("Your selection => ");
			selection = s.next();
			if (selection.compareTo("zzz") == 0)
			{
				printMainMenu();
			}
			else
			{
				System.out.println("Invalid selection.");
				optionTwo();
			}
		}
		else if (selection.compareTo("02") == 0)
		{
			System.out.println("You have selected sub-option two");
			System.out.println("");
			System.out.print("Your selection => ");
			selection = s.next();
			if (selection.compareTo("zzz") == 0)
			{
				printMainMenu();
			}
			else
			{
				System.out.println("Invalid selection.");
				optionTwo();
			}
		}
		else if (selection.compareTo("03") == 0)
		{
			System.out.println("You have selected sub-option three");
			System.out.println("");
			System.out.print("Your selection => ");
			selection = s.next();
			if (selection.compareTo("zzz") == 0)
			{
				printMainMenu();
			}
			else
			{
				System.out.println("Invalid selection.");
				optionTwo();
			}
		}
		else
		{
			System.out.println("Invalid selection");
			optionTwo();
		}
	}
	
	public static void optionThree()
	{
		System.out.println("This is the submenu for option three:");
		System.out.println("[01] Sub-option one");
		System.out.println("[02] Sub-option two");
		System.out.println("[03] Sub-option three");
		System.out.println("");
		System.out.print("Your selection => ");
		selection = s.next();
		if (selection.compareTo("zzz") == 0)
		{
			printMainMenu();
		}
		else if (selection.compareTo("01") == 0)
		{
			System.out.println("You have selected sub-option one");
			System.out.println("");
			System.out.print("Your selection => ");
			selection = s.next();
			if (selection.compareTo("zzz") == 0)
			{
				printMainMenu();
			}
			else
			{
				System.out.println("Invalid selection.");
				optionThree();
			}
		}
		else if (selection.compareTo("02") == 0)
		{
			System.out.println("You have selected sub-option two");
			System.out.println("");
			System.out.print("Your selection => ");
			selection = s.next();
			if (selection.compareTo("zzz") == 0)
			{
				printMainMenu();
			}
			else
			{
				System.out.println("Invalid selection.");
				optionThree();
			}
		}
		else if (selection.compareTo("03") == 0)
		{
			System.out.println("You have selected sub-option three");
			System.out.println("");
			System.out.print("Your selection => ");
			selection = s.next();
			if (selection.compareTo("zzz") == 0)
			{
				printMainMenu();
			}
			else
			{
				System.out.println("Invalid selection.");
				optionThree();
			}
		}
		else
		{
			System.out.println("Invalid selection");
			optionThree();
		}
	}
	
	public static void optionFour()
	{
		System.out.println("This is the submenu for option four:");
		System.out.println("[01] Sub-option one");
		System.out.println("[02] Sub-option two");
		System.out.println("[03] Sub-option three");
		System.out.println("");
		System.out.print("Your selection => ");
		selection = s.next();
		if (selection.compareTo("zzz") == 0)
		{
			printMainMenu();
		}
		else if (selection.compareTo("01") == 0)
		{
			System.out.println("You have selected sub-option one");
			System.out.println("");
			System.out.print("Your selection => ");
			selection = s.next();
			if (selection.compareTo("zzz") == 0)
			{
				printMainMenu();
			}
			else
			{
				System.out.println("Invalid selection.");
				optionFour();
			}
		}
		else if (selection.compareTo("02") == 0)
		{
			System.out.println("You have selected sub-option two");
			System.out.println("");
			System.out.print("Your selection => ");
			selection = s.next();
			if (selection.compareTo("zzz") == 0)
			{
				printMainMenu();
			}
			else
			{
				System.out.println("Invalid selection.");
				optionFour();
			}
		}
		else if (selection.compareTo("03") == 0)
		{
			System.out.println("You have selected sub-option three");
			System.out.println("");
			System.out.print("Your selection => ");
			selection = s.next();
			if (selection.compareTo("zzz") == 0)
			{
				printMainMenu();
			}
			else
			{
				System.out.println("Invalid selection.");
				optionFour();
			}
		}
		else
		{
			System.out.println("Invalid selection");
			optionFour();
		}
	}
	
	public static void optionFive()
	{
		System.out.println("This is the submenu for option five:");
		System.out.println("[01] Sub-option one");
		System.out.println("[02] Sub-option two");
		System.out.println("[03] Sub-option three");
		System.out.println("");
		System.out.print("Your selection => ");
		selection = s.next();
		if (selection.compareTo("zzz") == 0)
		{
			printMainMenu();
		}
		else if (selection.compareTo("01") == 0)
		{
			System.out.println("You have selected sub-option one");
			System.out.println("");
			System.out.print("Your selection => ");
			selection = s.next();
			if (selection.compareTo("zzz") == 0)
			{
				printMainMenu();
			}
			else
			{
				System.out.println("Invalid selection.");
				optionFive();
			}
		}
		else if (selection.compareTo("02") == 0)
		{
			System.out.println("You have selected sub-option two");
			System.out.println("");
			System.out.print("Your selection => ");
			selection = s.next();
			if (selection.compareTo("zzz") == 0)
			{
				printMainMenu();
			}
			else
			{
				System.out.println("Invalid selection.");
				optionFive();
			}
		}
		else if (selection.compareTo("03") == 0)
		{
			System.out.println("You have selected sub-option three");
			System.out.println("");
			System.out.print("Your selection => ");
			selection = s.next();
			if (selection.compareTo("zzz") == 0)
			{
				printMainMenu();
			}
			else
			{
				System.out.println("Invalid selection.");
				optionFive();
			}
		}
		else
		{
			System.out.println("Invalid selection");
			optionFive();
		}
	}
	
	public static void optionSix()
	{
		System.out.println("This is the submenu for option six:");
		System.out.println("[01] Sub-option one");
		System.out.println("[02] Sub-option two");
		System.out.println("[03] Sub-option three");
		System.out.println("");
		System.out.print("Your selection => ");
		selection = s.next();
		if (selection.compareTo("zzz") == 0)
		{
			printMainMenu();
		}
		else if (selection.compareTo("01") == 0)
		{
			System.out.println("You have selected sub-option one");
			System.out.println("");
			System.out.print("Your selection => ");
			selection = s.next();
			if (selection.compareTo("zzz") == 0)
			{
				printMainMenu();
			}
			else
			{
				System.out.println("Invalid selection.");
				optionSix();
			}
		}
		else if (selection.compareTo("02") == 0)
		{
			System.out.println("You have selected sub-option two");
			System.out.println("");
			System.out.print("Your selection => ");
			selection = s.next();
			if (selection.compareTo("zzz") == 0)
			{
				printMainMenu();
			}
			else
			{
				System.out.println("Invalid selection.");
				optionSix();
			}
		}
		else if (selection.compareTo("03") == 0)
		{
			System.out.println("You have selected sub-option three");
			System.out.println("");
			System.out.print("Your selection => ");
			selection = s.next();
			if (selection.compareTo("zzz") == 0)
			{
				printMainMenu();
			}
			else
			{
				System.out.println("Invalid selection.");
				optionSix();
			}
		}
		else
		{
			System.out.println("Invalid selection");
			optionSix();
		}
	}
	
	public static void optionSeven()
	{
		System.out.println("This is the submenu for option seven:");
		System.out.println("[01] Sub-option one");
		System.out.println("[02] Sub-option two");
		System.out.println("[03] Sub-option three");
		System.out.println("");
		System.out.print("Your selection => ");
		selection = s.next();
		if (selection.compareTo("zzz") == 0)
		{
			printMainMenu();
		}
		else if (selection.compareTo("01") == 0)
		{
			System.out.println("You have selected sub-option one");
			System.out.println("");
			System.out.print("Your selection => ");
			selection = s.next();
			if (selection.compareTo("zzz") == 0)
			{
				printMainMenu();
			}
			else
			{
				System.out.println("Invalid selection.");
				optionSeven();
			}
		}
		else if (selection.compareTo("02") == 0)
		{
			System.out.println("You have selected sub-option two");
			System.out.println("");
			System.out.print("Your selection => ");
			selection = s.next();
			if (selection.compareTo("zzz") == 0)
			{
				printMainMenu();
			}
			else
			{
				System.out.println("Invalid selection.");
				optionSeven();
			}
		}
		else if (selection.compareTo("03") == 0)
		{
			System.out.println("You have selected sub-option three");
			System.out.println("");
			System.out.print("Your selection => ");
			selection = s.next();
			if (selection.compareTo("zzz") == 0)
			{
				printMainMenu();
			}
			else
			{
				System.out.println("Invalid selection.");
				optionSeven();
			}
		}
		else
		{
			System.out.println("Invalid selection");
			optionSeven();
		}
	}
	
	public static void optionEight()
	{
		System.out.println("This is the submenu for option eight:");
		System.out.println("[01] Sub-option one");
		System.out.println("[02] Sub-option two");
		System.out.println("[03] Sub-option three");
		System.out.println("");
		System.out.print("Your selection => ");
		selection = s.next();
		if (selection.compareTo("zzz") == 0)
		{
			printMainMenu();
		}
		else if (selection.compareTo("01") == 0)
		{
			System.out.println("You have selected sub-option one");
			System.out.println("");
			System.out.print("Your selection => ");
			selection = s.next();
			if (selection.compareTo("zzz") == 0)
			{
				printMainMenu();
			}
			else
			{
				System.out.println("Invalid selection.");
				optionEight();
			}
		}
		else if (selection.compareTo("02") == 0)
		{
			System.out.println("You have selected sub-option two");
			System.out.println("");
			System.out.print("Your selection => ");
			selection = s.next();
			if (selection.compareTo("zzz") == 0)
			{
				printMainMenu();
			}
			else
			{
				System.out.println("Invalid selection.");
				optionEight();
			}
		}
		else if (selection.compareTo("03") == 0)
		{
			System.out.println("You have selected sub-option three");
			System.out.println("");
			System.out.print("Your selection => ");
			selection = s.next();
			if (selection.compareTo("zzz") == 0)
			{
				printMainMenu();
			}
			else
			{
				System.out.println("Invalid selection.");
				optionEight();
			}
		}
		else
		{
			System.out.println("Invalid selection");
			optionEight();
		}
	}
	
	public static void optionNine()
	{
		System.out.println("This is the submenu for option nine:");
		System.out.println("[01] Sub-option one");
		System.out.println("[02] Sub-option two");
		System.out.println("[03] Sub-option three");
		System.out.println("");
		System.out.print("Your selection => ");
		selection = s.next();
		if (selection.compareTo("zzz") == 0)
		{
			printMainMenu();
		}
		else if (selection.compareTo("01") == 0)
		{
			System.out.println("You have selected sub-option one");
			System.out.println("");
			System.out.print("Your selection => ");
			selection = s.next();
			if (selection.compareTo("zzz") == 0)
			{
				printMainMenu();
			}
			else
			{
				System.out.println("Invalid selection.");
				optionNine();
			}
		}
		else if (selection.compareTo("02") == 0)
		{
			System.out.println("You have selected sub-option two");
			System.out.println("");
			System.out.print("Your selection => ");
			selection = s.next();
			if (selection.compareTo("zzz") == 0)
			{
				printMainMenu();
			}
			else
			{
				System.out.println("Invalid selection.");
				optionNine();
			}
		}
		else if (selection.compareTo("03") == 0)
		{
			System.out.println("You have selected sub-option three");
			System.out.println("");
			System.out.print("Your selection => ");
			selection = s.next();
			if (selection.compareTo("zzz") == 0)
			{
				printMainMenu();
			}
			else
			{
				System.out.println("Invalid selection.");
				optionNine();
			}
		}
		else
		{
			System.out.println("Invalid selection");
			optionNine();
		}
	}
	
	public static void optionTen()
	{
		System.out.println("This is the submenu for option ten:");
		System.out.println("[01] Sub-option one");
		System.out.println("[02] Sub-option two");
		System.out.println("[03] Sub-option three");
		System.out.println("");
		System.out.print("Your selection => ");
		selection = s.next();
		if (selection.compareTo("zzz") == 0)
		{
			printMainMenu();
		}
		else if (selection.compareTo("01") == 0)
		{
			System.out.println("You have selected sub-option one");
			System.out.println("");
			System.out.print("Your selection => ");
			selection = s.next();
			if (selection.compareTo("zzz") == 0)
			{
				printMainMenu();
			}
			else
			{
				System.out.println("Invalid selection.");
				optionTen();
			}
		}
		else if (selection.compareTo("02") == 0)
		{
			System.out.println("You have selected sub-option two");
			System.out.println("");
			System.out.print("Your selection => ");
			selection = s.next();
			if (selection.compareTo("zzz") == 0)
			{
				printMainMenu();
			}
			else
			{
				System.out.println("Invalid selection.");
				optionTen();
			}
		}
		else if (selection.compareTo("03") == 0)
		{
			System.out.println("You have selected sub-option three");
			System.out.println("");
			System.out.print("Your selection => ");
			selection = s.next();
			if (selection.compareTo("zzz") == 0)
			{
				printMainMenu();
			}
			else
			{
				System.out.println("Invalid selection.");
				optionTen();
			}
		}
		else
		{
			System.out.println("Invalid selection");
			optionTen();
		}
	}
	
	public static void optionEleven()
	{
		System.out.println("This is the submenu for option eleven:");
		System.out.println("[01] Sub-option one");
		System.out.println("[02] Sub-option two");
		System.out.println("[03] Sub-option three");
		System.out.println("");
		System.out.print("Your selection => ");
		selection = s.next();
		if (selection.compareTo("zzz") == 0)
		{
			printMainMenu();
		}
		else if (selection.compareTo("01") == 0)
		{
			System.out.println("You have selected sub-option one");
			System.out.println("");
			System.out.print("Your selection => ");
			selection = s.next();
			if (selection.compareTo("zzz") == 0)
			{
				printMainMenu();
			}
			else
			{
				System.out.println("Invalid selection.");
				optionEleven();
			}
		}
		else if (selection.compareTo("02") == 0)
		{
			System.out.println("You have selected sub-option two");
			System.out.println("");
			System.out.print("Your selection => ");
			selection = s.next();
			if (selection.compareTo("zzz") == 0)
			{
				printMainMenu();
			}
			else
			{
				System.out.println("Invalid selection.");
				optionEleven();
			}
		}
		else if (selection.compareTo("03") == 0)
		{
			System.out.println("You have selected sub-option three");
			System.out.println("");
			System.out.print("Your selection => ");
			selection = s.next();
			if (selection.compareTo("zzz") == 0)
			{
				printMainMenu();
			}
			else
			{
				System.out.println("Invalid selection.");
				optionEleven();
			}
		}
		else
		{
			System.out.println("Invalid selection");
			optionEleven();
		}
	}
}