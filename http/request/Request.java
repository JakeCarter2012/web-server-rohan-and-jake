package http.request;

import java.util.HashMap; 
import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class Request {
    private String Uri;
    private String Verb;
    private String Query;
    private byte[] Body;
    private String httpVersion;
    private HashMap<String, ArrayList<String>> Headers;
    private Socket Client; 
    private boolean hasQuery;
    
    public Request(Socket client)
    {
        this.Client = client;
        this.Headers = new HashMap<>();
        this.parse();
    }
    
    private void parse()
    {
        String line;
        StringTokenizer tokenizer;
        ArrayList<String> tokens;
        boolean requestLine = true;
        
        try
        {
            BufferedReader requestReader = new BufferedReader(new InputStreamReader(Client.getInputStream()));
           
            while(!(line = requestReader.readLine()).equals(""))
            {
                tokenizer = new StringTokenizer(line);
                tokens = new ArrayList();

                while(tokenizer.hasMoreTokens())
                {
                    tokens.add(tokenizer.nextToken());
                }
                if(requestLine)
                {
                    this.Verb = tokens.get(0).toUpperCase();
                    if(tokens.size() < 3)
                    {
                        this.Uri = null;
                        this.httpVersion = tokens.get(1);
                    }
                    else
                    {
                        String query = "?";
                        if(!tokens.get(1).contains(query))
                        {
                            this.Uri = tokens.get(1);
                            this.hasQuery = false;
                        }
                        else
                        {
                            int index = tokens.get(1).indexOf(query);
                            this.Uri = tokens.get(1).substring(0, index);
                            this.Query = tokens.get(1).substring(index + query.length());
                            this.hasQuery = true;
                        }
                        this.httpVersion = tokens.get(2);
                    }
                }
                else
                {
                    ArrayList<String> headerValues = new ArrayList<>();
                    for(int i = 1; i < tokens.size(); i++)
                    {
                        headerValues.add(tokens.get(i));
                    }

                    this.Headers.put(tokens.get(0).toLowerCase(), headerValues);
                }
                requestLine = false;
            }
            if(this.Headers.containsKey("content-length:"))
            {
                int[] bodyArr = new int[Integer.parseInt(Headers.get("content-length:").get(0))];
                line = "";
                for(int i = 0; i < Integer.parseInt(Headers.get("content-length:").get(0)); i++)
                {
                    bodyArr[i] = requestReader.read();
                    line += Character.toString((char)bodyArr[i]);
                }
                Body = line.getBytes();
            }
        }
        catch(IOException e)
        {
            this.Verb = "Error 400";
        } 
    }
    
    public String getUri()
    {
        return this.Uri;
    }
    
    public String getVerb()
    {
        return this.Verb;
    }
    
    public byte[] getBody()
    {
        return this.Body;
    }
    
    public String getHttpVersion()
    {
        return this.httpVersion;
    }
    
    public String getQuery()
    {
        return this.Query;
    }
    
    public HashMap<String, ArrayList<String>> getHeaders()
    {
        return this.Headers;
    }
    
    public boolean hasQuery()
    {
        return this.hasQuery;
    }
}
