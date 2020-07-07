package com.lgcns.suno.net.p2p.client.p2ppeer;

import java.io.IOException;
import java.lang.Thread.State;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import com.lgcns.suno.net.p2p.client.packets.Packet;
import com.lgcns.suno.net.p2p.client.packets.PacketHandler;
import com.lgcns.suno.net.p2p.client.peers.Peer;
import com.lgcns.suno.net.p2p.client.peers.PeerManager;


public class Main {
	private static Listener mListener;
	private static Console mConsole;

	public  static void setConsolCanRead(boolean mCanRead) {
		mConsole.setCanRead(mCanRead);
	}

	public static void endWork() {
		mConsole.setRunning(false);
		mListener.setRunning(false);
		for (int i = 0; i < PeerManager.size(); i++)
			PeerManager.get(i).setRunning(false);
		mListener.close();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("P2P Peer v1.0\n");

		int port;
		try { port = Integer.parseInt(args[1]); } catch (Exception e) { port = 9090; }
		String host;
		try { host = args[0]; } catch (Exception e) { return; }

		mListener = new Listener(port + 1);
		mConsole = new Console();

		while  (mListener.getState() != State.RUNNABLE);

		Socket s = new Socket();
		try {
			s.setReuseAddress(true);
			s.setTcpNoDelay(true);
			s.setSendBufferSize(1);
			s.bind(new InetSocketAddress(port + 1));
			s.connect(new InetSocketAddress(host, port));

			Peer p = new Peer(PeerManager.popIndex(), s);
			PeerManager.add(p);

			Packet packet = new Packet();
			packet.setType(PacketHandler.TYPE_CHECK_PEER);
			packet.setOption((byte) 0);
			String localIP = InetAddress.getLocalHost().getHostAddress();
			packet.setPayload(localIP + ":" + (port + 1));
			p.write(packet);
		} catch (IOException e) {
			Console.error("�꽌踰꾩뿉 �뿰寃� �븷 �닔 �뾾�뒿�땲�떎.");
			endWork();
		}
	}

}
