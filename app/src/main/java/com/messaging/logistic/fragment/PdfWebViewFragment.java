package com.messaging.logistic.fragment;

import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.constants.Constants;
/*import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;*/
import com.messaging.logistic.Globally;
import com.messaging.logistic.R;
//import com.shockwave.pdfium.PdfDocument;

import java.util.List;

public class PdfWebViewFragment extends Fragment { //implements OnPageChangeListener, OnLoadCompleteListener,
    //OnPageErrorListener , OnErrorListener {

    View rootView;
    // PDFView pdfView;
    Constants constants;
    int pageNumber = 0;
    private static final String TAG = "PDF Viewer";
    String filePath;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (rootView != null) {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null)
                parent.removeView(rootView);
        }
        try {
            rootView = inflater.inflate(R.layout.fragment_dot_webview, container, false);
            rootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        } catch (InflateException e) {
            e.printStackTrace();
        }


        //    initView(rootView);

        return rootView;
    }


 /*   void initView(View v) {
        constants = new Constants();
        ScrollView dotScrollView = (ScrollView)v.findViewById(R.id.dotScrollView);
        pdfView    = (PDFView)v.findViewById(R.id.pdfView);

        RelativeLayout eldMenuLay = (RelativeLayout)v.findViewById(R.id.eldMenuLay);
        LinearLayout rightMenuBtn = (LinearLayout)v.findViewById(R.id.rightMenuBtn);
        TextView EldTitleTV = (TextView)v.findViewById(R.id.EldTitleTV);
        ImageView eldMenuBtn = (ImageView)v.findViewById(R.id.eldMenuBtn);

        dotScrollView.setVisibility(View.GONE);
        rightMenuBtn.setVisibility(View.GONE);
        eldMenuBtn.setImageResource(R.drawable.back_btn);

        Globally.getBundle  = this.getArguments();
        filePath = Globally.getBundle.getString("filePath");
        Uri file  = Uri.parse(Globally.getBundle.getString("file"));
        String filename = Globally.getBundle.getString("filename");

        // /storage/emulated/0/Logistic/AlsDoc/1_ELD Doc.pdf
        EldTitleTV.setText(filename);

        pdfView.setVisibility(View.VISIBLE);
        pdfView.fromUri(file)
                .defaultPage(pageNumber)
                .onPageChange(this)
                .enableAnnotationRendering(true)
                .onLoad(this)
                .scrollHandle(new DefaultScrollHandle(getActivity()))
                .spacing(10) // in dp
                .onPageError(this)
                .onPageErrorCallBack(this)
                .load();





        eldMenuLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });

    *//*    pdfView.setVisibility(View.GONE);
        WebView mWebView = (WebView)v.findViewById(R.id.dotWebView);
        mWebView.setVisibility(View.VISIBLE);
        mWebView.setFocusable(true);
        mWebView.setFocusableInTouchMode(true);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setDatabaseEnabled(true);
        mWebView.getSettings().setAppCacheEnabled(true);
        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        mWebView.setWebViewClient(new WebViewClient());

        String googleDocsURL = "https://docs.google.com/gview?embedded=true&url=";

        String html = "<html><head></head><body> <embed src=" +filePath + "> </body></html>";
        //mWebView.loadUrl("file://" + html);
        mWebView.loadUrl( googleDocsURL + "file://" + filePath);
       *//*



    }

*/
/*


    public void printBookmarksTree(List<PdfDocument.Bookmark> tree, String sep) {
        for (PdfDocument.Bookmark b : tree) {

            Log.e(TAG, String.format("%s %s, p %d", sep, b.getTitle(), b.getPageIdx()));

            if (b.hasChildren()) {
                printBookmarksTree(b.getChildren(), sep + "-");
            }
        }
    }


    @Override
    public void loadComplete(int nbPages) {
        PdfDocument.Meta meta = pdfView.getDocumentMeta();
        Log.e(TAG, "title = " + meta.getTitle());
        Log.e(TAG, "author = " + meta.getAuthor());
        Log.e(TAG, "subject = " + meta.getSubject());
        Log.e(TAG, "keywords = " + meta.getKeywords());
        Log.e(TAG, "creator = " + meta.getCreator());
        Log.e(TAG, "producer = " + meta.getProducer());
        Log.e(TAG, "creationDate = " + meta.getCreationDate());
        Log.e(TAG, "modDate = " + meta.getModDate());

        printBookmarksTree(pdfView.getTableOfContents(), "-");
    }

    @Override
    public void onPageChanged(int page, int pageCount) {
        pageNumber = page;
      //  setTitle(String.format("%s %s / %s", pdfFileName, page + 1, pageCount));
    }

    @Override
    public void onPageError(int page, Throwable t) {
        Log.e(TAG, "Cannot load page " + page);
        constants.DeleteFile(filePath);
        Globally.EldScreenToast(pdfView, getResources().getString(R.string.document_corrupted), getContext().getResources().getColor(R.color.colorVoilation));
    }


    @Override
    public void onError(Throwable t) {
        Log.e(TAG, "onError Cannot load page " +t);
        constants.DeleteFile(filePath);
        Globally.EldScreenToast(pdfView, getResources().getString(R.string.document_corrupted), getContext().getResources().getColor(R.color.colorVoilation));

        */
/*if (Globally.isConnected(getActivity())) {

        }*//*

    }

*/

}
