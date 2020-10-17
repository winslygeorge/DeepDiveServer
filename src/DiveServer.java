import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.security.PublicKey;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;



/**
 * 
 */

/**
 * @author georgos7
 *
 */
public class DiveServer {
	
	private int port;
	
	private ServerSocket server;
	
	protected Socket client;
	protected ObjectInputStream inob;
		
	protected ObjectOutputStream outob;
	
	protected String userName;
	
	protected PublicKey pk;
	
	protected LinkedList <String>GroupMembersList = null;
	
	protected LinkedHashMap <String, LinkedList<String>> GroupList = null;
	protected GetAccess access;
	protected LinkedHashMap<String, Socket> clientSocket = new LinkedHashMap<>();
	protected LinkedHashMap<String, ObjectOutputStream> clients = new LinkedHashMap<String , ObjectOutputStream>();
	public DiveServer(int p, String user, int portt, String host, String pass) throws IOException {
		
		setPort(p);
		
	
		
		userName = user;
		
	File file = new File("confid");
	
	if(!file.exists()) {
		
		
		
		GetCertUrl validate = new GetCertUrl(host, portt, user, pass);
		
		System.out.println(validate.getAccessCertification("client").getSubjectDN()+" : certificate created");
	}
		access = new GetAccess(pass);
		pk = access.getAccessCertification("root").getPublicKey();
		
		try {
			
			
			access.getAccessCertification("client").verify(pk);
			
			System.out.println("Server Verified..");
			
			setServer(new ServerSocket(port));
		}catch(Exception e) {
			
			e.printStackTrace();
		}
		
		
		
	System.out.println("Server is online");
	
	}
	
	public void run() throws ClassNotFoundException {
		
		ExecutorService  exe = Executors.newCachedThreadPool();

		while(true) {
			
			try {
				client = server.accept();
		
				outob = new ObjectOutputStream(client.getOutputStream());
				inob = new ObjectInputStream(client.getInputStream());
				
				
				Message cmes = (Message) inob.readObject(); 
			
				try{
					
					cmes.getCertificate().verify(pk);
					
					System.out.println("connected client  app is verified");
					
					outob.writeObject(new Message("DeepDiveServer", access.getAccessCertification("client"), access.getAsymmetrickey()));
					outob.flush();
				
					if(inob.readInt()!=1) {
						
						client.close();
					}else {
						
						String u = "";
						
						u = inob.readUTF();
						if(u == null|| u.isEmpty()) {
					
							System.out.println("Username not received");
					
							client.close();
				
						}else {		
							String userToAdd = u;
							
							if(clients.containsKey(userToAdd)) {
								
								System.out.println("client already registered");
									
									System.out.println("client is reconnecting ..");
									
									this.clientSocket.get(userToAdd).close();
									
									
									clients.replace(userToAdd, outob);
									
									this.clientSocket.replace(userToAdd, client);
									
									URLDef url = new URLDef();
									
									if(url.sendOnlinePresenceToken(userToAdd, true)){
										
										System.out.println(userToAdd+ "  :  is online");
									}else {
										
										System.err.println("error sending online presence");
									}
									
									
								
							}else {
								
								

								this.clientSocket.put(userToAdd, client);
						
								clients.put(userToAdd, outob);
								
								URLDef url = new URLDef();
								
								if(url.sendOnlinePresenceToken(userToAdd, true)){
									
									System.out.println(userToAdd+ "  :  is online");
								}else {
									
									System.err.println("error sending online presence");
								}
								
								
							
								
							
							}
							exe.execute(new ClientHandler(userToAdd,client,this.clientSocket, clients, inob, outob));
						}
						
						}
				}catch(Exception e) {
					
					e.printStackTrace();
					
					inob.close();
					outob.close();
					client.close();
					
					
				}
				
				} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				}
			
				System.out.println("new client conneted"+ "\n"+ clients);
				//exe.shutdownNow();
			}
		
		
		
		}

	public int getPort() {
		return port;
	}

	public void setPort(int por) {
		this.port = por;
	}

	public ServerSocket getServer() {
		return server;
	}

	public void setServer(ServerSocket server) {
		this.server = server;
	}

	public LinkedHashMap<String, ObjectOutputStream> getClients(){
		
		return this.clients;
	}

	

}