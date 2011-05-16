/**********************************************************
   Fraction.java - a Java representation of a fraction

   Author: Diane Kramer
   History:
      Created:   9/25/01
      Modified: 10/16/01 - added gcd method to reduce fraction
      Modified: 02/19/06 - include licence terms in comments

   Description:  This class provides storage for internal
   representation of, and methods to manipulate fractions.
   A fraction consists of two integers, one for numerator
   and one for denominator.  An example fraction is 3/4.
   A valid fraction must not have zero in the denominator.

   This software is licensed "as-is" under a non-exclusive,
   worldwide, royalty-free right to reproduce the software,
   prepare derivative works of the software and distribute
   the software or any derivative works created.  The user
   bears the risk of using it.  No express warranties,
   guarantees or conditions are implied.
***********************************************************/

/*******************************
Modified by Stewart Bracken
********************************/

import java.lang.*;
import java.io.*;
import java.util.*;

public class Fraction
{
   // member variables
   private int numerator, denominator;  // stores the fraction data

   /**********************************************************
    Method:         Default Constructor
    Purpose:        Create a new Fraction object and initialize it 
                    with "invalid" data
    Parameters:     None
    Preconditions:  None
    Postconditions: a new fraction object is created with numerator
                    and denominator set to 0
   ***********************************************************/
   public Fraction()
   {
      numerator = denominator = 0;
   }
   
   public Fraction( int numerator, int denominator ) {
      if (denominator == 0)
         throw new NumberFormatException("denominator is zero");
      this.numerator = numerator;
      this.denominator = denominator;
      this.reduce();
   }

   /********************************************/
   /* public accessor methods for private data */
   /********************************************/

   /**********************************************************
    Method:         getNumerator
    Purpose:        access data stored in numerator member variable
    Parameters:     None
    Preconditions:  None
    Postconditions: None
    Returns:        integer data stored in numerator member variable
   ***********************************************************/
   public int getNumerator()
   {
      return numerator;
   }

   /**********************************************************
    Method:         setNumerator
    Purpose:        provide data to store in numerator member variable
    Parameters:     num: an integer value
    Preconditions:  None
    Postconditions: the value of num will be stored in numerator 
                    member variable
   ***********************************************************/
   public void setNumerator(int num)
   {
      numerator=num;
   }

   /**********************************************************
    Method:         getDenominator
    Purpose:        access data stored in denominator member variable
    Parameters:     None
    Preconditions:  None
    Postconditions: None
    Returns:        integer data stored in denominator member variable
   ***********************************************************/
   public int getDenominator()
   {
      return denominator;
   }
   
   public boolean equals (Fraction fraction){
      if ( this.numerator == fraction.numerator && this.denominator == fraction.denominator)
         return true;
      else return false;
   }
   
   
   /**********************************************************
    Method:          length
    Purpose:         Finds length of specified fraction.
    Created by Stewart Bracken
   **********************************************************/
   public int length(){
      int count = 0; // at least 3 for num, denom, and /
      boolean negative = false;
      //if (this.numerator<0) negative = true;
      String buffer = Integer.toString(this.numerator);
      count +=buffer.length();
      if (this.denominator != 1) {
         buffer = Integer.toString(this.denominator);
         count +=buffer.length();
         count++; //acount for /
      }
      return count;
   }

   /**********************************************************
    Method:         setDenominator
    Purpose:        provide data to store in denominator member variable
    Parameters:     den: an integer value
    Preconditions:  None
    Postconditions: the value of den will be stored in denominator
                    member variable
   ***********************************************************/
   public void setDenominator(int den)
   {
      denominator=den;
   }

   /****************************************************/
   /* public action methods for manipulating fractions */
   /****************************************************/

