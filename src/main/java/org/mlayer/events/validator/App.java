package org.mlayer.events.validator;

import org.apache.commons.io.IOUtils;
import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

class App {

    private String dataPath;
    private String schemaPath;

    public App(String dataPath, String schemaPath) {
        this.dataPath = dataPath;
        this.schemaPath = schemaPath;
        printParams(dataPath, schemaPath);
    }

    private void printParams(String dataPath, String schemaPath) {
        System.out.println("schemaPath = " + schemaPath);
        System.out.println("dataPath = " + dataPath);
        System.out.println();
    }

    public static void main(String[] args) {
        if (args.length >= 2) {
            String schemaPath = args[0];
            String dataPath = args[1];
            App app = new App(dataPath, schemaPath);
            app.validate();
        } else {
            System.out.println("Help: java -jar validator.jar \"path_to_schema\" \"path_to_json\"");
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
            InputStream inputStream = new FileInputStream(schemaPath);
            String s = IOUtils.toString(inputStream, "utf-8");
            JSONObject jsonSchema = new JSONObject(s);
            return SchemaLoader.load(jsonSchema);
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
