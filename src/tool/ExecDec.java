package tool;

import java.math.BigInteger;
import java.util.List;

public class ExecDec implements Runnable {
	public final Controller controller;
	public int index;
	private final byte[] data;
	public List<DecObject> decObjectList;
	
	@Override
	public void run() {
		try {
			decObjectList.add(new DecObject(index, controller.decrypt(new BigInteger(data))));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public ExecDec(Controller controller, int index, byte[] data, List<DecObject> decObjectList) {
		this.controller = controller;
		this.index = index;
		this.data = data;
		this.decObjectList = decObjectList;
	}
}
