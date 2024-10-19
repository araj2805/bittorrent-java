import com.dampcake.bencode.Type;
import com.google.gson.Gson;

import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;
import com.dampcake.bencode.Bencode;
 //- available if you need it!

public class Main {
  private static final Gson gson = new Gson();

  public static void main(String[] args) throws Exception {
    // You can use print statements as follows for debugging, they'll be visible when running tests.
    //System.out.println("Logs from your program will appear here!");
    String command = args[0];
    //System.out.println("Command line args : "+gson.toJson(args));
    if("decode".equals(command)) {
      //  Uncomment this block to pass the first stage
        String bencodedValue = args[1];
        Object decoded;
        try {
          decoded = decodeBencode(bencodedValue);
        } catch(RuntimeException e) {
          System.out.println(e.getMessage());
          return;
        }
        System.out.println(gson.toJson(decoded));

    } else {
      System.out.println("Unknown command: " + command);
    }

  }

  static Object decodeBencode(String bencodedString) {
    Bencode bencode = new Bencode();
    char firstChar = bencodedString.charAt(0);
    Object decoded;
    if (Character.isDigit(firstChar)) {
      int firstColonIndex = 0;
      for(int i = 0; i < bencodedString.length(); i++) { 
        if(bencodedString.charAt(i) == ':') {
          firstColonIndex = i;
          break;
        }
      }
      int length = Integer.parseInt(bencodedString.substring(0, firstColonIndex));
      decoded = bencodedString.substring(firstColonIndex + 1, firstColonIndex + 1 + length);
    } else if (firstChar == 'i') {
      // bencoded number
      decoded = bencode.decode(bencodedString.getBytes(StandardCharsets.UTF_8), Type.NUMBER);
    } else if (firstChar == 'l') {
      // bencoded list
      decoded = bencode.decode(bencodedString.getBytes(StandardCharsets.UTF_8), Type.LIST);
    }
    else {
      throw new RuntimeException("Only strings are supported at the moment");
    }
    return gson.toJson(decoded);
  }

  Object decodeBencoded(String bencodedString) {
    if (Character.isDigit(bencodedString.charAt(0))) {
      int firstColonIndex = 0;
      for(int i = 0; i < bencodedString.length(); i++) {
        if(bencodedString.charAt(i) == ':') {
          firstColonIndex = i;
          break;
        }
      }
      int length = Integer.parseInt(bencodedString.substring(0, firstColonIndex));
      return bencodedString.substring(firstColonIndex+1, firstColonIndex+1+length);
    } else if (bencodedString.charAt(0) == 'i' && bencodedString.charAt(bencodedString.length() - 1) == 'e') {
      return (Object) Long.parseLong(bencodedString.substring(1,bencodedString.length() - 1));
    } else if (bencodedString.charAt(0) == 'l' && bencodedString.charAt(bencodedString.length() - 1) == 'e') {

      /*int length = 0;
      if (Character.isDigit(bencodedString.charAt(1)))
        length = bencodedString.charAt(1);*/

      String[] list = bencodedString.substring(3).split("i");

      StringBuilder lastString = new StringBuilder(String.valueOf(list[list.length - 1]));
      for (int i = lastString.length() - 1; i >= 0; i--) {
        if (lastString.charAt(i) == 'e')
          lastString = lastString.deleteCharAt(i);
      }
      list[list.length - 1] = String.valueOf(lastString);

      Pattern numberPattern = Pattern.compile("\\d+");
      Pattern decimalPattern = Pattern.compile("(\\.\\d+)?");
      Pattern negativeNumberPattern = Pattern.compile("-?\\d+");
      Pattern negativeDecimalPattern = Pattern.compile("-?(\\.\\d+)?");
      Pattern stringPatter = Pattern.compile("[A-Za-z]]");

      for (int i = 0; i < list.length; i++) {
        String str = (String)list[i];
        if (numberPattern.matcher(str).matches() || negativeNumberPattern.matcher(str).matches()) {
          list[i] = String.valueOf(Integer.parseInt(str));
        } else if (decimalPattern.matcher(str).matches() || negativeDecimalPattern.matcher(str).matches()) {
          list[i] = String.valueOf(Double.parseDouble(str));
        } else if (stringPatter.matcher(str).matches()) {
          list[i] = String.valueOf(str);
        }
      }

      return list;
    }
    else {
      throw new RuntimeException("Only strings are supported at the moment");
    }
  }
  
}
