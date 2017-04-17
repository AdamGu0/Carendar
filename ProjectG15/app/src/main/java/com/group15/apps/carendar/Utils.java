package com.group15.apps.carendar;

import com.alamkanak.weekview.WeekViewEvent;

import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.BusyType;
import net.fortuna.ical4j.model.property.Clazz;
import net.fortuna.ical4j.model.property.Geo;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.Uid;

import java.math.BigDecimal;
import java.net.SocketException;
import java.util.HashMap;

/**
 * Created by Lei on 4/16/2017.
 */

public class Utils {

    public static VEvent weekViewEvent2IcsEvent(MyWeekViewEvent event) throws SocketException {
        if(event == null){
            return null;
        }
        // Create the event
        String eventName = event.getName().trim();
        DateTime start = new DateTime(event.getStartTime().getTime());
        DateTime end = new DateTime(event.getEndTime().getTime());
        VEvent vEvent = new VEvent(start, end, eventName);

        // generate unique identifier..;
        Uid uid = new Uid(event.getEventKey());
        vEvent.getProperties().add(uid);
        // add location
        Location location = new Location(event.getLocation());
        vEvent.getProperties().add(location);
        // add GEO
        Geo geo = new Geo();
        geo.setLongitude(new BigDecimal(event.getLongitude()));
        geo.setLatitude(new BigDecimal(event.getLatitude()));
        vEvent.getProperties().add(geo);
        // add group name for group event, personal event doesn't have this property
        if(event.getIsGroupEvent()){
            Clazz clazz = new Clazz(event.getGroupName());
            vEvent.getProperties().add(clazz);
        }
        // add event type
        BusyType type = new BusyType(String.valueOf(event.getEventType()));
        vEvent.getProperties().add(type);

        // TODO: color

        return vEvent;
    }

    public static MyWeekViewEvent property2WeekViewEvent(PropertyList propertyList){
        if(propertyList == null || propertyList.size() == 0){
            return null;
        }
        MyWeekViewEvent event = new MyWeekViewEvent();
        HashMap<String, String> map = new HashMap<>();
        for(Object obj : propertyList){
            Property property = (Property)obj;
            String name = property.getName();
            if(name != null){
                map.put(name, property.getValue());
            }
        }
        event.setName(map.get(Property.SUMMARY));
        event.setEventKey(map.get(Property.UID));
        event.setLocation(map.get(Property.LOCATION));
        // TODO:
        // add start time
        // add end time

        // add geo
        String geo = map.get(Property.GEO);
        if(geo != null && geo.length() > 0){
            String arr[] = geo.split(",");
            if(arr != null && arr.length == 2){
                event.setLatitude(string2Double(arr[0]));
                event.setLongitude(string2Double(arr[1]));
            }
        }
        // add group name if this is a group event, no group name means it's not a group event
        String groupName = map.get(Property.CLASS);
        if(groupName != null){
            event.setIsGroupEvent(true);
            event.setGroupName(groupName);
        }
        // add event type
        String type = map.get(Property.BUSYTYPE);
        if(type != null){
            int t = 0;
            try{
                t = Integer.valueOf(type);
            }catch (NumberFormatException e){
                e.printStackTrace();
            }
            event.setEventType(t);
        }

        return event;
    }

    private static double string2Double(String value){
        if(value != null && value.length() > 0){
            try {
                double v = Double.valueOf(value);
                return v;
            }catch (NumberFormatException e){
                e.printStackTrace();
            }
        }
        return 0.0;
    }

}
