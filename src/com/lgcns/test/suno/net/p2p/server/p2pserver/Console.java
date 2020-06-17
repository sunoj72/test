package com.lgcns.suno.net.p2p.server.p2pserver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Vector;

import com.lgcns.suno.net.p2p.server.peers.Peer;
import com.lgcns.suno.net.p2p.server.peers.PeerManager;
import com.lgcns.suno.net.p2p.server.sharing.FileManager;
import com.lgcns.suno.net.p2p.server.sharing.SharedFile;

public class Console extends Thread {
	private static boolean mJustPrompted = false;
	private static BufferedWriter mLogger = null;

	/**
	 * @uml.property  name="mRunning"
	 */
	private boolean mRunning = false;
	/**
	 * @uml.property  name="mCanRead"
	 */
	private boolean mCanRead = false;

	public static boolean openLogger() {
		 try {
			mLogger = new BufferedWriter(new FileWriter("p2p_server.log"));
			Console.write("濡쒓퉭 �떆�옉: p2p_server.log");
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	public boolean isRunning() {
		return mRunning;
	}

	public void setRunning(boolean mRunning) {
		this.mRunning = mRunning;
	}

	public void setCanRead(boolean mCanRead) {
		this.mCanRead = mCanRead;
	}

	public static void write(String msg) {
		write(msg, true);
	}

	public static void write(String msg, boolean doLog) {
		Calendar cal = Calendar.getInstance();

		String buf = String.format("[%02d:%02d:%02d] %s", cal.get(Calendar.HOUR), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND), msg);
		if (mJustPrompted) System.out.println();
		mJustPrompted = false;

		System.out.println(buf);
		if (doLog && mLogger != null) try { mLogger.write(buf + "\n"); mLogger.flush(); } catch (IOException e) { }
	}

	public static void write(Peer p, String msg) {
		write(String.format("(%02x:%s) %s", p.getPeerIndex(), p.getPeerId(), msg));
	}

	public static void error(String msg) {
		write("Error> " + msg);
	}

	public static void error(Peer p, String msg) {
		write(p, "Error> " + msg);
	}

	public String readLine() {
		String buf = null;
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			buf = br.readLine();
		} catch (IOException e) { }

		return buf;
	}

	public Console() {
		Console.write("肄섏넄�뒪�젅�뱶 �깮�꽦.");

		this.mRunning = true;
		this.start();
	}

	@Override
	public void run() {
		boolean isPrompted = false;

		while (mRunning) {
			if (mCanRead) {
				if (!isPrompted) {
					System.out.print("Cmd> ");
					isPrompted = !isPrompted;
					mJustPrompted = true;
				}

				String buf = readLine();
				if (buf != null) {
					if (buf.length() > 0) {
						mCanRead = false;
						if (mLogger != null) try { mLogger.write("Cmd> " + buf + "\n"); } catch (IOException e) { }
						buf.replace('\t', ' ');
						String cmd[] = buf.split(" ");
						doCommand(cmd);
					}
					isPrompted = false;
				}
			}

			try { Thread.sleep(10); } catch (InterruptedException e) { }
		}

		if (mLogger != null) try {
			mLogger.flush();
			mLogger.close();
		} catch (IOException e) { }

		Console.write("肄섏넄�뒪�젅�뱶 醫낅즺.");
	}

	private void doCommand(String[] cmd) {
		if (cmd[0].equalsIgnoreCase("quit")) {
			Console.write("�꽌踰꾨�� 醫낅즺�빀�땲�떎.");
			Main.endWork();
			return;
		} else if (cmd[0].equalsIgnoreCase("show")) {
			if (!isCmdError(cmd, 2)) {
				if (cmd[1].equalsIgnoreCase("peers")) {
					StringBuilder sb = new StringBuilder();
					sb.append("�쟾泥� �뵾�뼱 �젙蹂� 異쒕젰\n");
					sb.append("==========================================================\n");
					sb.append(" Type | No. |    �씠由�    |    二�    �냼    |  �룷�듃  | 濡쒕뱶 \n");
					sb.append("==========================================================\n");
					String form = " %2s | %3x |%12s|%16s|%8d| %4d \n";
					for (int i = 0; i < PeerManager.size(); i++) {
						Peer p = PeerManager.get(i);
						sb.append(String.format(form,
								p.isLeaderPeer() ? "由щ뜑": "�씪諛�",
								p.getPeerIndex(),
								p.getPeerId(),
								p.getRemoteAddress(),
								p.getRemotePort(),
								p.getLoads()));
					}
					sb.append("==========================================================\n");
					Console.write(sb.toString());
					mCanRead = true;
					return;
				} else if (cmd[1].equalsIgnoreCase("files")) {
					if (!isCmdError(cmd, 3)) {
						if (cmd[2].equalsIgnoreCase("all")) {
							StringBuilder sb = new StringBuilder();
							sb.append("�쟾泥� 怨듭쑀�뙆�씪 �젙蹂� 異쒕젰\n");
							sb.append("======================================================================\n");
							sb.append(" �뙆�씪  ID |   �냼 �쑀 �옄   | �뙆�씪 �겕湲� |         �뙆  �씪  �씠  由�         \n");
							sb.append("======================================================================\n");
							String form = "  %8s | %12s | %9s | %s \n";
							for (int i = 0; i <= FileManager.getLastFileId(); i++) {
								Hashtable<Peer, Vector<SharedFile>> files = new Hashtable<Peer, Vector<SharedFile>>();
								FileManager.findFile(i, files);
								for (Peer p: files.keySet())
									for (SharedFile f: files.get(p)) {
										sb.append(String.format(form, f.getFileId(), p.getPeerId(), f.getFileSize2(), f.getFileName()));
									}
							}
							sb.append("======================================================================\n");
							Console.write(sb.toString());
							mCanRead = true;
							return;
						} else {
							Peer p = PeerManager.get(cmd[2]);
							if (p == null) {
								Console.error("�빐�떦 �뵾�뼱瑜� 李얠쓣 �닔 �뾾�뒿�땲�떎.");
								mCanRead = true;
								return;
							}
							StringBuilder sb = new StringBuilder();
							sb.append("�뵾�뼱 " + p.getPeerId() + " 怨듭쑀�뙆�씪 �젙蹂� 異쒕젰\n");
							sb.append("=======================================================\n");
							sb.append(" �뙆�씪  ID | �뙆�씪 �겕湲� |         �뙆  �씪  �씠  由�         \n");
							sb.append("=======================================================\n");
							String form = "  %8s | %9s | %s \n";
							Vector<SharedFile> files = FileManager.getPeerFiles(p);
							for (SharedFile f: files)
								sb.append(String.format(form, f.getFileId(), f.getFileSize2(), f.getFileName()));
							sb.append("=======================================================\n");
							Console.write(sb.toString());
							mCanRead = true;
							return;
						}
					}
				}
			}
		}

		Console.error("�옒紐� �맂 紐낅졊�엯�땲�떎.");
		mCanRead = true;
	}

	private boolean isCmdError(String[] cmd, int argc) {
		if (cmd.length < argc) {
			//Console.error("�옒紐� �맂 紐낅졊�엯�땲�떎.");
			return true;
		}

		return false;
	}
}
