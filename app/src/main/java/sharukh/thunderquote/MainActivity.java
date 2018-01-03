package sharukh.thunderquote;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.MalformedJsonException;
import com.nineoldandroids.animation.Animator;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import info.hoang8f.widget.FButton;
import is.arontibo.library.ElasticDownloadView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener
{

    public Context context;
    public TinyDB tinyDB;
    public NotificationManager notificationManager;
    public NotificationCompat.Builder downloadProgress;

    public DownloadQuotesFirstTime downloadQuotesForFirstUse;
    public DownloadQuotesSilent downloadQuotesSilent;
    public ArrayList<Quote> quotesList = new ArrayList<>();
    public Quote quote;

    public RelativeLayout layout;
    public Typeface quoteFont, authorFont;
    public TextView quoteTextView, authorTextView;
    public FButton moreButton, shareButton, copyButton;
    public ElasticDownloadView progress;

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

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();
        tinyDB = new TinyDB(context);

        downloadQuotesForFirstUse = new DownloadQuotesFirstTime();
        downloadQuotesSilent = new DownloadQuotesSilent();

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        downloadProgress = new NotificationCompat.Builder(context);

        layout = (RelativeLayout) findViewById(R.id.layout);
        quoteTextView = (TextView) findViewById(R.id.quoteTextView);
        authorTextView = (TextView) findViewById(R.id.authorTextView);
        moreButton = (FButton) findViewById(R.id.moreButton);
        shareButton = (FButton) findViewById(R.id.shareButton);
        copyButton = (FButton) findViewById(R.id.copyButton);
        progress = (ElasticDownloadView) findViewById(R.id.elastic_download_view);

        quoteFont = Typeface.createFromAsset(getAssets(), "fonts/NotoSerif.ttf");
        authorFont = Typeface.createFromAsset(getAssets(), "fonts/Tangerine.ttf");
        quoteTextView.setTypeface(quoteFont);
        authorTextView.setTypeface(authorFont);

        try
        {
            progress.setVisibility(View.GONE);

            moreButton.setOnClickListener(this);
            copyButton.setOnClickListener(this);
            shareButton.setOnClickListener(this);

            quotesList = tinyDB.getListObject("quotesList", Quote.class);

            downloadProgress.setContentTitle("Downloading Quotes for offline");
            downloadProgress.setContentText("Most Inspiring");
            downloadProgress.setColor(ContextCompat.getColor(context, R.color.colorPrimary));
            downloadProgress.setSmallIcon(R.drawable.ic_stat_communication_import_export);
            if (quotesList.size() < 50)
                downloadQuotesForFirstUse();
            else
            {
                if (getIntent().getAction().equals("NOTIFICATION_SHARE"))
                {
                    quoteTextView.setText(getIntent().getStringExtra("quoteText"));
                    authorTextView.setText(getIntent().getStringExtra("quoteAuthor"));
                    Log.i("Show the Stuff", getIntent().getStringExtra("quoteText") + getIntent().getStringExtra("quoteAuthor"));
                }
                else
                {
                    quoteTextView.setText("");
                    authorTextView.setText("");
                    quote = quotesList.get(new Random().nextInt(quotesList.size()));
                    getAndSetRandomQuote();
                    YoYo.with(Techniques.FadeInUp).duration(750).playOn(moreButton);
                    YoYo.with(Techniques.FadeInRight).duration(1000).playOn(copyButton);
                    YoYo.with(Techniques.FadeInLeft).duration(1000).playOn(shareButton);
                    downloadQuotesSilent.execute();
                }

            }
        } catch (NullPointerException | IllegalStateException e)
        {
            e.printStackTrace();
        }
    }

    public void downloadQuotesForFirstUse()
    {
        authorTextView.setVisibility(View.GONE);
        moreButton.setVisibility(View.GONE);
        shareButton.setVisibility(View.GONE);
        copyButton.setVisibility(View.GONE);

        layout.setBackgroundColor(ContextCompat.getColor(context, R.color.orange_salmon));
        quoteTextView.setTextColor(ContextCompat.getColor(context, R.color.icons));
        quoteTextView.setText(R.string.downloading_for_first_use);

        progress.setVisibility(View.VISIBLE);
        progress.startIntro();
        downloadQuotesForFirstUse.execute();
    }

    @Override
    public void onClick(View v)
    {
        try
        {
            switch (v.getId())
            {
                case R.id.moreButton:
                {
                    getAndSetRandomQuote();
                    break;
                }
                case R.id.copyButton:
                {
                    copyToClipboard(quote.getPrettyQuote() + "\n" + context.getString(R.string.sent_Via));
                    break;
                }
                case R.id.shareButton:
                {
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, quote.getPrettyQuote() + "\n" + context.getString(R.string.sent_Via));
                    sendIntent.setType("text/plain");
                    startActivity(Intent.createChooser(sendIntent, "Share this Awesome Quote to..."));
                    break;
                }
            }
        } catch (IllegalStateException e)
        {
            e.printStackTrace();
        }

    }

    public void getAndSetRandomQuote()
    {
        try
        {

            quote = quotesList.get(new Random().nextInt(quotesList.size()));
            YoYo.with(Techniques.FadeOutDown)
                    .duration(500)
                    .withListener(new Animator.AnimatorListener()
                    {
                        @Override
                        public void onAnimationStart(Animator animation)
                        {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation)
                        {
                            quoteTextView.setText(quote
                                    .getQuoteTextWithDoubleQuotes());
                            YoYo.with(Techniques.FadeInDown).duration(500).playOn(quoteTextView);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation)
                        {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation)
                        {
                            animation.end();
                        }
                    })
                    .playOn(quoteTextView);


            YoYo.with(Techniques.FadeOutDown)
                    .duration(500)
                    .withListener(new Animator.AnimatorListener()
                    {
                        @Override
                        public void onAnimationStart(Animator animation)
                        {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation)
                        {
                            authorTextView.setText(quote.quoteAuthor);
                            YoYo.with(Techniques.FadeInDown).duration(1000).playOn(authorTextView);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation)
                        {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation)
                        {
                            animation.end();
                        }
                    })
                    .playOn(authorTextView);

        } catch (IllegalArgumentException e)
        {
            quotesList = tinyDB.getListObject("quotesList", Quote.class);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.main_menu_settings:
            {
                startActivity(new Intent(this, Settings.class).addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT));
                return true;
            }

            case R.id.main_menu_about:
            {
                YoYo.with(Techniques.FadeOut).duration(750).withListener(new Animator.AnimatorListener()
                {
                    @Override
                    public void onAnimationStart(Animator animation)
                    {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation)
                    {
                        quoteTextView.setText("Most Inspiring Expressions Of Mankind.\nMade with Love by");
                        YoYo.with(Techniques.ZoomIn).duration(500).playOn(quoteTextView);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation)
                    {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation)
                    {

                    }
                }).playOn(quoteTextView);
                YoYo.with(Techniques.FadeOut).duration(500).withListener(new Animator.AnimatorListener()
                {
                    @Override
                    public void onAnimationStart(Animator animation)
                    {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation)
                    {
                        authorTextView.setText(R.string.Developer);
                        YoYo.with(Techniques.ZoomIn).duration(500).playOn(authorTextView);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation)
                    {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation)
                    {

                    }
                }).playOn(authorTextView);

                //noinspection ConstantConditions
                Snackbar.make(findViewById(android.R.id.content), "Free hug the developer?", Snackbar.LENGTH_LONG).setAction("SURE!", new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        openAppRating(context);
                        Toast.makeText(context, "Rate Thunder Quote 5 Stars and Developer receives a free hug!", Toast.LENGTH_LONG).show();
                    }
                }).show();
                return true;
            }

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onBackPressed()
    {
        if (downloadQuotesForFirstUse.getStatus() == AsyncTask.Status.RUNNING)
        {
            Intent gotoHome = new Intent(Intent.ACTION_MAIN);
            gotoHome.addCategory(Intent.CATEGORY_HOME);
            gotoHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(gotoHome);
            Toast.makeText(MainActivity.this, "If download stops, close app from recents and reopen", Toast.LENGTH_LONG).show();
        }
        else if (downloadQuotesSilent.getStatus() == AsyncTask.Status.RUNNING)
        {
            Intent gotoHome = new Intent(Intent.ACTION_MAIN);
            gotoHome.addCategory(Intent.CATEGORY_HOME);
            gotoHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(gotoHome);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
        else
            super.onBackPressed();
    }

    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    public void copyToClipboard(String copyText)
    {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB)
        {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(copyText);
        }
        else
        {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Thunder Quote", copyText);
            clipboard.setPrimaryClip(clip);
        }
        Toast.makeText(context, "Quote copied", Toast.LENGTH_LONG).show();
    }

    class DownloadQuotesFirstTime extends AsyncTask<Void, Integer, String>
    {
        URL url;
        URLConnection urlConnection;
        HttpURLConnection httpURLConnection;
        TinyDB tinyDB;

        private String getQuoteFromStream(InputStream is) throws IOException
        {
            BufferedReader in = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = in.readLine()) != null)
            {
                stringBuilder.append(line);
            }
            return stringBuilder.toString();
        }

        @Override
        protected String doInBackground(Void... params)
        {
            try
            {
                url = new URL("http://api.forismatic.com/api/1.0/?method=getQuote&format=json&lang=en");
                tinyDB = new TinyDB(context);

                while (quotesList.size() < 50)
                {
                    urlConnection = url.openConnection();
                    httpURLConnection = (HttpURLConnection) urlConnection;
                    httpURLConnection.connect();

                    if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK)
                    {
                        try
                        {
                            Quote quote = new Gson().fromJson(getQuoteFromStream(new BufferedInputStream(httpURLConnection.getInputStream())), Quote.class);
                            if (!quotesList.contains(quote))
                            {
                                quotesList.add(quote);
                                Log.d(String.valueOf(quotesList.size()), quote.quoteText);
                                publishProgress(quotesList.size());
                            }
                        } catch (JsonSyntaxException | MalformedJsonException e)
                        {
                            Log.e("HTTP", "Received Wrong JSON");
                        }

                    }
                    else
                        Log.e("HTTP", httpURLConnection.getResponseMessage());

                }
                return "Complete";

            } catch (Exception e)
            {
                e.printStackTrace();
            }
            return "Failed to Connect";
        }

        @Override
        protected void onProgressUpdate(final Integer... values)
        {
            downloadProgress.setProgress(50, values[0], false);
            downloadProgress.setContentText("Downloaded Quotes: " + values[0] + "/50");
            notificationManager.notify(23, downloadProgress.build());

            progress.setProgress(values[0] * 2);

            try
            {
                if (values[0] % 5 == 0)
                {
                    if (values[0] % 10 != 0)
                    {
                        final int value = values[0];
                        YoYo.with(Techniques.FadeOutDown)
                                .withListener(new Animator.AnimatorListener()
                                {

                                    @Override
                                    public void onAnimationStart(Animator animation)
                                    {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animation)
                                    {
                                        quoteTextView.setText(quotesList.get(value - 1).quoteText.length() < 100 ? quotesList.get(value - 1).quoteText : "Patience is the Key");
                                        YoYo.with(Techniques.FadeInDown)
                                                .duration(500)
                                                .playOn(quoteTextView);
                                    }

                                    @Override
                                    public void onAnimationCancel(Animator animation)
                                    {

                                    }

                                    @Override
                                    public void onAnimationRepeat(Animator animation)
                                    {

                                    }
                                })
                                .duration(500)
                                .playOn(quoteTextView);

                    }
                    else
                    {
                        YoYo.with(Techniques.FadeOutUp).withListener(new Animator.AnimatorListener()
                        {

                            @Override
                            public void onAnimationStart(Animator animation)
                            {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation)
                            {
                                if (values[0] > 39)
                                    quoteTextView.setText(R.string.almost_there_hold_on);
                                else
                                    quoteTextView.setText(R.string.downloading_for_first_use);
                                YoYo.with(Techniques.FadeInUp).duration(500).playOn(quoteTextView);
                            }

                            @Override
                            public void onAnimationCancel(Animator animation)
                            {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation)
                            {

                            }
                        }).duration(500).playOn(quoteTextView);

                    }


                }
            } catch (NullPointerException e)
            {
                e.printStackTrace();
            }
            tinyDB.putListObject("quotesList", quotesList);
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPreExecute()
        {
            try
            {
                context = getApplicationContext();
                progress.setVisibility(View.VISIBLE);

                downloadProgress.setColor(ContextCompat.getColor(context, R.color.colorAccentDark));
                downloadProgress.setContentText("Internet Unavailable");
                downloadProgress.setContentIntent(PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT), PendingIntent.FLAG_UPDATE_CURRENT));
                downloadProgress.setProgress(0, 0, true);
                notificationManager.notify(23, downloadProgress.build());
                Log.e("Pre-Download", "Quotes:" + quotesList.size());

            } catch (NullPointerException e)
            {
                e.printStackTrace();
            } finally
            {
                super.onPreExecute();
            }
        }

        @Override
        protected void onCancelled()
        {
            super.onCancelled();
            Toast.makeText(context, "Downloading Quotes Stopped.\nReopen Thunder Quote to Resume", Toast.LENGTH_LONG).show();
            Log.e("HTTP", "Cancelled");
        }

        @Override
        protected void onPostExecute(String string)
        {
            if (!string.equals("Failed to Connect"))
            {
                try
                {
                    tinyDB.putListObject("quotesList", quotesList);
                    downloadProgress.setProgress(2, 2, false);
                    downloadProgress.setColor(ContextCompat.getColor(context, R.color.fbutton_color_nephritis));
                    downloadProgress.setContentTitle("Downloaded Quotes: " + quotesList.size());
                    downloadProgress.setContentText("Click to open Thunder Quote");
                    downloadProgress.setContentIntent(PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class)
                            .setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT), PendingIntent.FLAG_UPDATE_CURRENT));
                    notificationManager.notify(23, downloadProgress.build());

                    progress.success();
                    layout.setBackgroundColor(ContextCompat.getColor(context, R.color.fbutton_color_clouds));
                    quoteTextView.setTextColor(ContextCompat.getColor(context, R.color.colorAccentDark));
                    YoYo.with(Techniques.FadeOutDown).duration(750).playOn(progress);

                    authorTextView.setVisibility(View.VISIBLE);
                    moreButton.setVisibility(View.VISIBLE);
                    shareButton.setVisibility(View.VISIBLE);
                    copyButton.setVisibility(View.VISIBLE);

                    YoYo.with(Techniques.BounceInUp).duration(750).playOn(moreButton);
                    YoYo.with(Techniques.FadeInRight).duration(1000).playOn(copyButton);
                    YoYo.with(Techniques.FadeInLeft).duration(1000).playOn(shareButton);
                    getAndSetRandomQuote();
                } catch (NullPointerException e)
                {
                    e.printStackTrace();
                } finally
                {
                    Log.e("Post-Download", "Quotes:" + quotesList.size());
                    super.onPostExecute(string);
                }
            }

        }
    }

    class DownloadQuotesSilent extends AsyncTask<Void, Void, Void>
    {
        URL url;
        URLConnection urlConnection;
        HttpURLConnection httpURLConnection;
        TinyDB tinyDB;

        private String getQuoteFromStream(InputStream is) throws IOException
        {
            BufferedReader in = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = in.readLine()) != null)
            {
                stringBuilder.append(line);
            }
            return stringBuilder.toString();
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            try
            {
                url = new URL("http://api.forismatic.com/api/1.0/?method=getQuote&format=json&lang=en");
                tinyDB = new TinyDB(context);
                for (int i = 0; i <= 500; i++)
                {
                    urlConnection = url.openConnection();
                    httpURLConnection = (HttpURLConnection) urlConnection;
                    httpURLConnection.connect();

                    if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK)
                    {
                        try
                        {
                            Quote quote = new Gson().fromJson(getQuoteFromStream(new BufferedInputStream(httpURLConnection
                                    .getInputStream())), Quote.class);
                            if (!quotesList.contains(quote))
                            {
                                quotesList.add(quote);
                                tinyDB.putListObject("quotesList", quotesList);
                                Log.d(String.valueOf(quotesList.size() + "\\") + i, quote.quoteText);
                            }
                        } catch (JsonSyntaxException | MalformedJsonException e)
                        {
                            Log.e("HTTP", "Received Wrong JSON");
                        }

                    }
                    else
                        Log.e("HTTP", httpURLConnection.getResponseMessage());
                }

            } catch (Exception e)
            {
                e.printStackTrace();
            }
            return null;
        }
    }
}
