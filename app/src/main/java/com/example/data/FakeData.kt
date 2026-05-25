package com.example.data

import com.example.model.CallDirection
import com.example.model.CallHistoryItem
import com.example.model.Contact
import com.example.model.RecentCall

object FakeData {
    val contacts = listOf(
        Contact(
            id = "1",
            name = "Carl Pei",
            subtitle = "London OFFICE",
            phone = "+44 7911 123456",
            image = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=300",
            favorite = true,
            redAccent = true
        ),
        Contact(
            id = "2",
            name = "Akis Evangelidis",
            subtitle = "Nothing Co-founder",
            phone = "+44 7911 987654",
            image = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=300",
            favorite = true
        ),
        Contact(
            id = "3",
            name = "Blobby Discord",
            subtitle = "Nothing Community",
            phone = "+44 1234 567890",
            image = "",
            favorite = true
        ),
        Contact(
            id = "4",
            name = "Rob Godwin",
            subtitle = "Nothing Team",
            phone = "+44 555 1234",
            image = "https://images.unsplash.com/photo-1519085360753-af0119f7cbe7?w=300",
            favorite = true
        ),
        Contact(
            id = "5",
            name = "Adbo CBO Nothing",
            subtitle = "Nothing Executive",
            phone = "+44 555 9999",
            image = "https://images.unsplash.com/photo-1560250097-0b93528c311a?w=300",
            favorite = true
        ),
        Contact(
            id = "6",
            name = "Adam Zampa",
            subtitle = "Nothing Spinner",
            phone = "+61 400 123 456",
            image = "",
            favorite = true
        ),
        Contact(
            id = "7",
            name = "Vivek Bhimani",
            subtitle = "Nothing Community",
            phone = "+91 98765 43210",
            secondaryPhone = "+91 12345 67890",
            image = "https://images.unsplash.com/photo-1620121692029-d088224ddc74?w=300", // space jupiter texture pattern
            favorite = false
        ),
        Contact(
            id = "8",
            name = "Sticky",
            subtitle = "Mobile",
            phone = "+91 98765 43210",
            image = "https://images.unsplash.com/photo-1607990283143-e81e7a2c93ab?w=300",
            favorite = false
        ),
        Contact(
            id = "9",
            name = "Max",
            subtitle = "Mobile",
            phone = "+91 12345 67890",
            image = "",
            favorite = false,
            redAccent = true
        ),
        Contact(
            id = "10",
            name = "Maxishappy",
            subtitle = "Mobile",
            phone = "+91 98765 43210",
            image = "https://images.unsplash.com/photo-1618077360395-f3068be8e001?w=300",
            favorite = false
        ),
        Contact(
            id = "11",
            name = "Aarav",
            subtitle = "Mobile",
            phone = "+91 88888 77777",
            image = "https://images.unsplash.com/photo-1542103749-8ef59b94f47e?w=300",
            favorite = false
        ),
        Contact(
            id = "12",
            name = "Creep Smile",
            subtitle = "Mobile",
            phone = "+91 12345 67890",
            image = "https://images.unsplash.com/photo-1614680376593-902f74fa0d41?w=300",
            favorite = false,
            redAccent = true
        ),
        Contact(
            id = "13",
            name = "Parth",
            subtitle = "Mobile",
            phone = "+91 99999 00000",
            image = "",
            favorite = false
        ),
        Contact(
            id = "14",
            name = "Pocket",
            subtitle = "Mobile",
            phone = "+91 88888 00000",
            image = "https://images.unsplash.com/photo-1566492031773-4f4e44671857?w=300",
            favorite = false
        ),
        Contact(
            id = "15",
            name = "Blobby",
            subtitle = "Mobile",
            phone = "+91 77777 00000",
            image = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=300",
            favorite = false
        )
    )

    val recents = listOf(
        RecentCall(
            id = "r1",
            contact = contacts[7], // Sticky
            direction = CallDirection.OUTGOING,
            carrierTime = "Mobile, 01:10 pm, Jio"
        ),
        RecentCall(
            id = "r2",
            contact = contacts[8], // Max (Missed)
            direction = CallDirection.MISSED,
            carrierTime = "Mobile, Yesterday, 01:00 pm, Jio"
        ),
        RecentCall(
            id = "r3",
            contact = contacts[9], // Maxishappy
            direction = CallDirection.OUTGOING,
            carrierTime = "Mobile, Yesterday, 01:10 pm, Jio"
        ),
        RecentCall(
            id = "r4",
            contact = contacts[10], // Aarav
            direction = CallDirection.OUTGOING,
            carrierTime = "Mobile, Yesterday, Airtel"
        ),
        RecentCall(
            id = "r5",
            contact = contacts[11], // Creep Smile (Missed)
            direction = CallDirection.MISSED,
            carrierTime = "Mobile, Yesterday, Jio"
        ),
        RecentCall(
            id = "r6",
            contact = contacts[12], // Parth
            direction = CallDirection.OUTGOING,
            carrierTime = "Mobile, 25 Dec, 01:00 pm, Airtel"
        ),
        RecentCall(
            id = "r7",
            contact = contacts[13], // Pocket
            direction = CallDirection.INCOMING,
            carrierTime = "Mobile, 6 Oct, Jio"
        ),
        RecentCall(
            id = "r8",
            contact = contacts[14], // Blobby
            direction = CallDirection.INCOMING,
            carrierTime = "Mobile, 12 Oct, Jio"
        )
    )

    fun getHistoryForContact(contact: Contact): List<CallHistoryItem> {
        if (contact.id == "7" || contact.name == "Vivek Bhimani") {
            // Match Image 1 HISTORY section perfectly:
            // 1. Today 01:10 pm | Outgoing ↗ | duration 30s | subtitle +91 98765 43210
            // 2. Today 01:10 pm | Missed ↙ (red) | subtitle +91 12345 67890
            // 3. Today 01:10 pm | Incoming ↙ | duration 8m 28s | subtitle +91 98765 43210
            return listOf(
                CallHistoryItem(
                    id = "h1",
                    direction = CallDirection.OUTGOING,
                    time = "Today 01:10 pm",
                    number = "+91 98765 43210",
                    duration = "30s"
                ),
                CallHistoryItem(
                    id = "h2",
                    direction = CallDirection.MISSED,
                    time = "Today 01:10 pm",
                    number = "+91 12345 67890",
                    duration = "" // Empty indicates missed call (or duration omitted in image 1)
                ),
                CallHistoryItem(
                    id = "h3",
                    direction = CallDirection.INCOMING,
                    time = "Today 01:10 pm",
                    number = "+91 98765 43210",
                    duration = "8m 28s"
                )
            )
        }
        return listOf(
            CallHistoryItem(
                id = "${contact.id}_h1",
                direction = CallDirection.INCOMING,
                time = "Yesterday 11:24 am",
                number = contact.phone,
                duration = "1m 15s"
            ),
            CallHistoryItem(
                id = "${contact.id}_h2",
                direction = CallDirection.MISSED,
                time = "2 days ago",
                number = contact.phone,
                duration = ""
            )
        )
    }
}
