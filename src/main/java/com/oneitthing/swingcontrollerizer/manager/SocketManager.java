package com.oneitthing.swingcontrollerizer.manager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;


public class SocketManager {

	private static SocketManager instance = null;

	public static String SOCKET = "socket";

	public static String IN = "in";

	public static String OUT = "out";

	Map<String, Map<String, Object>> socketMap = new HashMap<String, Map<String, Object>>();


	public static SocketManager getInstance() {
		if(instance == null) {
			instance = new SocketManager();
		}
		return instance;
	}

	private SocketManager() {
	}

	public Map<String, Object> connect(String ip, int port) throws IOException {
		String identifier = ip + ":" + port;

		Map<String, Object> map = socketMap.get(identifier);

		if(map == null) {
			Socket socket = new Socket(ip, port);
			InputStream in = socket.getInputStream();
			OutputStream out = socket.getOutputStream();

			map = new HashMap<String, Object>();
			map.put(SOCKET, socket);
			map.put(IN, in);
			map.put(OUT, out);
			socketMap.put(identifier, map);
		}

		return map;
	}

	public void disconnect(String ip, int port) throws IOException {
		String identifier = ip + ":" + port;

		Map<String, Object> map = socketMap.get(identifier);

		if(map == null) {
		}else{
			Socket socket = (Socket)map.get(SOCKET);
			InputStream in = (InputStream)map.get(IN);
			OutputStream out = (OutputStream)map.get(OUT);

			in.close();
			out.close();
			socket.close();

			socketMap.remove(identifier);
		}
	}
}
