package sharukh.thunderquote.modules.main

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.palette.graphics.Palette
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_quote.*
import sharukh.thunderquote.R
import sharukh.thunderquote.base.BaseActivity
import sharukh.thunderquote.data.Quote
import sharukh.thunderquote.modules.quoteList.QuoteListActivity
import java.io.File
import java.io.FileOutputStream


class QuoteActivity : BaseActivity(), QuotePresenter.View {

    private val presenter = QuotePresenter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quote)

        val w = window // in Activity's onCreate() for instance
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)


        presenter.getQuote()

        quote_next.setOnClickListener { presenter.getQuote() }
        quote_history.setOnClickListener { startActivity(Intent(this, QuoteListActivity::class.java)) }

    }


    override fun onDestroy() {
        super.onDestroy()
        presenter.dispose()
    }

    override fun showQuote(quote: Quote) {
        quote_text.text = quote.text
        quote_author.text = quote.author

        //val imageDimension = resources.getDimension(R.dimen.image_size).toInt()

        Picasso.get()
                .load(quote.backgroundUrl)
                .into(quote_background, object : Callback {
                    override fun onSuccess() {
                        Palette
                                .from((quote_background.drawable as BitmapDrawable).bitmap)
                                .generate {
                                    it?.let { palette ->

                                        val swatch = with(palette) {
                                            return@with dominantSwatch
                                                    ?: vibrantSwatch
                                                    ?: lightVibrantSwatch
                                                    ?: darkMutedSwatch
                                                    ?: darkVibrantSwatch
                                                    ?: mutedSwatch

                                        }

                                        val swatch2 = with(palette) {
                                            return@with darkMutedSwatch
                                                    ?: darkVibrantSwatch
                                                    ?: mutedSwatch
                                                    ?: lightMutedSwatch
                                                    ?: lightVibrantSwatch
                                                    ?: vibrantSwatch
                                        }

                                        swatch?.let {
                                            quote_start.setColorFilter(swatch.titleTextColor, PorterDuff.Mode.SRC_IN)
                                            quote_end.setColorFilter(swatch.titleTextColor, PorterDuff.Mode.SRC_IN)

                                            quote_box_background.setBackgroundColor(swatch.rgb)
                                            quote_box_background.alpha = (0.75F)

                                            quote_text.setTextColor(swatch.bodyTextColor)
                                            quote_author.setTextColor(swatch.bodyTextColor)

                                            quote_info_text.setTextColor(swatch.bodyTextColor)
                                            quote_info_icon.setColorFilter(swatch.titleTextColor, PorterDuff.Mode.SRC_IN)
                                            quote_share_text.setTextColor(swatch.bodyTextColor)
                                            quote_share_icon.setColorFilter(swatch.titleTextColor, PorterDuff.Mode.SRC_IN)

                                            quote_like.setColorFilter(swatch.rgb)
                                            quote_like.setBackgroundColor(swatch.titleTextColor)

                                            quote_next.setColorFilter(swatch.rgb)
                                            quote_next.setBackgroundColor(swatch.titleTextColor)

                                            quote_history.setColorFilter(swatch.rgb)
                                            quote_history.setBackgroundColor(swatch.titleTextColor)

                                            quote_thunder_logo.setColorFilter(swatch.rgb, PorterDuff.Mode.SRC_IN)

                                            quote_cta_layout.setBackgroundColor(swatch.rgb)

                                        } ?: Log.e("Palette", "No Vibrant Switch")

                                        swatch2?.let {
                                            //quote_author.setTextColor(swatch2.titleTextColor)
                                            quote_thunder_logo.setColorFilter(swatch2.bodyTextColor, PorterDuff.Mode.SRC_IN)
                                            quote_thunder_text.setTextColor(swatch2.titleTextColor)
                                        }
                                    } ?: Log.e("Palette", "No Palette")
                                }
                    }

                    override fun onError(e: Exception?) {

                    }
                })


        quote_like.setImageDrawable(getDrawable(if (quote.isFavorite) R.drawable.ic_fav else R.drawable.ic_no_fav))

        quote_like.setOnClickListener { toggleLike(quote.id) }
        quote_info.setOnClickListener { showAttribution(quote) }
        quote_share.setOnClickListener { shareQuote(quote_paint_layout) }


    }

    override fun showProgress() {
        quote_progress.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        quote_progress.visibility = View.INVISIBLE
    }

    override fun showEmptyView() {
        //Do nothing
    }

    override fun toggleLike(liked: Boolean) {
        quote_like.setImageDrawable(getDrawable(if (liked) R.drawable.ic_fav else R.drawable.ic_no_fav))
    }

    override fun showError(error: String) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
    }

    private fun toggleLike(id: Long) {
        presenter.toggleLike(id)
    }

    private fun showAttribution(quote: Quote) {
        AlertDialog.Builder(this).apply {
            setIcon(R.drawable.ic_info)
            setTitle("Background from Unsplash")
            setMessage("Photo by ${quote.userFullName} from Unsplash")
            setPositiveButton("Check out ${quote.userFullName}") { t, v ->
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(quote.userLinkUrl)))
                t.dismiss()
            }
            setNeutralButton("Visit Unsplash") { t, v ->
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(quote.unsplashLinkUrl)))
                t.dismiss()
            }
        }.show()
    }

    private fun shareQuote(view: View) {
        val finalBitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(finalBitmap)
        val bgDrawable = view.background

        if (bgDrawable != null)
            bgDrawable.draw(canvas)
        else
            canvas.drawColor(Color.WHITE)

        view.draw(canvas)


        try {
            val file = File(this.externalCacheDir, "tempQuote.png")
            val fOut = FileOutputStream(file)
            finalBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut)
            fOut.flush()
            fOut.close()
            file.setReadable(true, false)
            val intent = Intent(Intent.ACTION_SEND).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file))
                type = "image/png"
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

                val apkURI = FileProvider.getUriForFile(this@QuoteActivity,
                        applicationContext.packageName.toString() + ".provider",
                        file)
                setDataAndType(apkURI, type)
            }


            startActivity(Intent.createChooser(intent, "Share image via"))


        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

    }
}
