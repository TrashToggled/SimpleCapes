package maowcraft.simplecapes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.devtech.arrp.api.RRPCallback;
import net.devtech.arrp.api.RuntimeResourcePack;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SimpleCapes implements ModInitializer {
    public static final String MODID = "simplecapes";
    public static final String NAME = "Simple Capes";

    public static final RuntimeResourcePack RESOURCE_PACK = RuntimeResourcePack.create("simplecapes:resource");

    private static final Logger logger = LogManager.getLogger(NAME);
    private static final Gson gson = new GsonBuilder().create();

    public static final Path mainDir = Paths.get(MODID);

    public static List<String> identifiers = new ArrayList<>();
    public static String selectedIdentifier = "";

    private static final FileFilter fileFilter = file -> file.getName().endsWith(".png");

    @Override
    public void onInitialize() {
        logger.info("Simple Capes has initialized.");
        BufferedImage capeImage;
        SimpleCapes.writeDirectories();
        try {
            for (File file : SimpleCapes.grabAllCapeTextures()) {
                if (file.exists()) {
                    capeImage = ImageIO.read(file);
                    String identifierFile = file.getName().substring(0, file.getName().indexOf('.')).replace(' ', '_').toLowerCase() + "-" + Math.abs(MinecraftClient.getInstance().getSession().getProfile().getName().hashCode());
                    SimpleCapes.identifiers.add(identifierFile);
                    RESOURCE_PACK.addTexture(new Identifier(SimpleCapes.MODID, identifierFile), capeImage);
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        RRPCallback.EVENT.register(a -> a.add(1, RESOURCE_PACK));
        validateConfigExistence();
        readConfig();
    }

    public static File[] grabAllCapeTextures() {
        return Objects.requireNonNull(SimpleCapes.mainDir.toFile().listFiles(fileFilter));
    }

    public static void validateConfigExistence() {
        try {
            if (!Files.exists(Paths.get("simplecapes.json"))) {
                //noinspection ResultOfMethodCallIgnored
                Paths.get("simplecapes.json").toFile().createNewFile();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void readConfig() {
        try {
            Reader reader = new FileReader("simplecapes.json");
            selectedIdentifier = gson.fromJson(reader, String.class);
            reader.close();
        } catch (IOException ex) {
            logger.error("An exception occurred while reading the config.");
            ex.printStackTrace();
        }
    }

    public static void writeConfig() {
        try {
            Writer writer = new FileWriter("simplecapes.json");
            gson.toJson(selectedIdentifier, writer);
            writer.close();
        } catch (IOException ex) {
            logger.error("An exception occurred while writing the config.");
            ex.printStackTrace();
        }
    }

    public static void writeDirectories() {
        try {
            if (!Files.exists(mainDir)) {
                logger.warn("\"" + mainDir.toString() + "\" directory does not exist, creating it now.");
                Files.createDirectory(mainDir);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
