package com.lgcns.test.examples;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;

import com.lgcns.test.util.DateUtil;
import com.lgcns.test.util.FileUtil;

public class RunManager {
  public static HashMap<String, Bus> busMap = new HashMap<>();

  public static BusList buslist  = new BusList();
  public static StationList stationlist  = new StationList();

  public static Bus noBus = new Bus ("NOBUS", null, 0);
  
  public static Comparator<Bus> locationSort = (o1, o2) -> o1.location - o2.location;
  public static Comparator<Bus> predSort = (o1, o2) -> o1.predLocation - o2.predLocation;
  public static Comparator<String> nameSort = (o1, o2) -> o1.compareTo(o2);
  public static Comparator<Station> locationStSort = (o1, o2) -> o1.location - o2.location;

  //ex) LinkedHashMap Sort
  //public static Comparator<Map.Entry<String, Bus>> idSort = (o1, o2) -> o1.getValue().name.compareTo(o2.getValue().name);
  //LinkedList<Map.Entry<String, Bus>> list = new LinkedList<>(map.entrySet());
  //Collections.sort(list, idSort);
  
  
  public static void main(String[] args) {
    System.setProperty("line.separator", "\n");
    
    // 파일 읽어 자료 초기화
    readStations();
    
    // 소켓 서버 오픈
    NetworkServer network = new NetworkServer();
    network.start(9876);
    
  }


  public static void readStations() {
    String stFile = "./INFILE/STATION.TXT";
    
    // 정류장 정보 읽기
    Scanner stReader = FileUtil.getReader(stFile);
    while (stReader.hasNextLine()) {
      String line = stReader.nextLine().toUpperCase();

      String[] arr = line.split("#");
      Station station = new Station(arr[0], Integer.parseInt(arr[1]), Integer.parseInt(arr[2]));
      stationlist.list.add(station);
      stationlist.nameOrdered.add(station.name);
    }
    stReader.close();
  }
  
