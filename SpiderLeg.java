import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class SpiderLeg{
	private static final String USER_AGENT =
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.112 Safari/535.1";
    private List<String> links = new LinkedList<String>();
    private Document htmlDocument;


    //Making HTTP request and check for response

    public boolean crawl(String URL){
    	try{
    		Connection connection = Jsoup.connect(URL).userAgent(USER_AGENT);
	    	Document htmlDocument = connection.get();
	    	this.htmlDocument = htmlDocument;

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
	        	this.links.add(link.absUrl("href"));
	        }
	        return true;

    	}
    	
        catch(IOException ioe)
        {
            // We were not successful in our HTTP request
            return false;
        }
    }


    //If we have a successful crawl, then search through the body of this HTML 
    public boolean searchForWord(String searchWord){
    	if(this.htmlDocument == null)
        {
            System.out.println("ERROR! Call crawl() before performing analysis on the document");
            return false;
        }

        System.out.println("Searching for the word: " + searchWord );
        String bodyText = this.htmlDocument.body().text();
        return bodyText.toLowerCase().contains(searchWord.toLowerCase());
    }
        
    public List<String> getLinks()
    {
        return this.links;
    }
}