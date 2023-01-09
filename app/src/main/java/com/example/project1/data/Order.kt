package com.example.project1.data

data class Order(
    val Address: Map<String, String> = mapOf(
        "AptNo" to "",
        "PostalCode" to "",
        "Province" to "",
        "Street" to ""
    ),
    val Cart: ArrayList<CartItem> = ArrayList<CartItem>(),
    val Total: Double = 0.0,
    val ID: String = "",
    val Date: Map<String, Any> = mapOf(
        "dayOfMonth" to "",
        "dayOfWeek" to "",
        "month" to "",
        "year" to ""
    )
)


