package com.example.borrowcircle.app.navigation

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame

class TopLevelDestinationTest {

    @Test
    fun destinationOrderMatchesTheProductNavigation() {
        assertEquals(
            listOf(
                TopLevelDestination.Discover,
                TopLevelDestination.Requests,
                TopLevelDestination.Create,
                TopLevelDestination.Activity,
                TopLevelDestination.Profile,
            ),
            topLevelDestinations,
        )
        assertEquals(TopLevelDestination.Create, topLevelDestinations[2])
        assertEquals(TopLevelDestination.Profile, topLevelDestinations.last())
    }

    @Test
    fun destinationSelectionIsImmutableAndDeterministic() {
        val initialState = BorrowCircleNavigationState()
        val requestsState = initialState.select(TopLevelDestination.Requests)

        assertEquals(TopLevelDestination.Discover, initialState.selectedDestination)
        assertEquals(TopLevelDestination.Requests, requestsState.selectedDestination)
        assertNotSame(initialState, requestsState)
        assertEquals(requestsState, initialState.select(TopLevelDestination.Requests))
        assertEquals(initialState, initialState.select(TopLevelDestination.Create))
    }
}
