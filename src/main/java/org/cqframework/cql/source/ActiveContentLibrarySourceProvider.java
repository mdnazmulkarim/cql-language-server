package org.cqframework.cql.source;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.logging.Logger;

import org.cqframework.cql.CqlTextDocumentService;
import org.cqframework.cql.cql2elm.LibrarySourceProvider;
import org.hl7.elm.r1.VersionedIdentifier;


// LibrarySourceProvider implementation that pulls from the active content
public class ActiveContentLibrarySourceProvider implements LibrarySourceProvider {
    private static final Logger LOG = Logger.getLogger("main");

    private final CqlTextDocumentService textDocumentService;

    public ActiveContentLibrarySourceProvider(CqlTextDocumentService textDocumentService) {
        this.textDocumentService = textDocumentService;
    }

    @Override
    public InputStream getLibrarySource(VersionedIdentifier versionedIdentifier) {
        String id = versionedIdentifier.getId();
        String version = versionedIdentifier.getVersion();

        String matchText = "(?s).*library\\s+" + id;
        if (version != null) {
            matchText += ("\\s+version\\s+'" + version + "'\\s+(?s).*");
        }
        else {
            matchText += "'\\s+(?s).*";
        }

        for(URI uri : this.textDocumentService.openFiles()){
            Optional<String> content = this.textDocumentService.activeContent(uri);
            // This will match if the content contains the library definition is present.
            if (content.isPresent() && content.get().matches(matchText)){
                return new ByteArrayInputStream(content.get().getBytes(StandardCharsets.UTF_8));
            }
        }

        return null;
    }
}