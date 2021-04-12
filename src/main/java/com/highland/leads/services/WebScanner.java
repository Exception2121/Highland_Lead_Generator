package com.highland.leads.services;

import com.highland.leads.models.JobRequest;
import com.highland.leads.models.Lead;
import com.highland.leads.cosmos.Database;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.chrome.*;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.*;
import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class WebScanner implements Runnable{
    private static WebDriver driver;
    private static ChromeOptions options;
    static Lead[] leads;
    public PDFReader reader;
    public static String startDate = "";
    public static String endDate = "";
    static long lastModifiedTime = Long.MIN_VALUE;
    static File directory;
    static File[] files;
    static File chosenFile;
    public static int limit = 0;
    static int location = 1;
    static int numSkipped = 0;
    public static String username = "";
    public static String password = "";
    public static JobRequest jobRequest;
    public static String PDF_PATH = "";
    public static boolean isWindows;

    private void resetScanner(){
        location = 1;
        numSkipped = 0;
        limit = 0;
        directory = null;
        files = null;
        chosenFile = null;
        PDFReader.finished = false;
        PDFReader.processNum = 0;
        PDFReader.currentlyProcessing = 0;
    }

    public WebScanner()
    {
        resetScanner();
        options = new ChromeOptions();
        Map<String, Object> preferences = new Hashtable<String, Object>();
        preferences.put("plugins.plugins_disabled", new String[] {
                "Chrome PDF Viewer"
        });
        preferences.put("plugins.always_open_pdf_externally", true);
        if(System.getProperty("os.name").toLowerCase().contains("windows")){
            isWindows = true;
            preferences.put("download.default_directory", System.getProperty("user.dir")+"\\PDFs");
            PDF_PATH = System.getProperty("user.dir")+"\\PDFs";
        }else{
            isWindows = false;
            preferences.put("download.default_directory", System.getProperty("user.dir")+"/PDFs");
            PDF_PATH = System.getProperty("user.dir")+"/PDFs";
        }
        options.addArguments("--log-level=OFF");
        options.addArguments("--safebrowsing-disable-download-protection");
        //options.addArguments("--headless");
        options.setExperimentalOption("prefs", preferences);
        System.setProperty("webdriver.chrome.silentOutput", "true");
        System.setProperty("Dcom.couchbase.client.core.deps.io.netty.noUnsafe", "true");
        //if(!showBrowser)
        //    options.addArguments("--headless");
        //java.util.logging.Logger.getLogger("org.openqa.selenium").setLevel(Level.OFF);
        if(isWindows){
            System.setProperty(System.getProperty("user.dir") + "\\chromedriver.exe", "true");
        }else{
            System.setProperty(System.getProperty("user.dir") + "\\chromedriver", "true");
        }

        directory = null;
        files = null;
        chosenFile = null;
    }

    public Lead[] getLeads() {
        return leads;
    }

    @Override
    public void run() {
        ScanThread s = new ScanThread();
        s.start();
    }

    private class ScanThread extends Thread
    {
        @Override
        public void run()
        {
            try {
                //createTrustManager();

                driver = new ChromeDriver(options);
                driver.get("https://proaccess.williamson-tn.org/proaccess/"); // Website link
                driver.findElement(By.xpath("//a[contains(text(),'Login to Professional Access')]")).click();


                /* Begin Login */
                WebElement login = driver.findElement(By.xpath("//input[@id='login-login']"));
                login.clear();
                login.sendKeys(username);
                WebElement passwordField = driver.findElement(By.xpath("//input[@id='login-password']"));
                passwordField.clear();
                passwordField.sendKeys(password);
                driver.findElement(By.xpath("//input[@value='Log In']")).click();
                /* End Login */

                if(!driver.findElement(By.xpath("//nav[@id='pa_navigation']/ul/li[3]/a")).isDisplayed()) throw new Exception("Login Failed");
                /* Begin Search */
                driver.findElement(By.xpath("//nav[@id='pa_navigation']/ul/li[3]/a")).click();
                driver.findElement(By.xpath("//a[contains(@href, 'index.php')]")).click();
                driver.get("https://proaccess.williamson-tn.org/proaccess/deed_search/insttype.php?cnum=24");
                Select instrumentType = new Select(driver.findElement(By.xpath("//select[@name='itype1']")));
                instrumentType.selectByVisibleText("TRUST DEED");
                driver.findElement(By.xpath("//input[@name='startdate']")).sendKeys(startDate);
                driver.findElement(By.xpath("//input[@name='enddate']")).sendKeys(endDate);
                Select mortgageRange = new Select(driver.findElement(By.xpath("//select[@name='mortOp']")));
                mortgageRange.selectByVisibleText("Greater than");
                driver.findElement(By.xpath("//input[@name='mortAmount']")).sendKeys("500000");
                Select transferRange = new Select(driver.findElement(By.xpath("//select[@name='transOp']")));
                transferRange.selectByVisibleText("Less than");
                driver.findElement(By.xpath("//input[@name='transAmount']")).sendKeys("2000000");
                driver.findElement(By.xpath("//input[@name='search']")).click();
                String results = (driver.findElement(By.xpath("//table[2]/tbody/tr/td")).getText());
                String result = (results.substring(results.indexOf("returned")+9, results.indexOf("hits")-1));
                limit = Integer.parseInt(result);
                leads = new Lead[limit];
                int tracker = 0;
                /* End Search */

                /* Begin Details*/
                driver.findElement(By.xpath("//input[@value='Details']")).click();
                for(int i = 0; i < limit; i++)
                {
                    String docType = driver.findElement(By.xpath("//tr[2]/td/font")).getText().substring(10);
                    String grantors = driver.findElement(By.xpath("//textarea")).getText(); // buyer(s)

                    if(docType.contains("TRUST DEED") && !grantors.contains("HOMES") && !grantors.contains("HOMEBUILDING") && !grantors.contains("LLC") && !grantors.contains("CORPORATION") && !grantors.contains("COMPANY"))
                    {
                        String date = "";
                        String grantees = "";
                        String subdivision = "";
                        String lot = "";
                        String section = "";
                        String legalDesc = "";
                        String mortgage = "";
                        String transfer = "";
                        File pdf = null;
                        String pdfLink = "";
                        try{date = driver.findElement(By.xpath("//td[2]/table/tbody/tr/td/font")).getText().substring(11); } catch(Exception e){}
                        try{grantees = driver.findElement(By.xpath("//td[2]/textarea")).getText();} catch(Exception e){}
                        try{
                            subdivision = driver.findElement(By.xpath("//table[3]/tbody/tr[2]/td/font")).getText();
                            if(subdivision.contains("Comment:"))
                                subdivision = "n/a";
                        } catch(Exception e){ subdivision = "n/a";}
                        try{lot = driver.findElement(By.xpath("//table[3]/tbody/tr[2]/td[2]")).getText();} catch(Exception e){}
                        try{section = driver.findElement(By.xpath("//table[3]/tbody/tr[2]/td[4]/font")).getText();} catch(Exception e){}
                        try{legalDesc = driver.findElement(By.xpath("//tr[4]/td/font[2]")).getText();} catch(Exception e){}
                        try{mortgage = driver.findElement(By.xpath("//tr[2]/td[3]/font")).getText().substring(7);} catch(Exception e){}
                        try{transfer = driver.findElement(By.xpath("//td[4]/font")).getText().substring(7);} catch(Exception e){}
                        try{
                            driver.findElement(By.xpath("//img[@onclick='showImgPop()']")).click();
                            driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
                            driver.findElement(By.xpath("//a[contains(text(),'PDF')]")).click();
                            Thread.sleep(30000);

                            directory = new File(PDF_PATH);

                            files = directory.listFiles(File::isFile);
                            if (files != null)
                            {
                                for (File file : files)
                                {
                                    if (file.lastModified() > lastModifiedTime)
                                    {
                                        chosenFile = file;
                                        lastModifiedTime = file.lastModified();
                                    }
                                }
                                if(chosenFile.getName().contains("crdownload"))
                                {
                                    Thread.sleep(30000);
                                    try{
                                        if(isWindows) {
                                            File newFile = new File(System.getProperty("user.dir") + "\\PDFs\\" + chosenFile.getName().replace(".crdownload", ""));
                                            chosenFile.renameTo(newFile);
                                        }else{
                                            File newFile = new File(System.getProperty("user.dir") + "/PDFs/" + chosenFile.getName().replace(".crdownload", ""));
                                            chosenFile.renameTo(newFile);
                                        }
                                    }catch (Exception e){
                                        if(isWindows){
                                            String newFilePath = chosenFile.getName().replace(".crdownload", "");
                                            chosenFile = new File(System.getProperty("user.dir")+"\\PDFs\\" + newFilePath);
                                        }else{
                                            String newFilePath = chosenFile.getName().replace(".crdownload", "");
                                            chosenFile = new File(System.getProperty("user.dir")+"/PDFs/" + newFilePath);
                                        }
                                    }
                                }
                                pdf = chosenFile;
                                reader = new com.highland.leads.services.PDFReader(pdf);
                                reader.start();
                            }

                        }catch (Exception ignored){
                            jobRequest.setStatus(JobRequest.Status.FAILED);
                            Database.updateJobRequest(jobRequest);
                        }

                        String address = "n/a";
                        String builder = "n/a";
                        String firstDate = "n/a";
                        String lastDate = "n/a";
                        String imageLink = "n/a";

                        leads[tracker] = new Lead(date, docType, grantors, grantees, subdivision, address, lot, section, legalDesc, mortgage, transfer, pdf, builder, firstDate, lastDate, imageLink);
                        tracker++;
                    }else{
                        numSkipped++;
                    }

                    driver.navigate().back();
                    driver.navigate().refresh();
                    Thread.sleep(1000);
                    location++;
                    try{
                        WebElement nextElement = driver.findElement(By.xpath("(//input[@value='Details'])["+location+"]"));
                        int elementPosition = nextElement.getLocation().getY();
                        String js = String.format("window.scroll(0, %s)", elementPosition);
                        ((JavascriptExecutor)driver).executeScript(js);
                        nextElement.click();
                    }catch (Exception e){
                        try{
                            driver.get("https://proaccess.williamson-tn.org/proaccess/menu/");
                            driver.findElement(By.xpath("//nav[@id='pa_navigation']/ul/li[8]/a")).click();
                            System.out.println("PDF Download Complete!");
                            System.out.println("Downloaded " + location + " / " + limit);
                            System.out.println("\n------------------------------------------\n");
                            System.out.println("Now downloading images...");
                            driver.close();
                            setImageLinks();
                        }catch (Exception e1){
                            WebScanner.jobRequest.setStatus(JobRequest.Status.FAILED);
                            Database.updateJobRequest(jobRequest);
                            driver.close();
                        }
                    }
                }
                /* End Details*/

            }catch(Exception e)
            {
                WebScanner.jobRequest.setStatus(JobRequest.Status.FAILED);
                Database.updateJobRequest(jobRequest);
                driver.close();
            }
        }

        void setImageLinks()
        {
            int finalRow = 0;
            try {
                while (!PDFReader.finished || PDFReader.processed.size() != 0){
                    Thread.sleep(60000);
                    if(PDFReader.processed.peek() != null)
                    {
                        if(PDFReader.processed.peek().getAddress().contains("n/a") || PDFReader.processed.peek().getAddress().contains("pdf"))
                        {
                            PDFReader.processed.remove();
                            finalRow++;
                        }
                        else if(PDFReader.processed.size()!=0)
                        {
                            try{
                                int orgSize = PDFReader.processed.size();
                                for (int i = 0; i < orgSize; i++){
                                    finalRow++;
                                    driver = new ChromeDriver(options);
                                    driver.get("https://www.google.com/maps/place/");
                                    driver.findElement(By.xpath("//input[@id='searchboxinput']")).sendKeys(PDFReader.processed.peek().getAddress());
                                    WebElement searchButton = driver.findElement(By.xpath("//button[@id='searchbox-searchbutton']"));
                                    searchButton.click();
                                    Thread.sleep(20000);
                                    Actions builder = new Actions(driver);
                                    builder.moveToElement(searchButton, 0, 200).click().perform();
                                    Thread.sleep(10000);
                                    String link = driver.getCurrentUrl();
                                    PDFReader.processed.peek().setImageLink(link);
                                    PDFReader.excel.updateImageLink(finalRow, link);
                                    driver.close();
                                    PDFReader.processed.remove();
                                }
                            }catch (Exception ignored){ driver.close(); PDFReader.processed.remove(); }
                        }
                    }
                }


            } catch (InterruptedException ex) {
                ex.printStackTrace();
                jobRequest.setStatus(JobRequest.Status.FAILED);
                Database.updateJobRequest(jobRequest);
                driver.close();
            }
            //driver.close();
        }

        private void createTrustManager()
        {
            // Create a new trust manager that trust all certificates
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return null; // Not relevant.
                        }
                        @Override
                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
                            // Do nothing. Just allow them all.
                        }
                        @Override
                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
                            // Do nothing. Just allow them all.
                        }
                    }
            };

            // Activate the new trust manager
            try {
                SSLContext sc = SSLContext.getInstance("SSL");
                sc.init(null, trustAllCerts, new java.security.SecureRandom());
                HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            } catch (GeneralSecurityException e) {
                throw new ExceptionInInitializerError(e);
            }
        }
    }
}


