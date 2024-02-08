import org.jsoup.Jsoup;
import org.jsoup.UnsupportedMimeTypeException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.Connection;
import org.jsoup.nodes.Node;
import org.jsoup.HttpStatusException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Installer7{
    public void python_download(String choice, Scanner installer) throws IOException, InterruptedException{
        HashMap <String, String> map = new HashMap<String,String>();
        HashMap <String, String> map32_bitA = new HashMap<String,String>();
        HashMap <String, String> map32_bitB = new HashMap<String,String>();
        HashMap <String, String> map_msi_32 = new HashMap<String,String>();
        HashMap <String, String> map_msi_64 = new HashMap<String,String>();

        Map <String, String> treeMap = new HashMap<>();
        
        int count = 1;
        try{
            Connection connector = Jsoup.connect("https://www.python.org/downloads/");
            Document doc = connector.get();

            Elements downloads = doc.select(".list-row-container>li");

            System.out.print("\nChoose a version (2, or 3): ");
            String choice_version = installer.nextLine();
            //https://www.python.org/ftp/python/2.7.8/python-2.7.8.amd64.msi
            //https://www.python.org/ftp/python/2.7.8/python-2.7.8.msi
            
            String versionNumber = "";
            String download_link_64 = "";
            String download_link_32 = "";
            String download_link_32_cap = "";
            String download_link_msi_32 = "";
            String download_link_msi_64 = "";

            for(Element el : downloads){
                versionNumber = el.select(".release-number").text().trim();
                download_link_64 = el.select("a").attr("href").trim();
                download_link_32 = "";
                download_link_32_cap = "";
                download_link_msi_32 = "";
                download_link_msi_64 = "";
                
                if(versionNumber.length()>0){
                    String [] version_str_split = versionNumber.split(" ");
                    if(download_link_64.contains("downloads")){
                        download_link_64 = "https://www.python.org/ftp/python/"+version_str_split[1]+"/python-"+version_str_split[1]+"-amd64.exe";
                        download_link_32 = "https://www.python.org/ftp/python/"+version_str_split[1]+"/python-"+version_str_split[1]+".exe";
                        download_link_32_cap = "https://www.python.org/ftp/python/"+version_str_split[1]+"/Python-"+version_str_split[1]+".exe";
                        download_link_msi_64 = "https://www.python.org/ftp/python/"+version_str_split[1]+"/python-"+version_str_split[1]+".amd64.msi";
                        download_link_msi_32 = "https://www.python.org/ftp/python/"+version_str_split[1]+"/python-"+version_str_split[1]+".msi";
                    }else{
                        download_link_64 = "";
                        download_link_32 = "";
                        download_link_32_cap = "";
                        download_link_msi_32 = "";
                        download_link_msi_64 = "";
                    }
                    //https://www.python.org/ftp/python/2.0.1/Python-2.0.1.exe
                    //https://www.python.org/ftp/python/2.0.1/python-2.0.1.exe
                    //https://www.python.org/ftp/python/3.10.7/python-3.10.7.exe

                    //https://www.python.org/ftp/python/3.10.7/python-3.10.7-amd64.exe
                    
                    if(choice_version.equals(version_str_split[1].substring(0,1))){
                        map.put(String.valueOf(count), download_link_64);
                        map32_bitA.put(String.valueOf(count), download_link_32);
                        map32_bitB.put(String.valueOf(count), download_link_32_cap);
                        map_msi_32.put(String.valueOf(count), download_link_msi_32);
                        map_msi_64.put(String.valueOf(count), download_link_msi_64);
                        System.out.println(count + ": " + versionNumber);
                        treeMap.put(String.valueOf(count), versionNumber.split(" ")[1]);
                        count+=1;
                    }  
                }
            }

            if(count == 1){
                return;
            }
        }catch(IOException e){
            e.printStackTrace();
        }

        System.out.print("\nChoose number for corresponding version (Ex: 1,2,3,etc): ");
        String version_chosen = installer.nextLine();

        installer.close();
        
        Process process = null;
        Process process2 = null;
        
        if(map.containsKey(version_chosen.toLowerCase())){
            System.out.println("Started Downloading File...");

            String choice_split = treeMap.get(String.valueOf(version_chosen));

            Boolean [] match = {false, false};

            //System.out.println();
            URL url = new URL(map.get(String.valueOf(version_chosen)));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            int code_64 = connection.getResponseCode();
            
            try{
                if(code_64 != 404){
                    process = Runtime.getRuntime().exec("cmd /c cURL -L -o python_"+ choice_split +"_installer_64bit.exe " + map.get(String.valueOf(version_chosen)));
                }else{
                    match[0] = true;
                }

                URL url2 = new URL(map32_bitA.get(String.valueOf(version_chosen)));
                HttpURLConnection connection2 = (HttpURLConnection) url2.openConnection();
                connection2.connect();
                int code_32 = connection2.getResponseCode();

                if(code_32 != 404){
                    process2 = Runtime.getRuntime().exec("cmd /c cURL -L -o python_"+ choice_split +"_installer_32bit.exe " + map32_bitA.get(String.valueOf(version_chosen)));
                }else{
                    match[1] = true;
                }

                if(match[0] == true && match[1] == true){
                    System.out.println(map32_bitB.get(version_chosen));
                    
                    URL url3 = new URL(map32_bitB.get(String.valueOf(version_chosen)));
                    HttpURLConnection connection3 = (HttpURLConnection) url3.openConnection();
                    connection3.connect();
                    int code_32_b = connection3.getResponseCode();
                    
                    if(code_32_b != 404){
                        Runtime.getRuntime().exec("cmd /c cURL -L -o python_"+ choice_split +"_installer_32bit.exe " + map32_bitB.get(String.valueOf(version_chosen)));
                    }else{
                        URL url4 = new URL(map_msi_32.get(String.valueOf(version_chosen)));
                        HttpURLConnection connection4 = (HttpURLConnection) url4.openConnection();
                        connection4.connect();
                        int code_32_msi = connection4.getResponseCode();

                        if(code_32_msi != 404){
                            Runtime.getRuntime().exec("cmd /c cURL -L -o python_"+ choice_split +"_installer_32bit.msi " + map_msi_32.get(String.valueOf(version_chosen)));
                        }else{
                            URL url5 = new URL(map_msi_64.get(String.valueOf(version_chosen)));
                            HttpURLConnection connection5 = (HttpURLConnection) url5.openConnection();
                            connection5.connect();
                            int code_64_msi = connection5.getResponseCode();
                            
                            if(code_64_msi != 404){
                                Runtime.getRuntime().exec("cmd /c cURL -L -o python_"+ choice_split +"_installer_64bit.msi " + map_msi_64.get(String.valueOf(version_chosen)));
                            }else{
                                System.out.println("Nothing matched");
                            }
                        } 
                    }
                    
                    //download_link_64 = "https://www.python.org/ftp/python/"+version_str_split[1]+"/Python-"+version_str_split[1]+"-amd64.exe";
                }
            }catch(IOException e){
                e.printStackTrace();
                return;
            }
            //System.out.println(map.get(version_chosen.toLowerCase()));
            //System.out.println(map.get(version_chosen.split(" ")[0]+ " - 32bit"));
        
        }else{
            System.out.println("No such version");
            return;
        }
        
        Thread.sleep(5000);
        System.out.println("\nDownload Completed Successfully...");
        //inStream.close();
    }

    public void java_download(Scanner installer) throws IOException, InterruptedException{
        //https://www.oracle.com/java/technologies/javase/jdk16-archive-downloads.html
        ArrayList <String> list = new ArrayList<String>();
        
        System.out.print("Choose Java JDK version (17,18,19,etc): ");
        String choose = installer.nextLine();
        System.out.println();

        try{
            int choose_int = Integer.parseInt(choose.trim());
            StringBuilder builder = new StringBuilder();
            if(choose_int > 16){
                String urlString = String.format("https://www.oracle.com/java/technologies/javase/jdk%s-archive-downloads.html", String.valueOf(choose));
                URL url = new URL(urlString);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                //System.out.println(con.getResponseCode());

                try{
                    Connection out = Jsoup.connect(String.format("https://download.oracle.com/java/%d/archive/jdk-%d_windows-x64_bin.exe", choose_int, choose_int));
                    Document outDoc = out.get();
                }catch(UnsupportedMimeTypeException e){
                    
                }catch(HttpStatusException e){
                    System.out.println("The version is unvailable.");
                }
                

                if(con.getResponseCode() < 400){
                    Connection connection = Jsoup.connect(urlString);
                    Document doc = connection.get(); 
                    //Thread.sleep(2000);
                    Elements versions = doc.select(".cb133w1");

                    int count = 1;

                    for(Element el : versions){
                        String version = el.selectFirst("h4").text();
                        String inner_version = version.split(" ")[4];
                        builder.append(String.valueOf(count) + ": " + inner_version+"\n");
                        count+=1;
                        list.add(inner_version);
                    }

                    while(true){
                        System.out.println(builder);
                        System.out.print("Choose a number of the version you want: ");

                        choose = installer.nextLine();

                        try{
                            if(list.get(Integer.valueOf(choose)-1) != null){
                                System.out.println("\nNote: This will install both the msi and exe installers. You will have to go on the oracle site to download any zips or tar files.");
                                
                                Runtime.getRuntime().exec("cmd /c cURL -L -o jdk_"+list.get(Integer.valueOf(choose)-1)+String.format("_installer.exe https://download.oracle.com/java/%d/archive/jdk-%s_windows-x64_bin.exe", choose_int, list.get(Integer.valueOf(choose)-1)));
                                Runtime.getRuntime().exec("cmd /c cURL -L -o jdk_"+list.get(Integer.valueOf(choose)-1)+String.format("_installer.msi https://download.oracle.com/java/%d/archive/jdk-%s_windows-x64_bin.msi", choose_int, list.get(Integer.valueOf(choose)-1)));
                                
                                System.out.println("\n Downloading your files... \nNote: This operation cannot be cancelled");
                                Thread.sleep(30000);

                                /*File file = new File("./"+ "jdk_"+list.get(Integer.valueOf(choose)-1)+"_installer.exe");
                                File file2 = new File("./"+ "jdk_"+list.get(Integer.valueOf(choose)-1)+"_installer.exe");*/

                                System.out.println("\nDownload Completed Successfully...");
                                break;
                            }
                        }catch(IndexOutOfBoundsException e){
                            System.out.println("\nError: Choice is invalid please choose a valid number (Ex: 1,2,3...etc)\n");
                        }
                        
                        //System.out.println(versions);
                        //System.out.println(urlString);
                    }
                }
            }
            else{
                System.out.println("The version you chose is either no longer available, or not released");
            }
        }catch(NumberFormatException e){
            System.out.println("Type Error: Please make sure only numbers are entered");
        }
    }

    public void mysql_download(Scanner installer) throws IOException, InterruptedException{
        Connection connection = Jsoup.connect("https://downloads.mysql.com/archives/installer/");
        Document doc = connection.get();

        Elements elements = doc.select("select[name='version']>option");
        StringBuilder builder = new StringBuilder();

        System.out.print("Choose a version (Ex: 1,5,8,etc): ");
        String num = installer.nextLine();

        System.out.println();

        ArrayList<String> list = new ArrayList<String>();
        int count = 1;
        for(Element el: elements){
            String [] text = el.text().split(" ");
            if(text[0].substring(0,1).equals(num)){
                if(text.length>1){
                    list.add(text[0]+".0-"+text[1]);
                    builder.append(String.valueOf(count) + ": " + text[0] + " " + text[1] + "\n");
                    count+=1;
                }else{
                    list.add(text[0]+".0");
                    builder.append(String.valueOf(count) + ": " + el.text() + "\n");
                    count+=1;
                }
            }
        }
        System.out.println(builder);
        System.out.println("Choose a number of the version you want (1,2,3, etc): ");
        String choice = installer.nextLine();

        try{
            System.out.println("\nDownloading your file...\n");
            //System.out.println(String.format("cmd /c cURL -o mysql_%s_installer.msi https://downloads.mysql.com/archives/get/p/25/file/mysql-installer-web-community-%s.msi", list.get(Integer.parseInt(choice)-1), list.get(Integer.parseInt(choice)-1)));
            
            try{
                Connection con = Jsoup.connect(String.format("https://downloads.mysql.com/archives/get/p/25/file/mysql-installer-web-community-%s.msi", list.get(Integer.parseInt(choice)-1)));
                Document docker = con.get();
            }catch(HttpStatusException e){
                System.out.println("Downloader URL was abnormal ");
            }catch(UnsupportedMimeTypeException e){

            }

            //System.out.println(String.format("cmd /c cURL -L -o mysql_%s_installer.msi https://downloads.mysql.com/archives/get/p/25/file/mysql-installer-web-community-%s.msi", list.get(Integer.parseInt(choice)-1).replace(".","_"), list.get(Integer.parseInt(choice)-1)));
            Runtime.getRuntime().exec(String.format("cmd /c cURL -L -o mysql_%s_installer.msi https://downloads.mysql.com/archives/get/p/25/file/mysql-installer-web-community-%s.msi", list.get(Integer.parseInt(choice)-1).replace(".","_"), list.get(Integer.parseInt(choice)-1)));
            
            Thread.sleep(5000);
            System.out.println("Finished downloading your file");
        }catch(IndexOutOfBoundsException e){
            System.out.println("Error: Choice is invalid please choose a valid number (Ex: 1,2,3...etc)\n");
        }catch(NumberFormatException e){
            System.out.println("Error: Please make sure only numbers are entered\n");
        }
    }

    public void mingw_downloader(Scanner installer) throws IOException, InterruptedException{
        Connection connection = Jsoup.connect("https://sourceforge.net/projects/mingw-w64/files/mingw-w64/mingw-w64-release/");
        Document doc = connection.get();

        Elements elements = doc.select("th");
        StringBuilder builder = new StringBuilder();

        Elements exeEls = doc.getElementsByClass("simple");

        System.out.print("Choose a version (Ex: 1,2,3..11): ");
        String num = installer.nextLine();

        System.out.println();

        ArrayList<String> list = new ArrayList<String>();
        int count = 1;
        Pattern pattern = Pattern.compile("v"+num+".\\d.+$");
        Pattern alt_pattern = Pattern.compile("/.+exe$");
        for(Element el:exeEls){
            try{
                Matcher matcher1 = alt_pattern.matcher(el.select("li>a").attr("href"));
                if(matcher1.find()){
                    list.add(el.select("li>a").attr("href"));
                    builder.append(String.valueOf(count) +": " + el.select("li>a").text() + "\n");
                    count+=1;
                    System.out.println(el.select("li>a").attr("href"));
                    break;
                }
                
            }catch(IndexOutOfBoundsException e){
                continue;
            }
        }

        for(Element el: elements){
            String text = el.text();
            Matcher match = pattern.matcher(el.childNode(0).attr("href"));
            if(match.find()){ 
                list.add(el.childNode(0).attr("href"));   
                builder.append(String.valueOf(count) + ": " + el.text() + "\n");
                count+=1;
            }
        }
        System.out.println(builder);
        //System.out.println(list);
        System.out.println("Choose a number of the version you want (1,2,3, etc): ");
        Integer choice = Integer.parseInt(installer.nextLine().trim());

        try{
            System.out.println("\nDownloading your file...\n");
            //System.out.println(String.format("cmd /c cURL -o mysql_%s_installer.msi https://downloads.mysql.com/archives/get/p/25/file/mysql-installer-web-community-%s.msi", list.get(Integer.parseInt(choice)-1), list.get(Integer.parseInt(choice)-1)));
            
            try{
                Connection con = Jsoup.connect(list.get(choice-1));
                Document docker = con.get();
            }catch(HttpStatusException e){
                System.out.println("Downloader URL was abnormal ");
            }catch(UnsupportedMimeTypeException e){

            }

            //System.out.println(String.format("cmd /c cURL -L %s", list.get(choice-1)));
            Runtime.getRuntime().exec(String.format("cmd /c cURL -O -L %s", list.get(choice-1)));
            
            Thread.sleep(5000);
            System.out.println("Finished downloading your file");
        }catch(IndexOutOfBoundsException e){
            System.out.println("Error: Choice is invalid please choose a valid number next time (Ex: 1,2,3...etc)\n");
        }catch(NumberFormatException e){
            System.out.println("Error: Please make sure only numbers are entered\n");
        }
    }

    public void display_programs(){
        System.out.println("Choose an installer to download (enter 'h' for help): ");
        System.out.println("Python");
        System.out.println("Java");
        System.out.println("MinGW");
        System.out.println("MySQL\n");
    }

    public void show_options(){
        System.out.println("Available options:");;
        System.out.println("'h' = to show help options");
        System.out.println("'si' = show's available installers");
        System.out.println("'exit' = exits out of the program");
        System.out.println("'restart' = restarts the program from the beginning.");
        System.out.println("'nd' = doesn't save file in default directory './Cycle_Installer'\n");
    }
    public static void main(String [] args) throws IOException, InterruptedException{
        System.out.println("Choose an installer to download (enter 'h' for help): ");
        System.out.println("Python");
        System.out.println("Java");
        System.out.println("MinGW");
        System.out.println("MySQL\n");

        Scanner installer = new Scanner(System.in);
        System.out.print("Program: ");
        String choice = installer.nextLine();

        String os = System.getProperty("os.name").split(" ")[0];

        if(os.equals("Windows")){
            if(choice.toLowerCase().equals("python")){
                new Installer7().python_download(choice, installer);
            }

            if(choice.toLowerCase().equals("java")){
                new Installer7().java_download(installer);
            }

            if(choice.toLowerCase().equals("mysql")){
                new Installer7().mysql_download(installer);
            }

            if(choice.toLowerCase().equals("mingw")){
                new Installer7().mingw_downloader(installer);
            }
        }
    }
}