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
import java.net.URL
import java.util.*
import java.util.concurrent.CompletableFuture

class MainActivity : AppCompatActivity() {
    private var timer: Timer? = null

    companion object {
        const val TAG = "ROCKIT"
        const val ROOT_URL = "https://fathomless-cove-40821.herokuapp.com/kabachok/rock"
        // const val rootUrl = "http://192.168.0.20:5000/kabachok/rock"
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

    private fun mapResponseStatus(s: String): String = when (s) {
        getString(R.string.response_rocking) -> getString(R.string.status_rocking)
        getString(R.string.response_stopped) -> getString(R.string.status_stopped)
        else -> getString(R.string.status_unknown)
    }

    private fun refreshStatus(view: View) {
        statusText.text = getString(R.string.status_rocking)
        CompletableFuture.runAsync {
            Log.w(TAG, "Sending request")
            val res = URL(ROOT_URL).readText()
            Log.w(TAG, "Response: $res")
            statusText.text = mapResponseStatus(res)
        }.join()
        Snackbar.make(view, getString(R.string.refreshing_state), Snackbar.LENGTH_LONG)
            .setAction("Action", null).show()
    }

    private fun startCradle(view: View) {
        statusText.text = getString(R.string.status_rocking)
        CompletableFuture.runAsync {
            Log.w(TAG, "Sending request")
            val res = URL("$ROOT_URL/start").readText()
            Log.w(TAG, "Response: $res")
            statusText.text = mapResponseStatus(res)
        }.join()
        Snackbar.make(view, "Lets rock!", Snackbar.LENGTH_LONG)
            .setAction("Action", null).show()
    }

    private fun stopCradle(view: View) {
        statusText.text = getString(R.string.status_stopped)
        CompletableFuture.runAsync {
            Log.w(TAG, "Sending request")
            val res = URL("$ROOT_URL/stop").readText()
            Log.w(TAG, "Response: $res")
            statusText.text = mapResponseStatus(res)
        }.join()
        Snackbar.make(view, getString(R.string.view_zzz), Snackbar.LENGTH_LONG)
            .setAction("Action", null).show()
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