   /**********************************************************
    Method:         add
    Purpose:        Add two fractions, a and b, where a is the "this"
                    object, and b is passed as the input parameter
    Parameters:     b, the fraction to add to "this"
    Preconditions:  Both fractions a and b must contain valid values
    Postconditions: None
    Returns:        A Fraction representing the sum of two
                    fractions a & b
   ***********************************************************/
   public Fraction add(Fraction b)
   {
      // check preconditions
      if ((denominator == 0) || (b.denominator == 0))
         throw new IllegalArgumentException("invalid denominator");
      // find lowest common denominator
      int common = lcd(denominator, b.denominator);
      // convert fractions to lcd
      Fraction commonA = new Fraction();
      Fraction commonB = new Fraction();
      commonA = convert(common);
      commonB = b.convert(common);
      // create new fraction to return as sum
      Fraction sum = new Fraction();
      // calculate sum
      sum.numerator = commonA.numerator + commonB.numerator;
      sum.denominator = common;
      // reduce the resulting fraction
      sum = sum.reduce();
      return sum;
   }

   /**********************************************************
    Method:         subtract
    Purpose:        Subtract fraction b from a, where a is the "this"
                    object, and b is passed as the input parameter
    Parameters:     b, the fraction to subtract from "this"
    Preconditions:  Both fractions a and b must contain valid values
    Postconditions: None
    Returns:        A Fraction representing the differenct of the
                    two fractions a & b
   ***********************************************************/
   public Fraction subtract(Fraction b)
   {
      // check preconditions
      if ((denominator == 0) || (b.denominator == 0))
         throw new IllegalArgumentException("invalid denominator");
      // find lowest common denominator
      int common = lcd(denominator, b.denominator);
      // convert fractions to lcd
      Fraction commonA = new Fraction();
      Fraction commonB = new Fraction();
      commonA = convert(common);
      commonB = b.convert(common);
      // create new fraction to return as difference
      Fraction diff = new Fraction();
      // calculate difference
      diff.numerator = commonA.numerator - commonB.numerator;
      diff.denominator = common;
      // reduce the resulting fraction
      diff = diff.reduce();
      return diff;
   }

   /**********************************************************
    Method:         multiply
    Purpose:        Multiply fractions a and b, where a is the "this"
                    object, and b is passed as the input parameter
    Parameters:     The fraction b to multiply "this" by
    Preconditions:  Both fractions a and b must contain valid values
    Postconditions: None
    Returns:        A Fraction representing the product of the
                    two fractions a & b
   ***********************************************************/
   public Fraction multiply(Fraction b)
   {
      // check preconditions
      if ((denominator == 0) || (b.denominator == 0))
         throw new IllegalArgumentException("invalid denominator");
      // create new fraction to return as product
      Fraction product = new Fraction();
      // calculate product
      product.numerator = numerator * b.numerator;
      product.denominator = denominator * b.denominator;
      // reduce the resulting fraction
      product = product.reduce();
      return product;
   }

   /**********************************************************
    Method:         divide
    Purpose:        Divide fraction a by b, where a is the "this"
                    object, and b is passed as the input parameter
    Parameters:     The fraction b to divide "this" by
    Preconditions:  Both fractions a and b must contain valid values
    Postconditions: None
    Returns:        A Fraction representing the result of dividing
                    fraction a by b
   ***********************************************************/
   public Fraction divide(Fraction b)
   {
      // check preconditions
      if ((denominator == 0) || (b.numerator == 0))
         throw new IllegalArgumentException("invalid denominator");
      // create new fraction to return as result
      Fraction result = new Fraction();
      // calculate result
      result.numerator = numerator * b.denominator;
      result.denominator = denominator * b.numerator;
      // reduce the resulting fraction
      result = result.reduce();
      return result;
   }
   
   
   public Fraction inverse(Fraction b){
      if ((denominator == 0) || (b.numerator == 0))
         throw new IllegalArgumentException("invalid denominator");
      Fraction result = new Fraction();
      result.numerator = b.denominator;
      result.denominator = b.numerator;
      result = result.reduce();
      return result;
      //if ( result.numerator > && result.numerator < 0 )
   }
   
   
   

