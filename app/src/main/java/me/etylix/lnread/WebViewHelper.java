package me.etylix.lnread;

import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

public class WebViewHelper {

    public static void openInWebView(FragmentActivity activity, FragmentManager fragmentManager, String url, int containerId) {
        WebViewFragment webViewFragment = new WebViewFragment();
        Bundle args = new Bundle();
        args.putString("url", url);
        webViewFragment.setArguments(args);
        fragmentManager.beginTransaction()
                .replace(containerId, webViewFragment)
                .addToBackStack(null)
                .commit();
    }
}