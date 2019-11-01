package ir.beigirad.zigzagview.sample

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        clickableZigzagView.setOnClickListener(View.OnClickListener {
            clickableZigzagView.zigzagBackgroundColor = ContextCompat.getColor(this, R.color.colorAccent)
        })
    }
}
