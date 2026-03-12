package mn.num.unitconverter

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

/**
 * MainActivity
 * Энэ нь аппын үндсэн дэлгэц.
 * Энд бүх категорийн card-ууд байрлах бөгөөд
 * хэрэглэгч аль нэгийг дарвал ConverterActivity руу шилжинэ.
 */
class MainActivity : AppCompatActivity() {

    companion object {
        // ConverterActivity руу категори дамжуулах key
        const val EXTRA_CATEGORY = "extra_category"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // activity_main.xml layout-ийг энэ activity-д холбоно
        setContentView(R.layout.activity_main)

        // Card-ууд дээр дарахад ConverterActivity руу орно
        // bindCard функц ашиглан click event-ийг холбож байна
        bindCard(R.id.cardLength, "length")
        bindCard(R.id.cardArea, "area")
        bindCard(R.id.cardVolume, "volume")
        bindCard(R.id.cardMass, "mass")
        bindCard(R.id.cardTime, "time")
        bindCard(R.id.cardSpeed, "speed")
        bindCard(R.id.cardTemperature, "temperature")
        bindCard(R.id.cardDensity, "density")
        bindCard(R.id.cardEnergy, "energy")
        bindCard(R.id.cardAngle, "angle")
    }

    /**
     * bindCard функц:
     *  - cardId: XML дээрх CardView-ийн id
     *  - category: тухайн card-д харгалзах категори (ж: "length")
     *
     * Card дээр дарахад:
     *  - ConverterActivity руу Intent үүсгэнэ
     *  - Сонгосон категори-г EXTRA_CATEGORY-аар дамжуулна
     */
    //bindCard нь XML дээрх CardView-г олж, click event холбож, тухайн категори-г Intent-ээр дамжуулан ConverterActivity-г нээдэг helper function
    private fun bindCard(cardId: Int, category: String) {

        // XML-ээс CardView-г олж авна
        val card = findViewById<CardView>(cardId)

        // Card дээр click event тохируулна
        card.setOnClickListener {

            // ConverterActivity руу шилжих Intent үүсгэнэ
            val intent = Intent(this, ConverterActivity::class.java)

            // Сонгосон категори-г дамжуулна
            intent.putExtra(EXTRA_CATEGORY, category)

            // Шинэ activity-г эхлүүлнэ
            startActivity(intent)
        }
    }
}