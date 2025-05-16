import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.awt.*;
import java.io.IOException;

public class ColourAdaptor extends TypeAdapter<Color> {

    @Override
    public void write(JsonWriter jsonWriter, Color color) throws IOException {}

    @Override
    public Color read(JsonReader jsonReader) throws IOException {
//        JsonObject json = JsonParser.parseReader(jsonReader).getAsJsonObject(); // Convert input to json
//        String colourString = json.get("colour").getAsString(); //Get the obj type
//        return Color.decode(colourString);
        return Color.decode(jsonReader.nextString());
    }
}
