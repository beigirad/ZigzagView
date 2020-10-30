package ir.beigirad.zigzagview.sample

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_randomize.setOnClickListener {
            val randomColor = Color.argb(255, Random.nextInt(256), Random.nextInt(256), Random.nextInt(256))
            zigzag2.zigzagBackgroundColor = randomColor
        }
    }
}
