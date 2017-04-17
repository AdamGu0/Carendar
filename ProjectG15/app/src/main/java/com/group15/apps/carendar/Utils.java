package com.group15.apps.carendar;

import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.BusyType;
import net.fortuna.ical4j.model.property.Clazz;
import net.fortuna.ical4j.model.property.Comment;
import net.fortuna.ical4j.model.property.Geo;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.Uid;

import java.math.BigDecimal;
import java.net.SocketException;
import java.text.ParseException;
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
        // add event color, use comment to save the color
        Comment comment = new Comment(String.valueOf(event.getColor()));
        vEvent.getProperties().add(comment);

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
        // add start time
        event.setStartTimeMills(getTime(map.get(Property.DTSTART)));
        // add end time
        event.setEndTimeMills(getTime(map.get(Property.DTEND)));
        // add geo
        String geo = map.get(Property.GEO);
        if(geo != null && geo.length() > 0){
            String arr[] = geo.split(";");
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
        event.setEventType(string2Int(type));
        // add color
        String comment = map.get(Property.COMMENT);
        event.setColor(string2Int(comment));

        return event;
    }

    private static long getTime(String value){
        if(value != null && value.length() > 0){
            try {
                DateTime dateTime = new DateTime(value);
                return dateTime.getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    public static double string2Double(String value){
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

    public static int string2Int(String value){
        if(value != null && value.length() > 0){
            try {
                Integer integer = Integer.valueOf(value);
                return integer.intValue();
            }catch (NumberFormatException e){
                e.printStackTrace();
            }
        }
        return 0;
    }

}
