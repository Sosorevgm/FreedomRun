package com.freedomrun.features.main.model

data class Result(
    var business_status: String,
    var geometry: Geometry,
    var icon: String,
    var name: String,
    var opening_hours: OpeningHours,
    var photos: List<Photo>,
    var place_id: String,
    var plus_code: PlusCode,
    var price_level: Int,
    var rating: Double,
    var reference: String,
    var scope: String,
    var types: List<String>,
    var user_ratings_total: Int,
    var vicinity: String
)