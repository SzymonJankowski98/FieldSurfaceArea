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
import org.osmdroid.views.overlay.Polygon
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay


class MainActivity : AppCompatActivity() {

    lateinit var mMap: MapView
    lateinit var controller: IMapController;
    lateinit var mMyLocationOverlay: MyLocationNewOverlay;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Configuration.getInstance().load(
            applicationContext,
            getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE)
        )
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
        val geoPoints = ArrayList<GeoPoint>();
        //add your points here
        val polygon = Polygon();    //see note below
        geoPoints.add(GeoPoint(3.2, 4.4))
        geoPoints.add(GeoPoint(4.5, 5.6))
        geoPoints.add(GeoPoint(6.5, 3.2))
        geoPoints.add(geoPoints.get(0));    //forces the loop to close(connect last point to first point)
        polygon.fillPaint.color = Color.parseColor("#1EFFE70E") //set fill color
        polygon.setPoints(geoPoints);
        polygon.title = "A sample polygon"

        mMap.overlays.add(polygon)

        Log.e("TAG", "onCreate:in ${controller.zoomIn()}")
        Log.e("TAG", "onCreate: out  ${controller.zoomOut()}")

        val fieldsListBtn = findViewById<Button>(R.id.fieldsListBtn)
        fieldsListBtn.setOnClickListener {
            val intent = Intent(this, FieldsList::class.java)
            startActivity(intent)
        }

        val sqlLiteManager = SQLiteManager.instanceOfDatabase(this);
        if (sqlLiteManager != null) {
            sqlLiteManager.createField()
            Log.e("DEBUG", sqlLiteManager.getFields().toString())
        }
    }
}