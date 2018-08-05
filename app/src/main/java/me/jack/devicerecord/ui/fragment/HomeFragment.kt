package me.jack.devicerecord.ui.fragment

import android.Manifest
import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.MPPointF
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import me.jack.devicerecord.R
import me.jack.devicerecord.data.DataHelper
import me.jack.devicerecord.data.Device
import me.jack.devicerecord.data.DeviceRecord
import me.jack.devicerecord.extension.shuffle
import me.jack.devicerecord.ui.dialog.Constants
import me.jack.devicerecord.ui.dialog.LoadingDialog
import me.jack.devicerecord.ui.dialog.SearchDialog
import me.jack.devicerecord.ui.dialog.SearchResultCallback
import me.jack.devicerecord.util.FragmentUtils
import me.jack.devicerecord.util.POIUtils
import me.jack.kotlin.library.util.PermissionHelper
import me.jack.kotlin.library.util.ToolbarInterface
import org.jetbrains.anko.coroutines.experimental.bg
import org.jetbrains.anko.find
import org.jetbrains.anko.toast
import java.util.*
import me.jack.devicerecord.data.Constants as DataConstants


/**
 * Created by Jack on 2017/11/28.
 */
class HomeFragment : BaseFragment(), ToolbarInterface, SearchResultCallback {

    private var mPressedTime = 0L
    private val mLoadingDialog by lazy { LoadingDialog() }

