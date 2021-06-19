package com.oneitthing.swingcontrollerizer.model;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.oneitthing.swingcontrollerizer.event.ModelProcessEvent;
import com.oneitthing.swingcontrollerizer.manager.SocketManager;

public class SocketRecieveCore extends BaseModel {

	private String ip;

	private int port;

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

	@Override
	protected boolean preproc() throws Exception {
		SocketManager manager = SocketManager.getInstance();
		Map<String, Object> socketInfo = manager.connect(getIp(), getPort());

		Socket socket = (Socket)socketInfo.get(SocketManager.SOCKET);
		OutputStream out = (OutputStream)socketInfo.get(SocketManager.OUT);

		if (socket != null && socket.isConnected()) {
	        out.write("hogehoge".getBytes());
	        out.flush();
		}

		Reciever reciever = new Reciever();
		reciever.setSocketInfo(socketInfo);

		ExecutorService executor = Executors.newCachedThreadPool();
		Future<Object> future = executor.submit(reciever);

		return true;
	}

	@Override
	protected void mainproc() throws Exception {
//		Log.d("", "SocketCore mainproc");

	}

	@Override
	protected void postproc() throws Exception {
		fireModelSuccess(new ModelProcessEvent(this));
	}

	public class Reciever implements Callable<Object> {
		private Map<String, Object> socketInfo;

		public Map<String, Object> getSocketInfo() {
			return socketInfo;
		}

		public void setSocketInfo(Map<String, Object> socketInfo) {
			this.socketInfo = socketInfo;
		}

		public Object call() {
			int size;
			String str;
			byte[] w = new byte[1024];
			try {
				Socket socket = (Socket)getSocketInfo().get(SocketManager.SOCKET);
				InputStream in = (InputStream)getSocketInfo().get(SocketManager.IN);
//				OutputStream out = (OutputStream)getSocketInfo().get(SocketManager.OUT);

				while (socket != null && socket.isConnected()) {
					size = in.read(w);
					if (size <= 0)
						continue;
//					str = new String(w, 0, size, "UTF-8");
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					while(true) {
						size = in.read(w);
						if (size <= 0) {
							break;
						}
						out.write(w, 0, size);
					}
					out.close();
//					in.close();

					byte[] buf = out.toByteArray();

					ModelProcessEvent successEvent = new ModelProcessEvent(SocketRecieveCore.this);
					successEvent.setResult(new String(buf, "UTF-8"));
					fireModelSuccess(successEvent);

				}
			} catch (Exception e) {
//				Log.d("", "connect fail.");
			}
			return null;
		}
	};

}
