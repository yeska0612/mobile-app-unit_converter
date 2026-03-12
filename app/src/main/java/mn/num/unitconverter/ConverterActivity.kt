package mn.num.unitconverter

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import java.text.DecimalFormat
import kotlin.math.PI

/**
 * ConverterActivity
 * Сонгосон категори (length, mass, volume, ...) дээр үндэслэн:
 *  - From/To spinner-үүдийг нэгжээр дүүргэнэ
 *  - Input утгыг авч хөрвүүлэлт хийж
 *  - Output болон Result текст дээр үр дүнг харуулна
 */
class ConverterActivity : AppCompatActivity() {

    // Тоон үр дүнг гоё форматтай (их олон 0-гүй) харуулах формат
    private val df = DecimalFormat("#.########")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Converter дэлгэцийн layout-оо холбоно
        setContentView(R.layout.activity_converter)

        // MainActivity-аас дамжуулж ирсэн категори (ж: "length")
        val category = intent.getStringExtra(MainActivity.EXTRA_CATEGORY) ?: "length"

        // Toolbar тохируулга + back товч
        //XML дээрх @+id/toolbar-той Toolbar-ийг кодоор барьж авна
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        //supportActionBar null байж магадгүй тул crash-гүй ажиллуулах хамгаалалт.
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Буцах сумыг дархад өмнөх дэлгэц рүү буцна
        toolbar.setNavigationOnClickListener { finish() }

        // Дэлгэцийн гарчиг (Length, Mass гэх мэт)
        //XML дээрх tvTitle TextView-г олж аваад text-г нь өөрчилнө
        findViewById<TextView>(R.id.tvTitle).text =
            category.replaceFirstChar { it.uppercase() }

        // UI элементүүдийг кодоос барьж авна
        //XML дээрх spinner-ийг Kotlin кодтой холбож байна
        val spFrom = findViewById<Spinner>(R.id.spFrom)
        val spTo = findViewById<Spinner>(R.id.spTo)
        val etInput = findViewById<EditText>(R.id.etInput)
        val tvOutput = findViewById<TextView>(R.id.tvOutput)
        val tvResult = findViewById<TextView>(R.id.tvResult)
        val btnResult = findViewById<Button>(R.id.btnResult)

        // Категори бүрт харгалзах нэгжүүдийг буцаана
        val units = getUnitsByCategory(category)

        // Spinner-ийн adapter (custom spinner_item / spinner_dropdown_item ашиглана)
        val adapter = ArrayAdapter(this, R.layout.spinner_item, units).apply {
            setDropDownViewResource(R.layout.spinner_dropdown_item)
        }
        spFrom.adapter = adapter
        spTo.adapter = adapter

