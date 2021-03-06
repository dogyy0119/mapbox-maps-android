package com.mapbox.maps.testapp.examples.annotation

import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.style.layers.getLayer
import com.mapbox.maps.extension.style.utils.ColorUtils
import com.mapbox.maps.plugin.annotation.AnnotationConfig
import com.mapbox.maps.plugin.annotation.generated.*
import com.mapbox.maps.plugin.annotation.getAnnotationPlugin
import com.mapbox.maps.testapp.R
import kotlinx.android.synthetic.main.activity_add_marker_symbol.*
import kotlinx.android.synthetic.main.activity_add_marker_symbol.mapView
import kotlinx.android.synthetic.main.activity_annotation.*
import java.util.*

/**
 * Example showing how to add Line annotations
 */
class LineActivity : AppCompatActivity() {
  private val random = Random()
  private var lineManager: LineManager? = null
  private var index: Int = 0
  private val nextStyle: String
    get() {
      return AnnotationUtils.STYLES[index++ % AnnotationUtils.STYLES.size]
    }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_annotation)
    mapView.getMapboxMap().loadStyleUri(nextStyle) {
      val annotationPlugin = mapView.getAnnotationPlugin()
      lineManager = annotationPlugin.getLineManager(
        mapView,
        AnnotationConfig(COUNTRY_LABEL, LAYER_ID, SOURCE_ID)
      ).apply {
        it.getLayer(LAYER_ID)?.let { layer ->
          Toast.makeText(this@LineActivity, layer.layerId, Toast.LENGTH_LONG).show()
        }
        addClickListener(
          OnLineClickListener {
            Toast.makeText(
              this@LineActivity,
              "click",
              Toast.LENGTH_LONG
            ).show()
            false
          }
        )

        val points = listOf(
          Point.fromLngLat(-4.375974, -2.178992),
          Point.fromLngLat(-7.639772, -4.107888),
          Point.fromLngLat(-11.439207, 2.798737),
        )

        val lineOptions: LineOptions = LineOptions()
          .withPoints(points)
          .withLineColor(ColorUtils.colorToRgbaString(Color.RED))
          .withLineWidth(5.0)
        create(lineOptions)

        // random add lines across the globe
        val lists: MutableList<List<Point>> = ArrayList<List<Point>>()
        for (i in 0..99) {
          lists.add(AnnotationUtils.createRandomPoints())
        }
        val lineOptionsList = lists.map {
          val color = Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256))
          LineOptions()
            .withPoints(it)
            .withLineColor(ColorUtils.colorToRgbaString(color))
        }

        create(lineOptionsList)

        AnnotationUtils.loadStringFromAssets(
          this@LineActivity,
          "annotations.json"
        )?.let {
          create(FeatureCollection.fromJson(it))
        }
      }
    }

    deleteAll.setOnClickListener { lineManager?.deleteAll() }
    changeStyle.setOnClickListener {
      mapView.getMapboxMap().loadStyleUri(nextStyle)
    }
  }

  override fun onStart() {
    super.onStart()
    mapView.onStart()
  }

  override fun onStop() {
    super.onStop()
    mapView.onStop()
  }

  override fun onLowMemory() {
    super.onLowMemory()
    mapView.onLowMemory()
  }

  override fun onDestroy() {
    super.onDestroy()
    mapView.onDestroy()
  }

  companion object {
    private const val LAYER_ID = "line_layer"
    private const val SOURCE_ID = "line_source"
    private const val COUNTRY_LABEL = "country-label"
  }
}