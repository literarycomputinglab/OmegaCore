/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cnr.ilc.lc.omega.core;

import it.cnr.ilc.lc.omega.entity.Annotation;
import it.cnr.ilc.lc.omega.entity.Content;
import it.cnr.ilc.lc.omega.entity.Locus;
import it.cnr.ilc.lc.omega.entity.Source;
import java.util.Optional;
import java.util.OptionalInt;

/**
 *
 * @author simone
 * @param <T>
 * @param <E>
 */
public class ResourceStatus< T extends Content, E extends Annotation.Type> {

    private Class<?> clazz;
    private OptionalInt start = OptionalInt.empty();
    private OptionalInt end = OptionalInt.empty();
    private Optional<Source<T>> source = Optional.empty();
    private Optional<Annotation<T, E>> annotation = Optional.empty();
    private Optional<String> text = Optional.empty();
    private Optional<Locus.PointsTo> pointsTo = Optional.empty();

    public Optional<String> getText() {
        return text;
    }

    public ResourceStatus text(String text) {
        this.text = Optional.ofNullable(text);
        return this;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public OptionalInt getStart() {
        return start;
    }

    public OptionalInt getEnd() {
        return end;
    }

    public Optional<Source<T>> getSource() {
        return source;
    }

    public Optional<Annotation<T, E>> getAnnotation() {
        return annotation;
    }

    public ResourceStatus() {

    }

    public ResourceStatus clazz(Class<?> clazz) {
        this.clazz = clazz;
        return this;
    }

    public ResourceStatus start(int start) {
        this.start = OptionalInt.of(start);
        return this;
    }

    public ResourceStatus end(int end) {
        this.end = OptionalInt.of(end);
        return this;
    }

    public ResourceStatus source(Source<T> source) {
        this.source = Optional.ofNullable(source);
        return this;
    }

    public ResourceStatus annotation(Annotation<T, E> annotation) {
        this.annotation = Optional.ofNullable(annotation);
        return this;
    }

    public Optional<Locus.PointsTo> getPointsTo() {
        return pointsTo;
    }

    public ResourceStatus pointsTo(Locus.PointsTo pointsTo) {
        this.pointsTo = Optional.ofNullable(pointsTo);
        return this;
    }

}
