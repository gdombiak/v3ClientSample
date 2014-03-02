import com.jivesoftware.v3client.framework.AbstractJiveClient;
import com.jivesoftware.v3client.framework.JiveClient;
import com.jivesoftware.v3client.framework.NameValuePair;
import com.jivesoftware.v3client.framework.credentials.Credentials;
import com.jivesoftware.v3client.framework.entities.InboxEntryEntity;
import com.jivesoftware.v3client.framework.http.HttpTransportImpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class Updater {

    private final JiveClient jiveClient;

    public Updater() {
        jiveClient = new JiveClient("http://jaf.jiveland.com:8080/api/core/v3", new DefaultCredentials(), new HttpTransportImpl());
        AbstractJiveClient.JIVE_CLIENT.set(jiveClient);
    }

    public final static void main(String[] args) {
        Updater updater = new Updater();
        updater.watch();
    }

    private void watch() {
        Iterable<InboxEntryEntity> iterable = jiveClient.inboxEntries.get(null, 2, null, null, false, null, null, null, null);
        for (InboxEntryEntity inboxEntryEntity : iterable) {
            System.out.println(inboxEntryEntity.toString());
        }

    }

    class DefaultCredentials implements Credentials {

        public DefaultCredentials() {
            headers.add(new NameValuePair("username", "admin"));
            headers.add(new NameValuePair("password", "admin"));
        }

        private Collection<NameValuePair> headers = new ArrayList<NameValuePair>();

        @Override
        public Collection<NameValuePair> getHeaders() {
            return headers;
        }
    }


}
