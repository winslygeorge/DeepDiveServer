import java.io.IOException;
import java.util.Scanner;

/**
 * 
 */

/**
 * @author georgos7
 *
 */
public class AppServer {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	protected static Scanner scanner =  new Scanner(System.in);
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		// TODO Auto-generated method stub
		System.out.println("Welcome DeepDiveServer \n Please Enter the HostName/IP: ");
		
		String host =  scanner.nextLine();
		
		System.out.println("Enter Password : ");
		
		String password =  scanner.nextLine();
		
		scanner.close();
		
		DiveServer server = new DiveServer(5000, host,3030,host,  password);

		server.run();
	}

}
