package com.example.helloworld.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.helloworld.R
import com.example.helloworld.model.Author

class AuthorsAdapter(
    context: Context,
    private val authors: List<Author>
) : ArrayAdapter<Author>(context, 0, authors) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        val holder: ViewHolder

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_author, parent, false)
            holder = ViewHolder(
                view.findViewById(R.id.authorPhoto),
                view.findViewById(R.id.authorName),
                view.findViewById(R.id.authorRole)
            )
            view.tag = holder
        } else {
            holder = view.tag as ViewHolder
        }

        val author = getItem(position)
        author?.let {
            holder.photo.setImageResource(it.photoResId)
            holder.name.text = it.name
            holder.role.text = it.role
        }

        return view!!
    }

    private class ViewHolder(
        val photo: ImageView,
        val name: TextView,
        val role: TextView
    )
}