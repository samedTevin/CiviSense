package com.samedtevin.bagcilarapp.ui.main.detail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.samedtevin.bagcilarapp.R
import com.samedtevin.bagcilarapp.databinding.FragmentLocationBinding
import org.json.JSONObject
import com.google.android.gms.maps.model.Polygon
import com.google.android.gms.maps.model.PolygonOptions


class LocationFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentLocationBinding? = null
    val binding get() = _binding!!

    private lateinit var myMap: GoogleMap
    private var selectedMarker: Marker? = null
    private var bagcilarPolygon: Polygon? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLocationBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        myMap = googleMap

        val bagcilar = LatLng(41.0390, 28.8567)

        val bagcilarPoints = getBagcilarPoints()

        bagcilarPolygon  = myMap.addPolygon(
            PolygonOptions().addAll(bagcilarPoints).strokeWidth(4f).fillColor(0x22000000)
        )

        selectedMarker = myMap.addMarker(
            MarkerOptions().position(bagcilar).title("Selected location").draggable(true)
        )

        myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bagcilar, 15f))

        setupMarkerListeners()
    }

    private fun setupMarkerListeners(){
        myMap.setOnMapClickListener { latLng ->
            moveMarker(latLng)
        }

        myMap.setOnMarkerDragListener(object : GoogleMap.OnMarkerDragListener {
            override fun onMarkerDrag(p0: Marker) {
            }

            override fun onMarkerDragEnd(p0: Marker) {
                val latLng  = p0.position

                val latitude = latLng.latitude
                val longitude = latLng.longitude
            }

            override fun onMarkerDragStart(p0: Marker) {
                Toast.makeText(
                    requireContext(),
                    "DRAG BAŞLADI",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun moveMarker(latLng: LatLng){

        if(isPointInsidePolygon(latLng)){
            selectedMarker?.position = latLng
        }
        else{
            Toast.makeText(requireContext(),"Please select a location that contains Bagcilar.",Toast.LENGTH_SHORT).show()
        }
    }

    private fun isPointInsidePolygon(point: LatLng): Boolean{

        val polygon = getBagcilarPoints()

        if(polygon.isEmpty()){
            return false
        }

        var inside = false

        var j = polygon.lastIndex

        for(i in polygon.indices){

            val current = polygon[i]
            val previous = polygon[j]

            val intersects = ((current.latitude > point.latitude) !=
                    (previous.latitude > point.latitude)) &&
                    (point.longitude <
                            (previous.longitude - current.longitude) *
                            (point.latitude - current.latitude) /
                            (previous.latitude - current.latitude) +
                            current.longitude)


            if(intersects){
                inside = !inside
            }

            j = i

        }
        return inside
    }

    private fun loadBagcilarGeoJson(): JSONObject{

        val inputStream = requireContext().assets.open("ilce_geojson.json")

        val jsonString = inputStream.bufferedReader().use{ it.readText() }

        return JSONObject(jsonString)
    }

    private fun getBagcilarFeature(): JSONObject?{

        val geoJson = loadBagcilarGeoJson()

        val features = geoJson.getJSONArray("features")

        for(i in 0 until features.length()){

            val feature = features.getJSONObject(i)

            val properties = feature.getJSONObject("properties")
            val address = properties.getJSONObject("address")

            val town = address.optString("town")

            if(town.equals("Bağcılar", ignoreCase = true)){
                return feature
            }
        }

        return null
    }

    private fun getBagcilarPoints(): List<LatLng>{

        val feature = getBagcilarFeature() ?: return emptyList()

        val geometry = feature.getJSONObject("geometry")
        val coordinates = geometry.getJSONArray("coordinates")
        val outerRing = coordinates.getJSONArray(0)
        val points = mutableListOf<LatLng>()

        for(i in 0 until outerRing.length()){
            val coordinate = outerRing.getJSONArray(i)

            val longitude = coordinate.getDouble(0)
            val latitude = coordinate.getDouble(1)

            points.add(
                LatLng(latitude,longitude)
            )
        }

        return points
    }

}