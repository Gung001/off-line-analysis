package com.lxgy.analysis.track.ae.sdk;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * 发送url 数据的监控者，用于启动一个单独的线程来发送数据
 * @author Gryant
 */
public class SendDataMonitor {

    private static final Logger log = LoggerFactory.getLogger(SendDataMonitor.class);

    /**队列，用于存储发送的URL*/
    private BlockingQueue<String> queue = new LinkedBlockingDeque<String>();

    /**用于单例的一个类对象*/
    private static SendDataMonitor monitor = null;

    private SendDataMonitor() {
        // 私有构造方法，进行单例模式的创建

    }

    /**
     * 获取单例的monitor对象实例
     * @return
     */
    public static SendDataMonitor getSendDataMonitor() {

        if (monitor == null) {
            synchronized (SendDataMonitor.class) {
                if (monitor == null) {
                    monitor = new SendDataMonitor();

                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            // 线程中调用具体的处理方法
                            SendDataMonitor.monitor.run();
                        }
                    });

                    // 测试的时候不设置为守护模式
                    // thread.setDaemon(true);
                    thread.start();
                }
            }
        }
        return monitor;
    }

    /**
     * 添加一个url到队列中
     * @param url
     */
    public static void addSendUrl(String url) throws InterruptedException {
        getSendDataMonitor().queue.put(url);
    }

    /**
     * 正式发送
     */
    private void run(){

        // 实时监控有数据就会发送
        while (true) {
            try {
                String url = this.queue.take();
                HttpRequestUtil.sendData(url);
            } catch (InterruptedException e) {
                log.warn("发送url异常", e);
            }
        }
    }

    /**
     * 内部类，用户发送数据的HTTP工具类
     */
    public static class HttpRequestUtil{

        /**
         * 具体发送url 的方法
         * @param url
         */
        public static void sendData(String url){

            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {

                URL obj = new URL(url);

                // 打开连接
                connection = (HttpURLConnection) obj.openConnection();
                // 连接过期时间
                connection.setConnectTimeout(5000);
                // 读取数据过期时间
                connection.setReadTimeout(5000);
                // 设置请求类型 GET
                connection.setRequestMethod("GET");

                /**不可行*/
                /* connection.connect(); */
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                log.debug("成功发送url:" + url);
            } catch (Exception e) {
                log.warn("用户发送数据的HTTP工具类异常", e);
            } finally {

                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    if (connection != null) {
                        connection.disconnect();
                    }
                } catch (Exception e) {
                }
            }
        }
    }
}
