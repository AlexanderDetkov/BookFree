package com.adlexv.bookfree.library

import android.util.Log
import com.adlexv.bookfree.book.Book
import com.adlexv.bookfree.book.BookSuggestion
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.StringRequest
import org.json.JSONArray
import org.jsoup.Jsoup

object ZLibrary{
    private const val NUMBEROFSUGGESTIONS = 4

    interface BookListener {
        fun success(bookList: Array<Book>)
        fun failure()
    }

    interface SuggestionListener{
        fun success(searchSuggestions: ArrayList<BookSuggestion>)
        fun failure()
    }


    fun getPopular(volleyQueue: RequestQueue?, bookListener: BookListener){
        volleyQueue?.add(StringRequest(
            Request.Method.GET,
            "https://b-ok.cc/popular.php",
            Response.Listener<String> { response ->

                val bookHtmlElements = Jsoup
                    .parse(response)
                    .getElementsByClass("brick")


                bookListener.success(Array(bookHtmlElements.size) { i ->
                    Book(
                        bookHtmlElements[i].select("a").attr("title"),
                        null,
                        "https://${bookHtmlElements[i].select("img").attr("src").substring(2)}",
                        bookHtmlElements[i].select("a").attr("href"),
                    0
                    )
                })



            },
            Response.ErrorListener {
                bookListener.failure()
            }))
    }

    fun getSearch(searchTerm: String, pageNumber: Number, volleyQueue: RequestQueue?, bookListener: BookListener){
        volleyQueue?.add(StringRequest(
            Request.Method.GET, "https://b-ok.cc/s/$searchTerm?page=$pageNumber",
            Response.Listener<String> { response ->

                val bookHtmlElements = Jsoup
                    .parse(response)
                    .getElementsByClass("resItemTable")

                Log.d("here", bookHtmlElements[0].getElementsByClass("authors").text().toString())
                bookListener.success(Array(bookHtmlElements.size) { i ->
                    Book(
                        bookHtmlElements[i].select("h3[itemprop=name]").select("a").text(),
                        bookHtmlElements[0].getElementsByClass("authors").text(),
                        "https://${bookHtmlElements[i].getElementsByClass("itemCover").select("img").attr("data-src")}",
                        bookHtmlElements[i].select("h3[itemprop=name]").select("a").attr("href"),
                    1
                    )
                })
            },
            Response.ErrorListener {
                bookListener.failure()
            }))
    }


    fun getSearchSuggestions(searchTerm: String, volleyQueue: RequestQueue?, suggestionListener: SuggestionListener){

        volleyQueue?.add(JsonArrayRequest(
            Request.Method.GET,
            "https://suggestqueries.google.com/complete/search?client=firefox&ds=bo&callback=?&q=$searchTerm",
            null,
            Response.Listener<JSONArray> { response ->

                val bookSuggestion = ArrayList<BookSuggestion>(NUMBEROFSUGGESTIONS)

                val rawSuggestions = response[1].toString();

                // format and set suggestions
                var numberOfSuggestions = 0
                var startIndex = 2
                var stopIndex = rawSuggestions.indexOf("\",\"")
                while (startIndex >= 0 && stopIndex >= 0) {

                    bookSuggestion.add(
                        BookSuggestion(
                            rawSuggestions.substring(startIndex, stopIndex)
                        )
                    )
                    startIndex = stopIndex + 3;
                    stopIndex = rawSuggestions.indexOf("\",\"", startIndex)

                    numberOfSuggestions++
                    if (numberOfSuggestions == NUMBEROFSUGGESTIONS)
                        break
                }


                // return suggestions if has suggestions
                if (numberOfSuggestions != 0)
                    suggestionListener.success(bookSuggestion)

                // return failure if no suggestions
                else
                    suggestionListener.failure()


            }, Response.ErrorListener {error ->
                suggestionListener.failure()
            }))
    }
}