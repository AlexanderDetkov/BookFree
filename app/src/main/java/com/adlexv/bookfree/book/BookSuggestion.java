package com.adlexv.bookfree.book;

import android.os.Parcel;

import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;

public class BookSuggestion implements SearchSuggestion {

    private String bookTitle;

    public BookSuggestion(String suggestion) {
        this.bookTitle = suggestion;
    }

    private BookSuggestion(Parcel source) {
        this.bookTitle = source.readString();
    }

    @Override
    public String getBody() {
        return bookTitle;
    }

    public static final Creator<BookSuggestion> CREATOR = new Creator<BookSuggestion>() {
        @Override
        public BookSuggestion createFromParcel(Parcel in) {
            return new BookSuggestion(in);
        }

        @Override
        public BookSuggestion[] newArray(int size) {
            return new BookSuggestion[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(bookTitle);
    }
}