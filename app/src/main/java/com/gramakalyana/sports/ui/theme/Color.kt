package com.gramakalyana.sports.ui.theme

import androidx.compose.ui.graphics.Color

// Brand Colors from Screenshot: Orange, Black, White
val AppOrange = Color(0xFFF57C00)      // Primary Orange
val AppBlack = Color(0xFF121212)       // Dark Background
val AppWhite = Color(0xFFFFFFFF)       // Text and Icons

// Theme Colors - Using only the 3 brand colors
val md_theme_dark_primary = AppOrange
val md_theme_dark_onPrimary = AppBlack
val md_theme_dark_primaryContainer = AppOrange.copy(alpha = 0.2f)
val md_theme_dark_onPrimaryContainer = AppOrange

val md_theme_dark_secondary = AppOrange
val md_theme_dark_onSecondary = AppBlack
val md_theme_dark_secondaryContainer = Color(0xFF2C2C2C) // Dark Grey for contrast (still in the "Black" family)
val md_theme_dark_onSecondaryContainer = AppWhite

val md_theme_dark_tertiary = AppOrange
val md_theme_dark_onTertiary = AppBlack

val md_theme_dark_background = AppBlack
val md_theme_dark_onBackground = AppWhite
val md_theme_dark_surface = AppBlack
val md_theme_dark_onSurface = AppWhite
val md_theme_dark_surfaceVariant = Color(0xFF2C2C2C) // Card background
val md_theme_dark_onSurfaceVariant = AppWhite.copy(alpha = 0.7f)
val md_theme_dark_outline = AppOrange
val md_theme_dark_error = AppOrange
val md_theme_dark_onError = AppBlack

// Light Theme (Mapped to dark for total consistency as the app follows a dark theme brand)
val md_theme_light_primary = AppOrange
val md_theme_light_onPrimary = AppBlack
val md_theme_light_primaryContainer = AppOrange.copy(alpha = 0.2f)
val md_theme_light_onPrimaryContainer = AppOrange
val md_theme_light_secondary = AppOrange
val md_theme_light_onSecondary = AppBlack
val md_theme_light_secondaryContainer = Color(0xFF2C2C2C)
val md_theme_light_onSecondaryContainer = AppWhite

val md_theme_light_tertiary = AppOrange
val md_theme_light_onTertiary = AppBlack

val md_theme_light_background = AppBlack
val md_theme_light_onBackground = AppWhite
val md_theme_light_surface = AppBlack
val md_theme_light_onSurface = AppWhite
val md_theme_light_surfaceVariant = Color(0xFF2C2C2C)
val md_theme_light_onSurfaceVariant = AppWhite.copy(alpha = 0.7f)
val md_theme_light_outline = AppOrange
val md_theme_light_error = AppOrange
val md_theme_light_onError = AppBlack