  public static void readBusStatus(String str) {
    String[] arr = str.split("#");
    int len = arr.length;
    
    try {
      buslist.date = DateUtil.formatTime.parse(arr[0]);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    
    for (int i = 1; i < len; i++) {
      String arr2[] = arr[i].split(",");
      Bus bus = new Bus(arr2[0], buslist.date, Integer.parseInt(arr2[1]));
      
      if (buslist.nameOrdered.contains(bus.name)) {
        busMap.get(bus.name).update(bus);
      } else {
        buslist.nameOrdered.add(bus.name);
        buslist.list.add(bus);
        busMap.put(bus.name, bus);
      }
    }
  }
  
  public static void calcLocations(Date baseTime) {
    // 기준시간 위치 변경
    for (Bus bus : buslist.list) {
      bus.calcLocation(baseTime);
    }
    
    Collections.sort(stationlist.list, locationStSort);
    Collections.sort(stationlist.nameOrdered, nameSort);

    Collections.sort(buslist.list, predSort);
    Collections.sort(buslist.nameOrdered, nameSort);
    
  }
  
  public static void printPrepost() {
    String filename = "./OUTFILE/PREPOST.TXT";
    PrintWriter pw = FileUtil.getWriter(filename);
    
    for (String busName: buslist.nameOrdered) {
      Bus prev = null, post = null, curr = null;
      
      // 전/후 버스 찾기
      for (int i = 0; i < buslist.list.size(); i++) {
        if (busName.equalsIgnoreCase(buslist.list.get(i).name)) {
          // 기준 버스
          curr = buslist.list.get(i);
          
          // 선행
          if (i == 0) {
            post = noBus;
          } else {
            post = buslist.list.get(i - 1);
          }
          
          // 후행
          if (i == buslist.list.size() - 1) {
            prev = noBus;
          } else {
            prev = buslist.list.get(i + 1);
          }
        }
      }
      String result = String.format("%s#%s#%s,%05d#%s,%05d", DateUtil.formatTime.format(buslist.date), busName, prev.name, prev.distance(curr), post.name, post.distance(curr)); 
      pw.println(result);
      pw.flush();
      //System.out.println(result);
    }
    
    pw.close();
  }

  public static void printArrival() {
    String filename = "./OUTFILE/ARRIVAL.TXT";
    PrintWriter pw = FileUtil.getWriter(filename);
    
    for (Station st: stationlist.list) {
      Bus near = null;
      
      // 가장 가까운 버스 찾기
      int indexNearBus = -1;
      for (int i = 0; i < buslist.list.size(); i++) {
        if (buslist.list.get(i).location > st.location) {
          break;
        }
        indexNearBus = i;
      }
      
      // 도착 예정 버스 (i -1) or noBus
      near = (indexNearBus < 0) ? noBus : buslist.list.get(indexNearBus);
      
      String result = String.format("%s#%s#%s,%05d", DateUtil.formatTime.format(buslist.date), st.name, near.name, near.distance(st)); 
      pw.println(result);
      pw.flush();
      //System.out.println(result);
    }
    
    pw.close();
  }

  public static void sendMobile(ClientConnection conn, Station st) {
    // 스테이션 처리
    Station prev = null, post = null;

    // 승객위치 업데이트(STAxx,승객위치) -> 승객위치에서 가까운 정류장 얻기
    int idx = 0;
    for (; idx < RunManager.stationlist.list.size(); idx++) {
      if (RunManager.stationlist.list.get(idx).location > conn.bus.location) {
        break;
      }
    }
    
    post = RunManager.stationlist.list.get(idx);
    if (idx > 0 ) {
      prev = RunManager.stationlist.list.get(idx - 1);
    }
    
    // 두 개의 정류장 기준 탈 수 있는 버스 목록 구하기
    ArrayList<Bus> buses = new ArrayList<>();
    
    // 정류장 도착 예정 버스 찾기
    Date postTime = DateUtil.addSeconds(buslist.date, Math.abs(conn.bus.location - post.location));
    for (int i = 0; i < buslist.list.size(); i++) {
      if (buslist.list.get(i).predictTime(post).getTime() >= postTime.getTime()) {
        buses.add(buslist.list.get(i));
      }
    }
    
    if (prev != null) {
      Date prevTime = DateUtil.addSeconds(buslist.date, Math.abs(conn.bus.location - prev.location));
      
      for (int i = 0; i < buslist.list.size(); i++) {
        if (buslist.list.get(i).predictTime(prev).getTime() >= prevTime.getTime()) {
          if (!buses.contains(buslist.list.get(i))) {
            buses.add(buslist.list.get(i));
          }
        }
      }
    }
      
    // 구해진 버스로 목적지 도착 예상 시간 구하기
    Date fastest = null; 
    for (Bus bus : buses) {
      Date pred = bus.predictTime(st); 
      if (fastest == null || fastest.getTime() > pred.getTime()) {
        fastest = pred; 
      }
    }
    
    // 출력
    conn.sendMessage(String.format("%s", DateUtil.formatTime.format(fastest)));
  }
  
  public static void sendSignage() {
    ArrayList<String> signage = new ArrayList<>();
    signage.add("./SIGNAGE.EXE");
    ProcessBuilder pb = new ProcessBuilder(signage);
    Process ps;
    try {
      ps = pb.start();
      PrintWriter pw = FileUtil.getWriter(ps.getOutputStream());

      for (Station st: stationlist.list) {
        Bus near = null;
        
        // 가장 가까운 버스 찾기
        int indexNearBus = -1;
        for (int i = 0; i < buslist.list.size(); i++) {
          if (buslist.list.get(i).location > st.location) {
            break;
          }
          indexNearBus = i;
        }
        
        // 해당 인덱스보다 후행 버스 중 가장 빨리 도착하는 버스 찾기
        Date arrTime = DateUtil.formatTime.parse("23:59:59");
        int idx = -1;
        for (int i = 0; i <= indexNearBus; i++) {
          Date arr = buslist.list.get(i).predictTime(st);
          if (arr.getTime() <= arrTime.getTime()) {
            arrTime = arr;
            idx = i;
          }
        }
        
        // 도착 예정 버스 (i -1) or noBus
        near = (idx < 0) ? noBus : buslist.list.get(idx);
            
        String result = String.format("%s#%s#%s,%s", DateUtil.formatTime.format(buslist.date), st.name, near.name, near.predictTimeToString(st)); 
        pw.println(result);
        pw.flush();
      }
      
//      ps.waitFor(10, TimeUnit.SECONDS);
      pw.close();
    } catch (IOException | ParseException e) {
      e.printStackTrace();
//    } catch (InterruptedException e) {
//      e.printStackTrace();
    }
  }
  
  public static void sendBusInfo(ClientConnection conn) {
    ArrayList<Bus> prevs = new ArrayList<>();
    int loc = conn.bus.predLocation;
    Bus prev = noBus;
    Bus post = noBus;
    long prevLong = Long.MAX_VALUE, postLong = Long.MIN_VALUE;
    
    
    // 선/후행 버스목록(2,1,3)
    for (int i =0; i < buslist.list.size(); i++) {
      Bus bus = buslist.list.get(i);
      
      if (!conn.bus.equals(bus)) {
        if (bus.predLocation < loc) {
          prevs.add(bus);
        } else {
          if (post.equals(noBus)) {
            post = bus;
          }
        }
      }
    }

    // 후행 버스 검색
    for (Bus bus : prevs) {
      long busTime = bus.predictTime(loc).getTime();
      if (prev.equals(noBus) || prevLong >= busTime) {
        prev = bus;
        prevLong = busTime;
      }
    }
    
    // 선행 버스 검색
    if (!post.equals(noBus)) {
      postLong = conn.bus.predictTime(post.predLocation).getTime();
    }
    
    postLong = postLong - buslist.date.getTime();
    String postTime = (post.equals(noBus)) ? "00:00:00" : DateUtil.getSecondWithDateFormat((long)(postLong / 1000));
    
    prevLong = prevLong - buslist.date.getTime();
    String prevTime = (prev.equals(noBus)) ? "00:00:00" : DateUtil.getSecondWithDateFormat((long)(prevLong / 1000));
    
    conn.sendMessage(String.format("%s#%s#%s#%s", post.name, postTime, prev.name, prevTime));
  }
}

class BusList {
  Date date = null;
  CopyOnWriteArrayList<Bus> list = new CopyOnWriteArrayList<>();
  CopyOnWriteArrayList<String> nameOrdered = new CopyOnWriteArrayList<>();

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(date);
    sb.append(System.lineSeparator());
    for (Bus bus : list) {
      sb.append(bus.toString());
      sb.append(System.lineSeparator());
    }
    return sb.toString();
  }
}

