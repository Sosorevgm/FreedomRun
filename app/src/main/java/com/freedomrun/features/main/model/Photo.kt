package com.freedomrun.features.main.model

data class Photo(
    var height: Int,
    var html_attributions: List<String>,
    var photo_reference: String,
    var width: Int
)