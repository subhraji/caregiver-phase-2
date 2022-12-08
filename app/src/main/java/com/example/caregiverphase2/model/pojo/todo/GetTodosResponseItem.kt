package com.example.caregiverphase2.model.pojo.todo

data class GetTodosResponseItem(
    val completed: Boolean,
    val id: Int,
    val title: String,
    val userId: Int
)