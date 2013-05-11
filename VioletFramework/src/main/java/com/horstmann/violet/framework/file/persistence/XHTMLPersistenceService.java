package com.horstmann.violet.framework.file.persistence;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;

import javax.imageio.ImageIO;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;

import org.apache.commons.codec.binary.Base64OutputStream;

import com.horstmann.violet.framework.file.export.FileExportService;
import com.horstmann.violet.framework.injection.bean.ManiocFramework.InjectedBean;
import com.horstmann.violet.framework.injection.bean.ManiocFramework.ManagedBean;
import com.horstmann.violet.framework.util.VersionChecker;
import com.horstmann.violet.product.diagram.abstracts.IGraph;

@ManagedBean(registeredManually=true)
public class XHTMLPersistenceService implements IFilePersistenceService
{

    private static final String TEMPLATE_FILE = "XHTMLFileTemplate.violet.html";

    private static final String IMAGE_TYPE = "jpg";

    private static final String HTML_INLINE_IMAGE_PREFIX = "data:image/jpeg;base64,";

    private static final String TEMPLATE_VERSION_KEY = "${version}";
    
    private static final String TEMPLATE_IMAGE_KEY = "${image}";

    private static final String TEMPLATE_XMLCONTENT_KEY = "${content}";

    private XStreamBasedPersistenceService xstreamService = new XStreamBasedPersistenceService();

    @InjectedBean
    private VersionChecker versionChecker;
    
    @Override
    public void write(IGraph graph, OutputStream out)
    {
        try
        {
            InputStream templateAsStream = this.getClass().getResourceAsStream(TEMPLATE_FILE);
            String template = getInputStreamContent(templateAsStream);
            ByteArrayOutputStream graphOutputStream = new ByteArrayOutputStream();
            xstreamService.write(graph, graphOutputStream);
            String graphString = graphOutputStream.toString();
            ByteArrayOutputStream imageOutputStream = new ByteArrayOutputStream();
            ImageIO.write(FileExportService.getRasterImage(graph), IMAGE_TYPE, new Base64OutputStream(imageOutputStream));
            String imageString = HTML_INLINE_IMAGE_PREFIX + imageOutputStream.toString();
            template = template.replace(TEMPLATE_VERSION_KEY, this.versionChecker.getAppVersionNumber());
            template = template.replace(TEMPLATE_XMLCONTENT_KEY, graphString);
            template = template.replace(TEMPLATE_IMAGE_KEY, imageString);
            out.write(template.getBytes());
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public IGraph read(InputStream in) throws IOException
    {
        XHTMLPersistenceServiceParserGetter kit = new XHTMLPersistenceServiceParserGetter();
        HTMLEditorKit.Parser parser = kit.getParser();
        StringWriter writer = new StringWriter();
        HTMLEditorKit.ParserCallback callback = new XHTMLPersistenceServiceParserCallback(writer);
        InputStreamReader reader = new InputStreamReader(in);
        parser.parse(reader, callback, true);
        String xmlContent = writer.toString();
        ByteArrayInputStream xmlContentStream = new ByteArrayInputStream(xmlContent.getBytes());
        return this.xstreamService.read(xmlContentStream);
    }

    private String getInputStreamContent(InputStream in) throws IOException
    {
        BufferedInputStream bis = new BufferedInputStream(in);
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        int result = bis.read();
        while (result != -1)
        {
            byte b = (byte) result;
            buf.write(b);
            result = bis.read();
        }
        return buf.toString();
    }

    private class XHTMLPersistenceServiceParserGetter extends HTMLEditorKit
    {
        public HTMLEditorKit.Parser getParser()
        {
            return super.getParser();
        }
    }

    private class XHTMLPersistenceServiceParserCallback extends HTMLEditorKit.ParserCallback
    {

        private Writer out;

        private boolean inHeader = false;

        public XHTMLPersistenceServiceParserCallback(Writer out)
        {
            this.out = out;
        }

        public void handleStartTag(HTML.Tag tag, MutableAttributeSet attributes, int position)
        {
            if (!tag.equals(HTML.Tag.SCRIPT))
            {
                return;
            }
            if (!attributes.containsAttribute(HTML.getAttributeKey("id"), "content"))
            {
                return;
            }
            this.inHeader = true;
        }

        public void handleEndTag(HTML.Tag tag, int position)
        {
            if (tag.equals(HTML.Tag.SCRIPT))
            {
                if (this.inHeader)
                {
                    this.inHeader = false;
                }
            }
            // work around bug in the parser that fails to call flush
            if (tag.equals(HTML.Tag.HTML)) this.flush();
        }
        

        @Override
        public void handleComment(char[] text, int position)
        {
            if (this.inHeader)
            {
                try
                {
                    String xmlContent = new String(text);
                    xmlContent = xmlContent.replace("<![CDATA[", "");
                    xmlContent = xmlContent.replace("]]>", "");
                    out.write(xmlContent);
                    out.flush();
                }
                catch (IOException ex)
                {
                    throw new RuntimeException(ex);
                }
            }
        }

        public void flush()
        {
            try
            {
                out.flush();
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }

    }

}
