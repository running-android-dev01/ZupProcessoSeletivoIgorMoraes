package com.moraes.igor.zupprocessoseletivo;

import android.os.Parcel;
import android.os.Parcelable;

class Rating implements Parcelable {
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Rating createFromParcel(Parcel in) {
            return new Rating(in);
        }

        public Rating[] newArray(int size) {
            return new Rating[size];
        }
    };

    Rating(){
    }

    private Rating(Parcel in){
        this.ratingKey = in.readString();
        this.imdbID = in.readString();
        this.source = in.readString();
        this.value = in.readString();
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.ratingKey);
        dest.writeString(this.imdbID);
        dest.writeString(this.source);
        dest.writeString(this.value);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    String ratingKey;
    String imdbID;
    String source;
    String value;
}
