package Main.Gson;// Jiachen Si 1085839
import Main.DrawObjects.Drawable;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.awt.*;
import java.awt.image.BufferedImage;

public class GsonUtil {
    public static final Gson gson =
            new GsonBuilder()
                    .registerTypeAdapter(Drawable.class, new DrawableAdaptor())
                    .registerTypeAdapter(BufferedImage.class, new BufferedImageAdaptor())
                    .registerTypeAdapter(Color.class, new ColourAdaptor())
                    .create();
 }
