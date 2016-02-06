package com.aftersencha.main;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class HTMLParser {
	
	protected String targetUrl = "http://www.booking.com/hotel/hk/w-hong-kong.en-gb.html";
	protected String userAgent = "Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6";
	protected String referrer = "http://www.google.com";
	protected Document doc = null;
	
	public void setUserAgent( String userAgent ){
		this.userAgent = userAgent;
	}
	
	public String getUserAgent (){
		return this.userAgent;
	}
	
	public void setReferrer( String referrer ){
		this.referrer = referrer;
	}
	
	public String getReferrer(){
		return this.referrer;
	}
	
	public void setDocument() throws IOException{
		this.doc = Jsoup.connect(this.targetUrl).userAgent(this.userAgent).referrer(this.referrer).get();
	}
	
	public Document getDocument(){
		return this.doc;
	}
	
	public void setTargetUrl( String url ){
		this.targetUrl = url;
	}
	
	public String getTargetUrl (){
		return this.targetUrl;
	}
	
	public static void main ( String[] args ) throws IOException, JSONException{
		
		JSONObject json = new JSONObject();
		JSONArray jsonArray1;
		JSONArray jsonArray2;
		String hotelName;
		String hotelAddress;
		String description;
		int hotelStarNumber;
		int numberOfReviews;
		double reviewPoint;	
		
		
		HTMLParser htmlParser = new HTMLParser();
		htmlParser.setDocument();								
		
		
		//Get hotel name
		hotelName = htmlParser.getHotelName( "hp_hotel_name" );
		json.put( "Hotel Name", hotelName );
		
		
		//Get hotel address
		hotelAddress = htmlParser.getHotelAddress( "hp_address_subtitle" );
		json.put( "Hotel Address", hotelAddress );
		
		//Get hotel stars
		hotelStarNumber = htmlParser.getHotelStar( "star_track" );
		json.put( "Stars", hotelStarNumber );
		
		//Get review point
		reviewPoint = htmlParser.getReviewPoint( "js--hp-scorecard-scoreval" );
		json.put( "Review Points", reviewPoint );
		
		//Get number of reviews
		numberOfReviews = htmlParser.getNumberOfReviews( "score_from_number_of_reviews" );
		json.put( "Number of Reviews", numberOfReviews );
		
		//Get description
		description = htmlParser.getDescription( "summary" );
		json.put( "Description", description );
		
		//Get room types
		jsonArray1 = htmlParser.getRoomTypes( "maxotel_rooms" );
		json.put( "Room Types", jsonArray1 );
		
		jsonArray2 = htmlParser.getOtherHotels( "althotelsTable" );
		json.put( "Other Hotels", jsonArray2 );
		
		String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
		PrintWriter pw = new PrintWriter("ParsedHTML_"+timeStamp+".txt");
		pw.write(json.toString(4));
		pw.close();
		
		System.out.println(json.toString(4)); 
	}
	
	public String getHotelName( String selector ){
		/*
		 <span class="fn" id="hp_hotel_name">VP Hotel</span>
		 */
		
		String hotelName = "NULL";
		
		Element tempElement = this.doc.getElementById( selector );		
		if ( tempElement != null ){
			hotelName = tempElement.text();			
		}
		
		return hotelName;
	}
	
	public String getHotelAddress ( String selector ){
		/*
		 <span class="hp_address_subtitle jq_tooltip" rel="14" itemprop="address" data-source="top_link" data-coords="," data-node_tt_id="location_score_tooltip" data-bbox="114.148546189026,22.276984712595,114.179556369781,22.3742122996183" data-width="350" title="">
149 Lai Chi Kok Road, Prince Edward, Tai Kok Tsui, Hong Kong, Hong Kong
</span>
		 */
		
		String hotelAddress = "NULL";
		
		Elements tempElements = this.doc.getElementsByClass( selector );		
		if ( !tempElements.isEmpty() ){
			hotelAddress = tempElements.first().text();			
		}
		
		return hotelAddress;
	}
	
	public int getHotelStar (  String selector ){
		/*
		 <i class="b-sprite stars ratings_stars_2  star_track" title="2-star hotel"
			data-component="track" data-track="view" data-hash="YdVPYKDcdSBGRRaGaAUC" data-stage="1">	
		 	<span class="invisible_spoken">2-star hotel</span>
		 </i>
		*/
		
		String hotelStar = "NULL";
		int starNumber = 0;
		Elements tempElements = this.doc.getElementsByClass( selector );
		if ( !tempElements.isEmpty() ){
			hotelStar = tempElements.first().child(0).text() ;
			hotelStar = hotelStar.replaceAll("\\D+","");
			starNumber = Integer.parseInt( hotelStar );
			
		}
		
		return starNumber;
	
	}
	
	public double getReviewPoint( String selector ){
		/*
		 <span class="average js--hp-scorecard-scoreval">7.3</span>
		 */
		double reviewPoint = 0.0;
		Elements tempElements = this.doc.getElementsByClass( selector );
		if ( !tempElements.isEmpty() ){
			reviewPoint = Double.parseDouble( tempElements.first().text() );
		}
		return reviewPoint;
		
	}
	
	public int getNumberOfReviews( String selector ){
		/*
		  <span class="trackit score_from_number_of_reviews">
				Score from <strong class="count">198</strong> reviews
			</span>
		 */
		int numberOfReviews = 0;
		Elements tempElements = this.doc.getElementsByClass( selector );
		if ( !tempElements.isEmpty() ){
			numberOfReviews = Integer.parseInt( tempElements.first().child(0).text() );
		}
		return numberOfReviews;
	}
	
	public String getDescription( String selector ){
		/*
		  <div class="hp_hotel_description_hightlights_wrapper ">
          <div class="hotel_description_wrapper_exp hp-description">
           <div class="" id="summary">
            <div class="chain-content ">
            </div>
            <p>
             Inspired by nature, the chic W Hong Kong towers over busy West Kowloon with its 76th floor outdoor pool, pampering spa treatments and its 73rd floor 24-hour gym. The hotel showcases exceptional décor and architecture from the world's top designers.
            </p>
            <p>
             Overlooking the city or harbour, the stylish air-conditioned rooms all come with a 42-inch flat-screen TV, an iPod dock and an extensive media library. The feather bed is fitted with 400-thread-count bedsheets. En suite bathrooms have a 15-inch flat-screen TV, a soaking tub and soft bathrobes.
            </p>
            <p>
             W Hong Kong is located just above Kowloon MTR Station and is directly connected to the luxurious Elements Shopping Mall. Riding on the hotel's fleet of Audi Q7 limousines, it is just an 8-minute drive from the trendy bars and restaurants of Soho and Lan Kwai Fong.
            </p>
            <p>
             Bliss® Spa features a relaxing menu of body massage and beauty treatments, as well as healthy snacks and its unique Brownie Buffet. Sauna and steam rooms are also available. The WIRED Business Centre provides computer work stations and secretarial services.
            </p>
            <p>
             Delicious international buffet spreads are offered at Kitchen, while the elegant Sing Yin Cantonese Dining serves seafood delights and Dim Sum buffets. Regular parties with live DJ performances can be enjoyed at WOOBAR.
             <br/>

		 */
		String description = "NULL";
		
		Element tempElement = this.doc.getElementById( selector );		
		if ( tempElement != null ){
			description = tempElement.text() + System.getProperty("line.separator");;			
		}
		
		Elements tempElements = this.doc.getElementsByClass( "geo_information" );
		if ( !tempElements.isEmpty() ){
			description = description + tempElements.first().text() + System.getProperty("line.separator");;
		}
		
		Elements tempElements2 = this.doc.getElementsByClass( "hp-desc-we-speak" );
		if ( !tempElements2.isEmpty() ){
			description = description + tempElements2.first().text() + System.getProperty("line.separator");;
		}
		
		Elements tempElements3 = this.doc.getElementsByClass( "hotel_meta_style" );
		if ( !tempElements3.isEmpty() ){
			description = description + tempElements3.first().text() ;
		}
				
		return description;
	}
	
	public JSONArray getRoomTypes( String selector ) throws JSONException{
		/*
		  <table border="2" cellspacing="0" class="roomstable rt_no_dates __big-buttons rt_lightbox_enabled " id="maxotel_rooms">
            <thead>
             <tr id="maxotel_table_header">
              <th class="figure">
               Max
              </th>
              <th>
               Room type
              </th>
              <th class="figure" style="white-space:normal">
              </th>
             </tr>
            </thead>
            <tbody>
             <tr class="odd first">
              <td class="occ_no_dates" style=" border-left: 0 none; border-right: 1px solid #96b2d9;
">
               <i class="b-sprite occupancy_max2 jq_tooltip" title="Standard occupancy: 2">
               </i>
              </td>
              <td class="ftd">
               Cool Corner King Room
              </td>
              <td class="rt_show_dates">
               <span class="b-button b-button_primary jq_tooltip js_price_button " data-title="To see available rooms and prices please enter your check-in and check-out dates." id="33167009" rel="300">
                <span class="b-button__text">
                 Show prices
                </span>
               </span>
              </td>
             </tr>

		 */
		Element room;		
		String roomName;
		int maxOccupancy;
		
		JSONObject json;
		JSONArray jsonArray = new JSONArray();
		
		
		Element tempElement = this.doc.getElementById( selector );		
		if ( tempElement != null ){
			Elements rooms = tempElement.child(1).children();
			for(Iterator<Element> iterator = rooms.iterator(); iterator.hasNext(); ) {
			    room = iterator.next();
			    roomName = room.getElementsByClass("ftd").text();
			    maxOccupancy = Integer.parseInt( room.getElementsByTag("i").first().attr("title").replaceAll("\\D+","") );
			    json = new JSONObject();
			    json.put("Room Name", roomName);
			    json.put("Max Occupancy", maxOccupancy);
			    jsonArray.put(json);
			}
		
		}
		
		
		return jsonArray;
		
	}
	
	public JSONArray getOtherHotels(  String selector ) throws JSONException{
		/*
		  During development, it was found the webpage return a slight different webpage so that the source were different.
		  
		  =========================================================================================================
		  Case 1 - The name of hotel was the text of <p> tag
		  =========================================================================================================
		  <a class=" althotel_link althotels-name-w-photo " href="
/hotel/hk/renaissance-harbour-view-hong-kong.en-gb.html?aid=356980;label=gog235jc-hotel-en-hk-wNhongNkong-unspec-de-com-L%3Aen-O%3Aunk-B%3Aunk-N%3AXX-S%3Abo-U%3Ac;sid=4379b0e1bdba7d3886fbf77d5048436b;dcid=4;fs=1;shid=331670;
">
            <p class="althotel__title althotel__title-narrow">
             Renaissance Hong Kong Harbour View Hotel
             <br>
          =========================================================================================================
          Case 2 - The name of hotel was self-contained by the <a> tag.
          =========================================================================================================
           <a class="althotel_link" href="
/hotel/hk/renaissance-harbour-view-hong-kong.en-gb.html?aid=356980;label=gog235jc-hotel-en-hk-wNhongNkong-unspec-de-com-L%3Aen-O%3Aunk-B%3Aunk-N%3AXX-S%3Abo-U%3Ac;sid=08a8fd0968bef7528d3ad96d9502974a;dcid=1;fs=1;shid=331670;
">
             Renaissance Hong Kong Harbour View Hotel
            </a>


		  	 
		  	 */
		Elements hotelTds;
		Elements tempElements1;
		Elements tempElements2;		
		Element hotelTd;
		Element tempElement1;		
		String hotelName;
		String description= null;
		int hotelStar = 0;
		double hotelScore = 0.0;
		
		JSONObject json;
		JSONArray jsonArray = new JSONArray();
		
		tempElement1 = this.doc.getElementById( selector );		//Table
		if ( tempElement1 != null ){
			hotelTds = tempElement1.child(0).child(0).children(); //Table > tr > td
			
			for(Iterator<Element> iterator = hotelTds.iterator(); iterator.hasNext(); ) {
			    hotelTd = iterator.next(); //td			    
			    			    			    
			    hotelName = hotelTd.getElementsByClass("althotel_link").first().ownText(); //Get the <a> tag tree			    
			    if ( hotelName.equals("") ){			    	
			    	tempElements1 = hotelTd.getElementsByClass("althotel__title");
			    	if ( !tempElements1.isEmpty() ){ // <p> tag exist
			    		hotelName = tempElements1.first().ownText();
			    	}			    	
			    }
			    
			    hotelStar = Integer.parseInt( hotelTd.getElementsByClass("invisible_spoken").first().text().replaceAll("\\D+","") );
			    
			    tempElements1 = hotelTd.getElementsByClass("js--hp-scorecard-scoreval");
			    if ( !tempElements1.isEmpty() ){
			    	hotelScore = Double.parseDouble( tempElements1.first().text() );
			    	
			    }else{			    
			    	tempElements2 = hotelTd.getElementsByClass("althotel__score");
			    	if ( !tempElements2.isEmpty() ){
			    		hotelScore = Double.parseDouble( tempElements2.first().text() );
			    	}
			    }
			    
			    tempElements1 = hotelTd.getElementsByClass("hp_compset_description");
			    if ( !tempElements1.isEmpty() ){
			    	description = tempElements1.first().text();
			    }else{
			    	tempElements2 = hotelTd.getElementsByClass("althotel__description");
			    	if ( !tempElements2.isEmpty() ){
			    		description = tempElements2.first().text();
			    	}
			    }
			    
			    json = new JSONObject();
			    json.put( "Hotel Name", hotelName );
			    json.put( "Stars", hotelStar );
			    json.put( "Review Score", hotelScore );
			    json.put( "Description", description );
			    jsonArray.put(json);
			    
			}
			
		}
		return jsonArray;
		
	}
}
