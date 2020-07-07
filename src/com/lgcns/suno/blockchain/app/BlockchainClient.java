package com.lgcns.suno.blockchain.app;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class BlockchainClient implements Runnable {
  private static final String MSG_GET_BLOCK_LIST = "BLOCK_LIST";
  private static final String MSG_ADD_TRANSACTION = "TX";
  private static final String MSG_SEPARATOR = "#";

  private Socket client;
	private PrintWriter outputStream;
	private Scanner inputStream;
	private String addr = "localhost";
	private int port = 7777;
	private String nick;

	public BlockchainClient(String addr, int port) throws IOException {
    this.addr = addr;
    this.port = port;
	}

	private void connect() throws IOException {
		Scanner keyboard = new Scanner(System.in);

    // connect to server
		InetAddress host = null;
		try {
			host = InetAddress.getByName(this.addr);
		} catch (UnknownHostException e1) {
			System.out.println("Host not found");
		}
		System.out
				.println("You are now connected to: " + host.getHostAddress());

		client = null;
		try {
			client = new Socket(host, port);
			client.setReuseAddress(true);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("not found");
    }

		inputStream = new Scanner(client.getInputStream());
		outputStream = new PrintWriter(client.getOutputStream());

		Thread thread = new Thread(this);
		thread.start();

		// continuously listen your user input
		while (keyboard.hasNextLine()) {
			String msg = keyboard.nextLine();
			outputStream.println(nick + " says: " + msg);
			outputStream.flush();
		}
	}

	public static void main(String[] args) throws Exception {
    BlockchainClient client = new BlockchainClient(args[0], Integer.parseInt(args[1]));
    client.connect();
	}

  // public static void main(String[] args) {
  //   try {
  //     System.out.print(sendBlockList());
  //     System.out.print(sendTransaction());
  //     System.exit(0);
  //   } catch (Exception e) {
  //     e.printStackTrace();
  //   }
  // }

  // public static String sendBlockList() {
  //   StringBuilder sb = new StringBuilder();
  //   BufferedWriter writer = null;
  //   BufferedReader reader = null;

  //   try {
  //     String buf = null;
  //     Socket client = new Socket(IP, PORT);
  //     writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
  //     writer.write(MSG_GET_BLOCK_LIST);
  //     writer.flush();
  //     reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
  //     buf = reader.readLine();
  //     sb.append(buf);
  //     // while ((buf = reader.readLine()) != null) {
  //     //   sb.append(buf);
  //     // }
  //   } catch (UnknownHostException e) {
  //     System.err.println("Don't know about host");
  //   } catch (IOException e) {
  //     System.err.println("Couldn't get I/O for the connection to host");
  //   } catch (Exception e) {
  //     e.printStackTrace();
  //   } finally {
  //     try {
  //       if (writer != null) {
  //         writer.close();
  //       }
  //       if (reader != null) {
  //         reader.close();
  //       }
  //     } catch (Exception e) {
  //       e.printStackTrace();
  //     }
  //   }

  //   return sb.toString();
  // }

  // public static String sendTransaction() {
  //   StringBuilder sb = new StringBuilder();
  //   BufferedWriter writer = null;
  //   BufferedReader reader = null;

  //   try {
  //     String buf = null;
  //     Socket client = new Socket(IP, PORT);
  //     writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
  //     writer.write(MSG_GET_BLOCK_LIST);
  //     writer.flush();
  //     reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
  //     while ((buf = reader.readLine()) != null) {
  //       sb.append(buf);
  //     }
  //   } catch (UnknownHostException e) {
  //     System.err.println("Don't know about host");
  //   } catch (IOException e) {
  //     System.err.println("Couldn't get I/O for the connection to host");
  //   } catch (Exception e) {
  //     e.printStackTrace();
  //   } finally {
  //     try {
  //       if (writer != null) {
  //         writer.close();
  //       }
  //       if (reader != null) {
  //         reader.close();
  //       }
  //     } catch (Exception e) {
  //       e.printStackTrace();
  //     }
  //   }

  //   return sb.toString();
  // }

	@Override
	public void run() {
		while (true) {
			if (inputStream.hasNextLine())
				System.out.println(inputStream.nextLine());
		}
	}

}