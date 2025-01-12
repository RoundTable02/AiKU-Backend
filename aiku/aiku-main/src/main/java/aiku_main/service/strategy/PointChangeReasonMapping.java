package aiku_main.service.strategy;

import aiku_main.application_event.event.PointChangeReason;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface PointChangeReasonMapping {

    PointChangeReason[] value();
}