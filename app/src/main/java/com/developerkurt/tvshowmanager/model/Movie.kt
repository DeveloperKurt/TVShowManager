package com.developerkurt.tvshowmanager.model

import java.util.*

/**
 * Since the auto-generated graphql classes' fields' types can't be changed, this class is
 * required for the releaseDate field to be a formatted string.
 */
data class ShowcaseMovie(val title: String, val releaseDate: String, val seasons: Double?)

/**
 * Original Movie class where the [releaseDate] is not a stored in formatted string
 */
data class Movie(val title: String, val releaseDate: Date?, val seasons: Double?)
