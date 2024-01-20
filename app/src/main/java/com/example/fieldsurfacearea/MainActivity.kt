package com.example.fieldsurfacearea

import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import codewithcal.au.sqliteandroidstudiotutorial.SQLiteManager
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polygon
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay


class MainActivity : AppCompatActivity() {
    companion object {
        private var mapMode = "preview"
        private lateinit var currentField: Field

        fun switchToPreview() {
            mapMode = "preview"
        }

        fun switchToCreateField(field: Field) {
            mapMode = "createField"
            currentField = field
        }
    }

    lateinit var mMap: MapView
    lateinit var controller: IMapController
    lateinit var mMyLocationOverlay: MyLocationNewOverlay

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Configuration.getInstance().load(
            applicationContext,
            getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE)
        )

        SQLiteManager.instanceOfDatabase(this)?.populateFieldsList()
        initializeMap()
        when (mapMode) {
            "preview" -> initializePreview()
            "createField" -> initializeCreateField()
        }
    }

    private fun initializeCreateField() {
        val addFieldButton = findViewById<Button>(R.id.addFieldBtn)
        val cancelButton = findViewById<Button>(R.id.cancelBtn)
        val addPointButton = findViewById<Button>(R.id.addPointBtn)
        addFieldButton.text = "Save"
        cancelButton.visibility = android.view.View.VISIBLE
        addPointButton.visibility = android.view.View.VISIBLE

        addPointButton.setOnClickListener {
            val currentCenter = mMap.mapCenter
            val latitude = currentCenter.latitude
            val longitude = currentCenter.longitude

            Log.e("lat and long before", "$latitude $longitude")
            val point = Point(latitude, longitude)
            currentField.points.add(point)
            drawMarker(GeoPoint(latitude, longitude))
        }

        addFieldButton.setOnClickListener {
            currentField.save(this)
            switchToPreview()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initializePreview() {
        val fieldsListBtn = findViewById<Button>(R.id.fieldsListBtn)
        fieldsListBtn.setOnClickListener {
            val intent = Intent(this, FieldsList::class.java)
            startActivity(intent)
        }

        val addFieldBtn = findViewById<Button>(R.id.addFieldBtn)
        addFieldBtn.setOnClickListener {
            val intent = Intent(this, AddField::class.java)
            startActivity(intent)
        }
    }

    private fun initializeMap() {
        mMap = findViewById(R.id.osmmap)
        mMap.setTileSource(TileSourceFactory.MAPNIK)
        mMap.mapCenter
        mMap.setMultiTouchControls(true)
        mMap.getLocalVisibleRect(Rect())

        mMyLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(this), mMap)
        controller = mMap.controller

        mMyLocationOverlay.enableMyLocation()
        mMyLocationOverlay.enableFollowLocation()
        mMyLocationOverlay.isDrawAccuracyEnabled = true
        mMyLocationOverlay.runOnFirstFix {
            runOnUiThread {
                controller.setCenter(mMyLocationOverlay.myLocation);
                controller.animateTo(mMyLocationOverlay.myLocation)
            }
        }

        controller.setZoom(4)

        Log.e("TAG", "onCreate:in ${controller.zoomIn()}")
        Log.e("TAG", "onCreate: out  ${controller.zoomOut()}")

        Field.list.forEach { field ->
            if (field.points.isEmpty()) return@forEach
            val polygon = drawField(field)
            mMap.overlays.add(polygon)
        }
    }

    private fun drawField(field: Field): Polygon {
        val polygon = Polygon()
        val geoPoints = ArrayList<GeoPoint>();
        field.points.forEach { point ->
            Log.e("lat and long after", "${point.latitude} ${point.longitude}")
            geoPoints.add(GeoPoint(point.latitude, point.longitude))
        }
        geoPoints.add(geoPoints.get(0))
        polygon.fillPaint.color = Color.parseColor("red")
        polygon.setPoints(geoPoints)
        polygon.title = field.name

        return polygon
    }

    private fun drawMarker(point: GeoPoint) {
        val marker = Marker(mMap)
        marker.position = point
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        marker.title = "Marker Title"

        mMap.overlays.add(marker)
        mMap.invalidate()
    }
}