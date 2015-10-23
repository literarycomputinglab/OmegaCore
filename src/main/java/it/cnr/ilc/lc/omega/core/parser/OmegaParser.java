/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cnr.ilc.lc.omega.core.parser;

import it.cnr.ilc.lc.omega.entity.Content;
import it.cnr.ilc.lc.omega.entity.Source;
import it.cnr.ilc.lc.omega.entity.TextContent;
import java.net.URI;
import java.util.List;

/**
 *
 * @author angelo
 */
public interface OmegaParser {
   
    Source<TextContent> parseTextContent(URI uri);
    Source<? extends Content> parse(URI uri);

    /**
     *
     * @param <T>
     * @param uri
     * @param clazz
     * @return
     */
    <T extends Content> Source<T>  parse(URI uri, Class<T> clazz);
    List<Source<? extends Content>> parse(URI uri, Resource.Structure str, Resource.Granularity gra);
}
