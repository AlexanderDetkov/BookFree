package com.adlexv.bookfree

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adlexv.bookfree.book.Book
import com.adlexv.bookfree.book.BookSuggestion
import com.adlexv.bookfree.library.ZLibrary
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.arlib.floatingsearchview.FloatingSearchView
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    private var userLocation = "Popular";
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        configureVolley()
        configureBookListView()
        configureBookSearch()
        popularBookList()
    }

    private var volleyQueue: RequestQueue? = null
    private fun configureVolley() {
        volleyQueue = Volley.newRequestQueue(this)
    }


    var fastAdapter : FastAdapter<Book>? = null
    var itemAdapter : ItemAdapter<Book>? = null
    private fun configureBookListView() {
        bookRecyclerView.setHasFixedSize(true)

        itemAdapter = ItemAdapter()
        fastAdapter = FastAdapter.with(itemAdapter!!)

        bookRecyclerView.adapter = fastAdapter

        // Swipe Refresh Layout
        swipeRefreshLayout.setProgressViewOffset(true, 0, 250)
        swipeRefreshLayout.setOnRefreshListener {
            popularBookList();
        }

    }

    private fun configureBookSearch() {

        bookSearchView.setOnQueryChangeListener { _, newQuery ->

            // Handle Submit
            if (newQuery.contains("\n")){
                bookSearchView.setSearchText(newQuery.replace("\n",""))
                bookSearchView.clearSearchFocus()

                // Find Books
                bookSearch(newQuery, 0)
            }

            // Handle Book Search
            else {
                if (newQuery.isNotEmpty()) {
                    ZLibrary.getSearchSuggestions(
                        newQuery,
                        volleyQueue,
                        object :
                            ZLibrary.SuggestionListener {
                            override fun success(searchSuggestions: ArrayList<BookSuggestion>) {
                                if (bookSearchView.isSearchBarFocused)

                                    // Show Suggestions
                                    bookSearchView.swapSuggestions(searchSuggestions)
                            }

                            override fun failure() {

                                bookSearchView.clearSuggestions()
                            }
                        })
                } else {

                    // Empty Search
                    bookSearchView.clearSuggestions()
                }
            }
        }
        bookSearchView.setOnSearchListener(object : FloatingSearchView.OnSearchListener {
            override fun onSuggestionClicked(searchSuggestion: SearchSuggestion?) {
                bookSearchView.setSearchText(searchSuggestion?.body)
                bookSearchView.clearSearchFocus()

                // Find Books
                if (searchSuggestion != null)
                    bookSearch(searchSuggestion.body, 0)
            }

            override fun onSearchAction(currentQuery: String?) {
                Log.d("here", "onSearchAction $currentQuery")
            }
        })
    }

    private fun bookSearch(query: String, pageNumber: Number) {
        userLocation = "Srh $query"
        swipeRefreshLayout.isRefreshing = true
        ZLibrary.getSearch(query, pageNumber, volleyQueue, object : ZLibrary.BookListener {
            override fun success(bookList: Array<Book>) {

                bookRecyclerView.layoutManager = LinearLayoutManager(applicationContext)

                itemAdapter?.set(bookList.toList())
                swipeRefreshLayout.isRefreshing = false
            }

            override fun failure() {
                swipeRefreshLayout.isRefreshing = false
            }

        })
    }

    private fun popularBookList() {
        userLocation = "Pop"
        swipeRefreshLayout.isRefreshing = true
        ZLibrary.getPopular(volleyQueue, object : ZLibrary.BookListener {
            override fun success(bookList: Array<Book>) {
                bookRecyclerView.layoutManager = GridLayoutManager(applicationContext, 3)

                itemAdapter?.set(bookList.toList())
                swipeRefreshLayout.isRefreshing = false
            }

            override fun failure() {
                swipeRefreshLayout.isRefreshing = false
            }
        })
    }
}