package com.example.model

enum class CallDirection {
    INCOMING,
    OUTGOING,
    MISSED
}

data class Contact(
    val id: String,
    val name: String,
    val subtitle: String,
    val phone: String,
    val image: String,
    val favorite: Boolean,
    val redAccent: Boolean = false,
    val secondaryPhone: String? = null
)

data class RecentCall(
    val id: String,
    val contact: Contact,
    val direction: CallDirection,
    val carrierTime: String
)

data class CallHistoryItem(
    val id: String,
    val direction: CallDirection,
    val time: String,
    val number: String,
    val duration: String
)
