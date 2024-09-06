package io.minicap.covid19trackingApp.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import org.aspectj.weaver.bcel.ExceptionRange;
import org.json.JSONObject;

// Class used to get information via GET requests from online sources
public class statsService 
{
    public static int getHospitilizations() throws IOException
    {
        URL url = new URL("https://www.donneesquebec.ca/recherche/api/3/action/datastore_search?limit=2&resource_id=2d8bd4f8-4715-4f33-8cb4-eefcec60a4c9");
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

        String data = in.readLine();

        JSONObject obj = new JSONObject(data);

        int hospitilization = obj.getJSONObject("result").getJSONArray("records").getJSONObject(0).getInt("ACT_Total_RSS99");
    
        return hospitilization;
    }

    public static int getChangeinHospitilizations() throws IOException
    {
        URL url = new URL("https://www.donneesquebec.ca/recherche/api/3/action/datastore_search?limit=2&resource_id=2d8bd4f8-4715-4f33-8cb4-eefcec60a4c9");
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

        String data = in.readLine();

        JSONObject obj = new JSONObject(data);

        int hospitilization = obj.getJSONObject("result").getJSONArray("records").getJSONObject(0).getInt("ACT_Total_RSS99");
        int hospitilizationYesterday = obj.getJSONObject("result").getJSONArray("records").getJSONObject(1).getInt("ACT_Total_RSS99");
    
        return  hospitilization - hospitilizationYesterday;
    }

    public static int getCurrentCases() throws IOException
    {
        URL url = new URL("https://api.opencovid.ca/summary?loc=QC");
        
        try
        {
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

            String data = in.readLine();

            JSONObject obj = new JSONObject(data);

            int cases = obj.getJSONArray("summary").getJSONObject(0).getInt("cases");
    
            return cases;

        }
        catch(Exception e)
        {
            return 1000;
        }
        
    }

    public static int getDeaths() throws IOException
    {
        URL url = new URL("https://api.opencovid.ca/summary?loc=QC");
        
        try
        {
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

            String data = in.readLine();

            JSONObject obj = new JSONObject(data);

            int cases = obj.getJSONArray("summary").getJSONObject(0).getInt("deaths");
        
            return cases;
        }
        catch(Exception e)
        {
            return 0;
        }
    }


    
}
