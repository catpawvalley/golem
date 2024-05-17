import java.time.*;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

public class golemPunch{
	private static final String[] NECK = new String[]{
		"<!doctype html>",
		"<html>",
		"<head>",
		"<meta charset=\"UTF-8\">",
		"<style>",
		"img {",
		"display: inline-block;",
		"width: 20%;",
		"height: 20%;",
		"object-fit: cover;",
		"margin: 10px;",
		"}",
		"</style>",
		"</head>",
		"<body>"
	};
	private static final String[] FEET = new String[]{
		"</body>",
		"</html>"
	};

	public static void main(String [] args){
		if(args.length == 0){
			System.out.println("Needs a text file as input...");
			return;
		}
		else if(!args[0].contains(".txt") ){
			System.out.println("Input is not a text file!");
			return;
		}

		String date = LocalDateTime.now().toString();
		date = date.substring(0, date.lastIndexOf('.') );
		date = date.substring(0, date.lastIndexOf(':') );
		date = date.replaceAll(":", "");
		String export_name = "golemExport-"+date;
		//System.out.println(date);
		File export_dir = new File(System.getProperty("user.dir"), export_name);
		if(!export_dir.mkdirs() ){
			System.out.println("Java was not able to make a directory for the export.");
			return;
		}

		try(FileInputStream fileInputStream = new FileInputStream(args[0]);
			FileOutputStream htmlOutput = new FileOutputStream(export_dir.getPath() + File.separator + export_name + ".html");
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream) ) ) {
			//
			//byte[] export_buffer = new byte[1024];
            //int length;
            System.out.println("Writing head of html...");
            for (String entry : NECK) {
            	//System.out.println(entry);
            	byte[] towrite = entry.getBytes("UTF-8");
            	htmlOutput.write(towrite);
        	}
        	System.out.println("Beginning main writes and downloading thumbnails...");
            String readline;
            while( (readline = bufferedReader.readLine() ) != null ){
            	//String tube_url = new String(export_buffer, 0, length, "UTF-8");
            	int start;
            	int end;
            	String vid_code = "";
            	if(readline.contains("https://www.youtube.com/") | readline.contains("https://youtu.be/") ){
            		if (readline.indexOf("youtu.be") != -1) {
            			start = readline.indexOf(".be/") + 4;
            			end = readline.indexOf("?");
        			} else {
            			start = readline.indexOf("watch?v=") + 8;
            			end = -1;
        			}

        			if (end != -1)
            			vid_code = readline.substring(start, end);
        			else
            			vid_code = readline.substring(start);
            	}
            	//System.out.println(tube_url);
            	//System.out.println(vid_code);
            	//byte[] bytes = vid_code.getBytes("UTF-8");
            	//htmlOutput.write(bytes);
            	System.out.println("Attempting to download thumbnail: "+vid_code);
            	try {
            		URL thumbnail_url = new URL("https://img.youtube.com/vi/"+vid_code+"/hqdefault.jpg");
            		InputStream inputStream = thumbnail_url.openStream();
            		FileOutputStream thumbnail = new FileOutputStream(export_dir.getPath() + File.separator + vid_code + ".jpg");
            		byte[] buffer = new byte[2048];
            		int length;
            		while ((length = inputStream.read(buffer)) != -1) {
                		thumbnail.write(buffer, 0, length);
            		}
            		inputStream.close();
            		thumbnail.close();
        		} catch (IOException e) {
            		e.printStackTrace();
            		System.out.println("Error downloading the image.");
  					System.exit(0);
        		}
        		System.out.println("Got thumbnail: "+vid_code);
        		System.out.println("Writing entry to html...");
        		String image_link = "<a href="+readline+">"+"<img src=\""+vid_code+".jpg\" title=\""+readline+"\">";
        		byte[] entry_bytes = image_link.getBytes("UTF-8");
        		htmlOutput.write(entry_bytes);
        		System.out.println("Entry completed! Moving on...");
            }
            System.out.println("Finished reading entries. Finalizing html...");
            for (String entry : FEET) {
            	byte[] towrite = entry.getBytes("UTF-8");
            	htmlOutput.write(towrite);
        	}
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Golem Punch has finished successfully!");
	}
}
