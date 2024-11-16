package com.example.saifb


import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView

class ImageAdapter(private val context: Context) : BaseAdapter() {

    // List of images in the drawable folder
    private val images = arrayOf(
        R.drawable.design1,
        R.drawable.design2,
        R.drawable.design3,
        R.drawable.design4,
        R.drawable.design1
        // Add more images as needed
    )

    override fun getCount(): Int {
        return images.size
    }

    override fun getItem(position: Int): Any {
        return images[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val imageView: ImageView
        if (convertView == null) {
            // Create a new ImageView if there's no reusable view
            imageView = ImageView(context)
            imageView.layoutParams = ViewGroup.LayoutParams(200, 200) // Set width & height
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        } else {
            // Reuse the existing view
            imageView = convertView as ImageView
        }

        // Set the image resource
        imageView.setImageResource(images[position])
        return imageView
    }
}
