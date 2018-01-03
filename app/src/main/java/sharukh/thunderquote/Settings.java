package sharukh.thunderquote;

import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.v7.app.NotificationCompat;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class Settings extends PreferenceActivity implements Preference.OnPreferenceClickListener
{
    protected Preference downloadQuotes, about;
    protected TinyDB tinyDB;
    protected Context context;

    protected NotificationManager notificationManager;
    protected NotificationCompat.Builder downloadProgress;
    protected ArrayList<Quote> quotesList = new ArrayList<>();

    public static void openAppRating(Context context)
    {
        Intent rateIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + context.getPackageName()));
        boolean marketFound = false;

        // find all applications able to handle our rateIntent
        final List<ResolveInfo> otherApps = context.getPackageManager().queryIntentActivities(rateIntent, 0);
        for (ResolveInfo otherApp : otherApps)
        {
            // look for Google Play application
            if (otherApp.activityInfo.applicationInfo.packageName.equals("com.android.vending"))
            {

                ActivityInfo otherAppActivity = otherApp.activityInfo;
                ComponentName componentName = new ComponentName(
                        otherAppActivity.applicationInfo.packageName,
                        otherAppActivity.name
                );
                rateIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                rateIntent.setComponent(componentName);
                context.startActivity(rateIntent);
                marketFound = true;
                break;

            }
        }

        // if GP not present on device, open web browser
        if (!marketFound)
        {
            Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + context.getPackageName()));
            context.startActivity(webIntent);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        context = getApplicationContext();
        tinyDB = new TinyDB(context);
        quotesList = tinyDB.getListObject("quotesList", Quote.class);

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        downloadQuotes = findPreference("downloadQuotes");
        about = findPreference("about");
        about.setOnPreferenceClickListener(this);

    }

    @Override
    protected void onResume()
    {
        downloadQuotes.setTitle("Downloaded Quotes for Offline: " + quotesList.size());
        downloadQuotes.setSummary("New quotes will be downloaded periodically");
        super.onResume();
    }

    @Override
    public boolean onPreferenceClick(Preference preference)
    {
        switch (preference.getKey())
        {
            case "downloadQuotes":
            {
                Toast.makeText(Settings.this, "New Quotes will be ", Toast.LENGTH_SHORT).show();
                return true;
            }

            case "about":
            {
                openAppRating(context);
                Toast.makeText(context, "Rate Thunder Quote 5 Stars and Developer receives a free hug!", Toast.LENGTH_LONG).show();
                return true;
            }
        }
        return false;
    }

}
