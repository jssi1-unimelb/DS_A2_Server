import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Type;
import java.util.Base64;

public class BufferedImageSerializer implements JsonSerializer<BufferedImage> {

    @Override
    public JsonElement serialize(BufferedImage bufferedImage, Type type, JsonSerializationContext jsonSerializationContext) {
        try {
            // Convert BufferedImage into Base64 string
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            // Encodes the buffered image into a png
            ImageIO.write(bufferedImage, "png", os);
            // Convert PNG data into Base64 string
            String base64 = Base64.getEncoder().encodeToString(os.toByteArray());
            os.close();
            // Wrap in a JsonPrimitive to turn it back into a JsonElement
            return new JsonPrimitive(base64);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
