package sharukh.thunderquote;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.Random;

public class BroadCastReceiver extends BroadcastReceiver
{
    public NotificationManager notificationManager;
    public TinyDB tinyDB;

    public BroadCastReceiver()
    {

    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        tinyDB = new TinyDB(context);

        switch (intent.getAction())
        {
            case "NOTIFICATION_SHARE":
            {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, intent.getStringExtra("shareableQuote"));
                sendIntent.setType("text/plain");
                context.startActivity(Intent.createChooser(sendIntent, "Share Quote to").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                break;
            }

            default:
            {
                if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("sendNotifications", true))
                {
                    switch (PreferenceManager.getDefaultSharedPreferences(context).getString("notificationFrequency", "Regular Frequency"))
                    {

                        case "High Frequency":
                        {
                            pushRandomQuote(context);
                            break;
                        }
                        case "Regular Frequency":
                        {
                            String action = intent.getAction();
                            if (action.equals("android.intent.action.BATTERY_OKAY") || action.equals("android.intent.action.HEADSET_PLUG") || action.equals("android.intent.action.INPUT_METHOD_CHANGED") || action.equals("android.intent.action.WALLPAPER_CHANGED") || action.equals("android.intent.action.ACTION_POWER_DISCONNECTED") || action.equals("android.intent.action.CLOSE_SYSTEM_DIALOGS"))
                                pushRandomQuote(context);
                            break;
                        }
                        case "Low Frequency":
                        {
                            String action = intent.getAction();
                            if (action.equals("android.intent.action.BATTERY_OKAY") || action.equals("android.intent.action.HEADSET_PLUG") || action.equals("android.intent.action.INPUT_METHOD_CHANGED") || action.equals("android.intent.action.WALLPAPER_CHANGED"))
                                pushRandomQuote(context);
                            break;
                        }

                    }
                }
            }
        }
    }

    public void pushRandomQuote(Context context)
    {
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        ArrayList<Quote> quotesList = tinyDB.getListObject("quotesList", Quote.class);
        int randomQuote = new Random().nextInt(quotesList.size());

        notificationManager.notify(197, new android.support.v7.app.NotificationCompat.Builder(context)
                .setColor(ContextCompat.getColor(context, R.color.colorAccentDark))
                .setNumber(randomQuote)
                .setStyle(new android.support.v7.app.NotificationCompat.BigTextStyle().bigText(quotesList.get(randomQuote).quoteText)
                        .setBigContentTitle(quotesList.get(randomQuote).quoteAuthor)
                        .setSummaryText("Inspiration from Thunder Quote"))
                .setContentText(quotesList.get(randomQuote).quoteText)
                .setContentTitle(quotesList.get(randomQuote).quoteAuthor.equals("") ? "Thunder Quote" : quotesList.get(randomQuote).quoteAuthor + " - Thunder Quote")
                .setSmallIcon(R.drawable.ic_stat_editor_format_quote)
                .setAutoCancel(true)
                .setPriority(Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(context).getString("notificationPriority", "0")))
                .setContentIntent(PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class)
                        .setAction("NOTIFICATION_SHARE")
                        .putExtra("quoteText", quotesList.get(randomQuote).getQuoteTextWithDoubleQuotes())
                        .putExtra("quoteAuthor", quotesList.get(randomQuote).quoteAuthor), PendingIntent.FLAG_ONE_SHOT))
                .addAction(R.drawable.ic_stat_social_share, "SHARE", PendingIntent.getBroadcast(context, 0, new Intent("NOTIFICATION_SHARE")
                        .putExtra("shareableQuote", quotesList.get(randomQuote).getPrettyQuote() +"\n"+ context.getString(R.string.sent_Via)), PendingIntent.FLAG_ONE_SHOT))
                .build());
    }
}

