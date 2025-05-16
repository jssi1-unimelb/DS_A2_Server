import DrawObjects.*;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class DrawableAdaptor extends TypeAdapter<Drawable> {

    @Override
    public void write(JsonWriter jsonWriter, Drawable drawable) throws IOException {}

    @Override
    public Drawable read(JsonReader jsonReader) throws IOException {
        JsonObject json = JsonParser.parseReader(jsonReader).getAsJsonObject(); // Convert input to json
        String shapeType = json.get("shapeType").getAsString().toLowerCase(); // Get the obj type

        return switch (shapeType) {
            case "line" -> GsonUtil.gson.fromJson(json, Line.class);
            case "triangle" -> GsonUtil.gson.fromJson(json, Triangle.class);
            case "circle" -> GsonUtil.gson.fromJson(json, CircleOnCursor.class);
            case "oval" -> GsonUtil.gson.fromJson(json, Oval.class);
            case "rectangle" -> GsonUtil.gson.fromJson(json, Rectangle.class);
            case "text" -> GsonUtil.gson.fromJson(json, Text.class);
            default -> throw new IllegalArgumentException("Unknown type: " + shapeType);
        };
    }
}
