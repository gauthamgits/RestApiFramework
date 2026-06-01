package Resources;

import Pojo.Location;
import Pojo.AddPlaceSerialiser;
import Pojo.DeletePlaceSerialiser;

import java.util.ArrayList;
import java.util.List;

public class TestDataBuilder {

    public AddPlaceSerialiser addPlacePayload(String name, String language, int accuracy){
        AddPlaceSerialiser obj = new AddPlaceSerialiser();
        obj.setAccuracy(accuracy);
        obj.setAddress("29,wug de, igogl");
        obj.setLanguage(language);
        obj.setName(name);
        obj.setPhone_number("+51-59852973");
        obj.setWebsite("http://ihgw.gwse");
        List<String> typeslist = new ArrayList<>();
        typeslist.add("kieg");
        typeslist.add("guifgjfo");
        typeslist.add("kglijal");
        obj.setTypes(typeslist);
        Location loc = new Location();
        loc.setLat(93.892494);
        loc.setLng(54.22526);
        obj.setLocation(loc);

        return obj;
    }
    
    
    public DeletePlaceSerialiser deleteplacepayload (String placeid){
        DeletePlaceSerialiser delobj = new DeletePlaceSerialiser();
        delobj.setPlace_id(placeid);
        
        return delobj;
    }

}
