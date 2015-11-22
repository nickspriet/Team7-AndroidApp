package com.howest.nmct.bob.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * illyism
 * 21/10/15
 */
public class User implements Parcelable {
    @SerializedName("_id")
    @Expose
    public String Id;

    @SerializedName("facebookID")
    @Expose
    public String facebookID;

    @SerializedName("name")
    @Expose
    public String name;

    @SerializedName("firstName")
    @Expose
    public String firstName;

    @SerializedName("lastName")
    @Expose
    public String lastName;

    @SerializedName("picture")
    @Expose
    public String picture;

    @SerializedName("cover")
    @Expose
    public String cover;

    @SerializedName("link")
    @Expose
    public String link;
    private boolean email;

    public User(String id, String facebookID, String name, String firstName, String lastName,
                String picture, String cover, String link) {
        this.Id = id;
        this.facebookID = facebookID;
        this.name = name;
        this.firstName = firstName;
        this.lastName = lastName;
        this.picture = picture;
        this.cover = cover;
        this.link = link;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(Id);
        dest.writeString(facebookID);
        dest.writeString(name);
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(picture);
        dest.writeString(cover);
        dest.writeString(link);
    }

    public User(Parcel in) {
        Id = in.readString();
        facebookID = in.readString();
        name = in.readString();
        firstName = in.readString();
        lastName = in.readString();
        picture = in.readString();
        cover = in.readString();
        link = in.readString();
    }

    public static final Parcelable.Creator CREATOR =
            new Parcelable.Creator() {
                public User createFromParcel(Parcel in) {
                    return new User(in);
                }

                public User[] newArray(int size) {
                    return new User[size];
                }
            };

    public String getId() {
        return Id;
    }

    public String getName() {
        return name;
    }

    public String getCover() {
        return cover;
    }

    public boolean getEmail() {
        return email;
    }

    public String getPicture() {
        return picture;
    }
}