   /**********************************************************
    Method:         input
    Purpose:        Retrieve values from the user via keyboard input
                    for numerator and denominator of the "this" object.
                    A valid integer value must be entered for the 
                    numerator, and a non-zero integer value must be
                    entered for denominator.
    Parameters:     None
    Preconditions:  SavitchIn class must be available to read keyboard
                    input.  User needs to see command line window to be
                    prompted for input.
    Postconditions: The "this" object will contain the valid data entered
                    by the user.
   ***********************************************************/
   // public void input()
   //    {
   //       // prompt user to enter numerator
   //       System.out.print("Please enter an integer for numerator: ");
   //       // get user input
   //       numerator = SavitchIn.readLineInt();
   //       // prompt user to enter denominator in a loop to prevent
   //       // an invalid (zero) value for denominator
   //       do {
   //          System.out.print("Please enter a non-zero integer for denominator: ");
   //          denominator = SavitchIn.readLineInt();
   //          // make sure it is non-zero
   //          if (denominator == 0)
   //             System.out.println("Invalid value.  Please try again.");
   //       } while (denominator == 0);
   //    }

   /**********************************************************
    Method:         output
    Purpose:        Print the value of the "this" object to the screen.
                    Makes use of the toString() method.
                    Uses System.out.print, rather than println for flexibility
    Parameters:     None
    Preconditions:  User needs access to command line window to see output
    Postconditions: The value of the "this" object will be printed to
                    the screen
   ***********************************************************/
   public void output()
   {
       //this.reduce();
       System.out.print(this);
   }

   /**********************************************************
    Method:         toString
    Purpose:        Convert the internal representation of a fraction,
                    which is stored in two integers, into a String
                    (which could then be printed to the screen)
    Parameters:     None
    Preconditions:  None
    Postconditions: The value of the "this" object will be converted
                    to a String
    Returns:        A String representation of the "this" fraction
   ***********************************************************/
   public String toString()
   {
      String buffer;
      if (denominator!=1) buffer = numerator + "/" + denominator;
      else buffer = Integer.toString(numerator);
      return buffer;
   }

   /*****************************************************/
   /* private methods used internally by Fraction class */
   /*****************************************************/

   /**********************************************************
    Method:         lcd
    Purpose:        find lowest common denominator, used to add and
                    subtract fractions
    Parameters:     two integers, denom1 and denom2
    Preconditions:  denom1 and denom2 must be non-zero integer values
    Postconditions: None
    Returns:        the lowest common denominator between denom1 and
                    denom2
   ***********************************************************/
   private int lcd(int denom1, int denom2)
   {
      int factor = denom1;
      while ((denom1 % denom2) != 0)
         denom1 += factor;
      return denom1;
   }

   /**********************************************************
    Method:         gcd
    Purpose:        find greatest common denominator, used to reduce
                    fractions 
    Parameters:     two integers, denom1 and denom2
    Preconditions:  denom1 and denom2 must be positive integer values
                    denom1 is assumed to be greater than denom2
                    (denom1 > denom2 > 0)
    Postconditions: None
    Returns:        the greatest common denominator between denom1 and
                    denom2
    Credits:        Thanks to Euclid for inventing the gcd algorithm,
                    and to Prof. Joyce for explaining it to me.
   ***********************************************************/
   private int gcd(int denom1, int denom2)
   {
      int factor = denom2;
      while (denom2 != 0) {
         factor = denom2;
         denom2 = denom1 % denom2;
         denom1 = factor;
      }
      return denom1;
   }

