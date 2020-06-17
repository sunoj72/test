package com.lgcns.suno.net.p2p.server.p2pserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.lgcns.suno.net.p2p.server.peers.Peer;
import com.lgcns.suno.net.p2p.server.peers.PeerManager;

public class Listener extends Thread {
	private ServerSocket mServerSocket;
	private boolean mRunning = false;
	private int mPort;

	public boolean isRunning() {
		return mRunning;
	}

	public void setRunning(boolean mRunning) {
		this.mRunning = mRunning;
	}

	public int getPort() {
		return mPort;
	}

	public void close() {
		try {
			mServerSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Listener(int port) {
		Console.write("由ъ뒪�꼫�뒪�젅�뱶 �깮�꽦.");

		try {
			this.mPort = port;
			mServerSocket = new ServerSocket(this.mPort);
			this.mRunning = true;
			this.start();
			Console.write(port + "踰� �룷�듃�뿉�꽌 �뿰寃� ��湲곗쨷...\n");
		} catch (Exception e) {
			Console.error("由ъ뒪�꼫 �깮�꽦 �떎�뙣. �궗�슜以묒씤 �룷�듃 踰덊샇 �엯�땲�떎.");
			Main.endWork();
		}
	}

	@Override
	public void run() {
		try {
			while (mRunning) {
				Socket c = mServerSocket.accept();

				if (c != null) {
					Peer p = new Peer(PeerManager.popIndex(), c);
					PeerManager.add(p);

					Console.write(p.getRemoteAddress() + ":" + p.getRemotePort() + "濡쒕��꽣�쓽 �젒�냽 �닔�씫");
				}

				Thread.sleep(10);
			}

			mServerSocket.close();
		} catch (Exception e) {
			//e.printStackTrace();
		}
		Console.write("由ъ뒪�꼫�뒪�젅�뱶 醫낅즺.");
	}

}