class NetworkServer {
  public ServerSocket server;
  public ArrayList<ClientConnection> clients = new ArrayList<>();
  
  public void start(int port) {
    try {
//      messageHandler = new ServerMessageHandler(this);
      server = new ServerSocket(port);
      server.setReuseAddress(true);
      System.out.println("Accepting clients...");
    } catch (IOException e) {
      System.out.println(e.getStackTrace());
    }

    Socket client;
    
    while (true) {
      try {
//        if (this.isInterrupted()) return;

        client = server.accept();
        System.out.println("New client accepted..." + client.getRemoteSocketAddress());
        System.out.println("Total Clients: " + clients.size());

        ClientConnection handler = new ClientConnection(client);
        clients.add(handler);
        
        Thread thread = new Thread(handler);
        thread.start();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}

class ClientConnection implements Runnable {
  Socket client;
  boolean isMobile = false;
  Bus bus = null;
  Scanner inputStream;
  
  public ClientConnection(Socket conn) {
    this.client = conn;
  }

  @Override
  public void run() {
    try {
      inputStream = new Scanner(client.getInputStream());

      while (true) {
        if (Thread.currentThread().isInterrupted())
          break;

        if (!inputStream.hasNext())
          return;
        
        String line = inputStream.next().trim().toUpperCase();
        if (line.length() > 0) {
          processMessage(line);
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public synchronized void processMessage(String msg) {
    if (msg.substring(0, 3).equalsIgnoreCase("BUS")) {
      this.isMobile = false;
      this.bus = new Bus(msg);
      RunManager.buslist.nameOrdered.add(bus.name);
      RunManager.buslist.list.add(bus);
      RunManager.busMap.put(bus.name, bus);
    } else if (msg.substring(0, 3).equalsIgnoreCase("STA")) {
      if (!this.isMobile) return;
      String arr[] = msg.split("#");
      this.bus.location = Integer.parseInt(arr[1]); 
      this.bus.speed = 1;
      Station st = RunManager.stationlist.findStation(arr[0]);
      RunManager.sendMobile(this, st);
    } else if (msg.equalsIgnoreCase("MOBILE")) {
      this.isMobile = true;
      this.bus = (Bus)(new Mobile());
    } else if (msg.equalsIgnoreCase("PRINT")) {
      if (this.isMobile) {
        RunManager.calcLocations(RunManager.buslist.date);
        RunManager.printPrepost();
        RunManager.printArrival();
        RunManager.sendSignage();
      } else {
        RunManager.sendBusInfo(this);
      }
    } else {
      if (this.isMobile) return;
      
      Date date = null;
      String arr[] = msg.split("#");
      
      try {
        date = DateUtil.convertDate("HH:mm:ss", arr[0]);
      } catch (ParseException e) {
        e.printStackTrace();
      }
      
      if (arr.length == 2) {
        this.bus.update(date, Integer.parseInt(arr[1]));
      } 
      
      if (date != null) {
        RunManager.buslist.date = (RunManager.buslist.date == null) ? date : DateUtil.max(RunManager.buslist.date, date);
      }
    }
  }
  
  public synchronized void sendMessage(String msg) {
    try {
      PrintWriter writer = FileUtil.getWriter(this.client.getOutputStream());
      writer.print(msg);
      writer.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public synchronized void close() {
    try {
      PrintWriter writer = FileUtil.getWriter(this.client.getOutputStream());
      writer.close();
      this.client.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}

class Bus {
  Date date = null;
  String name = "";
  int    location = 0;
  Date prevDate = null;
  int    prevLocation = 0;
  int    speed = 0;
  int    predLocation = 0;
  
  public Bus(String name, Date checkTime, int location) {
    this.name = name;
    this.date = checkTime;
    this.location = location;
  }

  public Bus(String name) {
    this.name = name;
  }

  public Bus update(Bus bus) {
    if (!this.name.equalsIgnoreCase(bus.name)) {
      return null;
    } else {
      this.prevDate = new Date(this.date.getTime()); 
      this.prevLocation = this.location;
      this.date = bus.date;
      this.location = bus.location;
      getSpeed();
    }
    
    return this;
  }

  public Bus update(Date date, int location) {
    if (this.date != null) {
      this.prevDate = new Date(this.date.getTime()); 
      this.prevLocation = this.location;
    }
    
    this.date = date;
    this.location = location;
    
    if (this.prevDate != null) {
      getSpeed();
    }
    
    return this;
  }
  
  @Override
  public String toString() {
    return String.format("%s,%05d,%03d", this.name, this.location, this.speed);
  }
  
  public int getSpeed() {
    if (this.prevDate == null || this.prevLocation == 0) {
      this.speed = 0;
      return 0;
    }
    
    int dur = (int)((this.date.getTime() - this.prevDate.getTime()) / 1000);
    int dist = this.location - this.prevLocation;
    int speed = (int)Math.floor(dist / dur);
    
    this.speed = speed;
    
    return speed;
  }
  
  public int distance(Bus bus) {
    if (this == RunManager.noBus) {
      return 0;
    } else {
    return Math.abs(this.predLocation - bus.predLocation);
    }
  }
  
  public int distance(Station st) {
    if (this == RunManager.noBus) {
      return 0;
    } else {
    return Math.abs(this.predLocation - st.location);
    }
  }
  
  public Date predictTime(int location) {
    int target = location;
    int loc = this.predLocation;
    int dur = 0;
    
    if (this == RunManager.noBus) {
      try {
        return DateUtil.formatTime.parse("00:00:00");
      } catch (ParseException e) {}
      
      return null;
    }
    
    while (target > loc) {
      // 현재 위치의 제한 속도
      int limit = RunManager.stationlist.getSpeedByLocation(loc);
      loc += ((this.speed > limit) ? limit : this.speed);
      
      // dur += ((loc > target) ? 0 : 1); // 소수점 내림
      dur += 1;
    }
    
    long nlong = RunManager.buslist.date.getTime() + (dur * 1000);
    return new Date(nlong);
  }
  
  public String predictTimeToString(int location) {
    return DateUtil.formatTime.format(predictTime(location));
  }

  public Date predictTime(Station st) {
    return predictTime(st.location);
  }
  
  public String predictTimeToString(Station st) {
    return DateUtil.formatTime.format(predictTime(st));
  }
  
  public int calcLocation(Date baseTime) {
    long dur = (long)((baseTime.getTime() - this.date.getTime()) / 1000);
    
    // 초당 이동 거리... 계산... + 정류 구간별 제한 속도...
    this.predLocation = this.location;
    for (int i = 0; i < dur; i++) {
      int limit = RunManager.stationlist.getSpeedByLocation(this.predLocation);
      int dist = (speed > limit) ? limit : speed; 
      this.predLocation += dist;  
    }
    
    return this.predLocation;
  }
}

class StationList {
  CopyOnWriteArrayList<Station> list = new CopyOnWriteArrayList<>();
  CopyOnWriteArrayList<String> nameOrdered = new CopyOnWriteArrayList<>();

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (Station st : list) {
      sb.append(st.toString());
      sb.append(System.lineSeparator());
    }
    return sb.toString();
  }
  
  public int getSpeedByLocation(int location) {
    int index = 0;
    for (int i = 0; i < this.list.size(); i++) {
      if (this.list.get(i).location > location) {
        break;
      }
      
      index = i;
    }
    
    return this.list.get(index).limit;
  }

  public Station findStation(String name) {
    for (Station st : this.list) {
      if (st.name.equalsIgnoreCase(name)) {
        return st;
      }
    }
    
    return null;
  }
}

class Station {
  String name;
  int location;
  int limit;
  
  public Station(String name, int location, int limit) {
    this.name = name;
    this.location = location;
    this.limit = (int)((limit * 1000) / 60 / 60);
  }

  @Override
  public String toString() {
    return String.format("%s,%05d,%03d", name, location, limit);
  }
}

class Mobile extends Bus {
  
  public Mobile() {
    this("MOBILE");
  }
  
  public Mobile(String name) {
    super(name);
    this.speed = 1;
  }

  @Override
  public String toString() {
    return super.toString();
  }
}