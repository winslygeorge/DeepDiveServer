/**
 * 
 */

/**
 * @author georgos7
 *
 */
public class Jprac {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
	MessageQueue msq = new MessageQueue();
	
	msq.AddMsg(new Message("private", "text", "winslow", "tomas", "helle", true));
	System.out.println(msq.AddMsg(new Message("private", "text", "peter", "tomas", "helle", true)));
	System.out.println(msq.getMsgQueueSize());
	System.out.println(msq.removeMsg() +": "+ msq.getMsgQueueSize());
	}

}
