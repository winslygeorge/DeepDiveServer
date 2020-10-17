import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * 
 */

/**
 * @author georgos7
 *
 */
public  class URLDef {
	
	public HttpURLConnection PostHeader (URL url2) {
		
				HttpURLConnection http =null;
				try {
					http = (HttpURLConnection) url2.openConnection();
					http.setConnectTimeout(3000);
					http.setDoInput(true);
					http.setDoOutput(true);
					http.addRequestProperty("Content-Type", "application/json");
					http.setRequestMethod("POST");
					http.connect();
			
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return http;
	}
	
	public HttpURLConnection GetHeader (URL url2) {
		
		HttpURLConnection http =null;
		try {
			http = (HttpURLConnection) url2.openConnection();
			http.setConnectTimeout(3000);
			http.setDoInput(true);
			http.setDoOutput(false);
			http.addRequestProperty("Content-Type", "application/json");
			http.setRequestMethod("GET");
			http.connect();
	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return http;
}

	
	public boolean sendOnlinePresenceToken(String user,boolean is) {
		
		boolean n = false;
		
		URL url = null;
		
		try {
			
			url = new URL("http://localhost:6005/DeepDive/onlinePresenceToken");
			HttpURLConnection ht = this.PostHeader(url);
			
			ht.connect();
			
			PrintWriter out = new PrintWriter(new OutputStreamWriter(ht.getOutputStream()), true);
			
			
			ArrayList<String> f = new ArrayList<String>();
			
			f.add("username");
			f.add("presence");
					
			ArrayList<Object> v = new ArrayList<Object>();
			
			v.add(user);
			v.add(is);
				
			if(this.handleRequest(out, this.jOb(f, v))) {
				
				if((long)this.handleResponse(ht).get("responseCode") == 200) {
					
					n = true;
				}
			}
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return n;
	}
	
	
	@SuppressWarnings("unchecked")
	public String jOb(ArrayList<String> field, ArrayList<Object> v) {
		
		JSONObject obj = new JSONObject();
		
		int x= 0;
		
		while(x < field.size()) {
			
			obj.put(field.get(x), v.get(x));
			
			x++;
		}
		
		return obj.toJSONString();
	}
	
	public boolean handleRequest(PrintWriter o, String s) {
		
		boolean is = false;
		
		try {
		o.println(s);
		is = true;
		}catch(Exception e) {
			
			System.out.print(e);
		}
		return is;
	}
	
	public JSONObject  handleResponse(HttpURLConnection th) throws IOException {
		
		JSONObject  ob = null;
		
		if(th.getResponseCode()== 200) {
			
			
			BufferedReader in = null;
			try {
				in = new BufferedReader(new InputStreamReader(th.getInputStream()));

				String res = in.readLine();
				
				JSONParser parse = new JSONParser();
				
				ob = (JSONObject) parse.parse(res);
				
				in.close();
				
			} catch (IOException | ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		return ob;
	}

	public JSONObject getGroupRoomMembers(String st, String gt){
		
		JSONObject clients = null;
		
		try {
			URL url = new URL("http://localhost:6005/DeepDiveget/groupmemberslist?username="+st+"&groupname="+ gt);
			
			HttpURLConnection ht = this.GetHeader(url);
					
			clients = this.handleResponse(ht);
			
			
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return clients;
				
	}
}
