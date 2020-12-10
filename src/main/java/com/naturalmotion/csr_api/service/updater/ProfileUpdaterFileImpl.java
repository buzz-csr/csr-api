package com.naturalmotion.csr_api.service.updater;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.json.Json;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonValue;

import com.naturalmotion.csr_api.Configuration;
import com.naturalmotion.csr_api.api.EliteTokenParam;
import com.naturalmotion.csr_api.api.Resource;
import com.naturalmotion.csr_api.api.ResourceType;
import com.naturalmotion.csr_api.service.io.JsonBuilder;
import com.naturalmotion.csr_api.service.io.NsbException;
import com.naturalmotion.csr_api.service.io.NsbReader;
import com.naturalmotion.csr_api.service.io.ProfileFileWriter;
import com.naturalmotion.csr_api.service.io.ScbReader;
import com.naturalmotion.csr_api.service.reader.ProfileReader;
import com.naturalmotion.csr_api.service.reader.ProfileReaderFileImpl;
import com.naturalmotion.csr_api.service.reader.ReaderException;

public class ProfileUpdaterFileImpl implements ProfileUpdater {

    private static final String CCAC = "ccac";

	private static final String ELITE_TOKEN_EARNED = "afme";

    private static final String ELITE_TOKEN_SPENT = "afms";

    private String path;

    private ProfileReader profileReader;

    private NsbReader nsbReader = new NsbReader();

    private ScbReader scbReader = new ScbReader();

    private JsonBuilder jsonBuilder = new JsonBuilder();

    private ProfileFileWriter fileWriter = new ProfileFileWriter();

    public ProfileUpdaterFileImpl(String path) {
        this.path = path;
        profileReader = new ProfileReaderFileImpl(path);
    }

    @Override
    public void updateResource(ResourceType type, BigDecimal expected) throws UpdaterException, NsbException {
        File scb = scbReader.getScbFile(path);
        File nsb = nsbReader.getNsbFile(path);

        BigDecimal newValue = getValue(type, expected);

        editFile(type, newValue, scb, "scb");
        editFile(type, newValue, nsb, "nsb");
    }

    private BigDecimal getValue(ResourceType type, BigDecimal expected) throws UpdaterException {
        BigDecimal newValue = null;
        try {
            List<Resource> resources = profileReader.getResources();
            Resource actual = resources.stream().filter(x -> x.getType().equals(type)).findFirst().orElse(null);
            if (actual != null) {
                newValue = getNewValue(expected, actual);
            }
        } catch (ReaderException e) {
            throw new UpdaterException("Error reading profile", e);
        }
        return newValue;
    }

    private void editFile(ResourceType type, BigDecimal value, File file, String name)
            throws UpdaterException, NsbException {
        JsonObjectBuilder newJsonObject = getNewJson(type, value, file, name);

        if (newJsonObject != null) {
            fileWriter.write(file, newJsonObject);
        }
    }

    private JsonObjectBuilder getNewJson(ResourceType type, BigDecimal value, File file, String name)
            throws UpdaterException {
        JsonObjectBuilder jsonObjectBuilder = null;
        try (InputStream fis = new FileInputStream(file); JsonReader reader = Json.createReader(fis);) {
            Configuration conf = new Configuration();
            if (value != null) {
                JsonObject json = reader.readObject();

                JsonBuilderFactory jsonBuilderFactory = Json.createBuilderFactory(null);
                jsonObjectBuilder = jsonBuilderFactory.createObjectBuilder(json);

                jsonObjectBuilder.add(getLabel(type, name, conf), value); // update spent value
            }
        } catch (IOException e) {
            throw new UpdaterException("Error updating profile", e);
        }
        return jsonObjectBuilder;
    }

    private String getLabel(ResourceType type, String name, Configuration conf) {
        return conf.getString(type.name() + "." + name + ".spent");
    }

    private BigDecimal getNewValue(BigDecimal expected, Resource actual) {
        BigDecimal newValue = null;
        if (expected.compareTo(actual.getSpent()) > 0) {
            newValue = BigDecimal.ZERO;
        } else {
            newValue = actual.getSpent().subtract(expected);
        }
        return newValue;
    }

    @Override
    public JsonObject deban() throws NsbException {
        JsonObject spent = cleanNsb();
        cleanScb();
        return spent;
    }

