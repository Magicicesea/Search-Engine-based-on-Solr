package csci572;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.*;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

public class ExtractLinks{
    public static void main(String[] args) throws Exception, ParseException, IOException{

        BufferedReader br = new BufferedReader(new FileReader("/home/jianfei_william_wang/Reuters/URLtoHTML_reuters_news.csv"));
        String line =  null;

        //traverse documents and change doc with real URL
        //Key: url, Value: docId
        HashMap<String,String> dic = new HashMap<>();
	//Key: docId, Value: url
        HashMap<String,String> dic2 = new HashMap<>();
        File folder = new File("/home/jianfei_william_wang/Reuters/reutersnews");


        while((line=br.readLine())!=null){
            String str[] = line.split(",");
            dic.put(str[1],str[0]);
	    dic2.put(str[0],str[1]);
        }
	
	int numberRows = 0;
        Set<String> edges = new HashSet<>();
        for(File file:folder.listFiles()){
	    if(dic2.get(file.getName())==null){
		continue;		
            }
            Document doc = Jsoup.parse(file, "UTF-8",dic2.get(file.getName()));
            Elements links = doc.select("a[href]");

            for(Element link:links){
                String url = link.attr("abs:href").trim();
                if(dic.containsKey(url)){
                    edges.add(file.getName()+" "+dic.get(url));
		    numberRows++;
                }
            }
        }
	System.out.println("Number of Rows:"+numberRows );

        writeEdgestoFile("/home/jianfei_william_wang/Reuters/indexResults.txt", edges);


        // //dic ready for using
        // ArrayList<String> docList = new ArrayList<>();
        // ArrayList<ArrayList<String>> results = new ArrayList<>();

        // File[] fileNames = folder.listFiles();
        // for(File file: fileNames){
        //     String basic_url = dic.get(file.getName());
        //     if(basic_url != null){
        //         Document doc = Jsoup.parse(file, "UTF-8", basic_url);
        //         results.add(retrieveLinks(doc));
        //         docList.add(basic_url);
        //     }
        // }

        // //ArrayList<String> docIndex = new ArrayList<>();
        // //docIndex has order

        // ArrayList<ArrayList<Integer>> indexResults = new ArrayList<>();
        // for(ArrayList<String> result: results){
        //     indexResults.add(toAdjacentMatrix(docList, result));
        // }

        //appendIndextoFile("/home/jianfei_william_wang/Reuters/indexResults.txt", indexResults,docList);

    }

    public static ArrayList<Integer> toAdjacentMatrix(ArrayList<String> docList, ArrayList<String> result){
        ArrayList<Integer> tmp = new ArrayList<>();
        for(String docId: docList){
            if(result.contains(docId)){
                tmp.add(1);
            }else{
                tmp.add(0);
            }
        }
        return tmp;
    }


    public static void writeEdgestoFile(String filename, Set<String> edges) throws IOException{
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename, false));
        for(String s:edges){
            writer.append(s);
	    writer.newLine();
        }
        writer.close();
    }

    public static void appendIndextoFile(String filename, ArrayList<ArrayList<Integer>> indexResults, ArrayList<String> docList) throws IOException{
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename, false));
        for(int i = 0; i < docList.size()-1;i++){
            writer.append(docList.get(i));
            writer.append(";");
            writer.append(indexResults.get(i).toString());
            writer.newLine();
        }
        writer.close();
    }


    private static ArrayList<String> retrieveLinks(Document doc){
        // File file = new File("/home/jianfei_william_wang/Reuters/reutersnews/ffd80758-1afa-414d-932e-ddfe32438b5f.html");
        // Document doc = Jsoup.parse(file, "UTF-8", "http://www.reuters.com/");

        Elements links = doc.select("a[href]");
        //Elements imports = doc.select("link[href]");
        // Elements media = doc.select("[src]");
        
        // print("\nMedia: (%d)", media.size());
        // for(Element src: media){
        //     if(src.tagName().equals("img")){
        //         print("* %s: <%s> %sx%s (%s)",
        //                 src.tagName(), src.attr("abs:src"), src.attr("width"),src.attr("height"),trim(src.attr("alt"),20));
        //     }
        // }

        // print("\nImports:(%d)", imports.size());
        // for(Element link: imports){
        //     print("* %s <%s> (%s)", link.tagName(),link.attr("abs:href"),link.attr("rel"));
        // }

        //print("\nLinks: (%d)", links.size());

        ArrayList<String> results = new ArrayList<>();
        for(Element link: links){
            //print("* a: <%s> (%s)", link.attr("abs:href"), trim(link.text(),35));
            //Extract document id
            results.add(link.attr("abs:href").trim());
        }
        return results;
    }

    private static void print(String msg, Object... args){
        System.out.println(String.format(msg,args));
    }

    private static String trim(String s, int width){
        if(s.length() > width){
            return s.substring(0,width-1) + ".";
        }else{
            return s;
        }
    }
}
