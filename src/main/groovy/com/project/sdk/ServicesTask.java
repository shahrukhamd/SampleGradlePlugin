package com.project.sdk;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;

import java.io.*;
import java.util.Map;
import java.util.TreeMap;

public class ServicesTask extends DefaultTask {

    private File jsonFile;
    private File newFile;

    @InputFile
    @Optional
    public File getJsonFile() {
        return jsonFile;
    }

    public void setJsonFile(File jsonFile) {
        this.jsonFile = jsonFile;
    }

    @OutputDirectory
    public File getNewFile() {
        return newFile;
    }

    public void setNewFile(File newFile) {
        this.newFile = newFile;
    }

    @TaskAction
    public void action() throws IOException {
        JsonElement root = new JsonParser().parse(new BufferedReader(new InputStreamReader(
                new FileInputStream(jsonFile), "UTF-8")));
        JsonObject rootObject = root.getAsJsonObject();

        Map<String, String> resValues = new TreeMap<>();

        // set value map from config json
        for (String key: rootObject.keySet()) {
            resValues.put(key, rootObject.get(key).getAsString());
        }

        // write the values file.
        File values = new File(newFile, "values");
        if (!values.exists() && !values.mkdirs()) {
            throw new GradleException("Failed to create folder: " + values);
        }

        PrintWriter fileWriter = new PrintWriter(new OutputStreamWriter(
                new FileOutputStream(new File(values, "values.xml")), "UTF-8"));
        fileWriter.print(mapToString(resValues));
        fileWriter.flush();
        fileWriter.close();
    }

    /**
     * This will get the map and convert it into Android String resource format string.
     */
    private static String mapToString(Map<String, String> values) {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" + "<resources>\n");

        for (Map.Entry<String, String> entry : values.entrySet()) {
            String name = entry.getKey();
            sb.append("    <string name=\"").append(name).append("\" translatable=\"false\">")
                    .append(entry.getValue()).append("</string>\n");
        }

        sb.append("</resources>\n");

        return sb.toString();
    }
}