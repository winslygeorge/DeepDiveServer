import java.util.LinkedList;
import java.util.Queue;

/**
 * 
 */

/**
 * @author georgos7
 *
 */
public class MessageQueue {
	
	
	Queue<Message> msgq = new LinkedList<Message>(); 
	
	public Queue<Message>  AddMsg(Message msg) {
		
		
		msgq.add(msg);
		
		return msgq;
	}
	
	public Message removeMsg(){
		
		return msgq.remove();
	}

	public int getMsgQueueSize() {
		
		return msgq.size();
	}
	
	public boolean checkqIsEmpty() {
		
		return msgq.isEmpty();
	}
}
