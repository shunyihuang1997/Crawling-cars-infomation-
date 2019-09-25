import java.util.*;
import org.w3c.dom.*;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.nodes.DataNode;

public class Spider{
	// Fields
    private static final int MAX_PAGES_TO_SEARCH = 10;
    private static HashSet<String> pagesVisited = new HashSet<String>();
    private static LinkedList<String> pagesToVisit = new LinkedList<String>();
    private static final String USER_AGENT =
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.112 Safari/535.1";
    private static List<String> links = new LinkedList<String>();
    private Document htmlDocument;


    //Searching word to see if they are exist or not
    public static void searchWord(String URL, String wordToSearch){
    	String curr_url = "";
        while(pagesVisited.size() < MAX_PAGES_TO_SEARCH){
    		SpiderLeg leg = new SpiderLeg();

    		if(pagesToVisit.isEmpty()){
    			curr_url = URL;
    			pagesVisited.add(URL);
    		}
    		else{
    			System.out.println("No such word found!!!");
    		}

    		leg.crawl(curr_url);

    		boolean success = leg.searchForWord(wordToSearch);

    		//If we successfully find the word, print out ifo
    		if(success){
    			System.out.println(String.format("**Success** Word %s found at %s", wordToSearch, curr_url));
    			break;
    		}

    		pagesToVisit.addAll(leg.getLinks());
    	}

    	System.out.println(String.format("**Done** visiting %s", curr_url));
    }




    //Making HTTP request and check for response

    public static boolean crawl(String URL){
        try{
            Connection connection = Jsoup.connect(URL).userAgent(USER_AGENT);
            Document htmlDocument = connection.get();
            //this.htmlDocument = htmlDocument;

            //200 is the ok status code for HTTP, which means
            if(connection.response().statusCode() == 200){
                System.out.println("\n**Visiting** Received web page at " + URL);
            }
            if(!connection.response().contentType().contains("text/html")){
                System.out.println("**Failure** Retrieved something other than HTML");
                return false;
            }
            Elements linksOnPage = htmlDocument.select("a[href]");
            System.out.println("Found (" + linksOnPage.size() + ") links");
            for(Element link : linksOnPage)
            {
                links.add(link.absUrl("href"));
            }
            return true;

        }
        
        catch(IOException ioe)
        {
            // We were not successful in our HTTP request
            return false;
        }
    }

    //getting information from each of them
    private static final String website = "https://www.cars.com";
    private static final String broadWebsite = "https://www.cars.com/for-sale/searchresults.action/?dealerType=all&mdId=27681&mkId=20049&page=1&perPage=100&rd=99999&searchSource=PAGINATION&sort=relevance&zc=53713";
    private static Map<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();

    // api.cars.com key = tgIEjKJsl9PodnPW31nzyjGeWLaWZKMI


    // //Adding elements into the map
    // private synchronized void addToList( String mapKey, String element){
    //     List<String> infoList = map.get(mapKey);


    //     //if list dose not exist then create it
    //     if(infoList == null){
    //         infoList = new ArrayList<String>();
    //         infoList.add(element);
    //         map.put(mapKey, element);
    //     }
    //     //If the key matchs the input key, then add the element into it
    //     else if(mapKey = map.getKey()){
    //         infoList.add(element);
    //     }
    // }


    //getting information from the link
    /*TODO:::


    */
    public static void getInfoHtml(String docString){
        Document document;

        try{
            document = Jsoup.connect(website).get();
        }
        catch(IOException e){
            System.out.println("Can't get info!");
            return;
        }
        
        Elements elements = document.getElementsByTag("application/ld+json");

        for(Element e : elements){
            String link = e.attributes().get("href");
            findPrice();
            //findYearAndTrim(link);
            //findTrim(link);
        }
    }

    private static String[] getLink(String url){
        Document document;
        String[] storingLink = new String[100000];
        int counter = 0;
        try{
            document = Jsoup.connect(url).userAgent(USER_AGENT).get();
        }
        catch(IOException e){
            System.out.println("Could not find the website!");
            return storingLink;
        }

        System.out.println(document);
        Elements allHref = document.select("a[href]");
        for(Element e : allHref){
            storingLink[counter] = e.text();
            counter++;
            System.out.println(e);
        }
        //String relHref = link.attr("href"); 

        return storingLink;
    }

    //Looking for the price
    private static void findPrice(){
        Document document;
        String newURL = getLink(broadWebsite)[0];

        try{
            document = Jsoup.connect(website +newURL).userAgent(USER_AGENT).get();
        }
        catch(IOException e){
            return;
        }
        System.out.println("hi");
        //System.out.println(link);
        Elements elements = document.getElementsByClass("vehicle-info__price-display");

        // for(Element e : elements){
        //     System.out.println(e);
        //     for(DataNode node : e.dataNodes()){
        //         //If the key does not exist, then create a new one and add value to it
        //         if(map.get(link) == null){
        //             map.put(link, new ArrayList<String>());
        //             map.get(link).add("Price: "+ node.attr("price")+"\n");
        //         }
        //         //If it is there already, then just add
        //         else{
        //             map.get(link).add("Price: "+ node.attr("price")+"\n");
        //         }
        //     }

        // }

        for(Element e : elements){
            System.out.println(e);
            System.out.println("---");
        }
    }


    //Looking for the year
    private static void findYearAndTrim(String link){
        Document document;                   

        try{
            document = Jsoup.connect(website + link).get();
        }
        catch(IOException e){
            System.out.println("Could not find the year and trim!");
            return;
        }

        Elements elements = document.getElementsByClass("listing-tow__title");

        for(Element e : elements){
            for(DataNode node : e.dataNodes()){
                //If the key does not exist, then create a new one and add value to it
                if(map.get(link) == null){
                    map.put(link, new ArrayList<String>());
                    map.get(link).add("Year and Trim: "+ node.attr("year")+"\n");
                }
                //If it is there already, then just add
                else{
                    map.get(link).add("Year: "+ node.attr("year")+"\n");
                }
            }
        }
    }

    //Looking for the trim level
    private static void findTrim(String link){
        Document document;

        try{
            document = Jsoup.connect(website + link).get();
        }
        catch(IOException e){
            System.out.println("Could not find the trim level!");
            return;
        }

        Elements elements = document.getElementsByTag("script");

        for(Element e : elements){
            for(DataNode node : e.dataNodes()){
                //If the key does not exist, then create a new one and add value to it
                if(map.get(link) == null){
                    map.put(link, new ArrayList<String>());
                    map.get(link).add("Trim: "+ node.attr("trim")+"\n");
                }
                //If it is there already, then just add
                else{
                    map.get(link).add("Trim: "+ node.attr("trim")+"\n");
                }
            }
        }
    }


    private static void showData(){
        for(Map.Entry<String, ArrayList<String>> entry : map.entrySet()){
            String link = entry.getKey();
            String info = entry.getKey();

            System.out.println("Link: " + link);
            System.out.println(info);
        }
    }

    public static void main(String[] args){
        String url = args[0];
        //String word = args[1];
        //searchWord(url, word);
        //xiating(url);
        //crawl(url);
        //findPrice();
        getLink("https://www.cars.com/for-sale/searchresults.action/?dealerType=all&mdId=20807&mkId=20012&page=1&perPage=20&rd=30&searchSource=GN_REFINEMENT&sort=relevance&stkTypId=28881&yrId=51683,56007,58487,30031936,35797618,36362520&zc=53713");
        //findYear(url);
        //findTrim(url);
        //showData();
    }
}