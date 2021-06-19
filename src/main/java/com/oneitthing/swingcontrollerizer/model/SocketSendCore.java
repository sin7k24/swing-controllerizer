package com.oneitthing.swingcontrollerizer.model;

import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;

import com.oneitthing.swingcontrollerizer.event.ModelProcessEvent;
import com.oneitthing.swingcontrollerizer.manager.SocketManager;

public class SocketSendCore extends BaseModel {

	private String ip;

	private int port;

	private byte[] data;


	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}


	@Override
	protected boolean preproc() throws Exception {
		SocketManager manager = SocketManager.getInstance();
		Map<String, Object> socketInfo = manager.connect(getIp(), getPort());

		Socket socket = (Socket)socketInfo.get(SocketManager.SOCKET);
		OutputStream out = (OutputStream)socketInfo.get(SocketManager.OUT);

		if (socket != null && socket.isConnected()) {
	        out.write(getData());
	        out.flush();
		}

//		Log.d("", "sendmessage");

		return super.preproc();
	}

	@Override
	protected void mainproc() throws Exception {
//		Log.d("", "SocketCore mainproc");

		fireModelSuccess(new ModelProcessEvent(this));
		fireModelFinished(new ModelProcessEvent(this));
	}
}
