import com.sun.jndi.toolkit.url.Uri;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MyRobot {
    String[] URLs = new String[2000];
    int URL_size = 0;
    int currentURL = 0;
    int outputNumber = 0;
    BufferedWriter bufferedWriter = null;

    public MyRobot() {
        try {
            bufferedWriter = new BufferedWriter(new FileWriter("result.txt"));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Warning! Can't create result file!");
        }
    }

    protected void finalize() {
        try {
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int initialization(String URL) {
        try {
            Document document = Jsoup.connect(URL).timeout(1000 * 60).maxBodySize(0).get();//
            Elements links = document.getElementsByTag("a");
            int i = 0;
            for (Element link : links) {
                String href = link.attr("href");
                if (href.contains("content_cvpr_2018/html/")) {
                    URLs[i] = "http://openaccess.thecvf.com/" + href;
                    System.out.format("%d:%s\n", i, URLs[i]);
                    ++i;
                }
            }
            return URL_size = i;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void testWrite() {
        try {
            for (int i = 0; i < 1000; i++) {
                bufferedWriter.write(String.valueOf(i));
                bufferedWriter.newLine();
                bufferedWriter.newLine();
                bufferedWriter.newLine();
            }
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int testRobot() {
        try {
            Document document = Jsoup.connect("https://www.qq.com/").timeout(1000 * 60).maxBodySize(0).get();//
            Elements links = document.getElementsByTag("a");
            int i = 0;
            for (Element link : links) {
                String href = link.attr("href");
                if (href.contains("new.qq.com/")) {
                    System.out.format("%s\n", href);
                    ++i;
                }
            }
            return URL_size = i;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void workMethod() {
        ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
        for (int i = 0; i < URL_size; i++) {
            cachedThreadPool.execute(() -> getContent(getURL()));
        }
        try {
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        cachedThreadPool.shutdown();
    }

    public void getContent(String URL) {
        try {
            Document document = Jsoup.connect(URL).timeout(1000 * 600).maxBodySize(0).get();//
            Elements links = document.getElementsByTag("div");
            String content = null, title = null;
            for (Element link : links) {
                switch (link.id()) {//case "authors"://and time
                    case "papertitle":
                        title = link.text();
                        break;
                    case "abstract":
                        content = link.text();
                        break;
                }
            }
            synchronized (this) {
                System.out.println(outputNumber);
                System.out.println();
                bufferedWriter.write(String.valueOf(outputNumber));
                bufferedWriter.newLine();
                System.out.format("Title: %s\n", title);
                bufferedWriter.write(String.format("Title: %s\n", title));
                System.out.format("Abstract: %s\n", content);
                bufferedWriter.write(String.format("Abstract: %s\n", content));
                bufferedWriter.newLine();
                bufferedWriter.newLine();
                outputNumber++;
            }
            System.out.println();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized String getURL() {
        if (currentURL < URL_size) return URLs[currentURL++];
        else return null;
    }
}
