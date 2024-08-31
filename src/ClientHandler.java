import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Queue;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.gson.Gson;

/**
 * 
 */

/**
 * @author georgos7
 *
 */
public class ClientHandler implements Runnable {
	
	protected ObjectInputStream in;
	
	private ObjectOutputStream out;
	private LinkedHashMap<String, ObjectOutputStream> client;
	
	private ObjectOutputStream soc;
	public 	Message mess;
	protected Socket usc;
	protected String username;
	
	protected Queue<Message> msgq = new LinkedList<Message>();
	
	protected LinkedHashMap<String , Socket> cs;
	
	protected boolean end = true;
	public ClientHandler(String user ,Socket u, LinkedHashMap<String , Socket>clientsockets,LinkedHashMap<String, ObjectOutputStream> clients, ObjectInputStream inob, ObjectOutputStream outob) {
		
		this.in = inob;
		this.setClient(clients);
		this.out = outob;
		this.usc = u;
		this.username = user;
		this.cs = clientsockets;
	}

	@Override 
	public void run() {
		// TODO Auto-generated method 
		
		String sp = this.readOfflineMessages(this.username);
		
		if(sp == null|| sp == "") {
			
			System.out.println("There is no offline messages");
		}else {
			
			JSONParser parse = new JSONParser();
			
			try {
				JSONArray arr = (JSONArray) parse.parse(sp);
				int x = 0;
				for(x = 0; x< arr.size(); x++) {
					
					Gson gs = new Gson();
					JSONObject ob = (JSONObject) arr.get(x);
					Message omsg = gs.fromJson(ob.toJSONString(), Message.class);
					
					Socket scc  = this.cs.get(omsg.getTo());
					
					if(scc.isClosed()) {
						
						File f = new File("confid/offline/"+mess.getTo()+".json");
						System.out.println(this.writeJson(f, mess));

					}else {
						
						
						client.get(omsg.getTo()).writeObject(omsg);
						client.get(omsg.getTo()).flush();
						
						System.out.println("sending offline msg...%%");
					}
					
					
				}
			} catch (ParseException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	

		do {		try {
			
				try {
					
				
					msgq.add((Message)in.readObject());
					
					System.out.println(msgq.size());
					
				} catch (ClassNotFoundException | IOException e1) {
					// TODO Auto-generated catch block
					
					URLDef u = new URLDef();
					
					if(u.sendOnlinePresenceToken(this.username, false)){
						
						System.out.println(this.username + "  :  is offline");
					}else {
						
						System.err.println("error sending online presence");
					}
					
					System.out.println("...user  "+ username + " disconnected...");
										
					this.end = false;
				break;
				}
			
				 			while(!msgq.isEmpty()) {
				 				
				 				 this.mess = msgq.remove();
				 				 
				 				
								 if(mess == null) {
									 
									 System.out.println("mess is null");
									continue;
								 }

								 if(mess.getGroupClient() != null && mess.getGroupClient().equalsIgnoreCase("<*>close")){

									 try{

										 this.client.get(mess.getFrom()).close();

									 }catch (Exception e){

										 System.out.println(e.getMessage());
									 }


									 continue;
								 }
							 
								
				 			
				 				System.out.println(mess.getPrivateCode()+" : "+ mess.getCertificate()+ " : "+mess.getType()+ " : "+mess.getFrom()+" : "+ mess.getTo() + " : groupClient : " + mess.getGroupClient());
				
								System.out.println(mess.getisPrivate()+ "dd");
								
								if(mess.getisPrivate() || mess.getGroupClient() == null) {
									

									if(client.containsKey(mess.getFrom())&& client.containsKey(mess.getTo())) {
		
										Socket sc = cs.get(mess.getTo());
										
										if(sc.isClosed()) {
											
//											client.get(mess.getFrom()).writeObject(new Message("privatechat", "text", "DeepDiver", mess.getFrom(), "user : "+mess.getTo()+" -is not online###", false));
//											client.get(mess.getFrom()).flush();
											System.out.println(mess.getTo()+ " : sent offline msg..%%");
										}else {
				
											client.get(mess.getTo()).writeObject((Message)mess);
											client.get(mess.getTo()).flush();
												
										}	
					
									}else {
					
										System.out.println("Could not get the sender OR receiver...");
					
										return;
									}
					

								}else
													
									if(mess.getGroupClient().equalsIgnoreCase("groupchat")) {


										if(client.containsKey(mess.getFrom())) {
			
											try {
							
												URLDef groupdel = new URLDef();
							
												JSONObject obgroup = (JSONObject)groupdel.getGroupRoomMembers(mess.getFrom(), mess.getTo());
							
														
												if((long)obgroup.get("responseCode") == 200) {
							
							
													System.out.println(obgroup);
							
													JSONArray arr = (JSONArray)obgroup.get("responseMessage");
													int x = 0;
							
													JSONObject cob = null;
													while(x< arr.size()) {
								
														cob = (JSONObject) arr.get(x);
								
														System.out.println(cob.get("username"));
								
														if(cs.containsKey(cob.get("username"))) {
								
															Socket sc = cs.get(cob.get("username"));
								
															if(sc.isClosed()) {
									
									
																System.out.println(cob.get("username") + " : is offline storing offline messages");
									
																System.out.println(mess.getTo()+ " : is offline storing offline messages");
																
																File fl = new File("confid/offline");
																
																if(!fl.exists()){
																	
																	fl.mkdirs();
																}
																
																File f = new File("confid/offline/"+mess.getTo()+".json");
																
																if(f.createNewFile()) {
																	
																	System.out.println("created offline file for "+ mess.getTo());
																}
																
																
																
																System.out.println(this.writeJson(f, mess));
									
									
															}else {
									
																client.get(cob.get("username")).writeObject((Message)mess);
																client.get(cob.get("username")).flush();
																	
															}								
								
														}else {
								
								
															System.out.println("user does not exist in server system");
														}
								
														x++;
							
													}
												}else {
							
													System.out.println(obgroup);
												}
							
											}catch(Exception e) {
							
												e.printStackTrace();
											}
										}else {
							
							
											System.out.println("..sender does not exit");
							
											return;
										}
						
						
									}else {
										
										

										if(client.containsKey(mess.getFrom())&& client.containsKey(mess.getTo())) {
											Socket sc = cs.get(mess.getTo());
											
											if(sc.isClosed()) {
					
					
												System.out.println(mess.getTo()+ " : is offline storing offline messages");
												

												File fl = new File("confid/offline");
												
												if(!fl.exists()){
													
													fl.mkdirs();
												}
												
												File f = new File("confid/offline/"+mess.getTo()+".json");
												
												if(f.createNewFile()) {
													
													System.out.println("created offline file for "+ mess.getTo());
												}
												
												
												
												System.out.println(this.writeJson(f, mess));
					
												
											}else {
					
												client.get(mess.getTo()).writeObject((Message)mess);
												client.get(mess.getTo()).flush();
													
											}	
						
										}else {
						
											System.out.println("Could not get the sender OR receiver...");
						
											return;
										}
						

						
									}
								

								System.out.println("sent...");
				
				
				
								if(mess.getFrom() == null) {
									System.out.println("mess from is null");
									break;}
			
		}	} catch (IOException e) {
					// TODO Auto-generated catch block
					System.out.println(e.getMessage());
				}
				}
		
			while(this.end);
		
			
			try {
				in.close();
				out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println(e.getMessage());
			}
		

		}

	public LinkedHashMap<String, ObjectOutputStream> getClient() {
		return client;
	}

	public void setClient(LinkedHashMap<String, ObjectOutputStream> clients) {
		this.client = clients;
	}
	
	public LinkedHashMap<String, ObjectOutputStream> getClientsAfterClose() {
		
		return this.client;
	}

	public ObjectOutputStream getSoc() {
		return soc;
	}

	public void setSoc(ObjectOutputStream soc) {
		this.soc = soc;
	}
	
	public boolean writeJson(File f,  Message ms) {
		boolean is= false;
		Gson gs = new Gson();
		
		
		
		RandomAccessFile ac = null;
		try {
			ac = new RandomAccessFile(f, "rw");
			
			FileChannel channel = ac.getChannel();
			int x = 0;
			String s = "";
			
			
			while((x = ac.read()) != -1) {
				
				s += Character.toString((char)x);
			}
			
			if(s.getBytes().length == 0) {
				String stob = "["+gs.toJson(ms, Message.class)+"]";
				ByteBuffer bt = ByteBuffer.allocate(stob.getBytes().length);
				
				bt.put(stob.getBytes());
				
				bt.flip();
				
				channel.write(bt);
				
				is = true;
				
			}else {
				
				String stob = ","+gs.toJson(ms, Message.class)+"]";
				ByteBuffer bt = ByteBuffer.allocate(stob.getBytes().length);
				
				bt.put(stob.getBytes());
				
				bt.flip();
				ac.seek(s.getBytes().length-1);
				channel.write(bt);
				
				is = true;
			}
			
			channel.close();
			ac.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
			
			try {
                assert ac != null;
                ac.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				System.out.println(e1.getMessage());
			}
		}
		return is;
		
		
	}
public String readOfflineMessages(String userfile) {
		
		String jst = "";
		
		File f = new File("confid/offline/"+userfile+".json");
		
		
		
		if(!f.exists()) {
			
			System.out.println("no offline messages");
			
			jst = null;
		}else {
			
			if(f.length() > 0) {
			
			try {
				FileInputStream fin = new FileInputStream(f);
		
				int x = 0;
				while((x = fin.read()) != -1) {
					
					char  c = (char)x;
					jst += Character.toString(c);
					
				}
				
				FileOutputStream fout = new FileOutputStream(f);
				
				PrintWriter out = new PrintWriter(fout);
				
				out.write("");
				out.flush();
				
				out.close();
				fin.close();
				System.out.println(jst);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println(e.getMessage());
			}
			
		}else {
			
			jst = null;
		}
		}
		return jst;
	}
}
