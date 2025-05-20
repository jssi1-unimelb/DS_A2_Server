// Jiachen Si 1085839
package Main.Gson;

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
        return Color.decode(jsonReader.nextString());
    }
}
