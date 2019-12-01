package andrii.rockit

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.json.JSONObject
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.CompletableFuture

class MainActivity : AppCompatActivity() {
    private var timer: Timer? = null

    companion object {
        const val TAG = "ROCKIT"
        //const val ROOT_URL = "https://fathomless-cove-40821.herokuapp.com/kabachok/rock"
        const val ROOT_URL = "http://192.168.0.20:5000/kabachok/rock"
        const val REFRESH_DELAY : Long = 30 * 1000
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        on.setOnClickListener { view ->
            startCradle(view)
        }

        off.setOnClickListener { view ->
            stopCradle(view)
        }
    }

    override fun onPause() {
        super.onPause()
        cancelRefreshTimer()
    }

    private fun mapResponseStatus(response: JSONObject): String = when (response.getString("status")) {
        getString(R.string.response_rocking) -> getString(R.string.status_rocking)
        getString(R.string.response_stopped) -> getString(R.string.status_stopped)
        else -> getString(R.string.status_unknown)
    }

    private fun getResponseStatusDuration(response: JSONObject): String {
        val since = response.getLong("since")
        if (since > 0) {
            val instant = Instant.ofEpochMilli(since)
            val localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
            val formatted = DateTimeFormatter.ofPattern("HH:mm")
                .format(localDateTime)
            return "${getString(R.string.status_since)} $formatted"
        }
        return ""
    }

    private fun refreshStatus(view: View) {
        statusText.text = getString(R.string.status_rocking)
        CompletableFuture.runAsync {
            processGetResponse(URL(ROOT_URL))
        }.join()
        Snackbar.make(view, getString(R.string.refreshing_state), Snackbar.LENGTH_LONG)
            .setAction("Action", null).show()
    }

    private fun startCradle(view: View) {
        statusText.text = getString(R.string.status_rocking)
        CompletableFuture.runAsync {
            processGetResponse(URL("$ROOT_URL/start"))
        }.join()
        Snackbar.make(view, "Lets rock!", Snackbar.LENGTH_LONG)
            .setAction("Action", null).show()
    }

    private fun stopCradle(view: View) {
        statusText.text = getString(R.string.status_stopped)
        CompletableFuture.runAsync {
            processGetResponse(URL("$ROOT_URL/stop"))
        }.join()
        Snackbar.make(view, getString(R.string.view_zzz), Snackbar.LENGTH_LONG)
            .setAction("Action", null).show()
    }

    private fun processGetResponse(url: URL) {
        val c = url.openConnection()
        c.setRequestProperty("Accept", "application/json")

        Log.w(TAG, "Sending request to $url")
        val res = InputStreamReader(c.getInputStream()).readText()
        Log.w(TAG, "Response: $res")
        val json = JSONObject(res)
        statusText.text = mapResponseStatus(json)
        statusSince.text = getResponseStatusDuration(json)
    }

    private fun cancelRefreshTimer() {
        if (timer != null) {
            timer!!.cancel()
        }
    }

    private fun runRefreshTimer() {
        if (timer != null) {
            cancelRefreshTimer()
        }
        timer = Timer("Refresh Timer")
        class RefreshTimerTask : TimerTask() {
            override fun run() {
                runOnUiThread {
                    Runnable {
                        refreshStatus(activityMain)
                    }.run()
                }
            }
        }
        timer!!.scheduleAtFixedRate(RefreshTimerTask(), 0, REFRESH_DELAY)
    }

    override fun onResume() {
        super.onResume()
        runRefreshTimer()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }


}
