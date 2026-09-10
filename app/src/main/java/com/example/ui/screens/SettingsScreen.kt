package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SettingsBrightness
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.AppPreferences
import com.example.data.local.entity.UserProfileEntity
import com.example.data.model.WeightUnit
import com.example.ui.components.FramedSectionHeader
import com.example.ui.locale.AppLanguage
import com.example.ui.locale.LocalAppStrings
import com.example.ui.locale.LocalCurrentLanguage
import com.example.ui.locale.formatDigits
import com.example.ui.theme.AmberGold
import com.example.ui.theme.ElectricIndigo
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.KineticGreen
import com.example.ui.theme.ThemeMode
import com.example.ui.theme.tactileButton
import com.example.ui.theme.tactileCard
import com.example.ui.theme.tactileInset

@Composable
fun SettingsScreen(
    profile: UserProfileEntity,
    preferences: AppPreferences,
    onUpdateProfile: (name: String, age: Int?) -> Unit,
    onSetThemeMode: (ThemeMode) -> Unit,
    onSetLanguage: (AppLanguage) -> Unit,
    onSetWeightUnit: (WeightUnit) -> Unit,
    onSetReminder: (Boolean) -> Unit,
    onExportBackup: ((String) -> Unit) -> Unit,
    onImportBackup: (String, (Boolean) -> Unit) -> Unit,
    onResetToFreshInstall: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val language = LocalCurrentLanguage.current
    val clipboardManager: ClipboardManager = LocalClipboardManager.current
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val accentColor = if (isDark) EmeraldGreen else ElectricIndigo

    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }
    var showImportDialog by remember { mutableStateOf(false) }
    var showResetConfirmDialog by remember { mutableStateOf(false) }
    var exportedJsonText by remember { mutableStateOf("") }
    var importJsonInput by remember { mutableStateOf("") }
    var importStatusMessage by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            // Header
            Column {
                Text(
                    text = strings.settingsTitle,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    softWrap = false
                )
                Text(
                    text = strings.settingsSubtitle,
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // 1. Profile Bento Card with avatar gradient rim
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .tactileCard(shape = RoundedCornerShape(22.dp), elevation = 4.dp)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Avatar with vibrant dual gradient rim
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.sweepGradient(
                                        listOf(accentColor, AmberGold, accentColor)
                                    )
                                )
                                .padding(2.5.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surface),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = profile.name.take(1).ifEmpty { "U" },
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Black
                                    ),
                                    color = accentColor
                                )
                            }
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = profile.name,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Black
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            val ageText = profile.age?.let { "${formatDigits(it, language)} سال" } ?: "بدون ثبت سن"
                            Text(
                                text = "$ageText • برنامه هفتگی شخصی",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Raised Tactile Edit Button
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .tactileButton(
                                onClick = { showEditProfileDialog = true },
                                shape = RoundedCornerShape(12.dp),
                                baseColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                                lipColor = MaterialTheme.colorScheme.surfaceContainerLowest
                            )
                            .testTag("btn_edit_profile"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = strings.editBtn,
                            tint = accentColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // 2. Theme & Appearance Bento Card
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .tactileCard(shape = RoundedCornerShape(22.dp), elevation = 4.dp)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                FramedSectionHeader(
                    title = strings.appearanceSection,
                    badgeText = strings.settingsAppearanceBadge
                )

                // Theme Mode 3-way Segmented Pill Toggle with High-Visibility Selection Feedback
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = strings.themeLabel,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .tactileInset(shape = RoundedCornerShape(14.dp))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            Triple(ThemeMode.DARK, strings.themeDark, Icons.Default.DarkMode),
                            Triple(ThemeMode.LIGHT, strings.themeLight, Icons.Default.LightMode),
                            Triple(ThemeMode.SYSTEM, strings.themeSystem, Icons.Default.SettingsBrightness)
                        ).forEach { (mode, label, icon) ->
                            val isSelected = preferences.themeMode == mode
                            val btnLip = if (isDark) Color(0xFF064E3B) else Color(0xFF312E81)
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(42.dp)
                                    .then(
                                        if (isSelected) {
                                            Modifier.tactileButton(
                                                onClick = { onSetThemeMode(mode) },
                                                shape = RoundedCornerShape(10.dp),
                                                baseColor = accentColor,
                                                lipColor = btnLip,
                                                borderColor = accentColor.copy(alpha = 0.5f)
                                            )
                                        } else {
                                            Modifier
                                                .clip(RoundedCornerShape(10.dp))
                                                .clickable { onSetThemeMode(mode) }
                                        }
                                    )
                                    .testTag("theme_${mode.name.lowercase()}"),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = null,
                                        tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium
                                        ),
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 3. Preferences Bento Card (Language, Weight Unit) - Reminder Removed
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .tactileCard(shape = RoundedCornerShape(22.dp), elevation = 4.dp)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                FramedSectionHeader(
                    title = strings.settingsPracticalSection,
                    badgeText = strings.settingsPracticalBadge
                )

                // Language Segmented Selector with High-Visibility Selection Feedback
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = strings.languageLabel,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .tactileInset(shape = RoundedCornerShape(14.dp))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            Pair(AppLanguage.PERSIAN, strings.langPersian),
                            Pair(AppLanguage.ENGLISH, strings.langEnglish)
                        ).forEach { pair ->
                            val lang = pair.first
                            val label = pair.second
                            val isSelected = preferences.language == lang
                            val btnLip = if (isDark) Color(0xFF064E3B) else Color(0xFF312E81)
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(42.dp)
                                    .then(
                                        if (isSelected) {
                                            Modifier.tactileButton(
                                                onClick = { onSetLanguage(lang) },
                                                shape = RoundedCornerShape(10.dp),
                                                baseColor = accentColor,
                                                lipColor = btnLip,
                                                borderColor = accentColor.copy(alpha = 0.5f)
                                            )
                                        } else {
                                            Modifier
                                                .clip(RoundedCornerShape(10.dp))
                                                .clickable { onSetLanguage(lang) }
                                        }
                                    )
                                    .testTag("lang_${lang.name.lowercase()}"),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium
                                        ),
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))

                // Weight Unit Segmented Selector with High-Visibility Selection Feedback
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = strings.weightUnitLabel,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .tactileInset(shape = RoundedCornerShape(14.dp))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            Pair(WeightUnit.KG, strings.unitKgOption),
                            Pair(WeightUnit.LBS, strings.unitLbsOption)
                        ).forEach { pair ->
                            val unit = pair.first
                            val label = pair.second
                            val isSelected = preferences.weightUnit == unit
                            val btnLip = if (isDark) Color(0xFF064E3B) else Color(0xFF312E81)
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(42.dp)
                                    .then(
                                        if (isSelected) {
                                            Modifier.tactileButton(
                                                onClick = { onSetWeightUnit(unit) },
                                                shape = RoundedCornerShape(10.dp),
                                                baseColor = accentColor,
                                                lipColor = btnLip,
                                                borderColor = accentColor.copy(alpha = 0.5f)
                                            )
                                        } else {
                                            Modifier
                                                .clip(RoundedCornerShape(10.dp))
                                                .clickable { onSetWeightUnit(unit) }
                                        }
                                    )
                                    .testTag("weight_unit_${unit.name.lowercase()}"),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium
                                        ),
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 4. Data Management Bento Card
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .tactileCard(shape = RoundedCornerShape(22.dp), elevation = 4.dp)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                FramedSectionHeader(
                    title = strings.backupSection,
                    badgeText = "داده‌ها و پشتیبان"
                )

                Text(
                    text = strings.backupDesc,
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp
                )

                // Export Backup Tactile Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .tactileButton(
                            onClick = {
                                onExportBackup { json ->
                                    exportedJsonText = json
                                    showExportDialog = true
                                }
                            },
                            shape = RoundedCornerShape(14.dp),
                            baseColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                            lipColor = MaterialTheme.colorScheme.surfaceContainerLowest
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.CloudDownload, contentDescription = null, tint = accentColor)
                        Text(
                            text = strings.backupExportBtn,
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))

                // Import Backup Tactile Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .tactileButton(
                            onClick = {
                                importJsonInput = ""
                                importStatusMessage = null
                                showImportDialog = true
                            },
                            shape = RoundedCornerShape(14.dp),
                            baseColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                            lipColor = MaterialTheme.colorScheme.surfaceContainerLowest
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.CloudUpload, contentDescription = null, tint = accentColor)
                        Text(
                            text = strings.backupImportBtn,
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // 5. Security & Offline Guarantee Info
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .tactileInset(shape = RoundedCornerShape(18.dp))
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(accentColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = strings.offlineTrustBadge,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "تمامی اطلاعات، تمرینات، وزنه‌ها و جلسات شما به صورت کاملاً امن فقط در حافظه گوشی ذخیره شده و به هیچ سروری منتقل نمی‌شود.",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Dialog: Edit Profile
    if (showEditProfileDialog) {
        var tempName by remember { mutableStateOf(profile.name) }
        var tempAgeStr by remember { mutableStateOf(profile.age?.toString() ?: "") }

        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            title = {
                Text(
                    text = strings.editBtn,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = tempName,
                        onValueChange = { tempName = it },
                        label = { Text("نام") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = tempAgeStr,
                        onValueChange = { tempAgeStr = it },
                        label = { Text("سن") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUpdateProfile(tempName, tempAgeStr.toIntOrNull())
                        showEditProfileDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = accentColor,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(strings.saveToPlanBtn, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileDialog = false }) {
                    Text(strings.cancelBtn)
                }
            }
        )
    }

    // Dialog: Export Backup
    if (showExportDialog) {
        AlertDialog(
            onDismissRequest = { showExportDialog = false },
            title = {
                Text(
                    text = strings.backupExportBtn,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "کد پشتیبان شما آماده است. می‌توانید آن را کپی کرده و در محل امنی نگهداری کنید:",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
                            .padding(10.dp)
                    ) {
                        Text(
                            text = exportedJsonText,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(exportedJsonText))
                        showExportDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = accentColor,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("کپی در کلیپ‌بورد", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showExportDialog = false }) {
                    Text(strings.cancelBtn)
                }
            }
        )
    }

    // Dialog: Import Backup
    if (showImportDialog) {
        AlertDialog(
            onDismissRequest = { showImportDialog = false },
            title = {
                Text(
                    text = strings.backupImportBtn,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "متن فایل پشتیبان (JSON) را در کادر زیر وارد کنید:",
                        style = MaterialTheme.typography.bodySmall
                    )
                    OutlinedTextField(
                        value = importJsonInput,
                        onValueChange = {
                            importJsonInput = it
                            importStatusMessage = null
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp),
                        placeholder = { Text("{\n  \"version\": 1,\n  ...") },
                        shape = RoundedCornerShape(12.dp),
                        textStyle = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace)
                    )

                    importStatusMessage?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (importJsonInput.isBlank()) {
                            importStatusMessage = "لطفاً متن فایل پشتیبان را وارد کنید"
                        } else {
                            onImportBackup(importJsonInput) { success ->
                                if (success) {
                                    showImportDialog = false
                                } else {
                                    importStatusMessage = "ساختار فایل پشتیبان نامعتبر است"
                                }
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = accentColor,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("بازیابی برنامه", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showImportDialog = false }) {
                    Text(strings.cancelBtn)
                }
            }
        )
    }
}
