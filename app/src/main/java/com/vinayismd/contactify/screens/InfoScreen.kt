package com.vinayismd.contactify.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.vinayismd.contactify.MainActivityVM
import com.vinayismd.contactify.R

@Composable
fun InfoScreen(navController: NavHostController, viewModel: MainActivityVM?) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .background(Color.Black),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "back",
                    tint = Color.White,
                    modifier = Modifier
                        .size(48.dp)
                        .padding(8.dp)
                        .clickable {
                            navController.popBackStack()
                        }
                )
            }
            Image(
                painter = painterResource(id = R.drawable.excel),
                contentDescription = "",
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            Spacer(modifier = Modifier.size(16.dp))
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                Text(text = "*  First column contains the first name.")
                Spacer(modifier = Modifier.size(16.dp))
                Text(text = "*  Second column contains the last name or can keep it empty.")
                Spacer(modifier = Modifier.size(16.dp))
                Text(text = "*  Third column only and must contains the Phone Number.")
            }
        }

    }
}