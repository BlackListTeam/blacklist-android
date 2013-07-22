package cat.andreurm.blacklist.model;

import java.util.Date;

/**
 * Created by air on 18/07/13.
 */
public class Party {
    public int party_id;
    public String name;
    public Date date;
    public String cover;
    public String image;
    public String[] gallery;
    public String info;
    public String price_info;
    public String place_text;
    public Date location_date;
    public String map;

    int max_escorts;
    int max_rooms;
    Boolean vip_allowed;
    Boolean es_actual;
}
