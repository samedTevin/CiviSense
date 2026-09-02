package com.samedtevin.bagcilarapp.ui.main.detail

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polygon
import com.google.android.gms.maps.model.PolygonOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.samedtevin.bagcilarapp.BuildConfig
import com.samedtevin.bagcilarapp.R
import com.samedtevin.bagcilarapp.adapter.uiadapters.LocationSearchAdapter
import com.samedtevin.bagcilarapp.databinding.FragmentLocationBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.util.Locale

class LocationFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentLocationBinding? = null
    private val binding get() = _binding!!

    private lateinit var myMap: GoogleMap

    private var selectedMarker: Marker? = null
    private var bagcilarPolygon: Polygon? = null

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var placesClient: PlacesClient
    private lateinit var autoCompleteToken: AutocompleteSessionToken
    private lateinit var locationSearchAdapter: LocationSearchAdapter

    private var searchJob: Job? = null

    private var isSelectingPlace = false

    private val locationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permission ->

            val fineLocationGranted =
                permission[Manifest.permission.ACCESS_FINE_LOCATION] == true

            val coarseLocationGranted =
                permission[Manifest.permission.ACCESS_COARSE_LOCATION] == true

            if (fineLocationGranted || coarseLocationGranted) {
                getCurrentLocation()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Need location permission.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentLocationBinding.inflate(
            inflater,
            container,
            false
        )

        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {

        super.onViewCreated(view, savedInstanceState)

        initializePlaces()

        locationSearchAdapter = LocationSearchAdapter { prediction ->
            selectSearchResult(prediction)
        }

        binding.rvPlacesResult.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = locationSearchAdapter
        }

        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map)
                    as SupportMapFragment

        mapFragment.getMapAsync(this)

        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(
                requireContext()
            )

        binding.fabCurrentLocation.setOnClickListener {
            getCurrentLocation()
        }

        setupSearchView()
    }

    override fun onMapReady(googleMap: GoogleMap) {

        myMap = googleMap

        myMap.uiSettings.isZoomControlsEnabled = true

        myMap.setMinZoomPreference(13f)
        myMap.setMaxZoomPreference(20f)

        val bagcilar = LatLng(
            41.0390,
            28.8567
        )

        val bagcilarPoints = getBagcilarPoints()

        bagcilarPolygon = myMap.addPolygon(
            PolygonOptions()
                .addAll(bagcilarPoints)
                .strokeWidth(4f)
                .fillColor(0x22000000)
        )

        selectedMarker = myMap.addMarker(
            MarkerOptions()
                .position(bagcilar)
                .title("Selected location")
                .draggable(true)
        )

        myMap.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                bagcilar,
                15f
            )
        )

        setupMarkerListeners()
    }

    private fun initializePlaces() {

        if (!Places.isInitialized()) {

            Places.initializeWithNewPlacesApiEnabled(
                requireContext(),
                BuildConfig.GOOGLE_MAPS_API_KEY
            )
        }

        placesClient =
            Places.createClient(requireContext())

        autoCompleteToken =
            AutocompleteSessionToken.newInstance()
    }

    private fun setupSearchView() {

        binding.locationSearchView.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {

                override fun onQueryTextChange(newText: String?): Boolean {

                    val query = newText?.trim().orEmpty()

                    if (query.length < 2) {
                        locationSearchAdapter.submitList(emptyList())
                        binding.rvPlacesResult.visibility = View.GONE
                        return true
                    }

                    searchPlaces(query)

                    return true
                }

                override fun onQueryTextSubmit(query: String?): Boolean {

                    val text = query?.trim().orEmpty()

                    if (text.length >= 2) {
                        searchPlaces(text)
                    }

                    return true
                }
            }
        )
    }

    private fun searchPlaces(query: String) {

        val bagcilarBounds = RectangularBounds.newInstance(
            LatLng(41.0150, 28.8100),
            LatLng(41.0750, 28.9000)
        )

        val request =
            FindAutocompletePredictionsRequest
                .builder()
                .setQuery(query)
                .setCountries("TR")
                .setLocationRestriction(bagcilarBounds)
                .setSessionToken(autoCompleteToken)
                .build()

        placesClient
            .findAutocompletePredictions(request)
            .addOnSuccessListener { response ->

                val results =
                    response.autocompletePredictions

                locationSearchAdapter.submitList(results)

                binding.rvPlacesResult.visibility =
                    if (results.isEmpty()) {
                        View.GONE
                    } else {
                        View.VISIBLE
                    }
            }
            .addOnFailureListener { exception ->

                locationSearchAdapter.submitList(emptyList())

                binding.rvPlacesResult.visibility =
                    View.GONE

                Toast.makeText(
                    requireContext(),
                    "Search error: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    private fun selectSearchResult(
        prediction: AutocompletePrediction
    ) {

        isSelectingPlace = true

        val request =
            FetchPlaceRequest.builder(
                prediction.placeId,
                listOf(
                    Place.Field.DISPLAY_NAME,
                    Place.Field.FORMATTED_ADDRESS,
                    Place.Field.LOCATION
                )
            ).build()

        placesClient
            .fetchPlace(request)
            .addOnSuccessListener { response ->

                if (!isAdded) return@addOnSuccessListener

                val place = response.place

                val location =
                    place.location
                        ?: return@addOnSuccessListener

                if (!isPointInsidePolygon(location)) {

                    Toast.makeText(
                        requireContext(),
                        "Please select a location inside Bağcılar.",
                        Toast.LENGTH_SHORT
                    ).show()

                    isSelectingPlace = false
                    return@addOnSuccessListener
                }

                selectedMarker?.position = location
                selectedMarker?.tag = location

                myMap.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        location,
                        17f
                    )
                )

                val placeName =
                    place.displayName
                        ?: "Selected Location"

                val placeAddress =
                    place.formattedAddress
                        ?: "Bağcılar / İstanbul"

                binding.tvAddress.text =
                    placeName

                binding.tvAddressDetail.text =
                    placeAddress

                binding.rvPlacesResult.visibility =
                    View.GONE

                binding.locationSearchView.setQuery(
                    placeName,
                    false
                )

                autoCompleteToken =
                    AutocompleteSessionToken.newInstance()

                binding.locationSearchView.clearFocus()

                isSelectingPlace = false
            }
            .addOnFailureListener {

                isSelectingPlace = false

                Toast.makeText(
                    requireContext(),
                    "Could not get location details.",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun updateSelectedLocation(
        location: LatLng
    ) {

        if (!isPointInsidePolygon(location)) {

            Toast.makeText(
                requireContext(),
                "Please select a location inside Bağcılar.",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        selectedMarker?.position = location
        selectedMarker?.tag = location

        myMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                location,
                17f
            )
        )

        getAddressFromLocation(location)
    }

    private fun getAddressFromLocation(
        location: LatLng
    ) {

        viewLifecycleOwner.lifecycleScope.launch {

            val address = withContext(Dispatchers.IO) {

                try {

                    val geocoder =
                        Geocoder(
                            requireContext(),
                            Locale("tr", "TR")
                        )

                    @Suppress("DEPRECATION")
                    geocoder.getFromLocation(
                        location.latitude,
                        location.longitude,
                        1
                    )?.firstOrNull()

                } catch (e: Exception) {

                    null
                }
            }

            if (!isAdded) return@launch

            if (address != null) {

                val streetName =
                    address.thoroughfare
                        ?: address.subLocality
                        ?: address.locality
                        ?: "Selected Location"

                binding.tvAddress.text =
                    streetName

                binding.tvAddressDetail.text =
                    address.getAddressLine(0)
                        ?: "Bağcılar / İstanbul"

            } else {

                binding.tvAddress.text =
                    "Selected Location"

                binding.tvAddressDetail.text =
                    "Bağcılar / İstanbul"
            }
        }
    }

    private fun setupMarkerListeners() {

        myMap.setOnMapClickListener { latLng ->

            updateSelectedLocation(latLng)
        }

        myMap.setOnMarkerDragListener(
            object : GoogleMap.OnMarkerDragListener {

                override fun onMarkerDrag(
                    marker: Marker
                ) {
                }

                override fun onMarkerDragStart(
                    marker: Marker
                ) {
                }

                override fun onMarkerDragEnd(
                    marker: Marker
                ) {

                    val newPosition =
                        marker.position

                    if (!isPointInsidePolygon(newPosition)) {

                        marker.position =
                            marker.tag as? LatLng
                                ?: LatLng(
                                    41.0390,
                                    28.8567
                                )

                        Toast.makeText(
                            requireContext(),
                            "Please select a location inside Bağcılar.",
                            Toast.LENGTH_SHORT
                        ).show()

                    } else {

                        marker.tag =
                            newPosition

                        getAddressFromLocation(
                            newPosition
                        )
                    }
                }
            }
        )

        selectedMarker?.tag =
            selectedMarker?.position
    }

    private fun isPointInsidePolygon(
        point: LatLng
    ): Boolean {

        val polygon =
            getBagcilarPoints()

        if (polygon.isEmpty()) {
            return false
        }

        var inside = false
        var j = polygon.lastIndex

        for (i in polygon.indices) {

            val current =
                polygon[i]

            val previous =
                polygon[j]

            val intersects =
                ((current.latitude > point.latitude) !=
                        (previous.latitude > point.latitude)) &&
                        (
                                point.longitude <
                                        (previous.longitude - current.longitude) *
                                        (point.latitude - current.latitude) /
                                        (previous.latitude - current.latitude) +
                                        current.longitude
                                )

            if (intersects) {
                inside = !inside
            }

            j = i
        }

        return inside
    }

    private fun getCurrentLocation() {

        if (
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )

            return
        }

        fusedLocationClient
            .lastLocation
            .addOnSuccessListener { location ->

                if (location != null) {

                    val currentLocation =
                        LatLng(
                            location.latitude,
                            location.longitude
                        )

                    updateSelectedLocation(
                        currentLocation
                    )

                } else {

                    Toast.makeText(
                        requireContext(),
                        "Current location could not be obtained.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun loadBagcilarGeoJson(): JSONObject {

        val inputStream =
            requireContext()
                .assets
                .open("ilce_geojson.json")

        val jsonString =
            inputStream
                .bufferedReader()
                .use { it.readText() }

        return JSONObject(jsonString)
    }

    private fun getBagcilarFeature(): JSONObject? {

        val geoJson =
            loadBagcilarGeoJson()

        val features =
            geoJson.getJSONArray("features")

        for (i in 0 until features.length()) {

            val feature =
                features.getJSONObject(i)

            val properties =
                feature.getJSONObject("properties")

            val address =
                properties.getJSONObject("address")

            val town =
                address.optString("town")

            if (
                town.equals(
                    "Bağcılar",
                    ignoreCase = true
                )
            ) {
                return feature
            }
        }

        return null
    }

    private fun getBagcilarPoints(): List<LatLng> {

        val feature =
            getBagcilarFeature()
                ?: return emptyList()

        val geometry =
            feature.getJSONObject("geometry")

        val coordinates =
            geometry.getJSONArray("coordinates")

        val outerRing =
            coordinates.getJSONArray(0)

        val points =
            mutableListOf<LatLng>()

        for (i in 0 until outerRing.length()) {

            val coordinate =
                outerRing.getJSONArray(i)

            val longitude =
                coordinate.getDouble(0)

            val latitude =
                coordinate.getDouble(1)

            points.add(
                LatLng(
                    latitude,
                    longitude
                )
            )
        }

        return points
    }

    override fun onDestroyView() {

        searchJob?.cancel()

        super.onDestroyView()

        _binding = null
    }
}