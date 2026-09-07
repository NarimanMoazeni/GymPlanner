package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.example.data.local.entity.ActivityEntity
import com.example.ui.locale.LocalAppStrings

@Composable
fun DeleteConfirmDialog(
    activity: ActivityEntity,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    val strings = LocalAppStrings.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
                Text(text = strings.deleteActivityConfirm)
            }
        },
        text = {
            Text(
                text = "آیا از حذف حرکت «${activity.name}» مطمئن هستید؟ این عمل غیرقابل بازگشت است.",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                )
            ) {
                Text(text = strings.deleteBtn)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = strings.cancelBtn)
            }
        }
    )
}
