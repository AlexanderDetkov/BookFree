package com.adlexv.bookfree.book

import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.adlexv.bookfree.R
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.items.AbstractItem

open class Book(_title: String, _author: String?, _coverURL: String, _bookUrl: String, _layoutType: Int) : AbstractItem<Book.ViewHolder>() {
    var title: String? = null
    var author: String? = null
    var coverURL: String? = null
    var bookURL: String? = null

    // 0 - Book Cover
    // 1 - Book Card
    var layoutType: Int? = null


    init {
        title = _title
        author = _author
        coverURL = _coverURL
        bookURL = _bookUrl
        layoutType = _layoutType
    }

    override val type: Int
        get() = layoutType!!

    override val layoutRes: Int
        get() = if (layoutType == 0){
            R.layout.book_cover
        }else{
            R.layout.book_card
        }

    override fun getViewHolder(v: View): ViewHolder {
        return ViewHolder(v)
    }

    class ViewHolder(view: View) : FastAdapter.ViewHolder<Book>(view) {

        var coverImage : ImageView = view.findViewById(R.id.coverImage)
        var bookTitleText : TextView? = view.findViewById(R.id.bookTitleText)
        override fun bindView(item: Book, payloads: MutableList<Any>) {
            Glide
                .with(coverImage.context)
                .load(item.coverURL)
                .apply(RequestOptions().override(200, 320))
                .placeholder(R.drawable.bookplaceholder)
                .centerCrop()
                .into(coverImage);
            bookTitleText?.text = item.title

        }

        override fun unbindView(item: Book) {
            coverImage.setImageDrawable(null)
            bookTitleText?.text = null
        }
    }
}
