package corejava.ch3.v2ch01.HrefMatch;

import java.io.*;
import java.net.*;
import java.util.regex.*;

/**
 * This program displays all URLs in a web page by matching a regular expression that describes the
 * <a href=...> HTML tag. Start the program as <br>
 * java HrefMatch URL
 * @version 1.01 2004-06-04
 * @author Cay Horstmann
 */
public class HrefMatch
{
   public static void main(String[] args)
   {
      try
      {
         // get URL string from command line or use default
    	 String host = "168.1.6.110";
         String port = "9999";
         System.setProperty("proxySet", "true");
         System.setProperty("proxyHost", host);
         System.setProperty("proxyPort", port);
         
         String urlString;
         if (args.length > 0) urlString = args[0];
         else urlString = "http://www.sina.com.cn/";

         // open reader for URL
         InputStreamReader in = new InputStreamReader(new URL(urlString).openStream());

         // read contents into string builder
         BufferedReader reader = new BufferedReader(in);
         
         StringBuilder input = new StringBuilder();
         String ch;
         while ((ch = reader.readLine()) != null)
            input.append(ch+"\n");

         // search for all occurrences of pattern
         System.out.println("input::"+input.toString());
         System.out.println("----------------------");
         String patternString = "<a\\s+href\\s*=\\s*(\"[^\"]*\"|[^\\s>]*)\\s*>";
         Pattern pattern = Pattern.compile(patternString, Pattern.CASE_INSENSITIVE);
         Matcher matcher = pattern.matcher(input);

         while (matcher.find())
         {
            int start = matcher.start();
            int end = matcher.end();
            String match = input.substring(start, end);
            System.out.println(match);
         }
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
      catch (PatternSyntaxException e)
      {
         e.printStackTrace();
      }
   }
}
