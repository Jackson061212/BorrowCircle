package com.example.borrowcircle.app.navigation

enum class TopLevelDestination(val label: String) {
    Discover("Discover"),
    Requests("Requests"),
    Create("Create"),
    Activity("Activity"),
    Profile("Profile"),
}

val topLevelDestinations: List<TopLevelDestination> = TopLevelDestination.entries

data class BorrowCircleNavigationState(
    val selectedDestination: TopLevelDestination = TopLevelDestination.Discover,
) {
    fun select(destination: TopLevelDestination): BorrowCircleNavigationState =
        if (destination == TopLevelDestination.Create) this else copy(selectedDestination = destination)
}
