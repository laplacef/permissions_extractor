package com.example.android_permissions

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import com.example.android_permissions.ui.theme.android_permissionsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            android_permissionsTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    AppPermissionsList(this)
                }
            }
        }
    }
}

@Composable
fun AppPermissionsList(activity: ComponentActivity) {
    val appDetails = remember { getAppDetails(activity) }
    LazyColumn {
        items(appDetails) { appDetail ->
            AppItem(appDetail)
        }
    }
}

@Composable
fun AppItem(appDetail: AppDetail) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = appDetail.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        appDetail.permissions.forEach { permission ->
            Text(
                text = formatPermission(permission),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(start = 8.dp, top = 2.dp)
            )
        }
    }
}

fun formatPermission(permission: String): String {
    return permission.substringAfterLast('.')
}

fun getAppDetails(activity: ComponentActivity): List<AppDetail> {
    val packageManager = activity.packageManager
    val installedApps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
    val appPermissionsList = mutableListOf<AppDetail>()

    for (app in installedApps) {
        val appName = app.loadLabel(packageManager).toString()
        val packageInfo = packageManager.getPackageInfo(app.packageName, PackageManager.GET_PERMISSIONS)
        // Sort the permissions in alphabetical order after formatting
        val appPermissions = packageInfo.requestedPermissions
            ?.map { formatPermission(it) }
            ?.sorted() // Add this line to sort the permissions
            ?: listOf()

        // Only add the app to the list if it has permissions
        if (appPermissions.isNotEmpty()) {
            appPermissionsList.add(AppDetail(appName, app.packageName, appPermissions))
        }
    }
    return appPermissionsList.sortedBy { it.name } // Optionally, sort apps by name
}



data class AppDetail(val name: String, val packageName: String, val permissions: List<String>)
