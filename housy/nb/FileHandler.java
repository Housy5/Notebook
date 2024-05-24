package housy.nb;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;

public class FileHandler {
    
    private static final String userHomePath = System.getProperty("user.home");
    private static final String appHomePath = userHomePath + "\\Notebook\\";    
    
    static {
        File appHome = new File(appHomePath);
        if (!appHome.exists())
            appHome.mkdirs();
    }
    
    private static final File textSaveFile = new File(appHomePath + "text.txt");
    private static final File modelFile = new File(appHomePath + "model.ser");
    private static final File lockFile = new File(appHomePath + "lock");
    
    public static void saveTextToFile(String text) throws IOException {
        Files.writeString(textSaveFile.toPath(), text);
    }
    
    public static String loadTextFromFile() throws IOException {
        return Files.readString(textSaveFile.toPath());
    }
    
    public static void saveModelToFile(Model model) throws IOException {
        try(var fout = new FileOutputStream(modelFile);
                var out = new ObjectOutputStream(fout)) {
            out.writeObject(model);
        }
    }
    
    public static Model loadModelFromFile() throws IOException, ClassNotFoundException {
        try (var fin = new FileInputStream(modelFile);
                var in = new ObjectInputStream(fin)) {
            return (Model) in.readObject();
        }
    }
    
    public static void updateLockFile() throws IOException {
        Files.writeString(lockFile.toPath(), String.valueOf(System.currentTimeMillis()));
    }
    
    public static void emptyLockFile() throws IOException {
        Files.writeString(lockFile.toPath(), String.valueOf(-1));
    }
    
    public static long readLockFile() throws IOException {
        return Long.parseLong(Files.readString(lockFile.toPath()));
    }
}
