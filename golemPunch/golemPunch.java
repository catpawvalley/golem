import java.time.*;
import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;

public class golemPunch{
	private static final String[] NECK = new String[]{
        "<!DOCTYPE html>",
        "<html>",
        "<head>",
        "<meta charset=\"UTF-8\">",
        "<style>",
        ".column {",
        "display: inline-block;",
        "width: 20%;",
        "height: 20%;",
        "object-fit: cover;",
        "margin: 4px;",
        "}",
        "div img {",
        "width: 100%;",
        "height: 100%;",
        "}",
        ".column div {",
        "display: table;",
        "margin: 1 auto;",
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
		//you input the text file as an argument and it should 'process' the entries
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
            	//this while loop is the main loop that makes all this work; put the timer at the end
            	//String tube_url = new String(export_buffer, 0, length, "UTF-8");
            	int start;
            	int end;
            	String vid_code = "";
            	boolean missing_context = false;
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
            	//https://www.youtube.com/watch?v=YxSSd9MSDF4
            	String vid_title = "Could not find title";
            	String channel = "Could not find channel";
            	String pub_date = "Could not find date";
            	System.out.println("Attempting to obtain title, channel name, and publish date of "+vid_code);
            	try{
            		//this is where the main requests happen for url stuff
            		URL vid_page = new URI("https://www.youtube.com/watch?v="+vid_code).toURL();
            		InputStream pageStream = vid_page.openStream();
            		byte[] page_buffer = new byte[1024];
            		int l;
            		StringBuffer page_output = new StringBuffer();
            		while( ( (l=pageStream.read(page_buffer) ) != -1) ){
						String page = new String(page_buffer, 0, l, "UTF-8");
						page_output.append(page);
					}
					//debug channel substring for pOuBCk8XMC8 in line 117
					//https://www.youtube.com/watch?v=pOuBCk8XMC8
					if( page_output.indexOf("<title>") > -1 )
						vid_title = page_output.substring( page_output.indexOf("<title>")+7 , page_output.indexOf(" - YouTube</title>") );
					if( page_output.indexOf("<link itemprop=\"name\" content=\"") > -1 )
						channel = page_output.substring( page_output.indexOf("<link itemprop=\"name\" content=\"")+31 , page_output.indexOf("\"></span>") );
					if( page_output.indexOf("\"dateText\":{\"simpleText\":\"") > -1 )
						pub_date = page_output.substring( page_output.indexOf("\"dateText\":{\"simpleText\":\"")+26,  page_output.indexOf("\"},\"relativeDateText\":") );
						//System.out.println("reading: "+vid_code+"\ntitle_found = "+title_found+"\nsubstring is "+page.indexOf("<title>")+page );
						/*if(page.indexOf("<title>")>-1){
							System.out.println("title was found for "+vid_code);
							System.out.println("value of title_found = "+title_found);
						}
						/*if(channel_found)
							channel = page.substring( page.indexOf("<link itemprop=\"name\" content=\"")+31 , page.indexOf("</span>") );
						else
							channel = "Could not find channel";
						if(pd_found)
							pub_date = page.substring( page.indexOf("\"dateText\":{\"simpleText\":\"")+26,  page.indexOf("\"},\"relativeDateText\":") );
						else
							pub_date = "Could not find publish date";*/
					try{
						if(pageStream != null)pageStream.close();
					}
					catch(IOException e){
						e.printStackTrace();
						return;
					}
            	}
            	catch(MalformedURLException e){
					System.out.println("The link doesn't work!");
					System.out.println(vid_code);
					e.printStackTrace();
					return;
				}
				catch(IOException e){
					e.printStackTrace();
					return;
				}
				catch(URISyntaxException e){
					System.out.println("URI Syntax Exception!");
					System.out.println(vid_code);
					e.printStackTrace();
					return;
				}
				//mite need to add a finally statement later
				//figuring out how to parse bytes from html to scan...

            	System.out.println("Attempting to download thumbnail: "+vid_code);
            	try {
            		URL thumbnail_url = new URI("https://img.youtube.com/vi/"+vid_code+"/hqdefault.jpg").toURL();
            		InputStream inputStream = thumbnail_url.openStream();
            		FileOutputStream thumbnail = new FileOutputStream(export_dir.getPath() + File.separator + vid_code + ".jpg");
            		byte[] buffer = new byte[2048];
            		int length;
            		while ((length = inputStream.read(buffer)) != -1) {
                		thumbnail.write(buffer, 0, length);
            		}
            		try{
						if(inputStream != null)inputStream.close();
						if(thumbnail != null)thumbnail.close();
					}
					catch(IOException e){
						e.printStackTrace();
						return;
					}
        		}catch(MalformedURLException e){
					System.out.println("The link doesn't work!");
					System.out.println(vid_code);
					e.printStackTrace();
					return;
				}catch (IOException e) {
            		e.printStackTrace();
            		System.out.println("Error downloading the image.");
  					return;
        		}
        		catch(URISyntaxException e){
					System.out.println("URI Syntax Exception!");
					System.out.println(vid_code);
					e.printStackTrace();
					return;
				}
        		System.out.println("Got thumbnail: "+vid_code);
        		System.out.println("Writing entry to html...");
        		//need to figure out if I want span text or not here for ID
        		String image_link = "<div class=\"column\"> <a href=\""+readline+"\"><img src=\""+vid_code+".jpg\"></a><div id=\"title\">"+vid_title+"</div><div id=\"channel\">"+channel+"</div><div id=\"date\">"+pub_date+"</div></div>";
        		byte[] entry_bytes = image_link.getBytes("UTF-8");
        		htmlOutput.write(entry_bytes);
        		System.out.println("Entry completed! Moving on...");
        		//timer go here
        		Random random = new Random();
				int randomSeconds = 12 + random.nextInt(9);
        		System.out.println("Waiting for " + randomSeconds + " seconds...");
        		try {
            		Thread.sleep(randomSeconds * 1000); // Convert seconds to milliseconds
        		} catch (InterruptedException e) {
		            System.err.println("Sleep interrupted: " + e.getMessage());
        		}
            }
            System.out.println("Finished reading entries. Finalizing html...");
            for (String entry : FEET) {
            	byte[] towrite = entry.getBytes("UTF-8");
            	htmlOutput.write(towrite);
        	}
        	System.out.println("Golem Punch has finished successfully!");
        	//try-with resources for fileInputStream, htmlOutput, bufferedReader
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
	}
}
