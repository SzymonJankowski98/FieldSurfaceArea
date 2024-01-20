package com.example.fieldsurfacearea

import android.Manifest
import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.location.LocationListener
import android.location.LocationManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import codewithcal.au.sqliteandroidstudiotutorial.SQLiteManager
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polygon
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.advancedpolyline.MonochromaticPaintList
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay


class MainActivity : AppCompatActivity(), LocationListener {
    companion object {
        private var mapMode = "preview"
        private lateinit var currentField: Field
        private var polyline = Polyline()
        private var polyline2 = Polyline()
        private var currentLatitude = 0.0
        private var currentLongitude = 0.0
        lateinit var locationManager: LocationManager

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

        getLocation()
    }

    private fun initializeCreateField() {
        val addFieldButton = findViewById<Button>(R.id.addFieldBtn)
        val cancelButton = findViewById<Button>(R.id.cancelBtn)
        val addPointButton = findViewById<Button>(R.id.addPointBtn)
        val fieldsListBtn = findViewById<Button>(R.id.fieldsListBtn)
        val currentLocationBtn = findViewById<Button>(R.id.currentLocation)
        val point = findViewById<View>(R.id.point)
        addFieldButton.text = "Save"
        cancelButton.visibility = View.VISIBLE
        addPointButton.visibility = View.VISIBLE
        fieldsListBtn.visibility = View.INVISIBLE
        point.visibility = View.VISIBLE
        currentLocationBtn.visibility = View.VISIBLE

        addPointButton.setOnClickListener {
            val currentCenter = mMap.mapCenter
            val latitude = currentCenter.latitude
            val longitude = currentCenter.longitude

            val point = Point(latitude, longitude)
            currentField.points.add(point)
            refreshPolyline()
            drawMarker(GeoPoint(latitude, longitude), customIcon = true)
        }

        addFieldButton.setOnClickListener {
            if (currentField.points.count() < 3) {
                val duration = Toast.LENGTH_SHORT
                val toast = Toast.makeText(this, "Add at least 3 points", duration)
                toast.show()
            } else {
                currentField.save(this)
                switchToPreview()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }

        cancelButton.setOnClickListener {
            switchToPreview()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        currentLocationBtn.setOnClickListener {
            val point = Point(currentLatitude, currentLongitude)
            currentField.points.add(point)

            refreshPolyline()
            drawMarker(GeoPoint(currentLatitude, currentLongitude), customIcon = true)
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
        field.points.forEachIndexed { index, point ->
            val geoPoint = GeoPoint(point.latitude, point.longitude)
            geoPoints.add(geoPoint)
            if (index == 1) { drawMarker(geoPoint, field.name, "Surface area: ${field.surfaceAre()}") }
        }
        geoPoints.add(geoPoints.get(0))
        polygon.fillPaint.color = field.color
        polygon.setPoints(geoPoints)
        polygon.title = field.name

        return polygon
    }

    private fun drawMarker(point: GeoPoint, title: String = "", snippet: String = "", customIcon: Boolean = false) {
        val marker = Marker(mMap)
        marker.position = point
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        marker.title = title
        if (customIcon) {
            marker.icon = ResourcesCompat.getDrawable(resources, R.drawable.pin, null)
        }
        marker.snippet = snippet

        mMap.overlays.add(marker)
        mMap.invalidate()
    }

    private fun refreshPolyline() {
        mMap.overlays.remove(polyline)
        mMap.overlays.remove(polyline2)
        polyline = Polyline()
        polyline.color = currentField.color
        polyline.width = 12f
        polyline.color = currentField.color

        currentField.points.forEach { point ->
            val geoPoint = GeoPoint(point.latitude, point.longitude)
            polyline.addPoint(geoPoint)
        }

        mMap.overlays.add(polyline)

        if (currentField.points.count() < 3) { return }
        polyline2 = Polyline()
        val paint = Paint()
        paint.color = Color.GRAY
        paint.style = Paint.Style.STROKE
        paint.isAntiAlias = true
        polyline2.outlinePaintLists.add(MonochromaticPaintList(paint))

        polyline2.color = currentField.color
        polyline2.width = 25f

        val firstPoint = currentField.points.first()
        val geoPoint = GeoPoint(firstPoint.latitude, firstPoint.longitude)
        polyline2.addPoint(geoPoint)

        val lastPoint = currentField.points.last()
        val geoPoint2 = GeoPoint(lastPoint.latitude, lastPoint.longitude)
        polyline2.addPoint(geoPoint2)

        mMap.overlays.add(polyline2)
        mMap.invalidate()
    }

    private fun getLocation() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if ((ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, arrayOf(ACCESS_FINE_LOCATION), 2)
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5f, this)
    }

    override fun onLocationChanged(location: Location) {
        currentLongitude = location.longitude
        currentLatitude = location.latitude
        Log.e("current location", "Latitude: " + location.latitude + " , Longitude: " + location.longitude)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}