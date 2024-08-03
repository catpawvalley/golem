import java.io.*;
import java.net.*;
import java.time.*;
//golemScope is used to find <a> elements inside a golemSight page in order to make a text file list
//with entries separated by newline
public class golemScope{
	public static void main(String[] args){
		if(args.length < 1)
			return;
		for(String i : args){
			if(i.indexOf(".html") == -1){
				System.out.println("One of the inputs is not an html file. Exiting...");
				return;
			}
		}
		String date = LocalDateTime.now().toString();
		date = date.substring(0, date.lastIndexOf('.') );
		date = date.substring(0, date.lastIndexOf(':') );
		date = date.replaceAll(":", "");
		for(String i : args){
			System.out.println("Doing "+i);
			try(FileOutputStream scopeOutput = new FileOutputStream("scopeOutput"+date+".txt", true);
			InputStreamReader fileInput = new InputStreamReader(new FileInputStream(i) ) ) {
				int r;
				char c;
				byte[] bytes_to_write = null;
				boolean buff_s = false;//this is the switch that starts checking for <a> elemetns
				StringBuffer string_check = new StringBuffer();
				while( (r=fileInput.read()) != -1 ) {
					c = (char)r;
					if(c == '<')
						buff_s = true;
					if(buff_s)
						string_check.append(c);
					if(string_check.length() > 1 && string_check.indexOf("<a") == -1){
						buff_s = false;
						string_check.delete(0, string_check.length() );
					}
					else if(string_check.length() > 1 && c == '>'){
						String towrite = new String("");
						if(string_check.indexOf("href=https") > -1)
							towrite = string_check.substring( string_check.indexOf("href=")+5, string_check.indexOf(">") );
						else if(string_check.indexOf("href=\"https") > -1)
							towrite = string_check.substring( string_check.indexOf("href=\"")+6, string_check.indexOf("\">") );
						towrite = towrite+'\n';
						bytes_to_write = towrite.getBytes("UTF-8");
						if(bytes_to_write != null)
							scopeOutput.write(bytes_to_write);
						string_check.delete(0, string_check.length() );
					}
				}
			}catch (FileNotFoundException e) {
            	e.printStackTrace();
            	return;
        	} catch (IOException e) {
            	e.printStackTrace();
            	return;
        	}
		}
	}
}