   /**********************************************************
    Method:         convert
    Purpose:        convert a fraction to an equivalent one based on
                    a lowest common denominator
    Parameters:     an integer common, the new denominator
    Preconditions:  the "this" fraction must contain valid data for
                    numerator and denominator
                    the integer value common is assumed to be greater
                    than the "this" fraction's denominator
    Postconditions: None
    Returns:        A new fraction which is equivalent to the "this"
                    fraction, but has been converted to the new
                    denominator called common
   ***********************************************************/
   private Fraction convert(int common)
   {
      Fraction result = new Fraction();
      int factor = common / denominator;
      result.numerator = numerator * factor;
      result.denominator = common;
      return result;
   }

   /**********************************************************
    Method:         reduce
    Purpose:        convert the "this" fraction to an equivalent one
                    based on a greatest common denominator
    Parameters:     None
    Preconditions:  The "this" fraction must contain valid data for
                    numerator and denominator
    Postconditions: None
    Returns:        A new fraction which is equivalent to a, but has
                    been reduced to its lowest numerical form
   ***********************************************************/
   private Fraction reduce()
   {
      Fraction result = new Fraction();
      int common = 0;
      // get absolute values for numerator and denominator
      int num = Math.abs(numerator);
      int den = Math.abs(denominator);
      // figure out which is less, numerator or denominator
      if (num > den)
         common = gcd(num, den);
      else if (num < den)
         common = gcd(den, num);
      else  // if both are the same, don't need to call gcd
         common = num;

      // set result based on common factor derived from gcd
      result.numerator = numerator / common;
      result.denominator = denominator / common;
      if (result.numerator < 0 && result.denominator < 0){
         result.numerator = -result.numerator;
         result.denominator = -result.denominator;
      } else if ( result.numerator >= 0 && result.denominator < 0){
         result.denominator =-result.denominator;
         result.numerator =-result.numerator;
      }
      return result;
   }

   /**********************************************************
    Method:         main
    Purpose:        Show how to create some Fraction objects and then
                    call methods to manipulate them
    Parameters:     an array of Strings, expected to be empty
    Preconditions:  None
    Postconditions: None
   ***********************************************************/
   // public static void main(String args[])
   //    {
   //       Fraction f1 = new Fraction();   // local fraction objects
   //       Fraction f2 = new Fraction();   // used to test methods
   // 
   //       // one way to set up fractions is simply to hard-code some values
   //       f1.setNumerator(1);
   //       f1.setDenominator(3);
   //       f2.setNumerator(1);
   //       f2.setDenominator(6);
   //       
   //       // try some arithmetic on these fractions
   //       Fraction result = new Fraction();
   //       // test addition
   //       result = f1.add(f2);
   //       // one way to output results, using toString method directly
   //       System.out.println(f1 + " + " + f2 + " = " + result);
   //       // test addition going the other way - should be same result
   //       result = f2.add(f1);
   //       // output results
   //       System.out.println(f2 + " + " + f1 + " = " + result);
   //       System.out.println();
   // 
   //       // test subtraction
   //       result = f1.subtract(f2);
   //       // output results
   //       System.out.println(f1 + " - " + f2 + " = " + result);
   //       // test subtraction going the other way - should be different result
   //       result = f2.subtract(f1);
   //       // output results
   //       System.out.println(f2 + " - " + f1 + " = " + result);
   // 
   //       // another way to set up fractions is to get user input
   //       System.out.println();
   //       System.out.println("Fraction 1:");
   //       f1.input();
   //       System.out.println();
   //       System.out.println("Fraction 2:");
   //       f2.input();
   //       System.out.println();
   // 
   //       // test multiplication
   //       result = f1.multiply(f2);
   // 
   //       // another way to output results is to use the output method
   //       // this uses the toString method indirectly
   //       f1.output();
   //       System.out.print(" * ");
   //       f2.output();
   //       System.out.print(" = ");
   //       result.output();
   //       System.out.println();
   // 
   //       // test division
   //       result = f1.divide(f2);
   // 
   //       // output results
   //       f1.output();
   //       System.out.print(" / ");
   //       f2.output();
   //       System.out.print(" = ");
   //       result.output();
   //       System.out.println();
   //    }

}

