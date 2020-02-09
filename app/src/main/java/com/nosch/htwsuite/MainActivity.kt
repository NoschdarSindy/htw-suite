package com.nosch.htwsuite

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import kotlinx.android.synthetic.main.activity_main.*
import java.net.URLEncoder
import java.util.*

class MainActivity : AppCompatActivity() {
    val username = "" //Format: s0xxxxxx
    val password = "" //password

    class Web {
        var wv: WebView
        var url: String
        var args: Array<String>

        constructor(wvb: WebView, urlk: String, args: Array<String>) {
            this.wv = wvb
            this.url = urlk
            this.args = args
        }
    }

    private var firstTime = mutableMapOf(
        R.id.navigation_moodle to true,
        R.id.navigation_lsf to true,
        R.id.navigation_mail to true,
        R.id.navigation_mensa to true,
        R.id.navigation_calendar to true
    )

    private fun hideAllBut(v: View) {
        val views = arrayOf(wv_calendar, wv_moodle, wv_lsf, wv_mail, wv_mensa)
        for (v in views) {
            v.visibility = View.GONE
        }
        v.visibility = View.VISIBLE
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_calendar -> {
                if(firstTime[item.itemId]!!) {

                }
                hideAllBut(wv_calendar)
                return@OnNavigationItemSelectedListener true
            }

            R.id.navigation_moodle, R.id.navigation_lsf, R.id.navigation_mail, R.id.navigation_mensa -> {
                val webs = mapOf(
                    R.id.navigation_moodle to Web(wv_moodle, "https://moodle.htw-berlin.de/login/index.php", arrayOf("username", "password")),
                    R.id.navigation_lsf to Web(wv_lsf, "https://lsf.htw-berlin.de/qisserver/rds?state=user&type=1", arrayOf("username", "password")),
                    R.id.navigation_mail to Web(wv_mail, "https://oldwebmail.htw-berlin.de/oldsqmail/src/redirect.php", arrayOf("login_username", "secretkey")),
                    R.id.navigation_mensa to Web(wv_mensa, "https://www.stw.berlin/mensen/mensa-htw-wilheiminenhof.html#tagesreiter", arrayOf())
                )

                val web = webs[item.itemId]!!
                val wv = web.wv
                hideAllBut(wv)

                if(firstTime[item.itemId]!!) {
                    val url = web.url
                    val args = web.args
                    wv.settings.javaScriptEnabled = true
                    if (args.isNotEmpty()) {
                        val data = args[0] + "=" + URLEncoder.encode(username, "UTF-8") + "&" + args[1] + "=" + URLEncoder.encode(password, "UTF-8")
                        wv.postUrl(url, data.toByteArray())
                    } else {
                        wv.loadUrl(url)
                    }
                    wv.webViewClient = WebViewClient()
                    firstTime[item.itemId] = false
                }
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        wv_calendar.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                if(firstTime[R.id.navigation_calendar]!!) {
                    val curWeek = Calendar.WEEK_OF_YEAR.toString() + "_" + Calendar.YEAR.toString()
                    println(curWeek)
                    wv_calendar.loadUrl("https://lsf.htw-berlin.de/qisserver/rds?state=wplan&week=" + curWeek + "&act=show&pool=&show=plan&P.vx=kurz&P.Print=")
                    firstTime[R.id.navigation_calendar] = false
                } else {
                    hideAllBut(wv_calendar)
                }
            }
        }
        wv_calendar.settings.javaScriptEnabled = true
        wv_calendar.setInitialScale(120)
        val data = "username=" + URLEncoder.encode(username, "UTF-8") + "&password=" + URLEncoder.encode(password, "UTF-8")
        wv_calendar.postUrl("https://lsf.htw-berlin.de/qisserver/rds?state=user&type=1", data.toByteArray())
    }
}
