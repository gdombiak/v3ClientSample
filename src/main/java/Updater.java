import com.jivesoftware.v3client.framework.AbstractJiveClient;
import com.jivesoftware.v3client.framework.JiveClient;
import com.jivesoftware.v3client.framework.NameValuePair;
import com.jivesoftware.v3client.framework.credentials.Credentials;
import com.jivesoftware.v3client.framework.entities.DocumentEntity;
import com.jivesoftware.v3client.framework.entities.GroupEntity;
import com.jivesoftware.v3client.framework.entities.SummaryEntity;
import com.jivesoftware.v3client.framework.entity.ContentEntity;
import com.jivesoftware.v3client.framework.entity.PlaceEntity;
import com.jivesoftware.v3client.framework.http.HttpTransportImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

public class Updater {

    private final JiveClient jiveClientSrc, jiveClientDest;
    private String documentId, placeId;

    public Updater(Properties props, String documentId, String placeId) {
        this.documentId = documentId;
        this.placeId = placeId;
        jiveClientSrc = new JiveClient(props.getProperty("from.jive.url") + "/api/core/v3", new InstanceCredentials(props, "from"), new HttpTransportImpl());
        jiveClientDest = new JiveClient(props.getProperty("to.jive.url") + "/api/core/v3", new InstanceCredentials(props, "to"), new HttpTransportImpl());
        AbstractJiveClient.JIVE_CLIENT.set(jiveClientSrc);
    }

    public static void main(String[] args) {
        if(args.length != 2) {
            System.err.println("Source Document id and destination place id arguments are required");
            System.exit(-1);
        }

        Properties props = new Properties();
        try {
            props.load(Updater.class.getResourceAsStream("config.properties"));
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Is config.properties in classpath?");
        }
        Updater updater = new Updater(props, args[0], args[1]);
        updater.read();
    }

    private void write(ContentEntity documentEntity) {
        Iterable<PlaceEntity> places = jiveClientDest.places.find(null, 0, 1, null,
                NameValuePair.many().add("entityDescriptor", "700," + placeId));
        for(PlaceEntity placeEntity : places) {
            documentEntity.setType("document");
            ((DocumentEntity)documentEntity).setParent("http://jaf.jiveland.com:8080/api/core/v3/places/" + ((GroupEntity)placeEntity).getPlaceID());
            jiveClientDest.documents.create(documentEntity, null, null, null);
        }
    }

    private void read() {
        Iterable<ContentEntity> documents = jiveClientSrc.contents.find(null, 0, 1, null,
                NameValuePair.many().add("entityDescriptor", "102," + documentId));
        for(ContentEntity documentEntity : documents) {
            write(documentEntity);
        }
    }

    class InstanceCredentials implements Credentials {

        public InstanceCredentials(Properties props, String prefix) {
            headers.add(new NameValuePair("username", props.getProperty(prefix + ".jive.username")));
            headers.add(new NameValuePair("password", props.getProperty(prefix + ".jive.password")));
        }

        private Collection<NameValuePair> headers = new ArrayList<NameValuePair>();

        @Override
        public Collection<NameValuePair> getHeaders() {
            return headers;
        }
    }


}
