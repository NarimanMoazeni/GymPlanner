package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// =============================================================================
// OFFICIAL COLOR SYSTEM (TACTILE SOFT UI & NEUMORPHISM)
// =============================================================================

// Core Primary & Accent Brands
val ElectricIndigo = Color(0xFF4F46E5)      // Light Primary
val SleekIndigo = Color(0xFF6366F1)         // Dark Primary
val SleekIndigoHover = Color(0xFF4F46E5)
val IndigoPurpleDim = Color(0xFFC0C1FF)
val IndigoContainer = Color(0xFF3131C0)
val OnIndigo = Color(0xFFFFFFFF)
val IndigoPurple = SleekIndigo

// Athletic Success Green
val EmeraldGreen = Color(0xFF10B981)
val KineticGreen = EmeraldGreen             // Backward-compatible alias
val KineticGreenDark = Color(0xFF059669)
val DeepGreen = Color(0xFF064E3B)
val KineticGreenDim = Color(0xFF6EE7B7)
val OnKineticGreen = Color(0xFF022C22)

// Warm Accent (Stopwatch, Alerts, Badges)
val AmberGold = Color(0xFFEAB308)           // Light Accent
val DarkAmberGold = Color(0xFFF59E0B)       // Dark Accent
val OrangeAccent = DarkAmberGold
val OrangeContainer = Color(0xFF78350F)

// Coral / Red Action (Delete button)
val CoralDeleteLight = Color(0xFFDC2626)
val CoralDeleteDark = Color(0xFFEF4444)
val CoralDeleteSurfaceLight = Color(0xFFFEE2E2)
val CoralDeleteSurfaceDark = Color(0xFF381C24)

// -----------------------------------------------------------------------------
// Light Mode Palette
// -----------------------------------------------------------------------------
val LightBackground = Color(0xFFF7F7FB)          // Crisp cool off-white canvas
val LightSurface = Color(0xFFFFFFFF)             // Pure elevated white card
val LightSurfaceInset = Color(0xFFECEEF4)        // Debossed well for digital counters
val LightSurfaceContainerLowest = Color(0xFFECEEF4)
val LightSurfaceContainerLow = Color(0xFFF1F3F9)
val LightSurfaceContainer = Color(0xFFFFFFFF)
val LightSurfaceContainerHigh = Color(0xFFE5E7EB)
val LightSurfaceContainerHighest = Color(0xFFD1D5DB)

val LightTextPrimary = Color(0xFF171717)         // High-contrast Neutral 900
val LightTextSecondary = Color(0xFF6B7280)       // Subtle labels & metadata
val LightOnSurface = LightTextPrimary
val LightOnSurfaceVariant = LightTextSecondary
val LightOnSurfaceDim = Color(0xFF9CA3AF)

val LightOutlineBorder = Color(0xFFE5E7EB)       // Subtle 1dp boundary with alpha
val LightOutlineVariant = Color(0xFFD1D5DB)

// -----------------------------------------------------------------------------
// Dark Mode Palette
// -----------------------------------------------------------------------------
val DarkBackground = Color(0xFF0E1116)           // Deep charcoal
val DarkSurface = Color(0xFF151E28)              // Elevated slate container
val DarkSurfaceInset = Color(0xFF0B0F15)         // Debossed dark wells
val DarkSurfaceContainerLowest = Color(0xFF0B0F15)
val DarkSurfaceContainerLow = Color(0xFF0F161F)
val DarkSurfaceContainer = Color(0xFF151E28)
val DarkSurfaceContainerHigh = Color(0xFF1C2735)
val DarkSurfaceContainerHighest = Color(0xFF253446)

val DarkTextPrimary = Color(0xFFF8FAFC)
val DarkTextSecondary = Color(0xFF94A3B8)
val DarkOnSurface = DarkTextPrimary
val DarkOnSurfaceVariant = DarkTextSecondary
val DarkOnSurfaceDim = Color(0xFF64748B)

val DarkOutlineBorder = Color(0xFF1E2C3A)
val DarkOutlineVariant = Color(0xFF2A3C4E)
