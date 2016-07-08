/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cnr.ilc.lc.omega.core.datatype;

/**
 *
 * @author simone
 */
public final class TextualHit {

    private final Integer start;
    private final Integer end;

    TextualHit(Integer start, Integer end) {
        this.start = new Integer(start);
        this.end = new Integer(end);

    }

    public Integer getStart() {
        return start;
    }

    public Integer getEnd() {
        return end;
    }

    @Override
    public String toString() {
        return String.format(" hit: [start: %d, end: %d]", start.intValue(), end.intValue());
    }

    
    
}
