package com.vinayismd.contactify.screens

import android.Manifest
import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.vinayismd.contactify.*
import com.vinayismd.contactify.MainActivityVM.Companion.FILETYPE_CSV
import com.vinayismd.contactify.MainActivityVM.Companion.FILETYPE_XLS
import com.vinayismd.contactify.MainActivityVM.Companion.FILETYPE_XLSX
import com.vinayismd.contactify.R
import com.vinayismd.contactify.component.GradientButton
import com.vinayismd.contactify.component.InputDialogView
import com.vinayismd.contactify.ui.theme.ContactifyTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.apache.poi.ss.usermodel.*


val gradientColors = listOf(Color(0xFF7421D8), Color(0xFFdc2430))

@Composable
fun MainScreen(navController: NavHostController, viewModel: MainActivityVM?) {
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) {
        val context = LocalContext.current
        MainUI(context, navController, viewModel)
    }
}

@Composable
fun MainUI(context: Context, navController: NavHostController, viewModel: MainActivityVM?) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color.Black),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.app_name),
                    modifier = Modifier.padding(16.dp),
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.W500
                    )
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color.Transparent, shape = RoundedCornerShape(20.dp)),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(R.drawable.tocontact),
                    contentDescription = "Content description"
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
                    .background(
                        Color(0xf2dc2740), shape = RoundedCornerShape(4.dp)
                    )
                    .padding(vertical = 8.dp, horizontal = 8.dp)
                    .clickable {
                        navController.navigate(Screens.InfoScreen.route)
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.Info, "")
                Text(
                    modifier = Modifier.padding(start = 12.dp),
                    text = "Click here to know the Format of Excel",
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
            SelectFileUI(context = context, viewModel)
            Spacer(modifier = Modifier.weight(1f))
            if (viewModel?.uiState?.showList == true) {
                GradientButton(
                    modifier = Modifier
                        .padding(16.dp),
                    text = "Show Contact List  >>",
                    gradientColors = gradientColors,
                    cornerRadius = 24.dp,
                    onclick = {
                        navController.navigate(Screens.ContactScreen.route)
                    }
                )
                LaunchedEffect(key1 = viewModel?.uiState?.showList, block = {
                    if (viewModel?.uiState?.showList) {
                        showToast(context, "Contacts List Generated")
                    }
                })
            }

            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF7421D8)),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val textWithHeart = decodeHtmlEntities("Made with &#10084; by Ankita")
                val annotatedString = buildAnnotatedString {
                    append(textWithHeart)
                }
                Text(text = annotatedString, style = TextStyle())
            }
        }
    }
}

@Composable
fun SelectFileUI(context: Context, viewModel: MainActivityVM?) {

    val selectFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = {
            if (it != null) {
                val contentResolver: ContentResolver = context.contentResolver
                val extension = contentResolver.getType(it)
                when {
                    extension.equals(FILETYPE_XLS) -> {
                        copy1(context, it, "temp.xls")
                        val file = getFile(context = context, "temp.xls")
                        viewModel?.handleAction(
                            MainActivityVM.UIAction.SaveList(
                                FILETYPE_XLS,
                                file
                            )
                        )
                    }
                    extension.equals(FILETYPE_XLSX) -> {
                        try {
                            copy1(context, it, "test.xlsx")
                            val file = getFile(context = context, "test.xlsx")
                            viewModel?.handleAction(
                                MainActivityVM.UIAction.SaveList(
                                    FILETYPE_XLSX,
                                    file
                                )
                            )
                        } catch (e: java.lang.Exception) {
                            Log.d("VINAY", "FILETYPE_XLSXFILETYPE_XLSX")
                        }
                    }
                    else -> {
                        showToast(context, "Wrong File Type")
                    }
                }
            }

        })

    var shouldRequestPermission by remember { mutableStateOf(false) }

    val requestWriteContactsPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                selectFileLauncher.launch(
                    arrayOf(FILETYPE_XLS, FILETYPE_XLSX, FILETYPE_CSV)
                )
            } else {
                shouldRequestPermission = true
                showToast(context, "Provide Access to Contacts for Saving")
            }
        }

    if (shouldRequestPermission) {
        RequestWriteContactsPermission(requestWriteContactsPermissionLauncher)
        shouldRequestPermission = false
    }

    GradientButton(
        text = "Select File",
        gradientColors = gradientColors,
        cornerRadius = 24.dp,
        onclick = {
            if (checkWriteContactsPermission(context = context)) {
                selectFileLauncher.launch(
                    arrayOf(FILETYPE_XLS, FILETYPE_XLSX, FILETYPE_CSV)
                )
            } else {
                shouldRequestPermission = true
            }
        }
    )
}


fun showToast(context: Context, msg: String) {
    CoroutineScope(Dispatchers.Main).launch {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }
}

@Composable
fun RequestWriteContactsPermission(
    permissionRequester: ActivityResultLauncher<String>
) {
    LaunchedEffect(true) {
        permissionRequester.launch(Manifest.permission.WRITE_CONTACTS)
    }
}

fun checkWriteContactsPermission(context: Context): Boolean {
    val permission = Manifest.permission.WRITE_CONTACTS
    val granted = PackageManager.PERMISSION_GRANTED
    return ContextCompat.checkSelfPermission(context, permission) == granted
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    val navController = rememberNavController()
    ContactifyTheme {
        MainScreen(navController, hiltViewModel())
    }
}
