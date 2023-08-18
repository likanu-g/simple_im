package org.example;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Socks5ProxyServer {
    public static void main(String[] args) {
        int port = 1080; // 代理服务器监听的端口

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("SOCKS5 Proxy Server is listening on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                new ProxyThread(clientSocket).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ProxyThread extends Thread {
    private Socket clientSocket;

    public ProxyThread(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            InputStream clientIn = clientSocket.getInputStream();
            OutputStream clientOut = clientSocket.getOutputStream();

            // 响应客户端，表示代理连接建立成功
            byte[] response = {5, 0};
            clientOut.write(response);

            // 读取客户端请求
            byte[] request = new byte[10];
            clientIn.read(request);

            int version = request[0];
            int cmd = request[1];
            int addressType = request[3];

            if (cmd == 1) { // CONNECT命令
                String targetAddress = null;
                int targetPort = 0;

                if (addressType == 1) { // IPv4地址
                    targetAddress = request[4] + "." + request[5] + "." + request[6] + "." + request[7];
                    targetPort = (request[8] & 0xFF) << 8 | (request[9] & 0xFF);
                }

                // 建立与目标服务器的连接
                Socket targetSocket = new Socket(targetAddress, targetPort);
                InputStream targetIn = targetSocket.getInputStream();
                OutputStream targetOut = targetSocket.getOutputStream();

                // 响应客户端，表示与目标服务器连接建立成功
                byte[] targetResponse = {5, 0, 0, 1, 0, 0, 0, 0, 0, 0};
                clientOut.write(targetResponse);

                // 开启数据转发
                Thread clientToTarget = new Thread(() -> {
                    try {
                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = clientIn.read(buffer)) != -1) {
                            targetOut.write(buffer, 0, bytesRead);
                            targetOut.flush();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

                Thread targetToClient = new Thread(() -> {
                    try {
                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = targetIn.read(buffer)) != -1) {
                            clientOut.write(buffer, 0, bytesRead);
                            clientOut.flush();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

                clientToTarget.start();
                targetToClient.start();

                clientToTarget.join();
                targetToClient.join();

                targetSocket.close();
            }

            clientSocket.close();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}

