package easyIn;

import java.io.*;

public abstract class EasyIn
{
    static BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));

  public static String getString()
  {
     boolean ok = false;
     String s = null;
     while(!ok)
     {
        byte[] b = new byte[512];
        try
        {
           //stdin.read(b);
           s = stdin.readLine();
           s = s.trim();
           ok = true;
        }
        catch(IOException e)
        {
            System.out.println(e.getMessage());
        }
     }
	   return s;
   }

   public static int getInt()
   {
      int i = 0;
      boolean ok = false;
      String s ;
      while(!ok)
      {
         byte[] b = new byte[512];
         try
         {
            //stdin.read(b);
            s = stdin.readLine();
            i = Integer.parseInt(s.trim());
            ok = true;
         }
         catch(NumberFormatException e)
         {
            System.out.println("Make sure you enter an integer");
         }
         catch(IOException e)
         {
             System.out.println(e.getMessage());
         }
     }
     return i;
 }

 public static byte getByte()
 {
     byte i = 0;
     boolean ok = false;
     String s ;
     while(!ok)
     {
        byte[] b = new byte[512];
        try
        {
            //stdin.read(b);
            s = stdin.readLine();
            i = Byte.parseByte(s.trim());
            ok = true;
        }
        catch(NumberFormatException e)
        {
            System.out.println("Make sure you enter a byte");
        }
        catch(IOException e)
        {
             System.out.println(e.getMessage());
        }
     }
     return i;
 }

 public static short getShort()
 {
     short i = 0;
     boolean ok = false;
     String s ;
     while(!ok)
     {
        byte[] b = new byte[512];
        try
        {
            //stdin.read(b);
            s = stdin.readLine();
            i = Short.parseShort(s.trim());
            ok = true;
        }
        catch(NumberFormatException e)
        {
            System.out.println("Make sure you enter a short integer");
        }
        catch(IOException e)
        {
             System.out.println(e.getMessage());
        }
     }
     return i;
 }


 public static long getLong()
 {
    long l = 0;
    boolean ok = false;
    String s ;
    while(!ok)
    {
       byte[] b = new byte[512];
       try
       {
           //stdin.read(b);
           s = stdin.readLine();
           l = Long.parseLong(s.trim());
           ok = true;
       }
       catch(NumberFormatException e)
       {
           System.out.println("Make sure you enter a long integer");
       }

       catch(IOException e)
       {
            System.out.println(e.getMessage());
       }
    }
    return l;
 }


 public static double getDouble()
 {
    double d = 0;
    boolean ok = false;
    String s ;
     while(!ok)
    {
        byte[] b = new byte[512];
        try
        {
             //stdin.read(b);
             s = stdin.readLine();
             d = Double.parseDouble(s.trim());
             ok = true;
        }
        catch(NumberFormatException e)
        {
             System.out.println("Make sure you enter a decimal number");
        }
        catch(IOException e)
        {
           System.out.println(e.getMessage());
       }
    }
    return d;
 }

 public static float getFloat()
 {
     float f = 0;
     boolean ok = false;
     String s;
     while(!ok)
     {
        byte[] b = new byte[512];
        try
        {
            //stdin.read(b);
            s = stdin.readLine();
            f = Float.parseFloat(s.trim());
            ok = true;
        }
        catch(NumberFormatException e)
        {
            System.out.println("Make sure you enter a decimal number");
        }
        catch(IOException e)
        {
            System.out.println(e.getMessage());
        }
     }
	return f;
  }

  public static char getChar()
  {
     char c = ' ';
     boolean ok = false;
     String s;
     while(!ok)
     {
        byte[] b = new byte[512];
        try
        {
          //stdin.read(b);
          s = stdin.readLine();
           if(s.trim().length()!=1)
           {
             System.out.println("Make sure you enter a single character");
           }
           else
           {
                c = s.trim().charAt(0);
                ok = true;
           }
        }
        catch(IOException e)
        {
            System.out.println(e.getMessage());
        }
     }
     return c;
  }


  public static void pause()
  {
     boolean ok = false;
     while(!ok)
     {
         byte[] b = new byte[512];
         try
         {
             stdin.readLine();
             ok = true;
         }
         catch(IOException e)
         {
              System.out.println(e.getMessage());
         }
     }
  }

  public static void pause(String messageIn)
  {
     boolean ok = false;
     while(!ok)
     {
         byte[] b = new byte[512];
         try
         {
              System.out.print(messageIn);
              stdin.readLine();
              ok = true;
         }
         catch(IOException e)
        {
              System.out.println(e.getMessage());
        }
    }
}
}
