package com.naturalmotion.csr_api.service.reader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.json.*;

import com.naturalmotion.csr_api.Configuration;
import com.naturalmotion.csr_api.api.Resource;
import com.naturalmotion.csr_api.api.ResourceType;
import com.naturalmotion.csr_api.service.car.CarException;
import com.naturalmotion.csr_api.service.io.NsbException;
import com.naturalmotion.csr_api.service.io.NsbReader;

public class ProfileReaderFileImpl implements ProfileReader {

    private NsbReader nsbReader = new NsbReader();

    private Pattern pattern = Pattern.compile("^(.*?)_");

    private String path;

    public ProfileReaderFileImpl(String path) {
        this.path = path;
    }

    @Override
    public List<Resource> getResources() throws ReaderException {
        File editedDirectory = new File(path + "/Edited");
        if (!editedDirectory.exists()) {
            throw new ReaderException(path + " doesn't exist");
        }
        File scb = new File(editedDirectory.getAbsolutePath() + "/scb.json");
        if (!scb.exists()) {
            throw new ReaderException("Missing scb file into Edited folder");
        }

        List<Resource> resources = new ArrayList<>();

        try (InputStream fis = new FileInputStream(scb); JsonReader reader = Json.createReader(fis);) {
            Configuration conf = new Configuration();
            JsonObject personObject = reader.readObject();
            resources.add(createResource(ResourceType.CASH, personObject, conf));
            resources.add(createResource(ResourceType.GOLD, personObject, conf));
            resources.add(createResource(ResourceType.BRONZE_KEY, personObject, conf));
            resources.add(createResource(ResourceType.SILVER_KEY, personObject, conf));
            resources.add(createResource(ResourceType.GOLD_KEY, personObject, conf));

        } catch (IOException e) {
            throw new ReaderException("Error reading file", e);
        }

        return resources;
    }

    @Override
    public List<String> getBrands() throws NsbException {
        Set<String> brands = new HashSet<>();

        JsonObject nsbFull = nsbReader.getNsbFull();
        JsonArray carList = nsbFull.getJsonArray("caow");
        Iterator<JsonValue> iterator = carList.iterator();
        while (iterator.hasNext()) {
            JsonObject car = iterator.next().asJsonObject();
            String id = car.getString("crdb");
            Matcher matcher = pattern.matcher(id);
            if(matcher.find()){
                String brand = matcher.group(1);
                brands.add("id_" + brand.toLowerCase());
            }

        }
        List<String> result = new ArrayList<>(brands);
        Collections.sort(result);
        return result;
    }

    private Resource createResource(ResourceType type, JsonObject personObject, Configuration conf) {

        int earned = personObject.getInt(conf.getString(type.name() + ".scb.earned"));
        int spent = personObject.getInt(conf.getString(type.name() + ".scb.spent"));
        Resource resource = new Resource();
        resource.setType(type);
        resource.setEarned(BigDecimal.valueOf(earned));
        resource.setSpent(BigDecimal.valueOf(spent));
        return resource;
    }

}
