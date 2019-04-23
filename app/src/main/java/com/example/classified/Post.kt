package com.example.classified

import android.R.attr.country
import android.R.attr.description



class Post {

    private var post_id: String? = null
    private var user_id: String? = null
    private var image: String? = null
    private var title: String? = null
    private var description: String? = null
    private var price: String? = null
    private var country: String? = null
    private var state_province: String? = null
    private var city: String? = null
    private var contact_email: String? = null



    constructor()

    constructor(
        post_id: String?,
        user_id: String?,
        image: String?,
        title: String?,
        description: String?,
        price: String?,
        country: String?,
        state_province: String?,
        city: String?,
        contact_email: String?
    ) {
        this.post_id = post_id
        this.user_id = user_id
        this.image = image
        this.title = title
        this.description = description
        this.price = price
        this.country = country
        this.state_province = state_province
        this.city = city
        this.contact_email = contact_email
    }

    fun getPost_id(): String? {
        return post_id
    }

    fun setPost_id(post_id: String) {
        this.post_id = post_id
    }

    fun getUser_id(): String? {
        return user_id
    }

    fun setUser_id(user_id: String) {
        this.user_id = user_id
    }

    fun getImage(): String? {
        return image
    }

    fun setImage(image: String) {
        this.image = image
    }

    fun getTitle(): String? {
        return title
    }

    fun setTitle(title: String) {
        this.title = title
    }

    fun getDescription(): String? {
        return description
    }

    fun setDescription(description: String) {
        this.description = description
    }

    fun getPrice(): String? {
        return price
    }

    fun setPrice(price: String) {
        this.price = price
    }

    fun getCountry(): String? {
        return country
    }

    fun setCountry(country: String) {
        this.country = country
    }

    fun getState_province(): String? {
        return state_province
    }

    fun setState_province(state_province: String) {
        this.state_province = state_province
    }

    fun getCity(): String? {
        return city
    }

    fun setCity(city: String) {
        this.city = city
    }

    fun getContact_email(): String? {
        return contact_email
    }

    fun setContact_email(contact_email: String) {
        this.contact_email = contact_email
    }

    override fun toString(): String {
        return "Post{" +
                "post_id='" + post_id + '\''.toString() +
                ", user_id='" + user_id + '\''.toString() +
                ", image='" + image + '\''.toString() +
                ", title='" + title + '\''.toString() +
                ", description='" + description + '\''.toString() +
                ", price='" + price + '\''.toString() +
                ", country='" + country + '\''.toString() +
                ", state_province='" + state_province + '\''.toString() +
                ", city='" + city + '\''.toString() +
                ", contact_email='" + contact_email + '\''.toString() +
                '}'.toString()
    }
}