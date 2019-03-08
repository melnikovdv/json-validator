package org.mlayer.events.validator;

import org.apache.commons.io.IOUtils;
import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaClient;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

class App {

    private String schemaFolder;
    private String dataPath;
    private String schemaPath;

    public App(String schemaFolder, String schemaRelativePath, String dataPath) {
        this.schemaFolder = schemaFolder.replace("\\", "/");
        this.dataPath = dataPath.replace("\\", "/");
        this.schemaPath = schemaRelativePath.replace("\\", "/");
        printParams();
    }

    private void printParams() {
        System.out.println("schemaFolder = " + schemaFolder);
        System.out.println("schemaRelativePath = " + schemaPath);
        System.out.println("dataPath = " + dataPath);
        System.out.println();
    }

    public static void main(String[] args) {
        if (args.length >= 3) {
            String schemaFolder = args[0];
            String schemaRelativePath = args[1];
            String dataPath = args[2];
            App app = new App(schemaFolder, schemaRelativePath, dataPath);
            app.validate();
        } else {
            System.out.println("Help: java -jar validator.jar \"path_to_folder_with_schemas\" \"relative_path_to_schema\" \"path_to_json\"");
        }
    }

    private void validate() {
        String json = getJsonData();
        Schema schema = getSchema();

        JSONObject jsonObject = new JSONObject(json);
        try {
            schema.validate(jsonObject);
            System.out.println("schema is valid");
        } catch (ValidationException e) {
            System.out.println("schema is invalid");
            System.out.println();
            printErrors(e);
        }
    }

    private void printErrors(ValidationException exception) {
        exception.getCausingExceptions().stream()
                .filter(e -> e.getCausingExceptions().isEmpty())
                .map(ValidationException::getMessage)
                .forEach(System.out::println);

        exception.getCausingExceptions().stream()
                .filter(e -> !e.getCausingExceptions().isEmpty())
                .forEach(e -> {
                    System.out.println(e.getMessage());
                    printErrors(e);
                    System.out.println();
                });
    }

    private String getJsonData() {
        try {
            InputStream inputStream = new FileInputStream(dataPath);
            return IOUtils.toString(inputStream, "utf-8");
        } catch (FileNotFoundException e) {
            showErrorAndExit("no such file: " + dataPath);
        } catch (IOException e) {
            showErrorAndExit("can't read file: " + dataPath);
        }
        return null;
    }

    private Schema getSchema() {
        try {
            InputStream inputStream = new FileInputStream(schemaFolder + schemaPath);
            String s = IOUtils.toString(inputStream, "utf-8");
            JSONObject jsonSchema = new JSONObject(s);
            SchemaLoader schemaLoader = SchemaLoader.builder()
                    .schemaClient(SchemaClient.classPathAwareClient())
                    .schemaJson(jsonSchema)
                    .resolutionScope("file:///" + schemaFolder)
                    .build();

            return schemaLoader.load().build();
        } catch (FileNotFoundException e) {
            showErrorAndExit("no such file: " + schemaPath);
        } catch (IOException e) {
            showErrorAndExit("can't read file: " + schemaPath);
        }
        return null;
    }

    private void showErrorAndExit(String error) {
        System.out.println(error);
        System.exit(1);
    }
}
