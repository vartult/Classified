package com.example.classified


class User {

    private var user_id: String? = null
    private var name: String? = null

    constructor(user_id: String?, name: String?) {
        this.user_id = user_id
        this.name = name
    }

    constructor(){

    }

    fun getUser_id(): String? {
        return user_id
    }

    fun setUser_id(user_id: String) {
        this.user_id = user_id
    }

    fun getName(): String? {
        return name
    }

    fun setName(name: String) {
        this.name = name
    }

    override fun toString(): String {
        return super.toString()
    }
}
