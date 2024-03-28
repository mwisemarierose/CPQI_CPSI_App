package com.example.tnsapp.parsers

import com.example.tnsapp.data.Categories
import org.json.JSONObject

fun categoryParser(jsonString: String, parentId: Int): List<Categories> {
    val items = mutableListOf<Categories>()
    val jsonArray =
        JSONObject(JSONObject(jsonString).getJSONArray("audits")[parentId - 1].toString())
            .getJSONArray("categories")

    for (i in 0 until jsonArray.length()) {
        val itemJson: JSONObject = jsonArray.getJSONObject(i)
        val id = itemJson.getLong("id")
        val name = itemJson.getString("name")
        val iconUrl = itemJson.getString("icon_path")
        val auditId = (parentId - 1).toLong()

        val listItem = Categories(id, name, iconUrl, auditId)
        items.add(listItem)
    }

    return items
}
