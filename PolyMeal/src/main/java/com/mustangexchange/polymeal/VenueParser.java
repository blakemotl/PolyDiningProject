package com.mustangexchange.polymeal;

import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Scanner;

/**
 * Created by Blake on 8/6/13.
 */
public class  VenueParser
{
    /**
     * Parses the data from the internet and stores it into shared preferences.
     * @param doc The xml document to parse.
     * @param data Reference to the GetData Object that called this method.
     * @param spe Shared preferences editor to store data with.
     * @throws IOException If a connection error occurs.
     */
    public void parseAndStore(Document doc, GetData data, SharedPreferences.Editor spe) throws IOException
    {
        Gson cache = new Gson();
        Elements links = doc.select("link");
        ItemSet subVenue;
        int counter = 0;
        //Gets each venues name and open times.
        for(Element venueItem : doc.select("item"))
        {
            String venueName = venueItem.select("name").get(0).text();
            Elements times = venueItem.getElementsByTag("time");
            String link = links.get(counter).text();
            Venue venue = new Venue(venueName,link,counter);
            handleTimes(times, venue);
            Constants.venues.put(venueName, venue);
            Document venDoc = Jsoup.connect(link).get();
            //Gets each category of each venue which represents an ItemSet.
            for(Element category : venDoc.select("category"))
            {
                String subName = category.getElementsByTag("name").text();
                subName = subName.replace("amp;","");
                subVenue = new ItemSet(subName);
                //Gets each menu item from the category which represents an Item.
                for(Element menuItem : category.select("menu_item"))
                {
                    Item item = new Item();
                    item.setName(menuItem.getElementsByTag("product_name").text());
                    String price = menuItem.getElementsByTag("price").text();
                    if(price.contains("per oz."))
                    {
                        item.setOunces(true);
                    }
                    try
                    {
                        item.setPrice(new BigDecimal(price.replace("$","")));
                    }
                    catch(Exception e)
                    {
                        String tempPrice = handlePriceParseFail(price);
                        if(tempPrice.length() != 0)
                        {
                            item.setPrice(new BigDecimal(tempPrice));
                        }
                        else
                        {
                            item.setPrice(new BigDecimal(Constants.DEFAULT_PRICE));
                        }
                    }
                    item.setDescription(menuItem.getElementsByTag("description").text());
                    subVenue.add(item);
                }
                Constants.venues.get(venueName).venueItems.add(subVenue);
            }
            data.update(venueName);
            counter++;
        }
        //Write venue object to storage.
        spe.putString(Constants.speKey,cache.toJson(Constants.venues));
        spe.commit();
    }

    /**
     * Handles the situation where parsing the price is not as simple as making it a Big Decimal.
     * In that case the price must be extracted from the price string.
     * @param price The string to extract the price from.
     * @return The extracted price.
     */
    private String handlePriceParseFail(String price)
    {
        Scanner priceScanner = new Scanner(price);
        String value;
        //Iterates  over each part of the price String.
        while(priceScanner.hasNext())
        {
            value = priceScanner.next();
            StringBuilder parsedVal = new StringBuilder();
            //Ensures one of the parts of the string has a dollar sign.
            if(value.contains("$"))
            {
                value.replace("$","");
                //Iterates over the part of the price with a dollar sign and gets the value.
                for(int i = 0; i < value.length(); i++)
                {
                    if(Character.isDigit(value.charAt(i)) || value.charAt(i) == '.')
                    {
                        parsedVal.append(value.charAt(i));
                    }
                }
                return parsedVal.toString();
            }
        }
        return "0.00";
    }
    private void handleTimes(Elements times, Venue venue)
    {
        try
        {
            for(Element time : times)
            {
                if(time.text().equals("Closed"))
                {
                    DayTimes daytime = new DayTimes();
                    daytime.setClosedAllDay();
                    venue.addTime(daytime);
                }
                else
                {
                    DayTimes daytime = new DayTimes();
                    String[] wholeTimeString = time.text().split(" # ");
                    for(int i =0; i < wholeTimeString.length; i++)
                    {
                        String[] startAndEndTimes = wholeTimeString[i].split("-");
                        String[] oneTime = startAndEndTimes[0].split(":");
                        daytime.addOpen(new Time(checkInt(oneTime[0]), checkInt(oneTime[1])));
                        oneTime = startAndEndTimes[1].split(":");
                        daytime.addClosed(new Time(checkInt(oneTime[0]), checkInt(oneTime[1])));
                    }
                    venue.addTime(daytime);
                }
            }
        }
        catch(Exception e)
        {
            Log.e("Blake","Error with times!", e);
        }
    }
    private int checkInt(String number)
    {
        if(number.contains("00"))
        {
            return 0;
        }
        if(number.contains(" "))
        {
            return Integer.parseInt(number.replace(" ", ""));
        }
        else
        {
            return Integer.parseInt(number);
        }
    }
}
