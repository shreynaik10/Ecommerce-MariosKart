package com.example.project1.data

data class User(
    val FirstName: String ="",
    val LastName:String ="" ,
    val Email:String ="",
    val PhoneNumber:String ="",
    val Address: Map<String, String> = mapOf(
        "AptNo" to "",
        "PostalCode" to "",
        "Province" to "",
        "Street" to ""
    )
)
