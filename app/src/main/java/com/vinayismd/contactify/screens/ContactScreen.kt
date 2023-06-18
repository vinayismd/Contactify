package com.vinayismd.contactify.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.vinayismd.contactify.ContactUtils
import com.vinayismd.contactify.MainActivityVM
import com.vinayismd.contactify.R
import com.vinayismd.contactify.component.GradientButton

@Composable
fun ContactScreen(navController: NavHostController, viewModel: MainActivityVM?) {

    LaunchedEffect(key1 = viewModel?.uiState?.showList, block = {
        if (viewModel?.uiState?.showList == true) {
            viewModel.hideShowList()
        }
    })

    Box(modifier = Modifier.fillMaxSize()) {
        val context = LocalContext.current
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
        ) {
            item {
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
                        contentDescription = "Favorite",
                        tint = Color.White,
                        modifier = Modifier
                            .size(48.dp)
                            .padding(8.dp).clickable {
                                navController.popBackStack()
                            }
                    )
                }
            }

            items(viewModel?.uiState?.contactList.orEmpty()) { contacts ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .background(Color.White, shape = RoundedCornerShape(8.dp)),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.padding(vertical = 4.dp, horizontal = 16.dp)) {
                        Text(
                            modifier = Modifier
                                .padding(vertical = 4.dp),
                            text = "Name : " + contacts.firstName + " " + contacts.lastName,
                            style = TextStyle(
                                color = Color(0xFF7421D8),
                                fontWeight = FontWeight.ExtraBold
                            )
                        )
                        Text(
                            modifier = Modifier
                                .padding(vertical = 4.dp),
                            text = "Phone No. : " + contacts.phnNumber,
                            style = TextStyle(
                                color = Color(0xFFdc2430),
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                    Image(
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(24.dp)
                            .clickable {
                                viewModel?.removeElement(contacts)
                            },
                        painter = painterResource(R.drawable.delete),
                        contentDescription = "delete contact"
                    )

                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
        GradientButton(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            text = "Export to Contacts >>",
            gradientColors = gradientColors,
            cornerRadius = 24.dp,
            onclick = {
                if (ContactUtils.saveContacts(
                        context.contentResolver,
                        ArrayList(viewModel?.uiState?.contactList.orEmpty())
                    )
                ) {
                    showToast(context, "Contacts Saved Successfully")
                    navController.popBackStack()
                }
            }
        )
    }
}