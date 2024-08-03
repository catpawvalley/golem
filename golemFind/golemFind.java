import java.util.*;
import java.io.*;
//golemFind is used to find duplicates in lists. Your main arguments will be the lists you want to check against,
//while the list your provide during runtime is the list you want to check.
public class golemFind
{
    public static void main(String[] args){
        if(args.length < 1){
            System.out.println("no arguments");
            return;
        }
        for(String i: args){
            if(i.indexOf(".txt") == -1){
                System.out.println("One of the inputs is not a plain text file. Exiting...");
                return;
            }
        }
        String scanner_input;
        Scanner keyboard_entry = new Scanner(System.in);
        System.out.println("Input a plain text file(presumbly in the same directory golemFind is running in");
        scanner_input = keyboard_entry.nextLine();
        if(scanner_input.indexOf(".txt") == -1){
            System.out.println("Not a plain text file. Exiting...");
            return;
        }
        int dup_count = 0;
        int entries_checked = 0;
        try(BufferedReader main_input = new BufferedReader( (new InputStreamReader(new FileInputStream(scanner_input)/*file*/ )/*stream*/ )/*buffered*/ )  ){
            String bufferedLine;
            for(String arg: args){
                System.out.println("Scanning "+arg);
                while( (bufferedLine=main_input.readLine() ) != null ){
                    try(BufferedReader arg_stream = new BufferedReader( (new InputStreamReader(new FileInputStream(arg)/*file*/ )/*stream*/ )/*buffered*/ ) ){
                        String r;
                        while( (r=arg_stream.readLine() ) != null){
                            String vid_code = null;//this is where the vid code from top buffered reader line goes in
                            if(bufferedLine.indexOf("https://www.youtube.com/watch?v=") > -1)
                                vid_code = bufferedLine.substring(r.indexOf("?v=")+3 );
                            if(bufferedLine.indexOf("https://youtu.be/") > -1)
                                vid_code = bufferedLine.substring(r.indexOf("e/")+2 );
                            if(vid_code != null && r.indexOf(vid_code) > -1){
                                dup_count++;
                                System.out.println("A duplicate was found: "+bufferedLine);
                            }
                        }
                    }
                    entries_checked++;
                }
                System.out.println("Done with "+arg);
                System.out.println("Entries checked: "+entries_checked);
                System.out.println("Duplicates found: "+dup_count);
            }
        }catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        System.out.println("Total entries checked: "+entries_checked);
        if(dup_count > 0)
            System.out.println(dup_count+" entries that are duplicates were found.");
        else
            System.out.println("No duplicates were found.");
    }
}
