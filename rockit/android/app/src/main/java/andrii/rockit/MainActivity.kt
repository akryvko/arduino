package andrii.rockit

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.net.URL
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Future

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        on.setOnClickListener { view ->
            statusText.text = "Rocking..."
            CompletableFuture.runAsync {
                URL("https://fathomless-cove-40821.herokuapp.com/kabachok/rock/start").readText()
            }
            Snackbar.make(view, "Lets rock!", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        off.setOnClickListener { view ->
            statusText.text = "Stopped"
            CompletableFuture.runAsync {
                URL("https://fathomless-cove-40821.herokuapp.com/kabachok/rock/stop").readText()
            }
            Snackbar.make(view, "Zzzz...", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
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
