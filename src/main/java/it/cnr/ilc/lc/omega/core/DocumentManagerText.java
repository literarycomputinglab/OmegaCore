package it.cnr.ilc.lc.omega.core;

import it.cnr.ilc.lc.omega.core.spi.DocumentManagerSPI;
import it.cnr.ilc.lc.omega.entity.Source;
import it.cnr.ilc.lc.omega.entity.TextContent;
import java.net.URI;
import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import sirius.kernel.di.std.Register;

/**
 *
 * @author oakgen
 */
@Register(classes = DocumentManagerSPI.class)
public class DocumentManagerText implements DocumentManagerSPI {

    private final MimeType mimeType;

    private DocumentManagerText() throws MimeTypeParseException {
        this.mimeType = new MimeType("text/plain");
    }

    @Override
    public MimeType getMimeType() {
        return mimeType;
    }

    @Override
    public void create(DocumentManager.CreateAction createAction, URI uri) {
        switch (createAction) {
            case SOURCE:
                Source<TextContent> source = Source.sourceOf(TextContent.class);
            default:
                throw new UnsupportedOperationException(createAction.name() + " unsupported");
        }
    }

}
