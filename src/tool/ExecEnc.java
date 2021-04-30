package tool;

import java.util.List;

public class ExecEnc implements Runnable {
	public final Controller controller;
	public int index;
	private byte[] data;
	public List<EncObject> encObjectList;
	
	@Override
	public void run() {
		try {
			encObjectList.add(new EncObject(index, controller.encrypt(data)));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public ExecEnc(Controller controller, int index, byte[] data, List<EncObject> encObjectList) {
		this.controller = controller;
		this.index = index;
		this.data = data;
		this.encObjectList = encObjectList;
	}
}