        // Result товчлуур дээр дарахад хөрвүүлэлт хийнэ
        btnResult.setOnClickListener {

            // Input талбараас текстийг авна
            val raw = etInput.text.toString()

            // Тоо болж хөрвөх эсэхийг шалгана
            val input = raw.toDoubleOrNull()

            // Хэрэв тоо биш бол анхааруулга харуулна
            if (input == null) {
                Toast.makeText(this, getString(R.string.invalid_input), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Spinner-ээс сонгосон нэгжүүд
            val fromUnit = spFrom.selectedItem.toString()
            val toUnit = spTo.selectedItem.toString()

            // Категориос хамаарч хөрвүүлэх аргаа сонгоно
            val output = when (category) {
                "length" -> convertByFactor(input, fromUnit, toUnit, ::lengthFactor)
                "mass" -> convertByFactor(input, fromUnit, toUnit, ::massFactor)
                "volume" -> convertByFactor(input, fromUnit, toUnit, ::volumeFactor)
                "time" -> convertByFactor(input, fromUnit, toUnit, ::timeFactor)
                "speed" -> convertByFactor(input, fromUnit, toUnit, ::speedFactor)
                "area" -> convertByFactor(input, fromUnit, toUnit, ::areaFactor)
                "energy" -> convertByFactor(input, fromUnit, toUnit, ::energyFactor)
                "angle" -> convertByFactor(input, fromUnit, toUnit, ::angleFactor)
                "density" -> convertByFactor(input, fromUnit, toUnit, ::densityFactor)

                // Температур бол тусгай томъёотой тул тусдаа функц ашиглана
                "temperature" -> convertTemperature(input, fromUnit, toUnit)

                // Танигдаагүй категори бол өөрчлөхгүй буцаана (fallback)
                else -> input
            }

            // Output текст дээр үр дүнг харуулна
            tvOutput.text = df.format(output)

            // Доорх Result мөрийг форматтайгаар гаргана
            tvResult.text = getString(
                R.string.result_format,
                df.format(input),
                fromUnit.lowercase(),
                df.format(output),
                toUnit.lowercase()
            )
        }
    }

    //  ЕРӨНХИЙ FACTOR ХӨРВҮҮЛЭЛТ 
    /**
     * Factor ашигладаг нэгжүүдийн ерөнхий хөрвүүлэлт:
     *  1) from нэгжийн factor-оор base нэгж рүү шилжүүлнэ
     *  2) base-ээс to нэгж рүү factor-аар хувааж хөрвүүлнэ
     */
    private fun convertByFactor(
        value: Double,
        from: String,
        to: String,
        factorFunc: (String) -> Double
    ): Double {
        val base = value * factorFunc(from)
        return base / factorFunc(to)
    }

    //  LENGTH (суурь нэгж: meter) 
    // Length-ийн нэгж тус бүрийн meter-д харьцах factor
    private fun lengthFactor(unit: String) = when (unit) {
        getString(R.string.meter) -> 1.0
        getString(R.string.kilometer) -> 1000.0
        getString(R.string.centimeter) -> 0.01
        getString(R.string.millimeter) -> 0.001
        getString(R.string.inch) -> 0.0254
        getString(R.string.foot) -> 0.3048
        getString(R.string.yard) -> 0.9144
        getString(R.string.mile) -> 1609.344
        else -> 1.0
    }

    //  MASS (суурь нэгж: kg) 
    private fun massFactor(unit: String) = when (unit) {
        getString(R.string.kilogram) -> 1.0
        getString(R.string.gram) -> 0.001
        getString(R.string.pound) -> 0.45359237
        getString(R.string.ounce) -> 0.028349523125
        getString(R.string.ton) -> 1000.0
        else -> 1.0
    }

    //  VOLUME (суурь нэгж: liter) 
    private fun volumeFactor(unit: String) = when (unit) {
        getString(R.string.liter) -> 1.0
        getString(R.string.milliliter) -> 0.001
        getString(R.string.gallon) -> 3.78541
        getString(R.string.quart) -> 0.946353
        getString(R.string.pint) -> 0.473176
        getString(R.string.cup) -> 0.236588
        else -> 1.0
    }

    //  TIME (суурь нэгж: second) 
    private fun timeFactor(unit: String) = when (unit) {
        getString(R.string.second) -> 1.0
        getString(R.string.minute) -> 60.0
        getString(R.string.hour) -> 3600.0
        getString(R.string.day) -> 86400.0
        getString(R.string.week) -> 604800.0

        // Month/Year нь яг тогтмол биш тул дундаж секунд ашигласан (ойролцоо)
        getString(R.string.month) -> 2629800.0
        getString(R.string.year) -> 31557600.0
        else -> 1.0
    }

    //  SPEED (суурь нэгж: m/s) 
    private fun speedFactor(unit: String) = when (unit) {
        getString(R.string.meter_per_second) -> 1.0
        getString(R.string.kilometer_per_hour) -> 0.277778
        getString(R.string.mile_per_hour) -> 0.44704
        getString(R.string.knot) -> 0.514444
        else -> 1.0
    }

    //  AREA (суурь нэгж: m²) 
    private fun areaFactor(unit: String) = when (unit) {
        getString(R.string.square_meter) -> 1.0
        getString(R.string.square_kilometer) -> 1_000_000.0
        getString(R.string.square_foot) -> 0.092903
        getString(R.string.square_inch) -> 0.00064516
        getString(R.string.hectare) -> 10000.0
        getString(R.string.acre) -> 4046.86
        else -> 1.0
    }

    //  ENERGY (суурь нэгж: joule) 
    private fun energyFactor(unit: String) = when (unit) {
        getString(R.string.joule) -> 1.0
        getString(R.string.kilojoule) -> 1000.0
        getString(R.string.calorie) -> 4.184
        getString(R.string.kilocalorie) -> 4184.0
        getString(R.string.watt_hour) -> 3600.0
        getString(R.string.kilowatt_hour) -> 3_600_000.0
        else -> 1.0
    }

    //  ANGLE (суурь нэгж: radian) 
    private fun angleFactor(unit: String) = when (unit) {
        getString(R.string.radian) -> 1.0
        getString(R.string.degree) -> PI / 180
        getString(R.string.gradian) -> PI / 200
        else -> 1.0
    }

    //  DENSITY (суурь нэгж: kg/m³) 
    private fun densityFactor(unit: String) = when (unit) {
        getString(R.string.kg_per_cubic_meter) -> 1.0
        getString(R.string.g_per_cubic_centimeter) -> 1000.0
        getString(R.string.lb_per_cubic_foot) -> 16.0185
        else -> 1.0
    }

    //  TEMPERATURE (тусгай хөрвүүлэлт) 
    /**
     * Температур нь factor-аар биш, томъёогоор хувирдаг:
     *  - Эхлээд celsius рүү шилжүүлнэ
     *  - Дараа нь сонгосон to нэгж рүү хөрвүүлнэ
     */
    private fun convertTemperature(value: Double, from: String, to: String): Double {

        // Аль нэгжээс ирснийг celsius болгон хувиргана
        val celsius = when (from) {
            getString(R.string.celsius) -> value
            getString(R.string.fahrenheit) -> (value - 32) * 5 / 9
            getString(R.string.kelvin) -> value - 273.15
            else -> value
        }

        // Celsius-ээс to нэгж рүү хөрвүүлнэ
        return when (to) {
            getString(R.string.celsius) -> celsius
            getString(R.string.fahrenheit) -> celsius * 9 / 5 + 32
            getString(R.string.kelvin) -> celsius + 273.15
            else -> celsius
        }
    }

    //  Категори -> Нэгжүүдийн жагсаалт 
    /**
     * Категори бүрийн spinner дээр харагдах нэгжүүдийг жагсаалт хэлбэрээр буцаана
     */
    private fun getUnitsByCategory(category: String): List<String> {
        return when (category) {
            "length" -> listOf(
                getString(R.string.meter),
                getString(R.string.kilometer),
                getString(R.string.centimeter),
                getString(R.string.millimeter),
                getString(R.string.inch),
                getString(R.string.foot),
                getString(R.string.yard),
                getString(R.string.mile),
            )
            "mass" -> listOf(
                getString(R.string.kilogram),
                getString(R.string.gram),
                getString(R.string.pound),
                getString(R.string.ounce),
                getString(R.string.ton),
            )
            "volume" -> listOf(
                getString(R.string.liter),
                getString(R.string.milliliter),
                getString(R.string.gallon),
                getString(R.string.quart),
                getString(R.string.pint),
                getString(R.string.cup),
            )
            "time" -> listOf(
                getString(R.string.second),
                getString(R.string.minute),
                getString(R.string.hour),
                getString(R.string.day),
                getString(R.string.week),
                getString(R.string.month),
                getString(R.string.year),
            )
            "speed" -> listOf(
                getString(R.string.meter_per_second),
                getString(R.string.kilometer_per_hour),
                getString(R.string.mile_per_hour),
                getString(R.string.knot),
            )
            "area" -> listOf(
                getString(R.string.square_meter),
                getString(R.string.square_kilometer),
                getString(R.string.square_foot),
                getString(R.string.square_inch),
                getString(R.string.hectare),
                getString(R.string.acre),
            )
            "energy" -> listOf(
                getString(R.string.joule),
                getString(R.string.kilojoule),
                getString(R.string.calorie),
                getString(R.string.kilocalorie),
                getString(R.string.watt_hour),
                getString(R.string.kilowatt_hour),
            )
            "angle" -> listOf(
                getString(R.string.degree),
                getString(R.string.radian),
                getString(R.string.gradian),
            )
            "density" -> listOf(
                getString(R.string.kg_per_cubic_meter),
                getString(R.string.g_per_cubic_centimeter),
                getString(R.string.lb_per_cubic_foot),
            )
            "temperature" -> listOf(
                getString(R.string.celsius),
                getString(R.string.fahrenheit),
                getString(R.string.kelvin),
            )
            else -> listOf(getString(R.string.meter))
        }
    }
}