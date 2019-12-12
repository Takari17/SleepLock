package com.takari.sleeplock.feature.whitenoise.data

import com.google.gson.*
import java.lang.reflect.Type


/*
Used so Gson can serialize and deserialize custom objects of type interface, so that they can
be stored and retrieved as strings in shared preferences.
 */
class InterfaceAdapter : JsonDeserializer<Any>, JsonSerializer<Any> {

    @Throws(JsonParseException::class)
    override fun deserialize(
        jsonElement: JsonElement, type: Type,
        jsonDeserializationContext: JsonDeserializationContext
    ): Any {

        val jsonObject = jsonElement.asJsonObject
        val prim: JsonPrimitive = jsonObject.get(CLASSNAME).asJsonPrimitive
        val className = prim.asString
        val objectClass = getObjectClass(className)
        return jsonDeserializationContext.deserialize(jsonObject.get(DATA), objectClass)
    }

    override fun serialize(
        jsonElement: Any,
        type: Type,
        jsonSerializationContext: JsonSerializationContext
    ): JsonElement {
        val jsonObject = JsonObject()
        jsonObject.addProperty(CLASSNAME, jsonElement.javaClass.name)
        jsonObject.add(DATA, jsonSerializationContext.serialize(jsonElement))
        return jsonObject
    }

    private fun getObjectClass(className: String): Class<*> {
        try {
            return Class.forName(className)
        } catch (e: ClassNotFoundException) {
            throw JsonParseException(e.message)
        }

    }

    companion object {
        const val CLASSNAME = "CLASSNAME"
        const val DATA = "DATA"
    }
}