    override val toolbar by lazy { find<Toolbar>(R.id.toolbar) }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater?.inflate(R.layout.fragment_home, container, false)!!
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initToolbar()
        start()
    }

    override fun onBackPressed() {
        if (POIUtils.instance.modified) {
            showDialog()
            return
        }
        val currentTime = System.currentTimeMillis()
        if (currentTime - mPressedTime > 500) {
            mPressedTime = currentTime
        } else {
            activity.finish()
        }
    }

    override fun onItemSelected(device: Device) {
        val bundle = Bundle()
        bundle.putSerializable(DataConstants.DEVICE.name, device)
        FragmentUtils.instance.show(activity, DetailFragment::class.java.name, bundle)
    }

    private fun initToolbar() {
        toolbarTitle = getString(R.string.app_name)
        setupMenu(R.menu.menu_home) {
            when (it.itemId) {
                R.id.action_search -> {
                    val dialog = SearchDialog()
                    dialog.setTargetFragment(this, Constants.SEARCH_CODE.ordinal)
                    dialog.show(fragmentManager, SearchDialog::class.java.name)
                }
                R.id.action_add ->
                    FragmentUtils.instance.show(activity, AddFragment::class.java.name, Bundle())
                R.id.action_filter ->
                    FragmentUtils.instance.show(activity, FilterFragment::class.java.name, Bundle())
                R.id.action_info ->
                    FragmentUtils.instance.show(activity, InfoFragment::class.java.name, Bundle())
                R.id.action_import -> toast("Import")
                R.id.action_export -> toast("Export")
            }
        }
    }

    private fun start() {
        //Request permission
        PermissionHelper.instance.requestPermissions(Manifest.permission.READ_EXTERNAL_STORAGE)
                .onSuccess {
                    loadData()
                }
                .onFailure { _, _ ->
                    toast("failure")
                }
                .run(activity)
    }

    private fun loadData() {
        async(UI) {
            mLoadingDialog.show(activity.fragmentManager, LoadingDialog::class.java.name)
            val result = bg { POIUtils.instance.record() }
            val record = result.await()
            if (record != POIUtils.EMPTY_RECORD) {
                //show data
                showData(record)
            } else {
                //show error message and file selector
                toast("No existing excel")
            }
            mLoadingDialog.dismiss()
        }
    }

    private fun showDialog() {
        val dialog = AlertDialog.Builder(activity)
        dialog.setMessage(R.string.message_export_change)
        dialog.setPositiveButton(R.string.button_yes) { _, _ ->
            async(UI) {
                val record = POIUtils.instance.record()
                val result = bg { POIUtils.instance.exportExcel(record) }
                if (result.await()) {
                    activity.finish()
                    toast("export success")
                } else {
                    toast("export failed")
                }
            }
        }
        dialog.setNegativeButton(R.string.button_no) { _, _ ->
            activity.finish()
        }
        dialog.show()
    }

    private fun showData(record: DeviceRecord) {
        val helper = DataHelper(record)
        val brands = helper.sumOfBrand()
        initPieChart()
        showBrandPieChart(brands)
    }

    private fun showBrandPieChart(map: Map<String, Int>) {
        val sum = map.values.sum()
        val entries = ArrayList<PieEntry>()
        map.forEach {
            val value = it.value.toFloat() / sum
            entries.add(PieEntry(value, it.key, it.key))
        }

        val dataSet = PieDataSet(entries, getString(R.string.brand))
        dataSet.setDrawIcons(false)
        dataSet.sliceSpace = 3f
        dataSet.iconsOffset = MPPointF(0f, 40f)
        dataSet.selectionShift = 5f
        dataSet.setColors(colorList(), activity)

        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter())
        data.setValueTextSize(11f)
        data.setValueTextColor(Color.WHITE)

        pieChart.data = data
        pieChart.invalidate()
    }

    private fun initPieChart() {
        pieChart.visibility = View.VISIBLE

        pieChart.setUsePercentValues(true)
        pieChart.description.isEnabled = false
        pieChart.dragDecelerationFrictionCoef = 0.95f

        pieChart.centerText = getString(R.string.brand)
        pieChart.setDrawCenterText(false)
        pieChart.setCenterTextColor(Color.TRANSPARENT)
        pieChart.setCenterTextSize(15f)

        pieChart.isDrawHoleEnabled = true
        pieChart.setHoleColor(Color.TRANSPARENT)
        pieChart.holeRadius = 30f

        pieChart.setTransparentCircleColor(Color.WHITE)
        pieChart.setTransparentCircleAlpha(110)
        pieChart.transparentCircleRadius = 35f


        pieChart.isRotationEnabled = true
        pieChart.rotationAngle = 0f

        pieChart.isHighlightPerTapEnabled = true

        pieChart.animateY(1000, Easing.EasingOption.EaseInOutQuad)

        val l = pieChart.legend
        l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
//        l.orientation = Legend.LegendOrientation.VERTICAL
//        l.setDrawInside(false)
//        l.xEntrySpace = 7f
//        l.yEntrySpace = 0f
//        l.yOffset = 0f

        pieChart.setDrawEntryLabels(true)
        pieChart.setEntryLabelColor(Color.WHITE)
        pieChart.setEntryLabelTextSize(12f)

        pieChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                e?.let {
                    //                    Log.d(javaClass.simpleName, "Entry: value=${e.y}, data=${e.data}")
                    val helper = DataHelper(POIUtils.instance.record())
                    val models = helper.sumOfmodelByBrand(e.data.toString())
                    showModelBarChart(models.toSortedMap())
                }
            }

            override fun onNothingSelected() {

            }
        })
    }

    private fun showModelBarChart(map: SortedMap<String, Int>) {
        val list = map.toList()
        initBarChart(list)
        val entries = ArrayList<BarEntry>()
        list.forEachIndexed { index, pair ->
            entries.add(BarEntry(index.toFloat(), pair.second.toFloat()))
        }
        val dataSet = BarDataSet(entries, getString(R.string.model))
        dataSet.setDrawIcons(false)
        dataSet.setColors(colorList().shuffle(), activity)

        val data = BarData(dataSet)
        data.setValueTextSize(11f)
        data.barWidth = 0.5f

        barChart.data = data
        barChart.invalidate()
    }

    private fun initBarChart(list: List<Pair<String, Int>>) {
        barChart.visibility = View.VISIBLE

        barChart.setDrawBarShadow(false)
        barChart.setDrawValueAboveBar(true)
        barChart.setDrawGridBackground(false)
        barChart.setDrawBorders(false)
        barChart.setDrawMarkers(false)

        barChart.isClickable = false
        barChart.isDragEnabled = false

        barChart.description.isEnabled = false

        barChart.setMaxVisibleValueCount(20)
        barChart.setPinchZoom(false)
        barChart.setFitBars(true)

        val xAxis = barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(false)
        xAxis.granularity = 1f
        xAxis.labelCount = list.size
        xAxis.setValueFormatter { index, _ ->
            val value = Math.min(index.toInt(), list.lastIndex)
            list[value].first
        }

        val max = list.maxValue()

        val leftAxis = barChart.axisLeft
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
        leftAxis.labelCount = Math.min(max, 8)
        leftAxis.granularity = max.toFloat() / leftAxis.labelCount
        leftAxis.axisMinimum = 0f
        leftAxis.setDrawGridLines(false)
        leftAxis.isEnabled = false


        val rightAxis = barChart.axisRight
        rightAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
        rightAxis.setDrawTopYLabelEntry(false)
        rightAxis.setDrawGridLines(false)
        rightAxis.isEnabled = false

        val l = barChart.legend
        l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
        l.form = Legend.LegendForm.SQUARE
        l.setDrawInside(false)

        barChart.animateY(250, Easing.EasingOption.EaseInOutQuad)
    }

    private fun colorList(): IntArray {
        return intArrayOf(R.color.Red, R.color.LightBlue, R.color.Teal, R.color.Lime,
                R.color.LightGreen, R.color.Yellow, R.color.Grey, R.color.Orange,
                R.color.Purple, R.color.Pink, R.color.Blue, R.color.Cyan, R.color.Green,
                R.color.Amber, R.color.DeepOrange)
    }

    private fun List<Pair<String, Int>>.maxValue(): Int {
        var max = 0
        forEach {
            max = Math.max(it.second, max)
        }
        return max
    }

}