    private void cleanScb() throws NsbException {
        File scb = scbReader.getScbFile(path);
        JsonObject scbObject = jsonBuilder.readJsonObject(scb);

        JsonObjectBuilder scbBuilder = Json.createObjectBuilder(scbObject);
        scbBuilder.add("AMPartGreenEarned", 15);
        scbBuilder.add("AMPartBlueEarned", 0);
        scbBuilder.add("AMPartRedEarned", 0);
        scbBuilder.add("AMPartYellowEarned", 0);

        scbBuilder.add("AMPartGreenSpent", 15);
        scbBuilder.add("AMPartBlueSpent", 0);
        scbBuilder.add("AMPartRedSpent", 0);
        scbBuilder.add("AMPartYellowSpent", 0);
        fileWriter.write(scb, scbBuilder);
    }

    private JsonObject cleanNsb() throws NsbException {
        File nsb = nsbReader.getNsbFile(path);
        JsonObject nsbObject = jsonBuilder.readJsonObject(nsb);
        JsonObjectBuilder newNsbObject = Json.createObjectBuilder(nsbObject);
        newNsbObject.add("tcbl", 0);
        newNsbObject.add("rcbp", false);

        JsonObjectBuilder eliteTokenEarned = Json.createObjectBuilder();
        eliteTokenEarned.add("Green", 15);
        eliteTokenEarned.add("Blue", 0);
        eliteTokenEarned.add("Red", 0);
        eliteTokenEarned.add("Yellow", 0);
        newNsbObject.add(ELITE_TOKEN_EARNED, eliteTokenEarned);

        JsonObjectBuilder eliteTokenSpent = Json.createObjectBuilder();
        eliteTokenSpent.add("Green", 15);
        eliteTokenSpent.add("Blue", 0);
        eliteTokenSpent.add("Red", 0);
        eliteTokenSpent.add("Yellow", 0);
        newNsbObject.add(ELITE_TOKEN_SPENT, eliteTokenSpent);

        newNsbObject.add(CCAC, cleanCcac(nsbObject));

        fileWriter.write(nsb, newNsbObject);

        return nsbObject.getJsonObject(ELITE_TOKEN_SPENT);
    }

	private JsonObjectBuilder cleanCcac(JsonObject nsbObject) {
		JsonObjectBuilder newCcac = Json.createObjectBuilder();
        JsonObject ccac = nsbObject.getJsonObject(CCAC);
		Set<Entry<String, JsonValue>> entrySet = ccac.entrySet();
		for (Entry<String, JsonValue> entry : entrySet) {
			newCcac.add(entry.getKey(), Json.createArrayBuilder());
		}
		return newCcac;
	}

    @Override
    public void updateResourceAfterBan(List<EliteTokenParam> tokens)
            throws NsbException, IOException {
        Configuration configuration = new Configuration();

        updateNsb(tokens, configuration, "earned", ELITE_TOKEN_EARNED);
        updateScb(tokens, configuration, "earned");
    }

    @Override
    public void updateEliteTokens(List<EliteTokenParam> tokens) throws NsbException, IOException {
        Configuration configuration = new Configuration();

        updateNsb(tokens, configuration, "spent", ELITE_TOKEN_SPENT);
        updateScb(tokens, configuration, "spent");
    }

    public void updateScb(List<EliteTokenParam> tokens, Configuration configuration, String way) throws NsbException {
        File scb = scbReader.getScbFile(path);
        JsonObject scbObject = jsonBuilder.readJsonObject(scb);
        JsonObjectBuilder newScbBuilder = Json.createObjectBuilder(scbObject);
        for (EliteTokenParam token : tokens) {
            String jsonKey = configuration.getString(token.getToken().name() + ".scb." + way);
            newScbBuilder.add(jsonKey, token.getAmount().intValue());
        }
        fileWriter.write(scb, newScbBuilder);
    }

    public void updateNsb(List<EliteTokenParam> tokens, Configuration configuration, String way, String node) throws NsbException {
        File nsb = nsbReader.getNsbFile(path);
        JsonObject nsbObject = jsonBuilder.readJsonObject(nsb);
        JsonObjectBuilder tokenBuilder = Json.createObjectBuilder();
        for (EliteTokenParam token : tokens) {
            String jsonKey = configuration.getString(token.getToken().name() + ".nsb." + way);
            tokenBuilder.add(jsonKey, token.getAmount().intValue());
        }

        JsonObjectBuilder newNsbBuilder = Json.createObjectBuilder(nsbObject);
        newNsbBuilder.add(node, tokenBuilder);
        fileWriter.write(nsb, newNsbBuilder);
    }

}
