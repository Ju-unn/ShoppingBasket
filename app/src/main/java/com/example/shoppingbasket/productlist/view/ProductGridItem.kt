package com.example.shoppingbasket.productlist.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.example.shoppingbasket.R
import com.example.shoppingbasket.productlist.model.Product

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ProductGridItem(product: Product, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        val skeletonPlaceholder = placeholder(ColorPainter(colorResource(R.color.gray_skeleton)))
        GlideImage(
            model = product.displayImageUrl(),
            contentDescription = product.displayName(),
            contentScale = ContentScale.Crop,
            loading = skeletonPlaceholder,
            failure = skeletonPlaceholder,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(8.dp))
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = product.displayName(),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = colorResource(R.color.black),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = product.displayPrice(),
            fontSize = 16.sp,
            color = colorResource(R.color.gray_5)
        )
    }
}
