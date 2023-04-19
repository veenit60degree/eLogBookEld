package com.constants;

import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.SocketTimeoutException;

public class CheckIsUpdateReady extends AsyncTask<Void, String, String> {
    String appURL="";
    private UrlResponce mUrlResponce;

    public CheckIsUpdateReady(String appURL, UrlResponce callback) {
        this.appURL=appURL;
        mUrlResponce = callback;
    }

    @Override

    protected String doInBackground(Void... voids) {

        String newVersion = null;

        try {
           /* if(appURL == null || appURL.isEmpty()){
                appURL = "https://play.google.com/store/apps/details?id=com.als.logistic&hl=en";
            }*/

            Document document = Jsoup.connect(appURL)
                    .timeout(10000)
                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                    .referrer("http://www.google.com")
                    .get();
            if (document != null) {
                Elements element = document.getElementsContainingOwnText("Current Version");
                if(element != null) {
                    for (Element ele : element) {
                        if (ele.siblingElements() != null) {
                            Elements sibElemets = ele.siblingElements();
                            for (Element sibElemet : sibElemets) {
                                newVersion = sibElemet.text();
                            }
                        }
                    }
                }
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
        }catch (SocketTimeoutException e){
            e.printStackTrace();
        }catch (Exception e) {
            e.printStackTrace();
            Logger.LogDebug("CheckIsUpdateReady", "----UnknownHostException");
        }
        return newVersion;
    }

    @Override
    protected void onPostExecute(String onlineVersion) {
        super.onPostExecute(onlineVersion);
        if (onlineVersion != null && !onlineVersion.isEmpty()) {
            mUrlResponce.onReceived(onlineVersion);
        }

        Logger.LogDebug("update", "-----playstore App version " + onlineVersion);

    }
}
