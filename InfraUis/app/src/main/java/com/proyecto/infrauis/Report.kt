package com.proyecto.infrauis

data class Report(
    val name: String = "",
    val description: String = "",
    val location: String = "",
    val imageUrl: String? = "",
    val status: String = "Pendiente"
)
