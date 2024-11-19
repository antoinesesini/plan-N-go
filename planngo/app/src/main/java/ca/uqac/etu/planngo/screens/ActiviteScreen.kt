package ca.uqac.etu.planngo.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ca.uqac.etu.planngo.R
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.util.lerp
import androidx.lifecycle.viewmodel.compose.viewModel
import ca.uqac.etu.planngo.models.Activity
import ca.uqac.etu.planngo.viewmodel.ActivityViewModel
import coil.compose.rememberAsyncImagePainter
import kotlin.math.absoluteValue
import androidx.compose.runtime.*
import kotlin.Int

@Composable
fun ActiviteScreen() {
    val activityViewModel: ActivityViewModel = viewModel()

    var selectedCategory by remember { mutableStateOf("Tous") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 2.dp)
    ) {

        Text(
            text = "Activités",
            fontSize = 27.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 30.dp, start = 10.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        PromotionBanner()
        Spacer(modifier = Modifier.height(8.dp))
        ActivityCategory(activityViewModel, selectedCategory) { category ->
            selectedCategory = category
        }
        val activities = if (selectedCategory == "Tous") {
            activityViewModel.getActivities()
        } else {
            activityViewModel.getActivities().filter { it.type.toString() == selectedCategory }
        }
        ActivityCarrousel(activities ?: emptyList())
    }
}

@Composable
fun PromotionBanner() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(120.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Texte à gauche
            Column(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .weight(2f)
            ) {
                Text(
                    text = "Planifiez votre prochaine activité",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Découvrez des idées et rejoignez-nous !",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            // Image à droite
            Image(
                painter = painterResource(id = R.drawable.parc),
                contentDescription = "parc",
                modifier = Modifier
                    .size(75.dp)
                    .weight(1f),
//                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
fun ActivityCategory(
    activityViewModel: ActivityViewModel,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    val types = activityViewModel.getActivityTypes() ?: emptyList()

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(horizontal = 8.dp),
    ) {
        // Bouton "Tous"
        item {
            CategoryButton(
                text = "Tous",
                icon = painterResource(id = R.drawable.custom_logo),
                isSelected = selectedCategory == "Tous",
                onClick = { onCategorySelected("Tous") }
            )
        }
        // Boutons pour chaque catégorie
        items(types.size) { index ->
            val category = types[index]
            val iconPainter = painterResource(id = getIconForType(category))
            CategoryButton(
                text = category.toString(),
                icon = iconPainter,
                isSelected = selectedCategory == category.toString(),
                onClick = { onCategorySelected(category.toString()) }
            )
        }
    }
}

@Composable
fun CategoryButton(text: String, icon: Painter, isSelected: Boolean, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .width(80.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = onClick,
            modifier = Modifier
                .size(70.dp),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isSelected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.primary
            ),
            contentPadding = PaddingValues(0.dp)
        ) {
            Icon(
                painter = icon,
                contentDescription = text,
                modifier = Modifier.size(36.dp),
                tint = if (isSelected) Color.White else MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = text,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 1,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
fun ActivityCarrousel(activities: List<Activity>) {
    if (activities.isNotEmpty()) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val pagerState = rememberPagerState(initialPage = 0) {
                activities.size
            }
            HorizontalPager(
                state = pagerState,
                contentPadding = PaddingValues(
                    top = 15.dp,
                    bottom = 20.dp,
                    start = 60.dp,
                    end = 60.dp)
            ) { index ->
                ActivityCard(index, pagerState, activities[index])
            }
        }
    } else {
        Text(
            text = "Aucune activité disponible.",
            fontSize = 18.sp,
            color = Color.Gray
        )
    }
}


@Composable
fun ActivityCard(index: Int, pagerState: PagerState, activity: Activity) {
    val pageOffset = (pagerState.currentPage - index) + pagerState.currentPageOffsetFraction

    Card(
        modifier = Modifier
            .padding(8.dp)
            .graphicsLayer {
                lerp(
                    start = 0.85f,
                    stop = 1f,
                    fraction = 1f - pageOffset.coerceIn(0f, 1f)
                ).also { scale ->
                    scaleX = scale.absoluteValue
                    scaleY = scale.absoluteValue
                }
            }
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column {
            // Image principale
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                Image(
                    painter = rememberAsyncImagePainter(model = activity.pictures.firstOrNull() ?: ""),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                IconButton(
                    onClick = { /* Action pour le bouton favori */ },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                            shape = CircleShape
                        )
                        .size(40.dp)
                ) {
                    Icon(
                        painter = painterResource(id = getIconForType(activity.type)),
                        contentDescription = "typeActivity",
                        tint = Color.White
                    )
                }
            }

            // Contenu texte
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = activity.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Text(
                    text = activity.description,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(vertical = 4.dp),
                    maxLines = 2
                )

                // Horaires et durée
                Text(
                    text = "Horaires : ${activity.hours["start"]} - ${activity.hours["end"]}",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                Text(
                    text = "Durée : ${activity.duration} mins",
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { /* Action pour partager */ },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = Color.White
                        ),
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Share,
                            contentDescription = "Share Icon",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = "Share")
                    }

                }
            }
        }
    }
}

