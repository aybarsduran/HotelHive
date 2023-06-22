package com.example.hotelhive

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class testRapidAPI {

    suspend fun performLocationSearch(location:String): String {
        var geolocationId = ""

            val client = OkHttpClient()

            val locationRequest = Request.Builder()
                .url("https://hotels4.p.rapidapi.com/locations/v3/search?q=$location&locale=en_US&langid=1033&siteid=300000001")
                .get()
                .addHeader("X-RapidAPI-Key", "94279c485cmshae46e5f448af168p1a45a5jsn8bfc653905c4")
                .addHeader("X-RapidAPI-Host", "hotels4.p.rapidapi.com")
                .build()

            val locationResponse = client.newCall(locationRequest).execute()
            val jsonData = locationResponse.body?.string()

            if (locationResponse.isSuccessful && jsonData != null) {
                try {
                    val jsonObject = JSONObject(jsonData)
                    val searchResultsArray = jsonObject.getJSONArray("sr")
                    if (searchResultsArray.length() > 0) {
                        val searchResult = searchResultsArray.getJSONObject(0)
                        geolocationId = searchResult.getString("gaiaId")
                        println(geolocationId)
                       // performHotelSearch(geolocationId,numOfAdults,checkInDay,checkInMonth,checkInYear,checkOutDay,checkOutMonth,checkOutYear)

                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }

        return geolocationId
    }



    suspend fun fetchHotelImages(hotelId:String): List<ImageHotel> {
        val client = OkHttpClient()

        val mediaType = "application/json".toMediaTypeOrNull()
        var imageList = mutableListOf<ImageHotel>()


        val body = """
            {
                "currency": "USD",
                "eapid": 1,
                "locale": "en_US",
                "siteId": 300000001,
                "propertyId": "$hotelId"
             }
        """.trimIndent().toRequestBody(mediaType)
        val request = Request.Builder()
            .url("https://hotels4.p.rapidapi.com/properties/v2/detail")
            .post(body)
            .addHeader("content-type", "application/json")
            .addHeader("X-RapidAPI-Key", "4987535b2fmsh985bd7e3b97b95cp10a059jsne8f95d1af92b")
            .addHeader("X-RapidAPI-Host", "hotels4.p.rapidapi.com")
            .build()

        val response = client.newCall(request).execute()
        val hotelData = response.body?.string()

        if (response.isSuccessful && hotelData != null) {
            // Parse the hotel data and perform further operations
            try {
                val hotelsJsonObject = JSONObject(hotelData)
                val dataArray = hotelsJsonObject.getJSONObject("data")
                if (dataArray.length() > 0) {
                    val propertyInfoObject =
                        dataArray.getJSONObject("propertyInfo")
                    val propertyGalleryObject = propertyInfoObject.getJSONObject("propertyGallery")
                    val imagesArray = propertyGalleryObject.getJSONArray("images")


                    // Process the hotel data from the propertiesArray

                    for (i in 0 until imagesArray.length()) {
                        val imageObject = imagesArray.getJSONObject(i)
                        // Extract the desired hotel information from the propertyObject
                        val insideImageObject = imageObject.getJSONObject("image")
                        val imageURL= insideImageObject.getString("url")

                        val finalImage = ImageHotel(imageURL)
                        imageList.add((finalImage))
                    }

                    }
                }
            catch (e: JSONException) {
                e.printStackTrace()
            }


            }
        return imageList


    }







    suspend fun performHotelSearch(geolocationId: String,
                                   numOfAdults: Int,
                                   checkInDay: Int,
                                   checkInMonth: Int,
                                   checkInYear: Int,
                                   checkOutDay: Int,
                                   checkOutMonth: Int,
                                   checkOutYear: Int): List<Hotel> {
        fun calculateTotalPrice(dailyPrice : Float): Float {
            val checkInCalendar = Calendar.getInstance().apply {
                set(checkInYear, checkInMonth, checkInDay)
            }

            val checkOutCalendar = Calendar.getInstance().apply {
                set(checkOutYear, checkOutMonth, checkOutDay)
            }

            val stayDuration = ((checkOutCalendar.timeInMillis - checkInCalendar.timeInMillis) / (1000 * 60 * 60 * 24)).toInt()
            return dailyPrice * stayDuration
        }
        fun calculateStayDuration(): Int {
            val checkInCalendar = Calendar.getInstance().apply {
                set(checkInYear, checkInMonth, checkInDay)
            }

            val checkOutCalendar = Calendar.getInstance().apply {
                set(checkOutYear, checkOutMonth, checkOutDay)
            }

            val stayDuration = ((checkOutCalendar.timeInMillis - checkInCalendar.timeInMillis) / (1000 * 60 * 60 * 24)).toInt()
            return  stayDuration
        }

        var hotelList = mutableListOf<Hotel>()


        val client = OkHttpClient()

        val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
        val body = """
            {
                "currency": "TRY",
                "eapid": 1,
                "locale": "en_US",
                "siteId": 300000001,
                "destination": {
                    "regionId": "$geolocationId"
                },
                "checkInDate": {
                    "day": $checkInDay,
                    "month": $checkInMonth,
                    "year": $checkInYear
                },
                "checkOutDate": {
                    "day": $checkOutDay,
                    "month": $checkOutMonth,
                    "year": $checkOutYear
                },
                "rooms": [
                    {
                        "adults": $numOfAdults,
                        "children": [
                            {
                                "age": 5
                            },
                            {
                                "age": 7
                            }
                        ]
                    }
                ],
                "resultsStartingIndex": 0,
                "resultsSize": 10,
                "sort": "PRICE_LOW_TO_HIGH",
                "filters": {
                    "price": {
                        "max": 10000,
                        "min": 100
                    }
                }
            }
        """.trimIndent().toRequestBody(mediaType)

        val request = Request.Builder()
            .url("https://hotels4.p.rapidapi.com/properties/v2/list")
            .post(body)
            .addHeader("content-type", "application/json")
            .addHeader("X-RapidAPI-Key", "94279c485cmshae46e5f448af168p1a45a5jsn8bfc653905c4")
            .addHeader("X-RapidAPI-Host", "hotels4.p.rapidapi.com")
            .build()

        val response = client.newCall(request).execute()
        val hotelData = response.body?.string()

        // Handle the hotel data response
        if (response.isSuccessful && hotelData != null) {
            // Parse the hotel data and perform further operations
            try {
                val hotelsJsonObject = JSONObject(hotelData)
                val dataArray = hotelsJsonObject.getJSONObject("data")
                if(dataArray.length()>0) {
                    val propertySearchObject =
                        dataArray.getJSONObject("propertySearch")
                    val propertiesArray = propertySearchObject.getJSONArray("properties")


                    // Process the hotel data from the propertiesArray

                    for (i in 0 until propertiesArray.length()) {
                        val propertyObject = propertiesArray.getJSONObject(i)
                        // Extract the desired hotel information from the propertyObject
                        val hotelName = propertyObject.getString("name")

                        //getImageURL
                        val propertyImageObject = propertyObject.getJSONObject("propertyImage")
                        val imageObject= propertyImageObject.getJSONObject("image")
                        val imageUrl=imageObject.getString("url")

                        //getPrice
                        val priceObject = propertyObject.getJSONObject("price")
                        val leadObject = priceObject.getJSONObject("lead")
                        val priceInfo= leadObject.getString("formatted")

                        var priceNumber = priceInfo.replace("$", "").toFloatOrNull()
                        if (priceNumber != null) {
                          priceNumber=  calculateTotalPrice(priceNumber)
                        }

                        val priceInfoFormatted = "$${priceNumber?.toInt()}"

                        //stayDuration
                        val stayDuration = calculateStayDuration()

                        //getID
                        val hotelId = propertyObject.getString("id")



                        //getReviews
                        val reviewObject= propertyObject.getJSONObject("reviews")
                        val reviewScore= reviewObject.getString("score")
                        val reviewTotalCount= reviewObject.getString("total")

                        //getRegion
                        val neighborhoodObject= propertyObject.getJSONObject("neighborhood")
                        val regionName = neighborhoodObject.getString("name")



                        // Create a Hotel object and add it to the list
                        val hotel = Hotel(hotelId,hotelName,regionName, imageUrl, priceInfoFormatted,false,false,reviewScore,reviewTotalCount,stayDuration)
                        hotelList.add(hotel)



                    }


                }


            }catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        return hotelList
    }


}
