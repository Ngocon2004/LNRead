package me.etylix.lnread;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Patterns;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.content.ContextCompat;

public class CustomTabHelper {

    // Validate URL
    public static boolean isValidUrl(String url) {
        if (url == null || url.isEmpty()) {
            return false;
        }
        return Patterns.WEB_URL.matcher(url).matches();
    }

    // Launch URL in Custom Tab
    public static void launchCustomTab(Context context, String url) {
        if (!isValidUrl(url)) {
            return; // Validation should be handled by the caller
        }

        // Build CustomTabsIntent with customizations
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();

        // Enable URL bar hiding (optional)
        builder.setUrlBarHidingEnabled(true);

        // Build the CustomTabsIntent
        CustomTabsIntent customTabsIntent = builder.build();

        // Launch the URL
        try {
            customTabsIntent.launchUrl(context, Uri.parse(url));
        } catch (Exception e) {
            // Fallback to default browser if Custom Tabs fail
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(intent);
            }
        }
    }
}