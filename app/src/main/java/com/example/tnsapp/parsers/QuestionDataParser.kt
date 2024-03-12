package com.example.tnsapp.parsers

import com.example.tnsapp.data.Questions
import org.json.JSONObject

fun questionParser(jsonString: String, parentId: Int, catId: Int): List<Questions> {
    val items = mutableListOf<Questions>()
    val catJsonArray = JSONObject(JSONObject(jsonString).getJSONArray("audits")[parentId - 1].toString())
        .getJSONArray("questions")
    val qJsonArray = JSONObject(catJsonArray[catId - 1].toString()).getJSONArray("questions")

    for (i in 0 until qJsonArray.length()) {
        val itemJson: JSONObject = qJsonArray.getJSONObject(i)
        val id = itemJson.getLong("id")
        val qName = itemJson.getString("qName")
        val categoryId = catId - 1

        val listItem = Questions(id, qName, categoryId.toLong())
        items.add(listItem)
    }

    return items
}


